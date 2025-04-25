package com.ryuukonpalace.game.combat;

import static org.lwjgl.glfw.GLFW.*;

import java.util.List;
import java.util.Random;

import com.ryuukonpalace.game.capture.CaptureSystem;
import com.ryuukonpalace.game.capture.CaptureSystem.CaptureCallback;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.creatures.Ability;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.CaptureStone;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.ui.CombatInterface;

/**
 * Système de combat pour les affrontements entre le joueur et les créatures.
 * Gère les tours, les actions, les dégâts et la capture des créatures.
 */
public class CombatSystem implements CaptureCallback {
    
    // Singleton instance
    private static CombatSystem instance;
    
    // État du combat
    private CombatState state;
    
    // Joueur
    private Player player;
    
    // Créature du joueur actuellement en combat
    private Creature playerCreature;
    
    // Créature ennemie
    private Creature enemyCreature;
    
    // Tour actuel (true = joueur, false = ennemi)
    private boolean playerTurn;
    
    // Délai entre les actions
    private float actionDelay;
    
    // Message actuel
    private String currentMessage;
    
    // Temps d'affichage du message
    private float messageTime;
    
    // Capacité sélectionnée
    private int selectedAbility;
    
    // Système de capture
    private CaptureSystem captureSystem;
    
    // Gestionnaire d'entrées
    private InputManager inputManager;
    
    // Interface de combat
    private CombatInterface combatInterface;
    
    // Générateur de nombres aléatoires
    private Random random;
    
    /**
     * Énumération des états du combat
     */
    public enum CombatState {
        INACTIVE,       // Pas de combat en cours
        STARTING,       // Combat en cours de démarrage
        PLAYER_TURN,    // Tour du joueur
        ENEMY_TURN,     // Tour de l'ennemi
        MESSAGE,        // Affichage d'un message
        PLAYER_WIN,     // Le joueur a gagné
        ENEMY_WIN,      // L'ennemi a gagné
        FLEE,           // Le joueur a fui
        CAPTURE,        // Capture en cours
        COMPLETED       // Combat terminé
    }
    
    /**
     * Constructeur privé pour le singleton
     */
    private CombatSystem() {
        this.state = CombatState.INACTIVE;
        this.playerTurn = true;
        this.actionDelay = 0.0f;
        this.currentMessage = "";
        this.messageTime = 0.0f;
        this.selectedAbility = 0;
        
        // Obtenir les instances des gestionnaires
        this.captureSystem = CaptureSystem.getInstance();
        this.inputManager = InputManager.getInstance();
        this.combatInterface = CombatInterface.getInstance();
        
        // Initialiser le générateur de nombres aléatoires
        this.random = new Random();
    }
    
    /**
     * Obtenir l'instance unique du système de combat
     * @return L'instance du CombatSystem
     */
    public static CombatSystem getInstance() {
        if (instance == null) {
            instance = new CombatSystem();
        }
        return instance;
    }
    
    /**
     * Initialiser le système de combat
     */
    public void init() {
        // Initialiser les variables
        state = CombatState.INACTIVE;
        playerTurn = true;
        actionDelay = 0.0f;
        
        // Initialiser le système de capture
        captureSystem.init();
        
        // Initialiser l'interface de combat
        combatInterface.init();
    }
    
    /**
     * Démarrer un combat
     * 
     * @param player Joueur
     * @param enemyCreature Créature ennemie
     * @return true si le combat a été démarré avec succès, false sinon
     */
    public boolean startCombat(Player player, Creature enemyCreature) {
        // Vérifier si un combat est déjà en cours
        if (state != CombatState.INACTIVE) {
            return false;
        }
        
        // Vérifier si le joueur a des créatures
        if (player.getCapturedCreatures().isEmpty()) {
            // Le joueur n'a pas de créatures, on ne peut pas démarrer de combat
            return false;
        }
        
        this.player = player;
        this.enemyCreature = enemyCreature;
        
        // Sélectionner la première créature du joueur
        this.playerCreature = player.getCapturedCreatures().get(0);
        this.playerTurn = true;
        this.actionDelay = 1.0f;
        this.currentMessage = "Un " + enemyCreature.getName() + " sauvage apparaît !";
        this.messageTime = 2.0f;
        this.selectedAbility = 0;
        
        // Réinitialiser les cooldowns
        this.playerCreature.getCombatStats().resetCooldowns();
        
        // Changer l'état du combat
        this.state = CombatState.MESSAGE;
        
        // Initialiser et afficher l'interface de combat
        combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
        combatInterface.show();
        
        return true;
    }
    
    /**
     * Mettre à jour le système de combat
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        // Ne rien faire si le combat est inactif
        if (state == CombatState.INACTIVE) {
            return;
        }

        // Mettre à jour l'interface de combat
        combatInterface.update(deltaTime);

        // Mettre à jour le système de capture s'il est actif
        if (captureSystem.isActive()) {
            captureSystem.update(deltaTime);
            return;
        }

        // Mettre à jour le délai d'action
        if (actionDelay > 0) {
            actionDelay -= deltaTime;
            if (actionDelay <= 0) {
                // Passer à l'étape suivante
                switch (state) {
                    case MESSAGE:
                        if (playerTurn) {
                            state = CombatState.PLAYER_TURN;
                        } else {
                            state = CombatState.ENEMY_TURN;
                            executeEnemyAction();
                        }
                        break;
                    case PLAYER_WIN:
                        state = CombatState.COMPLETED;
                        break;
                    case ENEMY_WIN:
                        state = CombatState.COMPLETED;
                        break;
                    case FLEE:
                    case CAPTURE:
                        state = CombatState.COMPLETED;
                        break;
                    default:
                        break;
                }
            }
        }
        
        // Mettre à jour le temps d'affichage du message
        if (messageTime > 0) {
            messageTime -= deltaTime;
            if (messageTime <= 0) {
                currentMessage = "";
            }
        }
        
        // Gérer les entrées utilisateur
        handleInput();
    }
    
    /**
     * Dessiner le système de combat
     */
    public void render() {
        // Ne rien faire si le combat est inactif
        if (state == CombatState.INACTIVE) {
            return;
        }

        // Afficher le système de capture s'il est actif
        if (captureSystem.isActive()) {
            captureSystem.render();
            return;
        }

        // Dessiner l'interface de combat
        combatInterface.render();
    }
    
    /**
     * Gérer les entrées du joueur
     */
    public void handleInput() {
        // Ne rien faire si le combat est inactif ou si ce n'est pas le tour du joueur
        if (state == CombatState.INACTIVE || !playerTurn) {
            return;
        }
        
        // Obtenir les coordonnées de la souris
        float mouseX = (float) inputManager.getMouseX();
        float mouseY = (float) inputManager.getMouseY();
        boolean mousePressed = inputManager.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT);
        
        // Déléguer la gestion des entrées à l'interface de combat
        combatInterface.handleInput(mouseX, mouseY, mousePressed);
    }
    
    /**
     * Utiliser une capacité
     * 
     * @param abilityIndex Index de la capacité à utiliser
     */
    public void useAbility(int abilityIndex) {
        if (state != CombatState.PLAYER_TURN) {
            return;
        }
        
        // Vérifier si l'index est valide
        List<Ability> abilities = playerCreature.getAbilities();
        if (abilityIndex < 0 || abilityIndex >= abilities.size()) {
            return;
        }
        
        // Sélectionner la capacité
        selectedAbility = abilityIndex;
        
        // Exécuter l'action du joueur
        executePlayerAction();
        
        // Mettre à jour l'interface
        combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
    }
    
    /**
     * Exécuter l'action du joueur
     */
    private void executePlayerAction() {
        // Capacité sélectionnée
        Ability ability = playerCreature.getAbilities().get(selectedAbility);
        
        // Calculer les dégâts
        int damage = calculateDamage(playerCreature, enemyCreature, ability);
        
        // Appliquer les dégâts
        enemyCreature.takeDamage(damage);
        
        // Appliquer le cooldown de la capacité
        playerCreature.getCombatStats().useSkill(selectedAbility);
        
        // Afficher un message
        currentMessage = playerCreature.getName() + " utilise " + ability.getName() + " !\n" +
                         "Cela inflige " + damage + " points de dégâts à " + enemyCreature.getName() + " !";
        messageTime = 2.0f;
        
        // Vérifier si l'ennemi est vaincu
        if (enemyCreature.getHealth() <= 0) {
            // Victoire
            currentMessage += "\n" + enemyCreature.getName() + " est vaincu !";
            state = CombatState.PLAYER_WIN;
            actionDelay = 2.0f;
        } else {
            // Passer au tour de l'ennemi
            playerTurn = false;
            state = CombatState.ENEMY_TURN;
            actionDelay = 1.0f;
        }
        
        // Mettre à jour l'interface
        combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
    }
    
    /**
     * Exécuter l'action de l'ennemi
     */
    private void executeEnemyAction() {
        // Vérifier si l'ennemi peut agir
        if (enemyCreature.getHealth() <= 0) {
            // L'ennemi est KO
            currentMessage = enemyCreature.getName() + " est KO !";
            messageTime = 2.0f;
            state = CombatState.PLAYER_WIN;
            actionDelay = 2.0f;
            return;
        }
        
        // Choisir une capacité aléatoire
        List<Ability> abilities = enemyCreature.getAbilities();
        int abilityIndex = random.nextInt(abilities.size());
        Ability ability = abilities.get(abilityIndex);
        
        // Calculer les dégâts
        int damage = calculateDamage(enemyCreature, playerCreature, ability);
        
        // Appliquer les dégâts
        playerCreature.takeDamage(damage);
        
        // Afficher un message
        currentMessage = enemyCreature.getName() + " utilise " + ability.getName() + " !\n" +
                         "Cela inflige " + damage + " points de dégâts à " + playerCreature.getName() + " !";
        messageTime = 2.0f;
        
        // Vérifier si le joueur est vaincu
        if (playerCreature.getHealth() <= 0) {
            // Défaite
            currentMessage += "\n" + playerCreature.getName() + " est vaincu !";
            state = CombatState.ENEMY_WIN;
            actionDelay = 2.0f;
        } else {
            // Passer au tour du joueur
            playerTurn = true;
            state = CombatState.PLAYER_TURN;
            
            // Mettre à jour les capacités (diminuer les cooldowns)
            playerCreature.getCombatStats().decreaseCooldowns();
            
            // Afficher le menu principal
            combatInterface.showMainMenu();
        }
        
        // Mettre à jour l'interface
        combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
    }
    
    /**
     * Calculer les dégâts d'une attaque
     * 
     * @param attacker Créature attaquante
     * @param defender Créature défenseure
     * @param ability Capacité utilisée
     * @return Dégâts infligés
     */
    private int calculateDamage(Creature attacker, Creature defender, Ability ability) {
        // Formule de base : (Niveau * 0.4 + 2) * Puissance * Attaque / Défense / 50 + 2
        // Avec un facteur aléatoire entre 0.85 et 1.15
        int attackerLevel = attacker.getLevel();
        int attackerAttack = attacker.getAttack();
        int defenderDefense = defender.getDefense();
        int abilityPower = ability.getPower();
        
        float levelFactor = (float) attackerLevel * 0.4f + 2f;
        float attackFactor = (float) attackerAttack / defenderDefense;
        float randomFactor = 0.85f + random.nextFloat() * 0.3f; // Entre 0.85 et 1.15
        
        int damage = (int) (levelFactor * abilityPower * attackFactor / 50f + 2f);
        damage = (int) (damage * randomFactor);
        
        // Limiter les dégâts à au moins 1
        return Math.max(1, damage);
    }
    
    /**
     * Terminer le combat
     */
    public void endCombat() {
        // Réinitialiser les cooldowns
        if (playerCreature != null && playerCreature.getCombatStats() != null) {
            playerCreature.getCombatStats().resetCooldowns();
        }
        
        // Réinitialiser les états
        state = CombatState.INACTIVE;
        playerCreature = null;
        enemyCreature = null;
        player = null;
        
        // Cacher l'interface de combat
        combatInterface.hide();
    }
    
    /**
     * Obtenir l'état actuel du combat
     * 
     * @return État du combat
     */
    public CombatState getState() {
        return state;
    }
    
    /**
     * Tenter de fuir le combat
     */
    public void tryToFlee() {
        if (state != CombatState.PLAYER_TURN) {
            return;
        }
        
        // Calculer la probabilité de fuite
        int playerSpeed = playerCreature.getSpeed();
        int enemySpeed = enemyCreature.getSpeed();
        float fleeChance = 0.5f + 0.3f * (playerSpeed - enemySpeed) / Math.max(1, enemySpeed);
        fleeChance = Math.max(0.1f, Math.min(0.9f, fleeChance));
        
        if (random.nextFloat() < fleeChance) {
            // Fuite réussie
            currentMessage = "Vous avez fui le combat !";
            messageTime = 1.0f;
            state = CombatState.FLEE;
            actionDelay = 1.0f;
        } else {
            // Échec de la fuite
            currentMessage = "Impossible de fuir !";
            messageTime = 1.0f;
            state = CombatState.MESSAGE;
            
            // Passer au tour de l'ennemi
            playerTurn = false;
        }
        
        // Mettre à jour l'interface
        combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
    }

    /**
     * Callback appelé lorsque la capture est terminée
     */
    @Override
    public void onCaptureResult(Player player, Creature creature, boolean success) {
        if (success) {
            // Capture réussie
            currentMessage = creature.getName() + " a été capturé !";
            messageTime = 2.0f;
            state = CombatState.CAPTURE;
            actionDelay = 2.0f;
        } else {
            // Capture échouée
            currentMessage = creature.getName() + " s'est libéré !";
            messageTime = 2.0f;
            state = CombatState.MESSAGE;
            
            // Passer au tour de l'ennemi
            playerTurn = false;
        }
        
        // Mettre à jour l'interface
        combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
    }

    /**
     * Démarrer une tentative de capture
     */
    public void startCapture() {
        if (captureSystem.startCapture(player, enemyCreature, this)) {
            currentMessage = "Tentative de capture...";
            messageTime = 2.0f;
            
            // Mettre à jour l'interface
            combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
        }
    }
    
    /**
     * Démarrer une tentative de capture avec la pierre sélectionnée
     * 
     * @param stone Pierre de capture à utiliser
     */
    public void startCaptureWithSelectedStone(CaptureStone stone) {
        // Démarrer la capture avec la pierre sélectionnée
        if (captureSystem.startCapture(player, enemyCreature, this, stone)) {
            currentMessage = "Tentative de capture avec " + stone.getMaterial().getName() + " " + stone.getType().getName() + "...";
            messageTime = 2.0f;
            
            // Consommer la pierre de capture (la retirer de l'inventaire)
            player.getInventory().removeItem(stone);
            
            // Revenir à l'état normal du combat
            state = CombatState.MESSAGE;
            
            // Mettre à jour l'interface
            combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
        } else {
            currentMessage = "Impossible de démarrer la capture !";
            messageTime = 2.0f;
            state = CombatState.MESSAGE;
            
            // Mettre à jour l'interface
            combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, currentMessage);
        }
    }
    
    /**
     * Vérifier si un combat est actuellement actif
     * 
     * @return true si un combat est en cours, false sinon
     */
    public boolean isActive() {
        return state != CombatState.INACTIVE && state != CombatState.COMPLETED;
    }
    
    /**
     * Vérifier si le combat est terminé
     * 
     * @return true si le combat est terminé, false sinon
     */
    public boolean isCompleted() {
        return state == CombatState.COMPLETED || state == CombatState.PLAYER_WIN || 
               state == CombatState.ENEMY_WIN || state == CombatState.FLEE || 
               state == CombatState.CAPTURE;
    }
    
    /**
     * Obtenir la créature du joueur actuellement en combat
     * 
     * @return Créature du joueur
     */
    public Creature getPlayerCreature() {
        return playerCreature;
    }
    
    /**
     * Obtenir la créature ennemie
     * 
     * @return Créature ennemie
     */
    public Creature getEnemyCreature() {
        return enemyCreature;
    }
    
    /**
     * Changer la créature du joueur actuellement en combat
     * 
     * @param newCreature Nouvelle créature du joueur
     */
    public void setPlayerCreature(Creature newCreature) {
        if (newCreature != null && state != CombatState.INACTIVE) {
            this.playerCreature = newCreature;
            
            // Mettre à jour l'interface
            combatInterface.updateCombatInfo(player, playerCreature, enemyCreature, "Changement de variant !");
            
            // Passer au tour de l'ennemi après le changement
            state = CombatState.ENEMY_TURN;
        }
    }
    
    /**
     * Calculer les dégâts d'une attaque de base
     * 
     * @param attacker Créature attaquante
     * @param defender Créature défenseure
     * @return Dégâts infligés
     */
    public int calculateBasicAttackDamage(Creature attacker, Creature defender) {
        // Formule de base pour les dégâts
        int baseDamage = attacker.getAttack() - defender.getDefense() / 2;
        baseDamage = Math.max(1, baseDamage); // Au moins 1 point de dégât
        
        // Ajouter un peu d'aléatoire (±20%)
        float randomFactor = 0.8f + random.nextFloat() * 0.4f;
        int finalDamage = Math.round(baseDamage * randomFactor);
        
        return Math.max(1, finalDamage); // Au moins 1 point de dégât
    }
    
    /**
     * Calculer les dégâts d'une capacité
     * 
     * @param attacker Créature attaquante
     * @param defender Créature défenseure
     * @param ability Capacité utilisée
     * @return Dégâts infligés
     */
    public int calculateAbilityDamage(Creature attacker, Creature defender, Ability ability) {
        // Utiliser la méthode existante pour le calcul des dégâts
        return calculateDamage(attacker, defender, ability);
    }
    
    /**
     * Calculer la probabilité de capture d'une créature
     * 
     * @param creature Créature à capturer
     * @return Probabilité de capture (entre 0 et 1)
     */
    public float calculateCaptureChance(Creature creature) {
        // Formule de base pour la probabilité de capture
        // Plus la créature est faible (en % de PV), plus la probabilité est élevée
        float healthPercentage = (float) creature.getCurrentHealth() / creature.getMaxHealth();
        float baseChance = 0.5f * (1.0f - healthPercentage);
        
        // Ajuster en fonction du niveau de la créature
        float levelFactor = 1.0f - Math.min(0.7f, creature.getLevel() * 0.05f);
        
        // Probabilité finale
        float finalChance = baseChance * levelFactor;
        
        // Limiter entre 0.1 et 0.9
        return Math.max(0.1f, Math.min(0.9f, finalChance));
    }
}

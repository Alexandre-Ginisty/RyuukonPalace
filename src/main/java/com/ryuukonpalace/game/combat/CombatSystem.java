package com.ryuukonpalace.game.combat;

import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.creatures.Ability;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.capture.CaptureSystem;
import com.ryuukonpalace.game.capture.CaptureSystem.CaptureCallback;
import com.ryuukonpalace.game.utils.ResourceManager;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

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
    
    // Option sélectionnée dans le menu
    private int selectedOption;
    
    // Capacité sélectionnée
    private int selectedAbility;
    
    // Tentative de capture en cours
    private boolean capturingAttempt;
    
    // Système de capture
    private CaptureSystem captureSystem;
    
    // Renderer
    private Renderer renderer;
    
    // Gestionnaire d'entrées
    private InputManager inputManager;
    
    // ID de la texture du fond de combat
    private int backgroundTextureId;
    
    // ID de la texture du cadre de menu
    private int menuFrameTextureId;
    
    // Générateur de nombres aléatoires
    private Random random;
    
    /**
     * Constructeur privé pour le singleton
     */
    private CombatSystem() {
        this.state = CombatState.INACTIVE;
        this.playerTurn = true;
        this.actionDelay = 0.0f;
        this.currentMessage = "";
        this.messageTime = 0.0f;
        this.selectedOption = 0;
        this.selectedAbility = 0;
        this.capturingAttempt = false;
        
        // Obtenir les instances des gestionnaires
        this.captureSystem = CaptureSystem.getInstance();
        this.renderer = Renderer.getInstance();
        this.inputManager = InputManager.getInstance();
        
        // Initialiser le générateur de nombres aléatoires
        this.random = new Random();
        
        // Charger les textures
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.backgroundTextureId = resourceManager.loadTexture("src/main/resources/images/combat/background.png", "combat_background");
        this.menuFrameTextureId = resourceManager.loadTexture("src/main/resources/images/combat/menu_frame.png", "menu_frame");
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
        // Charger les textures nécessaires pour le combat
        ResourceManager resourceManager = ResourceManager.getInstance();
        
        // Récupérer les IDs des textures
        backgroundTextureId = resourceManager.getTextureId("combat_background");
        menuFrameTextureId = resourceManager.getTextureId("menu_frame");
        
        // Initialiser les variables
        state = CombatState.INACTIVE;
        playerTurn = true;
        actionDelay = 0.0f;
        currentMessage = "";
        messageTime = 0.0f;
        selectedOption = 0;
        selectedAbility = 0;
        capturingAttempt = false;
        
        // Initialiser le système de capture
        captureSystem.init();
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
        
        // Initialiser les variables
        this.playerTurn = true;
        this.actionDelay = 1.0f;
        this.currentMessage = "Un " + enemyCreature.getName() + " sauvage apparaît !";
        this.messageTime = 2.0f;
        this.selectedOption = 0;
        this.selectedAbility = 0;
        this.capturingAttempt = false;
        this.state = CombatState.MESSAGE;
        
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
        
        // Mettre à jour le système de capture s'il est actif
        if (captureSystem.isActive()) {
            captureSystem.update(deltaTime);
            return;
        }
        
        // Gérer les délais entre les actions
        if (actionDelay > 0) {
            actionDelay -= deltaTime;
            if (actionDelay <= 0) {
                // Passer à l'état suivant après le délai
                switch (state) {
                    case MESSAGE:
                        if (playerTurn) {
                            state = CombatState.PLAYER_TURN;
                        } else {
                            executeEnemyTurn();
                        }
                        break;
                    case PLAYER_ACTION:
                        executePlayerAction();
                        break;
                    case ENEMY_ACTION:
                        checkBattleEnd();
                        break;
                    default:
                        break;
                }
            }
            return;
        }
        
        // Gérer le temps d'affichage du message
        if (messageTime > 0) {
            messageTime -= deltaTime;
        }
        
        // Gérer les entrées du joueur si c'est son tour
        if (state == CombatState.PLAYER_TURN || state == CombatState.ABILITY_SELECTION) {
            handleInput();
        }
    }
    
    /**
     * Dessiner le système de combat
     */
    public void render() {
        // Ne rien faire si le combat est inactif
        if (state == CombatState.INACTIVE) {
            return;
        }
        
        // Dessiner le système de capture s'il est actif
        if (captureSystem.isActive()) {
            captureSystem.render();
            return;
        }
        
        // Obtenir les dimensions de l'écran
        float screenWidth = renderer.getScreenWidth();
        float screenHeight = renderer.getScreenHeight();
        
        // Dessiner le fond de combat
        renderer.drawUIElement(backgroundTextureId, 0, 0, screenWidth, screenHeight);
        
        // Dessiner la créature ennemie
        if (enemyCreature != null) {
            float enemyX = screenWidth * 0.7f;
            float enemyY = screenHeight * 0.3f;
            float enemySize = 128.0f;
            renderer.drawUIElement(enemyCreature.getTextureId(), enemyX - enemySize / 2, enemyY - enemySize / 2, enemySize, enemySize);
            
            // Dessiner la barre de vie de l'ennemi
            drawHealthBar(enemyX - enemySize / 2, enemyY - enemySize / 2 - 20, enemySize, 10, 
                         enemyCreature.getHealth(), enemyCreature.getMaxHealth());
            
            // Dessiner le nom et le niveau de l'ennemi
            renderer.drawText(enemyCreature.getName() + " Nv." + enemyCreature.getLevel(), 
                             enemyX - enemySize / 2, enemyY - enemySize / 2 - 40, 16, 0xFFFFFFFF);
        }
        
        // Dessiner la créature du joueur
        if (playerCreature != null) {
            float playerCreatureX = screenWidth * 0.3f;
            float playerCreatureY = screenHeight * 0.7f;
            float playerCreatureSize = 128.0f;
            renderer.drawUIElement(playerCreature.getTextureId(), playerCreatureX - playerCreatureSize / 2, 
                                  playerCreatureY - playerCreatureSize / 2, playerCreatureSize, playerCreatureSize);
            
            // Dessiner la barre de vie du joueur
            drawHealthBar(playerCreatureX - playerCreatureSize / 2, playerCreatureY - playerCreatureSize / 2 - 20, 
                         playerCreatureSize, 10, playerCreature.getHealth(), playerCreature.getMaxHealth());
            
            // Dessiner le nom et le niveau de la créature du joueur
            renderer.drawText(playerCreature.getName() + " Nv." + playerCreature.getLevel(), 
                             playerCreatureX - playerCreatureSize / 2, playerCreatureY - playerCreatureSize / 2 - 40, 16, 0xFFFFFFFF);
        }
        
        // Dessiner le message actuel
        if (messageTime > 0) {
            float messageX = screenWidth / 2;
            float messageY = screenHeight / 2;
            renderer.drawText(currentMessage, messageX, messageY, 24, 0xFFFFFFFF);
        }
        
        // Dessiner le menu selon l'état
        switch (state) {
            case PLAYER_TURN:
                drawMainMenu();
                break;
            case ABILITY_SELECTION:
                drawAbilityMenu();
                break;
            default:
                break;
        }
    }
    
    /**
     * Dessiner une barre de vie
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param currentHealth Santé actuelle
     * @param maxHealth Santé maximale
     */
    private void drawHealthBar(float x, float y, float width, float height, int currentHealth, int maxHealth) {
        // Fond de la barre (rouge)
        renderer.drawRect(x, y, width, height, 0xFFFF0000);
        
        // Partie remplie (vert)
        float fillWidth = width * ((float) currentHealth / maxHealth);
        renderer.drawRect(x, y, fillWidth, height, 0xFF00FF00);
        
        // Contour de la barre (noir)
        renderer.drawRectOutline(x, y, width, height, 2.0f, 0xFF000000);
    }
    
    /**
     * Dessiner le menu principal
     */
    private void drawMainMenu() {
        float screenWidth = renderer.getScreenWidth();
        float screenHeight = renderer.getScreenHeight();
        
        // Dessiner le cadre du menu
        float menuWidth = 300.0f;
        float menuHeight = 200.0f;
        float menuX = screenWidth - menuWidth - 20.0f;
        float menuY = screenHeight - menuHeight - 20.0f;
        renderer.drawUIElement(menuFrameTextureId, menuX, menuY, menuWidth, menuHeight);
        
        // Options du menu
        String[] options = {"Attaquer", "Capturer", "Changer", "Fuir"};
        
        // Dessiner les options
        float optionHeight = 40.0f;
        float optionSpacing = 10.0f;
        float optionY = menuY + 20.0f;
        
        for (int i = 0; i < options.length; i++) {
            // Couleur de l'option (blanc, ou jaune si sélectionnée)
            int color = (i == selectedOption) ? 0xFFFFFF00 : 0xFFFFFFFF;
            
            // Dessiner l'option
            renderer.drawText(options[i], menuX + 30.0f, optionY + i * (optionHeight + optionSpacing), 20, color);
        }
    }
    
    /**
     * Dessiner le menu de sélection de capacité
     */
    private void drawAbilityMenu() {
        float screenWidth = renderer.getScreenWidth();
        float screenHeight = renderer.getScreenHeight();
        
        // Dessiner le cadre du menu
        float menuWidth = 400.0f;
        float menuHeight = 200.0f;
        float menuX = screenWidth - menuWidth - 20.0f;
        float menuY = screenHeight - menuHeight - 20.0f;
        renderer.drawUIElement(menuFrameTextureId, menuX, menuY, menuWidth, menuHeight);
        
        // Dessiner le titre
        renderer.drawText("Capacités", menuX + 20.0f, menuY + 20.0f, 24, 0xFFFFFFFF);
        
        // Dessiner les capacités
        float abilityHeight = 40.0f;
        float abilitySpacing = 10.0f;
        float abilityY = menuY + 60.0f;
        
        for (int i = 0; i < playerCreature.getAbilities().size(); i++) {
            Ability ability = playerCreature.getAbilities().get(i);
            
            // Couleur de la capacité (blanc, ou jaune si sélectionnée)
            int color = (i == selectedAbility) ? 0xFFFFFF00 : 0xFFFFFFFF;
            
            // Dessiner la capacité
            renderer.drawText(ability.getName() + " (" + ability.getType() + ")", 
                             menuX + 30.0f, abilityY + i * (abilityHeight + abilitySpacing), 18, color);
        }
        
        // Dessiner l'option "Retour"
        int color = (selectedAbility == playerCreature.getAbilities().size()) ? 0xFFFFFF00 : 0xFFFFFFFF;
        renderer.drawText("Retour", menuX + 30.0f, 
                         abilityY + playerCreature.getAbilities().size() * (abilityHeight + abilitySpacing), 18, color);
    }
    
    /**
     * Gérer les entrées du joueur
     */
    private void handleInput() {
        switch (state) {
            case PLAYER_TURN:
                handleMainMenuInput();
                break;
            case ABILITY_SELECTION:
                handleAbilityMenuInput();
                break;
            default:
                break;
        }
    }
    
    /**
     * Gérer les entrées dans le menu principal
     */
    private void handleMainMenuInput() {
        // Navigation dans le menu
        if (inputManager.isKeyJustPressed(GLFW_KEY_UP) || inputManager.isKeyJustPressed(GLFW_KEY_W)) {
            selectedOption = (selectedOption - 1 + 4) % 4;
        } else if (inputManager.isKeyJustPressed(GLFW_KEY_DOWN) || inputManager.isKeyJustPressed(GLFW_KEY_S)) {
            selectedOption = (selectedOption + 1) % 4;
        }
        
        // Sélection d'une option
        if (inputManager.isKeyJustPressed(GLFW_KEY_ENTER) || inputManager.isKeyJustPressed(GLFW_KEY_SPACE)) {
            switch (selectedOption) {
                case 0: // Attaquer
                    state = CombatState.ABILITY_SELECTION;
                    selectedAbility = 0;
                    break;
                case 1: // Capturer
                    startCapture();
                    break;
                case 2: // Changer
                    // TODO: Implémenter le changement de créature
                    break;
                case 3: // Fuir
                    tryToFlee();
                    break;
            }
        }
    }
    
    /**
     * Gérer les entrées dans le menu de sélection de capacité
     */
    private void handleAbilityMenuInput() {
        int maxOptions = playerCreature.getAbilities().size() + 1; // +1 pour l'option "Retour"
        
        // Navigation dans le menu
        if (inputManager.isKeyJustPressed(GLFW_KEY_UP) || inputManager.isKeyJustPressed(GLFW_KEY_W)) {
            selectedAbility = (selectedAbility - 1 + maxOptions) % maxOptions;
        } else if (inputManager.isKeyJustPressed(GLFW_KEY_DOWN) || inputManager.isKeyJustPressed(GLFW_KEY_S)) {
            selectedAbility = (selectedAbility + 1) % maxOptions;
        }
        
        // Sélection d'une option
        if (inputManager.isKeyJustPressed(GLFW_KEY_ENTER) || inputManager.isKeyJustPressed(GLFW_KEY_SPACE)) {
            if (selectedAbility < playerCreature.getAbilities().size()) {
                // Utiliser la capacité sélectionnée
                useAbility(selectedAbility);
            } else {
                // Retour au menu principal
                state = CombatState.PLAYER_TURN;
            }
        }
        
        // Retour au menu principal avec Echap
        if (inputManager.isKeyJustPressed(GLFW_KEY_ESCAPE)) {
            state = CombatState.PLAYER_TURN;
        }
    }
    
    /**
     * Utiliser une capacité
     * 
     * @param abilityIndex Index de la capacité à utiliser
     */
    private void useAbility(int abilityIndex) {
        Ability ability = playerCreature.getAbilities().get(abilityIndex);
        
        // Message d'utilisation de la capacité
        currentMessage = playerCreature.getName() + " utilise " + ability.getName() + " !";
        messageTime = 2.0f;
        
        // Calculer les dégâts
        int damage = calculateDamage(playerCreature, enemyCreature, ability);
        
        // Appliquer les dégâts
        enemyCreature.setHealth(enemyCreature.getHealth() - damage);
        
        // Passer à l'état d'action du joueur
        state = CombatState.PLAYER_ACTION;
        actionDelay = 2.0f;
    }
    
    /**
     * Calculer les dégâts d'une attaque
     * 
     * @param attacker Créature attaquante
     * @param defender Créature défenseur
     * @param ability Capacité utilisée
     * @return Dégâts infligés
     */
    private int calculateDamage(Creature attacker, Creature defender, Ability ability) {
        // Formule de base: (niveau * 2 / 5 + 2) * puissance * attaque / défense / 50 + 2
        float baseDamage = (attacker.getLevel() * 2.0f / 5.0f + 2.0f) * 
                          ability.getPower() * attacker.getAttack() / defender.getDefense() / 50.0f + 2.0f;
        
        // Modificateur de type (à implémenter plus tard)
        float typeModifier = 1.0f;
        
        // Coup critique (10% de chance)
        float critModifier = (random.nextFloat() < 0.1f) ? 1.5f : 1.0f;
        
        // Modificateur aléatoire (0.85 - 1.0)
        float randomModifier = 0.85f + random.nextFloat() * 0.15f;
        
        // Calculer les dégâts finaux
        int finalDamage = Math.max(1, (int)(baseDamage * typeModifier * critModifier * randomModifier));
        
        return finalDamage;
    }
    
    /**
     * Exécuter l'action du joueur
     */
    private void executePlayerAction() {
        // Vérifier si le combat est terminé
        checkBattleEnd();
        
        // Si le combat n'est pas terminé, passer au tour de l'ennemi
        if (state != CombatState.VICTORY && state != CombatState.DEFEAT && state != CombatState.CAPTURE && state != CombatState.FLEE) {
            playerTurn = false;
            state = CombatState.MESSAGE;
            currentMessage = "Tour de " + enemyCreature.getName() + "...";
            messageTime = 1.0f;
            actionDelay = 1.0f;
        }
    }
    
    /**
     * Exécuter le tour de l'ennemi
     */
    private void executeEnemyTurn() {
        // Sélectionner une capacité aléatoire
        int abilityIndex = random.nextInt(enemyCreature.getAbilities().size());
        Ability ability = enemyCreature.getAbilities().get(abilityIndex);
        
        // Message d'utilisation de la capacité
        currentMessage = enemyCreature.getName() + " utilise " + ability.getName() + " !";
        messageTime = 2.0f;
        
        // Calculer les dégâts
        int damage = calculateDamage(enemyCreature, playerCreature, ability);
        
        // Appliquer les dégâts
        playerCreature.setHealth(playerCreature.getHealth() - damage);
        
        // Passer à l'état d'action de l'ennemi
        state = CombatState.ENEMY_ACTION;
        actionDelay = 2.0f;
    }
    
    /**
     * Vérifier si le combat est terminé
     */
    private void checkBattleEnd() {
        // Vérifier si le joueur est vaincu
        if (playerCreature.getHealth() <= 0) {
            // Le joueur est vaincu
            playerCreature.setHealth(0);
            currentMessage = playerCreature.getName() + " est vaincu !";
            messageTime = 2.0f;
            state = CombatState.DEFEAT;
            actionDelay = 2.0f;
        } else if (enemyCreature.getHealth() <= 0) {
            // L'ennemi est vaincu
            enemyCreature.setHealth(0);
            currentMessage = enemyCreature.getName() + " est vaincu !";
            messageTime = 2.0f;
            state = CombatState.VICTORY;
            actionDelay = 2.0f;
        }
    }
    
    /**
     * Démarrer une tentative de capture
     */
    private void startCapture() {
        // Vérifier si la capture est possible
        if (capturingAttempt) {
            currentMessage = "Vous avez déjà tenté de capturer cette créature !";
            messageTime = 2.0f;
            return;
        }
        
        // Démarrer la capture
        if (captureSystem.startCapture(player, enemyCreature, this)) {
            capturingAttempt = true;
            currentMessage = "Tentative de capture...";
            messageTime = 2.0f;
        }
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
            
            // Ajouter la créature à la collection du joueur
            player.getCapturedCreatures().add(creature);
        } else {
            // Capture échouée
            currentMessage = "La capture a échoué !";
            messageTime = 2.0f;
            
            // Passer au tour de l'ennemi
            playerTurn = false;
            state = CombatState.MESSAGE;
            actionDelay = 2.0f;
        }
    }
    
    /**
     * Tenter de fuir le combat
     */
    private void tryToFlee() {
        // 50% de chance de fuir
        if (random.nextFloat() < 0.5f) {
            // Fuite réussie
            currentMessage = "Vous avez fui le combat !";
            messageTime = 2.0f;
            state = CombatState.FLEE;
            actionDelay = 2.0f;
        } else {
            // Fuite échouée
            currentMessage = "Impossible de fuir !";
            messageTime = 2.0f;
            
            // Passer au tour de l'ennemi
            playerTurn = false;
            state = CombatState.MESSAGE;
            actionDelay = 2.0f;
        }
    }
    
    /**
     * Terminer le combat
     */
    public void endCombat() {
        state = CombatState.INACTIVE;
        player = null;
        playerCreature = null;
        enemyCreature = null;
        playerTurn = true;
        actionDelay = 0.0f;
        currentMessage = "";
        messageTime = 0.0f;
        selectedOption = 0;
        selectedAbility = 0;
        capturingAttempt = false;
    }
    
    /**
     * Vérifier si le combat est actif
     * 
     * @return true si le combat est actif, false sinon
     */
    public boolean isActive() {
        return state != CombatState.INACTIVE;
    }
    
    /**
     * Vérifier si le combat est terminé
     * 
     * @return true si le combat est terminé, false sinon
     */
    public boolean isCompleted() {
        return state == CombatState.VICTORY || state == CombatState.DEFEAT || 
               state == CombatState.CAPTURE || state == CombatState.FLEE;
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
     * États possibles du combat
     */
    public enum CombatState {
        INACTIVE,           // Combat inactif
        MESSAGE,            // Affichage d'un message
        PLAYER_TURN,        // Tour du joueur (menu principal)
        ABILITY_SELECTION,  // Sélection d'une capacité
        PLAYER_ACTION,      // Action du joueur en cours
        ENEMY_ACTION,       // Action de l'ennemi en cours
        VICTORY,            // Combat gagné
        DEFEAT,             // Combat perdu
        CAPTURE,            // Créature capturée
        FLEE                // Fuite réussie
    }
}

package com.ryuukonpalace.game.combat;

import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.creatures.Ability;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.LevelSystem;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.capture.CaptureSystem;
import com.ryuukonpalace.game.capture.CaptureSystem.CaptureCallback;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.utils.ResourceManager;

import java.util.Random;
import java.util.List;

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

        // Dessiner l'indicateur de tour
        drawTurnIndicator();

        // Calculer les positions des créatures
        float playerCreatureX = screenWidth * 0.25f;
        float playerCreatureY = screenHeight * 0.6f;
        float enemyCreatureX = screenWidth * 0.75f;
        float enemyCreatureY = screenHeight * 0.3f;

        // Effet de pulsation pour la créature active
        float time = (float) (System.currentTimeMillis() % 1000) / 1000.0f;
        float scale = 1.0f + 0.05f * (float) Math.sin(time * Math.PI * 2);

        // Dessiner la créature du joueur avec effet de pulsation si c'est son tour
        float playerScale = playerTurn ? scale : 1.0f;
        renderer.drawSprite(
            playerCreature.getTextureId(),
            playerCreatureX - (playerCreature.getWidth() * playerScale) / 2,
            playerCreatureY - (playerCreature.getHeight() * playerScale) / 2,
            playerCreature.getWidth() * playerScale,
            playerCreature.getHeight() * playerScale
        );

        // Dessiner la créature ennemie avec effet de pulsation si c'est son tour
        float enemyScale = !playerTurn ? scale : 1.0f;
        renderer.drawSprite(
            enemyCreature.getTextureId(),
            enemyCreatureX - (enemyCreature.getWidth() * enemyScale) / 2,
            enemyCreatureY - (enemyCreature.getHeight() * enemyScale) / 2,
            enemyCreature.getWidth() * enemyScale,
            enemyCreature.getHeight() * enemyScale
        );

        // Dessiner les barres de vie
        drawHealthBar(
            playerCreatureX - 50,
            playerCreatureY - 100,
            100,
            10,
            playerCreature.getHealth(),
            playerCreature.getMaxHealth()
        );
        drawHealthBar(
            enemyCreatureX - 50,
            enemyCreatureY - 80,
            100,
            10,
            enemyCreature.getHealth(),
            enemyCreature.getMaxHealth()
        );

        // Dessiner les statistiques des créatures
        drawCreatureStats(playerCreature, 20, screenHeight - 160, false);
        drawCreatureStats(enemyCreature, screenWidth - 150, 20, true);

        // Dessiner les effets de statut
        drawStatusEffects(playerCreature, 20, screenHeight - 180);
        drawStatusEffects(enemyCreature, screenWidth - 150, 100);

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
        
        // Dessiner un fond semi-transparent
        renderer.drawRect(menuX, menuY, menuWidth, menuHeight, 0xCC000000);
        
        // Dessiner le cadre du menu avec un contour plus épais
        renderer.drawRectOutline(menuX, menuY, menuWidth, menuHeight, 3.0f, 0xFFFFFFFF);
        
        // Dessiner le titre du menu
        renderer.drawText("ACTIONS", menuX + 20.0f, menuY + 15.0f, 24, 0xFFFFFF00);
        
        // Options du menu
        String[] options = {"Attaquer", "Capturer", "Changer", "Fuir"};
        
        // Dessiner les options
        float optionHeight = 40.0f;
        float optionSpacing = 10.0f;
        float optionY = menuY + 50.0f;
        
        for (int i = 0; i < options.length; i++) {
            // Couleur de l'option (blanc, ou jaune si sélectionnée)
            int color = (i == selectedOption) ? 0xFFFFFF00 : 0xFFFFFFFF;
            
            // Dessiner un fond pour l'option sélectionnée
            if (i == selectedOption) {
                renderer.drawRect(menuX + 10.0f, optionY + i * (optionHeight + optionSpacing) - 5.0f, 
                                 menuWidth - 20.0f, optionHeight, 0x44FFFFFF);
            }
            
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
        float menuHeight = 250.0f;
        float menuX = screenWidth - menuWidth - 20.0f;
        float menuY = screenHeight - menuHeight - 20.0f;
        
        // Dessiner un fond semi-transparent
        renderer.drawRect(menuX, menuY, menuWidth, menuHeight, 0xCC000000);
        
        // Dessiner le cadre du menu avec un contour plus épais
        renderer.drawRectOutline(menuX, menuY, menuWidth, menuHeight, 3.0f, 0xFFFFFFFF);
        
        // Dessiner le titre
        renderer.drawText("CAPACITÉS", menuX + 20.0f, menuY + 15.0f, 24, 0xFFFFFF00);
        
        // Dessiner les capacités
        float abilityHeight = 40.0f;
        float abilitySpacing = 10.0f;
        float abilityY = menuY + 60.0f;
        
        for (int i = 0; i < playerCreature.getAbilities().size(); i++) {
            Ability ability = playerCreature.getAbilities().get(i);
            
            // Couleur de la capacité (blanc, ou jaune si sélectionnée)
            int color = (i == selectedAbility) ? 0xFFFFFF00 : 0xFFFFFFFF;
            
            // Dessiner un fond pour l'option sélectionnée
            if (i == selectedAbility) {
                renderer.drawRect(menuX + 10.0f, abilityY + i * (abilityHeight + abilitySpacing) - 5.0f, 
                                 menuWidth - 20.0f, abilityHeight, 0x44FFFFFF);
            }
            
            // Vérifier si la capacité est en temps de récupération
            boolean onCooldown = !playerCreature.getCombatStats().isSkillReady(i);
            int nameColor = onCooldown ? 0xFF888888 : color;
            
            // Dessiner la capacité
            renderer.drawText(ability.getName(), menuX + 30.0f, 
                             abilityY + i * (abilityHeight + abilitySpacing), 18, nameColor);
            
            // Dessiner le type et la puissance de la capacité
            renderer.drawText(ability.getType().toString() + " - Puissance: " + ability.getPower(), 
                             menuX + 200.0f, abilityY + i * (abilityHeight + abilitySpacing), 16, nameColor);
            
            // Afficher le temps de récupération si nécessaire
            if (onCooldown) {
                int cooldown = playerCreature.getCombatStats().getCooldown(i);
                renderer.drawText("Récup: " + cooldown + " tour(s)", menuX + 30.0f, 
                                 abilityY + i * (abilityHeight + abilitySpacing) + 20.0f, 14, 0xFFFF0000);
            }
        }
        
        // Dessiner l'option "Retour"
        int color = (selectedAbility == playerCreature.getAbilities().size()) ? 0xFFFFFF00 : 0xFFFFFFFF;
        
        // Dessiner un fond pour l'option "Retour" si sélectionnée
        if (selectedAbility == playerCreature.getAbilities().size()) {
            renderer.drawRect(menuX + 10.0f, 
                             abilityY + playerCreature.getAbilities().size() * (abilityHeight + abilitySpacing) - 5.0f, 
                             menuWidth - 20.0f, abilityHeight, 0x44FFFFFF);
        }
        
        renderer.drawText("Retour", menuX + 30.0f, 
                         abilityY + playerCreature.getAbilities().size() * (abilityHeight + abilitySpacing), 18, color);
        
        // Dessiner une description de la capacité sélectionnée
        if (selectedAbility < playerCreature.getAbilities().size()) {
            Ability selectedAbilityObj = playerCreature.getAbilities().get(selectedAbility);
            String description = selectedAbilityObj.getDescription();
            if (description == null || description.isEmpty()) {
                description = "Une capacité de type " + selectedAbilityObj.getType().toString();
            }
            renderer.drawText("Description: " + description, 
                             menuX + 20.0f, menuY + menuHeight - 40.0f, 14, 0xFFCCCCCC);
        }
    }
    
    /**
     * Dessiner l'indicateur de tour
     */
    private void drawTurnIndicator() {
        float screenWidth = renderer.getScreenWidth();
        float screenHeight = renderer.getScreenHeight();
        
        // Définir les dimensions et la position de l'indicateur
        float indicatorWidth = 200.0f;
        float indicatorHeight = 40.0f;
        float indicatorX = (screenWidth - indicatorWidth) / 2;
        float indicatorY = 20.0f;
        
        // Couleur de l'indicateur selon le tour
        int backgroundColor = playerTurn ? 0x8000FF00 : 0x80FF0000; // Vert semi-transparent pour joueur, rouge semi-transparent pour ennemi
        int textColor = 0xFFFFFFFF;
        
        // Dessiner le fond de l'indicateur
        renderer.drawRect(indicatorX, indicatorY, indicatorWidth, indicatorHeight, backgroundColor);
        renderer.drawRectOutline(indicatorX, indicatorY, indicatorWidth, indicatorHeight, 2.0f, 0xFFFFFFFF);
        
        // Dessiner le texte de l'indicateur
        String turnText = playerTurn ? "Tour du Joueur" : "Tour de l'Ennemi";
        renderer.drawText(turnText, indicatorX + 20, indicatorY + 12, 18, textColor);
        
        // Ajouter une animation simple pour attirer l'attention
        float time = (float) (System.currentTimeMillis() % 2000) / 2000.0f; // Valeur entre 0 et 1 qui se répète toutes les 2 secondes
        float pulseSize = 5.0f * (float) Math.sin(time * Math.PI * 2); // Valeur entre -5 et 5
        
        // Dessiner un petit indicateur animé
        if (playerTurn) {
            // Flèche pointant vers le joueur
            renderer.drawRect(indicatorX + indicatorWidth - 30, 
                             indicatorY + indicatorHeight + pulseSize, 
                             20, 20, 0xFFFFFF00);
        } else {
            // Flèche pointant vers l'ennemi
            renderer.drawRect(indicatorX + indicatorWidth - 30, 
                             indicatorY - 20 - pulseSize, 
                             20, 20, 0xFFFFFF00);
        }
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
        // Obtenir les statistiques de combat
        CombatStats attackerStats = attacker.getCombatStats();
        CombatStats defenderStats = defender.getCombatStats();
        
        // Déterminer si l'attaque est physique ou magique
        boolean isPhysical = ability.getDamageType() == Ability.DamageType.PHYSICAL;
        
        // Valeurs de base pour l'attaque et la défense
        int attackValue = isPhysical ? attackerStats.getPhysicalAttack() : attackerStats.getMagicalAttack();
        int defenseValue = isPhysical ? defenderStats.getPhysicalDefense() : defenderStats.getMagicalDefense();
        
        // Appliquer la pénétration d'armure/magie
        float penetration = isPhysical ? attackerStats.getArmorPenetration() : attackerStats.getMagicPenetration();
        defenseValue = (int)(defenseValue * (1.0f - penetration / 100.0f));
        
        // Formule de base: (niveau * 2 / 5 + 2) * puissance * attaque / défense / 50 + 2
        float baseDamage = (attacker.getLevel() * 2.0f / 5.0f + 2.0f) * 
                          ability.getPower() * attackValue / Math.max(1, defenseValue) / 50.0f + 2.0f;
        
        // Modificateur de type (à implémenter plus tard)
        float typeModifier = 1.0f;
        
        // Coup critique (basé sur la chance de critique du combattant)
        boolean isCritical = random.nextFloat() * 100 < attackerStats.getCriticalChance();
        float critModifier = isCritical ? attackerStats.getCriticalDamage() : 1.0f;
        
        // Modificateur aléatoire (0.85 - 1.0)
        float randomModifier = 0.85f + random.nextFloat() * 0.15f;
        
        // Calculer les dégâts finaux
        int finalDamage = Math.max(1, (int)(baseDamage * typeModifier * critModifier * randomModifier));
        
        // Appliquer les effets de vol de vie et vampirisme
        if (finalDamage > 0) {
            int lifeStealAmount = 0;
            
            // Appliquer l'omnivampirisme (tous types de dégâts)
            if (attackerStats.getOmnivamp() > 0) {
                lifeStealAmount += (int)(finalDamage * attackerStats.getOmnivamp() / 100.0f);
            }
            // Sinon, appliquer le vol de vie ou vampirisme selon le type
            else if (isPhysical && attackerStats.getLifeSteal() > 0) {
                lifeStealAmount += (int)(finalDamage * attackerStats.getLifeSteal() / 100.0f);
            } else if (!isPhysical && attackerStats.getSpellVamp() > 0) {
                lifeStealAmount += (int)(finalDamage * attackerStats.getSpellVamp() / 100.0f);
            }
            
            // Appliquer le vol de vie
            if (lifeStealAmount > 0) {
                attackerStats.heal(lifeStealAmount);
                System.out.println(attacker.getName() + " récupère " + lifeStealAmount + " PV grâce au vol de vie!");
            }
        }
        
        // Afficher un message pour les coups critiques
        if (isCritical) {
            System.out.println("Coup critique !");
        }
        
        return finalDamage;
    }
    
    /**
     * Exécuter l'action du joueur
     */
    private void executePlayerAction() {
        if (selectedOption == 0) { // Attaque
            // Utiliser la capacité sélectionnée
            Ability ability = playerCreature.getAbilities().get(selectedAbility);
            
            // Vérifier si la capacité est prête (cooldown)
            if (!playerCreature.getCombatStats().isSkillReady(selectedAbility)) {
                currentMessage = ability.getName() + " est en temps de récupération!";
                messageTime = 2.0f;
                state = CombatState.MESSAGE;
                return;
            }
            
            // Vérifier si la capacité touche
            if (ability.checkHit()) {
                // Calculer les dégâts
                int damage = calculateDamage(playerCreature, enemyCreature, ability);
                
                // Appliquer les dégâts
                boolean defeated = enemyCreature.takeDamage(damage);
                
                // Mettre la capacité en temps de récupération
                int cooldown = 2; // Temps de récupération par défaut (2 tours)
                playerCreature.getCombatStats().setCooldown(selectedAbility, cooldown);
                
                // Vérifier si un effet secondaire se déclenche
                if (ability.checkEffect()) {
                    // Appliquer l'effet selon le type
                    applyStatusEffect(playerCreature, enemyCreature, ability.getEffectType());
                }
                
                // Message
                currentMessage = playerCreature.getName() + " utilise " + ability.getName() + " et inflige " + damage + " dégâts!";
                
                // Vérifier si l'ennemi est vaincu
                if (defeated) {
                    checkBattleEnd();
                } else {
                    // Passer au tour de l'ennemi
                    playerTurn = false;
                }
            } else {
                // L'attaque a échoué
                currentMessage = playerCreature.getName() + " rate son attaque!";
                
                // Passer au tour de l'ennemi
                playerTurn = false;
            }
        }
        
        // Afficher le message
        messageTime = 2.0f;
        state = CombatState.MESSAGE;
    }
    
    /**
     * Exécuter le tour de l'ennemi
     */
    private void executeEnemyTurn() {
        // Mettre à jour les temps de récupération des capacités
        playerCreature.getCombatStats().updateCooldowns();
        enemyCreature.getCombatStats().updateCooldowns();
        
        // Mettre à jour les effets de statut
        updateStatusEffects(playerCreature);
        updateStatusEffects(enemyCreature);
        
        // Vérifier si l'ennemi peut agir (paralysie, gel, etc.)
        if (!canAct(enemyCreature)) {
            // Passer au tour du joueur
            playerTurn = true;
            state = CombatState.PLAYER_TURN;
            return;
        }
        
        // Sélectionner une capacité aléatoire
        List<Ability> enemyAbilities = enemyCreature.getAbilities();
        if (!enemyAbilities.isEmpty()) {
            int abilityIndex = random.nextInt(enemyAbilities.size());
            Ability ability = enemyAbilities.get(abilityIndex);
            
            // Vérifier si la capacité touche
            if (ability.checkHit()) {
                // Calculer les dégâts
                int damage = calculateDamage(enemyCreature, playerCreature, ability);
                
                // Appliquer les dégâts
                boolean defeated = playerCreature.takeDamage(damage);
                
                // Vérifier si un effet secondaire se déclenche
                if (ability.checkEffect()) {
                    // Appliquer l'effet selon le type
                    applyStatusEffect(enemyCreature, playerCreature, ability.getEffectType());
                }
                
                // Message
                currentMessage = enemyCreature.getName() + " utilise " + ability.getName() + " et inflige " + damage + " dégâts!";
                
                // Vérifier si le joueur est vaincu
                if (defeated) {
                    checkBattleEnd();
                } else {
                    // Passer au tour du joueur
                    playerTurn = true;
                }
            } else {
                // L'attaque a échoué
                currentMessage = enemyCreature.getName() + " rate son attaque!";
                
                // Passer au tour du joueur
                playerTurn = true;
            }
        } else {
            // Attaque de base si pas de capacités
            int damage = calculateDamage(enemyCreature, playerCreature, new Ability("Attaque", "Attaque de base", enemyCreature.getType(), 40, 95, 999, Ability.EffectType.NONE, 0));
            
            // Appliquer les dégâts
            boolean defeated = playerCreature.takeDamage(damage);
            
            // Message
            currentMessage = enemyCreature.getName() + " attaque et inflige " + damage + " dégâts!";
            
            // Vérifier si le joueur est vaincu
            if (defeated) {
                checkBattleEnd();
            } else {
                // Passer au tour du joueur
                playerTurn = true;
            }
        }
        
        // Afficher le message
        messageTime = 2.0f;
        state = CombatState.MESSAGE;
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
            
            // Calculer et attribuer l'expérience gagnée
            awardExperience();
        }
    }
    
    /**
     * Calculer et attribuer l'expérience gagnée après une victoire
     */
    private void awardExperience() {
        // Utiliser le LevelSystem pour calculer l'expérience gagnée
        LevelSystem levelSystem = LevelSystem.getInstance();
        int expGained = levelSystem.calculateExperienceGain(enemyCreature, playerCreature.getLevel());
        
        // Appliquer des bonus éventuels (objets, etc.)
        // TODO: Implémenter les bonus d'expérience
        
        // Attribuer l'expérience à la créature du joueur
        boolean leveledUp = playerCreature.addExperience(expGained);
        
        // Générer les récompenses de combat
        CombatReward.Reward reward = CombatReward.generateReward(enemyCreature, playerCreature.getLevel());
        
        // Attribuer les récompenses au joueur
        awardRewardsToPlayer(reward);
        
        // Mettre à jour le message de victoire
        StringBuilder message = new StringBuilder();
        message.append("Vous avez gagné ! ").append(playerCreature.getName()).append(" a gagné ").append(expGained).append(" points d'expérience.");
        
        // Si la créature a gagné un niveau, ajouter cette information au message
        if (leveledUp) {
            message.append(" ").append(playerCreature.getName()).append(" a atteint le niveau ").append(playerCreature.getLevel()).append(" !");
        }
        
        // Ajouter les informations sur les récompenses
        message.append("\nVous avez obtenu : ").append(reward.getDescription());
        
        currentMessage = message.toString();
        messageTime = 5.0f; // Augmenter le temps d'affichage pour que le joueur puisse lire toutes les récompenses
    }
    
    /**
     * Attribuer les récompenses au joueur
     * 
     * @param reward Les récompenses à attribuer
     */
    private void awardRewardsToPlayer(CombatReward.Reward reward) {
        // Ajouter les crystaux au joueur
        player.addCrystals(reward.getCrystals());
        
        // Ajouter les objets à l'inventaire du joueur
        if (reward.hasItems()) {
            for (Item item : reward.getItems()) {
                player.getInventory().addItem(item);
            }
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
    
    /**
     * Vérifier si une créature peut agir ce tour-ci
     * 
     * @param creature La créature à vérifier
     * @return true si la créature peut agir, false sinon
     */
    private boolean canAct(Creature creature) {
        // Vérifier les effets de statut qui empêchent d'agir
        CombatStats stats = creature.getCombatStats();
        
        // Vérifier la désorientation (25% de chances de ne pas pouvoir agir)
        if (stats.hasStatusEffect(StatusEffect.DÉSORIENTÉ) && random.nextFloat() < 0.25f) {
            currentMessage = creature.getName() + " est désorienté et ne peut pas agir!";
            messageTime = 2.0f;
            return false;
        }
        
        // Vérifier l'état gelé (impossible d'agir)
        if (stats.hasStatusEffect(StatusEffect.GELÉ)) {
            // 20% de chances de se dégeler
            if (random.nextFloat() < 0.2f) {
                stats.removeStatusEffect(StatusEffect.GELÉ);
                currentMessage = creature.getName() + " n'est plus gelé!";
            } else {
                currentMessage = creature.getName() + " est gelé et ne peut pas agir!";
            }
            messageTime = 2.0f;
            return false;
        }
        
        // Vérifier l'état méditatif (ne peut pas attaquer mais récupère des PV)
        if (stats.hasStatusEffect(StatusEffect.MÉDITATIF)) {
            // 33% de chances de sortir de méditation
            if (random.nextFloat() < 0.33f) {
                stats.removeStatusEffect(StatusEffect.MÉDITATIF);
                currentMessage = creature.getName() + " sort de sa méditation!";
            } else {
                // Récupère 15% de PV
                int healAmount = (int)(creature.getMaxHealth() * 0.15f);
                creature.heal(healAmount);
                currentMessage = creature.getName() + " est en méditation et récupère " + healAmount + " PV!";
            }
            messageTime = 2.0f;
            return false;
        }
        
        // Vérifier la confusion (33% de chances de s'attaquer soi-même)
        if (stats.hasStatusEffect(StatusEffect.CONFUS) && random.nextFloat() < 0.33f) {
            // La créature s'attaque elle-même
            int damage = calculateDamage(creature, creature, new Ability("Confusion", "Attaque de confusion", creature.getType(), 40, 100, 999, Ability.EffectType.NONE, 0));
            creature.takeDamage(damage);
            currentMessage = creature.getName() + " est confus et s'inflige " + damage + " dégâts!";
            messageTime = 2.0f;
            return false;
        }
        
        return true;
    }
    
    /**
     * Appliquer un effet de statut à une cible
     * 
     * @param source La créature qui applique l'effet
     * @param target La créature cible
     * @param effectType Le type d'effet à appliquer
     */
    private void applyStatusEffect(Creature source, Creature target, Ability.EffectType effectType) {
        CombatStats targetStats = target.getCombatStats();
        CombatStats sourceStats = source.getCombatStats();
        
        switch (effectType) {
            case BURN:
                // Appliquer l'effet ENRAGÉ (augmente l'attaque mais réduit la défense)
                targetStats.addStatusEffect(StatusEffect.ENRAGÉ, 3);
                currentMessage += " " + target.getName() + " est enragé!";
                break;
            case FREEZE:
                // Appliquer l'effet GELÉ (réduit la vitesse, peut sauter un tour)
                targetStats.addStatusEffect(StatusEffect.GELÉ, 3);
                currentMessage += " " + target.getName() + " est gelé!";
                break;
            case PARALYZE:
                // Appliquer l'effet DÉSORIENTÉ (réduit la précision)
                targetStats.addStatusEffect(StatusEffect.DÉSORIENTÉ, 3);
                currentMessage += " " + target.getName() + " est désorienté!";
                break;
            case POISON:
                // Appliquer l'effet CORROMPU (perd des PV mais gagne en attaque)
                targetStats.addStatusEffect(StatusEffect.CORROMPU, 4);
                currentMessage += " " + target.getName() + " est corrompu!";
                break;
            case SLEEP:
                // Appliquer l'effet MÉDITATIF (ne peut pas attaquer mais récupère des PV)
                targetStats.addStatusEffect(StatusEffect.MÉDITATIF, 3);
                currentMessage += " " + target.getName() + " entre en méditation!";
                break;
            case CONFUSE:
                // Appliquer l'effet CONFUS (peut s'attaquer soi-même)
                targetStats.addStatusEffect(StatusEffect.CONFUS, 3);
                currentMessage += " " + target.getName() + " est confus!";
                break;
            case FLINCH:
                // Appliquer l'effet DÉSÉQUILIBRÉ (alternance entre états)
                targetStats.addStatusEffect(StatusEffect.DÉSÉQUILIBRÉ, 3);
                currentMessage += " " + target.getName() + " est déséquilibré!";
                break;
            case STAT_BOOST:
                // Appliquer l'effet ILLUMINÉ (révèle les faiblesses, augmente les coups critiques)
                sourceStats.addStatusEffect(StatusEffect.ILLUMINÉ, 3);
                currentMessage += " " + source.getName() + " est illuminé!";
                break;
            case STAT_REDUCE:
                // Appliquer l'effet PRÉVISIBLE (réduit l'esquive, vulnérable aux critiques)
                targetStats.addStatusEffect(StatusEffect.PRÉVISIBLE, 3);
                currentMessage += " " + target.getName() + " devient prévisible!";
                break;
            case HEAL:
                // Appliquer l'effet PROTÉGÉ (immunisé contre les effets négatifs)
                sourceStats.addStatusEffect(StatusEffect.PROTÉGÉ, 3);
                currentMessage += " " + source.getName() + " est protégé!";
                break;
            case DRAIN:
                // Appliquer l'effet CONSUMÉ (absorbe les dégâts mais perd des PV max)
                targetStats.addStatusEffect(StatusEffect.CONSUMÉ, 3);
                currentMessage += " " + target.getName() + " est consumé!";
                break;
            default:
                // Pas d'effet
                break;
        }
    }
    
    /**
     * Mettre à jour les effets de statut d'une créature
     * 
     * @param creature La créature dont il faut mettre à jour les effets
     */
    private void updateStatusEffects(Creature creature) {
        CombatStats stats = creature.getCombatStats();
        
        // Mettre à jour la durée des effets
        stats.updateStatusEffects();
        
        // Appliquer les effets de dégâts continus
        if (stats.hasStatusEffect(StatusEffect.CORROMPU)) {
            int damage = (int)(creature.getMaxHealth() * 0.05f); // 5% des PV max
            creature.takeDamage(damage);
            currentMessage = creature.getName() + " subit " + damage + " dégâts de corruption!";
            messageTime = 1.5f;
        }
        
        if (stats.hasStatusEffect(StatusEffect.MAUDIT)) {
            int damage = (int)(creature.getMaxHealth() * 0.1f); // 10% des PV max
            creature.takeDamage(damage);
            currentMessage = creature.getName() + " subit " + damage + " dégâts de malédiction!";
            messageTime = 1.5f;
        }
        
        if (stats.hasStatusEffect(StatusEffect.CONSUMÉ)) {
            // Réduire les PV max via les statistiques de combat
            int maxHealthReduction = (int)(creature.getMaxHealth() * 0.05f); // 5% des PV max
            CombatStats combatStats = creature.getCombatStats();
            combatStats.setMaxHealth(combatStats.getMaxHealth() - maxHealthReduction);
            
            // Mettre à jour les statistiques de base à partir des statistiques de combat
            creature.updateBasicStatsFromCombatStats();
            
            currentMessage = creature.getName() + " perd " + maxHealthReduction + " PV maximum à cause de la consomption!";
            messageTime = 1.5f;
        }
        
        // Vérifier si la créature est vaincue par les effets
        if (creature.getHealth() <= 0) {
            checkBattleEnd();
        }
    }
    
    /**
     * Dessiner les statistiques d'une créature
     * 
     * @param creature La créature dont il faut dessiner les statistiques
     * @param x Position X
     * @param y Position Y
     * @param isEnemy true si c'est l'ennemi, false si c'est la créature du joueur
     */
    private void drawCreatureStats(Creature creature, float x, float y, boolean isEnemy) {
        // Dessiner les statistiques de base
        renderer.drawText("PV : " + creature.getHealth() + "/" + creature.getMaxHealth(), x, y, 16, 0xFFFFFFFF);
        renderer.drawText("ATK : " + creature.getCombatStats().getPhysicalAttack(), x, y + 20, 16, 0xFFFFFFFF);
        renderer.drawText("DEF : " + creature.getCombatStats().getPhysicalDefense(), x, y + 40, 16, 0xFFFFFFFF);
        
        // Dessiner les statistiques de combat supplémentaires si c'est la créature du joueur
        if (!isEnemy) {
            renderer.drawText("MATK : " + creature.getCombatStats().getMagicalAttack(), x, y + 60, 16, 0xFFFFFFFF);
            renderer.drawText("MDEF : " + creature.getCombatStats().getMagicalDefense(), x, y + 80, 16, 0xFFFFFFFF);
            renderer.drawText("VIT : " + creature.getCombatStats().getSpeed(), x, y + 100, 16, 0xFFFFFFFF);
            renderer.drawText("ESQ : " + creature.getCombatStats().getEvasion(), x, y + 120, 16, 0xFFFFFFFF);
        }
    }
    
    /**
     * Dessiner les effets de statut d'une créature
     * 
     * @param creature Créature dont on veut afficher les effets
     * @param x Position X de départ
     * @param y Position Y de départ
     */
    private void drawStatusEffects(Creature creature, float x, float y) {
        CombatStats stats = creature.getCombatStats();
        
        // Vérifier s'il y a des effets de statut
        if (!stats.hasAnyStatusEffect()) {
            return;
        }
        
        // Dessiner les icônes d'effets de statut
        float iconSize = 24.0f;
        float spacing = 4.0f;
        float currentX = x;
        
        // Vérifier et dessiner chaque effet
        for (StatusEffect effect : StatusEffect.values()) {
            if (effect != StatusEffect.NONE && stats.hasStatusEffect(effect)) {
                // Choisir une couleur en fonction du type d'effet
                int color;
                
                // Utiliser le nom de l'effet pour déterminer la couleur
                String effectName = effect.name();
                
                if (effectName.equals("EMPOISONNÉ") || effectName.equals("ENRACINÉ")) {
                    color = 0xFF00FF00; // Vert
                } else if (effectName.equals("BRÛLÉ") || effectName.equals("ENRAGÉ")) {
                    color = 0xFFFF0000; // Rouge
                } else if (effectName.equals("GELÉ")) {
                    color = 0xFF00FFFF; // Cyan
                } else if (effectName.equals("PARALYSÉ") || effectName.equals("ILLUMINÉ")) {
                    color = 0xFFFFFF00; // Jaune
                } else if (effectName.equals("ENDORMI")) {
                    color = 0xFF8080FF; // Bleu clair
                } else if (effectName.equals("CONFUS") || effectName.equals("DÉSORIENTÉ")) {
                    color = 0xFFFF00FF; // Magenta
                } else if (effectName.equals("CORROMPU")) {
                    color = 0xFF800080; // Violet
                } else if (effectName.equals("MAUDIT")) {
                    color = 0xFF800000; // Bordeaux
                } else if (effectName.equals("PROTÉGÉ")) {
                    color = 0xFF80FF80; // Vert clair
                } else {
                    color = 0xFFCCCCCC; // Gris par défaut
                }
                
                // Dessiner l'icône
                renderer.drawRect(currentX, y, iconSize, iconSize, color);
                renderer.drawRectOutline(currentX, y, iconSize, iconSize, 1.0f, 0xFF000000);
                
                // Dessiner le texte (3 premières lettres de l'effet)
                String text = effectName.substring(0, Math.min(3, effectName.length()));
                renderer.drawText(text, currentX + 2, y + 6, 12, 0xFF000000);
                
                // Passer à la position suivante
                currentX += iconSize + spacing;
            }
        }
    }
}

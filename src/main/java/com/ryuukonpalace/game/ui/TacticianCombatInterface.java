package com.ryuukonpalace.game.ui;

import java.util.ArrayList;
import java.util.List;

import com.ryuukonpalace.game.combat.CombatSystem;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.creatures.Ability;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.utils.ResourceManager;

/**
 * Interface utilisateur pour les combats entre Tacticiens.
 * Gère l'affichage des informations de combat, les variants des deux Tacticiens,
 * et les interactions avec l'utilisateur.
 */
public class TacticianCombatInterface {
    // Singleton
    private static TacticianCombatInterface instance;
    
    // Références
    private CombatSystem combatSystem;
    private Renderer renderer;
    private ResourceManager resourceManager;
    
    // Dimensions de l'écran
    private float screenWidth;
    private float screenHeight;
    
    // Composants UI
    private Panel mainPanel;
    private Panel actionPanel;
    private Panel abilityPanel;
    private Panel itemPanel;
    private Panel messagePanel;
    private Panel tacticianInfoPanel;
    
    // Informations sur les Tacticiens
    private Panel playerTacticianPanel;
    private Panel enemyTacticianPanel;
    private Label playerTacticianNameLabel;
    private Label enemyTacticianNameLabel;
    private Label playerTacticianLevelLabel;
    private Label enemyTacticianLevelLabel;
    
    // Informations sur les variants
    private Panel playerVariantPanel;
    private Panel enemyVariantPanel;
    private ProgressBar playerHealthBar;
    private ProgressBar enemyHealthBar;
    private Label playerVariantNameLabel;
    private Label enemyVariantNameLabel;
    private Label playerVariantLevelLabel;
    private Label enemyVariantLevelLabel;
    private Label playerVariantTypeLabel;
    private Label enemyVariantTypeLabel;
    private Label playerVariantHealthLabel;
    private Label enemyVariantHealthLabel;
    
    // Boutons d'action
    private List<Button> actionButtons;
    private List<Button> abilityButtons;
    private List<Button> itemButtons;
    private List<Button> variantButtons;
    
    // Message
    private Label messageLabel;
    
    // État
    private boolean initialized;
    private boolean visible;
    
    // IDs de textures
    private int combatBackgroundId;
    private int tacticianPlayerImageId;
    private int tacticianEnemyImageId;
    
    // Couleurs
    private static final int COLOR_PANEL_BACKGROUND = 0xCC333333;
    private static final int COLOR_BUTTON_NORMAL = 0xFF4A4A4A;
    private static final int COLOR_BUTTON_HOVER = 0xFF5A5A5A;
    private static final int COLOR_BUTTON_PRESSED = 0xFF3A3A3A;
    private static final int COLOR_BUTTON_DISABLED = 0xFF2A2A2A;
    private static final int COLOR_TEXT_NORMAL = 0xFFFFFFFF;
    private static final int COLOR_HEALTH_BAR_BG = 0xFF333333;
    private static final int COLOR_HEALTH_BAR_FILL = 0xFF00AA00;
    private static final int COLOR_LOW_HEALTH_BAR_FILL = 0xFFAA0000;
    private static final int COLOR_HEALTH_BAR_BORDER = 0xFF000000;
    
    // Animation
    private boolean isAnimating = false;
    private float animationTime = 0;
    private float animationDuration = 1.5f; // Durée de l'animation en secondes
    private String animationMessage = "";
    private float animationX = 0;
    private float animationY = 0;
    private String animationType = "normal"; // Type d'animation (fire, water, earth, air, light, dark, normal)
    private float animationScale = 1.0f; // Échelle de l'animation
    private boolean animationOnEnemy = true; // Si l'animation est sur l'ennemi ou le joueur
    
    // Textures pour les animations
    private int fireAnimationId;
    private int waterAnimationId;
    private int earthAnimationId;
    private int airAnimationId;
    private int lightAnimationId;
    private int darkAnimationId;
    private int normalAnimationId;
    
    // Panneau de changement de variant
    private Panel switchVariantPanel;
    
    /**
     * Constructeur privé (singleton)
     */
    private TacticianCombatInterface() {
        this.combatSystem = CombatSystem.getInstance();
        this.renderer = Renderer.getInstance();
        this.resourceManager = ResourceManager.getInstance();
        this.initialized = false;
        this.visible = false;
        this.actionButtons = new ArrayList<>();
        this.abilityButtons = new ArrayList<>();
        this.itemButtons = new ArrayList<>();
        this.variantButtons = new ArrayList<>();
        this.isAnimating = false;
        this.animationTime = 0;
        this.animationDuration = 1.5f;
        this.animationMessage = "";
        this.animationX = 0;
        this.animationY = 0;
    }
    
    /**
     * Obtenir l'instance unique de l'interface de combat entre Tacticiens
     * 
     * @return Instance de TacticianCombatInterface
     */
    public static TacticianCombatInterface getInstance() {
        if (instance == null) {
            instance = new TacticianCombatInterface();
        }
        return instance;
    }
    
    /**
     * Initialiser l'interface
     */
    public void init() {
        if (initialized) {
            return;
        }
        
        this.visible = false;
        this.initialized = false;
        this.combatSystem = null;
        this.variantButtons = new ArrayList<>();
        this.isAnimating = false;
        this.animationTime = 0;
        this.animationDuration = 1.5f;
        this.animationMessage = "";
        this.animationX = 0;
        this.animationY = 0;
        this.animationType = "normal";
        this.animationScale = 1.0f;
        this.animationOnEnemy = true;
        
        // Récupérer les dimensions de l'écran
        screenWidth = renderer.getScreenWidth();
        screenHeight = renderer.getScreenHeight();
        
        // Charger les textures
        loadTextures();
        
        // Créer les composants UI
        createPanels();
        createLabels();
        createButtons();
        createProgressBars();
        
        initialized = true;
    }
    
    /**
     * Charger les textures nécessaires
     */
    private void loadTextures() {
        // Charger les textures de base
        combatBackgroundId = resourceManager.loadTexture("textures/backgrounds/combat_background.png", "tactician_combat_bg");
        
        // Charger les textures des Tacticiens
        tacticianPlayerImageId = resourceManager.loadTexture("textures/characters/tactician_player.png", "tactician_player");
        tacticianEnemyImageId = resourceManager.loadTexture("textures/characters/tactician_enemy.png", "tactician_enemy");
        
        // Charger les textures d'animation
        fireAnimationId = resourceManager.loadTexture("textures/animations/fire_animation.png", "fire_animation");
        waterAnimationId = resourceManager.loadTexture("textures/animations/water_animation.png", "water_animation");
        earthAnimationId = resourceManager.loadTexture("textures/animations/earth_animation.png", "earth_animation");
        airAnimationId = resourceManager.loadTexture("textures/animations/air_animation.png", "air_animation");
        lightAnimationId = resourceManager.loadTexture("textures/animations/light_animation.png", "light_animation");
        darkAnimationId = resourceManager.loadTexture("textures/animations/dark_animation.png", "dark_animation");
        normalAnimationId = resourceManager.loadTexture("textures/animations/normal_animation.png", "normal_animation");
    }
    
    /**
     * Créer les panneaux
     */
    private void createPanels() {
        // Panneau principal (occupe tout l'écran)
        mainPanel = new Panel(0, 0, screenWidth, screenHeight, 0x00000000);
        
        // Panneau d'information des Tacticiens (en haut)
        float tacticianInfoPanelWidth = screenWidth * 0.8f;
        float tacticianInfoPanelHeight = screenHeight * 0.15f;
        float tacticianInfoPanelX = (screenWidth - tacticianInfoPanelWidth) / 2;
        float tacticianInfoPanelY = screenHeight * 0.05f;
        
        tacticianInfoPanel = new Panel(tacticianInfoPanelX, tacticianInfoPanelY, 
                                      tacticianInfoPanelWidth, tacticianInfoPanelHeight, 
                                      COLOR_PANEL_BACKGROUND);
        mainPanel.addChild(tacticianInfoPanel);
        
        // Panneaux des Tacticiens (gauche et droite)
        float tacticianPanelWidth = tacticianInfoPanelWidth * 0.48f;
        float tacticianPanelHeight = tacticianInfoPanelHeight * 0.9f;
        float tacticianPanelY = tacticianInfoPanelY + (tacticianInfoPanelHeight - tacticianPanelHeight) / 2;
        
        playerTacticianPanel = new Panel(tacticianInfoPanelX + tacticianInfoPanelWidth * 0.02f, 
                                        tacticianPanelY, tacticianPanelWidth, tacticianPanelHeight, 
                                        0x00000000);
        tacticianInfoPanel.addChild(playerTacticianPanel);
        
        enemyTacticianPanel = new Panel(tacticianInfoPanelX + tacticianInfoPanelWidth * 0.5f, 
                                       tacticianPanelY, tacticianPanelWidth, tacticianPanelHeight, 
                                       0x00000000);
        tacticianInfoPanel.addChild(enemyTacticianPanel);
        
        // Panneaux des variants (au centre)
        float variantPanelWidth = screenWidth * 0.4f;
        float variantPanelHeight = screenHeight * 0.3f;
        float playerVariantPanelX = screenWidth * 0.1f;
        float enemyVariantPanelX = screenWidth * 0.5f;
        float variantPanelY = screenHeight * 0.25f;
        
        playerVariantPanel = new Panel(playerVariantPanelX, variantPanelY, 
                                      variantPanelWidth, variantPanelHeight, 
                                      COLOR_PANEL_BACKGROUND);
        mainPanel.addChild(playerVariantPanel);
        
        enemyVariantPanel = new Panel(enemyVariantPanelX, variantPanelY, 
                                     variantPanelWidth, variantPanelHeight, 
                                     COLOR_PANEL_BACKGROUND);
        mainPanel.addChild(enemyVariantPanel);
        
        // Panneau de message (en bas)
        float messagePanelWidth = screenWidth * 0.8f;
        float messagePanelHeight = screenHeight * 0.1f;
        float messagePanelX = (screenWidth - messagePanelWidth) / 2;
        float messagePanelY = screenHeight * 0.6f;
        
        messagePanel = new Panel(messagePanelX, messagePanelY, 
                                messagePanelWidth, messagePanelHeight, 
                                COLOR_PANEL_BACKGROUND);
        mainPanel.addChild(messagePanel);
        
        // Panneau d'action (en bas)
        float actionPanelWidth = screenWidth * 0.8f;
        float actionPanelHeight = screenHeight * 0.2f;
        float actionPanelX = (screenWidth - actionPanelWidth) / 2;
        float actionPanelY = screenHeight * 0.75f;
        
        actionPanel = new Panel(actionPanelX, actionPanelY, 
                               actionPanelWidth, actionPanelHeight, 
                               COLOR_PANEL_BACKGROUND);
        mainPanel.addChild(actionPanel);
        
        // Panneau de capacités (remplace le panneau d'action)
        abilityPanel = new Panel(actionPanelX, actionPanelY, 
                                actionPanelWidth, actionPanelHeight, 
                                COLOR_PANEL_BACKGROUND);
        mainPanel.addChild(abilityPanel);
        abilityPanel.setVisible(false);
        
        // Panneau d'objets (remplace le panneau d'action)
        itemPanel = new Panel(actionPanelX, actionPanelY, 
                             actionPanelWidth, actionPanelHeight, 
                             COLOR_PANEL_BACKGROUND);
        mainPanel.addChild(itemPanel);
        itemPanel.setVisible(false);
        
        // Panneau de changement de variant
        switchVariantPanel = new Panel(
            Renderer.getInstance().getScreenWidth() / 2 - 200,
            Renderer.getInstance().getScreenHeight() / 2 - 150,
            400, 300,
            COLOR_PANEL_BACKGROUND
        );
        mainPanel.addChild(switchVariantPanel);
        switchVariantPanel.setVisible(false);
    }
    
    /**
     * Créer les étiquettes
     */
    private void createLabels() {
        // Étiquettes pour le Tacticien du joueur
        float playerTacticianNameX = playerTacticianPanel.getX() + playerTacticianPanel.getWidth() * 0.5f;
        float playerTacticianNameY = playerTacticianPanel.getY() + 20;
        playerTacticianNameLabel = new Label(playerTacticianNameX, playerTacticianNameY, 
                                           playerTacticianPanel.getWidth(), 30, "Tacticien", COLOR_TEXT_NORMAL);
        playerTacticianPanel.addChild(playerTacticianNameLabel);
        
        float playerTacticianLevelX = playerTacticianPanel.getX() + playerTacticianPanel.getWidth() * 0.5f;
        float playerTacticianLevelY = playerTacticianPanel.getY() + 45;
        playerTacticianLevelLabel = new Label(playerTacticianLevelX, playerTacticianLevelY, 
                                            playerTacticianPanel.getWidth(), 30, "Niveau 1", COLOR_TEXT_NORMAL);
        playerTacticianPanel.addChild(playerTacticianLevelLabel);
        
        // Étiquettes pour le Tacticien ennemi
        float enemyTacticianNameX = enemyTacticianPanel.getX() + enemyTacticianPanel.getWidth() * 0.5f;
        float enemyTacticianNameY = enemyTacticianPanel.getY() + 20;
        enemyTacticianNameLabel = new Label(enemyTacticianNameX, enemyTacticianNameY, 
                                          enemyTacticianPanel.getWidth(), 30, "Tacticien Ennemi", COLOR_TEXT_NORMAL);
        enemyTacticianPanel.addChild(enemyTacticianNameLabel);
        
        float enemyTacticianLevelX = enemyTacticianPanel.getX() + enemyTacticianPanel.getWidth() * 0.5f;
        float enemyTacticianLevelY = enemyTacticianPanel.getY() + 45;
        enemyTacticianLevelLabel = new Label(enemyTacticianLevelX, enemyTacticianLevelY, 
                                           enemyTacticianPanel.getWidth(), 30, "Niveau 1", COLOR_TEXT_NORMAL);
        enemyTacticianPanel.addChild(enemyTacticianLevelLabel);
        
        // Étiquettes pour le variant du joueur
        float playerVariantNameX = playerVariantPanel.getX() + playerVariantPanel.getWidth() * 0.5f;
        float playerVariantNameY = playerVariantPanel.getY() + 20;
        playerVariantNameLabel = new Label(playerVariantNameX, playerVariantNameY, 
                                         playerVariantPanel.getWidth(), 30, "Variant", COLOR_TEXT_NORMAL);
        playerVariantPanel.addChild(playerVariantNameLabel);
        
        float playerVariantLevelX = playerVariantPanel.getX() + playerVariantPanel.getWidth() * 0.5f;
        float playerVariantLevelY = playerVariantPanel.getY() + 45;
        playerVariantLevelLabel = new Label(playerVariantLevelX, playerVariantLevelY, 
                                          playerVariantPanel.getWidth(), 30, "Niveau 1", COLOR_TEXT_NORMAL);
        playerVariantPanel.addChild(playerVariantLevelLabel);
        
        float playerVariantTypeX = playerVariantPanel.getX() + playerVariantPanel.getWidth() * 0.5f;
        float playerVariantTypeY = playerVariantPanel.getY() + 70;
        playerVariantTypeLabel = new Label(playerVariantTypeX, playerVariantTypeY, 
                                         playerVariantPanel.getWidth(), 30, "Type: Stratège", COLOR_TEXT_NORMAL);
        playerVariantPanel.addChild(playerVariantTypeLabel);
        
        float playerVariantHealthX = playerVariantPanel.getX() + playerVariantPanel.getWidth() * 0.5f;
        float playerVariantHealthY = playerVariantPanel.getY() + 100;
        playerVariantHealthLabel = new Label(playerVariantHealthX, playerVariantHealthY, 
                                           playerVariantPanel.getWidth(), 30, "100/100", COLOR_TEXT_NORMAL);
        playerVariantPanel.addChild(playerVariantHealthLabel);
        
        // Étiquettes pour le variant ennemi
        float enemyVariantNameX = enemyVariantPanel.getX() + enemyVariantPanel.getWidth() * 0.5f;
        float enemyVariantNameY = enemyVariantPanel.getY() + 20;
        enemyVariantNameLabel = new Label(enemyVariantNameX, enemyVariantNameY, 
                                        enemyVariantPanel.getWidth(), 30, "Variant Ennemi", COLOR_TEXT_NORMAL);
        enemyVariantPanel.addChild(enemyVariantNameLabel);
        
        float enemyVariantLevelX = enemyVariantPanel.getX() + enemyVariantPanel.getWidth() * 0.5f;
        float enemyVariantLevelY = enemyVariantPanel.getY() + 45;
        enemyVariantLevelLabel = new Label(enemyVariantLevelX, enemyVariantLevelY, 
                                         enemyVariantPanel.getWidth(), 30, "Niveau 1", COLOR_TEXT_NORMAL);
        enemyVariantPanel.addChild(enemyVariantLevelLabel);
        
        float enemyVariantTypeX = enemyVariantPanel.getX() + enemyVariantPanel.getWidth() * 0.5f;
        float enemyVariantTypeY = enemyVariantPanel.getY() + 70;
        enemyVariantTypeLabel = new Label(enemyVariantTypeX, enemyVariantTypeY, 
                                        enemyVariantPanel.getWidth(), 30, "Type: Furieux", COLOR_TEXT_NORMAL);
        enemyVariantPanel.addChild(enemyVariantTypeLabel);
        
        float enemyVariantHealthX = enemyVariantPanel.getX() + enemyVariantPanel.getWidth() * 0.5f;
        float enemyVariantHealthY = enemyVariantPanel.getY() + 100;
        enemyVariantHealthLabel = new Label(enemyVariantHealthX, enemyVariantHealthY, 
                                          enemyVariantPanel.getWidth(), 30, "100/100", COLOR_TEXT_NORMAL);
        enemyVariantPanel.addChild(enemyVariantHealthLabel);
        
        // Étiquette de message
        float messageX = messagePanel.getX() + messagePanel.getWidth() * 0.5f;
        float messageY = messagePanel.getY() + messagePanel.getHeight() * 0.5f;
        messageLabel = new Label(messageX, messageY, messagePanel.getWidth(), 30, 
                               "Un Tacticien ennemi vous défie !", COLOR_TEXT_NORMAL);
        messagePanel.addChild(messageLabel);
    }
    
    /**
     * Créer les boutons
     */
    private void createButtons() {
        // Boutons d'action
        float actionButtonWidth = actionPanel.getWidth() / 2 - 10;
        float actionButtonHeight = 40;
        float actionButtonX = actionPanel.getX() + 5;
        float actionButtonY = actionPanel.getY() + 10;
        
        // Bouton Attaque
        Button attackButton = new Button(
            actionButtonX, 
            actionButtonY, 
            actionButtonWidth, 
            actionButtonHeight, 
            "Attaque",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> onActionButtonClick(button, "attack")
        );
        actionButtons.add(attackButton);
        actionPanel.addChild(attackButton);
        
        // Bouton Capacités
        Button abilitiesButton = new Button(
            actionButtonX + actionButtonWidth + 10, 
            actionButtonY, 
            actionButtonWidth, 
            actionButtonHeight, 
            "Capacités",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> onActionButtonClick(button, "abilities")
        );
        actionButtons.add(abilitiesButton);
        actionPanel.addChild(abilitiesButton);
        
        // Bouton Objets
        Button itemsButton = new Button(
            actionButtonX, 
            actionButtonY + actionButtonHeight + 10, 
            actionButtonWidth, 
            actionButtonHeight, 
            "Objets",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> onActionButtonClick(button, "items")
        );
        actionButtons.add(itemsButton);
        actionPanel.addChild(itemsButton);
        
        // Bouton Changer de variant
        Button switchVariantButton = new Button(
            actionButtonX + actionButtonWidth + 10, 
            actionButtonY + actionButtonHeight + 10, 
            actionButtonWidth, 
            actionButtonHeight, 
            "Changer",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> onActionButtonClick(button, "switch")
        );
        actionButtons.add(switchVariantButton);
        actionPanel.addChild(switchVariantButton);
        
        createAbilityButtons();
        
        // Boutons d'objets (initialement cachés)
        float itemButtonWidth = itemPanel.getWidth() / 2 - 10;
        float itemButtonHeight = 40;
        float itemButtonX = itemPanel.getX() + 5;
        float itemButtonY = itemPanel.getY() + 10;
        
        // Bouton Pierre de Capture
        Button captureStoneButton = new Button(
            itemButtonX, 
            itemButtonY, 
            itemButtonWidth, 
            itemButtonHeight, 
            "Pierre de Capture",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> onItemButtonClick(button, "capture_stone")
        );
        itemButtons.add(captureStoneButton);
        itemPanel.addChild(captureStoneButton);
        
        // Bouton Potion
        Button potionButton = new Button(
            itemButtonX + itemButtonWidth + 10, 
            itemButtonY, 
            itemButtonWidth, 
            itemButtonHeight, 
            "Potion",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> onItemButtonClick(button, "potion")
        );
        itemButtons.add(potionButton);
        itemPanel.addChild(potionButton);
        
        // Bouton Retour pour le panneau des objets
        Button itemBackButton = new Button(
            itemButtonX, 
            itemButtonY + itemButtonHeight + 10, 
            itemPanel.getWidth() - 10, 
            itemButtonHeight, 
            "Retour",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> showActionPanel()
        );
        itemButtons.add(itemBackButton);
        itemPanel.addChild(itemBackButton);
    }
    
    /**
     * Créer les boutons de capacités
     */
    private void createAbilityButtons() {
        // Boutons de capacités (initialement cachés)
        float abilityButtonWidth = abilityPanel.getWidth() / 2 - 10;
        float abilityButtonHeight = 40;
        float abilityButtonX = abilityPanel.getX() + 5;
        float abilityButtonY = abilityPanel.getY() + 10;
        
        for (int index = 0; index < 4; index++) {
            final int abilityIndex = index; // Variable finale pour la lambda expression
            int row = index / 2;
            int col = index % 2;
            float x = abilityButtonX + col * (abilityButtonWidth + 10);
            float y = abilityButtonY + row * (abilityButtonHeight + 10);
            
            Button abilityButton = new Button(
                x, 
                y, 
                abilityButtonWidth, 
                abilityButtonHeight, 
                "Capacité " + (index + 1),
                COLOR_BUTTON_NORMAL,
                COLOR_BUTTON_HOVER,
                COLOR_BUTTON_PRESSED,
                COLOR_BUTTON_DISABLED,
                COLOR_TEXT_NORMAL,
                button -> onAbilityButtonClick(button, abilityIndex)
            );
            abilityButtons.add(abilityButton);
            abilityPanel.addChild(abilityButton);
        }
        
        // Bouton Retour pour le panneau des capacités
        Button abilityBackButton = new Button(
            abilityButtonX, 
            abilityButtonY + 2 * (abilityButtonHeight + 10), 
            abilityPanel.getWidth() - 10, 
            abilityButtonHeight, 
            "Retour",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> showActionPanel()
        );
        abilityButtons.add(abilityBackButton);
        abilityPanel.addChild(abilityBackButton);
    }
    
    /**
     * Créer les barres de progression
     */
    private void createProgressBars() {
        // Barre de vie du variant du joueur
        float playerHealthBarX = playerVariantPanel.getX() + 10;
        float playerHealthBarY = playerVariantPanel.getY() + 100;
        float healthBarWidth = playerVariantPanel.getWidth() - 20;
        float healthBarHeight = 20;
        
        playerHealthBar = new ProgressBar(
            playerHealthBarX, 
            playerHealthBarY, 
            healthBarWidth, 
            healthBarHeight, 
            0, 100, 100, 
            COLOR_HEALTH_BAR_BG, 
            COLOR_HEALTH_BAR_FILL
        );
        playerHealthBar.setHasBorder(true);
        playerHealthBar.setBorderColor(COLOR_HEALTH_BAR_BORDER);
        playerHealthBar.setShowText(false);
        playerVariantPanel.addChild(playerHealthBar);
        
        // Barre de vie du variant ennemi
        float enemyHealthBarX = enemyVariantPanel.getX() + 10;
        float enemyHealthBarY = enemyVariantPanel.getY() + 100;
        
        enemyHealthBar = new ProgressBar(
            enemyHealthBarX, 
            enemyHealthBarY, 
            healthBarWidth, 
            healthBarHeight, 
            0, 100, 100, 
            COLOR_HEALTH_BAR_BG, 
            COLOR_HEALTH_BAR_FILL
        );
        enemyHealthBar.setHasBorder(true);
        enemyHealthBar.setBorderColor(COLOR_HEALTH_BAR_BORDER);
        enemyHealthBar.setShowText(false);
        enemyVariantPanel.addChild(enemyHealthBar);
    }
    
    /**
     * Afficher le panneau d'actions
     */
    private void showActionPanel() {
        actionPanel.setVisible(true);
        abilityPanel.setVisible(false);
        itemPanel.setVisible(false);
    }
    
    /**
     * Afficher le panneau de capacités
     */
    private void showAbilityPanel() {
        actionPanel.setVisible(false);
        abilityPanel.setVisible(true);
        itemPanel.setVisible(false);
        
        // Mettre à jour les boutons de capacités avec les capacités du variant actif
        Creature playerCreature = combatSystem.getPlayerCreature();
        if (playerCreature != null) {
            List<Ability> abilities = playerCreature.getAbilities();
            for (int i = 0; i < Math.min(abilityButtons.size() - 1, 4); i++) { // -1 pour exclure le bouton Retour
                Button abilityButton = abilityButtons.get(i);
                if (i < abilities.size()) {
                    Ability ability = abilities.get(i);
                    abilityButton.setText(ability.getName());
                    abilityButton.setEnabled(playerCreature.canUseAbility(ability));
                } else {
                    abilityButton.setText("---");
                    abilityButton.setEnabled(false);
                }
            }
        }
    }
    
    /**
     * Afficher le panneau des objets
     */
    private void showItemPanel() {
        actionPanel.setVisible(false);
        abilityPanel.setVisible(false);
        itemPanel.setVisible(true);
        
        // Mettre à jour les boutons d'objets avec les objets disponibles
        Player player = Player.getInstance();
        boolean hasCaptureStone = player.hasItem("capture_stone");
        boolean hasPotion = player.hasItem("potion");
        
        // Activer/désactiver les boutons en fonction des objets disponibles
        itemButtons.get(0).setEnabled(hasCaptureStone); // Pierre de capture
        itemButtons.get(1).setEnabled(hasPotion); // Potion
    }
    
    /**
     * Afficher le panneau de changement de variant
     */
    private void showSwitchVariantPanel() {
        // Masquer les autres panneaux
        actionPanel.setVisible(false);
        abilityPanel.setVisible(false);
        itemPanel.setVisible(false);
        
        // Créer dynamiquement des boutons pour chaque variant capturé
        Player player = Player.getInstance();
        List<Creature> capturedCreatures = player.getCapturedCreatures();
        
        // Afficher un message si le joueur n'a pas d'autres variants
        if (capturedCreatures.size() <= 1) {
            messageLabel.setText("Vous n'avez pas d'autres variants disponibles !");
            showActionPanel(); // Revenir au panneau d'action
            return;
        }
        
        // Ajouter des boutons pour chaque variant
        float buttonWidth = 380;
        float buttonHeight = 40;
        float buttonX = 10;
        float buttonY = 10;
        
        switchVariantPanel.clearChildren();
        
        for (int i = 0; i < capturedCreatures.size(); i++) {
            final int variantIndex = i;
            Creature variant = capturedCreatures.get(i);
            
            // Ne pas afficher le variant actuellement en combat
            if (variant == combatSystem.getPlayerCreature()) {
                continue;
            }
            
            Button variantButton = new Button(
                buttonX, 
                buttonY, 
                buttonWidth, 
                buttonHeight, 
                variant.getName() + " (Nv. " + variant.getLevel() + ")",
                COLOR_BUTTON_NORMAL,
                COLOR_BUTTON_HOVER,
                COLOR_BUTTON_PRESSED,
                COLOR_BUTTON_DISABLED,
                COLOR_TEXT_NORMAL,
                button -> onSwitchVariantButtonClick(button, variantIndex)
            );
            
            variantButtons.add(variantButton);
            switchVariantPanel.addChild(variantButton);
            buttonY += buttonHeight + 5;
        }
        
        // Ajouter un bouton de retour
        Button backButton = new Button(
            buttonX, 
            buttonY + 10, 
            buttonWidth, 
            buttonHeight, 
            "Retour",
            COLOR_BUTTON_NORMAL,
            COLOR_BUTTON_HOVER,
            COLOR_BUTTON_PRESSED,
            COLOR_BUTTON_DISABLED,
            COLOR_TEXT_NORMAL,
            button -> showActionPanel()
        );
        
        variantButtons.add(backButton);
        switchVariantPanel.addChild(backButton);
        
        // Afficher le panneau de changement de variant
        switchVariantPanel.setVisible(true);
    }
    
    /**
     * Gère le clic sur les boutons d'action
     * @param button Bouton cliqué
     * @param action Action à effectuer
     */
    private void onActionButtonClick(Button button, String action) {
        switch (action) {
            case "attack":
                // Attaque de base
                startAnimation("Attaque de base !", "normal", true, 1.0f);
                // Calculer les dégâts
                if (combatSystem != null) {
                    Creature playerCreature = combatSystem.getPlayerCreature();
                    Creature enemyCreature = combatSystem.getEnemyCreature();
                    int damage = combatSystem.calculateBasicAttackDamage(playerCreature, enemyCreature);
                    messageLabel.setText("Attaque de base ! " + damage + " dégâts !");
                }
                break;
                
            case "abilities":
                // Afficher le panneau des capacités
                showAbilityPanel();
                break;
                
            case "items":
                // Afficher le panneau des objets
                showItemPanel();
                break;
                
            case "switch":
                // Afficher la liste des variants disponibles
                showSwitchVariantPanel();
                break;
        }
    }
    
    /**
     * Gère le clic sur les boutons de capacité
     * @param button Bouton cliqué
     * @param abilityIndex Index de la capacité
     */
    private void onAbilityButtonClick(Button button, int abilityIndex) {
        // Vérifier si le variant du joueur peut utiliser cette capacité
        Creature playerCreature = combatSystem.getPlayerCreature();
        if (playerCreature != null && abilityIndex >= 0 && abilityIndex < playerCreature.getAbilities().size()) {
            // Utiliser la capacité
            Ability ability = playerCreature.getAbilities().get(abilityIndex);
            
            if (playerCreature.canUseAbility(ability)) {
                // Déterminer le type d'animation en fonction du type de la capacité
                String animationType = "normal";
                String abilityType = ability.getType().toString().toLowerCase();
                
                if (abilityType.contains("fire") || abilityType.contains("feu")) {
                    animationType = "fire";
                } else if (abilityType.contains("water") || abilityType.contains("eau")) {
                    animationType = "water";
                } else if (abilityType.contains("earth") || abilityType.contains("terre")) {
                    animationType = "earth";
                } else if (abilityType.contains("air")) {
                    animationType = "air";
                } else if (abilityType.contains("light") || abilityType.contains("lumière")) {
                    animationType = "light";
                } else if (abilityType.contains("dark") || abilityType.contains("ténèbres")) {
                    animationType = "dark";
                }
                
                // Démarrer l'animation avec le type approprié
                startAnimation(ability.getName() + " !", animationType, true, 1.2f);
                
                // Calculer les dégâts
                if (combatSystem != null) {
                    Creature enemyCreature = combatSystem.getEnemyCreature();
                    int damage = combatSystem.calculateAbilityDamage(playerCreature, enemyCreature, ability);
                    messageLabel.setText(ability.getName() + " ! " + damage + " dégâts !");
                    playerCreature.useAbility(ability);
                }
            } else {
                messageLabel.setText("Impossible d'utiliser cette capacité !");
            }
        } else {
            messageLabel.setText("Capacité non disponible !");
        }
        
        // Revenir au panneau d'action
        showActionPanel();
    }
    
    /**
     * Gère le clic sur les boutons d'objets
     * @param button Bouton cliqué
     * @param itemId ID de l'objet
     */
    private void onItemButtonClick(Button button, String itemId) {
        Player player = Player.getInstance();
        
        switch (itemId) {
            case "capture_stone":
                // Vérifier si le joueur a une pierre de capture
                if (player.hasItem("capture_stone")) {
                    // Calculer les chances de capture
                    if (combatSystem != null) {
                        Creature enemyCreature = combatSystem.getEnemyCreature();
                        float captureChance = combatSystem.calculateCaptureChance(enemyCreature);
                        
                        startAnimation("Tentative de capture !", "light", true, 1.5f);
                        
                        // Simuler la capture (à implémenter plus tard)
                        messageLabel.setText("Tentative de capture ! Chance : " + (int)(captureChance * 100) + "%");
                        
                        // Utiliser une pierre de capture
                        player.removeItem("capture_stone", 1);
                    }
                } else {
                    messageLabel.setText("Vous n'avez pas de pierre de capture !");
                }
                break;
                
            case "potion":
                // Vérifier si le joueur a une potion
                if (player.hasItem("potion")) {
                    // Soigner le variant du joueur
                    Creature playerCreature = combatSystem.getPlayerCreature();
                    if (playerCreature != null) {
                        startAnimation("Utilisation d'une potion !", "water", false, 1.2f);
                        
                        // Soigner de 50 points de vie (à ajuster)
                        int healAmount = 50;
                        // Simuler la guérison (à implémenter plus tard)
                        messageLabel.setText("Utilisation d'une potion ! +" + healAmount + " PV !");
                        
                        // Utiliser une potion
                        player.removeItem("potion", 1);
                    }
                } else {
                    messageLabel.setText("Vous n'avez pas de potion !");
                }
                break;
        }
        
        // Revenir au panneau d'action
        showActionPanel();
    }
    
    /**
     * Gère le clic sur les boutons de changement de variant
     * @param button Bouton cliqué
     * @param variantIndex Index du variant dans la liste des variants capturés
     */
    private void onSwitchVariantButtonClick(Button button, int variantIndex) {
        Player player = Player.getInstance();
        List<Creature> capturedCreatures = player.getCapturedCreatures();
        
        // Vérifier que l'index est valide
        if (variantIndex >= 0 && variantIndex < capturedCreatures.size()) {
            Creature selectedVariant = capturedCreatures.get(variantIndex);
            Creature currentVariant = combatSystem.getPlayerCreature();
            
            // Vérifier que le variant sélectionné n'est pas déjà en combat
            if (selectedVariant != currentVariant) {
                // Animer le changement de variant
                startAnimation("Changement de variant !", "light", false, 1.3f);
                
                // Changer le variant du joueur dans le système de combat
                combatSystem.setPlayerCreature(selectedVariant);
                
                // Mettre à jour les informations du joueur
                updatePlayerHealthBar();
                
                // Afficher un message
                messageLabel.setText(currentVariant.getName() + ", reviens ! " + selectedVariant.getName() + ", à toi !");
            } else {
                messageLabel.setText("Ce variant est déjà en combat !");
            }
        } else {
            messageLabel.setText("Variant non disponible !");
        }
        
        // Cacher le panneau de changement de variant et revenir au panneau d'action
        switchVariantPanel.setVisible(false);
        showActionPanel();
    }
    
    /**
     * Active ou désactive tous les boutons d'action
     * @param enabled true pour activer, false pour désactiver
     */
    private void setButtonsEnabled(boolean enabled) {
        for (Button button : actionButtons) {
            button.setEnabled(enabled);
        }
        for (Button button : abilityButtons) {
            button.setEnabled(enabled);
        }
        for (Button button : itemButtons) {
            button.setEnabled(enabled);
        }
        for (Button button : variantButtons) {
            button.setEnabled(enabled);
        }
    }
    
    /**
     * Déclenche une animation d'attaque
     * @param message Message à afficher
     * @param type Type d'animation (fire, water, earth, air, light, dark, normal)
     * @param onEnemy Si l'animation doit être affichée sur l'ennemi (true) ou le joueur (false)
     * @param scale Échelle de l'animation (1.0f par défaut)
     */
    private void startAnimation(String message, String type, boolean onEnemy, float scale) {
        isAnimating = true;
        animationTime = 0;
        animationMessage = message;
        animationType = type;
        animationOnEnemy = onEnemy;
        animationScale = scale;
        animationX = Renderer.getInstance().getScreenWidth() / 2 - 100;
        animationY = Renderer.getInstance().getScreenHeight() / 2;
        
        // Désactiver les boutons pendant l'animation
        setButtonsEnabled(false);
    }
    
    /**
     * Met à jour les informations du joueur
     * @param playerVariant Variant du joueur
     */
    public void updatePlayerInfo(Creature playerVariant) {
        if (playerVariant != null) {
            playerVariantNameLabel.setText(playerVariant.getName());
            playerVariantLevelLabel.setText("Niveau " + playerVariant.getLevel());
            playerVariantTypeLabel.setText("Type: " + playerVariant.getType().getName());
            playerHealthBar.setMaxValue(playerVariant.getMaxHealth());
            playerHealthBar.setValue(playerVariant.getCurrentHealth());
            playerVariantHealthLabel.setText(playerVariant.getCurrentHealth() + "/" + playerVariant.getMaxHealth());
        }
    }
    
    /**
     * Met à jour les informations de l'ennemi
     * @param enemyVariant Variant de l'ennemi
     */
    public void updateEnemyInfo(Creature enemyVariant) {
        if (enemyVariant != null) {
            enemyVariantNameLabel.setText(enemyVariant.getName());
            enemyVariantLevelLabel.setText("Niveau " + enemyVariant.getLevel());
            enemyVariantTypeLabel.setText("Type: " + enemyVariant.getType().getName());
            enemyHealthBar.setMaxValue(enemyVariant.getMaxHealth());
            enemyHealthBar.setValue(enemyVariant.getCurrentHealth());
            enemyVariantHealthLabel.setText(enemyVariant.getCurrentHealth() + "/" + enemyVariant.getMaxHealth());
        }
    }
    
    /**
     * Afficher l'interface
     */
    public void show() {
        if (!initialized) {
            init();
        }
        
        visible = true;
        
        // Afficher le panneau d'actions par défaut
        showActionPanel();
    }
    
    /**
     * Cacher l'interface
     */
    public void hide() {
        visible = false;
    }
    
    /**
     * Met à jour l'interface
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        if (!visible || !initialized) {
            return;
        }

        // Mise à jour des animations
        if (isAnimating) {
            animationTime += deltaTime;
            
            // Faire varier l'échelle de l'animation pour un effet de pulsation
            float animationProgress = animationTime / animationDuration;
            if (animationProgress < 0.5f) {
                // Phase de croissance
                animationScale = 1.0f + animationProgress * 0.5f;
            } else {
                // Phase de décroissance
                animationScale = 1.5f - (animationProgress - 0.5f) * 1.0f;
            }
            
            if (animationTime >= animationDuration) {
                isAnimating = false;
                animationTime = 0;
                animationMessage = "";
                animationType = "normal";
                animationScale = 1.0f;
                // Réactiver les boutons après l'animation
                setButtonsEnabled(true);
            }
        }

        // Mise à jour des barres de santé
        if (combatSystem != null) {
            Creature playerCreature = combatSystem.getPlayerCreature();
            Creature enemyCreature = combatSystem.getEnemyCreature();

            if (playerCreature != null) {
                float healthPercent = (float) playerCreature.getCurrentHealth() / playerCreature.getMaxHealth();
                playerHealthBar.setValue(playerCreature.getCurrentHealth());
                playerHealthBar.setMaxValue(playerCreature.getMaxHealth());
                playerHealthBar.setFillColor(healthPercent <= 0.25f ? COLOR_LOW_HEALTH_BAR_FILL : COLOR_HEALTH_BAR_FILL);
            }

            if (enemyCreature != null) {
                float healthPercent = (float) enemyCreature.getCurrentHealth() / enemyCreature.getMaxHealth();
                enemyHealthBar.setValue(enemyCreature.getCurrentHealth());
                enemyHealthBar.setMaxValue(enemyCreature.getMaxHealth());
                enemyHealthBar.setFillColor(healthPercent <= 0.25f ? COLOR_LOW_HEALTH_BAR_FILL : COLOR_HEALTH_BAR_FILL);
            }
        }

        // Mise à jour des panneaux
        messagePanel.update(deltaTime);
        actionPanel.update(deltaTime);
        abilityPanel.update(deltaTime);
        itemPanel.update(deltaTime);
        switchVariantPanel.update(deltaTime);
    }
    
    /**
     * Dessiner l'interface
     */
    public void render() {
        if (!visible || !initialized) {
            return;
        }

        Renderer renderer = Renderer.getInstance();
        
        // Dessiner le fond
        renderer.drawUIElement(combatBackgroundId, 0, 0, renderer.getScreenWidth(), renderer.getScreenHeight());
        
        // Dessiner les images des Tacticiens
        renderer.drawUIElement(tacticianPlayerImageId, 50, renderer.getScreenHeight() - 200, 150, 150);
        renderer.drawUIElement(tacticianEnemyImageId, renderer.getScreenWidth() - 200, 50, 150, 150);
        
        // Dessiner les panneaux
        messagePanel.render();
        actionPanel.render();
        abilityPanel.render();
        itemPanel.render();
        switchVariantPanel.render();
        
        // Dessiner l'animation si nécessaire
        if (isAnimating) {
            // Afficher le message d'animation
            renderer.drawText(animationMessage, animationX, animationY, 24, 0xFFFFFFFF);
            
            // Calculer la position de l'animation
            float targetX, targetY;
            if (animationOnEnemy) {
                // Animation sur l'ennemi (côté droit en haut)
                targetX = renderer.getScreenWidth() - 200 + 75 - 50 * animationScale;
                targetY = 50 + 75 - 50 * animationScale;
            } else {
                // Animation sur le joueur (côté gauche en bas)
                targetX = 50 + 75 - 50 * animationScale;
                targetY = renderer.getScreenHeight() - 200 + 75 - 50 * animationScale;
            }
            
            // Taille de l'animation
            float animSize = 100 * animationScale;
            
            // Sélectionner la texture d'animation en fonction du type
            int animationId;
            switch (animationType.toLowerCase()) {
                case "fire":
                    animationId = fireAnimationId;
                    break;
                case "water":
                    animationId = waterAnimationId;
                    break;
                case "earth":
                    animationId = earthAnimationId;
                    break;
                case "air":
                    animationId = airAnimationId;
                    break;
                case "light":
                    animationId = lightAnimationId;
                    break;
                case "dark":
                    animationId = darkAnimationId;
                    break;
                default:
                    animationId = normalAnimationId;
                    break;
            }
            
            // Dessiner l'animation
            renderer.drawUIElement(animationId, targetX, targetY, animSize, animSize);
        }
    }
    
    /**
     * Définir les créatures pour le combat
     * @param playerVariant Le variant du joueur
     * @param enemyVariant Le variant ennemi
     */
    public void setCreatures(Creature playerVariant, Creature enemyVariant) {
        if (playerVariant != null) {
            playerVariantNameLabel.setText(playerVariant.getName());
            playerVariantLevelLabel.setText("Niveau " + playerVariant.getLevel());
            playerVariantTypeLabel.setText("Type: " + playerVariant.getType().getName());
            playerHealthBar.setMaxValue(playerVariant.getMaxHealth());
            playerHealthBar.setValue(playerVariant.getCurrentHealth());
        }
        
        if (enemyVariant != null) {
            enemyVariantNameLabel.setText(enemyVariant.getName());
            enemyVariantLevelLabel.setText("Niveau " + enemyVariant.getLevel());
            enemyVariantTypeLabel.setText("Type: " + enemyVariant.getType().getName());
            enemyHealthBar.setMaxValue(enemyVariant.getMaxHealth());
            enemyHealthBar.setValue(enemyVariant.getCurrentHealth());
        }
    }
    
    /**
     * Définir les Tacticiens pour le combat
     * @param playerTactician Le Tacticien du joueur
     * @param enemyTactician Le Tacticien ennemi
     */
    public void setTacticians(String playerTacticianName, int playerTacticianLevel, 
                             String enemyTacticianName, int enemyTacticianLevel) {
        playerTacticianNameLabel.setText(playerTacticianName);
        playerTacticianLevelLabel.setText("Niveau " + playerTacticianLevel);
        
        enemyTacticianNameLabel.setText(enemyTacticianName);
        enemyTacticianLevelLabel.setText("Niveau " + enemyTacticianLevel);
    }
    
    private void updatePlayerHealthBar() {
        Creature playerCreature = combatSystem.getPlayerCreature();
        if (playerCreature != null) {
            playerHealthBar.setMaxValue(playerCreature.getMaxHealth());
            playerHealthBar.setValue(playerCreature.getCurrentHealth());
            playerVariantHealthLabel.setText(playerCreature.getCurrentHealth() + "/" + playerCreature.getMaxHealth());
        }
    }
}

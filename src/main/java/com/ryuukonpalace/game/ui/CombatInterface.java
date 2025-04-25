package com.ryuukonpalace.game.ui;

import java.util.ArrayList;
import java.util.List;

import com.ryuukonpalace.game.combat.CombatSystem;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.creatures.Ability;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.CaptureStone;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.utils.ResourceManager;

/**
 * Interface utilisateur pour le système de combat.
 * Gère l'affichage des informations de combat et les interactions avec l'utilisateur.
 */
public class CombatInterface {
    // Singleton
    private static CombatInterface instance;
    
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
    private Panel capturePanel;
    private Panel messagePanel;
    
    // Informations sur les créatures
    private Panel playerCreaturePanel;
    private Panel enemyCreaturePanel;
    private ProgressBar playerHealthBar;
    private ProgressBar enemyHealthBar;
    private Label playerCreatureNameLabel;
    private Label enemyCreatureNameLabel;
    private Label playerCreatureLevelLabel;
    private Label enemyCreatureLevelLabel;
    
    // Boutons d'action
    private List<Button> actionButtons;
    private List<Button> abilityButtons;
    private List<Button> captureButtons;
    
    // Message
    private Label messageLabel;
    
    // État
    private boolean initialized;
    private boolean visible;
    
    // Textures
    private int combatBackgroundId;
    // Les textures suivantes sont déclarées mais pas utilisées actuellement
    // Elles seront utilisées dans une future implémentation pour des styles visuels personnalisés
    @SuppressWarnings("unused")
    private int panelBackgroundId;
    @SuppressWarnings("unused")
    private int buttonNormalId;
    @SuppressWarnings("unused")
    private int buttonHoverId;
    @SuppressWarnings("unused")
    private int buttonPressedId;
    @SuppressWarnings("unused")
    private int buttonDisabledId;
    
    // Couleurs
    private static final int COLOR_PANEL_BACKGROUND = 0xCC333333;
    private static final int COLOR_BUTTON_NORMAL = 0xFF4A4A4A;
    private static final int COLOR_BUTTON_HOVER = 0xFF5A5A5A;
    private static final int COLOR_BUTTON_PRESSED = 0xFF3A3A3A;
    private static final int COLOR_BUTTON_DISABLED = 0xFF2A2A2A;
    private static final int COLOR_TEXT_NORMAL = 0xFFFFFFFF;
    // Couleur pour le texte désactivé, sera utilisée dans l'implémentation future des boutons désactivés
    @SuppressWarnings("unused")
    private static final int COLOR_TEXT_DISABLED = 0xFF888888;
    private static final int COLOR_HEALTH_BAR_BG = 0xFF333333;
    private static final int COLOR_HEALTH_BAR_FILL = 0xFF00AA00;
    private static final int COLOR_HEALTH_BAR_BORDER = 0xFF000000;
    
    /**
     * Constructeur privé (singleton)
     */
    private CombatInterface() {
        this.combatSystem = CombatSystem.getInstance();
        this.renderer = Renderer.getInstance();
        this.resourceManager = ResourceManager.getInstance();
        this.initialized = false;
        this.visible = false;
        this.actionButtons = new ArrayList<>();
        this.abilityButtons = new ArrayList<>();
        this.captureButtons = new ArrayList<>();
    }
    
    /**
     * Obtenir l'instance unique de l'interface de combat
     * 
     * @return Instance de CombatInterface
     */
    public static CombatInterface getInstance() {
        if (instance == null) {
            instance = new CombatInterface();
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
        combatBackgroundId = resourceManager.loadTexture("src/main/resources/images/combat/background.png", "combat_background");
        panelBackgroundId = resourceManager.loadTexture("src/main/resources/images/ui/panel_background.png", "panel_background");
        buttonNormalId = resourceManager.loadTexture("src/main/resources/images/ui/button_normal.png", "button_normal");
        buttonHoverId = resourceManager.loadTexture("src/main/resources/images/ui/button_hover.png", "button_hover");
        buttonPressedId = resourceManager.loadTexture("src/main/resources/images/ui/button_pressed.png", "button_pressed");
        buttonDisabledId = resourceManager.loadTexture("src/main/resources/images/ui/button_disabled.png", "button_disabled");
    }
    
    /**
     * Créer les panneaux
     */
    private void createPanels() {
        // Panneau principal (occupe tout l'écran)
        mainPanel = new Panel(0, 0, screenWidth, screenHeight, combatBackgroundId, true);
        
        // Panneau d'action (en bas)
        float actionPanelWidth = screenWidth * 0.8f;
        float actionPanelHeight = screenHeight * 0.2f;
        float actionPanelX = (screenWidth - actionPanelWidth) / 2;
        float actionPanelY = screenHeight - actionPanelHeight - 20;
        actionPanel = new Panel(actionPanelX, actionPanelY, actionPanelWidth, actionPanelHeight, COLOR_PANEL_BACKGROUND);
        actionPanel.setHasBorder(true);
        actionPanel.setBorderColor(0xFF000000);
        actionPanel.setBorderThickness(2.0f);
        mainPanel.addChild(actionPanel);
        
        // Panneau de capacités (remplace le panneau d'action)
        abilityPanel = new Panel(actionPanelX, actionPanelY, actionPanelWidth, actionPanelHeight, COLOR_PANEL_BACKGROUND);
        abilityPanel.setHasBorder(true);
        abilityPanel.setBorderColor(0xFF000000);
        abilityPanel.setBorderThickness(2.0f);
        abilityPanel.setVisible(false);
        mainPanel.addChild(abilityPanel);
        
        // Panneau de capture (remplace le panneau d'action)
        capturePanel = new Panel(actionPanelX, actionPanelY, actionPanelWidth, actionPanelHeight, COLOR_PANEL_BACKGROUND);
        capturePanel.setHasBorder(true);
        capturePanel.setBorderColor(0xFF000000);
        capturePanel.setBorderThickness(2.0f);
        capturePanel.setVisible(false);
        mainPanel.addChild(capturePanel);
        
        // Panneau de message (en haut)
        float messagePanelWidth = screenWidth * 0.6f;
        float messagePanelHeight = screenHeight * 0.1f;
        float messagePanelX = (screenWidth - messagePanelWidth) / 2;
        float messagePanelY = 20;
        messagePanel = new Panel(messagePanelX, messagePanelY, messagePanelWidth, messagePanelHeight, COLOR_PANEL_BACKGROUND);
        messagePanel.setHasBorder(true);
        messagePanel.setBorderColor(0xFF000000);
        messagePanel.setBorderThickness(2.0f);
        mainPanel.addChild(messagePanel);
        
        // Panneau de la créature du joueur (en bas à gauche)
        float creaturePanelWidth = screenWidth * 0.3f;
        float creaturePanelHeight = screenHeight * 0.25f;
        float playerCreaturePanelX = 20;
        float playerCreaturePanelY = screenHeight - creaturePanelHeight - actionPanelHeight - 40;
        playerCreaturePanel = new Panel(playerCreaturePanelX, playerCreaturePanelY, creaturePanelWidth, creaturePanelHeight, COLOR_PANEL_BACKGROUND);
        playerCreaturePanel.setHasBorder(true);
        playerCreaturePanel.setBorderColor(0xFF000000);
        playerCreaturePanel.setBorderThickness(2.0f);
        mainPanel.addChild(playerCreaturePanel);
        
        // Panneau de la créature ennemie (en haut à droite)
        float enemyCreaturePanelX = screenWidth - creaturePanelWidth - 20;
        float enemyCreaturePanelY = messagePanelY + messagePanelHeight + 20;
        enemyCreaturePanel = new Panel(enemyCreaturePanelX, enemyCreaturePanelY, creaturePanelWidth, creaturePanelHeight, COLOR_PANEL_BACKGROUND);
        enemyCreaturePanel.setHasBorder(true);
        enemyCreaturePanel.setBorderColor(0xFF000000);
        enemyCreaturePanel.setBorderThickness(2.0f);
        mainPanel.addChild(enemyCreaturePanel);
    }
    
    /**
     * Créer les étiquettes
     */
    private void createLabels() {
        // Étiquette de message
        messageLabel = new Label(messagePanel.getX() + 10, messagePanel.getY() + 10, 
                                messagePanel.getWidth() - 20, messagePanel.getHeight() - 20, 
                                "", COLOR_TEXT_NORMAL, 18);
        messageLabel.setHorizontalAlignment(1); // Centre
        messageLabel.setVerticalAlignment(1); // Centre
        messagePanel.addChild(messageLabel);
        
        // Étiquettes pour la créature du joueur
        playerCreatureNameLabel = new Label(playerCreaturePanel.getX() + 10, playerCreaturePanel.getY() + 10, 
                                           playerCreaturePanel.getWidth() - 20, 30, 
                                           "Nom", COLOR_TEXT_NORMAL, 16);
        playerCreaturePanel.addChild(playerCreatureNameLabel);
        
        playerCreatureLevelLabel = new Label(playerCreaturePanel.getX() + 10, playerCreaturePanel.getY() + 40, 
                                            playerCreaturePanel.getWidth() - 20, 20, 
                                            "Nv. 1", COLOR_TEXT_NORMAL, 14);
        playerCreaturePanel.addChild(playerCreatureLevelLabel);
        
        // Étiquettes pour la créature ennemie
        enemyCreatureNameLabel = new Label(enemyCreaturePanel.getX() + 10, enemyCreaturePanel.getY() + 10, 
                                          enemyCreaturePanel.getWidth() - 20, 30, 
                                          "Nom", COLOR_TEXT_NORMAL, 16);
        enemyCreaturePanel.addChild(enemyCreatureNameLabel);
        
        enemyCreatureLevelLabel = new Label(enemyCreaturePanel.getX() + 10, enemyCreaturePanel.getY() + 40, 
                                           enemyCreaturePanel.getWidth() - 20, 20, 
                                           "Nv. 1", COLOR_TEXT_NORMAL, 14);
        enemyCreaturePanel.addChild(enemyCreatureLevelLabel);
    }
    
    /**
     * Créer les boutons
     */
    private void createButtons() {
        // Boutons d'action
        String[] actionNames = {"Attaquer", "Capturer", "Objets", "Fuir"};
        float buttonWidth = actionPanel.getWidth() / 2 - 15;
        float buttonHeight = actionPanel.getHeight() / 2 - 15;
        
        for (int i = 0; i < actionNames.length; i++) {
            int row = i / 2;
            int col = i % 2;
            float buttonX = actionPanel.getX() + col * (buttonWidth + 10) + 10;
            float buttonY = actionPanel.getY() + row * (buttonHeight + 10) + 10;
            
            Button button = new Button(buttonX, buttonY, buttonWidth, buttonHeight, actionNames[i],
                                      COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                      this::onActionButtonClick);
            actionButtons.add(button);
            actionPanel.addChild(button);
        }
    }
    
    /**
     * Créer les barres de progression
     */
    private void createProgressBars() {
        // Barre de vie de la créature du joueur
        float barWidth = playerCreaturePanel.getWidth() - 20;
        float barHeight = 20;
        float barX = playerCreaturePanel.getX() + 10;
        float barY = playerCreaturePanel.getY() + playerCreaturePanel.getHeight() - barHeight - 10;
        
        playerHealthBar = new ProgressBar(barX, barY, barWidth, barHeight, 0, 100, 100,
                                         COLOR_HEALTH_BAR_BG, COLOR_HEALTH_BAR_FILL);
        playerHealthBar.setBorderColor(COLOR_HEALTH_BAR_BORDER);
        playerHealthBar.setShowText(true);
        playerHealthBar.setFormat("%.0f / %.0f PV");
        playerCreaturePanel.addChild(playerHealthBar);
        
        // Barre de vie de la créature ennemie
        barX = enemyCreaturePanel.getX() + 10;
        barY = enemyCreaturePanel.getY() + enemyCreaturePanel.getHeight() - barHeight - 10;
        
        enemyHealthBar = new ProgressBar(barX, barY, barWidth, barHeight, 0, 100, 100,
                                        COLOR_HEALTH_BAR_BG, COLOR_HEALTH_BAR_FILL);
        enemyHealthBar.setBorderColor(COLOR_HEALTH_BAR_BORDER);
        enemyHealthBar.setShowText(true);
        enemyHealthBar.setFormat("%.0f / %.0f PV");
        enemyCreaturePanel.addChild(enemyHealthBar);
    }
    
    /**
     * Mettre à jour l'interface
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        if (!initialized || !visible) {
            return;
        }
        
        // Mettre à jour les composants UI
        mainPanel.update(deltaTime);
    }
    
    /**
     * Dessiner l'interface
     */
    public void render() {
        if (!initialized || !visible) {
            return;
        }
        
        // Dessiner les composants UI
        mainPanel.render();
    }
    
    /**
     * Afficher l'interface
     */
    public void show() {
        visible = true;
    }
    
    /**
     * Masquer l'interface
     */
    public void hide() {
        visible = false;
    }
    
    /**
     * Gérer les entrées de l'utilisateur
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param mousePressed true si le bouton de la souris est appuyé, false sinon
     * @return true si l'entrée a été traitée, false sinon
     */
    public boolean handleInput(float mouseX, float mouseY, boolean mousePressed) {
        if (!initialized || !visible) {
            return false;
        }
        
        // Gérer les événements de survol
        mainPanel.onHover(mouseX, mouseY);
        
        // Gérer les événements d'appui
        boolean handled = mainPanel.onPress(mouseX, mouseY, mousePressed);
        
        // Gérer les événements de clic
        if (!mousePressed) {
            handled = handled || mainPanel.onClick(mouseX, mouseY);
        }
        
        return handled;
    }
    
    /**
     * Mettre à jour l'interface avec les informations de combat
     * 
     * @param player Joueur
     * @param playerCreature Créature du joueur
     * @param enemyCreature Créature ennemie
     * @param message Message à afficher
     */
    public void updateCombatInfo(Player player, Creature playerCreature, Creature enemyCreature, String message) {
        if (!initialized) {
            return;
        }
        
        // Mettre à jour les informations des créatures
        updateCreatureInfo(playerCreature, enemyCreature);
        
        // Mettre à jour le message
        messageLabel.setText(message);
        
        // Mettre à jour les boutons de capacité
        updateAbilityButtons(playerCreature);
        
        // Mettre à jour les boutons de capture
        updateCaptureButtons(player);
    }
    
    /**
     * Mettre à jour les informations des créatures
     * 
     * @param playerCreature Créature du joueur
     * @param enemyCreature Créature ennemie
     */
    private void updateCreatureInfo(Creature playerCreature, Creature enemyCreature) {
        // Mettre à jour les informations de la créature du joueur
        playerCreatureNameLabel.setText(playerCreature.getName());
        playerCreatureLevelLabel.setText("Nv. " + playerCreature.getLevel());
        playerHealthBar.setMinValue(0);
        playerHealthBar.setMaxValue(playerCreature.getMaxHealth());
        playerHealthBar.setValue(playerCreature.getHealth());
        
        // Mettre à jour les informations de la créature ennemie
        enemyCreatureNameLabel.setText(enemyCreature.getName());
        enemyCreatureLevelLabel.setText("Nv. " + enemyCreature.getLevel());
        enemyHealthBar.setMinValue(0);
        enemyHealthBar.setMaxValue(enemyCreature.getMaxHealth());
        enemyHealthBar.setValue(enemyCreature.getHealth());
    }
    
    /**
     * Mettre à jour les boutons de capacité
     * 
     * @param playerCreature Créature du joueur
     */
    private void updateAbilityButtons(Creature playerCreature) {
        // Supprimer les boutons existants
        abilityButtons.clear();
        abilityPanel.clearChildren();
        
        // Créer les nouveaux boutons
        List<Ability> abilities = playerCreature.getAbilities();
        float buttonWidth = abilityPanel.getWidth() / 2 - 15;
        float buttonHeight = abilityPanel.getHeight() / 2 - 15;
        
        for (int i = 0; i < abilities.size(); i++) {
            int row = i / 2;
            int col = i % 2;
            float buttonX = abilityPanel.getX() + col * (buttonWidth + 10) + 10;
            float buttonY = abilityPanel.getY() + row * (buttonHeight + 10) + 10;
            
            Ability ability = abilities.get(i);
            Button button = new Button(buttonX, buttonY, buttonWidth, buttonHeight, ability.getName(),
                                      COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                      this::onAbilityButtonClick);
            
            // Désactiver le bouton si la capacité n'est pas utilisable
            boolean isReady = playerCreature.getCombatStats().isSkillReady(i);
            button.setEnabled(isReady);
            
            abilityButtons.add(button);
            abilityPanel.addChild(button);
        }
        
        // Ajouter un bouton de retour
        float buttonX = abilityPanel.getX() + 10;
        float buttonY = abilityPanel.getY() + abilityPanel.getHeight() - buttonHeight - 10;
        
        Button backButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight, "Retour",
                                     COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                     this::onBackButtonClick);
        abilityButtons.add(backButton);
        abilityPanel.addChild(backButton);
    }
    
    /**
     * Mettre à jour les boutons de capture
     * 
     * @param player Joueur
     */
    private void updateCaptureButtons(Player player) {
        // Supprimer les boutons existants
        captureButtons.clear();
        capturePanel.clearChildren();
        
        // Créer les nouveaux boutons
        List<CaptureStone> captureStones = player.getInventory().getCaptureStones();
        float buttonWidth = capturePanel.getWidth() / 2 - 15;
        float buttonHeight = capturePanel.getHeight() / 2 - 15;
        
        for (int i = 0; i < captureStones.size() && i < 3; i++) {
            int row = i / 2;
            int col = i % 2;
            float buttonX = capturePanel.getX() + col * (buttonWidth + 10) + 10;
            float buttonY = capturePanel.getY() + row * (buttonHeight + 10) + 10;
            
            CaptureStone stone = captureStones.get(i);
            String buttonText = stone.getMaterial().getName() + " " + stone.getType().getName();
            Button button = new Button(buttonX, buttonY, buttonWidth, buttonHeight, buttonText,
                                     COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                     this::onCaptureButtonClick);
            
            captureButtons.add(button);
            capturePanel.addChild(button);
        }
        
        // Ajouter un bouton de retour
        float buttonX = capturePanel.getX() + 10;
        float buttonY = capturePanel.getY() + capturePanel.getHeight() - buttonHeight - 10;
        
        Button backButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight, "Retour",
                                     COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                     this::onBackButtonClick);
        captureButtons.add(backButton);
        capturePanel.addChild(backButton);
    }
    
    /**
     * Afficher le menu principal
     */
    public void showMainMenu() {
        actionPanel.setVisible(true);
        abilityPanel.setVisible(false);
        capturePanel.setVisible(false);
    }
    
    /**
     * Afficher le menu de capacités
     */
    public void showAbilityMenu() {
        actionPanel.setVisible(false);
        abilityPanel.setVisible(true);
        capturePanel.setVisible(false);
    }
    
    /**
     * Afficher le menu de capture
     */
    public void showCaptureMenu() {
        actionPanel.setVisible(false);
        abilityPanel.setVisible(false);
        capturePanel.setVisible(true);
    }
    
    /**
     * Gérer le clic sur un bouton d'action
     * 
     * @param button Bouton cliqué
     */
    private void onActionButtonClick(Button button) {
        int index = actionButtons.indexOf(button);
        if (index == -1) {
            return;
        }
        
        switch (index) {
            case 0: // Attaquer
                showAbilityMenu();
                break;
            case 1: // Capturer
                showCaptureMenu();
                break;
            case 2: // Objets
                // TODO: Implémenter le menu d'objets
                break;
            case 3: // Fuir
                combatSystem.tryToFlee();
                break;
        }
    }
    
    /**
     * Gérer le clic sur un bouton de capacité
     * 
     * @param button Bouton cliqué
     */
    private void onAbilityButtonClick(Button button) {
        int index = abilityButtons.indexOf(button);
        if (index == -1 || index == abilityButtons.size() - 1) {
            return; // Ignorer le bouton de retour
        }
        
        // Utiliser la capacité
        combatSystem.useAbility(index);
        
        // Revenir au menu principal
        showMainMenu();
    }
    
    /**
     * Gérer le clic sur un bouton de capture
     * 
     * @param button Bouton cliqué
     */
    private void onCaptureButtonClick(Button button) {
        int index = captureButtons.indexOf(button);
        if (index == -1 || index == captureButtons.size() - 1) {
            return; // Ignorer le bouton de retour
        }
        
        // Utiliser la pierre de capture
        // TODO: Implémenter la capture avec la pierre sélectionnée
        
        // Revenir au menu principal
        showMainMenu();
    }
    
    /**
     * Gérer le clic sur un bouton de retour
     * 
     * @param button Bouton cliqué
     */
    private void onBackButtonClick(Button button) {
        showMainMenu();
    }
    
    /**
     * Vérifier si l'interface est initialisée
     * 
     * @return true si l'interface est initialisée, false sinon
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Vérifier si l'interface est visible
     * 
     * @return true si l'interface est visible, false sinon
     */
    public boolean isVisible() {
        return visible;
    }
}

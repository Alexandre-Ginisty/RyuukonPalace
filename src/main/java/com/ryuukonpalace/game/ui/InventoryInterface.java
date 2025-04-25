package com.ryuukonpalace.game.ui;

import java.util.ArrayList;
import java.util.List;

import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.CaptureStone;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.player.Inventory;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.utils.ResourceManager;

/**
 * Interface utilisateur pour l'inventaire du joueur.
 * Permet de gérer les objets, les pierres de capture et les créatures capturées.
 */
public class InventoryInterface {
    // Singleton
    private static InventoryInterface instance;
    
    // Références
    private Renderer renderer;
    private ResourceManager resourceManager;
    
    // Dimensions de l'écran
    private float screenWidth;
    private float screenHeight;
    
    // Composants UI
    private Panel mainPanel;
    private Panel itemsPanel;
    private Panel captureStonePanel;
    private Panel creaturesPanel;
    private Panel detailPanel;
    
    // Onglets
    private Button itemsTabButton;
    private Button captureStonesTabButton;
    private Button creaturesTabButton;
    
    // Boutons d'action
    private Button useButton;
    private Button dropButton;
    private Button closeButton;
    
    // Grilles
    private List<Button> itemButtons;
    private List<Button> captureStoneButtons;
    private List<Button> creatureButtons;
    
    // Détails
    private Label nameLabel;
    private Label descriptionLabel;
    private Label statsLabel;
    
    // État
    private boolean initialized;
    private boolean visible;
    private int currentTab; // 0 = objets, 1 = pierres de capture, 2 = créatures
    private int selectedItemIndex;
    private int selectedCaptureStoneIndex;
    private int selectedCreatureIndex;
    
    // Textures
    private int inventoryBackgroundId;
    private int tabNormalId;
    private int tabSelectedId;
    private int itemSlotId;
    private int selectedSlotId;
    
    // Couleurs
    private static final int COLOR_PANEL_BACKGROUND = 0xCC333333;
    private static final int COLOR_BUTTON_NORMAL = 0xFF4A4A4A;
    private static final int COLOR_BUTTON_HOVER = 0xFF5A5A5A;
    private static final int COLOR_BUTTON_PRESSED = 0xFF3A3A3A;
    private static final int COLOR_BUTTON_DISABLED = 0xFF2A2A2A;
    private static final int COLOR_BUTTON_SELECTED = 0xFF6A6AFF;
    private static final int COLOR_TEXT_NORMAL = 0xFFFFFFFF;
    private static final int COLOR_TEXT_DISABLED = 0xFF888888;
    
    /**
     * Constructeur privé (singleton)
     */
    private InventoryInterface() {
        this.renderer = Renderer.getInstance();
        this.resourceManager = ResourceManager.getInstance();
        this.initialized = false;
        this.visible = false;
        this.currentTab = 0;
        this.selectedItemIndex = -1;
        this.selectedCaptureStoneIndex = -1;
        this.selectedCreatureIndex = -1;
        this.itemButtons = new ArrayList<>();
        this.captureStoneButtons = new ArrayList<>();
        this.creatureButtons = new ArrayList<>();
    }
    
    /**
     * Obtenir l'instance unique de l'interface d'inventaire
     * 
     * @return Instance de InventoryInterface
     */
    public static InventoryInterface getInstance() {
        if (instance == null) {
            instance = new InventoryInterface();
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
        createTabs();
        createButtons();
        createLabels();
        
        initialized = true;
    }
    
    /**
     * Charger les textures nécessaires
     */
    private void loadTextures() {
        // Charger les textures de base
        inventoryBackgroundId = resourceManager.loadTexture("src/main/resources/images/ui/inventory_background.png", "inventory_background");
        tabNormalId = resourceManager.loadTexture("src/main/resources/images/ui/tab_normal.png", "tab_normal");
        tabSelectedId = resourceManager.loadTexture("src/main/resources/images/ui/tab_selected.png", "tab_selected");
        itemSlotId = resourceManager.loadTexture("src/main/resources/images/ui/item_slot.png", "item_slot");
        selectedSlotId = resourceManager.loadTexture("src/main/resources/images/ui/selected_slot.png", "selected_slot");
    }
    
    /**
     * Créer les panneaux
     */
    private void createPanels() {
        // Panneau principal (centré à l'écran)
        float mainPanelWidth = screenWidth * 0.8f;
        float mainPanelHeight = screenHeight * 0.8f;
        float mainPanelX = (screenWidth - mainPanelWidth) / 2;
        float mainPanelY = (screenHeight - mainPanelHeight) / 2;
        
        mainPanel = new Panel(mainPanelX, mainPanelY, mainPanelWidth, mainPanelHeight, inventoryBackgroundId, true);
        mainPanel.setHasBorder(true);
        mainPanel.setBorderColor(0xFF000000);
        mainPanel.setBorderThickness(2.0f);
        
        // Panneau des objets (partie gauche)
        float contentPanelWidth = mainPanelWidth * 0.6f;
        float contentPanelHeight = mainPanelHeight - 60; // Espace pour les onglets
        float contentPanelX = mainPanelX + 10;
        float contentPanelY = mainPanelY + 50;
        
        itemsPanel = new Panel(contentPanelX, contentPanelY, contentPanelWidth, contentPanelHeight, COLOR_PANEL_BACKGROUND);
        itemsPanel.setHasBorder(true);
        itemsPanel.setBorderColor(0xFF000000);
        itemsPanel.setBorderThickness(1.0f);
        mainPanel.addChild(itemsPanel);
        
        // Panneau des pierres de capture (même position que le panneau des objets)
        captureStonePanel = new Panel(contentPanelX, contentPanelY, contentPanelWidth, contentPanelHeight, COLOR_PANEL_BACKGROUND);
        captureStonePanel.setHasBorder(true);
        captureStonePanel.setBorderColor(0xFF000000);
        captureStonePanel.setBorderThickness(1.0f);
        captureStonePanel.setVisible(false);
        mainPanel.addChild(captureStonePanel);
        
        // Panneau des créatures (même position que le panneau des objets)
        creaturesPanel = new Panel(contentPanelX, contentPanelY, contentPanelWidth, contentPanelHeight, COLOR_PANEL_BACKGROUND);
        creaturesPanel.setHasBorder(true);
        creaturesPanel.setBorderColor(0xFF000000);
        creaturesPanel.setBorderThickness(1.0f);
        creaturesPanel.setVisible(false);
        mainPanel.addChild(creaturesPanel);
        
        // Panneau de détails (partie droite)
        float detailPanelWidth = mainPanelWidth - contentPanelWidth - 30;
        float detailPanelX = contentPanelX + contentPanelWidth + 10;
        float detailPanelY = contentPanelY;
        
        detailPanel = new Panel(detailPanelX, detailPanelY, detailPanelWidth, contentPanelHeight, COLOR_PANEL_BACKGROUND);
        detailPanel.setHasBorder(true);
        detailPanel.setBorderColor(0xFF000000);
        detailPanel.setBorderThickness(1.0f);
        mainPanel.addChild(detailPanel);
    }
    
    /**
     * Créer les onglets
     */
    private void createTabs() {
        float tabWidth = mainPanel.getWidth() / 3 - 20;
        float tabHeight = 40;
        float tabY = mainPanel.getY() + 10;
        
        // Onglet des objets
        float itemsTabX = mainPanel.getX() + 10;
        itemsTabButton = new Button(itemsTabX, tabY, tabWidth, tabHeight, "Objets",
                                   COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                   this::onTabButtonClick);
        mainPanel.addChild(itemsTabButton);
        
        // Onglet des pierres de capture
        float captureStonesTabX = itemsTabX + tabWidth + 10;
        captureStonesTabButton = new Button(captureStonesTabX, tabY, tabWidth, tabHeight, "Pierres de Capture",
                                          COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                          this::onTabButtonClick);
        mainPanel.addChild(captureStonesTabButton);
        
        // Onglet des créatures
        float creaturesTabX = captureStonesTabX + tabWidth + 10;
        creaturesTabButton = new Button(creaturesTabX, tabY, tabWidth, tabHeight, "Créatures",
                                       COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                       this::onTabButtonClick);
        mainPanel.addChild(creaturesTabButton);
        
        // Sélectionner l'onglet des objets par défaut
        updateTabSelection();
    }
    
    /**
     * Créer les boutons
     */
    private void createButtons() {
        float buttonWidth = detailPanel.getWidth() - 20;
        float buttonHeight = 40;
        float buttonX = detailPanel.getX() + 10;
        float buttonY = detailPanel.getY() + detailPanel.getHeight() - buttonHeight * 3 - 30;
        
        // Bouton Utiliser
        useButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight, "Utiliser",
                              COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                              this::onUseButtonClick);
        useButton.setEnabled(false);
        detailPanel.addChild(useButton);
        
        // Bouton Jeter
        dropButton = new Button(buttonX, buttonY + buttonHeight + 10, buttonWidth, buttonHeight, "Jeter",
                               COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                               this::onDropButtonClick);
        dropButton.setEnabled(false);
        detailPanel.addChild(dropButton);
        
        // Bouton Fermer
        closeButton = new Button(buttonX, buttonY + buttonHeight * 2 + 20, buttonWidth, buttonHeight, "Fermer",
                                COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                this::onCloseButtonClick);
        detailPanel.addChild(closeButton);
    }
    
    /**
     * Créer les étiquettes
     */
    private void createLabels() {
        float labelX = detailPanel.getX() + 10;
        float labelWidth = detailPanel.getWidth() - 20;
        
        // Étiquette du nom
        nameLabel = new Label(labelX, detailPanel.getY() + 10, labelWidth, 30, "", COLOR_TEXT_NORMAL, 18);
        nameLabel.setHorizontalAlignment(1); // Centre
        detailPanel.addChild(nameLabel);
        
        // Étiquette de description
        descriptionLabel = new Label(labelX, detailPanel.getY() + 50, labelWidth, 100, "", COLOR_TEXT_NORMAL, 14);
        descriptionLabel.setHorizontalAlignment(0); // Gauche
        detailPanel.addChild(descriptionLabel);
        
        // Étiquette des statistiques
        statsLabel = new Label(labelX, detailPanel.getY() + 160, labelWidth, 200, "", COLOR_TEXT_NORMAL, 14);
        statsLabel.setHorizontalAlignment(0); // Gauche
        detailPanel.addChild(statsLabel);
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
     * 
     * @param player Joueur dont l'inventaire doit être affiché
     */
    public void show(Player player) {
        if (!initialized) {
            init();
        }
        
        // Mettre à jour l'interface avec les données du joueur
        updateInventoryDisplay(player);
        
        visible = true;
    }
    
    /**
     * Masquer l'interface
     */
    public void hide() {
        visible = false;
        
        // Réinitialiser la sélection
        selectedItemIndex = -1;
        selectedCaptureStoneIndex = -1;
        selectedCreatureIndex = -1;
        
        // Désactiver les boutons d'action
        useButton.setEnabled(false);
        dropButton.setEnabled(false);
        
        // Effacer les détails
        nameLabel.setText("");
        descriptionLabel.setText("");
        statsLabel.setText("");
    }
    
    /**
     * Gérer les entrées de l'utilisateur
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param mousePressed true si le bouton de la souris est appuyé, false sinon
     */
    public void handleInput(float mouseX, float mouseY, boolean mousePressed) {
        if (!initialized || !visible) {
            return;
        }
        
        // Gérer les événements de survol
        mainPanel.onHover(mouseX, mouseY);
        
        // Gérer les événements d'appui
        mainPanel.onPress(mouseX, mouseY, mousePressed);
        
        // Gérer les événements de clic
        if (!mousePressed) {
            mainPanel.onClick(mouseX, mouseY);
        }
    }
    
    /**
     * Mettre à jour l'affichage de l'inventaire
     * 
     * @param player Joueur dont l'inventaire doit être affiché
     */
    public void updateInventoryDisplay(Player player) {
        if (player == null) {
            return;
        }
        
        // Récupérer les objets du joueur
        List<Item> items = player.getInventory().getItems();
        List<CaptureStone> captureStones = player.getInventory().getCaptureStones();
        List<Creature> creatures = player.getCapturedCreatures();
        
        // Mettre à jour l'affichage selon l'onglet actuel
        switch (currentTab) {
            case 0: // Objets
                updateItemsDisplay(items);
                break;
            case 1: // Pierres de capture
                updateCaptureStonesDisplay(captureStones);
                break;
            case 2: // Créatures
                updateCreaturesDisplay(creatures);
                break;
        }
        
        // Mettre à jour l'affichage des détails
        updateDetailsDisplay();
    }
    
    /**
     * Mettre à jour l'affichage des objets
     * 
     * @param items Liste des objets
     */
    private void updateItemsDisplay(List<Item> items) {
        // Supprimer les boutons existants
        itemButtons.clear();
        itemsPanel.clearChildren();
        
        // Créer les nouveaux boutons
        int columns = 4;
        int rows = (int) Math.ceil((double) items.size() / columns);
        float slotSize = 60;
        float startX = itemsPanel.getX() + 10;
        float startY = itemsPanel.getY() + 10;
        
        for (int i = 0; i < items.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            float x = startX + col * (slotSize + 10);
            float y = startY + row * (slotSize + 10);
            
            Item item = items.get(i);
            Button button = new Button(x, y, slotSize, slotSize, "",
                                     COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                     this::onItemButtonClick);
            
            // Ajouter l'icône de l'objet
            // TODO: Implémenter l'affichage des icônes
            
            // Sélectionner le bouton si nécessaire
            if (i == selectedItemIndex) {
                button.setBackgroundColor(COLOR_BUTTON_SELECTED);
            }
            
            itemButtons.add(button);
            itemsPanel.addChild(button);
        }
    }
    
    /**
     * Mettre à jour l'affichage des pierres de capture
     * 
     * @param captureStones Liste des pierres de capture
     */
    private void updateCaptureStonesDisplay(List<CaptureStone> captureStones) {
        // Supprimer les boutons existants
        captureStoneButtons.clear();
        captureStonePanel.clearChildren();
        
        // Créer les nouveaux boutons
        int columns = 4;
        int rows = (int) Math.ceil((double) captureStones.size() / columns);
        float slotSize = 60;
        float startX = captureStonePanel.getX() + 10;
        float startY = captureStonePanel.getY() + 10;
        
        for (int i = 0; i < captureStones.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            float x = startX + col * (slotSize + 10);
            float y = startY + row * (slotSize + 10);
            
            CaptureStone stone = captureStones.get(i);
            Button button = new Button(x, y, slotSize, slotSize, "",
                                     COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                     this::onCaptureStoneButtonClick);
            
            // Ajouter l'icône de la pierre
            // TODO: Implémenter l'affichage des icônes
            
            // Sélectionner le bouton si nécessaire
            if (i == selectedCaptureStoneIndex) {
                button.setBackgroundColor(COLOR_BUTTON_SELECTED);
            }
            
            captureStoneButtons.add(button);
            captureStonePanel.addChild(button);
        }
    }
    
    /**
     * Mettre à jour l'affichage des créatures
     * 
     * @param creatures Liste des créatures
     */
    private void updateCreaturesDisplay(List<Creature> creatures) {
        // Supprimer les boutons existants
        creatureButtons.clear();
        creaturesPanel.clearChildren();
        
        // Créer les nouveaux boutons
        int columns = 2;
        int rows = (int) Math.ceil((double) creatures.size() / columns);
        float slotWidth = (creaturesPanel.getWidth() - 30) / columns;
        float slotHeight = 80;
        float startX = creaturesPanel.getX() + 10;
        float startY = creaturesPanel.getY() + 10;
        
        for (int i = 0; i < creatures.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            float x = startX + col * (slotWidth + 10);
            float y = startY + row * (slotHeight + 10);
            
            Creature creature = creatures.get(i);
            Button button = new Button(x, y, slotWidth, slotHeight, creature.getName(),
                                     COLOR_BUTTON_NORMAL, COLOR_BUTTON_HOVER, COLOR_BUTTON_PRESSED, COLOR_BUTTON_DISABLED, COLOR_TEXT_NORMAL,
                                     this::onCreatureButtonClick);
            
            // Sélectionner le bouton si nécessaire
            if (i == selectedCreatureIndex) {
                button.setBackgroundColor(COLOR_BUTTON_SELECTED);
            }
            
            creatureButtons.add(button);
            creaturesPanel.addChild(button);
        }
    }
    
    /**
     * Mettre à jour l'affichage des détails
     */
    private void updateDetailsDisplay() {
        // Vider les détails si rien n'est sélectionné
        if (selectedItemIndex == -1 && selectedCaptureStoneIndex == -1 && selectedCreatureIndex == -1) {
            nameLabel.setText("");
            descriptionLabel.setText("");
            statsLabel.setText("");
            useButton.setEnabled(false);
            dropButton.setEnabled(false);
            return;
        }
        
        // Mettre à jour les détails selon l'onglet actif
        switch (currentTab) {
            case 0: // Objets
                if (selectedItemIndex != -1) {
                    updateItemDetails();
                }
                break;
            case 1: // Pierres de capture
                if (selectedCaptureStoneIndex != -1) {
                    updateCaptureStoneDetails();
                }
                break;
            case 2: // Créatures
                if (selectedCreatureIndex != -1) {
                    updateCreatureDetails();
                }
                break;
        }
    }
    
    /**
     * Mettre à jour les détails d'un objet
     */
    private void updateItemDetails() {
        // TODO: Implémenter l'affichage des détails d'un objet
        nameLabel.setText("Objet #" + selectedItemIndex);
        descriptionLabel.setText("Description de l'objet...");
        statsLabel.setText("Statistiques de l'objet...");
        
        useButton.setEnabled(true);
        dropButton.setEnabled(true);
    }
    
    /**
     * Mettre à jour les détails d'une pierre de capture
     */
    private void updateCaptureStoneDetails() {
        // TODO: Implémenter l'affichage des détails d'une pierre de capture
        nameLabel.setText("Pierre de Capture #" + selectedCaptureStoneIndex);
        descriptionLabel.setText("Description de la pierre...");
        statsLabel.setText("Statistiques de la pierre...");
        
        useButton.setEnabled(true);
        dropButton.setEnabled(true);
    }
    
    /**
     * Mettre à jour les détails d'une créature
     */
    private void updateCreatureDetails() {
        // TODO: Implémenter l'affichage des détails d'une créature
        nameLabel.setText("Créature #" + selectedCreatureIndex);
        descriptionLabel.setText("Description de la créature...");
        statsLabel.setText("Statistiques de la créature...");
        
        useButton.setEnabled(true);
        dropButton.setEnabled(true);
    }
    
    /**
     * Mettre à jour la sélection des onglets
     */
    private void updateTabSelection() {
        // Mettre à jour l'apparence des onglets
        itemsTabButton.setBackgroundColor(currentTab == 0 ? COLOR_BUTTON_SELECTED : COLOR_BUTTON_NORMAL);
        captureStonesTabButton.setBackgroundColor(currentTab == 1 ? COLOR_BUTTON_SELECTED : COLOR_BUTTON_NORMAL);
        creaturesTabButton.setBackgroundColor(currentTab == 2 ? COLOR_BUTTON_SELECTED : COLOR_BUTTON_NORMAL);
        
        // Afficher le panneau correspondant
        itemsPanel.setVisible(currentTab == 0);
        captureStonePanel.setVisible(currentTab == 1);
        creaturesPanel.setVisible(currentTab == 2);
        
        // Réinitialiser la sélection
        selectedItemIndex = -1;
        selectedCaptureStoneIndex = -1;
        selectedCreatureIndex = -1;
        
        // Désactiver les boutons d'action
        useButton.setEnabled(false);
        dropButton.setEnabled(false);
        
        // Effacer les détails
        nameLabel.setText("");
        descriptionLabel.setText("");
        statsLabel.setText("");
    }
    
    /**
     * Gérer le clic sur un onglet
     * 
     * @param button Bouton cliqué
     */
    private void onTabButtonClick(Button button) {
        if (button == itemsTabButton) {
            currentTab = 0;
        } else if (button == captureStonesTabButton) {
            currentTab = 1;
        } else if (button == creaturesTabButton) {
            currentTab = 2;
        }
        
        updateTabSelection();
    }
    
    /**
     * Gérer le clic sur un objet
     * 
     * @param button Bouton cliqué
     */
    private void onItemButtonClick(Button button) {
        int index = itemButtons.indexOf(button);
        if (index != -1) {
            selectedItemIndex = index;
            updateDetailsDisplay();
            
            // Mettre à jour l'apparence des boutons
            for (int i = 0; i < itemButtons.size(); i++) {
                itemButtons.get(i).setBackgroundColor(i == selectedItemIndex ? COLOR_BUTTON_SELECTED : COLOR_BUTTON_NORMAL);
            }
        }
    }
    
    /**
     * Gérer le clic sur une pierre de capture
     * 
     * @param button Bouton cliqué
     */
    private void onCaptureStoneButtonClick(Button button) {
        int index = captureStoneButtons.indexOf(button);
        if (index != -1) {
            selectedCaptureStoneIndex = index;
            updateDetailsDisplay();
            
            // Mettre à jour l'apparence des boutons
            for (int i = 0; i < captureStoneButtons.size(); i++) {
                captureStoneButtons.get(i).setBackgroundColor(i == selectedCaptureStoneIndex ? COLOR_BUTTON_SELECTED : COLOR_BUTTON_NORMAL);
            }
        }
    }
    
    /**
     * Gérer le clic sur une créature
     * 
     * @param button Bouton cliqué
     */
    private void onCreatureButtonClick(Button button) {
        int index = creatureButtons.indexOf(button);
        if (index != -1) {
            selectedCreatureIndex = index;
            updateDetailsDisplay();
            
            // Mettre à jour l'apparence des boutons
            for (int i = 0; i < creatureButtons.size(); i++) {
                creatureButtons.get(i).setBackgroundColor(i == selectedCreatureIndex ? COLOR_BUTTON_SELECTED : COLOR_BUTTON_NORMAL);
            }
        }
    }
    
    /**
     * Gérer le clic sur le bouton Utiliser
     * 
     * @param button Bouton cliqué
     */
    private void onUseButtonClick(Button button) {
        // TODO: Implémenter l'utilisation d'un objet/pierre/créature
    }
    
    /**
     * Gérer le clic sur le bouton Jeter
     * 
     * @param button Bouton cliqué
     */
    private void onDropButtonClick(Button button) {
        // TODO: Implémenter le fait de jeter un objet/pierre/créature
    }
    
    /**
     * Gérer le clic sur le bouton Fermer
     * 
     * @param button Bouton cliqué
     */
    private void onCloseButtonClick(Button button) {
        hide();
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

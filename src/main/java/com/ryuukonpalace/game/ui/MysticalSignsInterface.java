package com.ryuukonpalace.game.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.utils.JsonLoader;
import com.ryuukonpalace.game.utils.ResourceManager;
import com.ryuukonpalace.game.audio.AudioManager;
import com.ryuukonpalace.game.mystical.MysticalSignsSystem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Interface utilisateur pour le système de signes mystiques.
 * Permet au joueur de consulter, apprendre et utiliser les signes mystiques.
 */
public class MysticalSignsInterface {
    // Singleton
    private static MysticalSignsInterface instance;
    
    // Références
    private Renderer renderer;
    private ResourceManager resourceManager;
    private AudioManager audioManager;
    private MysticalSignsSystem mysticalSignsSystem;
    
    // Dimensions de l'écran
    private float screenWidth;
    private float screenHeight;
    
    // Composants UI
    private Panel mainPanel;
    private Panel categoriesPanel;
    private Panel signsPanel;
    private Panel detailPanel;
    private Panel visualizationPanel;
    
    // Onglets de catégories
    private List<Button> categoryButtons;
    private Map<String, String> categoryIdToName;
    
    // Liste des signes
    private List<Button> signButtons;
    
    // Boutons d'action
    private Button useSignButton;
    private Button practiceSignButton;
    private Button closeButton;
    
    // Détails
    private Label nameLabel;
    private Label categoryLabel;
    private Label descriptionLabel;
    private Label effectLabel;
    private Label costLabel;
    private Label cooldownLabel;
    private Label risksLabel;
    private Label loreLabel;
    
    // Visualisation
    private Panel gesturePanel;
    
    // État
    private boolean initialized;
    private boolean visible;
    private String currentCategoryId;
    private int selectedSignIndex;
    private JSONObject selectedSign;
    private List<JSONObject> filteredSigns;
    
    // Données
    private JSONObject mysticalSignsData;
    private JSONArray signCategories;
    private JSONArray mysticalSigns;
    
    // Couleurs
    private static final int COLOR_PANEL_BACKGROUND = 0xCC333333;
    private static final int COLOR_BUTTON_NORMAL = 0xFF4A4A4A;
    private static final int COLOR_BUTTON_HOVER = 0xFF5A5A5A;
    private static final int COLOR_BUTTON_PRESSED = 0xFF3A3A3A;
    private static final int COLOR_BUTTON_DISABLED = 0xFF2A2A2A;
    private static final int COLOR_BUTTON_SELECTED = 0xFF6A6AFF;
    private static final int COLOR_TEXT_NORMAL = 0xFFFFFFFF;
    private static final int COLOR_TEXT_DISABLED = 0xFF888888;
    
    // Couleurs spécifiques aux catégories
    private static final Map<String, Integer> CATEGORY_COLORS = new HashMap<>();
    static {
        CATEGORY_COLORS.put("traditional", 0xFF4A7AAA); // Bleu
        CATEGORY_COLORS.put("forbidden", 0xFF9A4A4A);   // Rouge
        CATEGORY_COLORS.put("spiritual", 0xFF4A9A4A);   // Vert
        CATEGORY_COLORS.put("combat", 0xFFAA7A4A);      // Marron
        CATEGORY_COLORS.put("experimental", 0xFF9A4A9A); // Violet
    }
    
    // Joueur actuel
    private Player player;
    
    /**
     * Constructeur privé (singleton)
     */
    private MysticalSignsInterface() {
        this.renderer = Renderer.getInstance();
        this.resourceManager = ResourceManager.getInstance();
        this.audioManager = AudioManager.getInstance();
        this.mysticalSignsSystem = MysticalSignsSystem.getInstance();
        this.initialized = false;
        this.visible = false;
        this.currentCategoryId = "traditional";
        this.selectedSignIndex = -1;
        this.selectedSign = null;
        this.categoryButtons = new ArrayList<>();
        this.signButtons = new ArrayList<>();
        this.filteredSigns = new ArrayList<>();
        this.categoryIdToName = new HashMap<>();
    }
    
    /**
     * Obtenir l'instance unique de l'interface des signes mystiques
     * 
     * @return Instance de MysticalSignsInterface
     */
    public static MysticalSignsInterface getInstance() {
        if (instance == null) {
            instance = new MysticalSignsInterface();
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
        
        // Charger les données des signes mystiques
        loadMysticalSignsData();
        
        // Créer les composants UI
        createPanels();
        createCategoryButtons();
        createSignButtons();
        createActionButtons();
        createLabels();
        createVisualizationPanel();
        
        initialized = true;
    }
    
    /**
     * Charger les données des signes mystiques
     */
    private void loadMysticalSignsData() {
        try {
            // Charger les données des signes mystiques
            mysticalSignsData = JsonLoader.loadJsonObject("src/main/resources/data/mystical_signs.json");
            
            // Récupérer les catégories
            signCategories = mysticalSignsData.getJSONArray("categories");
            
            // Récupérer les signes
            mysticalSigns = mysticalSignsData.getJSONArray("signs");
            
            // Remplir la map des noms de catégories
            for (int i = 0; i < signCategories.length(); i++) {
                JSONObject category = signCategories.getJSONObject(i);
                String id = category.getString("id");
                String name = category.getString("name");
                categoryIdToName.put(id, name);
            }
            
            // Afficher le nombre de signes chargés
            System.out.println("Signes mystiques chargés: " + mysticalSigns.length());
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des données des signes mystiques: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Créer les panneaux
     */
    private void createPanels() {
        // Panneau principal (fond)
        float mainPanelWidth = screenWidth * 0.8f;
        float mainPanelHeight = screenHeight * 0.85f;
        float mainPanelX = (screenWidth - mainPanelWidth) / 2;
        float mainPanelY = (screenHeight - mainPanelHeight) / 2;
        
        mainPanel = new Panel(mainPanelX, mainPanelY, mainPanelWidth, mainPanelHeight, COLOR_PANEL_BACKGROUND);
        mainPanel.setHasBorder(true);
        mainPanel.setBorderColor(0xFF555555);
        mainPanel.setBorderThickness(2);
        mainPanel.setVisible(false);
        
        // Panneau des catégories (gauche)
        float categoriesPanelWidth = mainPanelWidth * 0.2f;
        float categoriesPanelHeight = mainPanelHeight - 60;
        float categoriesPanelX = 20;
        float categoriesPanelY = 50;
        
        categoriesPanel = new Panel(mainPanelX + categoriesPanelX, mainPanelY + categoriesPanelY, 
                                   categoriesPanelWidth, categoriesPanelHeight, 0xAA222222);
        categoriesPanel.setHasBorder(true);
        categoriesPanel.setBorderColor(0xFF444444);
        categoriesPanel.setBorderThickness(1);
        
        // Panneau des signes (centre gauche)
        float signsPanelWidth = mainPanelWidth * 0.25f;
        float signsPanelHeight = mainPanelHeight - 60;
        float signsPanelX = categoriesPanelX + categoriesPanelWidth + 20;
        float signsPanelY = 50;
        
        signsPanel = new Panel(mainPanelX + signsPanelX, mainPanelY + signsPanelY, 
                              signsPanelWidth, signsPanelHeight, 0xAA222222);
        signsPanel.setHasBorder(true);
        signsPanel.setBorderColor(0xFF444444);
        signsPanel.setBorderThickness(1);
        
        // Panneau de détails (centre droit)
        float detailPanelWidth = mainPanelWidth * 0.3f;
        float detailPanelHeight = mainPanelHeight - 60;
        float detailPanelX = signsPanelX + signsPanelWidth + 20;
        float detailPanelY = 50;
        
        detailPanel = new Panel(mainPanelX + detailPanelX, mainPanelY + detailPanelY, 
                               detailPanelWidth, detailPanelHeight, 0xAA222222);
        detailPanel.setHasBorder(true);
        detailPanel.setBorderColor(0xFF444444);
        detailPanel.setBorderThickness(1);
        
        // Panneau de visualisation (droite)
        float visualizationPanelWidth = mainPanelWidth * 0.2f;
        float visualizationPanelHeight = mainPanelHeight - 60;
        float visualizationPanelX = detailPanelX + detailPanelWidth + 20;
        float visualizationPanelY = 50;
        
        visualizationPanel = new Panel(mainPanelX + visualizationPanelX, mainPanelY + visualizationPanelY, 
                                      visualizationPanelWidth, visualizationPanelHeight, 0xAA222222);
        visualizationPanel.setHasBorder(true);
        visualizationPanel.setBorderColor(0xFF444444);
        visualizationPanel.setBorderThickness(1);
    }
    
    /**
     * Créer les boutons de catégorie
     */
    private void createCategoryButtons() {
        float buttonWidth = categoriesPanel.getWidth() - 20;
        float buttonHeight = 40;
        float buttonX = 10;
        float buttonY = 10;
        float buttonSpacing = 10;
        
        for (int i = 0; i < signCategories.length(); i++) {
            JSONObject category = signCategories.getJSONObject(i);
            String categoryId = category.getString("id");
            String categoryName = category.getString("name");
            
            int categoryColor = CATEGORY_COLORS.getOrDefault(categoryId, COLOR_BUTTON_NORMAL);
            
            final Button categoryButton = new Button(
                categoriesPanel.getX() + buttonX,
                categoriesPanel.getY() + buttonY + (buttonHeight + buttonSpacing) * i,
                buttonWidth,
                buttonHeight,
                categoryName,
                categoryColor, // normalColor
                categoryColor, // hoverColor (même couleur pour l'instant)
                categoryColor, // pressedColor (même couleur pour l'instant)
                COLOR_BUTTON_DISABLED, // disabledColor
                COLOR_TEXT_NORMAL, // textColor
                null // action (définie ci-dessous)
            );
            
            final int index = i;
            categoryButton.setAction(button -> onCategoryButtonClick(index));
            
            // Stocker l'ID de la catégorie comme tag (utilisation d'une propriété personnalisée)
            categoryButtons.add(categoryButton);
        }
    }
    
    /**
     * Créer les boutons de signes
     */
    private void createSignButtons() {
        float buttonWidth = signsPanel.getWidth() - 20;
        float buttonHeight = 40;
        float buttonX = 10;
        float buttonY = 10;
        float buttonSpacing = 10;
        
        // Créer des boutons vides qui seront remplis dynamiquement
        for (int i = 0; i < 15; i++) {
            final Button signButton = new Button(
                signsPanel.getX() + buttonX,
                signsPanel.getY() + buttonY + (buttonHeight + buttonSpacing) * i,
                buttonWidth,
                buttonHeight,
                "",
                COLOR_BUTTON_NORMAL, // normalColor
                COLOR_BUTTON_HOVER, // hoverColor
                COLOR_BUTTON_PRESSED, // pressedColor
                COLOR_BUTTON_DISABLED, // disabledColor
                COLOR_TEXT_NORMAL, // textColor
                null // action (définie ci-dessous)
            );
            
            final int index = i;
            signButton.setAction(button -> onSignButtonClick(index));
            signButton.setVisible(false);
            
            signButtons.add(signButton);
        }
    }
    
    /**
     * Créer les boutons d'action
     */
    private void createActionButtons() {
        float buttonWidth = 120;
        float buttonHeight = 40;
        float buttonY = mainPanel.getHeight() - 50;
        
        // Bouton Utiliser
        useSignButton = new Button(
            mainPanel.getX() + mainPanel.getWidth() / 2 - buttonWidth - 10,
            mainPanel.getY() + buttonY,
            buttonWidth,
            buttonHeight,
            "Utiliser",
            COLOR_BUTTON_NORMAL, // normalColor
            COLOR_BUTTON_HOVER, // hoverColor
            COLOR_BUTTON_PRESSED, // pressedColor
            COLOR_BUTTON_DISABLED, // disabledColor
            COLOR_TEXT_NORMAL, // textColor
            button -> onUseSignButtonClick()
        );
        useSignButton.setEnabled(false);
        
        // Bouton Pratiquer
        practiceSignButton = new Button(
            mainPanel.getX() + mainPanel.getWidth() / 2 + 10,
            mainPanel.getY() + buttonY,
            buttonWidth,
            buttonHeight,
            "Pratiquer",
            COLOR_BUTTON_NORMAL, // normalColor
            COLOR_BUTTON_HOVER, // hoverColor
            COLOR_BUTTON_PRESSED, // pressedColor
            COLOR_BUTTON_DISABLED, // disabledColor
            COLOR_TEXT_NORMAL, // textColor
            button -> onPracticeSignButtonClick()
        );
        practiceSignButton.setEnabled(false);
        
        // Bouton Fermer
        closeButton = new Button(
            mainPanel.getX() + mainPanel.getWidth() - 100,
            mainPanel.getY() + 10,
            80,
            30,
            "Fermer",
            COLOR_BUTTON_NORMAL, // normalColor
            COLOR_BUTTON_HOVER, // hoverColor
            COLOR_BUTTON_PRESSED, // pressedColor
            COLOR_BUTTON_DISABLED, // disabledColor
            COLOR_TEXT_NORMAL, // textColor
            button -> hide()
        );
    }
    
    /**
     * Créer les étiquettes
     */
    private void createLabels() {
        float labelX = 10;
        float labelY = 10;
        float labelWidth = detailPanel.getWidth() - 20;
        float labelHeight = 25;
        float labelSpacing = 5;
        
        // Titre du signe
        nameLabel = new Label(
            detailPanel.getX() + labelX,
            detailPanel.getY() + labelY,
            labelWidth,
            labelHeight,
            "",
            COLOR_TEXT_NORMAL,
            18 // Taille du texte plus grande pour le titre
        );
        
        // Catégorie
        labelY += labelHeight + labelSpacing;
        categoryLabel = new Label(
            detailPanel.getX() + labelX,
            detailPanel.getY() + labelY,
            labelWidth,
            labelHeight,
            "",
            COLOR_TEXT_NORMAL,
            14
        );
        
        // Description
        labelY += labelHeight + labelSpacing * 2;
        descriptionLabel = new Label(
            detailPanel.getX() + labelX,
            detailPanel.getY() + labelY,
            labelWidth,
            labelHeight * 2,
            "",
            COLOR_TEXT_NORMAL,
            14
        );
        
        // Effet
        labelY += labelHeight * 2 + labelSpacing * 2;
        effectLabel = new Label(
            detailPanel.getX() + labelX,
            detailPanel.getY() + labelY,
            labelWidth,
            labelHeight * 2,
            "",
            COLOR_TEXT_NORMAL,
            14
        );
        
        // Coût
        labelY += labelHeight * 2 + labelSpacing;
        costLabel = new Label(
            detailPanel.getX() + labelX,
            detailPanel.getY() + labelY,
            labelWidth,
            labelHeight,
            "",
            COLOR_TEXT_NORMAL,
            14
        );
        
        // Temps de recharge
        labelY += labelHeight + labelSpacing;
        cooldownLabel = new Label(
            detailPanel.getX() + labelX,
            detailPanel.getY() + labelY,
            labelWidth,
            labelHeight,
            "",
            COLOR_TEXT_NORMAL,
            14
        );
        
        // Risques
        labelY += labelHeight + labelSpacing * 2;
        risksLabel = new Label(
            detailPanel.getX() + labelX,
            detailPanel.getY() + labelY,
            labelWidth,
            labelHeight * 2,
            "",
            COLOR_TEXT_NORMAL,
            14
        );
        
        // Lore
        labelY += labelHeight * 2 + labelSpacing * 2;
        loreLabel = new Label(
            detailPanel.getX() + labelX,
            detailPanel.getY() + labelY,
            labelWidth,
            labelHeight * 3,
            "",
            COLOR_TEXT_NORMAL,
            14
        );
    }
    
    /**
     * Créer le panneau de visualisation
     */
    private void createVisualizationPanel() {
        float panelWidth = visualizationPanel.getWidth() - 20;
        float panelHeight = visualizationPanel.getWidth() - 20; // Carré
        float panelX = 10;
        float panelY = 10;
        
        gesturePanel = new Panel(
            visualizationPanel.getX() + panelX,
            visualizationPanel.getY() + panelY,
            panelWidth,
            panelHeight,
            0xFF111111
        );
        gesturePanel.setHasBorder(true);
        gesturePanel.setBorderColor(0xFF555555);
        gesturePanel.setBorderThickness(1);
        
        // Ajouter un titre au panneau de visualisation
        Label visualizationTitle = new Label(
            visualizationPanel.getX() + 10,
            visualizationPanel.getY() + panelHeight + 20,
            panelWidth,
            30,
            "Geste du Signe",
            COLOR_TEXT_NORMAL,
            16
        );
        
        // Ajouter le titre au panneau de visualisation
        visualizationPanel.addChild(visualizationTitle);
        
        // Ajouter le panneau de geste au panneau de visualisation
        visualizationPanel.addChild(gesturePanel);
    }
    
    /**
     * Mettre à jour l'interface
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        if (!visible || !initialized) {
            return;
        }
        
        // Mettre à jour les composants UI
        for (Button button : categoryButtons) {
            button.update(deltaTime);
        }
        
        for (Button button : signButtons) {
            if (button.isVisible()) {
                button.update(deltaTime);
            }
        }
        
        useSignButton.update(deltaTime);
        practiceSignButton.update(deltaTime);
        closeButton.update(deltaTime);
    }
    
    /**
     * Dessiner l'interface
     */
    public void render() {
        if (!visible || !initialized) {
            return;
        }
        
        // Dessiner les panneaux
        mainPanel.render();
        categoriesPanel.render();
        signsPanel.render();
        detailPanel.render();
        visualizationPanel.render();
        gesturePanel.render();
        
        // Dessiner les boutons de catégorie
        for (Button button : categoryButtons) {
            button.render();
        }
        
        // Dessiner les boutons de signes
        for (Button button : signButtons) {
            if (button.isVisible()) {
                button.render();
            }
        }
        
        // Dessiner les étiquettes
        nameLabel.render();
        categoryLabel.render();
        descriptionLabel.render();
        effectLabel.render();
        costLabel.render();
        cooldownLabel.render();
        risksLabel.render();
        loreLabel.render();
        
        // Dessiner les boutons d'action
        useSignButton.render();
        practiceSignButton.render();
        closeButton.render();
    }
    
    /**
     * Afficher l'interface
     * 
     * @param player Joueur
     */
    public void show(Player player) {
        this.player = player;
        if (!initialized) {
            init();
        }
        
        mainPanel.setVisible(true);
        visible = true;
        
        // S'assurer que le système de signes mystiques est initialisé pour ce joueur
        mysticalSignsSystem.initPlayerData(player);
        
        // Charger les signes de la catégorie par défaut
        filterSignsByCategory(currentCategoryId, player);
        updateCategorySelection();
        
        // Jouer un son d'ouverture
        audioManager.playSound("ui_open");
    }
    
    /**
     * Masquer l'interface
     */
    public void hide() {
        mainPanel.setVisible(false);
        visible = false;
        
        // Réinitialiser la sélection
        selectedSignIndex = -1;
        selectedSign = null;
        
        // Désactiver les boutons d'action
        useSignButton.setEnabled(false);
        practiceSignButton.setEnabled(false);
        
        // Effacer les détails
        clearSignDetails();
        
        // Jouer un son de fermeture
        audioManager.playSound("ui_close");
    }
    
    /**
     * Gérer les entrées de l'utilisateur
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param mousePressed true si le bouton de la souris est appuyé, false sinon
     */
    public void handleInput(float mouseX, float mouseY, boolean mousePressed) {
        if (!visible || !initialized) {
            return;
        }
        
        // Gérer les clics sur les boutons
        for (Button button : categoryButtons) {
            button.onHover(mouseX, mouseY);
            if (mousePressed) {
                button.onClick(mouseX, mouseY);
            }
        }
        
        for (Button button : signButtons) {
            if (button.isVisible()) {
                button.onHover(mouseX, mouseY);
                if (mousePressed) {
                    button.onClick(mouseX, mouseY);
                }
            }
        }
        
        useSignButton.onHover(mouseX, mouseY);
        practiceSignButton.onHover(mouseX, mouseY);
        closeButton.onHover(mouseX, mouseY);
        
        if (mousePressed) {
            useSignButton.onClick(mouseX, mouseY);
            practiceSignButton.onClick(mouseX, mouseY);
            closeButton.onClick(mouseX, mouseY);
        }
    }
    
    /**
     * Filtrer les signes par catégorie
     * 
     * @param categoryId ID de la catégorie
     * @param player Joueur
     */
    private void filterSignsByCategory(String categoryId, Player player) {
        currentCategoryId = categoryId;
        filteredSigns.clear();
        
        // Obtenir les signes connus par le joueur dans cette catégorie
        List<JSONObject> knownSigns = mysticalSignsSystem.getKnownSignsByCategory(player, categoryId);
        filteredSigns.addAll(knownSigns);
        
        // Si aucun signe n'est connu, afficher un message
        if (filteredSigns.isEmpty()) {
            nameLabel.setText("Aucun signe connu");
            nameLabel.setTextColor(COLOR_TEXT_DISABLED);
        }
        
        updateSignButtons();
    }
    
    /**
     * Mettre à jour les boutons de signes
     */
    private void updateSignButtons() {
        // Masquer tous les boutons
        for (Button button : signButtons) {
            button.setVisible(false);
        }
        
        // Afficher les boutons pour les signes filtrés
        for (int i = 0; i < filteredSigns.size() && i < signButtons.size(); i++) {
            JSONObject sign = filteredSigns.get(i);
            Button button = signButtons.get(i);
            
            button.setText(sign.getString("name"));
            button.setVisible(true);
            button.setBackgroundColor(i == selectedSignIndex ? COLOR_BUTTON_SELECTED : COLOR_BUTTON_NORMAL);
        }
    }
    
    /**
     * Gérer le clic sur un bouton de catégorie
     * 
     * @param index Index du bouton
     */
    private void onCategoryButtonClick(int index) {
        JSONObject category = signCategories.getJSONObject(index);
        String categoryId = category.getString("id");
        
        if (!categoryId.equals(currentCategoryId)) {
            currentCategoryId = categoryId;
            selectedSignIndex = -1;
            selectedSign = null;
            
            // Filtrer les signes pour le joueur actuel
            filterSignsByCategory(currentCategoryId, player);
            updateCategorySelection();
            clearSignDetails();
            
            // Jouer un son de clic
            audioManager.playSound("ui_tab");
        }
    }
    
    /**
     * Gérer le clic sur un bouton de signe
     * 
     * @param index Index du bouton
     */
    private void onSignButtonClick(int index) {
        if (index < filteredSigns.size()) {
            selectedSignIndex = index;
            selectedSign = filteredSigns.get(index);
            
            updateSignButtons();
            updateSignDetails();
            
            // Jouer un son de clic
            audioManager.playSound("ui_select");
        }
    }
    
    /**
     * Gérer le clic sur le bouton Utiliser
     */
    private void onUseSignButtonClick() {
        if (selectedSign != null && player != null) {
            // Récupérer l'ID du signe sélectionné
            String signId = selectedSign.getString("id");
            
            // Utiliser le système de signes mystiques pour utiliser le signe
            boolean success = mysticalSignsSystem.useSign(player, signId);
            
            if (success) {
                // Jouer un son d'action réussie
                audioManager.playSound("ui_action");
                
                // Afficher un message de succès
                System.out.println("Signe utilisé avec succès: " + selectedSign.getString("name"));
                
                // Mettre à jour l'affichage des détails du signe (pour refléter le temps de recharge)
                displaySignDetails(selectedSign);
            } else {
                // Jouer un son d'échec
                audioManager.playSound("ui_cancel");
                
                // Afficher un message d'échec
                System.out.println("Impossible d'utiliser le signe: " + selectedSign.getString("name"));
                
                // Vérifier pourquoi l'utilisation a échoué
                if (mysticalSignsSystem.isSignOnCooldown(player, signId)) {
                    int remainingCooldown = mysticalSignsSystem.getRemainingCooldown(player, signId);
                    System.out.println("Le signe est en recharge pour encore " + remainingCooldown + " secondes.");
                } else if (player.getEnergy() < parseEnergyCost(selectedSign.getString("cost"))) {
                    System.out.println("Énergie insuffisante pour utiliser ce signe.");
                }
            }
        }
    }
    
    /**
     * Gérer le clic sur le bouton Pratiquer
     */
    private void onPracticeSignButtonClick() {
        if (selectedSign != null && player != null) {
            // Récupérer l'ID du signe sélectionné
            String signId = selectedSign.getString("id");
            
            // Utiliser le système de signes mystiques pour pratiquer le signe
            boolean success = mysticalSignsSystem.practiceSign(player, signId);
            
            if (success) {
                // Jouer un son d'action réussie
                audioManager.playSound("ui_action");
                
                // Afficher un message de succès
                int masteryLevel = mysticalSignsSystem.getSignMasteryLevel(player, signId);
                System.out.println("Pratique du signe réussie: " + selectedSign.getString("name") + " (Niveau de maîtrise: " + masteryLevel + ")");
                
                // Mettre à jour l'affichage des détails du signe
                displaySignDetails(selectedSign);
            } else {
                // Jouer un son d'échec
                audioManager.playSound("ui_cancel");
                
                // Afficher un message d'échec
                System.out.println("Impossible de pratiquer le signe: " + selectedSign.getString("name"));
                
                // Vérifier pourquoi la pratique a échoué
                if (player.getEnergy() < parseEnergyCost(selectedSign.getString("cost")) / 2) {
                    System.out.println("Énergie insuffisante pour pratiquer ce signe.");
                }
            }
        }
    }
    
    /**
     * Afficher les détails du signe
     * 
     * @param sign Signe à afficher
     */
    private void displaySignDetails(JSONObject sign) {
        // Mettre à jour les étiquettes
        nameLabel.setText(sign.getString("name"));
        
        String categoryId = sign.getString("category");
        String categoryName = categoryIdToName.getOrDefault(categoryId, "Inconnu");
        categoryLabel.setText("Catégorie: " + categoryName);
        
        descriptionLabel.setText("Description: " + sign.getString("description"));
        effectLabel.setText("Effet: " + sign.getString("effect"));
        costLabel.setText("Coût: " + sign.getString("cost"));
        
        if (sign.has("cooldown")) {
            cooldownLabel.setText("Temps de recharge: " + sign.getString("cooldown"));
        } else if (sign.has("duration")) {
            cooldownLabel.setText("Durée: " + sign.getString("duration"));
        } else {
            cooldownLabel.setText("");
        }
        
        risksLabel.setText("Risques: " + sign.getString("risks"));
        loreLabel.setText("Lore: " + sign.getString("lore"));
        
        // Activer les boutons d'action
        useSignButton.setEnabled(true);
        practiceSignButton.setEnabled(true);
        
        // Mettre à jour la visualisation du geste
        updateGestureVisualization();
    }
    
    /**
     * Effacer les détails du signe
     */
    private void clearSignDetails() {
        nameLabel.setText("");
        categoryLabel.setText("");
        descriptionLabel.setText("");
        effectLabel.setText("");
        costLabel.setText("");
        cooldownLabel.setText("");
        risksLabel.setText("");
        loreLabel.setText("");
        
        useSignButton.setEnabled(false);
        practiceSignButton.setEnabled(false);
    }
    
    /**
     * Mettre à jour la visualisation du geste
     */
    private void updateGestureVisualization() {
        if (selectedSign == null || gesturePanel == null) {
            return;
        }
        
        // Dans une implémentation future, nous pourrions dessiner le geste du signe
        // Pour l'instant, nous affichons simplement un message
        
        // Récupérer l'ID du signe
        String signId = selectedSign.getString("id");
        String category = selectedSign.getString("category");
        
        // Vérifier si nous avons une texture pour ce signe
        String texturePath = "signs/" + category + "/" + signId + ".png";
        int textureId = resourceManager.getTextureId(texturePath);
        
        if (textureId != -1) {
            // Si nous avons une texture, l'afficher dans le panneau de geste
            // TODO: Afficher la texture dans le panneau de geste
        } else {
            // Sinon, générer une visualisation procédurale simple
            // Basée sur l'ID et la catégorie du signe
            // TODO: Générer une visualisation procédurale
        }
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
    
    /**
     * Définir le joueur dont les signes mystiques sont affichés
     * 
     * @param player Le joueur
     */
    public void setPlayer(Player player) {
        if (player != null) {
            this.player = player;
            
            // Filtrer les signes par la catégorie actuelle
            filterSignsByCategory(currentCategoryId, player);
            
            // Mettre à jour les boutons de signes
            updateSignButtons();
            
            // Réinitialiser la sélection
            selectedSignIndex = -1;
            selectedSign = null;
            
            // Effacer les détails
            clearSignDetails();
            
            // Désactiver les boutons d'action
            useSignButton.setEnabled(false);
            practiceSignButton.setEnabled(false);
        }
    }
    
    /**
     * Définir la visibilité de l'interface
     * 
     * @param visible true pour afficher l'interface, false pour la masquer
     */
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            if (visible) {
                // Si on affiche l'interface, on s'assure que les panneaux sont correctement configurés
                updateCategorySelection();
            } else {
                // Si on masque l'interface, on réinitialise les sélections
                selectedSignIndex = -1;
                selectedSign = null;
                clearSignDetails();
            }
            this.visible = visible;
            
            // Rendre visible ou masquer le panneau principal
            if (mainPanel != null) {
                mainPanel.setVisible(visible);
            }
        }
    }
    
    /**
     * Mettre à jour la sélection de catégorie
     */
    private void updateCategorySelection() {
        for (Button button : categoryButtons) {
            String categoryId = (String) button.getTag();
            if (categoryId.equals(currentCategoryId)) {
                button.setBackgroundColor(CATEGORY_COLORS.getOrDefault(categoryId, COLOR_BUTTON_SELECTED));
            } else {
                button.setBackgroundColor(COLOR_BUTTON_NORMAL);
            }
        }
    }
    
    /**
     * Mettre à jour les détails du signe
     */
    private void updateSignDetails() {
        if (selectedSign == null) {
            clearSignDetails();
            return;
        }
        
        // Mettre à jour les étiquettes
        nameLabel.setText(selectedSign.getString("name"));
        
        String categoryId = selectedSign.getString("category");
        String categoryName = categoryIdToName.getOrDefault(categoryId, "Inconnu");
        categoryLabel.setText("Catégorie: " + categoryName);
        
        descriptionLabel.setText("Description: " + selectedSign.getString("description"));
        effectLabel.setText("Effet: " + selectedSign.getString("effect"));
        costLabel.setText("Coût: " + selectedSign.getString("cost"));
        
        if (selectedSign.has("cooldown")) {
            cooldownLabel.setText("Temps de recharge: " + selectedSign.getString("cooldown"));
        } else if (selectedSign.has("duration")) {
            cooldownLabel.setText("Durée: " + selectedSign.getString("duration"));
        } else {
            cooldownLabel.setText("");
        }
        
        risksLabel.setText("Risques: " + selectedSign.getString("risks"));
        loreLabel.setText("Lore: " + selectedSign.getString("lore"));
        
        // Activer les boutons d'action
        useSignButton.setEnabled(true);
        practiceSignButton.setEnabled(true);
        
        // Mettre à jour la visualisation du geste
        updateGestureVisualization();
    }
    
    /**
     * Analyser le coût en énergie d'un signe à partir de sa description
     * 
     * @param costStr Description du coût (ex: "30 points d'énergie")
     * @return Coût en énergie (valeur numérique)
     */
    private int parseEnergyCost(String costStr) {
        try {
            // Extraire le nombre du début de la chaîne
            String[] parts = costStr.split(" ");
            if (parts.length > 0) {
                return Integer.parseInt(parts[0]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur lors de l'analyse du coût: " + costStr);
        }
        return 0; // Valeur par défaut
    }
}

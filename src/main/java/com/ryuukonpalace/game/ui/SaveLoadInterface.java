package com.ryuukonpalace.game.ui;

import java.util.List;

import com.ryuukonpalace.game.core.GameState;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.save.SaveManager;
import com.ryuukonpalace.game.save.SaveMetadata;
import com.ryuukonpalace.game.utils.ResourceManager;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Interface de sauvegarde et de chargement pour Ryuukon Palace.
 * Permet au joueur de sauvegarder, charger et gérer ses sauvegardes.
 */
public class SaveLoadInterface extends UIComponent {
    
    // États de l'interface
    public enum State {
        SAVE,       // Mode sauvegarde
        LOAD,       // Mode chargement
        CONFIRM,    // Confirmation (sauvegarde/chargement/suppression)
        SETTINGS    // Paramètres de sauvegarde
    }
    
    // Gestionnaire de sauvegarde
    private SaveManager saveManager;
    
    // État actuel de l'interface
    private State state;
    
    // Slot sélectionné
    private int selectedSlot;
    
    // Message de confirmation
    private String confirmMessage;
    
    // Action à confirmer
    private Runnable confirmAction;
    
    // Liste des métadonnées de sauvegarde
    private List<SaveMetadata> saveMetadataList;
    
    // Gestionnaire de rendu
    private Renderer renderer;
    
    // Gestionnaire d'entrées
    private InputManager inputManager;
    
    // Gestionnaire de ressources
    private ResourceManager resourceManager;
    
    // IDs des textures
    private int backgroundTextureId;
    private int panelBackgroundId;
    private int buttonNormalId;
    private int buttonHoverId;
    private int buttonPressedId;
    private int buttonDisabledId;
    private int slotBackgroundId;
    private int slotSelectedId;
    
    // Couleurs
    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_TEXT_HIGHLIGHT = 0xFFFFCC00;
    private static final int COLOR_TEXT_DISABLED = 0xFF888888;
    private static final int COLOR_PANEL_BACKGROUND = 0xCC000000;
    
    // Dimensions et positions
    private float panelWidth;
    private float panelHeight;
    private float panelX;
    private float panelY;
    private float slotHeight;
    private float slotSpacing;
    
    // Boutons
    private Button saveButton;
    private Button loadButton;
    private Button deleteButton;
    private Button settingsButton;
    private Button confirmButton;
    private Button cancelButton;
    private Button backButton;
    private Button autoSaveToggleButton;
    
    // Temps du dernier clic
    private long lastClickTime;
    private int lastClickSlot;
    
    /**
     * Constructeur
     */
    public SaveLoadInterface() {
        super(0, 0, 800, 600); // Dimensions par défaut, à ajuster dans calculateLayout()
        
        // Obtenir les instances des gestionnaires
        this.saveManager = SaveManager.getInstance();
        this.renderer = Renderer.getInstance();
        this.inputManager = InputManager.getInstance();
        this.resourceManager = ResourceManager.getInstance();
        
        // Initialiser l'état
        this.state = State.SAVE;
        this.selectedSlot = 1; // Commencer au slot 1 (slot 0 réservé pour l'auto-sauvegarde)
        this.confirmMessage = "";
        this.confirmAction = null;
        
        // Charger les textures
        loadTextures();
        
        // Calculer les dimensions et positions
        calculateLayout();
        
        // Créer les boutons
        createButtons();
        
        // Charger les métadonnées de sauvegarde
        refreshSaveMetadata();
    }
    
    /**
     * Charger les textures nécessaires
     */
    private void loadTextures() {
        backgroundTextureId = resourceManager.loadTexture("src/main/resources/images/ui/menu_background.png", "save_background");
        panelBackgroundId = resourceManager.loadTexture("src/main/resources/images/ui/panel.png", "save_panel");
        buttonNormalId = resourceManager.loadTexture("src/main/resources/images/ui/button_normal.png", "save_button_normal");
        buttonHoverId = resourceManager.loadTexture("src/main/resources/images/ui/button_hover.png", "save_button_hover");
        buttonPressedId = resourceManager.loadTexture("src/main/resources/images/ui/button_pressed.png", "save_button_pressed");
        buttonDisabledId = resourceManager.loadTexture("src/main/resources/images/ui/button_disabled.png", "save_button_disabled");
        slotBackgroundId = resourceManager.loadTexture("src/main/resources/images/ui/slot_normal.png", "save_slot_normal");
        slotSelectedId = resourceManager.loadTexture("src/main/resources/images/ui/slot_selected.png", "save_slot_selected");
    }
    
    /**
     * Calculer les dimensions et positions des éléments
     */
    private void calculateLayout() {
        float screenWidth = renderer.getScreenWidth();
        float screenHeight = renderer.getScreenHeight();
        
        panelWidth = screenWidth * 0.8f;
        panelHeight = screenHeight * 0.8f;
        panelX = (screenWidth - panelWidth) / 2;
        panelY = (screenHeight - panelHeight) / 2;
        
        slotHeight = 80;
        slotSpacing = 10;
    }
    
    /**
     * Créer les boutons de l'interface
     */
    private void createButtons() {
        // Couleur du texte par défaut
        int textColor = 0xFFFFFFFF; // Blanc
        
        // Bouton de sauvegarde
        saveButton = new Button(
            panelX + 20, 
            panelY + 20, 
            150, 
            40, 
            "Sauvegarder",
            "src/main/resources/images/ui/button_normal.png",
            "src/main/resources/images/ui/button_hover.png",
            "src/main/resources/images/ui/button_pressed.png",
            "src/main/resources/images/ui/button_disabled.png",
            button -> {
                setState(State.SAVE);
            }
        );
        
        // Bouton de chargement
        loadButton = new Button(
            panelX + 180, 
            panelY + 20, 
            150, 
            40, 
            "Charger",
            "src/main/resources/images/ui/button_normal.png",
            "src/main/resources/images/ui/button_hover.png",
            "src/main/resources/images/ui/button_pressed.png",
            "src/main/resources/images/ui/button_disabled.png",
            button -> {
                setState(State.LOAD);
            }
        );
        
        // Bouton de suppression
        deleteButton = new Button(
            panelX + 340, 
            panelY + 20, 
            150, 
            40, 
            "Supprimer",
            "src/main/resources/images/ui/button_normal.png",
            "src/main/resources/images/ui/button_hover.png",
            "src/main/resources/images/ui/button_pressed.png",
            "src/main/resources/images/ui/button_disabled.png",
            button -> {
                confirmMessage = "Êtes-vous sûr de vouloir supprimer cette sauvegarde ?";
                confirmAction = () -> {
                    saveManager.deleteSave(selectedSlot);
                    refreshSaveMetadata();
                };
                setState(State.CONFIRM);
            }
        );
        
        // Bouton des paramètres
        settingsButton = new Button(
            panelX + panelWidth - 170, 
            panelY + 20, 
            150, 
            40, 
            "Paramètres",
            "src/main/resources/images/ui/button_normal.png",
            "src/main/resources/images/ui/button_hover.png",
            "src/main/resources/images/ui/button_pressed.png",
            "src/main/resources/images/ui/button_disabled.png",
            button -> {
                setState(State.SETTINGS);
            }
        );
        
        // Bouton de confirmation
        confirmButton = new Button(
            panelX + panelWidth / 2 - 160, 
            panelY + panelHeight / 2 + 20, 
            150, 
            40, 
            "Confirmer",
            "src/main/resources/images/ui/button_normal.png",
            "src/main/resources/images/ui/button_hover.png",
            "src/main/resources/images/ui/button_pressed.png",
            "src/main/resources/images/ui/button_disabled.png",
            button -> {
                if (confirmAction != null) {
                    confirmAction.run();
                }
                setState(state == State.CONFIRM ? State.SAVE : state);
            }
        );
        
        // Bouton d'annulation
        cancelButton = new Button(
            panelX + panelWidth / 2 + 10, 
            panelY + panelHeight / 2 + 20, 
            150, 
            40, 
            "Annuler",
            "src/main/resources/images/ui/button_normal.png",
            "src/main/resources/images/ui/button_hover.png",
            "src/main/resources/images/ui/button_pressed.png",
            "src/main/resources/images/ui/button_disabled.png",
            button -> {
                setState(state == State.CONFIRM ? State.SAVE : state);
            }
        );
        
        // Bouton de retour
        backButton = new Button(
            panelX + 20, 
            panelY + panelHeight - 60, 
            150, 
            40, 
            "Retour",
            "src/main/resources/images/ui/button_normal.png",
            "src/main/resources/images/ui/button_hover.png",
            "src/main/resources/images/ui/button_pressed.png",
            "src/main/resources/images/ui/button_disabled.png",
            button -> {
                if (state == State.SETTINGS) {
                    setState(State.SAVE);
                }
            }
        );
        
        // Bouton d'auto-sauvegarde
        autoSaveToggleButton = new Button(
            0, 0, 150, 40, 
            saveManager.isAutoSaveEnabled() ? "Activé" : "Désactivé",
            "src/main/resources/images/ui/button_normal.png",
            "src/main/resources/images/ui/button_hover.png",
            "src/main/resources/images/ui/button_pressed.png",
            "src/main/resources/images/ui/button_disabled.png",
            button -> {
                saveManager.setAutoSaveEnabled(!saveManager.isAutoSaveEnabled());
                button.setText(saveManager.isAutoSaveEnabled() ? "Activé" : "Désactivé");
            }
        );
    }
    
    /**
     * Rafraîchir la liste des métadonnées de sauvegarde
     */
    private void refreshSaveMetadata() {
        saveMetadataList = saveManager.getAllSaveMetadata();
    }
    
    @Override
    public void update(float deltaTime) {
        if (!isVisible()) {
            return;
        }
        
        // Mettre à jour les boutons
        updateButtons();
        
        // Gérer les entrées
        handleInput();
    }
    
    /**
     * Mettre à jour les boutons en fonction de l'état
     */
    private void updateButtons() {
        // Mettre à jour les boutons en fonction de l'état
        switch (state) {
            case SAVE:
                saveButton.update(0);
                loadButton.update(0);
                deleteButton.update(0);
                settingsButton.update(0);
                break;
                
            case LOAD:
                saveButton.update(0);
                loadButton.update(0);
                deleteButton.update(0);
                settingsButton.update(0);
                break;
                
            case CONFIRM:
                confirmButton.update(0);
                cancelButton.update(0);
                break;
                
            case SETTINGS:
                backButton.update(0);
                autoSaveToggleButton.update(0);
                break;
        }
    }
    
    /**
     * Gérer les entrées utilisateur
     */
    private void handleInput() {
        // Gérer les entrées spécifiques à l'état
        switch (state) {
            case SAVE:
            case LOAD:
                // Sélection des slots avec les touches fléchées
                if (inputManager.isKeyJustPressed(GLFW_KEY_UP)) {
                    selectedSlot = Math.max(1, selectedSlot - 1);
                } else if (inputManager.isKeyJustPressed(GLFW_KEY_DOWN)) {
                    selectedSlot = Math.min(SaveManager.MAX_SAVE_SLOTS - 1, selectedSlot + 1);
                }
                
                // Action sur le slot sélectionné
                if (inputManager.isKeyJustPressed(GLFW_KEY_ENTER)) {
                    if (state == State.SAVE) {
                        confirmMessage = "Êtes-vous sûr de vouloir sauvegarder dans le slot " + selectedSlot + " ?";
                        confirmAction = () -> {
                            saveManager.saveGame(selectedSlot);
                            refreshSaveMetadata();
                        };
                        setState(State.CONFIRM);
                    } else if (state == State.LOAD && saveManager.saveExists(selectedSlot)) {
                        confirmMessage = "Êtes-vous sûr de vouloir charger la sauvegarde du slot " + selectedSlot + " ?";
                        confirmAction = () -> {
                            saveManager.loadGame(selectedSlot);
                            setVisible(false);
                            GameState.getInstance().resumeGame();
                        };
                        setState(State.CONFIRM);
                    }
                }
                break;
                
            case CONFIRM:
                // Confirmer/annuler avec les touches
                if (inputManager.isKeyJustPressed(GLFW_KEY_ENTER)) {
                    if (confirmAction != null) {
                        confirmAction.run();
                    }
                    setState(state == State.CONFIRM ? State.SAVE : state);
                } else if (inputManager.isKeyJustPressed(GLFW_KEY_ESCAPE)) {
                    setState(state == State.CONFIRM ? State.SAVE : state);
                }
                break;
                
            case SETTINGS:
                // Aucune entrée spécifique pour les paramètres
                break;
        }
        
        // Fermer l'interface avec Échap
        if (inputManager.isKeyJustPressed(GLFW_KEY_ESCAPE) && state != State.CONFIRM) {
            setVisible(false);
            GameState.getInstance().resumeGame();
        }
    }
    
    /**
     * Gérer les entrées utilisateur avec la souris
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param mousePressed true si le bouton de la souris est appuyé, false sinon
     */
    public void handleInput(float mouseX, float mouseY, boolean mousePressed) {
        // Gérer les entrées clavier standard
        handleInput();
        
        // Gérer les entrées de la souris
        if (mousePressed) {
            // Vérifier si un bouton a été cliqué
            if (saveButton != null && saveButton.isVisible() && saveButton.contains(mouseX, mouseY)) {
                saveButton.handleClick();
            }
            
            if (loadButton != null && loadButton.isVisible() && loadButton.contains(mouseX, mouseY)) {
                loadButton.handleClick();
            }
            
            if (settingsButton != null && settingsButton.isVisible() && settingsButton.contains(mouseX, mouseY)) {
                settingsButton.handleClick();
            }
            
            if (backButton != null && backButton.isVisible() && backButton.contains(mouseX, mouseY)) {
                backButton.handleClick();
            }
            
            if (autoSaveToggleButton != null && autoSaveToggleButton.isVisible() && autoSaveToggleButton.contains(mouseX, mouseY)) {
                autoSaveToggleButton.handleClick();
            }
            
            // Vérifier si un slot a été cliqué
            if ((state == State.SAVE || state == State.LOAD) && panelX <= mouseX && mouseX <= panelX + panelWidth) {
                for (int slot = 1; slot < SaveManager.MAX_SAVE_SLOTS; slot++) {
                    float slotY = panelY + 80 + (slot - 1) * 70;
                    if (slotY <= mouseY && mouseY <= slotY + 60) {
                        selectedSlot = slot;
                        
                        // Double-clic pour action rapide
                        if (System.currentTimeMillis() - lastClickTime < 500 && lastClickSlot == selectedSlot) {
                            if (state == State.SAVE) {
                                confirmMessage = "Êtes-vous sûr de vouloir sauvegarder dans le slot " + selectedSlot + " ?";
                                confirmAction = () -> {
                                    saveManager.saveGame(selectedSlot);
                                    refreshSaveMetadata();
                                };
                                setState(State.CONFIRM);
                            } else if (state == State.LOAD && saveManager.saveExists(selectedSlot)) {
                                confirmMessage = "Êtes-vous sûr de vouloir charger la sauvegarde du slot " + selectedSlot + " ?";
                                confirmAction = () -> {
                                    saveManager.loadGame(selectedSlot);
                                };
                                setState(State.CONFIRM);
                            }
                        }
                        
                        lastClickTime = System.currentTimeMillis();
                        lastClickSlot = selectedSlot;
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void render() {
        if (!isVisible()) {
            return;
        }
        
        // Dessiner le fond
        renderer.drawUIElement(backgroundTextureId, 0, 0, renderer.getScreenWidth(), renderer.getScreenHeight());
        
        // Dessiner le panneau principal
        renderer.drawUIElement(panelBackgroundId, panelX, panelY, panelWidth, panelHeight);
        
        // Dessiner le titre
        String title = "";
        switch (state) {
            case SAVE:
                title = "Sauvegarder la partie";
                break;
            case LOAD:
                title = "Charger une partie";
                break;
            case CONFIRM:
                title = "Confirmation";
                break;
            case SETTINGS:
                title = "Paramètres de sauvegarde";
                break;
        }
        renderer.drawText(title, panelX + panelWidth / 2, panelY + 30, 24, COLOR_TEXT_HIGHLIGHT, Renderer.TextAlignment.CENTER);
        
        // Dessiner les éléments spécifiques à l'état
        switch (state) {
            case SAVE:
            case LOAD:
                renderSaveSlots();
                saveButton.render();
                loadButton.render();
                deleteButton.render();
                settingsButton.render();
                break;
                
            case CONFIRM:
                renderConfirmDialog();
                confirmButton.render();
                cancelButton.render();
                break;
                
            case SETTINGS:
                renderSettings();
                autoSaveToggleButton.render();
                break;
        }
        
        // Dessiner le bouton de retour
        backButton.render();
    }
    
    /**
     * Dessiner les slots de sauvegarde
     */
    private void renderSaveSlots() {
        float slotWidth = panelWidth - 40;
        float slotX = panelX + 20;
        float slotY = panelY + 60;
        
        // Dessiner chaque slot
        for (int slot = 1; slot < SaveManager.MAX_SAVE_SLOTS; slot++) {
            boolean isSelected = (slot == selectedSlot);
            
            // Dessiner le fond du slot
            int slotTextureId = isSelected ? slotSelectedId : slotBackgroundId;
            renderer.drawUIElement(slotTextureId, slotX, slotY, slotWidth, slotHeight);
            
            // Trouver les métadonnées pour ce slot
            SaveMetadata metadata = null;
            for (SaveMetadata meta : saveMetadataList) {
                if (meta.getSlot() == slot) {
                    metadata = meta;
                    break;
                }
            }
            
            // Dessiner les informations du slot
            if (metadata != null) {
                // Slot avec sauvegarde
                renderer.drawText("Slot " + slot, slotX + 10, slotY + 10, 18, COLOR_TEXT_HIGHLIGHT);
                renderer.drawText(metadata.getPlayerName() + " (Niveau " + metadata.getPlayerLevel() + ")", slotX + 10, slotY + 30, 16, COLOR_TEXT);
                renderer.drawText(metadata.getLocation(), slotX + 10, slotY + 50, 14, COLOR_TEXT);
                
                // Informations supplémentaires
                String infoText = metadata.getCapturedVariants() + " variants - " + metadata.getFormattedPlayTime();
                renderer.drawText(infoText, slotX + slotWidth - 10, slotY + 50, 14, COLOR_TEXT, Renderer.TextAlignment.RIGHT);
                
                // Date et heure
                renderer.drawText(metadata.getTimestamp(), slotX + slotWidth - 10, slotY + 10, 14, COLOR_TEXT, Renderer.TextAlignment.RIGHT);
            } else {
                // Slot vide
                renderer.drawText("Slot " + slot + " - Vide", slotX + 10, slotY + 30, 18, COLOR_TEXT);
            }
            
            // Passer au slot suivant
            slotY += slotHeight + slotSpacing;
        }
    }
    
    /**
     * Dessiner la boîte de dialogue de confirmation
     */
    private void renderConfirmDialog() {
        // Dessiner le fond de la boîte de dialogue
        float dialogWidth = panelWidth * 0.7f;
        float dialogHeight = panelHeight * 0.3f;
        float dialogX = panelX + (panelWidth - dialogWidth) / 2;
        float dialogY = panelY + (panelHeight - dialogHeight) / 2;
        
        renderer.drawRect(dialogX, dialogY, dialogWidth, dialogHeight, COLOR_PANEL_BACKGROUND);
        
        // Dessiner le message de confirmation
        renderer.drawText(confirmMessage, dialogX + dialogWidth / 2, dialogY + dialogHeight / 2 - 20, 18, COLOR_TEXT, Renderer.TextAlignment.CENTER);
    }
    
    /**
     * Dessiner les paramètres de sauvegarde
     */
    private void renderSettings() {
        float textX = panelX + 40;
        float textY = panelY + 80;
        
        // Dessiner les options
        renderer.drawText("Paramètres de sauvegarde", textX, textY, 20, COLOR_TEXT_HIGHLIGHT);
        textY += 40;
        
        renderer.drawText("Auto-sauvegarde:", textX, textY, 18, COLOR_TEXT);
        textY += 40;
        
        // Dessiner le bouton d'auto-sauvegarde
        autoSaveToggleButton.setPosition(textX + 200, textY - 30);
        
        textY += 20;
        
        renderer.drawText("Intervalle d'auto-sauvegarde: 5 minutes", textX, textY, 16, COLOR_TEXT);
        textY += 30;
        
        renderer.drawText("Slot d'auto-sauvegarde: " + saveManager.getAutoSaveSlot(), textX, textY, 16, COLOR_TEXT);
        textY += 40;
        
        // Informations sur les sauvegardes
        renderer.drawText("Informations:", textX, textY, 18, COLOR_TEXT);
        textY += 30;
        
        renderer.drawText("Nombre de sauvegardes: " + saveMetadataList.size(), textX, textY, 16, COLOR_TEXT);
        textY += 30;
        
        renderer.drawText("Emplacement des sauvegardes: ./saves/", textX, textY, 16, COLOR_TEXT);
    }
    
    /**
     * Définir l'état de l'interface
     * @param state Nouvel état
     */
    public void setState(State state) {
        this.state = state;
        refreshSaveMetadata();
    }
    
    /**
     * Obtenir l'état actuel de l'interface
     * @return État actuel
     */
    public State getState() {
        return state;
    }
    
    /**
     * Méthode à implémenter pour gérer un clic
     */
    @Override
    protected void handleClick() {
        // Cette méthode est appelée lorsque le composant lui-même est cliqué
        // Les clics sur les boutons sont gérés séparément dans handleInput()
    }
}

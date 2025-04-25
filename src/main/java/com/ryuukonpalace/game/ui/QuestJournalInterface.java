package com.ryuukonpalace.game.ui;

import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.quest.Quest;
import com.ryuukonpalace.game.quest.QuestManager;
import com.ryuukonpalace.game.quest.QuestObjective;
import com.ryuukonpalace.game.quest.QuestObjectiveState;
import com.ryuukonpalace.game.quest.QuestReward;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface du journal de quêtes.
 * Permet au joueur de consulter ses quêtes actives, complétées, échouées et disponibles.
 */
public class QuestJournalInterface {
    
    // Singleton
    private static QuestJournalInterface instance;
    
    // Références
    private Renderer renderer;
    private QuestManager questManager;
    
    // État de l'interface
    private boolean visible;
    private boolean initialized;
    
    // Catégories de quêtes
    public enum Category {
        ACTIVE("Quêtes Actives"),
        COMPLETED("Quêtes Complétées"),
        FAILED("Quêtes Échouées"),
        ABANDONED("Quêtes Abandonnées"),
        AVAILABLE("Quêtes Disponibles");
        
        private final String label;
        
        Category(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    }
    
    // Catégorie actuellement sélectionnée
    private Category currentCategory;
    
    // Quête actuellement sélectionnée
    private Quest selectedQuest;
    
    // Position de défilement
    private int scrollPosition;
    private int maxScrollPosition;
    
    // Constantes de mise en page
    private static final int PANEL_X = 100;
    private static final int PANEL_Y = 50;
    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 500;
    
    private static final int CATEGORY_BAR_HEIGHT = 40;
    private static final int QUEST_LIST_WIDTH = 200;
    
    private static final int QUEST_ITEM_HEIGHT = 40;
    private static final int MAX_VISIBLE_QUESTS = 10;
    
    /**
     * Constructeur privé (singleton)
     */
    private QuestJournalInterface() {
        this.renderer = Renderer.getInstance();
        this.questManager = QuestManager.getInstance();
        this.visible = false;
        this.initialized = false;
        this.currentCategory = Category.ACTIVE;
        this.scrollPosition = 0;
        this.maxScrollPosition = 0;
    }
    
    /**
     * Obtenir l'instance unique de l'interface du journal de quêtes
     * 
     * @return Instance de QuestJournalInterface
     */
    public static QuestJournalInterface getInstance() {
        if (instance == null) {
            instance = new QuestJournalInterface();
        }
        return instance;
    }
    
    /**
     * Initialiser l'interface
     */
    public void init() {
        if (!initialized) {
            // Initialisation spécifique si nécessaire
            initialized = true;
        }
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
        
        // Mettre à jour la liste des quêtes selon la catégorie sélectionnée
        List<Quest> quests = getQuestsForCurrentCategory();
        maxScrollPosition = Math.max(0, quests.size() - MAX_VISIBLE_QUESTS);
        
        // S'assurer que la position de défilement est valide
        if (scrollPosition < 0) {
            scrollPosition = 0;
        } else if (scrollPosition > maxScrollPosition) {
            scrollPosition = maxScrollPosition;
        }
    }
    
    /**
     * Dessiner l'interface
     */
    public void render() {
        if (!visible || !initialized) {
            return;
        }
        
        // Dessiner le fond du journal
        renderer.drawRect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, 0xAA000000);
        renderer.drawRect(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, 0xFF333333);
        
        // Dessiner le titre
        renderer.drawText("JOURNAL DE QUÊTES", PANEL_X + PANEL_WIDTH / 2, PANEL_Y + 20, 20, 0xFFFFFFFF);
        
        // Dessiner la barre de catégories
        renderCategoryBar();
        
        // Dessiner la liste des quêtes
        renderQuestList();
        
        // Dessiner les détails de la quête sélectionnée
        if (selectedQuest != null) {
            renderQuestDetails();
        } else {
            renderer.drawText("Sélectionnez une quête pour voir les détails", 
                    PANEL_X + QUEST_LIST_WIDTH + 20, 
                    PANEL_Y + CATEGORY_BAR_HEIGHT + 100, 
                    16, 0xFFCCCCCC);
        }
        
        // Dessiner les boutons de navigation
        renderNavigationButtons();
    }
    
    /**
     * Dessiner la barre de catégories
     */
    private void renderCategoryBar() {
        int categoryWidth = PANEL_WIDTH / Category.values().length;
        int categoryY = PANEL_Y + 40;
        
        // Dessiner le fond de la barre de catégories
        renderer.drawRect(PANEL_X, categoryY, PANEL_WIDTH, CATEGORY_BAR_HEIGHT, 0xFF222222);
        
        // Dessiner chaque catégorie
        for (int i = 0; i < Category.values().length; i++) {
            Category category = Category.values()[i];
            int categoryX = PANEL_X + i * categoryWidth;
            
            // Mettre en surbrillance la catégorie sélectionnée
            if (category == currentCategory) {
                renderer.drawRect(categoryX, categoryY, categoryWidth, CATEGORY_BAR_HEIGHT, 0xFF444444);
            }
            
            // Dessiner le texte de la catégorie
            renderer.drawText(category.getLabel(), categoryX + categoryWidth / 2, categoryY + CATEGORY_BAR_HEIGHT / 2, 14, 0xFFFFFFFF);
        }
    }
    
    /**
     * Dessiner la liste des quêtes
     */
    private void renderQuestList() {
        List<Quest> quests = getQuestsForCurrentCategory();
        
        int startY = PANEL_Y + 40 + CATEGORY_BAR_HEIGHT;
        renderer.drawRect(PANEL_X, startY, QUEST_LIST_WIDTH, PANEL_HEIGHT - CATEGORY_BAR_HEIGHT - 40, 0xFF222222);
        
        // Dessiner les quêtes visibles
        int visibleCount = Math.min(MAX_VISIBLE_QUESTS, quests.size());
        for (int i = 0; i < visibleCount; i++) {
            int index = i + scrollPosition;
            if (index < quests.size()) {
                Quest quest = quests.get(index);
                int y = startY + i * QUEST_ITEM_HEIGHT;
                
                // Dessiner le fond de l'élément (plus clair si sélectionné)
                int bgColor = (quest == selectedQuest) ? 0xFF555555 : 0xFF333333;
                renderer.drawRect(PANEL_X, y, QUEST_LIST_WIDTH, QUEST_ITEM_HEIGHT, bgColor);
                
                // Dessiner le titre de la quête
                String title = quest.getTitle();
                if (title.length() > 25) {
                    title = title.substring(0, 22) + "...";
                }
                renderer.drawText(title, 
                        PANEL_X + 10, 
                        y + QUEST_ITEM_HEIGHT / 2, 
                        14, 0xFFFFFFFF);
                
                // Dessiner un indicateur pour les quêtes principales
                if (quest.isMainQuest()) {
                    renderer.drawRect(PANEL_X + QUEST_LIST_WIDTH - 20, y + 10, 10, 20, 0xFFFFD700);
                }
            }
        }
        
        // Dessiner la barre de défilement si nécessaire
        if (maxScrollPosition > 0) {
            int scrollBarHeight = (PANEL_HEIGHT - CATEGORY_BAR_HEIGHT - 40) * MAX_VISIBLE_QUESTS / quests.size();
            int scrollBarY = startY + (PANEL_HEIGHT - CATEGORY_BAR_HEIGHT - 40 - scrollBarHeight) * scrollPosition / maxScrollPosition;
            renderer.drawRect(PANEL_X + QUEST_LIST_WIDTH - 5, scrollBarY, 5, scrollBarHeight, 0xFFAAAAAA);
        }
    }
    
    /**
     * Dessiner les détails de la quête sélectionnée
     */
    private void renderQuestDetails() {
        if (selectedQuest == null) {
            // Aucune quête sélectionnée
            int messageX = PANEL_X + QUEST_LIST_WIDTH + (PANEL_WIDTH - QUEST_LIST_WIDTH) / 2;
            int messageY = PANEL_Y + PANEL_HEIGHT / 2;
            renderer.drawText("Sélectionnez une quête pour voir ses détails", messageX, messageY, 16, 0xFFAAAAAA);
            return;
        }
        
        // Zone des détails
        int detailsX = PANEL_X + QUEST_LIST_WIDTH + 10;
        int detailsY = PANEL_Y + CATEGORY_BAR_HEIGHT + 50;
        int detailsWidth = PANEL_WIDTH - QUEST_LIST_WIDTH - 20;
        
        // Titre de la quête
        renderer.drawText(selectedQuest.getTitle(), detailsX + detailsWidth / 2, detailsY, 18, 0xFFFFFFFF);
        
        // Statut de la quête
        String statusText = "";
        int statusColor = 0xFFFFFFFF;
        
        switch (currentCategory) {
            case ACTIVE:
                statusText = "En cours";
                statusColor = 0xFF00FF00;
                break;
            case COMPLETED:
                statusText = "Terminée";
                statusColor = 0xFF00FFFF;
                break;
            case FAILED:
                statusText = "Échouée";
                statusColor = 0xFFFF0000;
                break;
            case ABANDONED:
                statusText = "Abandonnée";
                statusColor = 0xFFFF8800;
                break;
            case AVAILABLE:
                statusText = "Disponible";
                statusColor = 0xFFFFFF00;
                break;
        }
        
        renderer.drawText(statusText, detailsX + detailsWidth / 2, detailsY + 25, 14, statusColor);
        
        // Description de la quête
        String description = selectedQuest.getDescription();
        List<String> wrappedDescription = wrapText(description, detailsWidth - 20, 14);
        
        int descY = detailsY + 50;
        for (String line : wrappedDescription) {
            renderer.drawText(line, detailsX + 10, descY, 14, 0xFFCCCCCC);
            descY += 20;
        }
        
        // Objectifs de la quête
        renderer.drawText("Objectifs:", detailsX + 10, descY + 20, 16, 0xFFFFFFFF);
        descY += 40;
        
        List<QuestObjective> objectives = selectedQuest.getObjectives();
        for (QuestObjective objective : objectives) {
            // Icône de statut
            String statusIcon = "[ ]";
            int objectiveColor = 0xFFCCCCCC;
            
            if (objective.getState() == QuestObjectiveState.COMPLETED) {
                statusIcon = "[✓]";
                objectiveColor = 0xFF00FF00;
            } else if (objective.getState() == QuestObjectiveState.FAILED) {
                statusIcon = "[✗]";
                objectiveColor = 0xFFFF0000;
            } else if (objective.getState() == QuestObjectiveState.IN_PROGRESS) {
                statusIcon = "[...]";
                objectiveColor = 0xFFFFFF00;
            }
            
            // Description de l'objectif avec progression si applicable
            String objectiveText = statusIcon + " " + objective.getDescription();
            if (objective.getRequiredAmount() > 1) {
                objectiveText += " (" + objective.getCurrentAmount() + "/" + objective.getRequiredAmount() + ")";
            }
            
            renderer.drawText(objectiveText, detailsX + 10, descY, 14, objectiveColor);
            descY += 20;
        }
        
        // Récompenses de la quête
        if (selectedQuest.getRewards().size() > 0) {
            renderer.drawText("Récompenses:", detailsX + 10, descY + 20, 16, 0xFFFFFFFF);
            descY += 40;
            
            for (QuestReward reward : selectedQuest.getRewards()) {
                String rewardText = "- " + reward.getDescription();
                renderer.drawText(rewardText, detailsX + 10, descY, 14, 0xFFFFCC00);
                descY += 20;
            }
        }
    }
    
    /**
     * Dessiner les boutons de navigation
     */
    private void renderNavigationButtons() {
        int buttonY = PANEL_Y + PANEL_HEIGHT - 30;
        
        // Bouton Fermer
        renderer.drawRect(PANEL_X + PANEL_WIDTH - 100, buttonY, 90, 25, 0xFF555555);
        renderer.drawText("FERMER", 
                PANEL_X + PANEL_WIDTH - 55, 
                buttonY + 12, 
                14, 0xFFFFFFFF);
        
        // Bouton Abandonner (uniquement pour les quêtes actives)
        if (currentCategory == Category.ACTIVE && selectedQuest != null) {
            renderer.drawRect(PANEL_X + PANEL_WIDTH - 200, buttonY, 90, 25, 0xFF883333);
            renderer.drawText("ABANDONNER", 
                    PANEL_X + PANEL_WIDTH - 155, 
                    buttonY + 12, 
                    14, 0xFFFFFFFF);
        }
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
        if (!visible || !initialized) {
            return false;
        }
        
        if (mousePressed) {
            // Vérifier si une catégorie a été sélectionnée
            if (mouseY >= PANEL_Y + 40 && mouseY <= PANEL_Y + 40 + CATEGORY_BAR_HEIGHT) {
                int categoryWidth = PANEL_WIDTH / Category.values().length;
                for (int i = 0; i < Category.values().length; i++) {
                    if (mouseX >= PANEL_X + i * categoryWidth && mouseX <= PANEL_X + (i + 1) * categoryWidth) {
                        currentCategory = Category.values()[i];
                        selectedQuest = null;
                        scrollPosition = 0;
                        return true;
                    }
                }
            }
            
            // Vérifier si une quête a été sélectionnée
            if (mouseX >= PANEL_X && mouseX <= PANEL_X + QUEST_LIST_WIDTH) {
                int startY = PANEL_Y + 40 + CATEGORY_BAR_HEIGHT;
                int endY = startY + Math.min(MAX_VISIBLE_QUESTS, getQuestsForCurrentCategory().size()) * QUEST_ITEM_HEIGHT;
                
                if (mouseY >= startY && mouseY <= endY) {
                    int index = (int) ((mouseY - startY) / QUEST_ITEM_HEIGHT) + scrollPosition;
                    List<Quest> quests = getQuestsForCurrentCategory();
                    
                    if (index >= 0 && index < quests.size()) {
                        selectedQuest = quests.get(index);
                        return true;
                    }
                }
            }
            
            // Vérifier si le bouton Fermer a été cliqué
            int buttonY = PANEL_Y + PANEL_HEIGHT - 30;
            if (mouseX >= PANEL_X + PANEL_WIDTH - 100 && mouseX <= PANEL_X + PANEL_WIDTH - 10 &&
                mouseY >= buttonY && mouseY <= buttonY + 25) {
                setVisible(false);
                return true;
            }
            
            // Vérifier si le bouton Abandonner a été cliqué
            if (currentCategory == Category.ACTIVE && selectedQuest != null) {
                if (mouseX >= PANEL_X + PANEL_WIDTH - 200 && mouseX <= PANEL_X + PANEL_WIDTH - 110 &&
                    mouseY >= buttonY && mouseY <= buttonY + 25) {
                    // Abandonner la quête
                    selectedQuest.abandon();
                    selectedQuest = null;
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Gérer le défilement de la molette de la souris
     * 
     * @param scrollAmount Quantité de défilement
     * @return true si l'entrée a été traitée, false sinon
     */
    public boolean handleScroll(float scrollAmount) {
        if (!visible || !initialized) {
            return false;
        }
        
        // Mettre à jour la position de défilement
        scrollPosition += (int) scrollAmount;
        
        // S'assurer que la position de défilement est valide
        if (scrollPosition < 0) {
            scrollPosition = 0;
        } else if (scrollPosition > maxScrollPosition) {
            scrollPosition = maxScrollPosition;
        }
        
        return true;
    }
    
    /**
     * Obtenir les quêtes pour la catégorie actuellement sélectionnée
     * 
     * @return Liste des quêtes pour la catégorie actuelle
     */
    private List<Quest> getQuestsForCurrentCategory() {
        List<Quest> result = new ArrayList<>();
        
        switch (currentCategory) {
            case ACTIVE:
                result.addAll(questManager.getActiveQuests());
                break;
            case COMPLETED:
                result.addAll(questManager.getCompletedQuests());
                break;
            case FAILED:
                result.addAll(questManager.getFailedQuests());
                break;
            case ABANDONED:
                result.addAll(questManager.getAbandonedQuests());
                break;
            case AVAILABLE:
                result.addAll(questManager.getAvailableQuests());
                break;
        }
        
        return result;
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
     * Définir la visibilité de l'interface
     * 
     * @param visible true pour afficher l'interface, false pour la masquer
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        
        if (visible && !initialized) {
            init();
        }
        
        // Réinitialiser la sélection si l'interface est masquée
        if (!visible) {
            selectedQuest = null;
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
     * Découper un texte en plusieurs lignes selon une largeur maximale
     * 
     * @param text Texte à découper
     * @param maxWidth Largeur maximale en pixels
     * @param fontSize Taille de la police
     * @return Liste des lignes de texte
     */
    private List<String> wrapText(String text, int maxWidth, int fontSize) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.toString() + (currentLine.length() > 0 ? " " : "") + word;
            int textWidth = testLine.length() * fontSize / 2; // Approximation simple de la largeur
            
            if (textWidth <= maxWidth) {
                currentLine.append(currentLine.length() > 0 ? " " : "").append(word);
            } else {
                result.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        
        if (currentLine.length() > 0) {
            result.add(currentLine.toString());
        }
        
        return result;
    }
}

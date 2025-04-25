package com.ryuukonpalace.game.quest;

/**
 * Énumération des états possibles d'une quête
 */
public enum QuestState {
    NOT_STARTED("Non commencée"),
    AVAILABLE("Disponible"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminée"),
    FAILED("Échouée"),
    ABANDONED("Abandonnée");
    
    private final String description;
    
    QuestState(String description) {
        this.description = description;
    }
    
    /**
     * Obtenir la description de l'état
     * 
     * @return Description de l'état
     */
    public String getDescription() {
        return description;
    }
}

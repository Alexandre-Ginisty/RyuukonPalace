package com.ryuukonpalace.game.quest;

/**
 * Énumération des états possibles d'un objectif de quête
 */
public enum QuestObjectiveState {
    NOT_STARTED("Non commencé"),
    IN_PROGRESS("En cours"),
    COMPLETED("Complété"),
    FAILED("Échoué");
    
    private final String description;
    
    QuestObjectiveState(String description) {
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

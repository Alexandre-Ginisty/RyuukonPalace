package com.ryuukonpalace.game.quest;

/**
 * Interface pour les callbacks de quêtes.
 * Permet de réagir aux événements liés aux quêtes (démarrage, complétion, échec, etc.)
 */
public interface QuestCallback {
    
    /**
     * Appelé lorsqu'une quête est démarrée
     * 
     * @param quest Quête démarrée
     */
    void onQuestStarted(Quest quest);
    
    /**
     * Appelé lorsqu'une quête est complétée
     * 
     * @param quest Quête complétée
     */
    void onQuestCompleted(Quest quest);
    
    /**
     * Appelé lorsqu'une quête échoue
     * 
     * @param quest Quête échouée
     * @param reason Raison de l'échec
     */
    void onQuestFailed(Quest quest, String reason);
    
    /**
     * Appelé lorsqu'une quête est abandonnée
     * 
     * @param quest Quête abandonnée
     */
    void onQuestAbandoned(Quest quest);
    
    /**
     * Appelé lorsqu'une quête devient disponible pour le joueur
     * 
     * @param quest Quête disponible
     */
    void onQuestAvailable(Quest quest);
    
    /**
     * Appelé lorsqu'un objectif de quête est mis à jour
     * 
     * @param quest Quête concernée
     * @param objective Objectif mis à jour
     */
    void onObjectiveUpdated(Quest quest, QuestObjective objective);
    
    /**
     * Appelé lorsqu'un objectif de quête est complété
     * 
     * @param quest Quête concernée
     * @param objective Objectif complété
     */
    void onObjectiveCompleted(Quest quest, QuestObjective objective);
    
    /**
     * Appelé lorsqu'un objectif de quête échoue
     * 
     * @param quest Quête concernée
     * @param objective Objectif échoué
     */
    void onObjectiveFailed(Quest quest, QuestObjective objective);
}

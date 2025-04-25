package com.ryuukonpalace.game.quest;

/**
 * Énumération des statuts possibles pour une quête dans le système de sauvegarde.
 * Utilisé pour suivre l'état des quêtes entre les sessions de jeu.
 */
public enum QuestStatus {
    /**
     * La quête est disponible mais n'a pas encore été acceptée par le joueur.
     */
    AVAILABLE,
    
    /**
     * La quête est en cours, le joueur l'a acceptée et travaille dessus.
     */
    ACTIVE,
    
    /**
     * La quête a été complétée avec succès.
     */
    COMPLETED,
    
    /**
     * La quête a échoué, le joueur n'a pas réussi à la compléter.
     */
    FAILED,
    
    /**
     * La quête a été abandonnée par le joueur.
     */
    ABANDONED
}

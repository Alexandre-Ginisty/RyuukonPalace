package com.ryuukonpalace.game.combat;

/**
 * États possibles du système de combat.
 */
public enum CombatState {
    /**
     * Combat inactif
     */
    INACTIVE,
    
    /**
     * Affichage d'un message
     */
    MESSAGE,
    
    /**
     * Tour du joueur (menu principal)
     */
    PLAYER_TURN,
    
    /**
     * Sélection d'une capacité
     */
    ABILITY_SELECTION,
    
    /**
     * Sélection d'une pierre de capture
     */
    CAPTURE_SELECTION,
    
    /**
     * Utilisation d'un objet
     */
    ITEM_SELECTION,
    
    /**
     * Tour de l'ennemi
     */
    ENEMY_TURN,
    
    /**
     * Exécution d'une action
     */
    ACTION_EXECUTION,
    
    /**
     * Tentative de capture
     */
    CAPTURE,
    
    /**
     * Fin de combat - Victoire
     */
    VICTORY,
    
    /**
     * Fin de combat - Défaite
     */
    DEFEAT,
    
    /**
     * Fin de combat - Fuite
     */
    FLEE,
    
    /**
     * Menu principal
     */
    MAIN_MENU
}

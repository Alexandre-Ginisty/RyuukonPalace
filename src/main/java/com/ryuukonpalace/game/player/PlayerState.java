package com.ryuukonpalace.game.player;

/**
 * Énumération des états possibles du joueur.
 */
public enum PlayerState {
    IDLE,       // Joueur immobile
    WALKING,    // Joueur en déplacement
    RUNNING,    // Joueur en course
    JUMPING,    // Joueur en saut
    FALLING,    // Joueur en chute
    ATTACKING,  // Joueur en attaque
    CASTING,    // Joueur en train de lancer un sort
    DAMAGED,    // Joueur blessé
    DEAD,       // Joueur mort
    INTERACTING // Joueur en interaction avec un objet ou PNJ
}

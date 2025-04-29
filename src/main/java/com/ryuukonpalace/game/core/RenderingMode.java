package com.ryuukonpalace.game.core;

/**
 * Énumération des modes de rendu disponibles.
 * Ces modes permettent de contrôler comment le jeu est affiché à l'écran.
 */
public enum RenderingMode {
    /**
     * Mode de rendu normal, avec toutes les textures et effets visuels.
     */
    NORMAL,
    
    /**
     * Mode de rendu simplifié, pour les appareils à faible puissance.
     */
    SIMPLE,
    
    /**
     * Mode de rendu de débogage, affichant des informations supplémentaires.
     */
    DEBUG,
    
    /**
     * Mode de rendu en fil de fer, affichant uniquement les contours des objets.
     */
    WIREFRAME,
    
    /**
     * Mode de rendu des collisions, pour visualiser les zones de collision.
     */
    COLLISION,
    
    /**
     * Mode de rendu des zones de navigation pour les PNJ.
     */
    NAVIGATION,
    
    /**
     * Mode de rendu des zones d'interaction pour le joueur.
     */
    INTERACTION,
    
    /**
     * Mode de rendu en niveaux de gris.
     */
    GRAYSCALE,
    
    /**
     * Mode de rendu pour visualiser les performances (heatmap).
     */
    PERFORMANCE
}

package com.ryuukonpalace.game.data;

/**
 * Classe représentant les métadonnées communes pour les dictionnaires.
 * Utilisée pour la sérialisation/désérialisation JSON.
 */
public class Metadata {
    // Version du dictionnaire
    public String version;
    
    // Description du dictionnaire
    public String description;
    
    // Date de dernière mise à jour
    public String lastUpdated;
}

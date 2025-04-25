package com.ryuukonpalace.game.data;

import java.util.List;

/**
 * Classe représentant le dictionnaire complet des PNJ.
 * Utilisée pour la sérialisation/désérialisation JSON.
 */
public class NPCsDictionary {
    // Métadonnées du dictionnaire
    public Metadata metadata;
    
    // Liste des définitions de PNJ
    public List<NPCDefinition> npcs;
}

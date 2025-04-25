package com.ryuukonpalace.game.data;

import java.util.List;

/**
 * Classe représentant le dictionnaire complet des zones de spawn.
 * Utilisée pour la sérialisation/désérialisation JSON.
 */
public class SpawnZonesDictionary {
    // Métadonnées du dictionnaire
    public Metadata metadata;
    
    // Liste des définitions de zones de spawn
    public List<SpawnZoneDefinition> spawn_zones;
}

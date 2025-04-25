package com.ryuukonpalace.game.data;

import java.util.List;

/**
 * Classe représentant le dictionnaire complet des créatures.
 * Utilisée pour la sérialisation/désérialisation JSON.
 */
public class CreaturesDictionary {
    // Métadonnées du dictionnaire
    public Metadata metadata;
    
    // Liste des définitions de créatures
    public List<CreatureDefinition> creatures;
}

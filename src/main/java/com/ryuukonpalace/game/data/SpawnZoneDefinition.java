package com.ryuukonpalace.game.data;

import com.ryuukonpalace.game.creatures.CreatureType;
import java.util.List;
import java.util.Map;

/**
 * Définition d'une zone de spawn pour le chargement/sauvegarde des données.
 * Cette classe est utilisée pour la sérialisation/désérialisation JSON.
 */
public class SpawnZoneDefinition {
    // Identifiant unique de la zone
    public String id;
    
    // Nom de la zone
    public String name;
    
    // Description de la zone
    public String description;
    
    // Type de zone (ex: "Herbes hautes", "Zone ombragée", etc.)
    public String type;
    
    // Types de créatures dominants dans cette zone
    public List<CreatureType> dominant_types;
    
    // IDs des créatures qui peuvent apparaître dans cette zone
    public List<Integer> creatures;
    
    // Modificateurs de rareté par type de créature
    public Map<String, Double> rarity_modifiers;
    
    // Modificateurs selon l'heure de la journée
    public Map<String, Double> time_conditions;
    
    // Modificateurs selon les conditions météorologiques
    public Map<String, Double> weather_conditions;
    
    // Chemin vers la texture représentant la zone
    public String texture;
}

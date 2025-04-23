package com.ryuukonpalace.game.items;

import java.util.HashMap;
import java.util.Map;
import com.ryuukonpalace.game.creatures.CreatureType;

/**
 * Énumération des types de pierres de capture.
 * Chaque type a des affinités différentes avec certains types de créatures.
 */
public enum CaptureStoneType {
    FIRE("Feu", "du feu", createFireEffectiveness()),
    WATER("Eau", "de l'eau", createWaterEffectiveness()),
    EARTH("Terre", "de la terre", createEarthEffectiveness()),
    AIR("Air", "de l'air", createAirEffectiveness()),
    LIGHT("Lumière", "de la lumière", createLightEffectiveness()),
    DARK("Ténèbres", "des ténèbres", createDarkEffectiveness()),
    NEUTRAL("Neutre", "neutre", createNeutralEffectiveness());
    
    // Nom du type
    private final String name;
    
    // Description du type
    private final String description;
    
    // Table d'efficacité contre les différents types de créatures
    private final Map<CreatureType, Float> typeEffectiveness;
    
    /**
     * Constructeur
     * 
     * @param name Nom du type
     * @param description Description du type
     * @param typeEffectiveness Table d'efficacité
     */
    CaptureStoneType(String name, String description, Map<CreatureType, Float> typeEffectiveness) {
        this.name = name;
        this.description = description;
        this.typeEffectiveness = typeEffectiveness;
    }
    
    /**
     * Obtenir le nom du type
     * 
     * @return Nom du type
     */
    public String getName() {
        return name;
    }
    
    /**
     * Obtenir la description du type
     * 
     * @return Description du type
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtenir l'efficacité contre un type de créature spécifique
     * 
     * @param creatureType Type de la créature
     * @return Multiplicateur d'efficacité (1.0 = normal, >1.0 = plus efficace, <1.0 = moins efficace)
     */
    public float getTypeEffectiveness(CreatureType creatureType) {
        return typeEffectiveness.getOrDefault(creatureType, 1.0f);
    }
    
    /**
     * Créer la table d'efficacité pour le type Feu
     * 
     * @return Table d'efficacité
     */
    private static Map<CreatureType, Float> createFireEffectiveness() {
        Map<CreatureType, Float> effectiveness = new HashMap<>();
        effectiveness.put(CreatureType.FIRE, 1.5f);    // Super efficace contre le Feu
        effectiveness.put(CreatureType.EARTH, 1.2f);   // Efficace contre la Terre
        effectiveness.put(CreatureType.WATER, 0.8f);   // Peu efficace contre l'Eau
        effectiveness.put(CreatureType.AIR, 1.0f);     // Normal contre l'Air
        effectiveness.put(CreatureType.LIGHT, 1.0f);   // Normal contre la Lumière
        effectiveness.put(CreatureType.SHADOW, 1.0f);  // Normal contre les Ténèbres
        effectiveness.put(CreatureType.NATURE, 1.2f);  // Efficace contre la Nature
        effectiveness.put(CreatureType.ICE, 1.2f);     // Efficace contre la Glace
        return effectiveness;
    }
    
    /**
     * Créer la table d'efficacité pour le type Eau
     * 
     * @return Table d'efficacité
     */
    private static Map<CreatureType, Float> createWaterEffectiveness() {
        Map<CreatureType, Float> effectiveness = new HashMap<>();
        effectiveness.put(CreatureType.FIRE, 1.2f);    // Efficace contre le Feu
        effectiveness.put(CreatureType.EARTH, 1.0f);   // Normal contre la Terre
        effectiveness.put(CreatureType.WATER, 1.5f);   // Super efficace contre l'Eau
        effectiveness.put(CreatureType.AIR, 0.8f);     // Peu efficace contre l'Air
        effectiveness.put(CreatureType.LIGHT, 1.0f);   // Normal contre la Lumière
        effectiveness.put(CreatureType.SHADOW, 1.0f);  // Normal contre les Ténèbres
        effectiveness.put(CreatureType.NATURE, 0.8f);  // Peu efficace contre la Nature
        effectiveness.put(CreatureType.ELECTRIC, 0.8f);// Peu efficace contre l'Électrique
        return effectiveness;
    }
    
    /**
     * Créer la table d'efficacité pour le type Terre
     * 
     * @return Table d'efficacité
     */
    private static Map<CreatureType, Float> createEarthEffectiveness() {
        Map<CreatureType, Float> effectiveness = new HashMap<>();
        effectiveness.put(CreatureType.FIRE, 0.8f);    // Peu efficace contre le Feu
        effectiveness.put(CreatureType.EARTH, 1.5f);   // Super efficace contre la Terre
        effectiveness.put(CreatureType.WATER, 1.0f);   // Normal contre l'Eau
        effectiveness.put(CreatureType.AIR, 1.2f);     // Efficace contre l'Air
        effectiveness.put(CreatureType.LIGHT, 1.0f);   // Normal contre la Lumière
        effectiveness.put(CreatureType.SHADOW, 1.0f);  // Normal contre les Ténèbres
        effectiveness.put(CreatureType.METAL, 1.2f);   // Efficace contre le Métal
        effectiveness.put(CreatureType.ELECTRIC, 1.2f);// Efficace contre l'Électrique
        return effectiveness;
    }
    
    /**
     * Créer la table d'efficacité pour le type Air
     * 
     * @return Table d'efficacité
     */
    private static Map<CreatureType, Float> createAirEffectiveness() {
        Map<CreatureType, Float> effectiveness = new HashMap<>();
        effectiveness.put(CreatureType.FIRE, 1.0f);    // Normal contre le Feu
        effectiveness.put(CreatureType.EARTH, 0.8f);   // Peu efficace contre la Terre
        effectiveness.put(CreatureType.WATER, 1.2f);   // Efficace contre l'Eau
        effectiveness.put(CreatureType.AIR, 1.5f);     // Super efficace contre l'Air
        effectiveness.put(CreatureType.LIGHT, 1.0f);   // Normal contre la Lumière
        effectiveness.put(CreatureType.SHADOW, 1.0f);  // Normal contre les Ténèbres
        effectiveness.put(CreatureType.NATURE, 1.2f);  // Efficace contre la Nature
        effectiveness.put(CreatureType.ELECTRIC, 0.8f);// Peu efficace contre l'Électrique
        return effectiveness;
    }
    
    /**
     * Créer la table d'efficacité pour le type Lumière
     * 
     * @return Table d'efficacité
     */
    private static Map<CreatureType, Float> createLightEffectiveness() {
        Map<CreatureType, Float> effectiveness = new HashMap<>();
        effectiveness.put(CreatureType.FIRE, 1.0f);    // Normal contre le Feu
        effectiveness.put(CreatureType.EARTH, 1.0f);   // Normal contre la Terre
        effectiveness.put(CreatureType.WATER, 1.0f);   // Normal contre l'Eau
        effectiveness.put(CreatureType.AIR, 1.0f);     // Normal contre l'Air
        effectiveness.put(CreatureType.LIGHT, 1.5f);   // Super efficace contre la Lumière
        effectiveness.put(CreatureType.SHADOW, 1.2f);  // Efficace contre les Ténèbres
        effectiveness.put(CreatureType.METAL, 0.8f);   // Peu efficace contre le Métal
        effectiveness.put(CreatureType.PSYCHIC, 1.2f); // Efficace contre le Psychique
        return effectiveness;
    }
    
    /**
     * Créer la table d'efficacité pour le type Ténèbres
     * 
     * @return Table d'efficacité
     */
    private static Map<CreatureType, Float> createDarkEffectiveness() {
        Map<CreatureType, Float> effectiveness = new HashMap<>();
        effectiveness.put(CreatureType.FIRE, 1.0f);    // Normal contre le Feu
        effectiveness.put(CreatureType.EARTH, 1.0f);   // Normal contre la Terre
        effectiveness.put(CreatureType.WATER, 1.0f);   // Normal contre l'Eau
        effectiveness.put(CreatureType.AIR, 1.0f);     // Normal contre l'Air
        effectiveness.put(CreatureType.LIGHT, 1.2f);   // Efficace contre la Lumière
        effectiveness.put(CreatureType.SHADOW, 1.5f);  // Super efficace contre les Ténèbres
        effectiveness.put(CreatureType.PSYCHIC, 1.2f); // Efficace contre le Psychique
        effectiveness.put(CreatureType.MYTHICAL, 0.8f);// Peu efficace contre le Mythique
        return effectiveness;
    }
    
    /**
     * Créer la table d'efficacité pour le type Neutre
     * 
     * @return Table d'efficacité
     */
    private static Map<CreatureType, Float> createNeutralEffectiveness() {
        Map<CreatureType, Float> effectiveness = new HashMap<>();
        // Le type neutre a une efficacité normale (1.0) contre tous les types
        for (CreatureType type : CreatureType.values()) {
            effectiveness.put(type, 1.0f);
        }
        return effectiveness;
    }
}

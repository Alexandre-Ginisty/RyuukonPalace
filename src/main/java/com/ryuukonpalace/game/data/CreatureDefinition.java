package com.ryuukonpalace.game.data;

import com.ryuukonpalace.game.creatures.CreatureType;
import java.util.List;

/**
 * Définition d'une créature pour le chargement/sauvegarde des données.
 * Cette classe est utilisée pour la sérialisation/désérialisation JSON.
 */
public class CreatureDefinition {
    // Identifiant unique de la créature
    public int id;
    
    // Nom de la créature
    public String name;
    
    // Type de la créature
    public CreatureType type;
    
    // Catégorie de la créature (Stratège, Furieux, etc.)
    public String category;
    
    // Description de la créature
    public String description;
    
    // Statistiques de base
    public BaseStats baseStats;
    
    // Capacités de la créature
    public List<AbilityDefinition> abilities;
    
    // Informations d'évolution
    public Evolution evolution;
    
    // Zones de spawn
    public List<String> spawnLocations;
    
    // Rareté du spawn
    public String spawnRarity;
    
    // Conditions de spawn
    public String spawnConditions;
    
    // Chemin vers la texture
    public String texture;
    
    /**
     * Statistiques de base d'une créature
     */
    public static class BaseStats {
        public int health;
        public int attack;
        public int defense;
        public int speed;
        public int magicalAttack;
        public int magicalDefense;
    }
    
    /**
     * Définition d'une capacité
     */
    public static class AbilityDefinition {
        public int id;
        public String name;
        public String description;
        public int power;
        public int accuracy;
        public CreatureType type;
        public int levelLearned;
    }
    
    /**
     * Informations d'évolution
     */
    public static class Evolution {
        public Integer evolvesTo;  // ID de la créature évoluée, null si pas d'évolution
        public Integer level;      // Niveau requis pour évoluer, null si pas d'évolution
    }
}

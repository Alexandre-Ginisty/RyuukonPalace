package com.ryuukonpalace.game.creatures;

/**
 * Enum representing the different types of creatures in the game.
 * Each type has strengths and weaknesses against other types.
 */
public enum CreatureType {
    // Utilisation des anciens noms pour la compatibilité, mais avec les descriptions correspondant à l'univers Ryuukon Palace
    FIRE("Stratège"),
    WATER("Furieux"),
    EARTH("Mystique"),
    AIR("Serein"),
    LIGHT("Chaotique"),
    SHADOW("Ombreux"),
    METAL("Lumineux"),
    NATURE("Terrestre"),
    ELECTRIC("Aérien"),
    ICE("Aquatique"),
    PSYCHIC("Spirituel"),
    MYTHICAL("Ancestral");
    
    private final String name;
    
    CreatureType(String name) {
        this.name = name;
    }
    
    /**
     * Get the display name of the creature type
     * 
     * @return The display name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Calculate the type effectiveness multiplier for an attack
     * 
     * @param attackType The type of the attack
     * @param defenderType The type of the defender
     * @return The effectiveness multiplier (0.5 for not very effective, 1.0 for normal, 2.0 for super effective)
     */
    public static double getEffectiveness(CreatureType attackType, CreatureType defenderType) {
        // Type effectiveness chart based on Ryuukon Palace lore
        switch (attackType) {
            case FIRE: // Stratège
                if (defenderType == WATER || defenderType == LIGHT) return 2.0;
                if (defenderType == EARTH || defenderType == PSYCHIC) return 0.5;
                break;
            case WATER: // Furieux
                if (defenderType == AIR || defenderType == NATURE) return 2.0;
                if (defenderType == FIRE || defenderType == SHADOW) return 0.5;
                break;
            case EARTH: // Mystique
                if (defenderType == FIRE || defenderType == PSYCHIC) return 2.0;
                if (defenderType == LIGHT || defenderType == MYTHICAL) return 0.5;
                break;
            case AIR: // Serein
                if (defenderType == LIGHT || defenderType == SHADOW) return 2.0;
                if (defenderType == WATER || defenderType == ICE) return 0.5;
                break;
            case LIGHT: // Chaotique
                if (defenderType == FIRE || defenderType == AIR) return 2.0;
                if (defenderType == WATER || defenderType == MYTHICAL) return 0.5;
                break;
            case SHADOW: // Ombreux
                if (defenderType == LIGHT || defenderType == PSYCHIC) return 2.0;
                if (defenderType == MYTHICAL) return 0.5;
                break;
            case METAL: // Lumineux
                if (defenderType == SHADOW || defenderType == MYTHICAL) return 2.0;
                if (defenderType == PSYCHIC) return 0.5;
                break;
            case NATURE: // Terrestre
                if (defenderType == ICE || defenderType == ELECTRIC) return 2.0;
                if (defenderType == LIGHT || defenderType == AIR) return 0.5;
                break;
            case ELECTRIC: // Aérien
                if (defenderType == NATURE || defenderType == ICE) return 2.0;
                if (defenderType == SHADOW || defenderType == LIGHT) return 0.5;
                break;
            case ICE: // Aquatique
                if (defenderType == NATURE || defenderType == WATER) return 2.0;
                if (defenderType == ELECTRIC || defenderType == AIR) return 0.5;
                break;
            case PSYCHIC: // Spirituel
                if (defenderType == MYTHICAL) return 2.0;
                if (defenderType == SHADOW || defenderType == EARTH) return 0.5;
                break;
            case MYTHICAL: // Ancestral
                if (defenderType == SHADOW) return 2.0;
                if (defenderType == PSYCHIC || defenderType == METAL) return 0.5;
                break;
        }
        
        // Default effectiveness
        return 1.0;
    }
}

package com.ryuukonpalace.game.creatures;

/**
 * Enum representing the different types of creatures in the game.
 * Each type has strengths and weaknesses against other types.
 */
public enum CreatureType {
    FIRE("Feu"),
    WATER("Eau"),
    EARTH("Terre"),
    AIR("Air"),
    LIGHT("Lumière"),
    SHADOW("Ombre"),
    METAL("Métal"),
    NATURE("Nature"),
    ELECTRIC("Électrique"),
    ICE("Glace"),
    PSYCHIC("Psychique"),
    MYTHICAL("Mythique");
    
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
        // Type effectiveness chart
        switch (attackType) {
            case FIRE:
                if (defenderType == NATURE || defenderType == ICE) return 2.0;
                if (defenderType == WATER || defenderType == EARTH) return 0.5;
                break;
            case WATER:
                if (defenderType == FIRE || defenderType == EARTH) return 2.0;
                if (defenderType == NATURE || defenderType == ELECTRIC) return 0.5;
                break;
            case EARTH:
                if (defenderType == FIRE || defenderType == ELECTRIC || defenderType == METAL) return 2.0;
                if (defenderType == AIR || defenderType == ICE) return 0.5;
                break;
            case AIR:
                if (defenderType == NATURE || defenderType == ELECTRIC) return 2.0;
                if (defenderType == EARTH || defenderType == METAL) return 0.5;
                break;
            case LIGHT:
                if (defenderType == SHADOW || defenderType == PSYCHIC) return 2.0;
                if (defenderType == METAL) return 0.5;
                break;
            case SHADOW:
                if (defenderType == LIGHT || defenderType == PSYCHIC) return 2.0;
                if (defenderType == MYTHICAL) return 0.5;
                break;
            case METAL:
                if (defenderType == ICE || defenderType == NATURE) return 2.0;
                if (defenderType == FIRE || defenderType == EARTH || defenderType == ELECTRIC) return 0.5;
                break;
            case NATURE:
                if (defenderType == WATER || defenderType == EARTH) return 2.0;
                if (defenderType == FIRE || defenderType == AIR || defenderType == METAL) return 0.5;
                break;
            case ELECTRIC:
                if (defenderType == WATER || defenderType == AIR) return 2.0;
                if (defenderType == EARTH || defenderType == NATURE) return 0.5;
                break;
            case ICE:
                if (defenderType == NATURE || defenderType == AIR || defenderType == EARTH) return 2.0;
                if (defenderType == FIRE || defenderType == WATER || defenderType == METAL) return 0.5;
                break;
            case PSYCHIC:
                if (defenderType == MYTHICAL) return 2.0;
                if (defenderType == SHADOW || defenderType == LIGHT) return 0.5;
                break;
            case MYTHICAL:
                if (defenderType == SHADOW) return 2.0;
                if (defenderType == PSYCHIC || defenderType == LIGHT) return 0.5;
                break;
        }
        
        // Default effectiveness
        return 1.0;
    }
}

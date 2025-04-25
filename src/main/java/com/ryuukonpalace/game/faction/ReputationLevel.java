package com.ryuukonpalace.game.faction;

import java.util.List;

/**
 * Représente un niveau de réputation avec une faction.
 */
public class ReputationLevel {
    private String level;
    private int threshold;
    private String name;
    private String description;
    private List<String> benefits;
    private List<String> restrictions;
    
    /**
     * Constructeur pour un niveau de réputation.
     */
    public ReputationLevel(String level, int threshold, String name, String description, 
                          List<String> benefits, List<String> restrictions) {
        this.level = level;
        this.threshold = threshold;
        this.name = name;
        this.description = description;
        this.benefits = benefits;
        this.restrictions = restrictions;
    }
    
    /**
     * Vérifie si ce niveau de réputation est positif, neutre ou négatif.
     */
    public ReputationType getType() {
        if (threshold > 0) {
            return ReputationType.POSITIVE;
        } else if (threshold < 0) {
            return ReputationType.NEGATIVE;
        } else {
            return ReputationType.NEUTRAL;
        }
    }
    
    // Getters
    
    public String getLevel() {
        return level;
    }
    
    public int getThreshold() {
        return threshold;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<String> getBenefits() {
        return benefits;
    }
    
    public List<String> getRestrictions() {
        return restrictions;
    }
    
    /**
     * Énumération des types de réputation.
     */
    public enum ReputationType {
        POSITIVE,
        NEUTRAL,
        NEGATIVE
    }
}

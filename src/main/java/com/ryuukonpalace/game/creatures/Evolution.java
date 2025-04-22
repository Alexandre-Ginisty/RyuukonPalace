package com.ryuukonpalace.game.creatures;

/**
 * Represents the evolution of a creature.
 * Contains information about the required level and the evolved form.
 */
public class Evolution {
    
    private int requiredLevel;
    private Creature evolvedForm;
    
    /**
     * Constructor for creating a new evolution
     * 
     * @param requiredLevel The level required for evolution
     * @param evolvedForm The evolved form of the creature
     */
    public Evolution(int requiredLevel, Creature evolvedForm) {
        this.requiredLevel = requiredLevel;
        this.evolvedForm = evolvedForm;
    }
    
    /**
     * Get the level required for evolution
     * 
     * @return The required level
     */
    public int getRequiredLevel() {
        return requiredLevel;
    }
    
    /**
     * Get the evolved form of the creature
     * 
     * @return The evolved form
     */
    public Creature getEvolvedForm() {
        return evolvedForm;
    }
}

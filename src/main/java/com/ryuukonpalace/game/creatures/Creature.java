package com.ryuukonpalace.game.creatures;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all creatures in the game.
 * Similar to Pokémon, creatures can be captured, trained, and evolved.
 */
public class Creature {
    
    private int id;
    private String name;
    private CreatureType type;
    private int level;
    private int experience;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;
    private int speed;
    private List<Ability> abilities;
    private Evolution evolution;
    
    /**
     * Constructor for creating a new creature
     * 
     * @param id Unique identifier for the creature
     * @param name Name of the creature
     * @param type Type of the creature
     * @param level Starting level of the creature
     * @param health Starting health of the creature
     * @param attack Attack stat of the creature
     * @param defense Defense stat of the creature
     * @param speed Speed stat of the creature
     */
    public Creature(int id, String name, CreatureType type, int level, int health, int attack, int defense, int speed) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.experience = 0;
        this.health = health;
        this.maxHealth = health;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.abilities = new ArrayList<>();
    }
    
    /**
     * Add an ability to the creature
     * 
     * @param ability The ability to add
     */
    public void addAbility(Ability ability) {
        abilities.add(ability);
    }
    
    /**
     * Set the evolution for this creature
     * 
     * @param evolution The evolution details
     */
    public void setEvolution(Evolution evolution) {
        this.evolution = evolution;
    }
    
    /**
     * Check if the creature can evolve at its current level
     * 
     * @return True if the creature can evolve, false otherwise
     */
    public boolean canEvolve() {
        return evolution != null && level >= evolution.getRequiredLevel();
    }
    
    /**
     * Evolve the creature if possible
     * 
     * @return The evolved creature, or this creature if evolution is not possible
     */
    public Creature evolve() {
        if (canEvolve()) {
            return evolution.getEvolvedForm();
        }
        return this;
    }
    
    /**
     * Add experience to the creature and level up if necessary
     * 
     * @param exp The amount of experience to add
     * @return True if the creature leveled up, false otherwise
     */
    public boolean addExperience(int exp) {
        this.experience += exp;
        
        // Check if the creature should level up
        int expNeeded = level * 100; // Simple formula for experience needed to level up
        if (experience >= expNeeded) {
            levelUp();
            return true;
        }
        
        return false;
    }
    
    /**
     * Level up the creature, increasing its stats
     */
    private void levelUp() {
        level++;
        maxHealth += 5;
        health = maxHealth;
        attack += 2;
        defense += 2;
        speed += 2;
        
        // Reset experience for next level
        experience = 0;
        
        System.out.println(name + " leveled up to level " + level + "!");
        
        // Check if the creature can learn new abilities at this level
        checkNewAbilities();
    }
    
    /**
     * Check if the creature can learn new abilities at its current level
     */
    private void checkNewAbilities() {
        // This would be implemented based on a database of abilities
        // For now, just a placeholder
    }
    
    /**
     * Take damage from an attack
     * 
     * @param damage The amount of damage to take
     * @return True if the creature fainted, false otherwise
     */
    public boolean takeDamage(int damage) {
        // Apply defense to reduce damage
        int actualDamage = Math.max(1, damage - (defense / 5));
        health -= actualDamage;
        
        if (health <= 0) {
            health = 0;
            return true; // Creature fainted
        }
        
        return false;
    }
    
    /**
     * Heal the creature
     * 
     * @param amount The amount to heal
     */
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
    
    /**
     * Get a random ability from the creature's abilities
     * 
     * @return A random ability, or null if the creature has no abilities
     */
    public Ability getRandomAbility() {
        if (abilities.isEmpty()) {
            return null;
        }
        
        int index = (int) (Math.random() * abilities.size());
        return abilities.get(index);
    }
    
    // Getters and setters
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public CreatureType getType() {
        return type;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public int getAttack() {
        return attack;
    }
    
    public int getDefense() {
        return defense;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public List<Ability> getAbilities() {
        return abilities;
    }
}

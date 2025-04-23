package com.ryuukonpalace.game.creatures;

/**
 * Represents an ability that a creature can use in battle.
 */
public class Ability {
    
    private String name;
    private String description;
    private CreatureType type;
    private int power;
    private int accuracy;
    private int maxUses;
    private int currentUses;
    private EffectType effectType;
    private int effectChance;
    private DamageType damageType;
    
    /**
     * Constructor for creating a new ability
     * 
     * @param name Name of the ability
     * @param description Description of what the ability does
     * @param type Type of the ability (determines effectiveness)
     * @param power Base power of the ability
     * @param accuracy Chance to hit (0-100)
     * @param maxUses Maximum number of times the ability can be used
     * @param effectType Type of additional effect, if any
     * @param effectChance Chance for the additional effect to occur (0-100)
     * @param damageType Type of damage (physical or magical)
     */
    public Ability(String name, String description, CreatureType type, int power, int accuracy, int maxUses, 
                  EffectType effectType, int effectChance, DamageType damageType) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.power = power;
        this.accuracy = accuracy;
        this.maxUses = maxUses;
        this.currentUses = maxUses;
        this.effectType = effectType;
        this.effectChance = effectChance;
        this.damageType = damageType;
    }
    
    /**
     * Simplified constructor with default DamageType (PHYSICAL)
     */
    public Ability(String name, String description, CreatureType type, int power, int accuracy, int maxUses, 
                  EffectType effectType, int effectChance) {
        this(name, description, type, power, accuracy, maxUses, effectType, effectChance, DamageType.PHYSICAL);
    }
    
    /**
     * Check if the ability hits based on its accuracy
     * 
     * @return True if the ability hits, false if it misses
     */
    public boolean checkHit() {
        return Math.random() * 100 < accuracy;
    }
    
    /**
     * Check if the ability's additional effect activates
     * 
     * @return True if the effect activates, false otherwise
     */
    public boolean checkEffect() {
        return Math.random() * 100 < effectChance;
    }
    
    /**
     * Use the ability, reducing its current uses
     * 
     * @return True if the ability can be used, false if out of uses
     */
    public boolean use() {
        if (currentUses > 0) {
            currentUses--;
            return true;
        }
        return false;
    }
    
    /**
     * Restore all uses of the ability
     */
    public void restore() {
        currentUses = maxUses;
    }
    
    /**
     * Calculate the damage dealt by this ability
     * 
     * @param attackerLevel The level of the attacking creature
     * @param attackStat The attack stat of the attacking creature
     * @param defenseStat The defense stat of the defending creature
     * @param typeEffectiveness The type effectiveness multiplier
     * @return The amount of damage dealt
     */
    public int calculateDamage(int attackerLevel, int attackStat, int defenseStat, double typeEffectiveness) {
        // Base formula inspired by Pokémon's damage calculation
        double levelFactor = (2.0 * attackerLevel) / 5.0 + 2;
        double statFactor = (double) attackStat / defenseStat;
        double baseDamage = (levelFactor * power * statFactor) / 50.0 + 2;
        
        // Apply type effectiveness
        baseDamage *= typeEffectiveness;
        
        // Random factor (85-100%)
        double randomFactor = (Math.random() * 16 + 85) / 100.0;
        baseDamage *= randomFactor;
        
        return (int) baseDamage;
    }
    
    // Getters
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public CreatureType getType() {
        return type;
    }
    
    public int getPower() {
        return power;
    }
    
    public int getAccuracy() {
        return accuracy;
    }
    
    public int getMaxUses() {
        return maxUses;
    }
    
    public int getCurrentUses() {
        return currentUses;
    }
    
    public EffectType getEffectType() {
        return effectType;
    }
    
    public int getEffectChance() {
        return effectChance;
    }
    
    public DamageType getDamageType() {
        return damageType;
    }
    
    /**
     * Enum representing the different types of additional effects an ability can have
     */
    public enum EffectType {
        NONE,
        BURN,
        FREEZE,
        PARALYZE,
        POISON,
        SLEEP,
        CONFUSE,
        FLINCH,
        STAT_BOOST,
        STAT_REDUCE,
        HEAL,
        DRAIN
    }
    
    /**
     * Enum representing the different types of damage an ability can deal
     */
    public enum DamageType {
        PHYSICAL,   // Physical damage (based on physical attack and physical defense)
        MAGICAL,    // Magical damage (based on magical attack and magical defense)
        TRUE        // True damage (ignores defenses)
    }
}

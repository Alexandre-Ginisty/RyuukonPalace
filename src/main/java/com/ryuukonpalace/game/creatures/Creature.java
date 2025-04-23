package com.ryuukonpalace.game.creatures;

import com.ryuukonpalace.game.core.GameObject;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.utils.ResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all creatures in the game.
 * Similar to Pokémon, creatures can be captured, trained, and evolved.
 */
public class Creature extends GameObject {
    
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
    private int textureId;
    private Renderer renderer;
    private boolean active;
    private int friendship; // Niveau d'amitié avec le joueur (0-255)
    
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
        super(0, 0, 32, 48, "creature_" + id);
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
        this.active = false;
        this.friendship = 70; // Valeur d'amitié par défaut
        
        // Obtenir l'instance du renderer
        this.renderer = Renderer.getInstance();
        
        // Charger la texture de la créature
        ResourceManager resourceManager = ResourceManager.getInstance();
        CreatureFactory.CreatureDefinition definition = CreatureFactory.getInstance().getCreatureDefinition(id);
        if (definition != null) {
            this.textureId = resourceManager.loadTexture("src/main/resources/" + definition.spritePath, "creature_" + id);
        } else {
            // Texture par défaut si la définition n'est pas trouvée
            this.textureId = resourceManager.loadTexture("src/main/resources/images/unknown_creature.png", "unknown_creature");
        }
    }
    
    @Override
    public void update(float deltaTime) {
        // Animation ou comportement de la créature
        // Pour l'instant, rien à faire ici
    }
    
    @Override
    public void render() {
        // Dessiner la créature avec sa texture
        renderer.drawSprite(textureId, x, y, width, height);
    }
    
    @Override
    public void onCollision(GameObject other) {
        // Gérer les collisions
        // Pour l'instant, rien à faire ici
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
    
    /**
     * Définir si la créature est active ou non
     * 
     * @param active true si la créature est active, false sinon
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Vérifier si la créature est active
     * 
     * @return true si la créature est active, false sinon
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Obtenir l'ID de la texture de la créature
     * @return ID de la texture
     */
    public int getTextureId() {
        return textureId;
    }
    
    /**
     * Définir la santé actuelle de la créature
     * @param health Nouvelle valeur de santé
     */
    public void setHealth(int health) {
        this.health = Math.min(health, maxHealth);
        if (this.health <= 0) {
            this.health = 0;
            // La créature est vaincue, on pourrait déclencher un événement ici
        }
    }
    
    /**
     * Définir le niveau de la créature
     * 
     * @param level Nouveau niveau
     */
    public void setLevel(int level) {
        this.level = Math.max(1, level);
        
        // Recalculer les statistiques en fonction du nouveau niveau
        recalculateStats();
    }
    
    /**
     * Définir l'expérience de la créature
     * 
     * @param experience Nouvelle expérience
     */
    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
    }
    
    /**
     * Obtenir le niveau d'amitié de la créature
     * 
     * @return Niveau d'amitié (0-255)
     */
    public int getFriendship() {
        return friendship;
    }
    
    /**
     * Définir le niveau d'amitié de la créature
     * 
     * @param friendship Nouveau niveau d'amitié (0-255)
     */
    public void setFriendship(int friendship) {
        this.friendship = Math.max(0, Math.min(255, friendship));
    }
    
    /**
     * Augmenter le niveau d'amitié de la créature
     * 
     * @param amount Montant à ajouter
     */
    public void increaseFriendship(int amount) {
        this.friendship = Math.min(255, this.friendship + amount);
    }
    
    /**
     * Recalculer les statistiques en fonction du niveau
     */
    private void recalculateStats() {
        // Formule simple pour recalculer les statistiques
        // Ces formules peuvent être ajustées selon les besoins du jeu
        maxHealth = (int)(health * (1 + level * 0.1));
        attack = (int)(attack * (1 + level * 0.05));
        defense = (int)(defense * (1 + level * 0.05));
        speed = (int)(speed * (1 + level * 0.05));
        
        // S'assurer que la santé actuelle ne dépasse pas le nouveau maximum
        health = Math.min(health, maxHealth);
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
    
    public Evolution getEvolution() {
        return evolution;
    }
}

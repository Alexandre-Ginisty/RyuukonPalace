package com.ryuukonpalace.game.creatures;

import com.ryuukonpalace.game.combat.CombatStats;
import com.ryuukonpalace.game.world.GameObject;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.creatures.ai.CreatureAI;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.utils.ResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une créature dans le jeu
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
    private CreatureAI ai; // IA de la créature (pour les créatures sauvages)
    private boolean isWild; // Indique si la créature est sauvage
    private CombatStats combatStats; // Statistiques avancées pour le combat
    
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
        this.ai = null;
        this.isWild = false;
        
        // Initialiser les statistiques de combat avancées
        this.combatStats = new CombatStats(health, attack, attack/2, defense, defense/2, speed);
        
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
        // Si la créature a une IA et est active, mettre à jour son comportement
        if (ai != null && active) {
            // L'IA sera mise à jour par le système de jeu qui fournira le joueur
        }
    }
    
    /**
     * Mettre à jour l'IA de la créature avec la référence au joueur
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param player Le joueur (pour la détection)
     */
    public void updateAI(float deltaTime, Player player) {
        if (ai != null && active) {
            ai.update(deltaTime, player);
        }
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
        int oldLevel = this.level;
        this.experience += exp;
        
        // Utiliser le LevelSystem pour déterminer le niveau actuel
        LevelSystem levelSystem = LevelSystem.getInstance();
        int newLevel = levelSystem.getLevelFromExperience(this.experience);
        
        // Si le niveau a changé, appliquer les changements
        if (newLevel > oldLevel) {
            int levelsGained = newLevel - oldLevel;
            for (int i = 0; i < levelsGained; i++) {
                levelUp();
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Level up the creature, increasing its stats
     */
    private void levelUp() {
        level++;
        
        // Utiliser le LevelSystem pour obtenir les bonus de statistiques
        LevelSystem levelSystem = LevelSystem.getInstance();
        int[] statBonuses = levelSystem.getStatBonusesForLevel(id, level);
        
        // Appliquer les bonus
        maxHealth += statBonuses[0];
        health = maxHealth; // Restaurer la santé complète lors d'un niveau supérieur
        attack += statBonuses[1];
        defense += statBonuses[2];
        speed += statBonuses[3];
        
        System.out.println(name + " est passé au niveau " + level + " !");
        
        // Vérifier si la créature peut apprendre de nouvelles capacités à ce niveau
        checkNewAbilities();
        
        // Mettre à jour les statistiques de combat avancées
        updateCombatStatsFromBasicStats();
    }
    
    /**
     * Check if the creature can learn new abilities at its current level
     */
    private void checkNewAbilities() {
        LevelSystem levelSystem = LevelSystem.getInstance();
        Ability newAbility = levelSystem.getAbilityForLevel(id, level);
        
        if (newAbility != null) {
            // Ajouter la nouvelle capacité
            abilities.add(newAbility);
            System.out.println(name + " a appris " + newAbility.getName() + " !");
        }
    }
    
    /**
     * Take damage from an attack
     * 
     * @param damage The amount of damage to take
     * @return True if the creature fainted, false otherwise
     */
    public boolean takeDamage(int damage) {
        // Utiliser les statistiques de combat avancées
        combatStats.takeDamage(damage);
        
        // Mettre à jour les statistiques de base pour la compatibilité
        this.health = combatStats.getCurrentHealth();
        
        return health <= 0;
    }
    
    /**
     * Heal the creature
     * 
     * @param amount The amount to heal
     */
    public void heal(int amount) {
        // Utiliser les statistiques de combat avancées
        combatStats.heal(amount);
        
        // Mettre à jour les statistiques de base pour la compatibilité
        this.health = combatStats.getCurrentHealth();
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
        
        // Mettre à jour les statistiques de combat avancées
        combatStats.setCurrentHealth(this.health);
    }
    
    /**
     * Définir le niveau de la créature
     * 
     * @param level Nouveau niveau
     */
    public void setLevel(int level) {
        this.level = Math.max(1, level);
        
        // Utiliser le LevelSystem pour obtenir les statistiques pour le niveau
        LevelSystem levelSystem = LevelSystem.getInstance();
        int[] statBonuses = levelSystem.getStatBonusesForLevel(id, level);
        
        // Appliquer les bonus
        maxHealth += statBonuses[0];
        health = maxHealth; // Restaurer la santé complète lors d'un niveau supérieur
        attack += statBonuses[1];
        defense += statBonuses[2];
        speed += statBonuses[3];
        
        // Mettre à jour les statistiques de combat avancées
        updateCombatStatsFromBasicStats();
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
     * Définir l'IA de la créature
     * 
     * @param ai L'IA à utiliser pour cette créature
     */
    public void setAI(CreatureAI ai) {
        this.ai = ai;
    }
    
    /**
     * Obtenir l'IA de la créature
     * 
     * @return L'IA de la créature, ou null si elle n'en a pas
     */
    public CreatureAI getAI() {
        return ai;
    }
    
    /**
     * Définir si la créature est sauvage ou non
     * 
     * @param isWild true si la créature est sauvage, false sinon
     */
    public void setWild(boolean isWild) {
        this.isWild = isWild;
    }
    
    /**
     * Vérifier si la créature est sauvage
     * 
     * @return true si la créature est sauvage, false sinon
     */
    public boolean isWild() {
        return isWild;
    }
    
    /**
     * Obtenir les statistiques de combat avancées
     * 
     * @return Les statistiques de combat
     */
    public CombatStats getCombatStats() {
        return combatStats;
    }
    
    /**
     * Définir les statistiques de combat avancées
     * 
     * @param combatStats Les nouvelles statistiques de combat
     */
    public void setCombatStats(CombatStats combatStats) {
        this.combatStats = combatStats;
    }
    
    /**
     * Mettre à jour les statistiques de combat de base à partir des statistiques avancées
     * Cette méthode est utile pour maintenir la compatibilité avec le code existant
     */
    public void updateBasicStatsFromCombatStats() {
        this.health = combatStats.getCurrentHealth();
        this.maxHealth = combatStats.getMaxHealth();
        this.attack = combatStats.getPhysicalAttack();
        this.defense = combatStats.getPhysicalDefense();
        this.speed = combatStats.getSpeed();
    }
    
    /**
     * Mettre à jour les statistiques de combat avancées à partir des statistiques de base
     * Cette méthode est utile pour maintenir la compatibilité avec le code existant
     */
    public void updateCombatStatsFromBasicStats() {
        combatStats.setMaxHealth(this.maxHealth);
        combatStats.setCurrentHealth(this.health);
        combatStats.setPhysicalAttack(this.attack);
        combatStats.setPhysicalDefense(this.defense);
        combatStats.setSpeed(this.speed);
        // Pour les statistiques magiques, on utilise une valeur par défaut basée sur les stats physiques
        combatStats.setMagicalAttack(this.attack / 2);
        combatStats.setMagicalDefense(this.defense / 2);
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

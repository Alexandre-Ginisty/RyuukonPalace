package com.ryuukonpalace.game.faction;

import java.util.List;

/**
 * Représente une récompense de faction disponible pour le joueur.
 */
public class FactionReward {
    private String id;
    private String name;
    private String description;
    private String icon;
    private RewardType type;
    private int cost;
    private String requiredReputationLevel;
    private List<String> requiredQuests;
    private boolean unlocked;
    private boolean claimed;
    
    /**
     * Constructeur pour une récompense de faction.
     */
    public FactionReward(String id, String name, String description, String icon, 
                        RewardType type, int cost, String requiredReputationLevel,
                        List<String> requiredQuests) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.type = type;
        this.cost = cost;
        this.requiredReputationLevel = requiredReputationLevel;
        this.requiredQuests = requiredQuests;
        this.unlocked = false;
        this.claimed = false;
    }
    
    /**
     * Vérifie si cette récompense est disponible pour le joueur.
     */
    public boolean isAvailable(Faction faction, List<String> completedQuests) {
        // Vérifie le niveau de réputation
        if (!faction.hasReputationLevel(requiredReputationLevel)) {
            return false;
        }
        
        // Vérifie les quêtes requises
        for (String quest : requiredQuests) {
            if (!completedQuests.contains(quest)) {
                return false;
            }
        }
        
        return !claimed;
    }
    
    /**
     * Débloque cette récompense pour le joueur.
     */
    public void unlock() {
        this.unlocked = true;
    }
    
    /**
     * Réclame cette récompense pour le joueur.
     */
    public void claim() {
        if (unlocked && !claimed) {
            this.claimed = true;
            System.out.println("Récompense réclamée: " + name);
            // Ici, on pourrait ajouter la logique pour donner la récompense au joueur
        }
    }
    
    // Getters
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public RewardType getType() {
        return type;
    }
    
    public int getCost() {
        return cost;
    }
    
    public String getRequiredReputationLevel() {
        return requiredReputationLevel;
    }
    
    public List<String> getRequiredQuests() {
        return requiredQuests;
    }
    
    public boolean isUnlocked() {
        return unlocked;
    }
    
    public boolean isClaimed() {
        return claimed;
    }
    
    /**
     * Énumération des types de récompenses.
     */
    public enum RewardType {
        ITEM,
        VARIANT,
        ABILITY,
        OUTFIT,
        TITLE,
        ACCESS
    }
}

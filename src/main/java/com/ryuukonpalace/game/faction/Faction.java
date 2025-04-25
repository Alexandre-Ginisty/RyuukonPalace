package com.ryuukonpalace.game.faction;

import java.awt.Color;
import java.util.List;

/**
 * Représente une faction dans le monde de Ryuukon Palace.
 */
public class Faction {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String banner;
    private Color primaryColor;
    private Color secondaryColor;
    private List<String> centers;
    private List<String> leaders;
    private List<ReputationLevel> reputationLevels;
    private int playerReputation;
    
    /**
     * Constructeur pour une faction.
     */
    public Faction(String id, String name, String description, String icon, String banner, 
                  Color primaryColor, Color secondaryColor, List<String> centers, 
                  List<String> leaders, List<ReputationLevel> reputationLevels) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.banner = banner;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.centers = centers;
        this.leaders = leaders;
        this.reputationLevels = reputationLevels;
        this.playerReputation = 0; // Commence neutre
    }
    
    /**
     * Obtient le niveau de réputation actuel du joueur avec cette faction.
     */
    public ReputationLevel getCurrentReputationLevel() {
        ReputationLevel currentLevel = null;
        
        for (ReputationLevel level : reputationLevels) {
            if (playerReputation >= level.getThreshold() && 
                (currentLevel == null || level.getThreshold() > currentLevel.getThreshold())) {
                currentLevel = level;
            }
        }
        
        return currentLevel;
    }
    
    /**
     * Obtient le prochain niveau de réputation que le joueur peut atteindre.
     */
    public ReputationLevel getNextReputationLevel() {
        ReputationLevel currentLevel = getCurrentReputationLevel();
        ReputationLevel nextLevel = null;
        
        for (ReputationLevel level : reputationLevels) {
            if (level.getThreshold() > currentLevel.getThreshold() && 
                (nextLevel == null || level.getThreshold() < nextLevel.getThreshold())) {
                nextLevel = level;
            }
        }
        
        return nextLevel;
    }
    
    /**
     * Modifie la réputation du joueur avec cette faction.
     */
    public void changeReputation(int amount) {
        playerReputation += amount;
        System.out.println("Réputation avec " + name + " modifiée de " + amount + 
                          ". Nouvelle réputation: " + playerReputation);
    }
    
    /**
     * Vérifie si le joueur a au moins le niveau de réputation spécifié.
     */
    public boolean hasReputationLevel(String levelId) {
        ReputationLevel currentLevel = getCurrentReputationLevel();
        return currentLevel != null && currentLevel.getLevel().equals(levelId);
    }
    
    /**
     * Vérifie si le joueur a accès à un bénéfice spécifique.
     */
    public boolean hasBenefit(String benefit) {
        ReputationLevel currentLevel = getCurrentReputationLevel();
        return currentLevel != null && currentLevel.getBenefits().contains(benefit);
    }
    
    /**
     * Vérifie si le joueur a une restriction spécifique.
     */
    public boolean hasRestriction(String restriction) {
        ReputationLevel currentLevel = getCurrentReputationLevel();
        return currentLevel != null && currentLevel.getRestrictions().contains(restriction);
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
    
    public String getBanner() {
        return banner;
    }
    
    public Color getPrimaryColor() {
        return primaryColor;
    }
    
    public Color getSecondaryColor() {
        return secondaryColor;
    }
    
    public List<String> getCenters() {
        return centers;
    }
    
    public List<String> getLeaders() {
        return leaders;
    }
    
    public List<ReputationLevel> getReputationLevels() {
        return reputationLevels;
    }
    
    public int getPlayerReputation() {
        return playerReputation;
    }
}

package com.ryuukonpalace.game.faction;

import com.ryuukonpalace.game.utils.JsonLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire des factions du jeu.
 */
public class FactionManager {
    private static FactionManager instance;
    private Map<String, Faction> factions;
    private Map<String, List<FactionReward>> factionRewards;
    private List<String> completedQuests; // Temporaire, devrait être lié au système de quêtes
    
    /**
     * Constructeur privé pour le singleton.
     */
    private FactionManager() {
        factions = new HashMap<>();
        factionRewards = new HashMap<>();
        completedQuests = new ArrayList<>();
        loadFactions();
        loadRewards();
    }
    
    /**
     * Obtient l'instance unique du gestionnaire de factions.
     */
    public static FactionManager getInstance() {
        if (instance == null) {
            instance = new FactionManager();
        }
        return instance;
    }
    
    /**
     * Charge les factions depuis le fichier JSON.
     */
    private void loadFactions() {
        try {
            JSONObject config = JsonLoader.loadJsonFromFile("data/ui_faction_interface.json");
            JSONArray factionsArray = config.getJSONArray("factions");
            
            for (int i = 0; i < factionsArray.length(); i++) {
                JSONObject factionObj = factionsArray.getJSONObject(i);
                
                String id = factionObj.getString("id");
                String name = factionObj.getString("name");
                String description = factionObj.getString("description");
                String icon = factionObj.getString("icon");
                String banner = factionObj.getString("banner");
                
                // Couleurs
                JSONObject colors = factionObj.getJSONObject("colors");
                Color primaryColor = parseColor(colors.getString("primary"));
                Color secondaryColor = parseColor(colors.getString("secondary"));
                
                // Centres et leaders
                List<String> centers = jsonArrayToList(factionObj.getJSONArray("centers"));
                List<String> leaders = jsonArrayToList(factionObj.getJSONArray("leaders"));
                
                // Niveaux de réputation
                List<ReputationLevel> reputationLevels = new ArrayList<>();
                JSONArray levelsArray = factionObj.getJSONArray("reputationLevels");
                
                for (int j = 0; j < levelsArray.length(); j++) {
                    JSONObject levelObj = levelsArray.getJSONObject(j);
                    
                    String level = levelObj.getString("level");
                    int threshold = levelObj.getInt("threshold");
                    String levelName = levelObj.getString("name");
                    String levelDesc = levelObj.getString("description");
                    
                    List<String> benefits = jsonArrayToList(levelObj.getJSONArray("benefits"));
                    List<String> restrictions = jsonArrayToList(levelObj.getJSONArray("restrictions"));
                    
                    reputationLevels.add(new ReputationLevel(level, threshold, levelName, 
                                                           levelDesc, benefits, restrictions));
                }
                
                Faction faction = new Faction(id, name, description, icon, banner, 
                                            primaryColor, secondaryColor, centers, 
                                            leaders, reputationLevels);
                factions.put(id, faction);
            }
            
            System.out.println("Factions chargées: " + factions.size());
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des factions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge les récompenses des factions depuis le fichier JSON.
     */
    private void loadRewards() {
        try {
            JSONObject config = JsonLoader.loadJsonFromFile("data/ui_faction_interface.json");
            JSONArray rewardsArray = config.getJSONArray("rewards");
            
            for (int i = 0; i < rewardsArray.length(); i++) {
                JSONObject rewardObj = rewardsArray.getJSONObject(i);
                
                String id = rewardObj.getString("id");
                String name = rewardObj.getString("name");
                String description = rewardObj.getString("description");
                String icon = rewardObj.getString("icon");
                String typeStr = rewardObj.getString("type");
                FactionReward.RewardType type = FactionReward.RewardType.valueOf(typeStr.toUpperCase());
                int cost = rewardObj.getInt("cost");
                String factionId = rewardObj.getString("faction");
                String requiredLevel = rewardObj.getString("requiredReputationLevel");
                List<String> requiredQuests = jsonArrayToList(rewardObj.getJSONArray("requiredQuests"));
                
                FactionReward reward = new FactionReward(id, name, description, icon, 
                                                       type, cost, requiredLevel, requiredQuests);
                
                if (!factionRewards.containsKey(factionId)) {
                    factionRewards.put(factionId, new ArrayList<>());
                }
                factionRewards.get(factionId).add(reward);
            }
            
            System.out.println("Récompenses chargées pour " + factionRewards.size() + " factions");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des récompenses: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Convertit une chaîne de couleur hexadécimale en objet Color.
     */
    private Color parseColor(String colorStr) {
        if (colorStr.startsWith("#")) {
            colorStr = colorStr.substring(1);
        }
        
        int r = Integer.parseInt(colorStr.substring(0, 2), 16);
        int g = Integer.parseInt(colorStr.substring(2, 4), 16);
        int b = Integer.parseInt(colorStr.substring(4, 6), 16);
        
        return new Color(r, g, b);
    }
    
    /**
     * Convertit un JSONArray en List<String>.
     */
    private List<String> jsonArrayToList(JSONArray array) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }
    
    /**
     * Obtient une faction par son ID.
     */
    public Faction getFaction(String factionId) {
        return factions.get(factionId);
    }
    
    /**
     * Obtient toutes les factions.
     */
    public List<Faction> getAllFactions() {
        return new ArrayList<>(factions.values());
    }
    
    /**
     * Obtient les récompenses disponibles pour une faction.
     */
    public List<FactionReward> getRewardsForFaction(String factionId) {
        return factionRewards.getOrDefault(factionId, new ArrayList<>());
    }
    
    /**
     * Obtient les récompenses disponibles pour une faction, filtrées par disponibilité.
     */
    public List<FactionReward> getAvailableRewardsForFaction(String factionId) {
        List<FactionReward> available = new ArrayList<>();
        Faction faction = getFaction(factionId);
        
        if (faction != null) {
            List<FactionReward> rewards = getRewardsForFaction(factionId);
            for (FactionReward reward : rewards) {
                if (reward.isAvailable(faction, completedQuests)) {
                    available.add(reward);
                }
            }
        }
        
        return available;
    }
    
    /**
     * Change la réputation du joueur avec une faction.
     */
    public void changeReputation(String factionId, int amount) {
        Faction faction = getFaction(factionId);
        if (faction != null) {
            faction.changeReputation(amount);
        }
    }
    
    /**
     * Ajoute une quête complétée à la liste.
     */
    public void addCompletedQuest(String questId) {
        if (!completedQuests.contains(questId)) {
            completedQuests.add(questId);
        }
    }
}

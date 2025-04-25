package com.ryuukonpalace.game.quest.objectives;

import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.quest.QuestObjective;
import com.ryuukonpalace.game.quest.QuestObjectiveState;

/**
 * Objectif de quête consistant à explorer une zone spécifique.
 */
public class ExploreAreaObjective extends QuestObjective {
    
    // ID de la zone à explorer
    private String areaId;
    
    // Nom de la zone à explorer (pour l'affichage)
    private String areaName;
    
    // Coordonnées de la zone à explorer
    private float minX, minY, maxX, maxY;
    
    // Temps minimum à passer dans la zone (en secondes, 0 = pas de minimum)
    private float minTimeInArea;
    
    // Temps passé dans la zone
    private float timeInArea;
    
    // Indique si le joueur est actuellement dans la zone
    private boolean playerInArea;
    
    // Conditions environnementales
    private boolean requiresDaytime;
    private boolean requiresNighttime;
    private boolean requiresSpecificWeather;
    private String requiredWeather;
    
    /**
     * Constructeur
     * 
     * @param id Identifiant unique de l'objectif
     * @param areaId ID de la zone à explorer
     * @param areaName Nom de la zone à explorer
     * @param minX Coordonnée X minimale de la zone
     * @param minY Coordonnée Y minimale de la zone
     * @param maxX Coordonnée X maximale de la zone
     * @param maxY Coordonnée Y maximale de la zone
     */
    public ExploreAreaObjective(String id, String areaId, String areaName, float minX, float minY, float maxX, float maxY) {
        super(id, "Explorer " + areaName);
        this.areaId = areaId;
        this.areaName = areaName;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.minTimeInArea = 0;
        this.timeInArea = 0;
        this.playerInArea = false;
        this.requiresDaytime = false;
        this.requiresNighttime = false;
        this.requiresSpecificWeather = false;
        this.requiredWeather = "";
    }
    
    /**
     * Constructeur avec temps minimum
     * 
     * @param id Identifiant unique de l'objectif
     * @param areaId ID de la zone à explorer
     * @param areaName Nom de la zone à explorer
     * @param minX Coordonnée X minimale de la zone
     * @param minY Coordonnée Y minimale de la zone
     * @param maxX Coordonnée X maximale de la zone
     * @param maxY Coordonnée Y maximale de la zone
     * @param minTimeInArea Temps minimum à passer dans la zone (en secondes)
     */
    public ExploreAreaObjective(String id, String areaId, String areaName, float minX, float minY, float maxX, float maxY, float minTimeInArea) {
        this(id, areaId, areaName, minX, minY, maxX, maxY);
        this.minTimeInArea = minTimeInArea;
        
        if (minTimeInArea > 0) {
            this.description = "Explorer " + areaName + " pendant " + (int)minTimeInArea + " secondes";
        }
    }
    
    /**
     * Définir si l'objectif nécessite qu'il fasse jour
     * 
     * @param requiresDaytime true si l'objectif nécessite qu'il fasse jour, false sinon
     */
    public void setRequiresDaytime(boolean requiresDaytime) {
        this.requiresDaytime = requiresDaytime;
        if (requiresDaytime) {
            this.requiresNighttime = false;
        }
    }
    
    /**
     * Définir si l'objectif nécessite qu'il fasse nuit
     * 
     * @param requiresNighttime true si l'objectif nécessite qu'il fasse nuit, false sinon
     */
    public void setRequiresNighttime(boolean requiresNighttime) {
        this.requiresNighttime = requiresNighttime;
        if (requiresNighttime) {
            this.requiresDaytime = false;
        }
    }
    
    /**
     * Définir si l'objectif nécessite une météo spécifique
     * 
     * @param requiresSpecificWeather true si l'objectif nécessite une météo spécifique, false sinon
     * @param requiredWeather Météo requise
     */
    public void setRequiresSpecificWeather(boolean requiresSpecificWeather, String requiredWeather) {
        this.requiresSpecificWeather = requiresSpecificWeather;
        this.requiredWeather = requiredWeather;
    }
    
    /**
     * Définir si l'objectif nécessite qu'il fasse jour
     * 
     * @param requireDaytime true si l'objectif nécessite qu'il fasse jour, false sinon
     */
    public void setRequireDaytime(boolean requireDaytime) {
        setRequiresDaytime(requireDaytime);
    }
    
    /**
     * Définir si l'objectif nécessite qu'il fasse nuit
     * 
     * @param requireNighttime true si l'objectif nécessite qu'il fasse nuit, false sinon
     */
    public void setRequireNighttime(boolean requireNighttime) {
        setRequiresNighttime(requireNighttime);
    }
    
    /**
     * Définir la météo requise pour l'objectif
     * 
     * @param weather Type de météo requise
     */
    public void setRequireWeather(String weather) {
        if (weather != null && !weather.isEmpty()) {
            setRequiresSpecificWeather(true, weather);
        } else {
            setRequiresSpecificWeather(false, "");
        }
    }
    
    /**
     * Vérifier si l'objectif nécessite qu'il fasse jour
     * 
     * @return true si l'objectif nécessite qu'il fasse jour, false sinon
     */
    public boolean requiresDaytime() {
        return requiresDaytime;
    }
    
    /**
     * Vérifier si l'objectif nécessite qu'il fasse nuit
     * 
     * @return true si l'objectif nécessite qu'il fasse nuit, false sinon
     */
    public boolean requiresNighttime() {
        return requiresNighttime;
    }
    
    /**
     * Vérifier si l'objectif nécessite une météo spécifique
     * 
     * @return true si l'objectif nécessite une météo spécifique, false sinon
     */
    public boolean requiresSpecificWeather() {
        return requiresSpecificWeather;
    }
    
    /**
     * Obtenir la météo requise
     * 
     * @return Météo requise
     */
    public String getRequiredWeather() {
        return requiredWeather;
    }
    
    @Override
    public boolean update(float deltaTime, Player player) {
        if (state != QuestObjectiveState.IN_PROGRESS) {
            return false;
        }
        
        // Les vérifications des conditions environnementales sont maintenant gérées par QuestManager
        
        // Vérifier si le joueur est dans la zone
        boolean wasInArea = playerInArea;
        playerInArea = isPlayerInArea(player);
        
        // Si le joueur vient d'entrer dans la zone
        if (playerInArea && !wasInArea) {
            // Ajouter la zone aux zones découvertes du joueur
            player.addDiscoveredArea(areaId);
            
            // Si aucun temps minimum n'est requis, compléter l'objectif immédiatement
            if (minTimeInArea <= 0) {
                complete();
                return true;
            }
        }
        
        // Si le joueur est dans la zone, mettre à jour le temps passé
        if (playerInArea) {
            timeInArea += deltaTime;
            
            // Mettre à jour la progression
            currentAmount = (int)timeInArea;
            requiredAmount = (int)minTimeInArea;
            
            // Vérifier si le temps minimum est atteint
            if (timeInArea >= minTimeInArea) {
                complete();
                return true;
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Mettre à jour l'exploration de la zone
     * 
     * @param player Joueur
     * @return true si l'objectif a été mis à jour, false sinon
     */
    public boolean updateExploration(Player player) {
        if (state != QuestObjectiveState.IN_PROGRESS) {
            return false;
        }
        
        // Ajouter la zone aux zones découvertes du joueur
        player.addDiscoveredArea(areaId);
        
        // Si aucun temps minimum n'est requis, compléter l'objectif immédiatement
        if (minTimeInArea <= 0) {
            complete();
            return true;
        }
        
        // Mettre à jour le temps passé (on considère qu'on ajoute 1 seconde)
        timeInArea += 1.0f;
        
        // Mettre à jour la progression
        currentAmount = (int)timeInArea;
        requiredAmount = (int)minTimeInArea;
        
        // Vérifier si le temps minimum est atteint
        if (timeInArea >= minTimeInArea) {
            complete();
            return true;
        }
        
        return true;
    }
    
    /**
     * Vérifier si le joueur est dans la zone
     * 
     * @param player Joueur
     * @return true si le joueur est dans la zone, false sinon
     */
    private boolean isPlayerInArea(Player player) {
        float playerX = player.getX();
        float playerY = player.getY();
        
        return playerX >= minX && playerX <= maxX && playerY >= minY && playerY <= maxY;
    }
    
    /**
     * Obtenir l'ID de la zone à explorer
     * 
     * @return ID de la zone
     */
    public String getAreaId() {
        return areaId;
    }
    
    /**
     * Obtenir le nom de la zone à explorer
     * 
     * @return Nom de la zone
     */
    public String getAreaName() {
        return areaName;
    }
    
    /**
     * Obtenir le temps minimum à passer dans la zone
     * 
     * @return Temps minimum en secondes
     */
    public float getMinTimeInArea() {
        return minTimeInArea;
    }
    
    /**
     * Obtenir le temps passé dans la zone
     * 
     * @return Temps passé en secondes
     */
    public float getTimeInArea() {
        return timeInArea;
    }
    
    @Override
    public float getProgress() {
        if (state == QuestObjectiveState.COMPLETED) {
            return 1.0f;
        } else if (state == QuestObjectiveState.FAILED) {
            return 0.0f;
        }
        
        if (minTimeInArea <= 0) {
            return playerInArea ? 1.0f : 0.0f;
        }
        
        return Math.min(1.0f, timeInArea / minTimeInArea);
    }
    
    @Override
    public String getFormattedDescription() {
        if (minTimeInArea > 0) {
            return description + " (" + (int)timeInArea + "/" + (int)minTimeInArea + " sec)";
        } else {
            return description;
        }
    }
}

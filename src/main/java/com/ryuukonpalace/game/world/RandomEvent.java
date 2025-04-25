package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.world.RandomEventSystem.EventType;
import com.ryuukonpalace.game.world.RandomEventSystem.RandomEventAction;
import com.ryuukonpalace.game.world.TimeSystem.TimeOfDay;
import com.ryuukonpalace.game.world.WeatherSystem.Weather;

import java.util.HashMap;
import java.util.Map;

/**
 * Représente un événement aléatoire qui peut se produire dans le jeu.
 * Les événements peuvent être déclenchés en fonction de diverses conditions
 * comme le temps, la météo, la zone, etc.
 */
public class RandomEvent {
    
    // Identifiant unique de l'événement
    private String id;
    
    // Nom de l'événement
    private String name;
    
    // Description de l'événement
    private String description;
    
    // Type d'événement
    private EventType type;
    
    // Probabilité de base de l'événement (0.0 - 1.0)
    private float baseProbability;
    
    // Niveau minimum du joueur requis
    private int minPlayerLevel;
    
    // Conditions de temps (moment de la journée -> modificateur de probabilité)
    private Map<TimeOfDay, Float> timeConditions;
    
    // Conditions de météo (météo -> modificateur de probabilité)
    private Map<Weather, Float> weatherConditions;
    
    // Conditions de zone (nom de zone -> modificateur de probabilité)
    private Map<String, Float> zoneConditions;
    
    // Action à exécuter lorsque l'événement est déclenché
    private RandomEventAction action;
    
    // Durée de l'événement en secondes (0 = instantané)
    private float duration;
    
    // Temps écoulé depuis le déclenchement de l'événement
    private float elapsedTime;
    
    // Indique si l'événement est terminé
    private boolean finished;
    
    /**
     * Constructeur
     * @param id Identifiant unique de l'événement
     * @param name Nom de l'événement
     * @param description Description de l'événement
     * @param type Type d'événement
     * @param baseProbability Probabilité de base de l'événement (0.0 - 1.0)
     */
    public RandomEvent(String id, String name, String description, EventType type, float baseProbability) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.baseProbability = Math.max(0.0f, Math.min(1.0f, baseProbability));
        this.minPlayerLevel = 1;
        this.timeConditions = new HashMap<>();
        this.weatherConditions = new HashMap<>();
        this.zoneConditions = new HashMap<>();
        this.duration = 0.0f;
        this.elapsedTime = 0.0f;
        this.finished = false;
    }
    
    /**
     * Mettre à jour l'événement
     * @param deltaTime Temps écoulé depuis la dernière mise à jour en secondes
     */
    public void update(float deltaTime) {
        // Si l'événement est déjà terminé ou instantané, ne rien faire
        if (finished || duration <= 0.0f) {
            finished = true;
            return;
        }
        
        // Mettre à jour le temps écoulé
        elapsedTime += deltaTime;
        
        // Vérifier si l'événement est terminé
        if (elapsedTime >= duration) {
            finished = true;
        }
    }
    
    /**
     * Ajouter une condition de temps
     * @param timeOfDay Moment de la journée
     * @param probabilityModifier Modificateur de probabilité
     */
    public void addTimeCondition(TimeOfDay timeOfDay, float probabilityModifier) {
        timeConditions.put(timeOfDay, probabilityModifier);
    }
    
    /**
     * Ajouter une condition de météo
     * @param weather Météo
     * @param probabilityModifier Modificateur de probabilité
     */
    public void addWeatherCondition(Weather weather, float probabilityModifier) {
        weatherConditions.put(weather, probabilityModifier);
    }
    
    /**
     * Ajouter une condition de zone
     * @param zoneName Nom de la zone
     * @param probabilityModifier Modificateur de probabilité
     */
    public void addZoneCondition(String zoneName, float probabilityModifier) {
        zoneConditions.put(zoneName, probabilityModifier);
    }
    
    /**
     * Obtenir l'identifiant de l'événement
     * @return Identifiant de l'événement
     */
    public String getId() {
        return id;
    }
    
    /**
     * Obtenir le nom de l'événement
     * @return Nom de l'événement
     */
    public String getName() {
        return name;
    }
    
    /**
     * Obtenir la description de l'événement
     * @return Description de l'événement
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtenir le type d'événement
     * @return Type d'événement
     */
    public EventType getType() {
        return type;
    }
    
    /**
     * Obtenir la probabilité de base de l'événement
     * @return Probabilité de base (0.0 - 1.0)
     */
    public float getBaseProbability() {
        return baseProbability;
    }
    
    /**
     * Obtenir le niveau minimum du joueur requis
     * @return Niveau minimum du joueur
     */
    public int getMinPlayerLevel() {
        return minPlayerLevel;
    }
    
    /**
     * Définir le niveau minimum du joueur requis
     * @param minPlayerLevel Niveau minimum du joueur
     */
    public void setMinPlayerLevel(int minPlayerLevel) {
        this.minPlayerLevel = Math.max(1, minPlayerLevel);
    }
    
    /**
     * Obtenir les conditions de temps
     * @return Map des conditions de temps
     */
    public Map<TimeOfDay, Float> getTimeConditions() {
        return new HashMap<>(timeConditions);
    }
    
    /**
     * Obtenir les conditions de météo
     * @return Map des conditions de météo
     */
    public Map<Weather, Float> getWeatherConditions() {
        return new HashMap<>(weatherConditions);
    }
    
    /**
     * Obtenir les conditions de zone
     * @return Map des conditions de zone
     */
    public Map<String, Float> getZoneConditions() {
        return new HashMap<>(zoneConditions);
    }
    
    /**
     * Obtenir l'action de l'événement
     * @return Action de l'événement
     */
    public RandomEventAction getAction() {
        return action;
    }
    
    /**
     * Définir l'action de l'événement
     * @param action Action à exécuter lorsque l'événement est déclenché
     */
    public void setAction(RandomEventAction action) {
        this.action = action;
    }
    
    /**
     * Obtenir la durée de l'événement
     * @return Durée en secondes
     */
    public float getDuration() {
        return duration;
    }
    
    /**
     * Définir la durée de l'événement
     * @param duration Durée en secondes (0 = instantané)
     */
    public void setDuration(float duration) {
        this.duration = Math.max(0.0f, duration);
    }
    
    /**
     * Vérifier si l'événement est terminé
     * @return true si l'événement est terminé, false sinon
     */
    public boolean isFinished() {
        return finished || duration <= 0.0f;
    }
    
    /**
     * Réinitialiser l'événement
     */
    public void reset() {
        elapsedTime = 0.0f;
        finished = false;
    }
    
    @Override
    public String toString() {
        return name + " (" + type + "): " + description;
    }
}

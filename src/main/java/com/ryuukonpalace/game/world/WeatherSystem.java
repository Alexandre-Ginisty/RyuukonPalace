package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.creatures.CreatureType;
import com.ryuukonpalace.game.utils.ResourceManager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Système de gestion de la météo dans le jeu.
 * Gère les différentes conditions météorologiques et leurs effets sur le gameplay.
 */
public class WeatherSystem {
    
    // Singleton instance
    private static WeatherSystem instance;
    
    // Météo actuelle
    private Weather currentWeather;
    
    // Durée de la météo actuelle en secondes de jeu
    private float currentWeatherDuration;
    
    // Durée minimale et maximale d'une condition météo (en secondes de jeu)
    private static final float MIN_WEATHER_DURATION = 300.0f; // 5 minutes
    private static final float MAX_WEATHER_DURATION = 1200.0f; // 20 minutes
    
    // Probabilités de transition entre les différentes météos
    private Map<Weather, Map<Weather, Float>> weatherTransitionProbabilities;
    
    // Modificateurs de spawn selon la météo
    private Map<Weather, Map<CreatureType, Float>> weatherSpawnModifiers;
    
    // Générateur de nombres aléatoires
    private Random random;
    
    // Callback pour les événements liés à la météo
    private WeatherEventCallback weatherEventCallback;
    
    // IDs des textures d'effets météorologiques
    private int rainTextureId;
    private int snowTextureId;
    private int fogTextureId;
    private int lightningTextureId;
    
    // Intensité des effets météorologiques (0.0 - 1.0)
    private float effectIntensity;
    
    /**
     * Constructeur privé pour le singleton
     */
    private WeatherSystem() {
        this.currentWeather = Weather.CLEAR;
        this.currentWeatherDuration = 0.0f;
        this.random = new Random();
        this.effectIntensity = 0.0f;
        
        // Charger les textures d'effets météorologiques
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.rainTextureId = resourceManager.loadTexture("src/main/resources/images/weather/rain.png", "rain");
        this.snowTextureId = resourceManager.loadTexture("src/main/resources/images/weather/snow.png", "snow");
        this.fogTextureId = resourceManager.loadTexture("src/main/resources/images/weather/fog.png", "fog");
        this.lightningTextureId = resourceManager.loadTexture("src/main/resources/images/weather/lightning.png", "lightning");
        
        // Initialiser les probabilités de transition
        initializeWeatherTransitionProbabilities();
        
        // Initialiser les modificateurs de spawn
        initializeWeatherSpawnModifiers();
    }
    
    /**
     * Obtenir l'instance unique du système de météo
     * @return L'instance du WeatherSystem
     */
    public static WeatherSystem getInstance() {
        if (instance == null) {
            instance = new WeatherSystem();
        }
        return instance;
    }
    
    /**
     * Initialiser les probabilités de transition entre les différentes météos
     */
    private void initializeWeatherTransitionProbabilities() {
        weatherTransitionProbabilities = new HashMap<>();
        
        // Transitions depuis CLEAR (Dégagé)
        Map<Weather, Float> clearTransitions = new HashMap<>();
        clearTransitions.put(Weather.CLEAR, 0.5f);
        clearTransitions.put(Weather.CLOUDY, 0.3f);
        clearTransitions.put(Weather.SUNNY, 0.2f);
        clearTransitions.put(Weather.RAINY, 0.0f);
        clearTransitions.put(Weather.STORMY, 0.0f);
        clearTransitions.put(Weather.FOGGY, 0.0f);
        clearTransitions.put(Weather.SNOWY, 0.0f);
        weatherTransitionProbabilities.put(Weather.CLEAR, clearTransitions);
        
        // Transitions depuis CLOUDY (Nuageux)
        Map<Weather, Float> cloudyTransitions = new HashMap<>();
        cloudyTransitions.put(Weather.CLEAR, 0.3f);
        cloudyTransitions.put(Weather.CLOUDY, 0.3f);
        cloudyTransitions.put(Weather.SUNNY, 0.1f);
        cloudyTransitions.put(Weather.RAINY, 0.2f);
        cloudyTransitions.put(Weather.STORMY, 0.05f);
        cloudyTransitions.put(Weather.FOGGY, 0.05f);
        cloudyTransitions.put(Weather.SNOWY, 0.0f);
        weatherTransitionProbabilities.put(Weather.CLOUDY, cloudyTransitions);
        
        // Transitions depuis SUNNY (Ensoleillé)
        Map<Weather, Float> sunnyTransitions = new HashMap<>();
        sunnyTransitions.put(Weather.CLEAR, 0.4f);
        sunnyTransitions.put(Weather.CLOUDY, 0.3f);
        sunnyTransitions.put(Weather.SUNNY, 0.3f);
        sunnyTransitions.put(Weather.RAINY, 0.0f);
        sunnyTransitions.put(Weather.STORMY, 0.0f);
        sunnyTransitions.put(Weather.FOGGY, 0.0f);
        sunnyTransitions.put(Weather.SNOWY, 0.0f);
        weatherTransitionProbabilities.put(Weather.SUNNY, sunnyTransitions);
        
        // Transitions depuis RAINY (Pluvieux)
        Map<Weather, Float> rainyTransitions = new HashMap<>();
        rainyTransitions.put(Weather.CLEAR, 0.1f);
        rainyTransitions.put(Weather.CLOUDY, 0.4f);
        rainyTransitions.put(Weather.SUNNY, 0.0f);
        rainyTransitions.put(Weather.RAINY, 0.3f);
        rainyTransitions.put(Weather.STORMY, 0.2f);
        rainyTransitions.put(Weather.FOGGY, 0.0f);
        rainyTransitions.put(Weather.SNOWY, 0.0f);
        weatherTransitionProbabilities.put(Weather.RAINY, rainyTransitions);
        
        // Transitions depuis STORMY (Orageux)
        Map<Weather, Float> stormyTransitions = new HashMap<>();
        stormyTransitions.put(Weather.CLEAR, 0.05f);
        stormyTransitions.put(Weather.CLOUDY, 0.3f);
        stormyTransitions.put(Weather.SUNNY, 0.0f);
        stormyTransitions.put(Weather.RAINY, 0.4f);
        stormyTransitions.put(Weather.STORMY, 0.25f);
        stormyTransitions.put(Weather.FOGGY, 0.0f);
        stormyTransitions.put(Weather.SNOWY, 0.0f);
        weatherTransitionProbabilities.put(Weather.STORMY, stormyTransitions);
        
        // Transitions depuis FOGGY (Brumeux)
        Map<Weather, Float> foggyTransitions = new HashMap<>();
        foggyTransitions.put(Weather.CLEAR, 0.2f);
        foggyTransitions.put(Weather.CLOUDY, 0.3f);
        foggyTransitions.put(Weather.SUNNY, 0.1f);
        foggyTransitions.put(Weather.RAINY, 0.1f);
        foggyTransitions.put(Weather.STORMY, 0.0f);
        foggyTransitions.put(Weather.FOGGY, 0.3f);
        foggyTransitions.put(Weather.SNOWY, 0.0f);
        weatherTransitionProbabilities.put(Weather.FOGGY, foggyTransitions);
        
        // Transitions depuis SNOWY (Neigeux)
        Map<Weather, Float> snowyTransitions = new HashMap<>();
        snowyTransitions.put(Weather.CLEAR, 0.2f);
        snowyTransitions.put(Weather.CLOUDY, 0.3f);
        snowyTransitions.put(Weather.SUNNY, 0.1f);
        snowyTransitions.put(Weather.RAINY, 0.0f);
        snowyTransitions.put(Weather.STORMY, 0.0f);
        snowyTransitions.put(Weather.FOGGY, 0.1f);
        snowyTransitions.put(Weather.SNOWY, 0.3f);
        weatherTransitionProbabilities.put(Weather.SNOWY, snowyTransitions);
    }
    
    /**
     * Initialiser les modificateurs de spawn selon la météo
     */
    private void initializeWeatherSpawnModifiers() {
        weatherSpawnModifiers = new HashMap<>();
        
        // Modificateurs pour CLEAR (Dégagé)
        Map<CreatureType, Float> clearModifiers = new HashMap<>();
        clearModifiers.put(CreatureType.FIRE, 1.0f);      // Normal pour les créatures de feu
        clearModifiers.put(CreatureType.WATER, 0.8f);     // Moins de créatures d'eau
        clearModifiers.put(CreatureType.EARTH, 1.0f);     // Normal pour les créatures de terre
        clearModifiers.put(CreatureType.AIR, 1.2f);       // Plus de créatures d'air
        clearModifiers.put(CreatureType.LIGHT, 1.2f);     // Plus de créatures de lumière
        clearModifiers.put(CreatureType.SHADOW, 0.8f);    // Moins de créatures d'ombre
        weatherSpawnModifiers.put(Weather.CLEAR, clearModifiers);
        
        // Modificateurs pour CLOUDY (Nuageux)
        Map<CreatureType, Float> cloudyModifiers = new HashMap<>();
        cloudyModifiers.put(CreatureType.FIRE, 0.9f);     // Légèrement moins de créatures de feu
        cloudyModifiers.put(CreatureType.WATER, 1.0f);    // Normal pour les créatures d'eau
        cloudyModifiers.put(CreatureType.EARTH, 1.0f);    // Normal pour les créatures de terre
        cloudyModifiers.put(CreatureType.AIR, 1.1f);      // Légèrement plus de créatures d'air
        cloudyModifiers.put(CreatureType.LIGHT, 0.9f);    // Légèrement moins de créatures de lumière
        cloudyModifiers.put(CreatureType.SHADOW, 1.1f);   // Légèrement plus de créatures d'ombre
        weatherSpawnModifiers.put(Weather.CLOUDY, cloudyModifiers);
        
        // Modificateurs pour SUNNY (Ensoleillé)
        Map<CreatureType, Float> sunnyModifiers = new HashMap<>();
        sunnyModifiers.put(CreatureType.FIRE, 1.5f);      // Beaucoup plus de créatures de feu
        sunnyModifiers.put(CreatureType.WATER, 0.6f);     // Beaucoup moins de créatures d'eau
        sunnyModifiers.put(CreatureType.EARTH, 0.8f);     // Moins de créatures de terre
        sunnyModifiers.put(CreatureType.AIR, 1.2f);       // Plus de créatures d'air
        sunnyModifiers.put(CreatureType.LIGHT, 1.5f);     // Beaucoup plus de créatures de lumière
        sunnyModifiers.put(CreatureType.SHADOW, 0.5f);    // Beaucoup moins de créatures d'ombre
        weatherSpawnModifiers.put(Weather.SUNNY, sunnyModifiers);
        
        // Modificateurs pour RAINY (Pluvieux)
        Map<CreatureType, Float> rainyModifiers = new HashMap<>();
        rainyModifiers.put(CreatureType.FIRE, 0.5f);      // Beaucoup moins de créatures de feu
        rainyModifiers.put(CreatureType.WATER, 1.5f);     // Beaucoup plus de créatures d'eau
        rainyModifiers.put(CreatureType.EARTH, 1.2f);     // Plus de créatures de terre
        rainyModifiers.put(CreatureType.AIR, 0.8f);       // Moins de créatures d'air
        rainyModifiers.put(CreatureType.LIGHT, 0.7f);     // Moins de créatures de lumière
        rainyModifiers.put(CreatureType.SHADOW, 1.2f);    // Plus de créatures d'ombre
        weatherSpawnModifiers.put(Weather.RAINY, rainyModifiers);
        
        // Modificateurs pour STORMY (Orageux)
        Map<CreatureType, Float> stormyModifiers = new HashMap<>();
        stormyModifiers.put(CreatureType.FIRE, 0.3f);     // Beaucoup moins de créatures de feu
        stormyModifiers.put(CreatureType.WATER, 1.3f);    // Plus de créatures d'eau
        stormyModifiers.put(CreatureType.EARTH, 0.8f);    // Moins de créatures de terre
        stormyModifiers.put(CreatureType.AIR, 1.0f);      // Normal pour les créatures d'air
        stormyModifiers.put(CreatureType.LIGHT, 0.5f);    // Beaucoup moins de créatures de lumière
        stormyModifiers.put(CreatureType.SHADOW, 1.5f);   // Beaucoup plus de créatures d'ombre
        stormyModifiers.put(CreatureType.ELECTRIC, 2.0f); // Énormément plus de créatures électriques
        weatherSpawnModifiers.put(Weather.STORMY, stormyModifiers);
        
        // Modificateurs pour FOGGY (Brumeux)
        Map<CreatureType, Float> foggyModifiers = new HashMap<>();
        foggyModifiers.put(CreatureType.FIRE, 0.7f);      // Moins de créatures de feu
        foggyModifiers.put(CreatureType.WATER, 1.2f);     // Plus de créatures d'eau
        foggyModifiers.put(CreatureType.EARTH, 0.9f);     // Légèrement moins de créatures de terre
        foggyModifiers.put(CreatureType.AIR, 0.7f);       // Moins de créatures d'air
        foggyModifiers.put(CreatureType.LIGHT, 0.6f);     // Beaucoup moins de créatures de lumière
        foggyModifiers.put(CreatureType.SHADOW, 1.4f);    // Plus de créatures d'ombre
        foggyModifiers.put(CreatureType.PSYCHIC, 1.3f);   // Plus de créatures psychiques
        weatherSpawnModifiers.put(Weather.FOGGY, foggyModifiers);
        
        // Modificateurs pour SNOWY (Neigeux)
        Map<CreatureType, Float> snowyModifiers = new HashMap<>();
        snowyModifiers.put(CreatureType.FIRE, 0.4f);      // Beaucoup moins de créatures de feu
        snowyModifiers.put(CreatureType.WATER, 0.8f);     // Moins de créatures d'eau
        snowyModifiers.put(CreatureType.EARTH, 0.7f);     // Moins de créatures de terre
        snowyModifiers.put(CreatureType.AIR, 0.9f);       // Légèrement moins de créatures d'air
        snowyModifiers.put(CreatureType.LIGHT, 1.0f);     // Normal pour les créatures de lumière
        snowyModifiers.put(CreatureType.SHADOW, 1.0f);    // Normal pour les créatures d'ombre
        snowyModifiers.put(CreatureType.ICE, 2.0f);       // Énormément plus de créatures de glace
        weatherSpawnModifiers.put(Weather.SNOWY, snowyModifiers);
    }
    
    /**
     * Mettre à jour la météo
     * @param deltaTime Temps écoulé depuis la dernière frame en secondes
     */
    public void update(float deltaTime) {
        // Mettre à jour la durée de la météo actuelle
        currentWeatherDuration += deltaTime;
        
        // Vérifier si la météo doit changer
        if (currentWeatherDuration >= getRandomWeatherDuration()) {
            changeWeather();
        }
        
        // Mettre à jour l'intensité des effets météorologiques
        updateEffectIntensity(deltaTime);
        
        // Mettre à jour la météo dans le WorldManager
        WorldManager.getInstance().setCurrentWeather(currentWeather);
        
        // Appliquer les effets visuels de la météo
        applyWeatherVisualEffects();
    }
    
    /**
     * Mettre à jour l'intensité des effets météorologiques
     * @param deltaTime Temps écoulé depuis la dernière frame en secondes
     */
    private void updateEffectIntensity(float deltaTime) {
        // L'intensité varie légèrement au fil du temps pour un effet plus naturel
        float intensityChange = (random.nextFloat() * 2.0f - 1.0f) * 0.05f * deltaTime;
        effectIntensity = Math.max(0.0f, Math.min(1.0f, effectIntensity + intensityChange));
    }
    
    /**
     * Changer la météo en fonction des probabilités de transition
     */
    private void changeWeather() {
        Weather oldWeather = currentWeather;
        
        // Obtenir les probabilités de transition pour la météo actuelle
        Map<Weather, Float> transitions = weatherTransitionProbabilities.get(currentWeather);
        
        // Générer un nombre aléatoire entre 0 et 1
        float rand = random.nextFloat();
        
        // Somme cumulée des probabilités
        float cumulativeProbability = 0.0f;
        
        // Parcourir les transitions possibles
        for (Map.Entry<Weather, Float> entry : transitions.entrySet()) {
            cumulativeProbability += entry.getValue();
            
            // Si le nombre aléatoire est inférieur à la probabilité cumulée, choisir cette météo
            if (rand <= cumulativeProbability) {
                currentWeather = entry.getKey();
                break;
            }
        }
        
        // Réinitialiser la durée de la météo
        currentWeatherDuration = 0.0f;
        
        // Réinitialiser l'intensité des effets
        effectIntensity = 0.3f;
        
        // Notifier le callback si la météo a changé
        if (oldWeather != currentWeather && weatherEventCallback != null) {
            weatherEventCallback.onWeatherChanged(oldWeather, currentWeather);
        }
    }
    
    /**
     * Appliquer les effets visuels de la météo
     */
    private void applyWeatherVisualEffects() {
        Renderer renderer = Renderer.getInstance();
        
        // Réinitialiser les effets
        renderer.clearWeatherEffects();
        
        // Appliquer les effets en fonction de la météo
        switch (currentWeather) {
            case RAINY:
                renderer.addWeatherEffect(rainTextureId, 0.5f * effectIntensity);
                renderer.setGlobalTint(new Color(200, 200, 255));
                break;
            case STORMY:
                renderer.addWeatherEffect(rainTextureId, 0.8f * effectIntensity);
                renderer.addWeatherEffect(lightningTextureId, 0.3f * effectIntensity);
                renderer.setGlobalTint(new Color(150, 150, 200));
                break;
            case FOGGY:
                renderer.addWeatherEffect(fogTextureId, 0.7f * effectIntensity);
                renderer.setGlobalTint(new Color(230, 230, 240));
                break;
            case SNOWY:
                renderer.addWeatherEffect(snowTextureId, 0.6f * effectIntensity);
                renderer.setGlobalTint(new Color(240, 240, 255));
                break;
            case CLOUDY:
                renderer.setGlobalTint(new Color(220, 220, 220));
                break;
            case SUNNY:
                renderer.setGlobalTint(new Color(255, 240, 220));
                break;
            case CLEAR:
            default:
                // Pas d'effet spécial pour le temps clair
                renderer.setGlobalTint(Color.WHITE);
                break;
        }
    }
    
    /**
     * Obtenir une durée aléatoire pour la météo
     * @return Durée en secondes
     */
    private float getRandomWeatherDuration() {
        return MIN_WEATHER_DURATION + random.nextFloat() * (MAX_WEATHER_DURATION - MIN_WEATHER_DURATION);
    }
    
    /**
     * Obtenir la météo actuelle
     * @return Météo actuelle
     */
    public Weather getCurrentWeather() {
        return currentWeather;
    }
    
    /**
     * Définir la météo actuelle
     * @param weather Nouvelle météo
     */
    public void setCurrentWeather(Weather weather) {
        Weather oldWeather = this.currentWeather;
        this.currentWeather = weather;
        
        // Réinitialiser la durée de la météo
        currentWeatherDuration = 0.0f;
        
        // Réinitialiser l'intensité des effets
        effectIntensity = 0.3f;
        
        // Mettre à jour la météo dans le WorldManager
        WorldManager.getInstance().setCurrentWeather(this.currentWeather);
        
        // Appliquer les effets visuels de la météo
        applyWeatherVisualEffects();
        
        // Notifier le callback si la météo a changé
        if (oldWeather != this.currentWeather && weatherEventCallback != null) {
            weatherEventCallback.onWeatherChanged(oldWeather, this.currentWeather);
        }
    }
    
    /**
     * Changer aléatoirement la météo
     */
    public void randomizeWeather() {
        // Générer une météo aléatoire
        Weather[] weathers = Weather.values();
        int randomIndex = random.nextInt(weathers.length);
        setCurrentWeather(weathers[randomIndex]);
    }
    
    /**
     * Obtenir le modificateur de spawn pour un type de créature avec la météo actuelle
     * @param creatureType Type de créature
     * @return Modificateur de spawn (1.0 = normal, >1.0 = plus fréquent, <1.0 = moins fréquent)
     */
    public float getSpawnModifier(CreatureType creatureType) {
        Map<CreatureType, Float> modifiers = weatherSpawnModifiers.get(currentWeather);
        
        if (modifiers != null && modifiers.containsKey(creatureType)) {
            return modifiers.get(creatureType);
        }
        
        // Valeur par défaut si aucun modificateur n'est défini
        return 1.0f;
    }
    
    /**
     * Définir le callback pour les événements liés à la météo
     * @param callback Callback à appeler lors d'événements liés à la météo
     */
    public void setWeatherEventCallback(WeatherEventCallback callback) {
        this.weatherEventCallback = callback;
    }
    
    /**
     * Obtenir une description de la météo actuelle
     * @return Description de la météo
     */
    public String getWeatherDescription() {
        return currentWeather.getName();
    }
    
    /**
     * Types de météo possibles
     */
    public enum Weather {
        CLEAR("Dégagé"),
        CLOUDY("Nuageux"),
        RAINY("Pluvieux"),
        STORMY("Orageux"),
        FOGGY("Brumeux"),
        SNOWY("Neigeux"),
        SUNNY("Ensoleillé");
        
        private final String name;
        
        Weather(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * Interface pour les callbacks d'événements liés à la météo
     */
    public interface WeatherEventCallback {
        /**
         * Appelé lorsque la météo change
         * @param oldWeather Ancienne météo
         * @param newWeather Nouvelle météo
         */
        void onWeatherChanged(Weather oldWeather, Weather newWeather);
    }
}

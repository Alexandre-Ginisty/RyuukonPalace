package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.creatures.CreatureType;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Système de gestion du temps dans le jeu.
 * Gère le cycle jour/nuit et ses effets sur le gameplay.
 */
public class TimeSystem {
    
    // Singleton instance
    private static TimeSystem instance;
    
    // Temps de jeu actuel (en heures, 0-24)
    private float gameTime;
    
    // Vitesse d'écoulement du temps (1.0 = temps réel, 60.0 = 1 minute réelle = 1 heure de jeu)
    private float timeScale;
    
    // Durée d'un jour complet en secondes de jeu
    private float dayLengthInSeconds;
    
    // Heures de transition du jour
    private static final float DAWN_START = 5.0f;     // Début de l'aube
    private static final float DAWN_END = 7.0f;       // Fin de l'aube / début du jour
    private static final float DUSK_START = 18.0f;    // Début du crépuscule
    private static final float DUSK_END = 20.0f;      // Fin du crépuscule / début de la nuit
    
    // Callback pour les événements liés au temps
    private TimeEventCallback timeEventCallback;
    
    // Modificateurs de spawn selon l'heure
    private Map<TimeOfDay, Map<CreatureType, Float>> timeSpawnModifiers;
    
    /**
     * Constructeur privé pour le singleton
     */
    private TimeSystem() {
        this.gameTime = 8.0f; // 8h du matin par défaut
        this.timeScale = 60.0f; // 1 minute réelle = 1 heure de jeu
        this.dayLengthInSeconds = 24 * 60; // 24 minutes réelles = 1 jour de jeu
        
        // Initialiser les modificateurs de spawn selon l'heure
        initializeTimeSpawnModifiers();
    }
    
    /**
     * Obtenir l'instance unique du système de temps
     * @return L'instance du TimeSystem
     */
    public static TimeSystem getInstance() {
        if (instance == null) {
            instance = new TimeSystem();
        }
        return instance;
    }
    
    /**
     * Initialiser les modificateurs de spawn selon l'heure
     */
    private void initializeTimeSpawnModifiers() {
        timeSpawnModifiers = new HashMap<>();
        
        // Modificateurs pour le matin
        Map<CreatureType, Float> morningModifiers = new HashMap<>();
        morningModifiers.put(CreatureType.FIRE, 0.8f);      // Moins de créatures de feu le matin
        morningModifiers.put(CreatureType.WATER, 1.2f);     // Plus de créatures d'eau le matin
        morningModifiers.put(CreatureType.EARTH, 1.0f);     // Normal pour les créatures de terre
        morningModifiers.put(CreatureType.AIR, 1.3f);       // Plus de créatures d'air le matin
        morningModifiers.put(CreatureType.LIGHT, 1.5f);     // Beaucoup plus de créatures de lumière le matin
        morningModifiers.put(CreatureType.SHADOW, 0.5f);    // Beaucoup moins de créatures d'ombre le matin
        morningModifiers.put(CreatureType.NATURE, 1.4f);    // Plus de créatures de nature le matin
        timeSpawnModifiers.put(TimeOfDay.MORNING, morningModifiers);
        
        // Modificateurs pour l'après-midi
        Map<CreatureType, Float> afternoonModifiers = new HashMap<>();
        afternoonModifiers.put(CreatureType.FIRE, 1.3f);    // Plus de créatures de feu l'après-midi
        afternoonModifiers.put(CreatureType.WATER, 0.9f);   // Moins de créatures d'eau l'après-midi
        afternoonModifiers.put(CreatureType.EARTH, 1.0f);   // Normal pour les créatures de terre
        afternoonModifiers.put(CreatureType.AIR, 1.1f);     // Légèrement plus de créatures d'air
        afternoonModifiers.put(CreatureType.LIGHT, 1.2f);   // Plus de créatures de lumière
        afternoonModifiers.put(CreatureType.SHADOW, 0.7f);  // Moins de créatures d'ombre
        afternoonModifiers.put(CreatureType.NATURE, 1.0f);  // Normal pour les créatures de nature
        timeSpawnModifiers.put(TimeOfDay.AFTERNOON, afternoonModifiers);
        
        // Modificateurs pour le soir
        Map<CreatureType, Float> eveningModifiers = new HashMap<>();
        eveningModifiers.put(CreatureType.FIRE, 1.2f);      // Plus de créatures de feu le soir
        eveningModifiers.put(CreatureType.WATER, 1.0f);     // Normal pour les créatures d'eau
        eveningModifiers.put(CreatureType.EARTH, 0.9f);     // Moins de créatures de terre
        eveningModifiers.put(CreatureType.AIR, 0.8f);       // Moins de créatures d'air
        eveningModifiers.put(CreatureType.LIGHT, 0.7f);     // Moins de créatures de lumière
        eveningModifiers.put(CreatureType.SHADOW, 1.3f);    // Plus de créatures d'ombre
        eveningModifiers.put(CreatureType.NATURE, 0.8f);    // Moins de créatures de nature
        timeSpawnModifiers.put(TimeOfDay.EVENING, eveningModifiers);
        
        // Modificateurs pour la nuit
        Map<CreatureType, Float> nightModifiers = new HashMap<>();
        nightModifiers.put(CreatureType.FIRE, 0.7f);        // Moins de créatures de feu la nuit
        nightModifiers.put(CreatureType.WATER, 0.8f);       // Moins de créatures d'eau la nuit
        nightModifiers.put(CreatureType.EARTH, 0.7f);       // Moins de créatures de terre la nuit
        nightModifiers.put(CreatureType.AIR, 0.6f);         // Moins de créatures d'air la nuit
        nightModifiers.put(CreatureType.LIGHT, 0.4f);       // Beaucoup moins de créatures de lumière la nuit
        nightModifiers.put(CreatureType.SHADOW, 2.0f);      // Beaucoup plus de créatures d'ombre la nuit
        nightModifiers.put(CreatureType.NATURE, 0.5f);      // Beaucoup moins de créatures de nature la nuit
        timeSpawnModifiers.put(TimeOfDay.NIGHT, nightModifiers);
    }
    
    /**
     * Mettre à jour le temps de jeu
     * @param deltaTime Temps écoulé depuis la dernière frame en secondes
     */
    public void update(float deltaTime) {
        // Calculer l'ancien moment de la journée
        TimeOfDay oldTimeOfDay = getCurrentTimeOfDay();
        
        // Avancer le temps
        float timeAdvance = deltaTime * timeScale / dayLengthInSeconds * 24.0f;
        gameTime += timeAdvance;
        
        // Assurer que le temps reste dans l'intervalle 0-24
        gameTime %= 24.0f;
        if (gameTime < 0) {
            gameTime += 24.0f;
        }
        
        // Vérifier si le moment de la journée a changé
        TimeOfDay newTimeOfDay = getCurrentTimeOfDay();
        if (oldTimeOfDay != newTimeOfDay && timeEventCallback != null) {
            timeEventCallback.onTimeOfDayChanged(oldTimeOfDay, newTimeOfDay);
        }
        
        // Mettre à jour le temps dans le WorldManager
        WorldManager.getInstance().setGameTime(gameTime);
        
        // Appliquer les effets visuels du temps
        applyTimeVisualEffects();
    }
    
    /**
     * Appliquer les effets visuels du temps (luminosité, couleurs, etc.)
     */
    private void applyTimeVisualEffects() {
        Renderer renderer = Renderer.getInstance();
        
        // Calculer la luminosité en fonction de l'heure
        float brightness = calculateBrightness();
        
        // Calculer la teinte en fonction de l'heure
        Color tint = calculateTint();
        
        // Appliquer la luminosité et la teinte au renderer
        renderer.setGlobalBrightness(brightness);
        renderer.setGlobalTint(tint);
    }
    
    /**
     * Calculer la luminosité en fonction de l'heure
     * @return Luminosité entre 0.0 (nuit noire) et 1.0 (plein jour)
     */
    private float calculateBrightness() {
        if (gameTime >= DAWN_END && gameTime < DUSK_START) {
            // Plein jour
            return 1.0f;
        } else if (gameTime >= DUSK_END || gameTime < DAWN_START) {
            // Pleine nuit
            return 0.3f;
        } else if (gameTime >= DAWN_START && gameTime < DAWN_END) {
            // Aube - transition progressive
            return 0.3f + (gameTime - DAWN_START) / (DAWN_END - DAWN_START) * 0.7f;
        } else {
            // Crépuscule - transition progressive
            return 1.0f - (gameTime - DUSK_START) / (DUSK_END - DUSK_START) * 0.7f;
        }
    }
    
    /**
     * Calculer la teinte en fonction de l'heure
     * @return Couleur de teinte à appliquer
     */
    private Color calculateTint() {
        if (gameTime >= DAWN_START && gameTime < DAWN_END) {
            // Aube - teinte orangée
            return new Color(255, 200, 150);
        } else if (gameTime >= DUSK_START && gameTime < DUSK_END) {
            // Crépuscule - teinte rougeâtre
            return new Color(255, 180, 130);
        } else if (gameTime >= DAWN_END && gameTime < DUSK_START) {
            // Jour - pas de teinte
            return Color.WHITE;
        } else {
            // Nuit - teinte bleutée
            return new Color(150, 150, 255);
        }
    }
    
    /**
     * Obtenir le temps de jeu actuel
     * @return Temps de jeu en heures (0-24)
     */
    public float getGameTime() {
        return gameTime;
    }
    
    /**
     * Définir le temps de jeu
     * @param gameTime Nouveau temps de jeu en heures (0-24)
     */
    public void setGameTime(float gameTime) {
        // Assurer que le temps reste dans l'intervalle 0-24
        this.gameTime = gameTime % 24;
        if (this.gameTime < 0) {
            this.gameTime += 24;
        }
        
        // Mettre à jour le temps dans le WorldManager
        WorldManager.getInstance().setGameTime(this.gameTime);
        
        // Appliquer les effets visuels du temps
        applyTimeVisualEffects();
    }
    
    /**
     * Avancer le temps de jeu
     * @param hours Nombre d'heures à avancer
     */
    public void advanceTime(float hours) {
        setGameTime(gameTime + hours);
    }
    
    /**
     * Obtenir la vitesse d'écoulement du temps
     * @return Vitesse d'écoulement du temps
     */
    public float getTimeScale() {
        return timeScale;
    }
    
    /**
     * Définir la vitesse d'écoulement du temps
     * @param timeScale Nouvelle vitesse d'écoulement du temps
     */
    public void setTimeScale(float timeScale) {
        this.timeScale = timeScale;
    }
    
    /**
     * Obtenir le moment actuel de la journée
     * @return Moment de la journée
     */
    public TimeOfDay getCurrentTimeOfDay() {
        if (gameTime >= 5 && gameTime < 12) {
            return TimeOfDay.MORNING;
        } else if (gameTime >= 12 && gameTime < 18) {
            return TimeOfDay.AFTERNOON;
        } else if (gameTime >= 18 && gameTime < 22) {
            return TimeOfDay.EVENING;
        } else {
            return TimeOfDay.NIGHT;
        }
    }
    
    /**
     * Obtenir le modificateur de spawn pour un type de créature à l'heure actuelle
     * @param creatureType Type de créature
     * @return Modificateur de spawn (1.0 = normal, >1.0 = plus fréquent, <1.0 = moins fréquent)
     */
    public float getSpawnModifier(CreatureType creatureType) {
        TimeOfDay currentTime = getCurrentTimeOfDay();
        Map<CreatureType, Float> modifiers = timeSpawnModifiers.get(currentTime);
        
        if (modifiers != null && modifiers.containsKey(creatureType)) {
            return modifiers.get(creatureType);
        }
        
        // Valeur par défaut si aucun modificateur n'est défini
        return 1.0f;
    }
    
    /**
     * Définir le callback pour les événements liés au temps
     * @param callback Callback à appeler lors d'événements liés au temps
     */
    public void setTimeEventCallback(TimeEventCallback callback) {
        this.timeEventCallback = callback;
    }
    
    /**
     * Obtenir une description textuelle de l'heure actuelle
     * @return Description de l'heure
     */
    public String getTimeDescription() {
        int hours = (int) gameTime;
        int minutes = (int) ((gameTime - hours) * 60);
        
        return String.format("%02d:%02d", hours, minutes);
    }
    
    /**
     * Moments de la journée
     */
    public enum TimeOfDay {
        MORNING("Matin"),
        AFTERNOON("Après-midi"),
        EVENING("Soir"),
        NIGHT("Nuit");
        
        private final String name;
        
        TimeOfDay(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * Interface pour les callbacks d'événements liés au temps
     */
    public interface TimeEventCallback {
        /**
         * Appelé lorsque le moment de la journée change
         * @param oldTimeOfDay Ancien moment de la journée
         * @param newTimeOfDay Nouveau moment de la journée
         */
        void onTimeOfDayChanged(TimeOfDay oldTimeOfDay, TimeOfDay newTimeOfDay);
    }
}

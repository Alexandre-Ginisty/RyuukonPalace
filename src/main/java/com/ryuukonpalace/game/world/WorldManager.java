package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.core.GameObject;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureType;
import com.ryuukonpalace.game.utils.ResourceManager;
import com.ryuukonpalace.game.world.TimeSystem.TimeOfDay;
import com.ryuukonpalace.game.world.WeatherSystem.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Gestionnaire du monde du jeu.
 * Responsable de la création et de la gestion des zones d'apparition des créatures,
 * des obstacles, des PNJ et autres éléments du monde.
 */
public class WorldManager {
    
    // Singleton instance
    private static WorldManager instance;
    
    // Dimensions du monde
    private float worldWidth;
    private float worldHeight;
    
    // Liste des zones d'apparition
    private List<SpawnZone> spawnZones;
    
    // Liste des obstacles
    private List<GameObject> obstacles;
    
    // ID de la texture de l'herbe haute
    private int grassTextureId;
    
    // ID de la texture du sol
    private int groundTextureId;
    
    // Renderer
    private Renderer renderer;
    
    // Nom de la zone actuelle
    private String currentZoneName;
    
    // Temps de jeu (en heures)
    private float gameTime;
    
    // Météo actuelle
    private Weather currentWeather;
    
    // Systèmes de temps et de météo
    private TimeSystem timeSystem;
    private WeatherSystem weatherSystem;
    
    // État des PNJ
    private Map<Integer, Boolean> npcsState;
    
    // Liste des créatures visibles dans le monde
    private List<Creature> visibleCreatures;
    
    // Modificateurs temporaires de taux d'apparition des créatures
    private Map<CreatureType, SpawnRateModifier> spawnRateModifiers;
    
    // Groupes de PNJ spéciaux
    private Map<String, List<Integer>> specialNpcGroups;
    
    /**
     * Constructeur privé pour le singleton
     */
    private WorldManager() {
        this.worldWidth = 2000.0f;
        this.worldHeight = 2000.0f;
        this.spawnZones = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.currentZoneName = "Zone de départ";
        this.gameTime = 8.0f; // 8h du matin
        this.currentWeather = Weather.CLEAR;
        this.npcsState = new HashMap<>();
        this.visibleCreatures = new ArrayList<>();
        this.spawnRateModifiers = new HashMap<>();
        this.specialNpcGroups = new HashMap<>();
        
        // Obtenir l'instance du renderer
        this.renderer = Renderer.getInstance();
        
        // Initialiser les systèmes de temps et de météo
        this.timeSystem = TimeSystem.getInstance();
        this.weatherSystem = WeatherSystem.getInstance();
        
        // Configurer les callbacks pour les événements de temps et de météo
        setupTimeAndWeatherCallbacks();
        
        // Charger les textures
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.grassTextureId = resourceManager.loadTexture("src/main/resources/images/tall_grass.png", "tall_grass");
        this.groundTextureId = resourceManager.loadTexture("src/main/resources/images/ground.png", "ground");
        
        // Initialiser le monde
        initializeWorld();
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire de monde
     * @return L'instance du WorldManager
     */
    public static WorldManager getInstance() {
        if (instance == null) {
            instance = new WorldManager();
        }
        return instance;
    }
    
    /**
     * Initialiser le monde avec les zones d'apparition et les obstacles
     */
    private void initializeWorld() {
        // Créer des zones d'apparition aléatoire (hautes herbes)
        createRandomSpawnZones();
        
        // Créer des zones avec des créatures visibles
        createVisibleCreatureZones();
        
        // Créer des obstacles
        createObstacles();
    }
    
    /**
     * Créer des zones d'apparition aléatoire (hautes herbes)
     */
    private void createRandomSpawnZones() {
        // Zone 1: Près du point de départ
        SpawnZone zone1 = new SpawnZone(100, 200, 200, 150, 0.1f, 10);
        zone1.addPossibleCreature(1, 40, 3, 7);  // Flameling (commun)
        zone1.addPossibleCreature(2, 40, 3, 7);  // Aquaflux (commun)
        zone1.addPossibleCreature(5, 20, 4, 8);  // Terravolt (moins commun)
        spawnZones.add(zone1);
        
        // Zone 2: Un peu plus loin
        SpawnZone zone2 = new SpawnZone(400, 300, 250, 200, 0.15f, 8);
        zone2.addPossibleCreature(1, 30, 5, 10);  // Flameling
        zone2.addPossibleCreature(2, 30, 5, 10);  // Aquaflux
        zone2.addPossibleCreature(5, 30, 5, 10);  // Terravolt
        zone2.addPossibleCreature(7, 10, 6, 12);  // Zephyrus (rare)
        spawnZones.add(zone2);
        
        // Zone 3: Zone avancée
        SpawnZone zone3 = new SpawnZone(800, 500, 300, 250, 0.2f, 6);
        zone3.addPossibleCreature(5, 35, 8, 15);  // Terravolt
        zone3.addPossibleCreature(7, 35, 8, 15);  // Zephyrus
        zone3.addPossibleCreature(9, 20, 10, 18); // Luminar (moins commun)
        zone3.addPossibleCreature(11, 10, 12, 20); // Umbra (rare)
        spawnZones.add(zone3);
        
        // Zone 4: Zone d'élite
        SpawnZone zone4 = new SpawnZone(1200, 800, 350, 300, 0.25f, 5);
        zone4.addPossibleCreature(3, 5, 15, 25);  // Infernus (très rare)
        zone4.addPossibleCreature(4, 5, 15, 25);  // Tsunamis (très rare)
        zone4.addPossibleCreature(6, 10, 12, 22); // Seismoroc (rare)
        zone4.addPossibleCreature(8, 10, 12, 22); // Tempestus (rare)
        zone4.addPossibleCreature(9, 30, 10, 20); // Luminar
        zone4.addPossibleCreature(11, 30, 10, 20); // Umbra
        zone4.addPossibleCreature(10, 5, 18, 30); // Solarius (très rare)
        zone4.addPossibleCreature(12, 5, 18, 30); // Nocturnix (très rare)
        spawnZones.add(zone4);
    }
    
    /**
     * Créer des zones avec des créatures visibles
     */
    private void createVisibleCreatureZones() {
        // Créature visible 1: Flameling (niveau 5)
        SpawnZone visibleZone1 = new SpawnZone(300, 150, 40, 40, 1);
        spawnZones.add(visibleZone1);
        
        // Créature visible 2: Aquaflux (niveau 5)
        SpawnZone visibleZone2 = new SpawnZone(600, 400, 40, 40, 2);
        spawnZones.add(visibleZone2);
        
        // Créature visible 3: Terravolt (niveau 8)
        SpawnZone visibleZone3 = new SpawnZone(900, 700, 40, 40, 5);
        spawnZones.add(visibleZone3);
        
        // Créature visible 4: Infernus (niveau 20, évolution de Flameling)
        SpawnZone visibleZone4 = new SpawnZone(1400, 900, 50, 50, 3);
        spawnZones.add(visibleZone4);
    }
    
    /**
     * Créer des obstacles dans le monde
     */
    private void createObstacles() {
        // Les obstacles seront implémentés plus tard
    }
    
    /**
     * Configurer les callbacks pour les événements de temps et de météo
     */
    private void setupTimeAndWeatherCallbacks() {
        // Callback pour les changements de moment de la journée
        timeSystem.setTimeEventCallback(new TimeSystem.TimeEventCallback() {
            @Override
            public void onTimeOfDayChanged(TimeOfDay oldTimeOfDay, TimeOfDay newTimeOfDay) {
                // Mettre à jour les probabilités de spawn en fonction du moment de la journée
                updateSpawnProbabilities();
                
                // Afficher un message de notification (à remplacer par une notification UI)
                System.out.println("Le moment de la journée a changé : " + newTimeOfDay.getName());
            }
        });
        
        // Callback pour les changements de météo
        weatherSystem.setWeatherEventCallback(new WeatherSystem.WeatherEventCallback() {
            @Override
            public void onWeatherChanged(Weather oldWeather, Weather newWeather) {
                // Mettre à jour les probabilités de spawn en fonction de la météo
                updateSpawnProbabilities();
                
                // Afficher un message de notification (à remplacer par une notification UI)
                System.out.println("La météo a changé : " + newWeather.getName());
            }
        });
    }
    
    /**
     * Mettre à jour les probabilités de spawn en fonction du temps et de la météo
     */
    private void updateSpawnProbabilities() {
        for (SpawnZone zone : spawnZones) {
            zone.updateSpawnProbabilities(timeSystem, weatherSystem);
        }
    }
    
    /**
     * Mettre à jour toutes les zones d'apparition
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        // Mettre à jour les systèmes de temps et de météo
        timeSystem.update(deltaTime);
        weatherSystem.update(deltaTime);
        
        // Mettre à jour les zones d'apparition
        for (SpawnZone zone : spawnZones) {
            zone.update(deltaTime);
        }
        
        // Mettre à jour les créatures visibles
        updateVisibleCreatures(deltaTime);
        
        // Mettre à jour les modificateurs de taux d'apparition
        updateSpawnRateModifiers(deltaTime);
    }
    
    /**
     * Mettre à jour les créatures visibles dans le monde
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    private void updateVisibleCreatures(float deltaTime) {
        for (int i = visibleCreatures.size() - 1; i >= 0; i--) {
            Creature creature = visibleCreatures.get(i);
            creature.update(deltaTime);
            
            // Supprimer les créatures qui ont expiré
            if (creature.isExpired()) {
                visibleCreatures.remove(i);
            }
        }
    }
    
    /**
     * Mettre à jour les modificateurs de taux d'apparition
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    private void updateSpawnRateModifiers(float deltaTime) {
        // Créer une liste des modificateurs à supprimer
        List<CreatureType> typesToRemove = new ArrayList<>();
        
        // Mettre à jour chaque modificateur
        for (Map.Entry<CreatureType, SpawnRateModifier> entry : spawnRateModifiers.entrySet()) {
            SpawnRateModifier modifier = entry.getValue();
            modifier.remainingTime -= deltaTime;
            
            // Si le modificateur a expiré, l'ajouter à la liste de suppression
            if (modifier.remainingTime <= 0) {
                typesToRemove.add(entry.getKey());
            }
        }
        
        // Supprimer les modificateurs expirés
        for (CreatureType type : typesToRemove) {
            spawnRateModifiers.remove(type);
            System.out.println("Modificateur de taux d'apparition expiré pour le type " + type.name());
        }
    }
    
    /**
     * Faire apparaître une créature dans le monde
     * 
     * @param creature Créature à faire apparaître
     */
    public void spawnCreature(Creature creature) {
        // Vérifier si la créature est déjà dans le monde
        if (visibleCreatures.contains(creature)) {
            return;
        }
        
        // Ajouter la créature à la liste des créatures visibles
        visibleCreatures.add(creature);
        
        System.out.println("Créature " + creature.getName() + " apparue en position (" + 
                creature.getX() + ", " + creature.getY() + ")");
    }
    
    /**
     * Faire apparaître une créature à une position spécifique
     * 
     * @param creature Créature à faire apparaître
     * @param x Position X
     * @param y Position Y
     */
    public void spawnCreature(Creature creature, float x, float y) {
        // Définir la position de la créature
        creature.setPosition(x, y);
        
        // Faire apparaître la créature
        spawnCreature(creature);
    }
    
    /**
     * Faire apparaître une créature dans une zone spécifique
     * 
     * @param creature Créature à faire apparaître
     * @param zoneIndex Index de la zone d'apparition
     */
    public void spawnCreatureInZone(Creature creature, int zoneIndex) {
        if (zoneIndex < 0 || zoneIndex >= spawnZones.size()) {
            System.err.println("Index de zone d'apparition invalide: " + zoneIndex);
            return;
        }
        
        // Obtenir la zone d'apparition
        SpawnZone zone = spawnZones.get(zoneIndex);
        
        // Générer une position aléatoire dans la zone
        Random random = new Random();
        float x = zone.getX() + random.nextFloat() * zone.getWidth();
        float y = zone.getY() + random.nextFloat() * zone.getHeight();
        
        // Faire apparaître la créature à cette position
        spawnCreature(creature, x, y);
    }
    
    /**
     * Définir un modificateur temporaire de taux d'apparition pour un type de créature
     * 
     * @param type Type de créature
     * @param multiplier Multiplicateur de taux d'apparition
     * @param duration Durée en secondes
     */
    public void setCreatureSpawnRateMultiplier(CreatureType type, float multiplier, float duration) {
        SpawnRateModifier modifier = new SpawnRateModifier(multiplier, duration);
        spawnRateModifiers.put(type, modifier);
        
        // Mettre à jour les probabilités de spawn dans toutes les zones
        updateSpawnProbabilities();
        
        System.out.println("Modificateur de taux d'apparition défini pour le type " + type.name() + 
                ": x" + multiplier + " pendant " + duration + " secondes");
    }
    
    /**
     * Obtenir le multiplicateur de taux d'apparition pour un type de créature
     * 
     * @param type Type de créature
     * @return Multiplicateur (1.0 par défaut si aucun modificateur n'est actif)
     */
    public float getCreatureSpawnRateMultiplier(CreatureType type) {
        SpawnRateModifier modifier = spawnRateModifiers.get(type);
        return (modifier != null) ? modifier.multiplier : 1.0f;
    }
    
    /**
     * Ajouter un groupe de PNJ spéciaux
     * 
     * @param groupName Nom du groupe
     * @param npcIds Liste des IDs des PNJ dans ce groupe
     */
    public void addSpecialNpcGroup(String groupName, List<Integer> npcIds) {
        specialNpcGroups.put(groupName, new ArrayList<>(npcIds));
    }
    
    /**
     * Faire apparaître un groupe de PNJ spéciaux
     * 
     * @param groupName Nom du groupe à faire apparaître
     */
    public void spawnSpecialNPCs(String groupName) {
        List<Integer> npcIds = specialNpcGroups.get(groupName);
        if (npcIds == null || npcIds.isEmpty()) {
            System.err.println("Groupe de PNJ spéciaux non trouvé: " + groupName);
            return;
        }
        
        // Activer tous les PNJ du groupe
        for (Integer npcId : npcIds) {
            setNpcState(npcId, true);
        }
        
        System.out.println("Groupe de PNJ spéciaux apparu: " + groupName + " (" + npcIds.size() + " PNJ)");
    }
    
    /**
     * Faire disparaître un groupe de PNJ spéciaux
     * 
     * @param groupName Nom du groupe à faire disparaître
     */
    public void despawnSpecialNPCs(String groupName) {
        List<Integer> npcIds = specialNpcGroups.get(groupName);
        if (npcIds == null || npcIds.isEmpty()) {
            System.err.println("Groupe de PNJ spéciaux non trouvé: " + groupName);
            return;
        }
        
        // Désactiver tous les PNJ du groupe
        for (Integer npcId : npcIds) {
            setNpcState(npcId, false);
        }
        
        System.out.println("Groupe de PNJ spéciaux disparu: " + groupName);
    }
    
    /**
     * Obtenir la liste des créatures visibles dans le monde
     * 
     * @return Liste des créatures visibles
     */
    public List<Creature> getVisibleCreatures() {
        return new ArrayList<>(visibleCreatures);
    }
    
    /**
     * Dessiner le monde
     */
    public void render() {
        // Dessiner le sol
        for (int x = 0; x < worldWidth; x += 64) {
            for (int y = 0; y < worldHeight; y += 64) {
                renderer.drawSprite(groundTextureId, x, y, 64, 64);
            }
        }
        
        // Dessiner les zones d'apparition aléatoire (hautes herbes)
        for (SpawnZone zone : spawnZones) {
            if (zone.getType() == SpawnZone.SpawnZoneType.RANDOM_SPAWN) {
                // Dessiner l'herbe haute pour les zones d'apparition aléatoire
                for (float x = zone.getX(); x < zone.getX() + zone.getWidth(); x += 32) {
                    for (float y = zone.getY(); y < zone.getY() + zone.getHeight(); y += 32) {
                        renderer.drawSprite(grassTextureId, x, y, 32, 32);
                    }
                }
            }
        }
        
        // Dessiner les zones d'apparition avec créatures visibles
        for (SpawnZone zone : spawnZones) {
            if (zone.getType() == SpawnZone.SpawnZoneType.VISIBLE_CREATURE) {
                zone.render();
            }
        }
        
        // Dessiner les obstacles
        for (GameObject obstacle : obstacles) {
            obstacle.render();
        }
    }
    
    /**
     * Obtenir la liste des zones d'apparition
     * 
     * @return Liste des zones d'apparition
     */
    public List<SpawnZone> getSpawnZones() {
        return new ArrayList<>(spawnZones);
    }
    
    /**
     * Obtenir la liste des obstacles
     * 
     * @return Liste des obstacles
     */
    public List<GameObject> getObstacles() {
        return new ArrayList<>(obstacles);
    }
    
    /**
     * Obtenir la largeur du monde
     * 
     * @return Largeur du monde
     */
    public float getWorldWidth() {
        return worldWidth;
    }
    
    /**
     * Obtenir la hauteur du monde
     * 
     * @return Hauteur du monde
     */
    public float getWorldHeight() {
        return worldHeight;
    }
    
    /**
     * Initialiser le monde
     */
    public void init() {
        // Réinitialiser les zones d'apparition et les obstacles
        spawnZones.clear();
        obstacles.clear();
        
        // Initialiser le monde
        initializeWorld();
    }
    
    /**
     * Charger une zone spécifique
     * 
     * @param zoneName Nom de la zone à charger
     */
    public void loadZone(String zoneName) {
        this.currentZoneName = zoneName;
        
        // Réinitialiser les zones d'apparition et les obstacles
        spawnZones.clear();
        obstacles.clear();
        
        // Charger les zones d'apparition et les obstacles pour cette zone
        if ("Zone de départ".equals(zoneName)) {
            createRandomSpawnZones();
            createVisibleCreatureZones();
        } else if ("Forêt mystique".equals(zoneName)) {
            // Créer des zones d'apparition pour la forêt mystique
            SpawnZone forestZone = new SpawnZone(100, 100, 500, 400, 0.2f, 8);
            forestZone.addPossibleCreature(5, 40, 10, 15);  // Terravolt (commun dans la forêt)
            forestZone.addPossibleCreature(9, 30, 12, 18);  // Luminar (commun dans la forêt)
            spawnZones.add(forestZone);
        } else if ("Montagne volcanique".equals(zoneName)) {
            // Créer des zones d'apparition pour la montagne volcanique
            SpawnZone volcanoZone = new SpawnZone(100, 100, 400, 300, 0.15f, 6);
            volcanoZone.addPossibleCreature(1, 40, 15, 20);  // Flameling (commun dans le volcan)
            volcanoZone.addPossibleCreature(3, 30, 18, 25);  // Infernus (commun dans le volcan)
            spawnZones.add(volcanoZone);
        } else {
            // Zone par défaut
            createRandomSpawnZones();
        }
        
        // Créer des obstacles pour cette zone
        createObstacles();
    }
    
    /**
     * Obtenir le nom de la zone actuelle
     * 
     * @return Nom de la zone actuelle
     */
    public String getCurrentZoneName() {
        return currentZoneName;
    }
    
    /**
     * Définir le nom de la zone actuelle
     * 
     * @param zoneName Nouveau nom de zone
     */
    public void setCurrentZoneName(String zoneName) {
        this.currentZoneName = zoneName;
    }
    
    /**
     * Obtenir le temps de jeu actuel
     * 
     * @return Temps de jeu en heures (0-24)
     */
    public float getGameTime() {
        return gameTime;
    }
    
    /**
     * Définir le temps de jeu
     * 
     * @param gameTime Nouveau temps de jeu en heures (0-24)
     */
    public void setGameTime(float gameTime) {
        // Assurer que le temps reste dans l'intervalle 0-24
        this.gameTime = gameTime % 24;
        if (this.gameTime < 0) {
            this.gameTime += 24;
        }
    }
    
    /**
     * Avancer le temps de jeu
     * 
     * @param hours Nombre d'heures à avancer
     */
    public void advanceTime(float hours) {
        timeSystem.advanceTime(hours);
    }
    
    /**
     * Obtenir la météo actuelle
     * 
     * @return Météo actuelle
     */
    public Weather getCurrentWeather() {
        return currentWeather;
    }
    
    /**
     * Définir la météo actuelle
     * 
     * @param weather Nouvelle météo
     */
    public void setCurrentWeather(Weather weather) {
        this.currentWeather = weather;
    }
    
    /**
     * Changer aléatoirement la météo
     */
    public void randomizeWeather() {
        weatherSystem.randomizeWeather();
    }
    
    /**
     * Obtenir l'état des PNJ
     * 
     * @return Map des états des PNJ (ID -> actif)
     */
    public Map<Integer, Boolean> getNpcsState() {
        return new HashMap<>(npcsState);
    }
    
    /**
     * Définir l'état des PNJ
     * 
     * @param npcsState Nouvelle map d'états des PNJ
     */
    public void setNpcsState(Map<Integer, Boolean> npcsState) {
        this.npcsState = new HashMap<>(npcsState);
    }
    
    /**
     * Définir l'état d'un PNJ spécifique
     * 
     * @param npcId ID du PNJ
     * @param active État actif ou non
     */
    public void setNpcState(int npcId, boolean active) {
        this.npcsState.put(npcId, active);
    }
    
    /**
     * Obtenir le système de temps
     * 
     * @return Système de temps
     */
    public TimeSystem getTimeSystem() {
        return timeSystem;
    }
    
    /**
     * Obtenir le système de météo
     * 
     * @return Système de météo
     */
    public WeatherSystem getWeatherSystem() {
        return weatherSystem;
    }
    
    /**
     * Classe interne pour représenter un modificateur temporaire de taux d'apparition
     */
    private static class SpawnRateModifier {
        float multiplier;
        float remainingTime;
        
        public SpawnRateModifier(float multiplier, float duration) {
            this.multiplier = multiplier;
            this.remainingTime = duration;
        }
    }
}

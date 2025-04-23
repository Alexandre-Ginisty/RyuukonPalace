package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.core.GameObject;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.utils.ResourceManager;

import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * Constructeur privé pour le singleton
     */
    private WorldManager() {
        this.worldWidth = 2000.0f;
        this.worldHeight = 2000.0f;
        this.spawnZones = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        
        // Obtenir l'instance du renderer
        this.renderer = Renderer.getInstance();
        
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
     * Mettre à jour toutes les zones d'apparition
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        for (SpawnZone zone : spawnZones) {
            zone.update(deltaTime);
        }
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
}

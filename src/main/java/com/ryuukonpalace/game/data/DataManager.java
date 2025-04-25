package com.ryuukonpalace.game.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.Item;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestionnaire de données pour le jeu.
 * Cette classe est responsable du chargement, de la mise en cache et de la sauvegarde des données du jeu.
 */
public class DataManager {
    private static final Logger LOGGER = Logger.getLogger(DataManager.class.getName());
    private static DataManager instance;
    
    // Chemins des fichiers de données
    private static final String DATA_DIR = "src/main/resources/data/";
    private static final String CREATURES_FILE = DATA_DIR + "creatures_dictionary.json";
    private static final String NPCS_FILE = DATA_DIR + "npcs_dictionary.json";
    private static final String SPAWN_ZONES_FILE = DATA_DIR + "spawn_zones_dictionary.json";
    
    // Cache des données
    private Map<Integer, CreatureDefinition> creatureCache;
    private Map<Integer, NPCDefinition> npcCache;
    private Map<String, SpawnZoneDefinition> spawnZoneCache;
    
    // Gson pour la sérialisation/désérialisation
    private final Gson gson;
    
    /**
     * Constructeur privé (singleton)
     */
    private DataManager() {
        // Initialiser Gson avec les adaptateurs personnalisés
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Creature.class, new CreatureTypeAdapter());
        gsonBuilder.registerTypeAdapter(Item.class, new ItemTypeAdapter());
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
        
        // Initialiser les caches
        creatureCache = new HashMap<>();
        npcCache = new HashMap<>();
        spawnZoneCache = new HashMap<>();
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire de données
     * 
     * @return Instance de DataManager
     */
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    /**
     * Obtenir la définition d'une créature par son ID
     * 
     * @param creatureId ID de la créature
     * @return Définition de la créature, ou null si non trouvée
     */
    public CreatureDefinition getCreatureDefinition(int creatureId) {
        // Vérifier si la créature est dans le cache
        if (creatureCache.containsKey(creatureId)) {
            return creatureCache.get(creatureId);
        }
        
        // Charger toutes les définitions de créatures si le cache est vide
        if (creatureCache.isEmpty()) {
            loadAllCreatureDefinitions();
        }
        
        return creatureCache.get(creatureId);
    }
    
    /**
     * Charger toutes les définitions de créatures
     */
    private void loadAllCreatureDefinitions() {
        // Charger le fichier principal
        loadCreatureDefinitionsFromFile(CREATURES_FILE);
        
        // Charger les fichiers supplémentaires (s'ils existent)
        File dataDir = new File(DATA_DIR);
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((dir, name) -> 
                    name.startsWith("creatures_dictionary_part") && name.endsWith(".json"));
            
            if (files != null) {
                for (File file : files) {
                    loadCreatureDefinitionsFromFile(file.getPath());
                }
            }
        }
    }
    
    /**
     * Charger les définitions de créatures à partir d'un fichier
     * 
     * @param filePath Chemin du fichier
     */
    private void loadCreatureDefinitionsFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            CreaturesDictionary dictionary = gson.fromJson(reader, CreaturesDictionary.class);
            
            if (dictionary != null && dictionary.creatures != null) {
                for (CreatureDefinition definition : dictionary.creatures) {
                    creatureCache.put(definition.id, definition);
                }
                LOGGER.info("Loaded " + dictionary.creatures.size() + " creature definitions from " + filePath);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load creature definitions from " + filePath, e);
        }
    }
    
    /**
     * Obtenir toutes les définitions de créatures
     * 
     * @return Map des définitions de créatures, avec l'ID comme clé
     */
    public Map<Integer, CreatureDefinition> getAllCreatureDefinitions() {
        if (creatureCache.isEmpty()) {
            loadAllCreatureDefinitions();
        }
        return new HashMap<>(creatureCache);
    }
    
    /**
     * Sauvegarder une définition de créature
     * 
     * @param definition Définition de créature à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     */
    public boolean saveCreatureDefinition(CreatureDefinition definition) {
        // Mettre à jour le cache
        creatureCache.put(definition.id, definition);
        
        // Sauvegarder toutes les définitions
        return saveAllCreatureDefinitions();
    }
    
    /**
     * Sauvegarder toutes les définitions de créatures
     * 
     * @return true si la sauvegarde a réussi, false sinon
     */
    private boolean saveAllCreatureDefinitions() {
        CreaturesDictionary dictionary = new CreaturesDictionary();
        dictionary.metadata = new Metadata();
        dictionary.metadata.version = "1.0";
        dictionary.metadata.description = "Dictionnaire des créatures de Ryuukon Palace";
        dictionary.metadata.lastUpdated = java.time.LocalDateTime.now().toString();
        dictionary.creatures = new ArrayList<>(creatureCache.values());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREATURES_FILE))) {
            gson.toJson(dictionary, writer);
            LOGGER.info("Saved " + dictionary.creatures.size() + " creature definitions to " + CREATURES_FILE);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save creature definitions to " + CREATURES_FILE, e);
            return false;
        }
    }
    
    /**
     * Obtenir la définition d'un PNJ par son ID
     * 
     * @param npcId ID du PNJ
     * @return Définition du PNJ, ou null si non trouvé
     */
    public NPCDefinition getNPCDefinition(int npcId) {
        // Vérifier si le PNJ est dans le cache
        if (npcCache.containsKey(npcId)) {
            return npcCache.get(npcId);
        }
        
        // Charger toutes les définitions de PNJ si le cache est vide
        if (npcCache.isEmpty()) {
            loadAllNPCDefinitions();
        }
        
        return npcCache.get(npcId);
    }
    
    /**
     * Charger toutes les définitions de PNJ
     */
    private void loadAllNPCDefinitions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NPCS_FILE))) {
            NPCsDictionary dictionary = gson.fromJson(reader, NPCsDictionary.class);
            
            if (dictionary != null && dictionary.npcs != null) {
                for (NPCDefinition definition : dictionary.npcs) {
                    npcCache.put(definition.id, definition);
                }
                LOGGER.info("Loaded " + dictionary.npcs.size() + " NPC definitions from " + NPCS_FILE);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load NPC definitions from " + NPCS_FILE, e);
        }
    }
    
    /**
     * Obtenir toutes les définitions de PNJ
     * 
     * @return Map des définitions de PNJ, avec l'ID comme clé
     */
    public Map<Integer, NPCDefinition> getAllNPCDefinitions() {
        if (npcCache.isEmpty()) {
            loadAllNPCDefinitions();
        }
        return new HashMap<>(npcCache);
    }
    
    /**
     * Obtenir la définition d'une zone de spawn par son ID
     * 
     * @param zoneId ID de la zone de spawn
     * @return Définition de la zone de spawn, ou null si non trouvée
     */
    public SpawnZoneDefinition getSpawnZoneDefinition(String zoneId) {
        // Vérifier si la zone est dans le cache
        if (spawnZoneCache.containsKey(zoneId)) {
            return spawnZoneCache.get(zoneId);
        }
        
        // Charger toutes les définitions de zones de spawn si le cache est vide
        if (spawnZoneCache.isEmpty()) {
            loadAllSpawnZoneDefinitions();
        }
        
        return spawnZoneCache.get(zoneId);
    }
    
    /**
     * Charger toutes les définitions de zones de spawn
     */
    private void loadAllSpawnZoneDefinitions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SPAWN_ZONES_FILE))) {
            SpawnZonesDictionary dictionary = gson.fromJson(reader, SpawnZonesDictionary.class);
            
            if (dictionary != null && dictionary.spawn_zones != null) {
                for (SpawnZoneDefinition definition : dictionary.spawn_zones) {
                    spawnZoneCache.put(definition.id, definition);
                }
                LOGGER.info("Loaded " + dictionary.spawn_zones.size() + " spawn zone definitions from " + SPAWN_ZONES_FILE);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load spawn zone definitions from " + SPAWN_ZONES_FILE, e);
        }
    }
    
    /**
     * Obtenir toutes les définitions de zones de spawn
     * 
     * @return Map des définitions de zones de spawn, avec l'ID comme clé
     */
    public Map<String, SpawnZoneDefinition> getAllSpawnZoneDefinitions() {
        if (spawnZoneCache.isEmpty()) {
            loadAllSpawnZoneDefinitions();
        }
        return new HashMap<>(spawnZoneCache);
    }
    
    /**
     * Recharger toutes les définitions (vider les caches)
     */
    public void reloadAllDefinitions() {
        creatureCache.clear();
        npcCache.clear();
        spawnZoneCache.clear();
        
        loadAllCreatureDefinitions();
        loadAllNPCDefinitions();
        loadAllSpawnZoneDefinitions();
        
        LOGGER.info("Reloaded all definitions");
    }
    
    /**
     * Vérifier si les fichiers de données existent
     * 
     * @return true si tous les fichiers existent, false sinon
     */
    public boolean dataFilesExist() {
        File creaturesFile = new File(CREATURES_FILE);
        File npcsFile = new File(NPCS_FILE);
        File spawnZonesFile = new File(SPAWN_ZONES_FILE);
        
        return creaturesFile.exists() && npcsFile.exists() && spawnZonesFile.exists();
    }
}

package com.ryuukonpalace.game.save;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ryuukonpalace.game.core.GameState;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.world.WorldManager;
import com.ryuukonpalace.game.world.WeatherSystem.Weather;

/**
 * Gestionnaire de sauvegarde pour Ryuukon Palace.
 * Gère la sauvegarde et le chargement des données de jeu.
 */
public class SaveManager {
    
    // Singleton instance
    private static SaveManager instance;
    
    // Dossier de sauvegarde
    private static final String SAVE_DIRECTORY = "saves";
    
    // Extension des fichiers de sauvegarde
    private static final String SAVE_EXTENSION = ".json";
    
    // Nombre maximum de sauvegardes
    public static final int MAX_SAVE_SLOTS = 5;
    
    // Intervalle d'auto-sauvegarde en millisecondes (5 minutes)
    private static final long AUTO_SAVE_INTERVAL = 5 * 60 * 1000;
    
    // Dernier temps d'auto-sauvegarde
    private long lastAutoSaveTime;
    
    // Liste des métadonnées de sauvegarde
    private List<SaveMetadata> saveMetadataList;
    
    // État d'auto-sauvegarde
    private boolean autoSaveEnabled;
    
    // Slot d'auto-sauvegarde
    private int autoSaveSlot;
    
    /**
     * Constructeur privé (singleton)
     */
    private SaveManager() {
        this.saveMetadataList = new ArrayList<>();
        this.autoSaveEnabled = true;
        this.autoSaveSlot = 0; // Slot réservé pour l'auto-sauvegarde
        this.lastAutoSaveTime = System.currentTimeMillis();
        
        // Créer le dossier de sauvegarde s'il n'existe pas
        createSaveDirectory();
        
        // Charger les métadonnées des sauvegardes existantes
        loadSaveMetadata();
    }
    
    /**
     * Obtenir l'instance unique du SaveManager
     * @return L'instance du SaveManager
     */
    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }
    
    /**
     * Créer le dossier de sauvegarde s'il n'existe pas
     */
    private void createSaveDirectory() {
        File saveDir = new File(SAVE_DIRECTORY);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
    }
    
    /**
     * Charger les métadonnées des sauvegardes existantes
     */
    private void loadSaveMetadata() {
        saveMetadataList.clear();
        
        File saveDir = new File(SAVE_DIRECTORY);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(SAVE_EXTENSION));
        
        if (saveFiles != null) {
            for (File saveFile : saveFiles) {
                try {
                    SaveMetadata metadata = loadMetadataFromFile(saveFile);
                    if (metadata != null) {
                        saveMetadataList.add(metadata);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement des métadonnées de " + saveFile.getName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Charger les métadonnées d'un fichier de sauvegarde
     * @param saveFile Fichier de sauvegarde
     * @return Métadonnées de la sauvegarde, ou null en cas d'erreur
     */
    private SaveMetadata loadMetadataFromFile(File saveFile) {
        try (FileReader reader = new FileReader(saveFile)) {
            JsonObject saveData = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (saveData.has("metadata")) {
                JsonObject metadataJson = saveData.getAsJsonObject("metadata");
                
                int slot = metadataJson.has("slot") ? metadataJson.get("slot").getAsInt() : -1;
                String playerName = metadataJson.has("playerName") ? metadataJson.get("playerName").getAsString() : "Inconnu";
                String timestamp = metadataJson.has("timestamp") ? metadataJson.get("timestamp").getAsString() : "";
                String location = metadataJson.has("location") ? metadataJson.get("location").getAsString() : "Inconnu";
                int playerLevel = metadataJson.has("playerLevel") ? metadataJson.get("playerLevel").getAsInt() : 1;
                int playTime = metadataJson.has("playTime") ? metadataJson.get("playTime").getAsInt() : 0;
                int capturedVariants = metadataJson.has("capturedVariants") ? metadataJson.get("capturedVariants").getAsInt() : 0;
                String checksum = metadataJson.has("checksum") ? metadataJson.get("checksum").getAsString() : "";
                
                SaveMetadata metadata = new SaveMetadata(
                    slot,
                    playerName,
                    timestamp,
                    location,
                    playerLevel,
                    playTime,
                    capturedVariants,
                    checksum,
                    saveFile.getPath()
                );
                
                return metadata;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture des métadonnées: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Sauvegarder l'état actuel du jeu
     * @param slot Numéro du slot de sauvegarde (0 = auto-sauvegarde, 1-5 = slots manuels)
     * @return true si la sauvegarde a réussi, false sinon
     */
    public boolean saveGame(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            System.err.println("Numéro de slot invalide: " + slot);
            return false;
        }
        
        // Obtenir l'état actuel du jeu
        GameState gameState = GameState.getInstance();
        Player player = gameState.getPlayer();
        WorldManager worldManager = WorldManager.getInstance();
        
        // Créer l'objet de sauvegarde
        JsonObject saveData = new JsonObject();
        
        // Ajouter les métadonnées
        JsonObject metadata = new JsonObject();
        metadata.addProperty("slot", slot);
        metadata.addProperty("playerName", player.getName());
        metadata.addProperty("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        metadata.addProperty("location", worldManager.getCurrentZoneName());
        metadata.addProperty("playerLevel", player.getLevel());
        metadata.addProperty("playTime", gameState.getPlayTime());
        metadata.addProperty("capturedVariants", player.getCapturedCreatures().size());
        
        saveData.add("metadata", metadata);
        
        // Ajouter les données du joueur
        JsonObject playerData = new JsonObject();
        playerData.addProperty("name", player.getName());
        playerData.addProperty("level", player.getLevel());
        playerData.addProperty("experience", player.getExperience());
        playerData.addProperty("money", player.getMoney());
        playerData.addProperty("x", player.getX());
        playerData.addProperty("y", player.getY());
        playerData.addProperty("direction", player.getDirection().toString());
        playerData.addProperty("faction", player.getFaction().toString());
        
        // Ajouter l'inventaire du joueur
        JsonObject inventoryData = new JsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        inventoryData.add("items", gson.toJsonTree(player.getInventory().getItems()));
        inventoryData.add("equipment", gson.toJsonTree(player.getInventory().getEquipment()));
        playerData.add("inventory", inventoryData);
        
        // Ajouter les créatures capturées
        JsonObject creaturesData = new JsonObject();
        creaturesData.add("captured", gson.toJsonTree(player.getCapturedCreatures()));
        playerData.add("creatures", creaturesData);
        
        saveData.add("player", playerData);
        
        // Ajouter les données du monde
        JsonObject worldData = new JsonObject();
        worldData.addProperty("currentZone", worldManager.getCurrentZoneName());
        worldData.addProperty("time", worldManager.getGameTime());
        worldData.addProperty("weather", worldManager.getCurrentWeather().toString());
        
        // Ajouter l'état des PNJ
        worldData.add("npcs", gson.toJsonTree(worldManager.getNpcsState()));
        
        // Ajouter l'état des quêtes
        worldData.add("quests", gson.toJsonTree(gameState.getQuestsState()));
        
        saveData.add("world", worldData);
        
        // Calculer le checksum
        String saveDataString = saveData.toString();
        Checksum crc32 = new CRC32();
        crc32.update(saveDataString.getBytes());
        String checksum = Long.toHexString(crc32.getValue());
        
        // Ajouter le checksum aux métadonnées
        metadata.addProperty("checksum", checksum);
        
        // Écrire le fichier de sauvegarde
        String fileName = "save_" + slot + SAVE_EXTENSION;
        String filePath = SAVE_DIRECTORY + File.separator + fileName;
        
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(saveData, writer);
            
            // Mettre à jour les métadonnées
            updateSaveMetadata(slot, player.getName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    worldManager.getCurrentZoneName(), player.getLevel(), gameState.getPlayTime(),
                    player.getCapturedCreatures().size(), checksum, filePath);
            
            System.out.println("Jeu sauvegardé avec succès dans le slot " + slot);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Charger une sauvegarde
     * @param slot Numéro du slot de sauvegarde
     * @return true si le chargement a réussi, false sinon
     */
    public boolean loadGame(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            System.err.println("Numéro de slot invalide: " + slot);
            return false;
        }
        
        // Trouver la sauvegarde correspondante
        SaveMetadata metadata = null;
        for (SaveMetadata meta : saveMetadataList) {
            if (meta.getSlot() == slot) {
                metadata = meta;
                break;
            }
        }
        
        if (metadata == null) {
            System.err.println("Aucune sauvegarde trouvée dans le slot " + slot);
            return false;
        }
        
        // Charger le fichier de sauvegarde
        try (FileReader reader = new FileReader(metadata.getFilePath())) {
            JsonObject saveData = JsonParser.parseReader(reader).getAsJsonObject();
            
            // Vérifier le checksum
            String storedChecksum = metadata.getChecksum();
            
            // Créer une copie des données sans le checksum pour calculer le nouveau checksum
            JsonObject saveDataCopy = saveData.deepCopy();
            if (saveDataCopy.has("metadata")) {
                JsonObject metadataCopy = saveDataCopy.getAsJsonObject("metadata");
                if (metadataCopy.has("checksum")) {
                    metadataCopy.remove("checksum");
                }
            }
            
            String saveDataString = saveDataCopy.toString();
            Checksum crc32 = new CRC32();
            crc32.update(saveDataString.getBytes());
            String calculatedChecksum = Long.toHexString(crc32.getValue());
            
            if (!calculatedChecksum.equals(storedChecksum)) {
                System.err.println("Erreur de checksum: la sauvegarde pourrait être corrompue");
                return false;
            }
            
            // Obtenir l'état actuel du jeu
            GameState gameState = GameState.getInstance();
            
            // Charger les données du joueur
            if (saveData.has("player")) {
                JsonObject playerData = saveData.getAsJsonObject("player");
                
                // Créer un nouveau joueur
                Player player = new Player();
                
                // Charger les attributs de base
                player.setName(playerData.get("name").getAsString());
                player.setLevel(playerData.get("level").getAsInt());
                player.setExperience(playerData.get("experience").getAsInt());
                player.setMoney(playerData.get("money").getAsInt());
                player.setX(playerData.get("x").getAsFloat());
                player.setY(playerData.get("y").getAsFloat());
                player.setDirection(Player.Direction.valueOf(playerData.get("direction").getAsString()));
                player.setFaction(Player.Faction.valueOf(playerData.get("faction").getAsString()));
                
                // Charger l'inventaire
                if (playerData.has("inventory")) {
                    JsonObject inventoryData = playerData.getAsJsonObject("inventory");
                    
                    // Charger les objets
                    if (inventoryData.has("items")) {
                        Gson gson = new Gson();
                        Type itemListType = new TypeToken<List<Item>>(){}.getType();
                        List<Item> items = gson.fromJson(inventoryData.get("items"), itemListType);
                        player.getInventory().setItems(items);
                    }
                    
                    // Charger l'équipement
                    if (inventoryData.has("equipment")) {
                        Gson gson = new Gson();
                        Type equipmentListType = new TypeToken<List<Item>>(){}.getType();
                        List<Item> equipment = gson.fromJson(inventoryData.get("equipment"), equipmentListType);
                        player.getInventory().setEquipment(equipment);
                    }
                }
                
                // Charger les créatures capturées
                if (playerData.has("creatures") && playerData.getAsJsonObject("creatures").has("captured")) {
                    Gson gson = new Gson();
                    Type creatureListType = new TypeToken<List<Creature>>(){}.getType();
                    List<Creature> capturedCreatures = gson.fromJson(
                            playerData.getAsJsonObject("creatures").get("captured"),
                            creatureListType);
                    player.setCapturedCreatures(capturedCreatures);
                }
                
                // Charger les découvertes
                if (playerData.has("discoveries")) {
                    JsonObject discoveriesData = playerData.getAsJsonObject("discoveries");
                    
                    // Charger les zones découvertes
                    if (discoveriesData.has("areas")) {
                        Gson gson = new Gson();
                        Type areaMapType = new TypeToken<Map<Integer, Boolean>>(){}.getType();
                        Map<Integer, Boolean> discoveredAreas = gson.fromJson(
                                discoveriesData.get("areas"), areaMapType);
                        gameState.setDiscoveredAreas(discoveredAreas);
                    }
                    
                    // Charger l'état des quêtes
                    if (discoveriesData.has("quests")) {
                        Gson gson = new Gson();
                        Type questMapType = new TypeToken<Map<Integer, GameState.QuestState>>(){}.getType();
                        Map<Integer, GameState.QuestState> questStates = gson.fromJson(
                                discoveriesData.get("quests"), questMapType);
                        gameState.setQuestsState(questStates);
                    }
                }
                
                // Mettre à jour le joueur dans l'état du jeu
                gameState.setPlayer(player);
            }
            
            // Charger les données du monde
            if (saveData.has("world")) {
                JsonObject worldData = saveData.getAsJsonObject("world");
                WorldManager worldManager = WorldManager.getInstance();
                
                // Charger la zone actuelle
                if (worldData.has("currentZone")) {
                    worldManager.loadZone(worldData.get("currentZone").getAsString());
                }
                
                // Charger le temps de jeu
                if (worldData.has("time")) {
                    worldManager.setGameTime(worldData.get("time").getAsFloat());
                }
                
                // Charger la météo
                if (worldData.has("weather")) {
                    worldManager.setCurrentWeather(Weather.valueOf(worldData.get("weather").getAsString()));
                }
                
                // Charger l'état des PNJ
                if (worldData.has("npcs")) {
                    Gson gson = new Gson();
                    Type npcsMapType = new TypeToken<Map<Integer, Boolean>>(){}.getType();
                    Map<Integer, Boolean> npcsState = gson.fromJson(
                            worldData.get("npcs"), npcsMapType);
                    worldManager.setNpcsState(npcsState);
                }
                
                // Charger l'état des quêtes
                if (worldData.has("quests")) {
                    Gson gson = new Gson();
                    Type questMapType = new TypeToken<Map<Integer, GameState.QuestState>>(){}.getType();
                    Map<Integer, GameState.QuestState> questStates = gson.fromJson(
                            worldData.get("quests"), questMapType);
                    gameState.setQuestsState(questStates);
                }
            }
            
            System.out.println("Jeu chargé avec succès depuis le slot " + slot);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mettre à jour les métadonnées d'une sauvegarde
     */
    private void updateSaveMetadata(int slot, String playerName, String timestamp, String location,
                                   int playerLevel, int playTime, int capturedVariants, String checksum, String filePath) {
        // Chercher si les métadonnées existent déjà pour ce slot
        SaveMetadata existingMetadata = null;
        for (SaveMetadata metadata : saveMetadataList) {
            if (metadata.getSlot() == slot) {
                existingMetadata = metadata;
                break;
            }
        }
        
        if (existingMetadata != null) {
            // Mettre à jour les métadonnées existantes
            existingMetadata.setPlayerName(playerName);
            existingMetadata.setTimestamp(timestamp);
            existingMetadata.setLocation(location);
            existingMetadata.setPlayerLevel(playerLevel);
            existingMetadata.setPlayTime(playTime);
            existingMetadata.setCapturedVariants(capturedVariants);
            existingMetadata.setChecksum(checksum);
            existingMetadata.setFilePath(filePath);
        } else {
            // Créer de nouvelles métadonnées
            SaveMetadata newMetadata = new SaveMetadata(
                slot, playerName, timestamp, location, playerLevel, playTime, capturedVariants, checksum, filePath
            );
            saveMetadataList.add(newMetadata);
        }
    }
    
    /**
     * Supprimer une sauvegarde
     * @param slot Numéro du slot de sauvegarde
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deleteSave(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            System.err.println("Numéro de slot invalide: " + slot);
            return false;
        }
        
        // Trouver la sauvegarde correspondante
        SaveMetadata metadata = null;
        for (SaveMetadata meta : saveMetadataList) {
            if (meta.getSlot() == slot) {
                metadata = meta;
                break;
            }
        }
        
        if (metadata == null) {
            System.err.println("Aucune sauvegarde trouvée dans le slot " + slot);
            return false;
        }
        
        // Supprimer le fichier
        File saveFile = new File(metadata.getFilePath());
        if (saveFile.exists() && saveFile.delete()) {
            // Supprimer les métadonnées
            saveMetadataList.remove(metadata);
            System.out.println("Sauvegarde supprimée du slot " + slot);
            return true;
        } else {
            System.err.println("Erreur lors de la suppression de la sauvegarde du slot " + slot);
            return false;
        }
    }
    
    /**
     * Vérifier si une sauvegarde existe dans un slot
     * @param slot Numéro du slot de sauvegarde
     * @return true si une sauvegarde existe, false sinon
     */
    public boolean saveExists(int slot) {
        for (SaveMetadata metadata : saveMetadataList) {
            if (metadata.getSlot() == slot) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtenir les métadonnées d'une sauvegarde
     * @param slot Numéro du slot de sauvegarde
     * @return Métadonnées de la sauvegarde, ou null si aucune sauvegarde n'existe dans ce slot
     */
    public SaveMetadata getSaveMetadata(int slot) {
        for (SaveMetadata metadata : saveMetadataList) {
            if (metadata.getSlot() == slot) {
                return metadata;
            }
        }
        return null;
    }
    
    /**
     * Obtenir la liste des métadonnées de toutes les sauvegardes
     * @return Liste des métadonnées de sauvegarde
     */
    public List<SaveMetadata> getAllSaveMetadata() {
        return new ArrayList<>(saveMetadataList);
    }
    
    /**
     * Activer ou désactiver l'auto-sauvegarde
     * @param enabled true pour activer, false pour désactiver
     */
    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
    }
    
    /**
     * Vérifier si l'auto-sauvegarde est activée
     * @return true si l'auto-sauvegarde est activée, false sinon
     */
    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }
    
    /**
     * Définir le slot d'auto-sauvegarde
     * @param slot Numéro du slot d'auto-sauvegarde
     */
    public void setAutoSaveSlot(int slot) {
        if (slot >= 0 && slot < MAX_SAVE_SLOTS) {
            this.autoSaveSlot = slot;
        }
    }
    
    /**
     * Obtenir le slot d'auto-sauvegarde
     * @return Numéro du slot d'auto-sauvegarde
     */
    public int getAutoSaveSlot() {
        return autoSaveSlot;
    }
    
    /**
     * Mettre à jour l'auto-sauvegarde
     * Appelé régulièrement pour vérifier si une auto-sauvegarde est nécessaire
     */
    public void updateAutoSave() {
        if (!autoSaveEnabled) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAutoSaveTime >= AUTO_SAVE_INTERVAL) {
            // Effectuer une auto-sauvegarde
            if (saveGame(autoSaveSlot)) {
                lastAutoSaveTime = currentTime;
            }
        }
    }
    
    /**
     * Tenter de récupérer une sauvegarde corrompue
     * @param slot Numéro du slot de sauvegarde
     * @return true si la récupération a réussi, false sinon
     */
    public boolean recoverCorruptedSave(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            System.err.println("Numéro de slot invalide: " + slot);
            return false;
        }
        
        // Trouver la sauvegarde correspondante
        SaveMetadata metadata = null;
        for (SaveMetadata meta : saveMetadataList) {
            if (meta.getSlot() == slot) {
                metadata = meta;
                break;
            }
        }
        
        if (metadata == null) {
            System.err.println("Aucune sauvegarde trouvée dans le slot " + slot);
            return false;
        }
        
        // Créer une copie de sauvegarde
        String backupPath = metadata.getFilePath() + ".bak";
        try {
            Files.copy(Paths.get(metadata.getFilePath()), Paths.get(backupPath));
        } catch (IOException e) {
            System.err.println("Erreur lors de la création de la copie de sauvegarde: " + e.getMessage());
            return false;
        }
        
        // Tenter de charger et de réparer la sauvegarde
        try (FileReader reader = new FileReader(metadata.getFilePath())) {
            JsonObject saveData = JsonParser.parseReader(reader).getAsJsonObject();
            
            // Vérifier les sections essentielles
            boolean needsRepair = false;
            
            // Vérifier les métadonnées
            if (!saveData.has("metadata")) {
                saveData.add("metadata", new JsonObject());
                needsRepair = true;
            }
            
            // Vérifier les données du joueur
            if (!saveData.has("player")) {
                saveData.add("player", new JsonObject());
                needsRepair = true;
            }
            
            // Vérifier les données du monde
            if (!saveData.has("world")) {
                saveData.add("world", new JsonObject());
                needsRepair = true;
            }
            
            // Si des réparations sont nécessaires, sauvegarder le fichier réparé
            if (needsRepair) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (FileWriter writer = new FileWriter(metadata.getFilePath())) {
                    gson.toJson(saveData, writer);
                    System.out.println("Sauvegarde réparée avec succès");
                    return true;
                } catch (IOException e) {
                    System.err.println("Erreur lors de l'écriture de la sauvegarde réparée: " + e.getMessage());
                    
                    // Restaurer la copie de sauvegarde
                    try {
                        Files.copy(Paths.get(backupPath), Paths.get(metadata.getFilePath()));
                    } catch (IOException ex) {
                        System.err.println("Erreur lors de la restauration de la copie de sauvegarde: " + ex.getMessage());
                    }
                    
                    return false;
                }
            } else {
                System.out.println("Aucune réparation nécessaire pour la sauvegarde");
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la tentative de réparation: " + e.getMessage());
            
            // Restaurer la copie de sauvegarde
            try {
                Files.copy(Paths.get(backupPath), Paths.get(metadata.getFilePath()));
            } catch (IOException ex) {
                System.err.println("Erreur lors de la restauration de la copie de sauvegarde: " + ex.getMessage());
            }
            
            return false;
        } finally {
            // Supprimer la copie de sauvegarde
            try {
                Files.delete(Paths.get(backupPath));
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression de la copie de sauvegarde: " + e.getMessage());
            }
        }
    }
}

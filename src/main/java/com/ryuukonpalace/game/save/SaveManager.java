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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ryuukonpalace.game.core.GameState;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureFactory;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.items.ItemFactory;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.quest.Quest;
import com.ryuukonpalace.game.quest.QuestManager;
import com.ryuukonpalace.game.quest.QuestStatus;
import com.ryuukonpalace.game.world.WorldManager;
import com.ryuukonpalace.game.world.WeatherSystem.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

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
        
        // Obtenir l'état du jeu
        GameState gameState = GameState.getInstance();
        Player player = gameState.getPlayer();
        
        if (player == null) {
            System.err.println("Aucun joueur actif");
            return false;
        }
        
        // Créer l'objet de sauvegarde
        JsonObject saveData = new JsonObject();
        
        // Ajouter les métadonnées
        JsonObject metadata = new JsonObject();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String currentLocation = WorldManager.getInstance().getCurrentZoneName();
        metadata.addProperty("slot", slot);
        metadata.addProperty("playerName", player.getName());
        metadata.addProperty("timestamp", timestamp);
        metadata.addProperty("location", currentLocation);
        metadata.addProperty("playerLevel", player.getLevel());
        metadata.addProperty("playTime", gameState.getPlayTime());
        metadata.addProperty("capturedVariants", player.getCapturedCreatures().size());
        saveData.add("metadata", metadata);
        
        // Ajouter les données du joueur
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject playerData = new JsonObject();
        playerData.addProperty("name", player.getName());
        playerData.addProperty("level", player.getLevel());
        playerData.addProperty("experience", player.getExperience());
        playerData.addProperty("money", player.getMoney());
        playerData.addProperty("x", player.getX());
        playerData.addProperty("y", player.getY());
        playerData.addProperty("direction", player.getDirection().toString());
        
        // Ajouter les créatures capturées
        JsonArray capturedCreatures = new JsonArray();
        for (Creature creature : player.getCapturedCreatures()) {
            JsonObject creatureData = new JsonObject();
            creatureData.addProperty("id", creature.getId());
            creatureData.addProperty("level", creature.getLevel());
            creatureData.addProperty("experience", creature.getExperience());
            capturedCreatures.add(creatureData);
        }
        playerData.add("capturedCreatures", capturedCreatures);
        
        // Ajouter l'inventaire
        JsonArray inventory = new JsonArray();
        for (Item item : player.getInventory().getItems()) {
            JsonObject itemData = new JsonObject();
            itemData.addProperty("id", item.getId());
            inventory.add(itemData);
        }
        playerData.add("inventory", inventory);
        
        saveData.add("player", playerData);
        
        // Ajouter les données du monde
        JsonObject worldData = new JsonObject();
        worldData.addProperty("currentZone", WorldManager.getInstance().getCurrentZoneName());
        worldData.addProperty("weather", WorldManager.getInstance().getCurrentWeather().toString());
        worldData.addProperty("time", WorldManager.getInstance().getGameTime());
        saveData.add("world", worldData);
        
        // Ajouter l'état des quêtes
        JsonObject questsData = new JsonObject();
        Type questStateMapType = new TypeToken<Map<Integer, GameState.QuestState>>(){}.getType();
        questsData.add("questsState", gson.toJsonTree(gameState.getQuestsState(), questStateMapType));
        saveData.add("quests", questsData);
        
        // Ajouter les zones découvertes
        JsonObject areasData = new JsonObject();
        Type areasMapType = new TypeToken<Map<Integer, Boolean>>(){}.getType();
        areasData.add("discoveredAreas", gson.toJsonTree(gameState.getDiscoveredAreas(), areasMapType));
        saveData.add("areas", areasData);
        
        // Ajouter la progression de l'histoire
        JsonObject storyProgressionData = new JsonObject();
        Type storyProgressionMapType = new TypeToken<Map<String, Object>>(){}.getType();
        storyProgressionData.add("storyProgressionState", gson.toJsonTree(gameState.getStoryProgressionState(), storyProgressionMapType));
        
        // Ajouter les choix du joueur
        Type playerChoicesMapType = new TypeToken<Map<String, String>>(){}.getType();
        storyProgressionData.add("playerChoices", gson.toJsonTree(gameState.getPlayerChoices(), playerChoicesMapType));
        
        saveData.add("storyProgression", storyProgressionData);
        
        // Générer le checksum
        String jsonString = gson.toJson(saveData);
        Checksum crc32 = new CRC32();
        crc32.update(jsonString.getBytes());
        String checksum = Long.toHexString(crc32.getValue());
        metadata.addProperty("checksum", checksum);
        
        // Créer le nom du fichier
        String fileName = "save_" + slot + SAVE_EXTENSION;
        String filePath = SAVE_DIRECTORY + File.separator + fileName;
        
        // Sauvegarder le fichier
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(saveData, writer);
            
            // Mettre à jour les métadonnées
            updateSaveMetadata(slot, player.getName(), timestamp, currentLocation, player.getLevel(), gameState.getPlayTime(), player.getCapturedCreatures().size(), checksum, filePath);
            
            System.out.println("Sauvegarde réussie dans le slot " + slot);
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
            String jsonString = saveData.toString();
            // Retirer le checksum pour le calcul
            JsonObject metadataObj = saveData.getAsJsonObject("metadata");
            String savedChecksum = metadataObj.get("checksum").getAsString();
            metadataObj.remove("checksum");
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            jsonString = gson.toJson(saveData);
            
            Checksum crc32 = new CRC32();
            crc32.update(jsonString.getBytes());
            String calculatedChecksum = Long.toHexString(crc32.getValue());
            
            if (!calculatedChecksum.equals(savedChecksum)) {
                System.err.println("Erreur de checksum: la sauvegarde est corrompue");
                return false;
            }
            
            // Restaurer le checksum
            metadataObj.addProperty("checksum", savedChecksum);
            
            // Obtenir l'état du jeu
            GameState gameState = GameState.getInstance();
            
            // Charger les données du joueur
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
            
            // Charger les créatures capturées
            if (playerData.has("capturedCreatures")) {
                JsonArray capturedCreaturesArray = playerData.getAsJsonArray("capturedCreatures");
                for (int i = 0; i < capturedCreaturesArray.size(); i++) {
                    JsonObject creatureObj = capturedCreaturesArray.get(i).getAsJsonObject();
                    int creatureId = creatureObj.get("id").getAsInt();
                    int creatureLevel = creatureObj.get("level").getAsInt();
                    int creatureExp = creatureObj.get("experience").getAsInt();
                    
                    // Créer la créature en utilisant CreatureFactory pour obtenir une instance correcte
                    Creature creature = CreatureFactory.createCreature(
                        creatureId, creatureLevel
                    );
                    
                    // Mettre à jour les attributs spécifiques
                    creature.setExperience(creatureExp);
                    
                    player.addCreature(creature);
                }
            }
            
            // Charger l'inventaire
            if (playerData.has("inventory")) {
                JsonArray inventoryArray = playerData.getAsJsonArray("inventory");
                for (int i = 0; i < inventoryArray.size(); i++) {
                    JsonObject itemObj = inventoryArray.get(i).getAsJsonObject();
                    int itemId = itemObj.get("id").getAsInt();
                    
                    // Créer l'objet en utilisant ItemFactory pour obtenir une instance correcte
                    Item item = ItemFactory.getInstance().createItem(itemId);
                    
                    player.getInventory().addItem(item);
                }
            }
            
            // Définir le joueur
            gameState.setPlayer(player);
            
            // Charger les données du monde
            JsonObject worldData = saveData.getAsJsonObject("world");
            String currentZone = worldData.get("currentZone").getAsString();
            Weather weather = Weather.valueOf(worldData.get("weather").getAsString());
            float time = worldData.get("time").getAsFloat();
            
            // Initialiser le monde
            WorldManager worldManager = WorldManager.getInstance();
            worldManager.setCurrentZoneName(currentZone);
            worldManager.setCurrentWeather(weather);
            worldManager.setGameTime(time);
            
            // Charger l'état des quêtes
            if (saveData.has("quests")) {
                JsonObject questsData = saveData.getAsJsonObject("quests");
                Type questStateMapType = new TypeToken<Map<Integer, GameState.QuestState>>(){}.getType();
                Map<Integer, GameState.QuestState> questsState = gson.fromJson(questsData.get("questsState"), questStateMapType);
                gameState.setQuestsState(questsState);
            }
            
            // Charger les zones découvertes
            if (saveData.has("areas")) {
                JsonObject areasData = saveData.getAsJsonObject("areas");
                Type areasMapType = new TypeToken<Map<Integer, Boolean>>(){}.getType();
                Map<Integer, Boolean> discoveredAreas = gson.fromJson(areasData.get("discoveredAreas"), areasMapType);
                gameState.setDiscoveredAreas(discoveredAreas);
            }
            
            // Charger la progression de l'histoire
            if (saveData.has("storyProgression")) {
                JsonObject storyProgressionData = saveData.getAsJsonObject("storyProgression");
                
                // Charger l'état de la progression
                if (storyProgressionData.has("storyProgressionState")) {
                    Type storyProgressionMapType = new TypeToken<Map<String, Object>>(){}.getType();
                    Map<String, Object> storyProgressionState = gson.fromJson(storyProgressionData.get("storyProgressionState"), storyProgressionMapType);
                    gameState.setStoryProgressionState(storyProgressionState);
                }
                
                // Charger les choix du joueur
                if (storyProgressionData.has("playerChoices")) {
                    Type playerChoicesMapType = new TypeToken<Map<String, String>>(){}.getType();
                    Map<String, String> playerChoices = gson.fromJson(storyProgressionData.get("playerChoices"), playerChoicesMapType);
                    gameState.setPlayerChoices(playerChoices);
                }
            }
            
            // Définir le temps de jeu
            gameState.setPlayTime(metadataObj.get("playTime").getAsInt());
            
            // Définir l'état du jeu
            gameState.setCurrentState(GameState.State.PLAYING);
            
            System.out.println("Chargement réussi depuis le slot " + slot);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
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
    
    /**
     * Sauvegarder les données des quêtes
     * @param filePath Chemin du fichier de sauvegarde
     * @return true si la sauvegarde a réussi, false sinon
     */
    public boolean saveQuestData(String filePath) {
        try {
            QuestManager questManager = QuestManager.getInstance();
            
            JSONObject questData = new JSONObject();
            
            // Sauvegarder les quêtes disponibles
            JSONArray availableQuestsArray = new JSONArray();
            for (Quest quest : questManager.getAvailableQuests()) {
                JSONObject questJson = quest.toJson();
                questJson.put("status", QuestStatus.AVAILABLE.toString());
                availableQuestsArray.put(questJson);
            }
            questData.put("availableQuests", availableQuestsArray);
            
            // Sauvegarder les quêtes actives
            JSONArray activeQuestsArray = new JSONArray();
            for (Quest quest : questManager.getActiveQuests()) {
                JSONObject questJson = quest.toJson();
                questJson.put("status", QuestStatus.ACTIVE.toString());
                activeQuestsArray.put(questJson);
            }
            questData.put("activeQuests", activeQuestsArray);
            
            // Sauvegarder les quêtes complétées
            JSONArray completedQuestsArray = new JSONArray();
            for (Quest quest : questManager.getCompletedQuests()) {
                JSONObject questJson = quest.toJson();
                questJson.put("status", QuestStatus.COMPLETED.toString());
                completedQuestsArray.put(questJson);
            }
            questData.put("completedQuests", completedQuestsArray);
            
            // Écrire les données dans un fichier
            File questFile = new File(filePath + ".quests.json");
            try (FileWriter writer = new FileWriter(questFile)) {
                writer.write(questData.toString(4)); // Indentation de 4 espaces
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde des quêtes: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Charger les données des quêtes
     * @return Le contenu JSON des quêtes chargées, ou null si le chargement a échoué
     */
    public String loadQuestData() {
        try {
            // Obtenir l'instance du gestionnaire de quêtes (sera utilisé dans une future implémentation)
            QuestManager.getInstance();
            
            // Trouver le slot de sauvegarde actif
            int activeSlot = -1;
            for (SaveMetadata metadata : saveMetadataList) {
                if (metadata.isActive()) {
                    activeSlot = metadata.getSlot();
                    break;
                }
            }
            
            if (activeSlot == -1) {
                System.err.println("Aucune sauvegarde active trouvée");
                return null;
            }
            
            // Construire le chemin du fichier de quêtes
            String questFilePath = SAVE_DIRECTORY + "/save_" + activeSlot + SAVE_EXTENSION + ".quests.json";
            File questFile = new File(questFilePath);
            
            if (!questFile.exists()) {
                System.err.println("Fichier de quêtes non trouvé: " + questFilePath);
                return null;
            }
            
            // Lire le fichier
            StringBuilder content = new StringBuilder();
            try (FileReader reader = new FileReader(questFile)) {
                int c;
                while ((c = reader.read()) != -1) {
                    content.append((char) c);
                }
            }
            
            // Retourner le contenu JSON
            return content.toString();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des quêtes: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

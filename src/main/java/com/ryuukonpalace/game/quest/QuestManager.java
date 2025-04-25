package com.ryuukonpalace.game.quest;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.quest.objectives.*;
import com.ryuukonpalace.game.save.SaveManager;
import com.ryuukonpalace.game.utils.ResourceManager;
import com.ryuukonpalace.game.world.TimeSystem;
import com.ryuukonpalace.game.world.WeatherSystem;
import com.ryuukonpalace.game.world.WorldManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Gestionnaire de quêtes.
 * Responsable du chargement, de la sauvegarde, de l'assignation et du suivi des quêtes.
 */
public class QuestManager implements QuestCallback {
    
    // Instance unique (singleton)
    private static QuestManager instance;
    
    // Liste de toutes les quêtes disponibles
    private Map<String, Quest> availableQuests;
    
    // Liste des quêtes actives (en cours)
    private Map<String, Quest> activeQuests;
    
    // Liste des quêtes complétées
    private Map<String, Quest> completedQuests;
    
    // Liste des quêtes échouées
    private Map<String, Quest> failedQuests;
    
    // Liste des quêtes abandonnées
    private Map<String, Quest> abandonedQuests;
    
    // Joueur
    private Player player;
    
    // Gestionnaire de monde
    private WorldManager worldManager;
    
    // Callbacks externes
    private List<QuestCallback> callbacks;
    
    /**
     * Constructeur privé (singleton)
     */
    private QuestManager() {
        availableQuests = new HashMap<>();
        activeQuests = new HashMap<>();
        completedQuests = new HashMap<>();
        failedQuests = new HashMap<>();
        abandonedQuests = new HashMap<>();
        callbacks = new ArrayList<>();
    }
    
    /**
     * Obtenir l'instance unique
     * 
     * @return Instance unique
     */
    public static QuestManager getInstance() {
        if (instance == null) {
            instance = new QuestManager();
        }
        return instance;
    }
    
    /**
     * Initialiser le gestionnaire de quêtes
     * 
     * @param player Joueur
     * @param worldManager Gestionnaire de monde
     */
    public void initialize(Player player, WorldManager worldManager) {
        this.player = player;
        this.worldManager = worldManager;
        
        // Charger les quêtes depuis les fichiers JSON
        loadQuests();
    }
    
    /**
     * Charger les quêtes depuis le fichier JSON
     */
    private void loadQuests() {
        try {
            // Charger le fichier JSON principal des quêtes
            loadMainQuests();
            
            // Charger les quêtes de l'histoire principale
            loadMainStoryQuests();
            
            // Charger les quêtes secondaires
            loadSideQuests();
            
            // Charger les quêtes de faction
            loadFactionQuests();
            
            System.out.println("Quêtes chargées avec succès: " + availableQuests.size() + " quêtes disponibles");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des quêtes: " + e.getMessage());
            e.printStackTrace();
            createTestQuests(); // Fallback sur les quêtes de test
        }
    }
    
    /**
     * Charger les quêtes principales depuis le fichier quests.json
     */
    private void loadMainQuests() {
        try {
            // Charger le fichier JSON
            InputStream is = ResourceManager.class.getResourceAsStream("/data/quests.json");
            if (is == null) {
                System.err.println("Impossible de trouver le fichier quests.json");
                return;
            }
            
            // Lire le contenu du fichier
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            
            // Parser le JSON
            JSONObject json = new JSONObject(jsonContent.toString());
            JSONArray questsArray = json.getJSONArray("quests");
            
            // Parcourir les quêtes
            for (int i = 0; i < questsArray.length(); i++) {
                JSONObject questJson = questsArray.getJSONObject(i);
                Quest quest = createQuestFromJson(questJson);
                if (quest != null) {
                    availableQuests.put(quest.getId(), quest);
                }
            }
            
            System.out.println("Quêtes principales chargées avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des quêtes principales: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charger les quêtes de l'histoire principale depuis le fichier main_story_quests.json
     */
    private void loadMainStoryQuests() {
        try {
            // Charger le fichier JSON
            InputStream is = ResourceManager.class.getResourceAsStream("/data/main_story_quests.json");
            if (is == null) {
                System.err.println("Impossible de trouver le fichier main_story_quests.json");
                return;
            }
            
            // Lire le contenu du fichier
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            
            // Parser le JSON
            JSONObject json = new JSONObject(jsonContent.toString());
            JSONArray questsArray = json.getJSONArray("main_story_quests");
            
            // Parcourir les quêtes
            for (int i = 0; i < questsArray.length(); i++) {
                JSONObject questJson = questsArray.getJSONObject(i);
                Quest quest = createQuestFromJson(questJson);
                if (quest != null) {
                    availableQuests.put(quest.getId(), quest);
                }
            }
            
            System.out.println("Quêtes de l'histoire principale chargées avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des quêtes de l'histoire principale: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charger les quêtes secondaires depuis le fichier side_quests.json
     */
    private void loadSideQuests() {
        try {
            // Charger le fichier JSON
            InputStream is = ResourceManager.class.getResourceAsStream("/data/side_quests.json");
            if (is == null) {
                System.err.println("Impossible de trouver le fichier side_quests.json");
                return;
            }
            
            // Lire le contenu du fichier
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            
            // Parser le JSON
            JSONObject json = new JSONObject(jsonContent.toString());
            JSONArray questsArray = json.getJSONArray("side_quests");
            
            // Parcourir les quêtes
            for (int i = 0; i < questsArray.length(); i++) {
                JSONObject questJson = questsArray.getJSONObject(i);
                Quest quest = createQuestFromJson(questJson);
                if (quest != null) {
                    availableQuests.put(quest.getId(), quest);
                }
            }
            
            System.out.println("Quêtes secondaires chargées avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des quêtes secondaires: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charger les quêtes de faction depuis le fichier faction_quests.json
     */
    private void loadFactionQuests() {
        try {
            // Charger le fichier JSON
            InputStream is = ResourceManager.class.getResourceAsStream("/data/faction_quests.json");
            if (is == null) {
                System.err.println("Impossible de trouver le fichier faction_quests.json");
                return;
            }
            
            // Lire le contenu du fichier
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            
            // Parser le JSON
            JSONObject json = new JSONObject(jsonContent.toString());
            JSONObject factionsJson = json.getJSONObject("faction_quests");
            
            // Parcourir les factions
            for (String factionName : factionsJson.keySet()) {
                JSONArray questsArray = factionsJson.getJSONArray(factionName);
                
                // Parcourir les quêtes de la faction
                for (int i = 0; i < questsArray.length(); i++) {
                    JSONObject questJson = questsArray.getJSONObject(i);
                    Quest quest = createQuestFromJson(questJson);
                    if (quest != null) {
                        availableQuests.put(quest.getId(), quest);
                    }
                }
            }
            
            System.out.println("Quêtes de faction chargées avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des quêtes de faction: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Créer une quête à partir d'un objet JSON
     * 
     * @param questJson Objet JSON contenant les données de la quête
     * @return Quête créée, ou null en cas d'erreur
     */
    private Quest createQuestFromJson(JSONObject questJson) {
        try {
            // Créer la quête
            String id = questJson.getString("id");
            String title = questJson.getString("title");
            String description = questJson.getString("description");
            int minLevel = questJson.getInt("minLevel");
            boolean isMainQuest = questJson.getBoolean("isMainQuest");
            
            Quest quest = new Quest(id, title, description);
            quest.setMinLevel(minLevel);
            quest.setMainQuest(isMainQuest);
            
            // Vérifier si c'est une quête spécifique à une faction
            if (questJson.has("isFactionSpecific") && questJson.getBoolean("isFactionSpecific")) {
                quest.setFactionSpecific(true);
                quest.setFactionName(questJson.getString("factionName"));
            }
            
            // Ajouter les objectifs
            JSONArray objectivesArray = questJson.getJSONArray("objectives");
            for (int j = 0; j < objectivesArray.length(); j++) {
                JSONObject objectiveJson = objectivesArray.getJSONObject(j);
                QuestObjective objective = createObjectiveFromJson(objectiveJson);
                if (objective != null) {
                    quest.addObjective(objective);
                }
            }
            
            // Ajouter les récompenses
            JSONArray rewardsArray = questJson.getJSONArray("rewards");
            for (int j = 0; j < rewardsArray.length(); j++) {
                JSONObject rewardJson = rewardsArray.getJSONObject(j);
                QuestReward reward = createRewardFromJson(rewardJson);
                if (reward != null) {
                    quest.addReward(reward);
                }
            }
            
            // Ajouter les quêtes débloquées
            if (questJson.has("unlocksQuests")) {
                JSONArray unlocksQuestsArray = questJson.getJSONArray("unlocksQuests");
                for (int j = 0; j < unlocksQuestsArray.length(); j++) {
                    String unlockedQuestId = unlocksQuestsArray.getString(j);
                    quest.addUnlockedQuest(unlockedQuestId);
                }
            }
            
            return quest;
        } catch (Exception e) {
            System.err.println("Erreur lors de la création d'une quête depuis JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Créer un objectif de quête à partir des données JSON
     * 
     * @param objectiveJson Données JSON de l'objectif
     * @return Objectif de quête créé
     */
    private QuestObjective createObjectiveFromJson(JSONObject objectiveJson) {
        String type = objectiveJson.getString("type");
        String id = objectiveJson.getString("id");
        
        switch (type) {
            case "TALK_TO_NPC":
                int npcId = objectiveJson.getInt("npcId");
                String npcName = objectiveJson.getString("npcName");
                String npcTypeStr = objectiveJson.getString("npcType");
                TalkToNPCObjective.NPCType npcType = TalkToNPCObjective.NPCType.valueOf(npcTypeStr);
                return new TalkToNPCObjective(id, npcId, npcName, npcType);
                
            case "EXPLORE_AREA":
                String areaId = objectiveJson.getString("areaId");
                String areaName = objectiveJson.getString("areaName");
                int x = objectiveJson.getInt("x");
                int y = objectiveJson.getInt("y");
                int width = objectiveJson.getInt("width");
                int height = objectiveJson.getInt("height");
                
                ExploreAreaObjective exploreObjective = new ExploreAreaObjective(id, areaId, areaName, x, y, width, height);
                
                // Conditions environnementales optionnelles
                if (objectiveJson.has("requireDaytime")) {
                    exploreObjective.setRequireDaytime(objectiveJson.getBoolean("requireDaytime"));
                }
                if (objectiveJson.has("requireNighttime")) {
                    exploreObjective.setRequireNighttime(objectiveJson.getBoolean("requireNighttime"));
                }
                if (objectiveJson.has("requireWeather")) {
                    exploreObjective.setRequireWeather(objectiveJson.getString("requireWeather"));
                }
                
                return exploreObjective;
                
            case "DEFEAT_CREATURES":
                int defeatCount = objectiveJson.getInt("count");
                DefeatCreaturesObjective defeatObjective = new DefeatCreaturesObjective(id, defeatCount);
                
                // Type de créature optionnel
                if (objectiveJson.has("creatureType")) {
                    defeatObjective.setCreatureType(objectiveJson.getString("creatureType"));
                }
                
                return defeatObjective;
                
            case "COLLECT_ITEMS":
                int itemId = objectiveJson.getInt("itemId");
                String itemName = objectiveJson.getString("itemName");
                int quantity = objectiveJson.getInt("quantity");
                boolean consumeItems = objectiveJson.getBoolean("consumeItems");
                return new CollectItemsObjective(id, itemId, itemName, quantity, consumeItems);
                
            case "CAPTURE_VARIANTS":
                int captureCount = objectiveJson.getInt("count");
                CaptureVariantsObjective captureObjective = new CaptureVariantsObjective(id, captureCount);
                
                // Type de variant optionnel
                if (objectiveJson.has("variantType")) {
                    captureObjective.setVariantType(objectiveJson.getString("variantType"));
                }
                
                return captureObjective;
                
            default:
                System.err.println("Type d'objectif inconnu: " + type);
                return null;
        }
    }
    
    /**
     * Créer une récompense de quête à partir des données JSON
     * 
     * @param rewardJson Données JSON de la récompense
     * @return Récompense de quête créée
     */
    private QuestReward createRewardFromJson(JSONObject rewardJson) {
        String type = rewardJson.getString("type");
        
        switch (type) {
            case "EXPERIENCE":
                int expAmount = rewardJson.getInt("amount");
                return QuestReward.createExperienceReward(expAmount);
                
            case "MONEY":
                int moneyAmount = rewardJson.getInt("amount");
                return QuestReward.createMoneyReward(moneyAmount);
                
            case "ITEM":
                int itemId = rewardJson.getInt("itemId");
                String itemName = rewardJson.getString("itemName");
                int quantity = rewardJson.getInt("quantity");
                return QuestReward.createItemReward(itemId, itemName, quantity);
                
            case "REPUTATION":
                String factionName = rewardJson.getString("factionName");
                int repAmount = rewardJson.getInt("amount");
                return QuestReward.createReputationReward(factionName, repAmount);
                
            default:
                System.err.println("Type de récompense inconnu: " + type);
                return null;
        }
    }
    
    /**
     * Créer des quêtes de test
     */
    private void createTestQuests() {
        // Quête principale: Bienvenue à Ryuukon Palace
        Quest mainQuest = new Quest("main_quest_1", "Bienvenue à Ryuukon Palace", "Découvrez le monde de Ryuukon Palace et apprenez les bases du jeu.", 1);
        mainQuest.setMainQuest(true);
        
        // Objectifs
        QuestObjective talkToGuide = new TalkToNPCObjective("talk_to_guide", 1, "Guide du village", TalkToNPCObjective.NPCType.TACTICIEN);
        QuestObjective exploreVillage = new ExploreAreaObjective("explore_village", "village_1", "Village de départ", 100, 100, 200, 200);
        QuestObjective defeatTrainer = new DefeatCreaturesObjective("defeat_trainer", 1);
        
        // Ajouter les objectifs
        mainQuest.addObjective(talkToGuide);
        mainQuest.addObjective(exploreVillage);
        mainQuest.addObjective(defeatTrainer);
        
        // Récompenses
        mainQuest.addReward(QuestReward.createExperienceReward(100));
        mainQuest.addReward(QuestReward.createMoneyReward(500));
        mainQuest.addReward(QuestReward.createItemReward(1, "Pierre de capture basique", 5));
        
        // Ajouter la quête
        availableQuests.put(mainQuest.getId(), mainQuest);
        
        // Quête secondaire: Collecte de plantes
        Quest sideQuest = new Quest("side_quest_1", "Collecte de plantes médicinales", "Collectez des plantes médicinales pour l'herboriste du village.", 2);
        
        // Objectifs
        QuestObjective collectHerbs = new CollectItemsObjective("collect_herbs", 10, "Plante médicinale", 5, true);
        QuestObjective returnToHerbalist = new TalkToNPCObjective("return_to_herbalist", 2, "Herboriste", TalkToNPCObjective.NPCType.NEUTRE);
        
        // Ajouter les objectifs
        sideQuest.addObjective(collectHerbs);
        sideQuest.addObjective(returnToHerbalist);
        
        // Récompenses
        sideQuest.addReward(QuestReward.createExperienceReward(50));
        sideQuest.addReward(QuestReward.createMoneyReward(200));
        sideQuest.addReward(QuestReward.createItemReward(2, "Potion de soin", 3));
        
        // Ajouter la quête
        availableQuests.put(sideQuest.getId(), sideQuest);
        
        // Quête de faction: Aide à la guilde des Tacticiens
        Quest factionQuest = new Quest("faction_quest_1", "Aide à la guilde des Tacticiens", "Aidez la guilde des Tacticiens à capturer des variants rares.", 5);
        factionQuest.setFactionSpecific(true);
        factionQuest.setFactionName("Guilde des Tacticiens");
        
        // Objectifs
        QuestObjective captureVariants = new CaptureVariantsObjective("capture_variants", 3);
        
        // Ajouter les objectifs
        factionQuest.addObjective(captureVariants);
        
        // Récompenses
        factionQuest.addReward(QuestReward.createExperienceReward(200));
        factionQuest.addReward(QuestReward.createReputationReward("Guilde des Tacticiens", 50));
        factionQuest.addReward(QuestReward.createItemReward(3, "Pierre de capture avancée", 2));
        
        // Ajouter la quête
        availableQuests.put(factionQuest.getId(), factionQuest);
    }
    
    /**
     * Mettre à jour les quêtes
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     */
    public void update(float deltaTime) {
        // Mettre à jour les quêtes actives
        for (Quest quest : activeQuests.values()) {
            quest.update(deltaTime, player);
        }
        
        // Vérifier les quêtes qui peuvent être démarrées automatiquement
        checkAutoStartQuests();
        
        // Vérifier les conditions environnementales pour les quêtes
        checkEnvironmentalConditions();
    }
    
    /**
     * Vérifier les conditions environnementales pour les quêtes
     */
    private void checkEnvironmentalConditions() {
        if (worldManager == null) {
            return;
        }
        
        // Vérifier les conditions liées à l'heure du jour
        boolean isDay = isDaytime();
        boolean isNight = !isDay;
        
        // Vérifier les conditions liées à la météo
        WeatherSystem.Weather currentWeather = worldManager.getCurrentWeather();
        String weatherString = currentWeather.toString();
        
        // Mettre à jour les quêtes en fonction des conditions environnementales
        for (Quest quest : activeQuests.values()) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof ExploreAreaObjective) {
                    ExploreAreaObjective exploreObjective = (ExploreAreaObjective) objective;
                    
                    // Vérifier si l'objectif a des conditions spécifiques
                    if (exploreObjective.requiresDaytime() && !isDay) {
                        continue;
                    }
                    
                    if (exploreObjective.requiresNighttime() && !isNight) {
                        continue;
                    }
                    
                    if (exploreObjective.requiresSpecificWeather() && 
                        !exploreObjective.getRequiredWeather().equals(weatherString)) {
                        continue;
                    }
                    
                    // Mettre à jour la progression si le joueur est dans la zone
                    if (isPlayerInArea(exploreObjective.getAreaId())) {
                        exploreObjective.updateExploration(player);
                    }
                }
            }
        }
    }
    
    /**
     * Vérifier si c'est le jour dans le jeu
     * 
     * @return true si c'est le jour, false si c'est la nuit
     */
    private boolean isDaytime() {
        if (worldManager == null) {
            return true; // Par défaut, considérer qu'il fait jour
        }
        
        TimeSystem timeSystem = worldManager.getTimeSystem();
        TimeSystem.TimeOfDay timeOfDay = timeSystem.getCurrentTimeOfDay();
        
        // Considérer que c'est le jour pendant MORNING et AFTERNOON
        return timeOfDay == TimeSystem.TimeOfDay.MORNING || 
               timeOfDay == TimeSystem.TimeOfDay.AFTERNOON;
    }
    
    /**
     * Vérifier si le joueur est dans une zone spécifique
     * 
     * @param areaId ID de la zone
     * @return true si le joueur est dans la zone, false sinon
     */
    private boolean isPlayerInArea(String areaId) {
        if (player == null || worldManager == null) {
            return false;
        }
        
        // Vérifier si le joueur a découvert cette zone
        return player.hasDiscoveredArea(areaId);
    }
    
    /**
     * Vérifier les quêtes disponibles qui peuvent être démarrées automatiquement
     */
    private void checkAutoStartQuests() {
        List<Quest> questsToStart = new ArrayList<>();
        
        for (Quest quest : availableQuests.values()) {
            // Vérifier si la quête peut être démarrée
            if (canStartQuest(quest)) {
                questsToStart.add(quest);
            }
        }
        
        // Démarrer les quêtes
        for (Quest quest : questsToStart) {
            startQuest(quest.getId());
        }
    }
    
    /**
     * Vérifier si une quête peut être démarrée
     * 
     * @param quest Quête à vérifier
     * @return true si la quête peut être démarrée, false sinon
     */
    public boolean canStartQuest(Quest quest) {
        // Vérifier si la quête est déjà active, complétée, échouée ou abandonnée
        if (activeQuests.containsKey(quest.getId()) || 
            completedQuests.containsKey(quest.getId()) || 
            failedQuests.containsKey(quest.getId()) || 
            abandonedQuests.containsKey(quest.getId())) {
            return false;
        }
        
        // Vérifier le niveau minimum
        if (player.getLevel() < quest.getMinLevel()) {
            return false;
        }
        
        // Vérifier si la quête est spécifique à une faction
        if (quest.isFactionSpecific()) {
            if (!player.isMemberOfFaction(quest.getFactionName())) {
                return false;
            }
        }
        
        // Vérifier les prérequis
        for (String prerequisiteId : quest.getPrerequisites()) {
            if (!completedQuests.containsKey(prerequisiteId)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Démarrer une quête
     * 
     * @param questId ID de la quête à démarrer
     * @return true si la quête a été démarrée, false sinon
     */
    public boolean startQuest(String questId) {
        // Vérifier si la quête existe
        Quest quest = availableQuests.get(questId);
        if (quest == null) {
            System.err.println("Quête introuvable: " + questId);
            return false;
        }
        
        // Vérifier si la quête est déjà active
        if (activeQuests.containsKey(questId)) {
            System.err.println("La quête est déjà active: " + questId);
            return false;
        }
        
        // Vérifier si la quête est déjà complétée
        if (completedQuests.containsKey(questId)) {
            System.err.println("La quête est déjà complétée: " + questId);
            return false;
        }
        
        // Vérifier si la quête est déjà échouée
        if (failedQuests.containsKey(questId)) {
            System.err.println("La quête a déjà échoué: " + questId);
            return false;
        }
        
        // Vérifier si la quête est déjà abandonnée
        if (abandonedQuests.containsKey(questId)) {
            System.err.println("La quête a déjà été abandonnée: " + questId);
            return false;
        }
        
        // Vérifier le niveau minimum requis
        if (player.getLevel() < quest.getMinLevel()) {
            System.err.println("Niveau insuffisant pour la quête: " + questId + " (Niveau requis: " + quest.getMinLevel() + ")");
            return false;
        }
        
        // Vérifier si la quête est spécifique à une faction
        if (quest.isFactionSpecific()) {
            String factionName = quest.getFactionName();
            if (!player.isMemberOfFaction(factionName)) {
                System.err.println("Le joueur n'appartient pas à la faction requise pour cette quête: " + factionName);
                return false;
            }
        }
        
        // Démarrer la quête
        if (quest.start(player)) {
            // Ajouter la quête aux quêtes actives
            activeQuests.put(questId, quest);
            
            // Notifier le callback
            onQuestStarted(quest);
            
            System.out.println("Quête démarrée: " + quest.getTitle());
            return true;
        }
        
        return false;
    }
    
    /**
     * Compléter une quête
     * 
     * @param questId ID de la quête à compléter
     * @return true si la quête a été complétée, false sinon
     */
    public boolean completeQuest(String questId) {
        // Vérifier si la quête est active
        Quest quest = activeQuests.get(questId);
        if (quest == null) {
            return false;
        }
        
        // Compléter la quête
        if (quest.complete()) {
            // Ajouter la quête aux quêtes complétées
            activeQuests.remove(questId);
            completedQuests.put(questId, quest);
            
            // Donner les récompenses
            for (QuestReward reward : quest.getRewards()) {
                reward.giveToPlayer(player);
            }
            
            // Débloquer les quêtes suivantes
            for (String unlockedQuestId : quest.getUnlocksQuests()) {
                Quest unlockedQuest = availableQuests.get(unlockedQuestId);
                if (unlockedQuest != null) {
                    // Vérifier si la quête peut être démarrée automatiquement
                    if (canStartQuest(unlockedQuest)) {
                        startQuest(unlockedQuestId);
                    }
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Échouer une quête
     * 
     * @param questId ID de la quête à échouer
     * @param reason Raison de l'échec
     * @return true si la quête a échoué, false sinon
     */
    public boolean failQuest(String questId, String reason) {
        // Vérifier si la quête est active
        Quest quest = activeQuests.get(questId);
        if (quest == null) {
            return false;
        }
        
        // Échouer la quête
        if (quest.fail(reason)) {
            // Ajouter la quête aux quêtes échouées
            activeQuests.remove(questId);
            failedQuests.put(questId, quest);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Abandonner une quête
     * 
     * @param questId ID de la quête à abandonner
     * @return true si la quête a été abandonnée, false sinon
     */
    public boolean abandonQuest(String questId) {
        // Vérifier si la quête est active
        Quest quest = activeQuests.get(questId);
        if (quest == null) {
            return false;
        }
        
        // Abandonner la quête
        if (quest.abandon()) {
            // Ajouter la quête aux quêtes abandonnées
            activeQuests.remove(questId);
            abandonedQuests.put(questId, quest);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Réinitialiser une quête
     * 
     * @param questId ID de la quête à réinitialiser
     * @return true si la quête a été réinitialisée, false sinon
     */
    public boolean resetQuest(String questId) {
        // Vérifier si la quête existe
        Quest quest = null;
        
        if (activeQuests.containsKey(questId)) {
            quest = activeQuests.get(questId);
            activeQuests.remove(questId);
        } else if (completedQuests.containsKey(questId)) {
            quest = completedQuests.get(questId);
            completedQuests.remove(questId);
        } else if (failedQuests.containsKey(questId)) {
            quest = failedQuests.get(questId);
            failedQuests.remove(questId);
        } else if (abandonedQuests.containsKey(questId)) {
            quest = abandonedQuests.get(questId);
            abandonedQuests.remove(questId);
        }
        
        if (quest == null) {
            return false;
        }
        
        // Réinitialiser la quête
        if (quest.reset()) {
            // Ajouter la quête aux quêtes disponibles
            availableQuests.put(questId, quest);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Obtenir une quête par son ID
     * 
     * @param questId ID de la quête
     * @return Quête correspondante, ou null si non trouvée
     */
    public Quest getQuestById(String questId) {
        return getQuest(questId, availableQuests, activeQuests, completedQuests, failedQuests, abandonedQuests);
    }
    
    private Quest getQuest(String questId, Map<String, Quest>... maps) {
        for (Map<String, Quest> map : maps) {
            if (map.containsKey(questId)) {
                return map.get(questId);
            }
        }
        return null;
    }
    
    /**
     * Vérifier si une quête est complétée
     * 
     * @param questId ID de la quête
     * @return true si la quête est complétée, false sinon
     */
    public boolean isQuestCompleted(String questId) {
        return completedQuests.containsKey(questId);
    }
    
    /**
     * Débloquer une quête pour la rendre disponible
     * 
     * @param questId ID de la quête à débloquer
     * @return true si la quête a été débloquée, false sinon
     */
    public boolean unlockQuest(String questId) {
        // Vérifier si la quête existe dans les quêtes disponibles
        if (!availableQuests.containsKey(questId)) {
            System.err.println("Quête non trouvée: " + questId);
            return false;
        }
        
        // Vérifier si la quête est déjà active, complétée, échouée ou abandonnée
        if (activeQuests.containsKey(questId) || 
            completedQuests.containsKey(questId) || 
            failedQuests.containsKey(questId) || 
            abandonedQuests.containsKey(questId)) {
            System.err.println("La quête " + questId + " est déjà débloquée ou terminée");
            return false;
        }
        
        // Obtenir la quête
        Quest quest = availableQuests.get(questId);
        
        // Marquer la quête comme disponible pour le joueur
        quest.setAvailable(true);
        
        // Notifier les callbacks
        for (QuestCallback callback : callbacks) {
            if (callback != null) {
                callback.onQuestAvailable(quest);
            }
        }
        
        System.out.println("Quête débloquée: " + quest.getTitle());
        return true;
    }
    
    /**
     * Sauvegarder l'état des quêtes
     * 
     * @param saveManager Gestionnaire de sauvegarde
     */
    public void save(SaveManager saveManager) {
        if (saveManager == null) return;
        
        try {
            // Créer un objet JSON pour stocker l'état des quêtes
            JSONObject questsData = new JSONObject();
            
            // Sauvegarder les quêtes actives
            JSONArray activeQuestsArray = new JSONArray();
            for (Quest quest : activeQuests.values()) {
                activeQuestsArray.put(quest.toJson());
            }
            questsData.put("activeQuests", activeQuestsArray);
            
            // Sauvegarder les quêtes complétées (juste les IDs)
            JSONArray completedQuestsArray = new JSONArray();
            for (String questId : completedQuests.keySet()) {
                completedQuestsArray.put(questId);
            }
            questsData.put("completedQuests", completedQuestsArray);
            
            // Sauvegarder les quêtes échouées (juste les IDs)
            JSONArray failedQuestsArray = new JSONArray();
            for (String questId : failedQuests.keySet()) {
                failedQuestsArray.put(questId);
            }
            questsData.put("failedQuests", failedQuestsArray);
            
            // Sauvegarder les quêtes abandonnées (juste les IDs)
            JSONArray abandonedQuestsArray = new JSONArray();
            for (String questId : abandonedQuests.keySet()) {
                abandonedQuestsArray.put(questId);
            }
            questsData.put("abandonedQuests", abandonedQuestsArray);
            
            // Enregistrer les données dans le gestionnaire de sauvegarde
            saveManager.saveQuestData(questsData.toString());
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde des quêtes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charger l'état des quêtes
     * 
     * @param saveManager Gestionnaire de sauvegarde
     */
    public void load(SaveManager saveManager) {
        if (saveManager == null) return;
        
        try {
            // Récupérer les données de quêtes depuis le gestionnaire de sauvegarde
            String questDataStr = saveManager.loadQuestData();
            if (questDataStr == null || questDataStr.isEmpty()) {
                return; // Pas de données à charger
            }
            
            // Réinitialiser les listes de quêtes
            activeQuests.clear();
            completedQuests.clear();
            failedQuests.clear();
            abandonedQuests.clear();
            
            // Charger les données depuis le JSON
            JSONObject questData = new JSONObject(questDataStr);
            
            // Charger les quêtes actives
            if (questData.has("activeQuests")) {
                JSONArray activeQuestsArray = questData.getJSONArray("activeQuests");
                for (int i = 0; i < activeQuestsArray.length(); i++) {
                    JSONObject questJson = activeQuestsArray.getJSONObject(i);
                    String questId = questJson.getString("id");
                    
                    // Récupérer la quête depuis les quêtes disponibles
                    if (availableQuests.containsKey(questId)) {
                        Quest quest = Quest.fromJson(questJson); // Utiliser la méthode statique correctement
                        activeQuests.put(questId, quest);
                    }
                }
            }
            
            // Charger les quêtes complétées
            if (questData.has("completedQuests")) {
                JSONArray completedQuestsArray = questData.getJSONArray("completedQuests");
                for (int i = 0; i < completedQuestsArray.length(); i++) {
                    String questId = completedQuestsArray.getString(i);
                    
                    // Récupérer la quête depuis les quêtes disponibles
                    if (availableQuests.containsKey(questId)) {
                        Quest quest = availableQuests.get(questId).clone();
                        quest.setStatus(QuestStatus.COMPLETED);
                        completedQuests.put(questId, quest);
                    }
                }
            }
            
            // Charger les quêtes échouées
            if (questData.has("failedQuests")) {
                JSONArray failedQuestsArray = questData.getJSONArray("failedQuests");
                for (int i = 0; i < failedQuestsArray.length(); i++) {
                    String questId = failedQuestsArray.getString(i);
                    
                    // Récupérer la quête depuis les quêtes disponibles
                    if (availableQuests.containsKey(questId)) {
                        Quest quest = availableQuests.get(questId).clone();
                        quest.setStatus(QuestStatus.FAILED);
                        failedQuests.put(questId, quest);
                    }
                }
            }
            
            // Charger les quêtes abandonnées
            if (questData.has("abandonedQuests")) {
                JSONArray abandonedQuestsArray = questData.getJSONArray("abandonedQuests");
                for (int i = 0; i < abandonedQuestsArray.length(); i++) {
                    String questId = abandonedQuestsArray.getString(i);
                    
                    // Récupérer la quête depuis les quêtes disponibles
                    if (availableQuests.containsKey(questId)) {
                        Quest quest = availableQuests.get(questId).clone();
                        quest.setStatus(QuestStatus.ABANDONED);
                        abandonedQuests.put(questId, quest);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des quêtes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fusionner plusieurs maps de quêtes en une seule
     * 
     * @param maps Maps à fusionner
     * @return Map fusionnée
     */
    @SafeVarargs
    @SuppressWarnings("unused")
    private Map<String, Quest> mergeMaps(Map<String, Quest>... maps) {
        Map<String, Quest> result = new HashMap<>();
        
        for (Map<String, Quest> map : maps) {
            result.putAll(map);
        }
        
        return result;
    }

    // Implémentation de l'interface QuestCallback
    
    @Override
    public void onQuestStarted(Quest quest) {
        System.out.println("Quête démarrée: " + quest.getTitle());
    }
    
    @Override
    public void onQuestCompleted(Quest quest) {
        System.out.println("Quête complétée: " + quest.getTitle());
    }
    
    @Override
    public void onQuestFailed(Quest quest, String reason) {
        System.out.println("Quête échouée: " + quest.getTitle() + " - Raison: " + reason);
    }
    
    @Override
    public void onQuestAbandoned(Quest quest) {
        System.out.println("Quête abandonnée: " + quest.getTitle());
    }
    
    @Override
    public void onQuestAvailable(Quest quest) {
        System.out.println("Quête disponible: " + quest.getTitle());
    }
    
    @Override
    public void onObjectiveUpdated(Quest quest, QuestObjective objective) {
        System.out.println("Objectif mis à jour: " + objective.getDescription() + " - Quête: " + quest.getTitle());
    }
    
    @Override
    public void onObjectiveCompleted(Quest quest, QuestObjective objective) {
        System.out.println("Objectif complété: " + objective.getDescription() + " - Quête: " + quest.getTitle());
    }
    
    @Override
    public void onObjectiveFailed(Quest quest, QuestObjective objective) {
        System.out.println("Objectif échoué: " + objective.getDescription() + " - Quête: " + quest.getTitle());
    }
    
    /**
     * Obtenir toutes les quêtes disponibles
     * 
     * @return Liste des quêtes disponibles
     */
    public List<Quest> getAvailableQuests() {
        return new ArrayList<>(availableQuests.values());
    }
    
    /**
     * Obtenir toutes les quêtes actives
     * 
     * @return Liste des quêtes actives
     */
    public List<Quest> getActiveQuests() {
        return new ArrayList<>(activeQuests.values());
    }
    
    /**
     * Obtenir toutes les quêtes complétées
     * 
     * @return Liste des quêtes complétées
     */
    public List<Quest> getCompletedQuests() {
        return new ArrayList<>(completedQuests.values());
    }
    
    /**
     * Notifier le gestionnaire qu'une créature a été vaincue
     * 
     * @param creature Créature vaincue
     */
    public void onCreatureDefeated(Creature creature) {
        for (Quest quest : activeQuests.values()) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof DefeatCreaturesObjective) {
                    DefeatCreaturesObjective defeatObjective = (DefeatCreaturesObjective) objective;
                    defeatObjective.onCreatureDefeated(creature);
                }
            }
        }
    }
    
    /**
     * Notifier le gestionnaire qu'un variant a été capturé
     * 
     * @param creature Variant capturé
     */
    public void onVariantCaptured(Creature creature) {
        for (Quest quest : activeQuests.values()) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof CaptureVariantsObjective) {
                    CaptureVariantsObjective captureObjective = (CaptureVariantsObjective) objective;
                    captureObjective.onVariantCaptured(creature);
                }
            }
        }
    }
    
    /**
     * Notifier le gestionnaire qu'un objet a été ajouté à l'inventaire
     * 
     * @param item Objet ajouté
     * @param count Nombre d'objets ajoutés
     */
    public void onItemAdded(Item item, int count) {
        for (Quest quest : activeQuests.values()) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof CollectItemsObjective) {
                    CollectItemsObjective collectObjective = (CollectItemsObjective) objective;
                    collectObjective.onItemAdded(item, count);
                }
            }
        }
    }
    
    /**
     * Notifier le gestionnaire que le joueur a parlé à un PNJ
     * 
     * @param npcId ID du PNJ
     * @param dialogueId ID du dialogue
     */
    public void onTalkToNPC(int npcId, int dialogueId) {
        for (Quest quest : activeQuests.values()) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof TalkToNPCObjective) {
                    TalkToNPCObjective talkObjective = (TalkToNPCObjective) objective;
                    talkObjective.onTalkToNPC(npcId, dialogueId);
                }
            }
        }
    }
    
    /**
     * Ajouter un callback
     * 
     * @param callback Callback à ajouter
     */
    public void addCallback(QuestCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }
    
    /**
     * Supprimer un callback
     * 
     * @param callback Callback à supprimer
     */
    public void removeCallback(QuestCallback callback) {
        callbacks.remove(callback);
    }
}

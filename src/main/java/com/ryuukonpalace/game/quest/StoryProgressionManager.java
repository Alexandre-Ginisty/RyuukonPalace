package com.ryuukonpalace.game.quest;

import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.save.SaveManager;
import com.ryuukonpalace.game.utils.ResourceManager;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.items.ItemFactory;
import com.ryuukonpalace.game.core.GameState;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire de progression de l'histoire.
 * Permet de suivre la progression du joueur dans l'histoire principale et de débloquer
 * de nouvelles quêtes en fonction de ses actions.
 */
public class StoryProgressionManager {
    
    // Instance unique (singleton)
    private static StoryProgressionManager instance;
    
    // Référence au gestionnaire de quêtes
    private QuestManager questManager;
    
    // Référence au joueur
    private Player player;
    
    // Chapitres de l'histoire
    private List<StoryChapter> chapters;
    
    // Chapitre actuel
    private int currentChapterIndex;
    
    // Points de décision de l'histoire
    private Map<String, StoryDecisionPoint> decisionPoints;
    
    // Choix effectués par le joueur
    private Map<String, String> playerChoices;
    
    /**
     * Constructeur privé (singleton)
     */
    private StoryProgressionManager() {
        this.chapters = new ArrayList<>();
        this.currentChapterIndex = 0;
        this.decisionPoints = new HashMap<>();
        this.playerChoices = new HashMap<>();
    }
    
    /**
     * Obtenir l'instance unique
     * 
     * @return Instance unique
     */
    public static StoryProgressionManager getInstance() {
        if (instance == null) {
            instance = new StoryProgressionManager();
        }
        return instance;
    }
    
    /**
     * Initialiser le gestionnaire
     * 
     * @param questManager Gestionnaire de quêtes
     * @param player Joueur
     */
    public void initialize(QuestManager questManager, Player player) {
        this.questManager = questManager;
        this.player = player;
        
        // Charger les chapitres de l'histoire
        loadStoryChapters();
        
        // Charger les points de décision
        loadDecisionPoints();
    }
    
    /**
     * Charger les chapitres de l'histoire depuis le fichier JSON
     */
    private void loadStoryChapters() {
        try {
            // Charger le fichier JSON
            InputStream is = ResourceManager.class.getResourceAsStream("/data/story_chapters.json");
            if (is == null) {
                System.err.println("Impossible de trouver le fichier story_chapters.json");
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
            JSONArray chaptersArray = json.getJSONArray("chapters");
            
            // Parcourir les chapitres
            for (int i = 0; i < chaptersArray.length(); i++) {
                JSONObject chapterJson = chaptersArray.getJSONObject(i);
                
                // Créer le chapitre
                String id = chapterJson.getString("id");
                String title = chapterJson.getString("title");
                String description = chapterJson.getString("description");
                
                StoryChapter chapter = new StoryChapter(id, title, description);
                
                // Ajouter les quêtes principales du chapitre
                JSONArray mainQuestsArray = chapterJson.getJSONArray("mainQuests");
                for (int j = 0; j < mainQuestsArray.length(); j++) {
                    String questId = mainQuestsArray.getString(j);
                    chapter.addMainQuest(questId);
                }
                
                // Ajouter les quêtes secondaires du chapitre
                if (chapterJson.has("sideQuests")) {
                    JSONArray sideQuestsArray = chapterJson.getJSONArray("sideQuests");
                    for (int j = 0; j < sideQuestsArray.length(); j++) {
                        String questId = sideQuestsArray.getString(j);
                        chapter.addSideQuest(questId);
                    }
                }
                
                // Ajouter les points de décision du chapitre
                if (chapterJson.has("decisionPoints")) {
                    JSONArray decisionPointsArray = chapterJson.getJSONArray("decisionPoints");
                    for (int j = 0; j < decisionPointsArray.length(); j++) {
                        String decisionPointId = decisionPointsArray.getString(j);
                        chapter.addDecisionPoint(decisionPointId);
                    }
                }
                
                // Ajouter le chapitre à la liste
                chapters.add(chapter);
            }
            
            System.out.println("Chapitres de l'histoire chargés avec succès: " + chapters.size() + " chapitres");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des chapitres de l'histoire: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charger les points de décision depuis le fichier JSON
     */
    private void loadDecisionPoints() {
        try {
            // Charger le fichier JSON
            InputStream is = ResourceManager.class.getResourceAsStream("/data/story_decisions.json");
            if (is == null) {
                System.err.println("Impossible de trouver le fichier story_decisions.json");
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
            JSONArray decisionsArray = json.getJSONArray("decisions");
            
            // Parcourir les points de décision
            for (int i = 0; i < decisionsArray.length(); i++) {
                JSONObject decisionJson = decisionsArray.getJSONObject(i);
                
                // Créer le point de décision
                String id = decisionJson.getString("id");
                String title = decisionJson.getString("title");
                String description = decisionJson.getString("description");
                
                StoryDecisionPoint decisionPoint = new StoryDecisionPoint(id, title, description);
                
                // Ajouter les choix possibles
                JSONArray choicesArray = decisionJson.getJSONArray("choices");
                for (int j = 0; j < choicesArray.length(); j++) {
                    JSONObject choiceJson = choicesArray.getJSONObject(j);
                    
                    String choiceId = choiceJson.getString("id");
                    String choiceText = choiceJson.getString("text");
                    
                    // Ajouter les conséquences du choix
                    Map<String, Object> consequences = new HashMap<>();
                    if (choiceJson.has("consequences")) {
                        JSONObject consequencesJson = choiceJson.getJSONObject("consequences");
                        
                        // Ajouter les quêtes débloquées
                        if (consequencesJson.has("unlocksQuests")) {
                            JSONArray unlocksQuestsArray = consequencesJson.getJSONArray("unlocksQuests");
                            List<String> unlocksQuests = new ArrayList<>();
                            for (int k = 0; k < unlocksQuestsArray.length(); k++) {
                                unlocksQuests.add(unlocksQuestsArray.getString(k));
                            }
                            consequences.put("unlocksQuests", unlocksQuests);
                        }
                        
                        // Ajouter les changements de réputation
                        if (consequencesJson.has("reputationChanges")) {
                            JSONObject reputationChangesJson = consequencesJson.getJSONObject("reputationChanges");
                            Map<String, Integer> reputationChanges = new HashMap<>();
                            for (String factionName : reputationChangesJson.keySet()) {
                                reputationChanges.put(factionName, reputationChangesJson.getInt(factionName));
                            }
                            consequences.put("reputationChanges", reputationChanges);
                        }
                        
                        // Ajouter les objets reçus
                        if (consequencesJson.has("items")) {
                            JSONArray itemsArray = consequencesJson.getJSONArray("items");
                            List<Map<String, Object>> items = new ArrayList<>();
                            for (int k = 0; k < itemsArray.length(); k++) {
                                JSONObject itemJson = itemsArray.getJSONObject(k);
                                Map<String, Object> item = new HashMap<>();
                                item.put("id", itemJson.getInt("id"));
                                item.put("name", itemJson.getString("name"));
                                item.put("quantity", itemJson.getInt("quantity"));
                                items.add(item);
                            }
                            consequences.put("items", items);
                        }
                        
                        // Ajouter l'expérience reçue
                        if (consequencesJson.has("experience")) {
                            consequences.put("experience", consequencesJson.getInt("experience"));
                        }
                    }
                    
                    decisionPoint.addChoice(choiceId, choiceText, consequences);
                }
                
                // Ajouter le point de décision à la map
                decisionPoints.put(id, decisionPoint);
            }
            
            System.out.println("Points de décision chargés avec succès: " + decisionPoints.size() + " points de décision");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des points de décision: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Démarrer le chapitre actuel
     * 
     * @return true si le chapitre a été démarré, false sinon
     */
    public boolean startCurrentChapter() {
        if (currentChapterIndex >= chapters.size()) {
            System.err.println("Aucun chapitre disponible");
            return false;
        }
        
        StoryChapter chapter = chapters.get(currentChapterIndex);
        
        // Démarrer les quêtes principales du chapitre
        for (String questId : chapter.getMainQuests()) {
            questManager.startQuest(questId);
        }
        
        System.out.println("Chapitre démarré: " + chapter.getTitle());
        return true;
    }
    
    /**
     * Passer au chapitre suivant
     * 
     * @return true si le chapitre suivant a été démarré, false sinon
     */
    public boolean advanceToNextChapter() {
        currentChapterIndex++;
        
        if (currentChapterIndex >= chapters.size()) {
            System.out.println("Fin de l'histoire");
            return false;
        }
        
        return startCurrentChapter();
    }
    
    /**
     * Vérifier si le chapitre actuel est terminé
     * 
     * @return true si le chapitre est terminé, false sinon
     */
    public boolean isCurrentChapterCompleted() {
        if (currentChapterIndex >= chapters.size()) {
            return false;
        }
        
        StoryChapter currentChapter = chapters.get(currentChapterIndex);
        
        // Vérifier si toutes les quêtes principales du chapitre sont terminées
        for (String questId : currentChapter.getMainQuests()) {
            Quest quest = questManager.getQuestById(questId);
            if (quest == null) {
                continue;
            }
            
            if (!questManager.isQuestCompleted(questId)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Obtenir le chapitre actuel
     * 
     * @return Chapitre actuel
     */
    public StoryChapter getCurrentChapter() {
        if (currentChapterIndex >= chapters.size()) {
            return null;
        }
        
        return chapters.get(currentChapterIndex);
    }
    
    /**
     * Obtenir un point de décision par son ID
     * 
     * @param decisionPointId ID du point de décision
     * @return Point de décision, ou null s'il n'existe pas
     */
    public StoryDecisionPoint getDecisionPoint(String decisionPointId) {
        return decisionPoints.get(decisionPointId);
    }
    
    /**
     * Effectuer un choix pour un point de décision
     * 
     * @param decisionPointId ID du point de décision
     * @param choiceId ID du choix
     * @return true si le choix a été effectué, false sinon
     */
    public boolean makeChoice(String decisionPointId, String choiceId) {
        StoryDecisionPoint decisionPoint = decisionPoints.get(decisionPointId);
        if (decisionPoint == null) {
            System.err.println("Point de décision introuvable: " + decisionPointId);
            return false;
        }
        
        Map<String, Object> consequences = decisionPoint.getChoiceConsequences(choiceId);
        if (consequences == null) {
            System.err.println("Choix introuvable: " + choiceId);
            return false;
        }
        
        // Enregistrer le choix
        playerChoices.put(decisionPointId, choiceId);
        
        // Appliquer les conséquences
        applyChoiceConsequences(consequences);
        
        System.out.println("Choix effectué: " + decisionPoint.getChoiceText(choiceId));
        return true;
    }
    
    /**
     * Appliquer les conséquences d'un choix
     * 
     * @param consequences Conséquences du choix
     */
    private void applyChoiceConsequences(Map<String, Object> consequences) {
        if (consequences == null) {
            return;
        }
        
        // Parcourir les conséquences
        for (Map.Entry<String, Object> entry : consequences.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            switch (key) {
                case "unlockQuest":
                    // Débloquer une quête
                    if (value instanceof String) {
                        questManager.unlockQuest((String) value);
                    }
                    break;
                case "completeQuest":
                    // Terminer une quête
                    if (value instanceof String) {
                        questManager.completeQuest((String) value);
                    }
                    break;
                case "failQuest":
                    // Échouer une quête
                    if (value instanceof String) {
                        questManager.failQuest((String) value, "Échec suite à un choix du joueur");
                    }
                    break;
                case "addItem":
                    // Ajouter un objet à l'inventaire du joueur
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemData = (Map<String, Object>) value;
                        int itemId = ((Number) itemData.get("id")).intValue();
                        
                        // Créer et ajouter l'objet
                        Item item = ItemFactory.getInstance().createItem(itemId);
                        if (item != null) {
                            // Ajouter la quantité spécifiée si disponible
                            int quantity = 1;
                            if (itemData.containsKey("quantity")) {
                                quantity = ((Number) itemData.get("quantity")).intValue();
                            }
                            
                            // Ajouter l'item autant de fois que nécessaire
                            for (int i = 0; i < quantity; i++) {
                                player.getInventory().addItem(item);
                                
                                // Pour les items suivants, créer de nouvelles instances
                                if (i < quantity - 1) {
                                    item = ItemFactory.getInstance().createItem(itemId);
                                    if (item == null) break;
                                }
                            }
                        }
                    }
                    break;
                case "setReputation":
                    // Modifier la réputation du joueur auprès d'une faction
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> repData = (Map<String, Object>) value;
                        String faction = (String) repData.get("faction");
                        int amount = ((Number) repData.get("amount")).intValue();
                        
                        // Mettre à jour la réputation
                        player.setFactionReputation(faction, amount);
                    }
                    break;
                case "advanceChapter":
                    // Passer au chapitre suivant
                    if (value instanceof Boolean && (Boolean) value) {
                        advanceToNextChapter();
                    }
                    break;
            }
        }
    }
    
    /**
     * Sauvegarder l'état de la progression de l'histoire
     * 
     * @param saveManager Gestionnaire de sauvegarde
     */
    public void save(SaveManager saveManager) {
        // Utiliser la méthode saveProgressionState pour obtenir les données de progression
        Map<String, Object> progressionState = saveProgressionState();
        
        // Mettre à jour l'état du jeu avec les données de progression
        GameState gameState = GameState.getInstance();
        gameState.setStoryProgressionState(progressionState);
        gameState.setPlayerChoices(playerChoices);
        
        System.out.println("Sauvegarde de la progression de l'histoire terminée avec succès");
    }
    
    /**
     * Charger l'état de la progression de l'histoire
     * 
     * @param saveManager Gestionnaire de sauvegarde
     */
    public void load(SaveManager saveManager) {
        // Obtenir l'état du jeu
        GameState gameState = GameState.getInstance();
        
        // Charger l'état de progression
        Map<String, Object> progressionState = gameState.getStoryProgressionState();
        if (progressionState != null) {
            loadProgressionState(progressionState);
        }
        
        // Charger les choix du joueur
        Map<String, String> choices = gameState.getPlayerChoices();
        if (choices != null) {
            playerChoices.clear();
            playerChoices.putAll(choices);
        }
        
        System.out.println("Chargement de la progression de l'histoire terminé avec succès");
    }
    
    /**
     * Sauvegarder l'état de la progression de l'histoire
     * 
     * @return Map contenant l'état de la progression
     */
    public Map<String, Object> saveProgressionState() {
        Map<String, Object> state = new HashMap<>();
        
        // Sauvegarder l'index du chapitre actuel
        state.put("currentChapterIndex", currentChapterIndex);
        
        // Sauvegarder les choix du joueur
        state.put("playerChoices", new HashMap<>(playerChoices));
        
        return state;
    }
    
    /**
     * Charger l'état de la progression de l'histoire
     * 
     * @param state État de la progression
     */
    public void loadProgressionState(Map<String, Object> state) {
        if (state == null) {
            return;
        }
        
        // Charger l'index du chapitre actuel
        if (state.containsKey("currentChapterIndex")) {
            currentChapterIndex = ((Number) state.get("currentChapterIndex")).intValue();
        }
        
        // Charger les choix du joueur
        if (state.containsKey("playerChoices")) {
            @SuppressWarnings("unchecked")
            Map<String, String> choices = (Map<String, String>) state.get("playerChoices");
            playerChoices.clear();
            playerChoices.putAll(choices);
        }
    }
    
    /**
     * Classe représentant un chapitre de l'histoire
     */
    public class StoryChapter {
        
        // Identifiant unique du chapitre
        private String id;
        
        // Titre du chapitre
        private String title;
        
        // Description du chapitre
        private String description;
        
        // Quêtes principales du chapitre
        private List<String> mainQuests;
        
        // Quêtes secondaires du chapitre
        private List<String> sideQuests;
        
        // Points de décision du chapitre
        private List<String> decisionPoints;
        
        /**
         * Constructeur
         * 
         * @param id Identifiant unique du chapitre
         * @param title Titre du chapitre
         * @param description Description du chapitre
         */
        public StoryChapter(String id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.mainQuests = new ArrayList<>();
            this.sideQuests = new ArrayList<>();
            this.decisionPoints = new ArrayList<>();
        }
        
        /**
         * Obtenir l'identifiant du chapitre
         * 
         * @return Identifiant du chapitre
         */
        public String getId() {
            return id;
        }
        
        /**
         * Obtenir le titre du chapitre
         * 
         * @return Titre du chapitre
         */
        public String getTitle() {
            return title;
        }
        
        /**
         * Obtenir la description du chapitre
         * 
         * @return Description du chapitre
         */
        public String getDescription() {
            return description;
        }
        
        /**
         * Ajouter une quête principale au chapitre
         * 
         * @param questId ID de la quête
         */
        public void addMainQuest(String questId) {
            mainQuests.add(questId);
        }
        
        /**
         * Ajouter une quête secondaire au chapitre
         * 
         * @param questId ID de la quête
         */
        public void addSideQuest(String questId) {
            sideQuests.add(questId);
        }
        
        /**
         * Ajouter un point de décision au chapitre
         * 
         * @param decisionPointId ID du point de décision
         */
        public void addDecisionPoint(String decisionPointId) {
            decisionPoints.add(decisionPointId);
        }
        
        /**
         * Obtenir les quêtes principales du chapitre
         * 
         * @return Liste des IDs des quêtes principales
         */
        public List<String> getMainQuests() {
            return mainQuests;
        }
        
        /**
         * Obtenir les quêtes secondaires du chapitre
         * 
         * @return Liste des IDs des quêtes secondaires
         */
        public List<String> getSideQuests() {
            return sideQuests;
        }
        
        /**
         * Obtenir les points de décision du chapitre
         * 
         * @return Liste des IDs des points de décision
         */
        public List<String> getDecisionPoints() {
            return decisionPoints;
        }
    }
    
    /**
     * Classe représentant un point de décision dans l'histoire
     */
    public class StoryDecisionPoint {
        
        // Identifiant unique du point de décision
        private String id;
        
        // Titre du point de décision
        private String title;
        
        // Description du point de décision
        private String description;
        
        // Choix possibles (ID -> texte)
        private Map<String, String> choices;
        
        // Conséquences des choix (ID -> conséquences)
        private Map<String, Map<String, Object>> consequences;
        
        /**
         * Constructeur
         * 
         * @param id Identifiant unique du point de décision
         * @param title Titre du point de décision
         * @param description Description du point de décision
         */
        public StoryDecisionPoint(String id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.choices = new HashMap<>();
            this.consequences = new HashMap<>();
        }
        
        /**
         * Obtenir l'identifiant du point de décision
         * 
         * @return Identifiant du point de décision
         */
        public String getId() {
            return id;
        }
        
        /**
         * Obtenir le titre du point de décision
         * 
         * @return Titre du point de décision
         */
        public String getTitle() {
            return title;
        }
        
        /**
         * Obtenir la description du point de décision
         * 
         * @return Description du point de décision
         */
        public String getDescription() {
            return description;
        }
        
        /**
         * Ajouter un choix possible
         * 
         * @param choiceId ID du choix
         * @param choiceText Texte du choix
         * @param consequences Conséquences du choix
         */
        public void addChoice(String choiceId, String choiceText, Map<String, Object> consequences) {
            choices.put(choiceId, choiceText);
            this.consequences.put(choiceId, consequences);
        }
        
        /**
         * Obtenir les choix possibles
         * 
         * @return Map des choix (ID -> texte)
         */
        public Map<String, String> getChoices() {
            return choices;
        }
        
        /**
         * Obtenir le texte d'un choix
         * 
         * @param choiceId ID du choix
         * @return Texte du choix, ou null s'il n'existe pas
         */
        public String getChoiceText(String choiceId) {
            return choices.get(choiceId);
        }
        
        /**
         * Obtenir les conséquences d'un choix
         * 
         * @param choiceId ID du choix
         * @return Conséquences du choix, ou null s'il n'existe pas
         */
        public Map<String, Object> getChoiceConsequences(String choiceId) {
            return consequences.get(choiceId);
        }
    }
}

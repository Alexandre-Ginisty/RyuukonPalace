package com.ryuukonpalace.game.core;

import java.util.HashMap;
import java.util.Map;

import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.ui.SaveLoadInterface;
import com.ryuukonpalace.game.world.WorldManager;

/**
 * Classe qui gère l'état global du jeu.
 * Contient des informations sur l'état actuel du jeu, le joueur, et les quêtes.
 */
public class GameState {
    
    // Singleton instance
    private static GameState instance;
    
    // États possibles du jeu
    public enum State {
        MAIN_MENU,      // Menu principal
        PLAYING,        // En jeu
        PAUSED,         // Jeu en pause
        COMBAT,         // En combat
        DIALOG,         // En dialogue
        CUTSCENE,       // Cinématique
        GAME_OVER,      // Game over
        SAVE_LOAD       // Interface de sauvegarde/chargement
    }
    
    // État actuel du jeu
    private State currentState;
    
    // État précédent du jeu (pour revenir après une pause)
    private State previousState;
    
    // Joueur
    private Player player;
    
    // Temps de jeu en secondes
    private int playTime;
    
    // Horodatage du dernier calcul de temps de jeu
    private long lastTimeUpdate;
    
    // État des quêtes (ID de quête -> état)
    private Map<Integer, QuestState> questsState;
    
    // Zones découvertes (ID de zone -> découverte)
    private Map<Integer, Boolean> discoveredAreas;
    
    // Quêtes spéciales disponibles (ID de quête -> disponible)
    private Map<String, Boolean> specialQuests;
    
    // Interface de sauvegarde/chargement
    private SaveLoadInterface saveLoadInterface;
    
    /**
     * Constructeur privé (singleton)
     */
    private GameState() {
        this.currentState = State.MAIN_MENU;
        this.previousState = State.MAIN_MENU;
        this.player = null;
        this.playTime = 0;
        this.lastTimeUpdate = System.currentTimeMillis();
        this.questsState = new HashMap<>();
        this.discoveredAreas = new HashMap<>();
        this.specialQuests = new HashMap<>();
    }
    
    /**
     * Obtenir l'instance unique du GameState
     * @return L'instance du GameState
     */
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    /**
     * Initialiser le GameState
     */
    public void init() {
        // Créer l'interface de sauvegarde/chargement
        saveLoadInterface = new SaveLoadInterface();
        saveLoadInterface.setVisible(false);
    }
    
    /**
     * Mettre à jour l'état du jeu
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        // Mettre à jour le temps de jeu si le jeu est en cours
        if (currentState == State.PLAYING) {
            long currentTime = System.currentTimeMillis();
            playTime += (int) ((currentTime - lastTimeUpdate) / 1000);
            lastTimeUpdate = currentTime;
        }
        
        // Mettre à jour l'interface de sauvegarde/chargement si elle est visible
        if (saveLoadInterface != null && saveLoadInterface.isVisible()) {
            saveLoadInterface.update(deltaTime);
        }
    }
    
    /**
     * Dessiner les éléments spécifiques à l'état du jeu
     */
    public void render() {
        // Dessiner l'interface de sauvegarde/chargement si elle est visible
        if (saveLoadInterface != null && saveLoadInterface.isVisible()) {
            saveLoadInterface.render();
        }
    }
    
    /**
     * Démarrer une nouvelle partie
     * @param playerName Nom du joueur
     */
    public void startNewGame(String playerName) {
        // Créer un nouveau joueur
        player = new Player();
        player.setName(playerName);
        
        // Initialiser le monde
        WorldManager.getInstance().init();
        
        // Réinitialiser le temps de jeu
        playTime = 0;
        lastTimeUpdate = System.currentTimeMillis();
        
        // Réinitialiser l'état des quêtes
        questsState.clear();
        
        // Réinitialiser les zones découvertes
        discoveredAreas.clear();
        
        // Réinitialiser les quêtes spéciales
        specialQuests.clear();
        
        // Définir l'état du jeu
        currentState = State.PLAYING;
        previousState = State.PLAYING;
    }
    
    /**
     * Mettre le jeu en pause
     */
    public void pauseGame() {
        if (currentState == State.PLAYING) {
            previousState = currentState;
            currentState = State.PAUSED;
        }
    }
    
    /**
     * Reprendre le jeu après une pause
     */
    public void resumeGame() {
        if (currentState == State.PAUSED || currentState == State.SAVE_LOAD) {
            currentState = previousState;
        }
    }
    
    /**
     * Afficher l'interface de sauvegarde/chargement
     * @param saveMode true pour le mode sauvegarde, false pour le mode chargement
     */
    public void showSaveLoadInterface(boolean saveMode) {
        previousState = currentState;
        currentState = State.SAVE_LOAD;
        
        if (saveLoadInterface != null) {
            saveLoadInterface.setState(saveMode ? SaveLoadInterface.State.SAVE : SaveLoadInterface.State.LOAD);
            saveLoadInterface.setVisible(true);
        }
    }
    
    /**
     * Obtenir l'état actuel du jeu
     * @return État actuel du jeu
     */
    public State getCurrentState() {
        return currentState;
    }
    
    /**
     * Définir l'état actuel du jeu
     * @param state Nouvel état du jeu
     */
    public void setCurrentState(State state) {
        this.previousState = this.currentState;
        this.currentState = state;
    }
    
    /**
     * Obtenir le joueur
     * @return Joueur
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Définir le joueur
     * @param player Nouveau joueur
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    /**
     * Obtenir le temps de jeu en secondes
     * @return Temps de jeu en secondes
     */
    public int getPlayTime() {
        return playTime;
    }
    
    /**
     * Définir le temps de jeu en secondes
     * @param playTime Nouveau temps de jeu en secondes
     */
    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
    
    /**
     * Obtenir l'état des quêtes
     * @return État des quêtes
     */
    public Map<Integer, QuestState> getQuestsState() {
        return questsState;
    }
    
    /**
     * Définir l'état des quêtes
     * @param questsState Nouvel état des quêtes
     */
    public void setQuestsState(Map<Integer, QuestState> questsState) {
        this.questsState = questsState;
    }
    
    /**
     * Obtenir l'état d'une quête
     * @param questId ID de la quête
     * @return État de la quête, ou null si la quête n'existe pas
     */
    public QuestState getQuestState(int questId) {
        return questsState.get(questId);
    }
    
    /**
     * Définir l'état d'une quête
     * @param questId ID de la quête
     * @param state Nouvel état de la quête
     */
    public void setQuestState(int questId, QuestState state) {
        questsState.put(questId, state);
    }
    
    /**
     * Obtenir les zones découvertes
     * @return Zones découvertes
     */
    public Map<Integer, Boolean> getDiscoveredAreas() {
        return discoveredAreas;
    }
    
    /**
     * Définir les zones découvertes
     * @param discoveredAreas Nouvelles zones découvertes
     */
    public void setDiscoveredAreas(Map<Integer, Boolean> discoveredAreas) {
        this.discoveredAreas = discoveredAreas;
    }
    
    /**
     * Vérifier si une zone a été découverte
     * @param areaId ID de la zone
     * @return true si la zone a été découverte, false sinon
     */
    public boolean isAreaDiscovered(int areaId) {
        Boolean discovered = discoveredAreas.get(areaId);
        return discovered != null && discovered;
    }
    
    /**
     * Marquer une zone comme découverte
     * @param areaId ID de la zone
     * @param discovered true pour marquer comme découverte, false sinon
     */
    public void setAreaDiscovered(int areaId, boolean discovered) {
        discoveredAreas.put(areaId, discovered);
    }
    
    /**
     * Ajouter une quête spéciale
     * 
     * @param questId Identifiant de la quête spéciale
     * @return true si la quête a été ajoutée avec succès, false si elle existait déjà
     */
    public boolean addSpecialQuest(String questId) {
        if (specialQuests.containsKey(questId) && specialQuests.get(questId)) {
            // La quête est déjà disponible
            return false;
        }
        
        // Marquer la quête comme disponible
        specialQuests.put(questId, true);
        
        // Créer un état de quête pour cette quête spéciale
        int numericQuestId = parseQuestId(questId);
        if (numericQuestId > 0 && !questsState.containsKey(numericQuestId)) {
            QuestState state = new QuestState(numericQuestId);
            state.setStatus("NOT_STARTED");
            questsState.put(numericQuestId, state);
        }
        
        System.out.println("Quête spéciale ajoutée: " + questId);
        return true;
    }
    
    /**
     * Vérifier si une quête spéciale est disponible
     * 
     * @param questId Identifiant de la quête spéciale
     * @return true si la quête est disponible, false sinon
     */
    public boolean isSpecialQuestAvailable(String questId) {
        Boolean available = specialQuests.get(questId);
        return available != null && available;
    }
    
    /**
     * Définir la disponibilité d'une quête spéciale
     * 
     * @param questId Identifiant de la quête spéciale
     * @param available true si la quête est disponible, false sinon
     */
    public void setSpecialQuestAvailable(String questId, boolean available) {
        specialQuests.put(questId, available);
    }
    
    /**
     * Obtenir toutes les quêtes spéciales disponibles
     * 
     * @return Map des quêtes spéciales (ID -> disponible)
     */
    public Map<String, Boolean> getSpecialQuests() {
        return new HashMap<>(specialQuests);
    }
    
    /**
     * Définir les quêtes spéciales disponibles
     * 
     * @param specialQuests Nouvelles quêtes spéciales
     */
    public void setSpecialQuests(Map<String, Boolean> specialQuests) {
        this.specialQuests = new HashMap<>(specialQuests);
    }
    
    /**
     * Convertir un ID de quête au format chaîne en entier
     * 
     * @param questId ID de quête au format chaîne (ex: "SPECIAL_1")
     * @return ID numérique de la quête, ou -1 si la conversion échoue
     */
    private int parseQuestId(String questId) {
        try {
            // Essayer de convertir directement si c'est un nombre
            return Integer.parseInt(questId);
        } catch (NumberFormatException e) {
            // Si c'est une chaîne au format "SPECIAL_X", extraire X
            if (questId.startsWith("SPECIAL_")) {
                try {
                    return Integer.parseInt(questId.substring(8));
                } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                    // Échec de l'extraction
                    return -1;
                }
            }
            
            // Format non reconnu
            return -1;
        }
    }
    
    /**
     * Classe interne représentant l'état d'une quête
     */
    public static class QuestState {
        private int questId;
        private String status; // "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "FAILED"
        private int currentStep;
        private Map<String, Object> questData;
        
        /**
         * Constructeur
         * @param questId ID de la quête
         */
        public QuestState(int questId) {
            this.questId = questId;
            this.status = "NOT_STARTED";
            this.currentStep = 0;
            this.questData = new HashMap<>();
        }
        
        /**
         * Obtenir l'ID de la quête
         * @return ID de la quête
         */
        public int getQuestId() {
            return questId;
        }
        
        /**
         * Obtenir le statut de la quête
         * @return Statut de la quête
         */
        public String getStatus() {
            return status;
        }
        
        /**
         * Définir le statut de la quête
         * @param status Nouveau statut de la quête
         */
        public void setStatus(String status) {
            this.status = status;
        }
        
        /**
         * Obtenir l'étape actuelle de la quête
         * @return Étape actuelle de la quête
         */
        public int getCurrentStep() {
            return currentStep;
        }
        
        /**
         * Définir l'étape actuelle de la quête
         * @param currentStep Nouvelle étape de la quête
         */
        public void setCurrentStep(int currentStep) {
            this.currentStep = currentStep;
        }
        
        /**
         * Obtenir les données de la quête
         * @return Données de la quête
         */
        public Map<String, Object> getQuestData() {
            return questData;
        }
        
        /**
         * Définir les données de la quête
         * @param questData Nouvelles données de la quête
         */
        public void setQuestData(Map<String, Object> questData) {
            this.questData = questData;
        }
        
        /**
         * Ajouter une donnée à la quête
         * @param key Clé de la donnée
         * @param value Valeur de la donnée
         */
        public void addQuestData(String key, Object value) {
            questData.put(key, value);
        }
        
        /**
         * Obtenir une donnée de la quête
         * @param key Clé de la donnée
         * @return Valeur de la donnée, ou null si la clé n'existe pas
         */
        public Object getQuestData(String key) {
            return questData.get(key);
        }
    }
}

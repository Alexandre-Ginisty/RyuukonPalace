package com.ryuukonpalace.game.quest;

import com.ryuukonpalace.game.player.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Classe représentant une quête dans le jeu.
 * Une quête est composée d'objectifs à accomplir et offre des récompenses.
 */
public class Quest {
    
    // Identifiant unique de la quête
    private String id;
    
    // Titre de la quête
    private String title;
    
    // Description de la quête
    private String description;
    
    // Niveau minimum requis pour entreprendre la quête
    private int minLevel;
    
    // Indique si la quête fait partie de l'histoire principale
    private boolean mainQuest;
    
    // Indique si la quête est spécifique à une faction
    private boolean factionSpecific;
    
    // Nom de la faction concernée (si factionSpecific est true)
    private String factionName;
    
    // Liste des objectifs de la quête
    private List<QuestObjective> objectives;
    
    // Liste des récompenses de la quête
    private List<QuestReward> rewards;
    
    // État actuel de la quête
    private QuestState state;
    
    // Statut de la quête pour le système de sauvegarde
    private QuestStatus status;
    
    // Quêtes requises avant de pouvoir entreprendre celle-ci
    private List<String> prerequisites;
    
    // Quêtes débloquées une fois celle-ci terminée
    private List<String> unlocksQuests;
    
    // Dialogue de début de quête
    private String startDialogue;
    
    // Dialogue de fin de quête
    private String endDialogue;
    
    // Dialogue d'échec de quête (si applicable)
    private String failDialogue;
    
    // Temps limite pour compléter la quête (en secondes, 0 = pas de limite)
    private float timeLimit;
    
    // Temps écoulé depuis le début de la quête
    private float elapsedTime;
    
    // Callbacks pour les événements de quête
    private QuestCallback callback;
    
    // Date de complétion de la quête (si complétée)
    private String completionDate;
    
    // Raison de l'échec de la quête (si échouée)
    private String failureReason;
    
    /**
     * Constructeur
     * 
     * @param id Identifiant unique de la quête
     * @param title Titre de la quête
     * @param description Description de la quête
     */
    public Quest(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.minLevel = 1;
        this.mainQuest = false;
        this.factionSpecific = false;
        this.factionName = "";
        this.objectives = new ArrayList<>();
        this.rewards = new ArrayList<>();
        this.state = QuestState.NOT_STARTED;
        this.status = QuestStatus.AVAILABLE;
        this.prerequisites = new ArrayList<>();
        this.unlocksQuests = new ArrayList<>();
        this.startDialogue = "";
        this.endDialogue = "";
        this.failDialogue = "";
        this.timeLimit = 0;
        this.elapsedTime = 0;
    }
    
    /**
     * Constructeur avec niveau minimum
     * 
     * @param id Identifiant unique de la quête
     * @param title Titre de la quête
     * @param description Description de la quête
     * @param minLevel Niveau minimum requis
     */
    public Quest(String id, String title, String description, int minLevel) {
        this(id, title, description);
        this.minLevel = minLevel;
    }
    
    /**
     * Mettre à jour la quête
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param player Joueur
     * @return true si la quête a été mise à jour, false sinon
     */
    public boolean update(float deltaTime, Player player) {
        // Si la quête n'est pas active, ne rien faire
        if (state != QuestState.IN_PROGRESS) {
            return false;
        }
        
        // Mettre à jour le temps écoulé si la quête a une limite de temps
        if (timeLimit > 0) {
            elapsedTime += deltaTime;
            
            // Vérifier si le temps est écoulé
            if (elapsedTime >= timeLimit) {
                fail("Temps écoulé");
                return true;
            }
        }
        
        // Mettre à jour les objectifs
        boolean updated = false;
        for (QuestObjective objective : objectives) {
            if (objective.getState() == QuestObjectiveState.IN_PROGRESS) {
                if (objective.update(deltaTime, player)) {
                    updated = true;
                }
                
                // Vérifier si l'objectif est complété
                if (objective.getState() == QuestObjectiveState.COMPLETED) {
                    if (callback != null) {
                        callback.onObjectiveCompleted(this, objective);
                    }
                }
            }
        }
        
        // Vérifier si tous les objectifs sont complétés
        if (areAllObjectivesCompleted()) {
            complete();
            return true;
        }
        
        return updated;
    }
    
    /**
     * Démarrer la quête
     * 
     * @param player Joueur
     * @return true si la quête a été démarrée, false sinon
     */
    public boolean start(Player player) {
        // Vérifier si la quête peut être démarrée
        if (state != QuestState.NOT_STARTED && state != QuestState.AVAILABLE) {
            return false;
        }
        
        // Vérifier le niveau du joueur
        if (player.getLevel() < minLevel) {
            return false;
        }
        
        // Démarrer la quête
        state = QuestState.IN_PROGRESS;
        
        // Démarrer les objectifs
        for (QuestObjective objective : objectives) {
            objective.start(player);
        }
        
        // Notifier le callback
        if (callback != null) {
            callback.onQuestStarted(this);
        }
        
        return true;
    }
    
    /**
     * Compléter la quête
     * 
     * @return true si la quête a été complétée, false sinon
     */
    public boolean complete() {
        if (state != QuestState.IN_PROGRESS || !areAllObjectivesCompleted()) {
            return false;
        }
        
        state = QuestState.COMPLETED;
        
        // Enregistrer la date de complétion
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        completionDate = sdf.format(new Date());
        
        // Notifier le callback
        if (callback != null) {
            callback.onQuestCompleted(this);
        }
        
        return true;
    }
    
    /**
     * Échouer la quête
     * 
     * @param reason Raison de l'échec
     * @return true si la quête a échoué, false sinon
     */
    public boolean fail(String reason) {
        if (state != QuestState.IN_PROGRESS) {
            return false;
        }
        
        state = QuestState.FAILED;
        failureReason = reason;
        
        // Notifier le callback
        if (callback != null) {
            callback.onQuestFailed(this, reason);
        }
        
        return true;
    }
    
    /**
     * Échouer la quête sans raison spécifiée
     * 
     * @return true si la quête a échoué, false sinon
     */
    public boolean fail() {
        return fail("Échec de la quête");
    }
    
    /**
     * Abandonner la quête
     * 
     * @return true si la quête a été abandonnée, false sinon
     */
    public boolean abandon() {
        // Vérifier si la quête peut être abandonnée
        if (state != QuestState.IN_PROGRESS) {
            return false;
        }
        
        // Abandonner la quête
        state = QuestState.ABANDONED;
        
        // Notifier le callback
        if (callback != null) {
            callback.onQuestAbandoned(this);
        }
        
        return true;
    }
    
    /**
     * Réinitialiser la quête
     * 
     * @return true si la quête a été réinitialisée, false sinon
     */
    public boolean reset() {
        // Réinitialiser la quête
        state = QuestState.NOT_STARTED;
        elapsedTime = 0;
        completionDate = null;
        failureReason = null;
        
        // Réinitialiser les objectifs
        for (QuestObjective objective : objectives) {
            objective.reset();
        }
        
        return true;
    }
    
    /**
     * Vérifier si tous les objectifs sont complétés
     * 
     * @return true si tous les objectifs sont complétés, false sinon
     */
    public boolean areAllObjectivesCompleted() {
        for (QuestObjective objective : objectives) {
            if (objective.getState() != QuestObjectiveState.COMPLETED) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Ajouter un objectif à la quête
     * 
     * @param objective Objectif à ajouter
     */
    public void addObjective(QuestObjective objective) {
        objectives.add(objective);
    }
    
    /**
     * Ajouter une récompense à la quête
     * 
     * @param reward Récompense à ajouter
     */
    public void addReward(QuestReward reward) {
        rewards.add(reward);
    }
    
    /**
     * Définir le callback pour les événements de quête
     * 
     * @param callback Callback
     */
    public void setCallback(QuestCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Obtenir l'identifiant de la quête
     * 
     * @return Identifiant de la quête
     */
    public String getId() {
        return id;
    }
    
    /**
     * Obtenir le titre de la quête
     * 
     * @return Titre de la quête
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Obtenir la description de la quête
     * 
     * @return Description de la quête
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtenir le niveau minimum requis pour la quête
     * 
     * @return Niveau minimum
     */
    public int getMinLevel() {
        return minLevel;
    }
    
    /**
     * Définir le niveau minimum requis pour la quête
     * 
     * @param minLevel Niveau minimum
     */
    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }
    
    /**
     * Vérifier si la quête fait partie de l'histoire principale
     * 
     * @return true si la quête fait partie de l'histoire principale, false sinon
     */
    public boolean isMainQuest() {
        return mainQuest;
    }
    
    /**
     * Définir si la quête fait partie de l'histoire principale
     * 
     * @param mainQuest true si la quête fait partie de l'histoire principale, false sinon
     */
    public void setMainQuest(boolean mainQuest) {
        this.mainQuest = mainQuest;
    }
    
    /**
     * Vérifier si la quête est spécifique à une faction
     * 
     * @return true si la quête est spécifique à une faction, false sinon
     */
    public boolean isFactionSpecific() {
        return factionSpecific;
    }
    
    /**
     * Définir si la quête est spécifique à une faction
     * 
     * @param factionSpecific true si la quête est spécifique à une faction, false sinon
     */
    public void setFactionSpecific(boolean factionSpecific) {
        this.factionSpecific = factionSpecific;
        if (!factionSpecific) {
            this.factionName = "";
        }
    }
    
    /**
     * Définir si la quête est spécifique à une faction
     * 
     * @param factionSpecific true si la quête est spécifique à une faction, false sinon
     * @param factionName Nom de la faction concernée
     */
    public void setFactionSpecific(boolean factionSpecific, String factionName) {
        this.factionSpecific = factionSpecific;
        this.factionName = factionName;
    }
    
    /**
     * Définir le nom de la faction concernée par la quête
     * 
     * @param factionName Nom de la faction
     */
    public void setFactionName(String factionName) {
        this.factionName = factionName;
        if (factionName != null && !factionName.isEmpty()) {
            this.factionSpecific = true;
        }
    }
    
    /**
     * Obtenir le nom de la faction concernée par la quête
     * 
     * @return Nom de la faction
     */
    public String getFactionName() {
        return factionName;
    }
    
    /**
     * Obtenir les objectifs de la quête
     * 
     * @return Liste des objectifs
     */
    public List<QuestObjective> getObjectives() {
        return objectives;
    }
    
    /**
     * Obtenir les récompenses de la quête
     * 
     * @return Liste des récompenses
     */
    public List<QuestReward> getRewards() {
        return rewards;
    }
    
    /**
     * Obtenir l'état actuel de la quête
     * 
     * @return État de la quête
     */
    public QuestState getState() {
        return state;
    }
    
    /**
     * Définir l'état de la quête
     * 
     * @param state Nouvel état
     */
    public void setState(QuestState state) {
        this.state = state;
    }
    
    /**
     * Obtenir le statut de la quête pour le système de sauvegarde
     * 
     * @return Statut de la quête
     */
    public QuestStatus getStatus() {
        return status;
    }
    
    /**
     * Définir le statut de la quête pour le système de sauvegarde
     * 
     * @param status Nouveau statut de la quête
     */
    public void setStatus(QuestStatus status) {
        this.status = status;
        
        // Mettre à jour l'état de la quête en fonction du statut
        switch (status) {
            case AVAILABLE:
                this.state = QuestState.NOT_STARTED;
                break;
            case ACTIVE:
                this.state = QuestState.IN_PROGRESS;
                break;
            case COMPLETED:
                this.state = QuestState.COMPLETED;
                break;
            case FAILED:
                this.state = QuestState.FAILED;
                break;
            case ABANDONED:
                this.state = QuestState.ABANDONED;
                break;
        }
    }
    
    /**
     * Ajouter une quête prérequise
     * 
     * @param questId Identifiant de la quête prérequise
     */
    public void addPrerequisite(String questId) {
        prerequisites.add(questId);
    }
    
    /**
     * Obtenir les quêtes prérequises
     * 
     * @return Liste des identifiants des quêtes prérequises
     */
    public List<String> getPrerequisites() {
        return prerequisites;
    }
    
    /**
     * Ajouter une quête débloquée
     * 
     * @param questId Identifiant de la quête débloquée
     */
    public void addUnlocksQuest(String questId) {
        unlocksQuests.add(questId);
    }
    
    /**
     * Obtenir les quêtes débloquées
     * 
     * @return Liste des identifiants des quêtes débloquées
     */
    public List<String> getUnlocksQuests() {
        return unlocksQuests;
    }
    
    /**
     * Ajouter une quête débloquée par cette quête
     * 
     * @param questId Identifiant de la quête débloquée
     */
    public void addUnlockedQuest(String questId) {
        if (!unlocksQuests.contains(questId)) {
            unlocksQuests.add(questId);
        }
    }
    
    /**
     * Obtenir la liste des quêtes débloquées par cette quête
     * 
     * @return Liste des identifiants de quêtes débloquées
     */
    public List<String> getUnlockedQuests() {
        return new ArrayList<>(unlocksQuests);
    }
    
    /**
     * Vérifier si cette quête débloque d'autres quêtes
     * 
     * @return true si cette quête débloque d'autres quêtes, false sinon
     */
    public boolean hasUnlockedQuests() {
        return !unlocksQuests.isEmpty();
    }
    
    /**
     * Définir le dialogue de début de quête
     * 
     * @param startDialogue Dialogue de début
     */
    public void setStartDialogue(String startDialogue) {
        this.startDialogue = startDialogue;
    }
    
    /**
     * Obtenir le dialogue de début de quête
     * 
     * @return Dialogue de début
     */
    public String getStartDialogue() {
        return startDialogue;
    }
    
    /**
     * Définir le dialogue de fin de quête
     * 
     * @param endDialogue Dialogue de fin
     */
    public void setEndDialogue(String endDialogue) {
        this.endDialogue = endDialogue;
    }
    
    /**
     * Obtenir le dialogue de fin de quête
     * 
     * @return Dialogue de fin
     */
    public String getEndDialogue() {
        return endDialogue;
    }
    
    /**
     * Définir le dialogue d'échec de quête
     * 
     * @param failDialogue Dialogue d'échec
     */
    public void setFailDialogue(String failDialogue) {
        this.failDialogue = failDialogue;
    }
    
    /**
     * Obtenir le dialogue d'échec de quête
     * 
     * @return Dialogue d'échec
     */
    public String getFailDialogue() {
        return failDialogue;
    }
    
    /**
     * Définir la limite de temps pour compléter la quête
     * 
     * @param timeLimit Limite de temps en secondes (0 = pas de limite)
     */
    public void setTimeLimit(float timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    /**
     * Obtenir la limite de temps pour compléter la quête
     * 
     * @return Limite de temps en secondes
     */
    public float getTimeLimit() {
        return timeLimit;
    }
    
    /**
     * Obtenir le temps écoulé depuis le début de la quête
     * 
     * @return Temps écoulé en secondes
     */
    public float getElapsedTime() {
        return elapsedTime;
    }
    
    /**
     * Obtenir le temps restant avant la fin de la limite de temps
     * 
     * @return Temps restant en secondes, ou -1 si pas de limite
     */
    public float getRemainingTime() {
        if (timeLimit <= 0) {
            return -1;
        }
        return Math.max(0, timeLimit - elapsedTime);
    }
    
    /**
     * Vérifier si la quête a une limite de temps
     * 
     * @return true si la quête a une limite de temps, false sinon
     */
    public boolean hasTimeLimit() {
        return timeLimit > 0;
    }
    
    /**
     * Obtenir la progression globale de la quête (pourcentage)
     * 
     * @return Progression entre 0 et 1
     */
    public float getProgress() {
        if (objectives.isEmpty()) {
            return 0;
        }
        
        float totalProgress = 0;
        for (QuestObjective objective : objectives) {
            totalProgress += objective.getProgress();
        }
        
        return totalProgress / objectives.size();
    }
    
    /**
     * Obtenir une description de la progression de la quête
     * 
     * @return Description de la progression
     */
    public String getProgressDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(" (").append(Math.round(getProgress() * 100)).append("%):\n");
        
        for (QuestObjective objective : objectives) {
            sb.append("- ").append(objective.getDescription());
            if (objective.getState() == QuestObjectiveState.COMPLETED) {
                sb.append(" [Complété]");
            } else if (objective.getState() == QuestObjectiveState.IN_PROGRESS) {
                sb.append(" [").append(Math.round(objective.getProgress() * 100)).append("%]");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Obtenir le dialogue de la quête (combinaison de tous les dialogues)
     * 
     * @return Dialogue complet
     */
    public String getDialogue() {
        StringBuilder sb = new StringBuilder();
        
        if (startDialogue != null && !startDialogue.isEmpty()) {
            sb.append("Début: ").append(startDialogue).append("\n\n");
        }
        
        if (endDialogue != null && !endDialogue.isEmpty()) {
            sb.append("Fin: ").append(endDialogue).append("\n\n");
        }
        
        if (failDialogue != null && !failDialogue.isEmpty()) {
            sb.append("Échec: ").append(failDialogue);
        }
        
        return sb.toString();
    }
    
    /**
     * Obtenir la date de complétion de la quête
     * 
     * @return Date de complétion au format "yyyy-MM-dd HH:mm:ss", ou null si la quête n'est pas complétée
     */
    public String getCompletionDate() {
        return completionDate;
    }
    
    /**
     * Obtenir la raison de l'échec de la quête
     * 
     * @return Raison de l'échec, ou null si la quête n'a pas échoué
     */
    public String getFailureReason() {
        return failureReason;
    }
    
    /**
     * Définir si la quête est disponible pour le joueur
     * 
     * @param available true si la quête est disponible, false sinon
     */
    public void setAvailable(boolean available) {
        if (available && this.state == QuestState.NOT_STARTED) {
            this.state = QuestState.AVAILABLE;
        } else if (!available && this.state == QuestState.AVAILABLE) {
            this.state = QuestState.NOT_STARTED;
        }
    }
    
    /**
     * Vérifier si la quête est disponible pour le joueur
     * 
     * @return true si la quête est disponible, false sinon
     */
    public boolean isAvailable() {
        return this.state == QuestState.AVAILABLE;
    }
    
    /**
     * Convertir la quête en JSONObject
     * 
     * @return JSONObject représentant la quête
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("title", title);
        json.put("description", description);
        json.put("minLevel", minLevel);
        json.put("mainQuest", mainQuest);
        json.put("factionSpecific", factionSpecific);
        json.put("factionName", factionName);
        json.put("state", state.toString());
        json.put("status", status.toString());
        json.put("timeLimit", timeLimit);
        json.put("elapsedTime", elapsedTime);
        
        if (completionDate != null) {
            json.put("completionDate", completionDate);
        }
        
        if (failureReason != null) {
            json.put("failureReason", failureReason);
        }
        
        // Sauvegarder les objectifs
        JSONArray objectivesArray = new JSONArray();
        for (QuestObjective objective : objectives) {
            objectivesArray.put(objective.toJson());
        }
        json.put("objectives", objectivesArray);
        
        // Sauvegarder les récompenses
        JSONArray rewardsArray = new JSONArray();
        for (QuestReward reward : rewards) {
            rewardsArray.put(reward.toJson());
        }
        json.put("rewards", rewardsArray);
        
        // Sauvegarder les prérequis
        JSONArray prerequisitesArray = new JSONArray();
        for (String prereq : prerequisites) {
            prerequisitesArray.put(prereq);
        }
        json.put("prerequisites", prerequisitesArray);
        
        // Sauvegarder les quêtes débloquées
        JSONArray unlocksArray = new JSONArray();
        for (String unlock : unlocksQuests) {
            unlocksArray.put(unlock);
        }
        json.put("unlocksQuests", unlocksArray);
        
        // Sauvegarder les dialogues
        json.put("startDialogue", startDialogue);
        json.put("endDialogue", endDialogue);
        json.put("failDialogue", failDialogue);
        
        return json;
    }
    
    /**
     * Créer une quête à partir d'un JSONObject
     * 
     * @param json JSONObject représentant la quête
     * @return Nouvelle instance de Quest
     */
    public static Quest fromJson(JSONObject json) {
        String id = json.getString("id");
        String title = json.getString("title");
        String description = json.getString("description");
        int minLevel = json.getInt("minLevel");
        
        Quest quest = new Quest(id, title, description, minLevel);
        
        quest.setMainQuest(json.getBoolean("mainQuest"));
        quest.setFactionSpecific(json.getBoolean("factionSpecific"), json.getString("factionName"));
        
        // Charger l'état
        quest.state = QuestState.valueOf(json.getString("state"));
        
        // Charger le statut s'il est présent
        if (json.has("status")) {
            quest.status = QuestStatus.valueOf(json.getString("status"));
        } else {
            // Déduire le statut à partir de l'état
            switch (quest.state) {
                case NOT_STARTED:
                    quest.status = QuestStatus.AVAILABLE;
                    break;
                case IN_PROGRESS:
                    quest.status = QuestStatus.ACTIVE;
                    break;
                case COMPLETED:
                    quest.status = QuestStatus.COMPLETED;
                    break;
                case FAILED:
                    quest.status = QuestStatus.FAILED;
                    break;
                case ABANDONED:
                    quest.status = QuestStatus.ABANDONED;
                    break;
                default:
                    quest.status = QuestStatus.AVAILABLE;
            }
        }
        
        // Charger les limites de temps
        quest.timeLimit = (float) json.getDouble("timeLimit");
        quest.elapsedTime = (float) json.getDouble("elapsedTime");
        
        // Charger la date de complétion et la raison d'échec si présentes
        if (json.has("completionDate")) {
            quest.completionDate = json.getString("completionDate");
        }
        
        if (json.has("failureReason")) {
            quest.failureReason = json.getString("failureReason");
        }
        
        // Charger les objectifs
        JSONArray objectivesArray = json.getJSONArray("objectives");
        for (int i = 0; i < objectivesArray.length(); i++) {
            JSONObject objJson = objectivesArray.getJSONObject(i);
            QuestObjective objective = QuestObjective.fromJson(objJson);
            quest.addObjective(objective);
        }
        
        // Charger les récompenses
        JSONArray rewardsArray = json.getJSONArray("rewards");
        for (int i = 0; i < rewardsArray.length(); i++) {
            JSONObject rewJson = rewardsArray.getJSONObject(i);
            QuestReward reward = QuestReward.fromJson(rewJson);
            quest.addReward(reward);
        }
        
        // Charger les prérequis
        JSONArray prerequisitesArray = json.getJSONArray("prerequisites");
        for (int i = 0; i < prerequisitesArray.length(); i++) {
            quest.prerequisites.add(prerequisitesArray.getString(i));
        }
        
        // Charger les quêtes débloquées
        JSONArray unlocksArray = json.getJSONArray("unlocksQuests");
        for (int i = 0; i < unlocksArray.length(); i++) {
            quest.unlocksQuests.add(unlocksArray.getString(i));
        }
        
        // Charger les dialogues
        quest.startDialogue = json.getString("startDialogue");
        quest.endDialogue = json.getString("endDialogue");
        quest.failDialogue = json.getString("failDialogue");
        
        return quest;
    }
    
    /**
     * Créer une copie de cette quête
     * 
     * @return Nouvelle instance de Quest avec les mêmes attributs
     */
    @Override
    public Quest clone() {
        Quest clone = new Quest(id, title, description, minLevel);
        
        clone.mainQuest = this.mainQuest;
        clone.factionSpecific = this.factionSpecific;
        clone.factionName = this.factionName;
        clone.state = this.state;
        clone.status = this.status;
        clone.timeLimit = this.timeLimit;
        clone.elapsedTime = this.elapsedTime;
        clone.completionDate = this.completionDate;
        clone.failureReason = this.failureReason;
        clone.startDialogue = this.startDialogue;
        clone.endDialogue = this.endDialogue;
        clone.failDialogue = this.failDialogue;
        
        // Cloner les objectifs
        for (QuestObjective objective : this.objectives) {
            clone.objectives.add(objective.clone());
        }
        
        // Cloner les récompenses
        for (QuestReward reward : this.rewards) {
            clone.rewards.add(reward.clone());
        }
        
        // Cloner les prérequis
        clone.prerequisites.addAll(this.prerequisites);
        
        // Cloner les quêtes débloquées
        clone.unlocksQuests.addAll(this.unlocksQuests);
        
        return clone;
    }
}

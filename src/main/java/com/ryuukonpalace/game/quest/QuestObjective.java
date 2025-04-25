package com.ryuukonpalace.game.quest;

import com.ryuukonpalace.game.player.Player;
import org.json.JSONObject;

/**
 * Classe abstraite représentant un objectif de quête.
 * Les objectifs peuvent être de différents types (tuer des créatures, collecter des objets, etc.)
 */
public abstract class QuestObjective {
    
    // Identifiant unique de l'objectif
    protected String id;
    
    // Description de l'objectif
    protected String description;
    
    // État actuel de l'objectif
    protected QuestObjectiveState state;
    
    // Quantité actuelle (pour les objectifs quantifiables)
    protected int currentAmount;
    
    // Quantité requise (pour les objectifs quantifiables)
    protected int requiredAmount;
    
    // Indique si l'objectif est optionnel
    protected boolean optional;
    
    /**
     * Constructeur
     * 
     * @param id Identifiant unique de l'objectif
     * @param description Description de l'objectif
     */
    public QuestObjective(String id, String description) {
        this.id = id;
        this.description = description;
        this.state = QuestObjectiveState.NOT_STARTED;
        this.currentAmount = 0;
        this.requiredAmount = 1;
        this.optional = false;
    }
    
    /**
     * Mettre à jour l'objectif
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param player Joueur
     * @return true si l'objectif a été mis à jour, false sinon
     */
    public abstract boolean update(float deltaTime, Player player);
    
    /**
     * Démarrer l'objectif
     * 
     * @param player Joueur
     * @return true si l'objectif a été démarré, false sinon
     */
    public boolean start(Player player) {
        if (state == QuestObjectiveState.NOT_STARTED) {
            state = QuestObjectiveState.IN_PROGRESS;
            return true;
        }
        return false;
    }
    
    /**
     * Compléter l'objectif
     * 
     * @return true si l'objectif a été complété, false sinon
     */
    public boolean complete() {
        if (state == QuestObjectiveState.IN_PROGRESS) {
            state = QuestObjectiveState.COMPLETED;
            return true;
        }
        return false;
    }
    
    /**
     * Échouer l'objectif
     * 
     * @return true si l'objectif a échoué, false sinon
     */
    public boolean fail() {
        if (state == QuestObjectiveState.IN_PROGRESS) {
            state = QuestObjectiveState.FAILED;
            return true;
        }
        return false;
    }
    
    /**
     * Réinitialiser l'objectif
     * 
     * @return true si l'objectif a été réinitialisé, false sinon
     */
    public boolean reset() {
        state = QuestObjectiveState.NOT_STARTED;
        currentAmount = 0;
        return true;
    }
    
    /**
     * Incrémenter la quantité actuelle
     * 
     * @param amount Quantité à ajouter
     * @return true si l'objectif est complété après l'incrémentation, false sinon
     */
    public boolean incrementAmount(int amount) {
        if (state != QuestObjectiveState.IN_PROGRESS) {
            return false;
        }
        
        currentAmount += amount;
        
        // Vérifier si l'objectif est complété
        if (currentAmount >= requiredAmount) {
            currentAmount = requiredAmount;
            complete();
            return true;
        }
        
        return false;
    }
    
    /**
     * Obtenir l'identifiant de l'objectif
     * 
     * @return Identifiant de l'objectif
     */
    public String getId() {
        return id;
    }
    
    /**
     * Obtenir la description de l'objectif
     * 
     * @return Description de l'objectif
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtenir l'état actuel de l'objectif
     * 
     * @return État de l'objectif
     */
    public QuestObjectiveState getState() {
        return state;
    }
    
    /**
     * Définir l'état de l'objectif
     * 
     * @param state Nouvel état
     */
    public void setState(QuestObjectiveState state) {
        this.state = state;
    }
    
    /**
     * Obtenir la quantité actuelle
     * 
     * @return Quantité actuelle
     */
    public int getCurrentAmount() {
        return currentAmount;
    }
    
    /**
     * Définir la quantité actuelle
     * 
     * @param currentAmount Nouvelle quantité actuelle
     */
    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
        
        // Vérifier si l'objectif est complété
        if (this.currentAmount >= requiredAmount && state == QuestObjectiveState.IN_PROGRESS) {
            this.currentAmount = requiredAmount;
            complete();
        }
    }
    
    /**
     * Obtenir la quantité requise
     * 
     * @return Quantité requise
     */
    public int getRequiredAmount() {
        return requiredAmount;
    }
    
    /**
     * Définir la quantité requise
     * 
     * @param requiredAmount Nouvelle quantité requise
     */
    public void setRequiredAmount(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }
    
    /**
     * Vérifier si l'objectif est optionnel
     * 
     * @return true si l'objectif est optionnel, false sinon
     */
    public boolean isOptional() {
        return optional;
    }
    
    /**
     * Définir si l'objectif est optionnel
     * 
     * @param optional true si l'objectif est optionnel, false sinon
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }
    
    /**
     * Obtenir la progression de l'objectif (pourcentage)
     * 
     * @return Progression entre 0 et 1
     */
    public float getProgress() {
        if (requiredAmount <= 0) {
            return 0;
        }
        
        if (state == QuestObjectiveState.COMPLETED) {
            return 1.0f;
        } else if (state == QuestObjectiveState.FAILED) {
            return 0.0f;
        }
        
        return Math.min(1.0f, (float) currentAmount / requiredAmount);
    }
    
    /**
     * Obtenir une description formatée de l'objectif avec sa progression
     * 
     * @return Description formatée
     */
    public String getFormattedDescription() {
        if (requiredAmount > 1) {
            return description + " (" + currentAmount + "/" + requiredAmount + ")";
        } else {
            return description;
        }
    }
    
    /**
     * Convertir l'objectif en JSONObject
     * 
     * @return JSONObject représentant l'objectif
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("description", description);
        json.put("state", state.toString());
        json.put("currentAmount", currentAmount);
        json.put("requiredAmount", requiredAmount);
        json.put("optional", optional);
        return json;
    }
    
    /**
     * Créer un objectif à partir d'un JSONObject
     * 
     * @param json JSONObject représentant l'objectif
     * @return Nouvel objectif
     */
    public static QuestObjective fromJson(JSONObject json) {
        String id = json.getString("id");
        String description = json.getString("description");
        
        // Créer un objectif générique
        QuestObjective objective = new GenericObjective(id, description);
        
        // Définir les propriétés
        objective.state = QuestObjectiveState.valueOf(json.getString("state"));
        objective.currentAmount = json.getInt("currentAmount");
        objective.requiredAmount = json.getInt("requiredAmount");
        objective.optional = json.getBoolean("optional");
        
        return objective;
    }
    
    /**
     * Créer une copie de cet objectif
     * 
     * @return Nouvelle instance de QuestObjective avec les mêmes attributs
     */
    @Override
    public QuestObjective clone() {
        try {
            QuestObjective clone = (QuestObjective) super.clone();
            // Copier les attributs si nécessaire
            return clone;
        } catch (CloneNotSupportedException e) {
            // Créer un nouvel objectif générique si le clonage échoue
            QuestObjective clone = new GenericObjective(id, description);
            clone.state = this.state;
            clone.currentAmount = this.currentAmount;
            clone.requiredAmount = this.requiredAmount;
            clone.optional = this.optional;
            return clone;
        }
    }
    
    /**
     * Classe interne pour les objectifs génériques
     */
    private static class GenericObjective extends QuestObjective {
        public GenericObjective(String id, String description) {
            super(id, description);
        }
        
        @Override
        public boolean update(float deltaTime, Player player) {
            // Implémentation par défaut
            return false;
        }
    }
}

package com.ryuukonpalace.game.creatures;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the evolution of a creature.
 * Contains information about the required level and the evolved form.
 */
public class Evolution {
    
    private int requiredLevel;
    private Creature evolvedForm;
    private Map<String, Object> specialConditions;
    
    /**
     * Constructor for creating a new evolution
     * 
     * @param requiredLevel The level required for evolution
     * @param evolvedForm The evolved form of the creature
     */
    public Evolution(int requiredLevel, Creature evolvedForm) {
        this.requiredLevel = requiredLevel;
        this.evolvedForm = evolvedForm;
        this.specialConditions = new HashMap<>();
    }
    
    /**
     * Get the level required for evolution
     * 
     * @return The required level
     */
    public int getRequiredLevel() {
        return requiredLevel;
    }
    
    /**
     * Get the evolved form of the creature
     * 
     * @return The evolved form
     */
    public Creature getEvolvedForm() {
        return evolvedForm;
    }
    
    /**
     * Ajouter une condition spéciale pour l'évolution
     * 
     * @param conditionName Nom de la condition
     * @param conditionValue Valeur de la condition
     */
    public void addSpecialCondition(String conditionName, Object conditionValue) {
        specialConditions.put(conditionName, conditionValue);
    }
    
    /**
     * Obtenir une condition spéciale
     * 
     * @param conditionName Nom de la condition
     * @return Valeur de la condition, ou null si elle n'existe pas
     */
    public Object getSpecialCondition(String conditionName) {
        return specialConditions.get(conditionName);
    }
    
    /**
     * Vérifier si toutes les conditions spéciales sont remplies
     * 
     * @param creature Créature à vérifier
     * @return true si toutes les conditions sont remplies, false sinon
     */
    public boolean checkSpecialConditions(Creature creature) {
        // Si aucune condition spéciale, l'évolution est possible
        if (specialConditions.isEmpty()) {
            return true;
        }
        
        // Vérifier chaque condition spéciale
        for (Map.Entry<String, Object> condition : specialConditions.entrySet()) {
            String conditionName = condition.getKey();
            Object conditionValue = condition.getValue();
            
            // Vérifier selon le type de condition
            switch (conditionName) {
                case "item":
                    // Vérifier si le joueur possède l'objet requis
                    // Cette vérification doit être faite au niveau du système de combat
                    // ou d'évolution, car la créature ne connaît pas l'inventaire du joueur
                    return false; // Par défaut, considérer que l'objet n'est pas disponible
                
                case "friendship":
                    // Vérifier le niveau d'amitié
                    if (creature.getFriendship() < (int) conditionValue) {
                        return false;
                    }
                    break;
                
                case "time_of_day":
                    // Vérifier l'heure de la journée
                    // Cette vérification doit être faite au niveau du système de jeu
                    return false; // Par défaut, considérer que l'heure n'est pas correcte
                
                case "location":
                    // Vérifier la localisation
                    // Cette vérification doit être faite au niveau du système de jeu
                    return false; // Par défaut, considérer que la localisation n'est pas correcte
                
                // Ajouter d'autres conditions selon les besoins du jeu
                default:
                    // Condition inconnue, considérer qu'elle n'est pas remplie
                    return false;
            }
        }
        
        // Toutes les conditions sont remplies
        return true;
    }
    
    /**
     * Vérifier si cette évolution a des conditions spéciales
     * 
     * @return true si l'évolution a des conditions spéciales, false sinon
     */
    public boolean hasSpecialConditions() {
        return !specialConditions.isEmpty();
    }
    
    /**
     * Obtenir toutes les conditions spéciales
     * 
     * @return Map des conditions spéciales
     */
    public Map<String, Object> getSpecialConditions() {
        return new HashMap<>(specialConditions);
    }
}

package com.ryuukonpalace.game.dialogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un nœud de dialogue dans une conversation.
 * Contient le texte du dialogue et les options de réponse disponibles.
 */
public class DialogueNode {
    
    // Identifiant unique du nœud
    private String id;
    
    // Texte du dialogue
    private String text;
    
    // Options de réponse disponibles
    private List<DialogueOption> options;
    
    // Conditions pour que ce nœud soit disponible
    private List<DialogueCondition> conditions;
    
    // Actions à exécuter lorsque ce nœud est atteint
    private List<DialogueAction> actions;
    
    /**
     * Constructeur
     * 
     * @param id Identifiant unique du nœud
     * @param text Texte du dialogue
     */
    public DialogueNode(String id, String text) {
        this.id = id;
        this.text = text;
        this.options = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.actions = new ArrayList<>();
    }
    
    /**
     * Ajouter une option de réponse
     * 
     * @param option Option à ajouter
     */
    public void addOption(DialogueOption option) {
        options.add(option);
    }
    
    /**
     * Ajouter une condition
     * 
     * @param condition Condition à ajouter
     */
    public void addCondition(DialogueCondition condition) {
        conditions.add(condition);
    }
    
    /**
     * Ajouter une action
     * 
     * @param action Action à ajouter
     */
    public void addAction(DialogueAction action) {
        actions.add(action);
    }
    
    /**
     * Vérifier si toutes les conditions sont remplies
     * 
     * @return true si toutes les conditions sont remplies, false sinon
     */
    public boolean checkConditions() {
        for (DialogueCondition condition : conditions) {
            if (!condition.check()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Exécuter toutes les actions
     */
    public void executeActions() {
        for (DialogueAction action : actions) {
            action.execute();
        }
    }
    
    /**
     * Vérifier si le nœud a des actions
     * 
     * @return true si le nœud a au moins une action, false sinon
     */
    public boolean hasAction() {
        return !actions.isEmpty();
    }
    
    /**
     * Exécuter toutes les actions
     */
    public void executeAction() {
        executeActions();
    }
    
    /**
     * Obtenir l'identifiant du nœud
     * 
     * @return Identifiant du nœud
     */
    public String getId() {
        return id;
    }
    
    /**
     * Obtenir le texte du dialogue
     * 
     * @return Texte du dialogue
     */
    public String getText() {
        return text;
    }
    
    /**
     * Définir le texte du dialogue
     * 
     * @param text Nouveau texte
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Obtenir les options de réponse
     * 
     * @return Liste des options de réponse
     */
    public List<DialogueOption> getOptions() {
        return options;
    }
    
    /**
     * Obtenir les conditions
     * 
     * @return Liste des conditions
     */
    public List<DialogueCondition> getConditions() {
        return conditions;
    }
    
    /**
     * Obtenir les actions
     * 
     * @return Liste des actions
     */
    public List<DialogueAction> getActions() {
        return actions;
    }
    
    /**
     * Interface pour les conditions de dialogue
     */
    public interface DialogueCondition {
        /**
         * Vérifier si la condition est remplie
         * 
         * @return true si la condition est remplie, false sinon
         */
        boolean check();
    }
    
    /**
     * Interface pour les actions de dialogue
     */
    public interface DialogueAction {
        /**
         * Exécuter l'action
         */
        void execute();
    }
}

package com.ryuukonpalace.game.dialogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une option de réponse dans un dialogue.
 * Contient le texte de l'option et le nœud de dialogue suivant.
 */
public class DialogueOption {
    
    // Texte de l'option
    private String text;
    
    // Nœud de dialogue suivant
    private DialogueNode nextNode;
    
    // Conditions pour que cette option soit disponible
    private List<DialogueCondition> conditions;
    
    // Actions à exécuter lorsque cette option est choisie
    private List<DialogueAction> actions;
    
    /**
     * Constructeur
     * 
     * @param text Texte de l'option
     * @param nextNode Nœud de dialogue suivant
     */
    public DialogueOption(String text, DialogueNode nextNode) {
        this.text = text;
        this.nextNode = nextNode;
        this.conditions = new ArrayList<>();
        this.actions = new ArrayList<>();
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
     * Obtenir le texte de l'option
     * 
     * @return Texte de l'option
     */
    public String getText() {
        return text;
    }
    
    /**
     * Définir le texte de l'option
     * 
     * @param text Nouveau texte
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Obtenir le nœud de dialogue suivant
     * 
     * @return Nœud de dialogue suivant
     */
    public DialogueNode getNextNode() {
        return nextNode;
    }
    
    /**
     * Définir le nœud de dialogue suivant
     * 
     * @param nextNode Nouveau nœud de dialogue suivant
     */
    public void setNextNode(DialogueNode nextNode) {
        this.nextNode = nextNode;
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

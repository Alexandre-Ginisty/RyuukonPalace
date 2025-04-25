package com.ryuukonpalace.game.dialogue;

/**
 * Interface pour les callbacks d'événements de dialogue.
 */
public interface DialogueCallback {
    
    /**
     * Appelé lorsqu'un dialogue est démarré
     * 
     * @param node Nœud de dialogue initial
     */
    void onDialogueStarted(DialogueNode node);
    
    /**
     * Appelé lorsqu'une option est sélectionnée
     * 
     * @param node Nœud de dialogue courant
     * @param option Option sélectionnée
     */
    void onOptionSelected(DialogueNode node, DialogueOption option);
    
    /**
     * Appelé lorsque le nœud de dialogue change
     * 
     * @param node Nouveau nœud de dialogue
     */
    void onDialogueNodeChanged(DialogueNode node);
    
    /**
     * Appelé lorsqu'un dialogue est terminé
     * 
     * @param node Dernier nœud de dialogue
     */
    void onDialogueEnded(DialogueNode node);
}

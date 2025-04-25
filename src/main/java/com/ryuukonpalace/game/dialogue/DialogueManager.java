package com.ryuukonpalace.game.dialogue;

import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.ui.UIManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire des dialogues du jeu.
 * Responsable du chargement, de l'affichage et de la gestion des dialogues avec les PNJ.
 */
public class DialogueManager {
    
    // Instance unique (singleton)
    private static DialogueManager instance;
    
    // Nœuds de dialogue
    private Map<String, DialogueNode> dialogueNodes;
    
    // Nœud de dialogue actuel
    private DialogueNode currentNode;
    
    // Joueur
    private Player player;
    
    // UIManager pour l'affichage des dialogues
    private UIManager uiManager;
    
    // Callbacks pour les événements de dialogue
    private List<DialogueCallback> callbacks;
    
    /**
     * Constructeur privé (singleton)
     */
    private DialogueManager() {
        dialogueNodes = new HashMap<>();
        callbacks = new ArrayList<>();
    }
    
    /**
     * Obtenir l'instance unique
     * 
     * @return Instance unique
     */
    public static DialogueManager getInstance() {
        if (instance == null) {
            instance = new DialogueManager();
        }
        return instance;
    }
    
    /**
     * Initialiser le gestionnaire de dialogues
     * 
     * @param player Joueur
     * @param uiManager Gestionnaire d'interface utilisateur
     */
    public void initialize(Player player, UIManager uiManager) {
        this.player = player;
        this.uiManager = uiManager;
        
        // Charger les dialogues depuis les fichiers JSON
        loadDialogues();
    }
    
    /**
     * Charger les dialogues depuis les fichiers JSON
     */
    private void loadDialogues() {
        // TODO: Implémenter le chargement des dialogues depuis les fichiers JSON
        // Pour l'instant, on crée quelques dialogues de test
        createTestDialogues();
    }
    
    /**
     * Créer des dialogues de test
     */
    private void createTestDialogues() {
        // Dialogue avec le guide du village
        DialogueNode guideStart = new DialogueNode("guide_start", "Bienvenue à Ryuukon Palace ! Je suis le guide du village.");
        DialogueNode guideInfo = new DialogueNode("guide_info", "Ryuukon Palace est un monde rempli de créatures appelées variants. En tant que Tacticien, tu peux les capturer et les utiliser pour combattre.");
        DialogueNode guideAdvice = new DialogueNode("guide_advice", "N'oublie pas de visiter le laboratoire du Professeur Chêne pour obtenir ton premier variant !");
        DialogueNode guideEnd = new DialogueNode("guide_end", "Bonne chance dans ton aventure !");
        
        // Options de dialogue
        DialogueOption askInfo = new DialogueOption("Parlez-moi de ce monde", guideInfo);
        DialogueOption askAdvice = new DialogueOption("Que dois-je faire maintenant ?", guideAdvice);
        DialogueOption sayThanks = new DialogueOption("Merci pour l'information", guideEnd);
        DialogueOption sayBye = new DialogueOption("Au revoir", null);
        
        // Ajouter les options aux nœuds
        guideStart.addOption(askInfo);
        guideStart.addOption(askAdvice);
        guideInfo.addOption(askAdvice);
        guideInfo.addOption(sayThanks);
        guideAdvice.addOption(sayThanks);
        guideEnd.addOption(sayBye);
        
        // Ajouter les nœuds au gestionnaire
        addDialogueNode(guideStart);
        addDialogueNode(guideInfo);
        addDialogueNode(guideAdvice);
        addDialogueNode(guideEnd);
    }
    
    /**
     * Ajouter un nœud de dialogue
     * 
     * @param node Nœud de dialogue à ajouter
     */
    public void addDialogueNode(DialogueNode node) {
        dialogueNodes.put(node.getId(), node);
    }
    
    /**
     * Démarrer un dialogue
     * 
     * @param nodeId ID du nœud de dialogue à démarrer
     * @return true si le dialogue a été démarré, false sinon
     */
    public boolean startDialogue(String nodeId) {
        DialogueNode node = dialogueNodes.get(nodeId);
        if (node == null) {
            return false;
        }
        
        currentNode = node;
        
        // Afficher le dialogue dans l'interface utilisateur
        if (uiManager != null) {
            uiManager.showDialogue(node.getText(), node.getOptions());
        }
        
        // Notifier les callbacks
        for (DialogueCallback callback : callbacks) {
            callback.onDialogueStarted(node);
        }
        
        return true;
    }
    
    /**
     * Choisir une option de dialogue
     * 
     * @param optionIndex Index de l'option choisie
     * @return true si l'option a été choisie, false sinon
     */
    public boolean chooseOption(int optionIndex) {
        if (currentNode == null) {
            return false;
        }
        
        List<DialogueOption> options = currentNode.getOptions();
        if (optionIndex < 0 || optionIndex >= options.size()) {
            return false;
        }
        
        DialogueOption option = options.get(optionIndex);
        DialogueNode nextNode = option.getNextNode();
        
        // Notifier les callbacks
        for (DialogueCallback callback : callbacks) {
            callback.onOptionSelected(currentNode, option);
        }
        
        // Exécuter les actions associées au nœud actuel
        if (currentNode.hasAction()) {
            currentNode.executeAction();
        }
        
        if (nextNode != null) {
            currentNode = nextNode;
            
            // Afficher le nouveau dialogue dans l'interface utilisateur
            if (uiManager != null) {
                uiManager.showDialogue(nextNode.getText(), nextNode.getOptions());
            }
            
            // Notifier les callbacks
            for (DialogueCallback callback : callbacks) {
                callback.onDialogueNodeChanged(nextNode);
            }
            
            return true;
        } else {
            // Fin du dialogue
            endDialogue();
            return false;
        }
    }
    
    /**
     * Terminer le dialogue actuel
     */
    public void endDialogue() {
        if (currentNode != null) {
            // Fermer l'interface de dialogue
            if (uiManager != null) {
                uiManager.hideDialogue();
            }
            
            // Notifier les callbacks
            for (DialogueCallback callback : callbacks) {
                callback.onDialogueEnded(currentNode);
            }
            
            currentNode = null;
        }
    }
    
    /**
     * Démarrer un dialogue avec un PNJ
     * 
     * @param npcId ID du PNJ
     * @return true si le dialogue a été démarré, false sinon
     */
    public boolean startDialogueWithNPC(int npcId) {
        // Trouver le dialogue initial pour ce PNJ
        String nodeId = "npc_" + npcId + "_start";
        
        // Vérifier si le dialogue existe
        if (dialogueNodes.containsKey(nodeId)) {
            return startDialogue(nodeId);
        }
        
        return false;
    }
    
    /**
     * Obtenir le joueur actuel
     * 
     * @return Joueur actuel
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Obtenir le gestionnaire d'interface utilisateur
     * 
     * @return Gestionnaire d'interface utilisateur
     */
    public UIManager getUIManager() {
        return uiManager;
    }
    
    /**
     * Obtenir le nœud de dialogue actuel
     * 
     * @return Nœud de dialogue actuel, ou null si aucun dialogue n'est en cours
     */
    public DialogueNode getCurrentNode() {
        return currentNode;
    }
    
    /**
     * Obtenir un nœud de dialogue par son ID
     * 
     * @param nodeId ID du nœud de dialogue
     * @return Nœud de dialogue, ou null si non trouvé
     */
    public DialogueNode getDialogueNode(String nodeId) {
        return dialogueNodes.get(nodeId);
    }
    
    /**
     * Ajouter un callback
     * 
     * @param callback Callback à ajouter
     */
    public void addCallback(DialogueCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }
    
    /**
     * Supprimer un callback
     * 
     * @param callback Callback à supprimer
     */
    public void removeCallback(DialogueCallback callback) {
        callbacks.remove(callback);
    }
}

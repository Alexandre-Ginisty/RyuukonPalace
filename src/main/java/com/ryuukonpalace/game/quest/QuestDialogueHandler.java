package com.ryuukonpalace.game.quest;

import com.ryuukonpalace.game.dialogue.DialogueManager;
import com.ryuukonpalace.game.dialogue.DialogueNode;
import com.ryuukonpalace.game.dialogue.DialogueOption;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.quest.objectives.TalkToNPCObjective;

/**
 * Classe qui gère l'intégration entre le système de quêtes et le système de dialogue.
 * Permet de déclencher des quêtes via les dialogues avec les PNJ.
 */
public class QuestDialogueHandler {
    
    // Instance unique (singleton)
    private static QuestDialogueHandler instance;
    
    // Gestionnaire de quêtes
    private QuestManager questManager;
    
    // Gestionnaire de dialogue
    private DialogueManager dialogueManager;
    
    // Joueur
    private Player player;
    
    /**
     * Constructeur privé (singleton)
     */
    private QuestDialogueHandler() {
        questManager = QuestManager.getInstance();
    }
    
    /**
     * Obtenir l'instance unique
     * 
     * @return Instance unique
     */
    public static QuestDialogueHandler getInstance() {
        if (instance == null) {
            instance = new QuestDialogueHandler();
        }
        return instance;
    }
    
    /**
     * Initialiser le gestionnaire d'intégration quête-dialogue
     * 
     * @param player Joueur
     * @param dialogueManager Gestionnaire de dialogue
     */
    public void initialize(Player player, DialogueManager dialogueManager) {
        this.player = player;
        this.dialogueManager = dialogueManager;
    }
    
    /**
     * Ajouter des options de dialogue liées aux quêtes pour un PNJ
     * 
     * @param npcId ID du PNJ
     * @param dialogueNode Nœud de dialogue actuel
     */
    public void addQuestDialogueOptions(int npcId, DialogueNode dialogueNode) {
        // Ajouter des options pour les quêtes disponibles
        for (Quest quest : questManager.getAvailableQuests()) {
            // Vérifier si la quête peut être démarrée par ce PNJ
            if (isQuestStartableByNPC(quest, npcId)) {
                // Créer un nœud de dialogue pour la description de la quête
                DialogueNode questDescriptionNode = new DialogueNode(
                    "quest_description_" + quest.getId(),
                    getQuestDescriptionDialogue(quest)
                );
                
                // Ajouter un nœud de dialogue pour l'acceptation de la quête
                DialogueNode questAcceptedNode = new DialogueNode(
                    "quest_accepted_" + quest.getId(),
                    "J'accepte cette quête !"
                );
                questAcceptedNode.addAction(new DialogueNode.DialogueAction() {
                    @Override
                    public void execute() {
                        questManager.startQuest(quest.getId());
                    }
                });
                
                // Ajouter un nœud de dialogue pour le refus de la quête
                DialogueNode questRefusedNode = new DialogueNode(
                    "quest_refused_" + quest.getId(),
                    "Je ne suis pas intéressé pour le moment."
                );
                
                // Ajouter les options au nœud de description
                DialogueOption acceptOption = new DialogueOption("Accepter la quête", questAcceptedNode);
                questDescriptionNode.addOption(acceptOption);
                
                DialogueOption refuseOption = new DialogueOption("Refuser la quête", questRefusedNode);
                questDescriptionNode.addOption(refuseOption);
                
                // Ajouter l'option pour voir la quête au dialogue principal
                DialogueOption questOption = new DialogueOption(
                    "Quête: " + quest.getTitle(),
                    questDescriptionNode
                );
                dialogueNode.addOption(questOption);
                
                // Ajouter les nœuds au gestionnaire de dialogue
                dialogueManager.addDialogueNode(questDescriptionNode);
                dialogueManager.addDialogueNode(questAcceptedNode);
                dialogueManager.addDialogueNode(questRefusedNode);
            }
        }
        
        // Ajouter des options pour les quêtes actives
        for (Quest quest : questManager.getActiveQuests()) {
            // Vérifier si le PNJ est impliqué dans la quête
            if (isNPCInvolvedInQuest(quest, npcId)) {
                // Créer un nœud de dialogue pour la progression de la quête
                DialogueNode questProgressNode = new DialogueNode(
                    "quest_progress_" + quest.getId(),
                    getQuestProgressDialogue(quest)
                );
                
                // Vérifier si la quête peut être complétée par ce PNJ
                if (isQuestCompletableByNPC(quest, npcId)) {
                    // Créer un nœud de dialogue pour la complétion de la quête
                    DialogueNode questCompletedNode = new DialogueNode(
                        "quest_completed_" + quest.getId(),
                        getQuestCompletionDialogue(quest)
                    );
                    questCompletedNode.addAction(new DialogueNode.DialogueAction() {
                        @Override
                        public void execute() {
                            questManager.completeQuest(quest.getId());
                        }
                    });
                    
                    // Ajouter l'option au nœud de progression
                    DialogueOption completeOption = new DialogueOption("Compléter la quête", questCompletedNode);
                    questProgressNode.addOption(completeOption);
                    
                    // Ajouter le nœud au gestionnaire de dialogue
                    dialogueManager.addDialogueNode(questCompletedNode);
                }
                
                // Ajouter une option pour quitter
                DialogueOption exitOption = new DialogueOption("Quitter", null);
                questProgressNode.addOption(exitOption);
                
                // Ajouter l'option pour voir la progression au dialogue principal
                DialogueOption progressOption = new DialogueOption(
                    "Quête active: " + quest.getTitle(),
                    questProgressNode
                );
                dialogueNode.addOption(progressOption);
                
                // Ajouter le nœud au gestionnaire de dialogue
                dialogueManager.addDialogueNode(questProgressNode);
            }
        }
        
        // Ajouter des options pour les quêtes complétées
        for (Quest quest : questManager.getCompletedQuests()) {
            // Vérifier si le PNJ est impliqué dans la quête
            if (isNPCInvolvedInQuest(quest, npcId)) {
                // Créer un nœud de dialogue pour la quête complétée
                DialogueNode questCompletedNode = new DialogueNode(
                    "quest_completed_info_" + quest.getId(),
                    "Vous avez déjà complété la quête '" + quest.getTitle() + "'. Merci pour votre aide !"
                );
                
                // Ajouter une option pour quitter
                DialogueOption exitOption = new DialogueOption("Quitter", null);
                questCompletedNode.addOption(exitOption);
                
                // Ajouter l'option pour voir l'information au dialogue principal
                DialogueOption completedOption = new DialogueOption(
                    "Quête complétée: " + quest.getTitle(),
                    questCompletedNode
                );
                dialogueNode.addOption(completedOption);
                
                // Ajouter le nœud au gestionnaire de dialogue
                dialogueManager.addDialogueNode(questCompletedNode);
            }
        }
    }
    
    /**
     * Vérifier si une quête peut être démarrée par un PNJ
     * 
     * @param quest Quête à vérifier
     * @param npcId ID du PNJ
     * @return true si la quête peut être démarrée par ce PNJ, false sinon
     */
    private boolean isQuestStartableByNPC(Quest quest, int npcId) {
        // Vérifier si la quête peut être démarrée
        if (!questManager.canStartQuest(quest)) {
            return false;
        }
        
        // Vérifier si le joueur a le niveau requis
        if (player.getLevel() < quest.getMinLevel()) {
            return false;
        }
        
        // Vérifier si la quête est spécifique à une faction
        if (quest.isFactionSpecific() && !player.getFaction().name().equals(quest.getFactionName())) {
            return false;
        }
        
        // Vérifier si le PNJ est impliqué dans la quête
        return isNPCInvolvedInQuest(quest, npcId);
    }
    
    /**
     * Vérifier si un PNJ est impliqué dans une quête
     * 
     * @param quest Quête à vérifier
     * @param npcId ID du PNJ
     * @return true si le PNJ est impliqué dans la quête, false sinon
     */
    private boolean isNPCInvolvedInQuest(Quest quest, int npcId) {
        // Vérifier si le PNJ est associé à la quête
        // Pour l'instant, on utilise une logique simple : le PNJ doit être mentionné dans le dialogue de la quête
        String questDialogue = quest.getDialogue();
        if (questDialogue != null && questDialogue.contains("npc:" + npcId)) {
            return true;
        }
        
        // Vérifier si le PNJ est impliqué dans un objectif de la quête
        for (QuestObjective objective : quest.getObjectives()) {
            if (objective instanceof TalkToNPCObjective) {
                TalkToNPCObjective talkObjective = (TalkToNPCObjective) objective;
                if (talkObjective.getNpcId() == npcId) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Vérifier si une quête peut être complétée par un PNJ
     * 
     * @param quest Quête à vérifier
     * @param npcId ID du PNJ
     * @return true si la quête peut être complétée par ce PNJ, false sinon
     */
    private boolean isQuestCompletableByNPC(Quest quest, int npcId) {
        // Vérifier si tous les objectifs sont complétés
        if (!quest.areAllObjectivesCompleted()) {
            return false;
        }
        
        // Vérifier si le PNJ est impliqué dans la quête
        return isNPCInvolvedInQuest(quest, npcId);
    }
    
    /**
     * Obtenir le dialogue de description d'une quête
     * 
     * @param quest Quête
     * @return Texte du dialogue
     */
    private String getQuestDescriptionDialogue(Quest quest) {
        StringBuilder sb = new StringBuilder();
        
        // Titre et description
        sb.append("Quête: ").append(quest.getTitle()).append("\n\n");
        sb.append(quest.getDescription()).append("\n\n");
        
        // Objectifs
        sb.append("Objectifs:\n");
        for (QuestObjective objective : quest.getObjectives()) {
            sb.append("- ").append(objective.getDescription()).append("\n");
        }
        sb.append("\n");
        
        // Récompenses
        sb.append("Récompenses:\n");
        for (QuestReward reward : quest.getRewards()) {
            sb.append("- ").append(reward.getDescription()).append("\n");
        }
        
        // Niveau minimum
        if (quest.getMinLevel() > 1) {
            sb.append("\nNiveau minimum: ").append(quest.getMinLevel());
        }
        
        // Faction spécifique
        if (quest.isFactionSpecific()) {
            sb.append("\nFaction: ").append(quest.getFactionName());
        }
        
        return sb.toString();
    }
    
    /**
     * Obtenir le dialogue de progression d'une quête
     * 
     * @param quest Quête
     * @return Texte du dialogue
     */
    private String getQuestProgressDialogue(Quest quest) {
        StringBuilder sb = new StringBuilder();
        
        // Titre et description
        sb.append("Quête en cours: ").append(quest.getTitle()).append("\n\n");
        
        // Objectifs
        sb.append("Progression des objectifs:\n");
        for (QuestObjective objective : quest.getObjectives()) {
            sb.append("- ").append(objective.getFormattedDescription()).append(" (");
            
            switch (objective.getState()) {
                case NOT_STARTED:
                    sb.append("Non commencé");
                    break;
                case IN_PROGRESS:
                    sb.append(Math.round(objective.getProgress() * 100)).append("%)");
                    break;
                case COMPLETED:
                    sb.append("Complété");
                    break;
                case FAILED:
                    sb.append("Échoué");
                    break;
            }
            
            sb.append("\n");
        }
        
        // Temps restant (si applicable)
        if (quest.hasTimeLimit()) {
            float timeRemaining = quest.getTimeLimit() - quest.getElapsedTime();
            if (timeRemaining > 0) {
                sb.append("\nTemps restant: ").append(Math.round(timeRemaining)).append(" secondes");
            } else {
                sb.append("\nTemps écoulé !");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Obtenir le dialogue de complétion d'une quête
     * 
     * @param quest Quête
     * @return Texte du dialogue
     */
    private String getQuestCompletionDialogue(Quest quest) {
        StringBuilder sb = new StringBuilder();
        
        // Message de félicitations
        sb.append("Félicitations ! Vous avez terminé la quête '").append(quest.getTitle()).append("'.\n\n");
        
        // Récompenses
        sb.append("Vous recevez les récompenses suivantes:\n");
        for (QuestReward reward : quest.getRewards()) {
            sb.append("- ").append(reward.getDescription()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Notifier le gestionnaire qu'un dialogue a été terminé avec un PNJ
     * 
     * @param npcId ID du PNJ
     * @param dialogueId ID du dialogue
     */
    public void onDialogueCompleted(int npcId, int dialogueId) {
        // Mettre à jour les objectifs de type "Parler à un PNJ"
        for (Quest quest : questManager.getActiveQuests()) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof TalkToNPCObjective) {
                    TalkToNPCObjective talkObjective = (TalkToNPCObjective) objective;
                    if (talkObjective.getNpcId() == npcId && talkObjective.getDialogueId() == dialogueId) {
                        talkObjective.complete();
                        
                        // Vérifier si tous les objectifs sont complétés
                        if (quest.areAllObjectivesCompleted()) {
                            // Afficher un message au joueur via le journal
                            player.addJournalEntry("Tous les objectifs de la quête '" + quest.getTitle() + "' sont complétés. Retournez voir le PNJ pour terminer la quête.");
                        }
                    }
                }
            }
        }
        
        // Notifier le gestionnaire de quêtes
        questManager.onTalkToNPC(npcId, dialogueId);
    }
}

package com.ryuukonpalace.game.quest.objectives;

import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.quest.QuestObjective;
import com.ryuukonpalace.game.quest.QuestObjectiveState;

/**
 * Objectif de quête consistant à parler à un PNJ.
 */
public class TalkToNPCObjective extends QuestObjective {
    
    // ID du PNJ à qui parler
    private int npcId;
    
    // Nom du PNJ à qui parler (pour l'affichage)
    private String npcName;
    
    // Type du PNJ (Tacticien ou Guerrier)
    private NPCType npcType;
    
    // ID du dialogue spécifique à avoir (0 = n'importe quel dialogue)
    private int dialogueId;
    
    /**
     * Constructeur
     * 
     * @param id Identifiant unique de l'objectif
     * @param npcId ID du PNJ à qui parler
     * @param npcName Nom du PNJ à qui parler
     * @param npcType Type du PNJ (Tacticien ou Guerrier)
     */
    public TalkToNPCObjective(String id, int npcId, String npcName, NPCType npcType) {
        super(id, "Parler à " + npcName);
        this.npcId = npcId;
        this.npcName = npcName;
        this.npcType = npcType;
        this.dialogueId = 0;
        this.requiredAmount = 1;
    }
    
    /**
     * Constructeur avec dialogue spécifique
     * 
     * @param id Identifiant unique de l'objectif
     * @param npcId ID du PNJ à qui parler
     * @param npcName Nom du PNJ à qui parler
     * @param npcType Type du PNJ (Tacticien ou Guerrier)
     * @param dialogueId ID du dialogue spécifique à avoir
     */
    public TalkToNPCObjective(String id, int npcId, String npcName, NPCType npcType, int dialogueId) {
        super(id, "Parler à " + npcName + " à propos de " + dialogueId);
        this.npcId = npcId;
        this.npcName = npcName;
        this.npcType = npcType;
        this.dialogueId = dialogueId;
        this.requiredAmount = 1;
    }
    
    @Override
    public boolean update(float deltaTime, Player player) {
        // Cette méthode ne fait rien car l'objectif est mis à jour via onTalkToNPC
        return false;
    }
    
    /**
     * Méthode appelée lorsque le joueur parle à un PNJ
     * 
     * @param npcId ID du PNJ
     * @param dialogueId ID du dialogue
     * @return true si l'objectif a été mis à jour, false sinon
     */
    public boolean onTalkToNPC(int npcId, int dialogueId) {
        if (state != QuestObjectiveState.IN_PROGRESS) {
            return false;
        }
        
        // Vérifier si le PNJ correspond
        if (this.npcId != npcId) {
            return false;
        }
        
        // Vérifier si le dialogue correspond (si un dialogue spécifique est requis)
        if (this.dialogueId > 0 && this.dialogueId != dialogueId) {
            return false;
        }
        
        // Compléter l'objectif
        return complete();
    }
    
    /**
     * Obtenir l'ID du PNJ à qui parler
     * 
     * @return ID du PNJ
     */
    public int getNpcId() {
        return npcId;
    }
    
    /**
     * Obtenir le nom du PNJ à qui parler
     * 
     * @return Nom du PNJ
     */
    public String getNpcName() {
        return npcName;
    }
    
    /**
     * Obtenir le type du PNJ
     * 
     * @return Type du PNJ
     */
    public NPCType getNpcType() {
        return npcType;
    }
    
    /**
     * Obtenir l'ID du dialogue spécifique à avoir
     * 
     * @return ID du dialogue, ou 0 si n'importe quel dialogue
     */
    public int getDialogueId() {
        return dialogueId;
    }
    
    /**
     * Énumération des types de PNJ
     */
    public enum NPCType {
        TACTICIEN("Tacticien"),
        GUERRIER("Guerrier"),
        NEUTRE("Neutre");
        
        private final String name;
        
        NPCType(String name) {
            this.name = name;
        }
        
        /**
         * Obtenir le nom du type de PNJ
         * 
         * @return Nom du type
         */
        public String getName() {
            return name;
        }
    }
}

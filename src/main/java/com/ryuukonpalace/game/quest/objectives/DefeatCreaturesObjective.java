package com.ryuukonpalace.game.quest.objectives;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureType;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.quest.QuestObjective;
import com.ryuukonpalace.game.quest.QuestObjectiveState;

/**
 * Objectif de quête consistant à vaincre un certain nombre de créatures.
 */
public class DefeatCreaturesObjective extends QuestObjective {
    
    // Type de créature à vaincre (null = n'importe quel type)
    private CreatureType creatureType;
    
    // ID spécifique de créature à vaincre (0 = n'importe quelle créature du type spécifié)
    private int creatureId;
    
    // Nom de la créature à vaincre (pour l'affichage)
    private String creatureName;
    
    /**
     * Constructeur pour vaincre n'importe quelle créature
     * 
     * @param id Identifiant unique de l'objectif
     * @param requiredAmount Nombre de créatures à vaincre
     */
    public DefeatCreaturesObjective(String id, int requiredAmount) {
        super(id, "Vaincre " + requiredAmount + " créatures");
        this.requiredAmount = requiredAmount;
        this.creatureType = null;
        this.creatureId = 0;
        this.creatureName = "créatures";
    }
    
    /**
     * Constructeur pour vaincre des créatures d'un type spécifique
     * 
     * @param id Identifiant unique de l'objectif
     * @param creatureType Type de créature à vaincre
     * @param requiredAmount Nombre de créatures à vaincre
     */
    public DefeatCreaturesObjective(String id, CreatureType creatureType, int requiredAmount) {
        super(id, "Vaincre " + requiredAmount + " créatures de type " + creatureType.getName());
        this.requiredAmount = requiredAmount;
        this.creatureType = creatureType;
        this.creatureId = 0;
        this.creatureName = "créatures de type " + creatureType.getName();
    }
    
    /**
     * Constructeur pour vaincre une créature spécifique
     * 
     * @param id Identifiant unique de l'objectif
     * @param creatureId ID de la créature à vaincre
     * @param creatureName Nom de la créature à vaincre
     * @param requiredAmount Nombre de créatures à vaincre
     */
    public DefeatCreaturesObjective(String id, int creatureId, String creatureName, int requiredAmount) {
        super(id, "Vaincre " + requiredAmount + " " + creatureName);
        this.requiredAmount = requiredAmount;
        this.creatureType = null;
        this.creatureId = creatureId;
        this.creatureName = creatureName;
    }
    
    @Override
    public boolean update(float deltaTime, Player player) {
        // Cette méthode ne fait rien car l'objectif est mis à jour via onCreatureDefeated
        return false;
    }
    
    /**
     * Méthode appelée lorsqu'une créature est vaincue
     * 
     * @param creature Créature vaincue
     * @return true si l'objectif a été mis à jour, false sinon
     */
    public boolean onCreatureDefeated(Creature creature) {
        if (getState() != QuestObjectiveState.IN_PROGRESS) {
            return false;
        }
        
        // Vérifier si la créature correspond aux critères
        if (creatureId > 0 && creature.getId() != creatureId) {
            return false;
        }
        
        if (creatureType != null && creature.getType() != creatureType) {
            return false;
        }
        
        // Incrémenter le compteur
        return incrementAmount(1);
    }
    
    /**
     * Obtenir le type de créature à vaincre
     * 
     * @return Type de créature, ou null si n'importe quel type
     */
    public CreatureType getCreatureType() {
        return creatureType;
    }
    
    /**
     * Obtenir l'ID de la créature à vaincre
     * 
     * @return ID de la créature, ou 0 si n'importe quelle créature du type spécifié
     */
    public int getCreatureId() {
        return creatureId;
    }
    
    /**
     * Obtenir le nom de la créature à vaincre
     * 
     * @return Nom de la créature
     */
    public String getCreatureName() {
        return creatureName;
    }
    
    /**
     * Définir le type de créature à vaincre par son nom
     * 
     * @param creatureTypeName Nom du type de créature
     */
    public void setCreatureType(String creatureTypeName) {
        try {
            if (creatureTypeName != null && !creatureTypeName.isEmpty()) {
                this.creatureType = CreatureType.valueOf(creatureTypeName);
                this.creatureName = "créatures de type " + this.creatureType.getName();
                this.description = "Vaincre " + this.requiredAmount + " " + this.creatureName;
            } else {
                this.creatureType = null;
                this.creatureName = "créatures";
                this.description = "Vaincre " + this.requiredAmount + " créatures";
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Type de créature invalide: " + creatureTypeName);
            this.creatureType = null;
        }
    }
}

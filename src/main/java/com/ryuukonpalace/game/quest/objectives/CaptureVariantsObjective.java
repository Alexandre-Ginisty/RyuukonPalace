package com.ryuukonpalace.game.quest.objectives;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureType;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.quest.QuestObjective;
import com.ryuukonpalace.game.quest.QuestObjectiveState;

/**
 * Objectif de quête consistant à capturer un certain nombre de variants.
 * Cet objectif est spécifique aux Tacticiens qui utilisent des variants pour combattre.
 */
public class CaptureVariantsObjective extends QuestObjective {
    
    // Type de variant à capturer (null = n'importe quel type)
    private CreatureType variantType;
    
    // ID spécifique de variant à capturer (0 = n'importe quel variant du type spécifié)
    private int variantId;
    
    // Nom du variant à capturer (pour l'affichage)
    private String variantName;
    
    /**
     * Constructeur pour capturer n'importe quel variant
     * 
     * @param id Identifiant unique de l'objectif
     * @param requiredAmount Nombre de variants à capturer
     */
    public CaptureVariantsObjective(String id, int requiredAmount) {
        super(id, "Capturer " + requiredAmount + " variants");
        this.requiredAmount = requiredAmount;
        this.variantType = null;
        this.variantId = 0;
        this.variantName = "variants";
    }
    
    /**
     * Constructeur pour capturer des variants d'un type spécifique
     * 
     * @param id Identifiant unique de l'objectif
     * @param variantType Type de variant à capturer
     * @param requiredAmount Nombre de variants à capturer
     */
    public CaptureVariantsObjective(String id, CreatureType variantType, int requiredAmount) {
        super(id, "Capturer " + requiredAmount + " variants de type " + variantType.getName());
        this.requiredAmount = requiredAmount;
        this.variantType = variantType;
        this.variantId = 0;
        this.variantName = "variants de type " + variantType.getName();
    }
    
    /**
     * Constructeur pour capturer un variant spécifique
     * 
     * @param id Identifiant unique de l'objectif
     * @param variantId ID du variant à capturer
     * @param variantName Nom du variant à capturer
     * @param requiredAmount Nombre de variants à capturer
     */
    public CaptureVariantsObjective(String id, int variantId, String variantName, int requiredAmount) {
        super(id, "Capturer " + requiredAmount + " " + variantName);
        this.requiredAmount = requiredAmount;
        this.variantType = null;
        this.variantId = variantId;
        this.variantName = variantName;
    }
    
    @Override
    public boolean update(float deltaTime, Player player) {
        // Cette méthode ne fait rien car l'objectif est mis à jour via onVariantCaptured
        return false;
    }
    
    /**
     * Méthode appelée lorsqu'un variant est capturé
     * 
     * @param creature Variant capturé
     * @return true si l'objectif a été mis à jour, false sinon
     */
    public boolean onVariantCaptured(Creature creature) {
        if (state != QuestObjectiveState.IN_PROGRESS) {
            return false;
        }
        
        // Vérifier si le variant correspond aux critères
        if (variantId > 0 && creature.getId() != variantId) {
            return false;
        }
        
        if (variantType != null && creature.getType() != variantType) {
            return false;
        }
        
        // Incrémenter le compteur
        return incrementAmount(1);
    }
    
    /**
     * Obtenir le type de variant à capturer
     * 
     * @return Type de variant, ou null si n'importe quel type
     */
    public CreatureType getVariantType() {
        return variantType;
    }
    
    /**
     * Obtenir l'ID du variant à capturer
     * 
     * @return ID du variant, ou 0 si n'importe quel variant du type spécifié
     */
    public int getVariantId() {
        return variantId;
    }
    
    /**
     * Obtenir le nom du variant à capturer
     * 
     * @return Nom du variant
     */
    public String getVariantName() {
        return variantName;
    }
    
    /**
     * Définir le type de variant à capturer par son nom
     * 
     * @param variantTypeName Nom du type de variant
     */
    public void setVariantType(String variantTypeName) {
        try {
            if (variantTypeName != null && !variantTypeName.isEmpty()) {
                this.variantType = CreatureType.valueOf(variantTypeName);
                this.variantName = "variants de type " + this.variantType.getName();
                this.description = "Capturer " + this.requiredAmount + " " + this.variantName;
            } else {
                this.variantType = null;
                this.variantName = "variants";
                this.description = "Capturer " + this.requiredAmount + " variants";
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Type de variant invalide: " + variantTypeName);
            this.variantType = null;
        }
    }
}

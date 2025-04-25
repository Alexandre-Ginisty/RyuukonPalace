package com.ryuukonpalace.game.quest.objectives;

import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.quest.QuestObjective;
import com.ryuukonpalace.game.quest.QuestObjectiveState;

/**
 * Objectif de quête consistant à collecter un certain nombre d'objets.
 */
public class CollectItemsObjective extends QuestObjective {
    
    // ID de l'objet à collecter
    private int itemId;
    
    // Nom de l'objet à collecter (pour l'affichage)
    private String itemName;
    
    // Indique si les objets doivent être retirés de l'inventaire une fois l'objectif complété
    private boolean removeItemsOnCompletion;
    
    /**
     * Constructeur
     * 
     * @param id Identifiant unique de l'objectif
     * @param itemId ID de l'objet à collecter
     * @param itemName Nom de l'objet à collecter
     * @param requiredAmount Nombre d'objets à collecter
     * @param removeItemsOnCompletion Indique si les objets doivent être retirés de l'inventaire une fois l'objectif complété
     */
    public CollectItemsObjective(String id, int itemId, String itemName, int requiredAmount, boolean removeItemsOnCompletion) {
        super(id, "Collecter " + requiredAmount + " " + itemName);
        this.itemId = itemId;
        this.itemName = itemName;
        this.requiredAmount = requiredAmount;
        this.removeItemsOnCompletion = removeItemsOnCompletion;
    }
    
    @Override
    public boolean update(float deltaTime, Player player) {
        if (getState() != QuestObjectiveState.IN_PROGRESS) {
            return false;
        }
        
        // Vérifier combien d'objets le joueur possède
        int count = player.getInventory().getItemQuantity(itemId);
        
        // Mettre à jour la quantité actuelle
        if (count != currentAmount) {
            currentAmount = count;
            
            // Vérifier si l'objectif est complété
            if (currentAmount >= requiredAmount) {
                currentAmount = requiredAmount;
                complete();
                
                // Retirer les objets si nécessaire
                if (removeItemsOnCompletion) {
                    player.getInventory().removeItem(itemId, requiredAmount);
                }
                
                return true;
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Méthode appelée lorsqu'un objet est ajouté à l'inventaire
     * 
     * @param item Objet ajouté
     * @param count Nombre d'objets ajoutés
     * @return true si l'objectif a été mis à jour, false sinon
     */
    public boolean onItemAdded(Item item, int count) {
        if (getState() != QuestObjectiveState.IN_PROGRESS) {
            return false;
        }
        
        // Vérifier si l'objet correspond
        if (item.getId() != itemId) {
            return false;
        }
        
        // L'objectif sera mis à jour lors du prochain appel à update()
        return true;
    }
    
    /**
     * Obtenir l'ID de l'objet à collecter
     * 
     * @return ID de l'objet
     */
    public int getItemId() {
        return itemId;
    }
    
    /**
     * Obtenir le nom de l'objet à collecter
     * 
     * @return Nom de l'objet
     */
    public String getItemName() {
        return itemName;
    }
    
    /**
     * Vérifier si les objets doivent être retirés de l'inventaire une fois l'objectif complété
     * 
     * @return true si les objets doivent être retirés, false sinon
     */
    public boolean isRemoveItemsOnCompletion() {
        return removeItemsOnCompletion;
    }
    
    /**
     * Définir si les objets doivent être retirés de l'inventaire une fois l'objectif complété
     * 
     * @param removeItemsOnCompletion true si les objets doivent être retirés, false sinon
     */
    public void setRemoveItemsOnCompletion(boolean removeItemsOnCompletion) {
        this.removeItemsOnCompletion = removeItemsOnCompletion;
    }
}

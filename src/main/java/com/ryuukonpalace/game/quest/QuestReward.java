package com.ryuukonpalace.game.quest;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.player.Player;
import org.json.JSONObject;

/**
 * Classe représentant une récompense de quête.
 * Les récompenses peuvent être de différents types (expérience, objets, créatures, etc.)
 */
public class QuestReward {
    
    // Type de récompense
    private RewardType type;
    
    // Quantité de la récompense (pour l'expérience, l'argent, etc.)
    private int amount;
    
    // ID de l'objet ou de la créature (pour les récompenses d'objets ou de créatures)
    private int itemOrCreatureId;
    
    // Objet ou créature (pour les récompenses d'objets ou de créatures)
    private Object itemOrCreature;
    
    // Description de la récompense
    private String description;
    
    /**
     * Constructeur pour une récompense d'expérience
     * 
     * @param amount Quantité d'expérience
     */
    public static QuestReward createExperienceReward(int amount) {
        QuestReward reward = new QuestReward();
        reward.type = RewardType.EXPERIENCE;
        reward.amount = amount;
        reward.description = amount + " points d'expérience";
        return reward;
    }
    
    /**
     * Constructeur pour une récompense d'argent
     * 
     * @param amount Quantité d'argent
     */
    public static QuestReward createMoneyReward(int amount) {
        QuestReward reward = new QuestReward();
        reward.type = RewardType.MONEY;
        reward.amount = amount;
        reward.description = amount + " pièces d'or";
        return reward;
    }
    
    /**
     * Constructeur pour une récompense d'objet
     * 
     * @param item Objet
     * @param amount Quantité
     */
    public static QuestReward createItemReward(Item item, int amount) {
        QuestReward reward = new QuestReward();
        reward.type = RewardType.ITEM;
        reward.itemOrCreature = item;
        reward.itemOrCreatureId = item.getId();
        reward.amount = amount;
        reward.description = amount + " " + item.getName();
        return reward;
    }
    
    /**
     * Constructeur pour une récompense d'objet par ID
     * 
     * @param itemId ID de l'objet
     * @param itemName Nom de l'objet
     * @param amount Quantité
     */
    public static QuestReward createItemReward(int itemId, String itemName, int amount) {
        QuestReward reward = new QuestReward();
        reward.type = RewardType.ITEM;
        reward.itemOrCreatureId = itemId;
        reward.amount = amount;
        reward.description = amount + " " + itemName;
        return reward;
    }
    
    /**
     * Constructeur pour une récompense de créature
     * 
     * @param creature Créature
     */
    public static QuestReward createCreatureReward(Creature creature) {
        QuestReward reward = new QuestReward();
        reward.type = RewardType.CREATURE;
        reward.itemOrCreature = creature;
        reward.itemOrCreatureId = creature.getId();
        reward.amount = 1;
        reward.description = "Variant: " + creature.getName();
        return reward;
    }
    
    /**
     * Constructeur pour une récompense de créature par ID
     * 
     * @param creatureId ID de la créature
     * @param creatureName Nom de la créature
     */
    public static QuestReward createCreatureReward(int creatureId, String creatureName) {
        QuestReward reward = new QuestReward();
        reward.type = RewardType.CREATURE;
        reward.itemOrCreatureId = creatureId;
        reward.amount = 1;
        reward.description = "Variant: " + creatureName;
        return reward;
    }
    
    /**
     * Constructeur pour une récompense de réputation
     * 
     * @param factionName Nom de la faction
     * @param amount Quantité de réputation
     */
    public static QuestReward createReputationReward(String factionName, int amount) {
        QuestReward reward = new QuestReward();
        reward.type = RewardType.REPUTATION;
        reward.itemOrCreatureId = 0;
        reward.itemOrCreature = factionName;
        reward.amount = amount;
        reward.description = amount + " points de réputation avec la faction " + factionName;
        return reward;
    }
    
    /**
     * Constructeur privé
     */
    private QuestReward() {
    }
    
    /**
     * Donner la récompense au joueur
     * 
     * @param player Joueur
     * @return true si la récompense a été donnée, false sinon
     */
    public boolean giveToPlayer(Player player) {
        switch (type) {
            case EXPERIENCE:
                player.addExperience(amount);
                return true;
                
            case MONEY:
                player.addMoney(amount);
                return true;
                
            case ITEM:
                if (itemOrCreature instanceof Item) {
                    // Si l'objet est déjà créé, l'ajouter directement
                    Item item = (Item) itemOrCreature;
                    for (int i = 0; i < amount; i++) {
                        player.getInventory().addItem(item);
                    }
                    return true;
                } else {
                    // Créer l'objet à partir de l'ID
                    // Cette partie dépend de l'implémentation du système d'objets
                    return false;
                }
                
            case CREATURE:
                if (itemOrCreature instanceof Creature) {
                    return player.addCreature((Creature) itemOrCreature);
                } else {
                    // Créer la créature à partir de l'ID
                    // Cette partie dépend de l'implémentation du système de créatures
                    return false;
                }
                
            case REPUTATION:
                if (itemOrCreature instanceof String) {
                    String factionName = (String) itemOrCreature;
                    player.addFactionReputation(factionName, amount);
                    return true;
                }
                return false;
                
            default:
                return false;
        }
    }
    
    /**
     * Obtenir le type de récompense
     * 
     * @return Type de récompense
     */
    public RewardType getType() {
        return type;
    }
    
    /**
     * Obtenir la quantité de la récompense
     * 
     * @return Quantité
     */
    public int getAmount() {
        return amount;
    }
    
    /**
     * Obtenir l'ID de l'objet ou de la créature
     * 
     * @return ID de l'objet ou de la créature
     */
    public int getItemOrCreatureId() {
        return itemOrCreatureId;
    }
    
    /**
     * Obtenir l'objet ou la créature
     * 
     * @return Objet ou créature
     */
    public Object getItemOrCreature() {
        return itemOrCreature;
    }
    
    /**
     * Obtenir la description de la récompense
     * 
     * @return Description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Convertir la récompense en JSONObject
     * 
     * @return JSONObject représentant la récompense
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", type.toString());
        json.put("amount", amount);
        json.put("itemOrCreatureId", itemOrCreatureId);
        json.put("description", description);
        
        // Sauvegarder le nom de l'objet ou de la créature pour les récompenses d'objets ou de créatures
        if (type == RewardType.ITEM) {
            if (itemOrCreature instanceof Item) {
                json.put("itemName", ((Item) itemOrCreature).getName());
            }
        } else if (type == RewardType.CREATURE) {
            if (itemOrCreature instanceof Creature) {
                json.put("creatureName", ((Creature) itemOrCreature).getName());
            }
        } else if (type == RewardType.REPUTATION) {
            if (itemOrCreature instanceof String) {
                json.put("factionName", (String) itemOrCreature);
            }
        }
        
        return json;
    }
    
    /**
     * Créer une récompense à partir d'un JSONObject
     * 
     * @param json JSONObject représentant la récompense
     * @return Nouvelle récompense
     */
    public static QuestReward fromJson(JSONObject json) {
        RewardType type = RewardType.valueOf(json.getString("type"));
        int amount = json.getInt("amount");
        int itemOrCreatureId = json.getInt("itemOrCreatureId");
        String description = json.getString("description");
        
        QuestReward reward = new QuestReward();
        reward.type = type;
        reward.amount = amount;
        reward.itemOrCreatureId = itemOrCreatureId;
        reward.description = description;
        
        // Charger les informations spécifiques au type
        switch (type) {
            case ITEM:
                if (json.has("itemName")) {
                    String itemName = json.getString("itemName");
                    reward.itemOrCreature = itemName; // Utiliser la variable
                    // Ici, on pourrait charger l'objet à partir de son ID et de son nom
                }
                break;
                
            case CREATURE:
                if (json.has("creatureName")) {
                    String creatureName = json.getString("creatureName");
                    reward.itemOrCreature = creatureName; // Utiliser la variable
                    // Ici, on pourrait charger la créature à partir de son ID et de son nom
                }
                break;
                
            case REPUTATION:
                if (json.has("factionName")) {
                    String factionName = json.getString("factionName");
                    reward.itemOrCreature = factionName;
                }
                break;
                
            case EXPERIENCE:
                if (json.has("amount")) {
                    reward.amount = json.getInt("amount");
                }
                break;
                
            case MONEY:
                if (json.has("amount")) {
                    reward.amount = json.getInt("amount");
                }
                break;
        }
        
        return reward;
    }
    
    /**
     * Créer une copie de cette récompense
     * 
     * @return Nouvelle instance de QuestReward avec les mêmes attributs
     */
    @Override
    public QuestReward clone() {
        QuestReward clone = new QuestReward();
        clone.type = this.type;
        clone.amount = this.amount;
        clone.itemOrCreatureId = this.itemOrCreatureId;
        clone.itemOrCreature = this.itemOrCreature; // Référence partagée, pas de clonage profond
        clone.description = this.description;
        return clone;
    }
    
    /**
     * Énumération des types de récompenses
     */
    public enum RewardType {
        EXPERIENCE,
        MONEY,
        ITEM,
        CREATURE,
        REPUTATION
    }
}

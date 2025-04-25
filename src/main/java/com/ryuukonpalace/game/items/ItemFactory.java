package com.ryuukonpalace.game.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Fabrique d'objets pour le jeu.
 * Permet de créer des objets à partir de leur ID ou de générer des objets aléatoires.
 */
public class ItemFactory {
    
    // Instance singleton
    private static ItemFactory instance;
    
    // Map des définitions d'objets par ID
    private Map<Integer, ItemDefinition> itemDefinitions;
    
    // Générateur de nombres aléatoires
    private Random random;
    
    /**
     * Constructeur privé (singleton)
     */
    private ItemFactory() {
        this.itemDefinitions = new HashMap<>();
        this.random = new Random();
        
        // Initialiser les définitions d'objets
        initItemDefinitions();
    }
    
    /**
     * Obtenir l'instance unique de la fabrique d'objets
     * 
     * @return Instance de ItemFactory
     */
    public static ItemFactory getInstance() {
        if (instance == null) {
            instance = new ItemFactory();
        }
        return instance;
    }
    
    /**
     * Initialiser les définitions d'objets
     */
    private void initItemDefinitions() {
        // Potions
        addItemDefinition(1, "Potion de soin", "Restaure 20 PV", 50, ItemType.CONSUMABLE);
        addItemDefinition(2, "Super potion", "Restaure 50 PV", 200, ItemType.CONSUMABLE);
        addItemDefinition(3, "Hyper potion", "Restaure 100 PV", 500, ItemType.CONSUMABLE);
        addItemDefinition(4, "Potion max", "Restaure tous les PV", 1000, ItemType.CONSUMABLE);
        
        // Pierres de capture
        addItemDefinition(10, "Pierre de capture", "Permet de capturer des créatures", 100, ItemType.CAPTURE_STONE);
        addItemDefinition(11, "Pierre de capture +", "Meilleur taux de capture", 300, ItemType.CAPTURE_STONE);
        addItemDefinition(12, "Pierre de capture ultime", "Taux de capture très élevé", 1000, ItemType.CAPTURE_STONE);
        
        // Objets tenus
        addItemDefinition(20, "Bandeau focus", "Augmente la précision", 500, ItemType.HELD_ITEM);
        addItemDefinition(21, "Gants de puissance", "Augmente l'attaque", 500, ItemType.HELD_ITEM);
        addItemDefinition(22, "Armure légère", "Augmente la défense", 500, ItemType.HELD_ITEM);
        addItemDefinition(23, "Bottes de célérité", "Augmente la vitesse", 500, ItemType.HELD_ITEM);
        
        // Objets rares
        addItemDefinition(30, "Cristal de feu", "Augmente la puissance des attaques de feu", 2000, ItemType.RARE_ITEM);
        addItemDefinition(31, "Cristal d'eau", "Augmente la puissance des attaques d'eau", 2000, ItemType.RARE_ITEM);
        addItemDefinition(32, "Cristal de terre", "Augmente la puissance des attaques de terre", 2000, ItemType.RARE_ITEM);
        addItemDefinition(33, "Cristal d'air", "Augmente la puissance des attaques d'air", 2000, ItemType.RARE_ITEM);
        
        // Artefacts
        addItemDefinition(40, "Amulette du dragon", "Artefact légendaire qui augmente toutes les statistiques", 10000, ItemType.ARTIFACT);
        addItemDefinition(41, "Couronne du roi", "Artefact légendaire qui augmente le charisme", 10000, ItemType.ARTIFACT);
        addItemDefinition(42, "Épée de l'ancien", "Artefact légendaire qui augmente l'attaque", 10000, ItemType.ARTIFACT);
    }
    
    /**
     * Ajouter une définition d'objet
     * 
     * @param id ID de l'objet
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param price Prix de l'objet
     * @param type Type de l'objet
     */
    private void addItemDefinition(int id, String name, String description, int price, ItemType type) {
        ItemDefinition definition = new ItemDefinition(id, name, description, price, type);
        itemDefinitions.put(id, definition);
    }
    
    /**
     * Créer un objet à partir de son ID
     * 
     * @param itemId ID de l'objet
     * @return L'objet créé, ou null si l'ID n'existe pas
     */
    public Item createItem(int itemId) {
        ItemDefinition definition = itemDefinitions.get(itemId);
        if (definition == null) {
            return null;
        }
        
        switch (definition.type) {
            case CONSUMABLE:
                return new ConsumableItem(definition.id, definition.name, definition.description, definition.price);
            case CAPTURE_STONE:
                return new CaptureStone(definition.id, definition.name, definition.description, definition.price, CaptureStoneMaterial.QUARTZ, CaptureStoneType.NEUTRAL);
            case HELD_ITEM:
                return new HeldItem(definition.id, definition.name, definition.description, definition.price);
            case RARE_ITEM:
                return new RareItem(definition.id, definition.name, definition.description, definition.price);
            case ARTIFACT:
                return new Artifact(definition.id, definition.name, definition.description, definition.price);
            default:
                return new GenericItem(definition.id, definition.name, definition.description, definition.price);
        }
    }
    
    /**
     * Créer une liste d'objets aléatoires
     * 
     * @param count Nombre d'objets à créer
     * @param minRarity Rareté minimale (1-10)
     * @param maxRarity Rareté maximale (1-10)
     * @return Liste d'objets aléatoires
     */
    public List<Item> createRandomItems(int count, int minRarity, int maxRarity) {
        List<Item> items = new ArrayList<>();
        List<ItemDefinition> eligibleItems = getItemsByRarity(minRarity, maxRarity);
        
        if (eligibleItems.isEmpty()) {
            return items; // Retourner une liste vide si aucun objet ne correspond aux critères
        }
        
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(eligibleItems.size());
            ItemDefinition definition = eligibleItems.get(index);
            Item item = createItem(definition.id);
            if (item != null) {
                items.add(item);
            }
        }
        
        return items;
    }
    
    /**
     * Obtenir les définitions d'objets par rareté
     * 
     * @param minRarity Rareté minimale (1-10)
     * @param maxRarity Rareté maximale (1-10)
     * @return Liste des définitions d'objets correspondant à la rareté
     */
    private List<ItemDefinition> getItemsByRarity(int minRarity, int maxRarity) {
        List<ItemDefinition> result = new ArrayList<>();
        
        for (ItemDefinition definition : itemDefinitions.values()) {
            int rarity = getRarityForItemType(definition.type);
            if (rarity >= minRarity && rarity <= maxRarity) {
                result.add(definition);
            }
        }
        
        return result;
    }
    
    /**
     * Obtenir la rareté pour un type d'objet
     * 
     * @param type Type d'objet
     * @return Rareté (1-10)
     */
    private int getRarityForItemType(ItemType type) {
        switch (type) {
            case CONSUMABLE:
                return 1; // Commun
            case CAPTURE_STONE:
                return 3; // Peu commun
            case HELD_ITEM:
                return 5; // Rare
            case RARE_ITEM:
                return 8; // Très rare
            case ARTIFACT:
                return 10; // Légendaire
            default:
                return 1; // Par défaut, commun
        }
    }
    
    /**
     * Obtenir toutes les définitions d'objets
     * 
     * @return Map des définitions d'objets
     */
    public Map<Integer, ItemDefinition> getItemDefinitions() {
        return new HashMap<>(itemDefinitions);
    }
    
    /**
     * Classe interne pour représenter une définition d'objet
     */
    public static class ItemDefinition {
        private int id;
        private String name;
        private String description;
        private int price;
        private ItemType type;
        
        public ItemDefinition(int id, String name, String description, int price, ItemType type) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.type = type;
        }
        
        public int getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getPrice() {
            return price;
        }
        
        public ItemType getType() {
            return type;
        }
    }
    
    /**
     * Énumération des types d'objets
     */
    public enum ItemType {
        CONSUMABLE,    // Objets consommables (potions, etc.)
        CAPTURE_STONE, // Pierres de capture
        HELD_ITEM,     // Objets tenus par les créatures
        RARE_ITEM,     // Objets rares
        ARTIFACT,      // Artefacts légendaires
        GENERIC        // Objets génériques
    }
    
    /**
     * Classe pour les objets génériques
     */
    private static class GenericItem extends Item {
        public GenericItem(int id, String name, String description, int price) {
            super(id, name, description, price);
        }
        
        @Override
        public boolean use() {
            // Implémentation par défaut
            return false;
        }
    }
    
    /**
     * Classe pour les objets consommables
     */
    private static class ConsumableItem extends Item {
        public ConsumableItem(int id, String name, String description, int price) {
            super(id, name, description, price);
        }
        
        @Override
        public boolean use() {
            // Implémentation pour les consommables
            return true;
        }
    }
    
    /**
     * Classe pour les objets rares
     */
    private static class RareItem extends Item {
        public RareItem(int id, String name, String description, int price) {
            super(id, name, description, price);
        }
        
        @Override
        public boolean use() {
            // Implémentation pour les objets rares
            return true;
        }
    }
    
    /**
     * Classe pour les artefacts
     */
    private static class Artifact extends Item {
        public Artifact(int id, String name, String description, int price) {
            super(id, name, description, price);
        }
        
        @Override
        public boolean use() {
            // Implémentation pour les artefacts
            return true;
        }
    }
    
    /**
     * Classe pour les objets tenus
     */
    private static class HeldItem extends Item {
        public HeldItem(int id, String name, String description, int price) {
            super(id, name, description, price);
        }
        
        @Override
        public boolean use() {
            // Implémentation pour les objets tenus
            return true;
        }
    }
}

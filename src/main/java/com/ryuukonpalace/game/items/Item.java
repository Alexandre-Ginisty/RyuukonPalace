package com.ryuukonpalace.game.items;

/**
 * Classe de base pour tous les objets du jeu.
 */
public abstract class Item {
    
    // Nom de l'objet
    private String name;
    
    // Description de l'objet
    private String description;
    
    // ID de la texture de l'objet
    private int textureId;
    
    // Rareté de l'objet
    private ItemRarity rarity;
    
    /**
     * Constructeur
     * 
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param textureId ID de la texture de l'objet
     * @param rarity Rareté de l'objet
     */
    public Item(String name, String description, int textureId, ItemRarity rarity) {
        this.name = name;
        this.description = description;
        this.textureId = textureId;
        this.rarity = rarity;
    }
    
    /**
     * Obtenir le nom de l'objet
     * 
     * @return Nom de l'objet
     */
    public String getName() {
        return name;
    }
    
    /**
     * Obtenir la description de l'objet
     * 
     * @return Description de l'objet
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtenir l'ID de la texture de l'objet
     * 
     * @return ID de la texture
     */
    public int getTextureId() {
        return textureId;
    }
    
    /**
     * Obtenir la rareté de l'objet
     * 
     * @return Rareté de l'objet
     */
    public ItemRarity getRarity() {
        return rarity;
    }
    
    /**
     * Utiliser l'objet
     * 
     * @return true si l'objet a été utilisé avec succès, false sinon
     */
    public abstract boolean use();
    
    /**
     * Énumération des raretés d'objets
     */
    public enum ItemRarity {
        COMMON,     // Commun
        UNCOMMON,   // Peu commun
        RARE,       // Rare
        EPIC,       // Épique
        LEGENDARY   // Légendaire
    }
}

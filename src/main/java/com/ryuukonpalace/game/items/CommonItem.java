package com.ryuukonpalace.game.items;

/**
 * Classe représentant un objet commun du jeu
 */
public class CommonItem extends Item {
    
    // Valeur de l'objet (en crystaux)
    private int value;
    
    /**
     * Constructeur
     * 
     * @param id ID de l'objet
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param textureId ID de la texture de l'objet
     * @param rarity Rareté de l'objet
     * @param value Valeur de l'objet en crystaux
     */
    public CommonItem(int id, String name, String description, int textureId, ItemRarity rarity, int value) {
        super(id, name, description, value, textureId, rarity);
    }
    
    /**
     * Constructeur simplifié avec ID de texture par défaut
     * 
     * @param id ID de l'objet
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param value Valeur de l'objet en crystaux
     */
    public CommonItem(int id, String name, String description, int value) {
        super(id, name, description, value);
    }
    
    /**
     * Constructeur original (pour compatibilité)
     * 
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param textureId ID de la texture de l'objet
     * @param rarity Rareté de l'objet
     * @param value Valeur de l'objet en crystaux
     */
    public CommonItem(String name, String description, int textureId, ItemRarity rarity, int value) {
        super(0, name, description, value, textureId, rarity);
    }
    
    /**
     * Constructeur simplifié original (pour compatibilité)
     * 
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param value Valeur de l'objet en crystaux
     */
    public CommonItem(String name, String description, int value) {
        super(0, name, description, value);
    }
    
    /**
     * Obtenir la valeur de l'objet
     * 
     * @return Valeur de l'objet en crystaux
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Définir la valeur de l'objet
     * 
     * @param value Nouvelle valeur de l'objet
     */
    public void setValue(int value) {
        this.value = value;
    }
    
    /**
     * Utiliser l'objet
     * 
     * @return true si l'objet a été utilisé avec succès, false sinon
     */
    @Override
    public boolean use() {
        // Les objets communs ne font rien de spécial quand ils sont utilisés
        // Ils sont généralement vendus ou utilisés comme composants
        return false;
    }
}

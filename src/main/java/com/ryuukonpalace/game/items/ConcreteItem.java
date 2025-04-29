package com.ryuukonpalace.game.items;

/**
 * Implémentation concrète de la classe Item pour les tests.
 */
public class ConcreteItem extends Item {
    
    private ItemType type;
    
    /**
     * Constructeur
     * 
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param type Type de l'objet
     */
    public ConcreteItem(String name, String description, ItemType type) {
        super(0, name, description, 0);
        this.type = type;
    }
    
    /**
     * Obtenir le type de l'objet
     * 
     * @return Type de l'objet
     */
    public ItemType getType() {
        return type;
    }
    
    @Override
    public boolean use() {
        // Implémentation simple pour les tests
        return true;
    }
}

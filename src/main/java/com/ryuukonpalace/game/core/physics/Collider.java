package com.ryuukonpalace.game.core.physics;

/**
 * Interface de base pour tous les types de colliders.
 * Permet d'implémenter différentes formes de collision.
 */
public interface Collider {
    
    /**
     * Vérifier si ce collider est en collision avec un autre
     * @param other L'autre collider à tester
     * @return true si les colliders sont en collision, false sinon
     */
    boolean collidesWith(Collider other);
    
    /**
     * Obtenir le type de ce collider
     * @return Le type de collider
     */
    ColliderType getType();
    
    /**
     * Mettre à jour la position du collider
     * @param x Nouvelle position X
     * @param y Nouvelle position Y
     */
    void setPosition(float x, float y);
    
    /**
     * Obtenir la position X du collider
     * @return Position X
     */
    float getX();
    
    /**
     * Obtenir la position Y du collider
     * @return Position Y
     */
    float getY();
    
    /**
     * Types de colliders disponibles
     */
    enum ColliderType {
        RECTANGLE,
        CIRCLE
    }
}

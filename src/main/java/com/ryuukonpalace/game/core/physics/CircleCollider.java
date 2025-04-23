package com.ryuukonpalace.game.core.physics;

/**
 * Collider circulaire pour la détection de collision.
 * Utilisé pour les objets avec une forme circulaire comme les créatures, les projectiles, etc.
 */
public class CircleCollider implements Collider {
    
    private float x;
    private float y;
    private float radius;
    
    /**
     * Constructeur pour un collider circulaire
     * @param x Position X du centre
     * @param y Position Y du centre
     * @param radius Rayon du cercle
     */
    public CircleCollider(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    
    @Override
    public boolean collidesWith(Collider other) {
        if (other.getType() == ColliderType.CIRCLE) {
            return collidesWithCircle((CircleCollider) other);
        } else if (other.getType() == ColliderType.RECTANGLE) {
            return other.collidesWith(this); // Déléguer à RectangleCollider
        }
        return false;
    }
    
    /**
     * Vérifier la collision avec un autre cercle
     * @param other L'autre cercle
     * @return true s'il y a collision, false sinon
     */
    private boolean collidesWithCircle(CircleCollider other) {
        // Calculer la distance entre les centres
        float distanceX = x - other.x;
        float distanceY = y - other.y;
        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        
        // Comparer avec la somme des rayons au carré
        float radiusSum = radius + other.radius;
        return distanceSquared <= (radiusSum * radiusSum);
    }
    
    @Override
    public ColliderType getType() {
        return ColliderType.CIRCLE;
    }
    
    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public float getX() {
        return x;
    }
    
    @Override
    public float getY() {
        return y;
    }
    
    /**
     * Obtenir le rayon du cercle
     * @return Rayon
     */
    public float getRadius() {
        return radius;
    }
    
    /**
     * Définir le rayon du cercle
     * @param radius Nouveau rayon
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }
}

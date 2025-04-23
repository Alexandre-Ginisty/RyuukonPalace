package com.ryuukonpalace.game.core.physics;

/**
 * Collider rectangulaire pour la détection de collision.
 * Utilisé pour les objets avec une forme rectangulaire comme les personnages, les bâtiments, etc.
 */
public class RectangleCollider implements Collider {
    
    private float x;
    private float y;
    private float width;
    private float height;
    
    /**
     * Constructeur pour un collider rectangulaire
     * @param x Position X du coin supérieur gauche
     * @param y Position Y du coin supérieur gauche
     * @param width Largeur du rectangle
     * @param height Hauteur du rectangle
     */
    public RectangleCollider(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public boolean collidesWith(Collider other) {
        if (other.getType() == ColliderType.RECTANGLE) {
            return collidesWithRectangle((RectangleCollider) other);
        } else if (other.getType() == ColliderType.CIRCLE) {
            return collidesWithCircle((CircleCollider) other);
        }
        return false;
    }
    
    /**
     * Vérifier la collision avec un autre rectangle
     * @param other L'autre rectangle
     * @return true s'il y a collision, false sinon
     */
    private boolean collidesWithRectangle(RectangleCollider other) {
        // Algorithme AABB (Axis-Aligned Bounding Box)
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }
    
    /**
     * Vérifier la collision avec un cercle
     * @param circle Le cercle
     * @return true s'il y a collision, false sinon
     */
    private boolean collidesWithCircle(CircleCollider circle) {
        // Trouver le point le plus proche du cercle sur le rectangle
        float closestX = clamp(circle.getX(), x, x + width);
        float closestY = clamp(circle.getY(), y, y + height);
        
        // Calculer la distance entre le point le plus proche et le centre du cercle
        float distanceX = circle.getX() - closestX;
        float distanceY = circle.getY() - closestY;
        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        
        // Comparer avec le rayon au carré
        return distanceSquared <= (circle.getRadius() * circle.getRadius());
    }
    
    /**
     * Limiter une valeur entre un minimum et un maximum
     * @param value Valeur à limiter
     * @param min Minimum
     * @param max Maximum
     * @return Valeur limitée
     */
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    @Override
    public ColliderType getType() {
        return ColliderType.RECTANGLE;
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
     * Obtenir la largeur du rectangle
     * @return Largeur
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * Obtenir la hauteur du rectangle
     * @return Hauteur
     */
    public float getHeight() {
        return height;
    }
    
    /**
     * Définir les dimensions du rectangle
     * @param width Nouvelle largeur
     * @param height Nouvelle hauteur
     */
    public void setDimensions(float width, float height) {
        this.width = width;
        this.height = height;
    }
}

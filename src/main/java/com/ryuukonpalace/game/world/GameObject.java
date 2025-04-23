package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.core.physics.Collider;

/**
 * Classe de base pour tous les objets du jeu.
 * Chaque objet a une position, des dimensions, et peut être mis à jour et rendu.
 */
public abstract class GameObject {
    
    // Position
    protected float x;
    protected float y;
    
    // Dimensions
    protected float width;
    protected float height;
    
    // Collider
    protected Collider collider;
    
    // Groupe de collision (pour déterminer quels objets peuvent interagir entre eux)
    protected String collisionGroup;
    
    /**
     * Constructeur
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     */
    public GameObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.collisionGroup = "default";
    }
    
    /**
     * Constructeur avec dimensions entières
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     */
    public GameObject(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.collisionGroup = "default";
    }
    
    /**
     * Constructeur avec groupe de collision
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param collisionGroup Groupe de collision
     */
    public GameObject(float x, float y, float width, float height, String collisionGroup) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.collisionGroup = collisionGroup;
    }
    
    /**
     * Mettre à jour l'objet
     * @param dt Delta time
     */
    public abstract void update(float dt);
    
    /**
     * Rendre l'objet
     */
    public abstract void render();
    
    /**
     * Gérer la collision avec un autre objet
     * @param other L'autre objet
     */
    public abstract void onCollision(GameObject other);
    
    /**
     * Vérifier si cet objet est en collision avec un autre
     * @param other L'autre objet
     * @return true si collision, false sinon
     */
    public boolean collidesWith(GameObject other) {
        if (collider != null && other.getCollider() != null) {
            return collider.collidesWith(other.getCollider());
        }
        return false;
    }
    
    /**
     * Obtenir le groupe de collision de cet objet
     * @return Le groupe de collision
     */
    public String getCollisionGroup() {
        return collisionGroup;
    }
    
    // Getters et setters
    
    public float getX() {
        return x;
    }
    
    public void setX(float x) {
        this.x = x;
        if (collider != null) {
            collider.setPosition(x, y);
        }
    }
    
    public float getY() {
        return y;
    }
    
    public void setY(float y) {
        this.y = y;
        if (collider != null) {
            collider.setPosition(x, y);
        }
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        if (collider != null) {
            collider.setPosition(x, y);
        }
    }
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        this.width = width;
        if (collider != null) {
            collider.setSize(width, height);
        }
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = height;
        if (collider != null) {
            collider.setSize(width, height);
        }
    }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        if (collider != null) {
            collider.setSize(width, height);
        }
    }
    
    public Collider getCollider() {
        return collider;
    }
    
    public void setCollider(Collider collider) {
        this.collider = collider;
    }
}

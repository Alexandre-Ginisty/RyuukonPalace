package com.ryuukonpalace.game.core;

import com.ryuukonpalace.game.core.physics.Collider;
import com.ryuukonpalace.game.core.physics.CollisionManager;
import com.ryuukonpalace.game.core.physics.RectangleCollider;

/**
 * Classe de base pour tous les objets du jeu.
 * Gère la position, le rendu et les collisions.
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
    
    // Groupe de collision
    protected String collisionGroup;
    
    // État actif
    protected boolean active = true;
    
    /**
     * Constructeur pour un GameObject
     * @param x Position X initiale
     * @param y Position Y initiale
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
        
        // Créer un collider rectangulaire par défaut
        this.collider = new RectangleCollider(x, y, width, height);
        
        // Enregistrer le collider auprès du gestionnaire de collision
        if (collisionGroup != null && !collisionGroup.isEmpty()) {
            CollisionManager.getInstance().addCollider(collider, collisionGroup);
        }
    }
    
    /**
     * Mettre à jour l'objet
     * Cette méthode doit être appelée à chaque frame
     * @param deltaTime Temps écoulé depuis la dernière frame en secondes
     */
    public abstract void update(float deltaTime);
    
    /**
     * Dessiner l'objet
     * Cette méthode doit être appelée à chaque frame
     */
    public abstract void render();
    
    /**
     * Déplacer l'objet
     * @param dx Déplacement en X
     * @param dy Déplacement en Y
     */
    public void move(float dx, float dy) {
        x += dx;
        y += dy;
        
        // Mettre à jour la position du collider
        if (collider != null) {
            collider.setPosition(x, y);
        }
    }
    
    /**
     * Définir la position de l'objet
     * @param x Nouvelle position X
     * @param y Nouvelle position Y
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        
        // Mettre à jour la position du collider
        if (collider != null) {
            collider.setPosition(x, y);
        }
    }
    
    /**
     * Vérifier si cet objet est en collision avec un groupe spécifique
     * @param groupName Le nom du groupe
     * @return true si l'objet est en collision avec au moins un membre du groupe, false sinon
     */
    public boolean isCollidingWithGroup(String groupName) {
        if (collider == null) {
            return false;
        }
        return CollisionManager.getInstance().isCollidingWithGroup(collider, groupName);
    }
    
    /**
     * Gérer une collision avec un autre GameObject
     * Cette méthode est appelée lorsqu'une collision est détectée
     * @param other L'autre GameObject
     */
    public abstract void onCollision(GameObject other);
    
    /**
     * Activer ou désactiver l'objet
     * @param active true pour activer, false pour désactiver
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Vérifier si l'objet est actif
     * @return true si l'objet est actif, false sinon
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Obtenir la position X
     * @return Position X
     */
    public float getX() {
        return x;
    }
    
    /**
     * Obtenir la position Y
     * @return Position Y
     */
    public float getY() {
        return y;
    }
    
    /**
     * Obtenir la largeur
     * @return Largeur
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * Obtenir la hauteur
     * @return Hauteur
     */
    public float getHeight() {
        return height;
    }
    
    /**
     * Obtenir le collider
     * @return Collider
     */
    public Collider getCollider() {
        return collider;
    }
    
    /**
     * Obtenir le groupe de collision
     * @return Groupe de collision
     */
    public String getCollisionGroup() {
        return collisionGroup;
    }
    
    /**
     * Nettoyer les ressources lors de la destruction de l'objet
     */
    public void dispose() {
        // Supprimer le collider du gestionnaire de collision
        if (collider != null && collisionGroup != null && !collisionGroup.isEmpty()) {
            CollisionManager.getInstance().removeCollider(collider, collisionGroup);
        }
    }
}

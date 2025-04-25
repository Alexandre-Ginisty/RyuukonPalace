package com.ryuukonpalace.game.ui;

import com.ryuukonpalace.game.core.Renderer;

/**
 * Classe de base pour tous les composants d'interface utilisateur.
 * Fournit les fonctionnalités communes à tous les éléments d'UI.
 */
public abstract class UIComponent {
    // Position et dimensions
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    
    // Visibilité
    protected boolean visible;
    
    // État
    protected boolean enabled;
    protected boolean hovered;
    protected boolean pressed;
    
    // Renderer
    protected Renderer renderer;
    
    /**
     * Constructeur
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     */
    public UIComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;
        this.enabled = true;
        this.hovered = false;
        this.pressed = false;
        this.renderer = Renderer.getInstance();
    }
    
    /**
     * Mettre à jour le composant
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public abstract void update(float deltaTime);
    
    /**
     * Dessiner le composant
     */
    public abstract void render();
    
    /**
     * Vérifier si un point est à l'intérieur du composant
     * 
     * @param pointX Coordonnée X du point
     * @param pointY Coordonnée Y du point
     * @return true si le point est à l'intérieur, false sinon
     */
    public boolean contains(float pointX, float pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }
    
    /**
     * Gérer un événement de clic
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @return true si le clic a été géré, false sinon
     */
    public boolean onClick(float mouseX, float mouseY) {
        if (!visible || !enabled) {
            return false;
        }
        
        if (contains(mouseX, mouseY)) {
            handleClick();
            return true;
        }
        
        return false;
    }
    
    /**
     * Gérer un événement de survol
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     */
    public void onHover(float mouseX, float mouseY) {
        if (!visible || !enabled) {
            hovered = false;
            return;
        }
        
        hovered = contains(mouseX, mouseY);
    }
    
    /**
     * Gérer un événement d'appui
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param pressed true si le bouton est appuyé, false s'il est relâché
     */
    public void onPress(float mouseX, float mouseY, boolean pressed) {
        if (!visible || !enabled) {
            this.pressed = false;
            return;
        }
        
        if (contains(mouseX, mouseY)) {
            this.pressed = pressed;
        } else {
            this.pressed = false;
        }
    }
    
    /**
     * Méthode à implémenter pour gérer un clic
     */
    protected abstract void handleClick();
    
    // Getters et setters
    
    public float getX() {
        return x;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public float getY() {
        return y;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = height;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isHovered() {
        return hovered;
    }
    
    public boolean isPressed() {
        return pressed;
    }
}

package com.ryuukonpalace.game.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Composant de panneau pour l'interface utilisateur.
 * Peut contenir d'autres composants UI.
 */
public class Panel extends UIComponent {
    // Liste des composants enfants
    private List<UIComponent> children;
    
    // Couleur de fond
    private int backgroundColor;
    
    // Texture de fond
    private int backgroundTextureId;
    
    // Transparence
    private float alpha;
    
    // Bordure
    private boolean hasBorder;
    private int borderColor;
    private float borderThickness;
    
    /**
     * Constructeur avec couleur de fond
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param backgroundColor Couleur de fond
     */
    public Panel(float x, float y, float width, float height, int backgroundColor) {
        super(x, y, width, height);
        this.children = new ArrayList<>();
        this.backgroundColor = backgroundColor;
        this.backgroundTextureId = -1;
        this.alpha = 1.0f;
        this.hasBorder = false;
        this.borderColor = 0xFF000000; // Noir
        this.borderThickness = 1.0f;
    }
    
    /**
     * Constructeur avec texture de fond
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param backgroundTextureId ID de la texture de fond
     */
    public Panel(float x, float y, float width, float height, int backgroundTextureId, boolean isTexture) {
        super(x, y, width, height);
        this.children = new ArrayList<>();
        this.backgroundColor = 0x00000000; // Transparent
        this.backgroundTextureId = backgroundTextureId;
        this.alpha = 1.0f;
        this.hasBorder = false;
        this.borderColor = 0xFF000000; // Noir
        this.borderThickness = 1.0f;
    }
    
    @Override
    public void update(float deltaTime) {
        if (!visible || !enabled) {
            return;
        }
        
        // Mettre à jour les composants enfants
        for (UIComponent child : children) {
            child.update(deltaTime);
        }
    }
    
    @Override
    public void render() {
        if (!visible) {
            return;
        }
        
        // Dessiner le fond
        if (backgroundTextureId != -1) {
            // Fond avec texture
            renderer.drawUIElement(backgroundTextureId, x, y, width, height, alpha);
        } else {
            // Fond avec couleur
            int color = backgroundColor;
            // Appliquer l'alpha à la couleur
            int a = (color >> 24) & 0xFF;
            a = (int)(a * alpha) & 0xFF;
            color = (color & 0x00FFFFFF) | (a << 24);
            
            renderer.drawRect(x, y, width, height, color);
        }
        
        // Dessiner la bordure
        if (hasBorder && borderThickness > 0) {
            renderer.drawRectOutline(x, y, width, height, borderThickness, borderColor);
        }
        
        // Dessiner les composants enfants
        for (UIComponent child : children) {
            child.render();
        }
    }
    
    @Override
    protected void handleClick() {
        // Le panneau lui-même ne fait rien quand on clique dessus
    }
    
    /**
     * Gérer un événement de clic pour le panneau et ses enfants
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @return true si le clic a été géré, false sinon
     */
    @Override
    public boolean onClick(float mouseX, float mouseY) {
        if (!visible || !enabled) {
            return false;
        }
        
        // Vérifier si le clic est à l'intérieur du panneau
        if (!contains(mouseX, mouseY)) {
            return false;
        }
        
        // Propager le clic aux enfants (de l'avant vers l'arrière)
        for (int i = children.size() - 1; i >= 0; i--) {
            UIComponent child = children.get(i);
            if (child.onClick(mouseX, mouseY)) {
                return true;
            }
        }
        
        // Si aucun enfant n'a géré le clic, le panneau le gère
        handleClick();
        return true;
    }
    
    /**
     * Gérer un événement de survol pour le panneau et ses enfants
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     */
    @Override
    public void onHover(float mouseX, float mouseY) {
        if (!visible || !enabled) {
            hovered = false;
            return;
        }
        
        // Vérifier si la souris est à l'intérieur du panneau
        hovered = contains(mouseX, mouseY);
        
        // Propager le survol aux enfants
        for (UIComponent child : children) {
            child.onHover(mouseX, mouseY);
        }
    }
    
    /**
     * Gérer un événement d'appui pour le panneau et ses enfants
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param pressed true si le bouton est appuyé, false s'il est relâché
     */
    @Override
    public void onPress(float mouseX, float mouseY, boolean pressed) {
        if (!visible || !enabled) {
            this.pressed = false;
            return;
        }
        
        // Vérifier si la souris est à l'intérieur du panneau
        if (contains(mouseX, mouseY)) {
            this.pressed = pressed;
        } else {
            this.pressed = false;
        }
        
        // Propager l'appui aux enfants
        for (UIComponent child : children) {
            child.onPress(mouseX, mouseY, pressed);
        }
    }
    
    /**
     * Ajouter un composant enfant
     * 
     * @param child Composant à ajouter
     */
    public void addChild(UIComponent child) {
        children.add(child);
    }
    
    /**
     * Supprimer un composant enfant
     * 
     * @param child Composant à supprimer
     * @return true si le composant a été supprimé, false sinon
     */
    public boolean removeChild(UIComponent child) {
        return children.remove(child);
    }
    
    /**
     * Supprimer tous les composants enfants
     */
    public void clearChildren() {
        children.clear();
    }
    
    /**
     * Obtenir la liste des composants enfants
     * 
     * @return Liste des composants enfants
     */
    public List<UIComponent> getChildren() {
        return children;
    }
    
    // Getters et setters supplémentaires
    
    public int getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public int getBackgroundTextureId() {
        return backgroundTextureId;
    }
    
    public void setBackgroundTextureId(int backgroundTextureId) {
        this.backgroundTextureId = backgroundTextureId;
    }
    
    public float getAlpha() {
        return alpha;
    }
    
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }
    
    public boolean hasBorder() {
        return hasBorder;
    }
    
    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }
    
    public int getBorderColor() {
        return borderColor;
    }
    
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }
    
    public float getBorderThickness() {
        return borderThickness;
    }
    
    public void setBorderThickness(float borderThickness) {
        this.borderThickness = Math.max(0.0f, borderThickness);
    }
}

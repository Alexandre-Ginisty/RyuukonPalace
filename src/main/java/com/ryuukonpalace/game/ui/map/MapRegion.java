package com.ryuukonpalace.game.ui.map;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Représente une région sur la carte du monde.
 */
public class MapRegion {
    private String id;
    private String name;
    private String texture;
    private Point position;
    private boolean discovered;
    
    // Propriétés pour l'optimisation du rendu
    private Polygon shape;
    private Color color;
    private Color borderColor;
    private Rectangle bounds;
    private boolean visible = true;
    
    /**
     * Constructeur pour une région de carte.
     */
    public MapRegion(String id, String name, String texture, Point position, boolean discoveredByDefault) {
        this.id = id;
        this.name = name;
        this.texture = texture;
        this.position = position;
        this.discovered = discoveredByDefault;
        
        // Valeurs par défaut pour les propriétés de rendu
        this.color = new Color(50, 50, 100, 180);
        this.borderColor = new Color(80, 80, 150);
        this.shape = createDefaultShape();
        this.bounds = shape.getBounds();
    }
    
    /**
     * Crée une forme par défaut pour la région
     */
    private Polygon createDefaultShape() {
        Polygon polygon = new Polygon();
        polygon.addPoint(position.x, position.y);
        polygon.addPoint(position.x + 100, position.y);
        polygon.addPoint(position.x + 100, position.y + 100);
        polygon.addPoint(position.x, position.y + 100);
        return polygon;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getTexture() {
        return texture;
    }
    
    public Point getPosition() {
        return position;
    }
    
    public boolean isDiscovered() {
        return discovered;
    }
    
    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }
    
    /**
     * Obtenir la forme de la région
     */
    public Polygon getShape() {
        return shape;
    }
    
    /**
     * Définir la forme de la région
     */
    public void setShape(Polygon shape) {
        this.shape = shape;
        this.bounds = shape.getBounds();
    }
    
    /**
     * Obtenir la couleur de la région
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Définir la couleur de la région
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Obtenir la couleur de bordure de la région
     */
    public Color getBorderColor() {
        return borderColor;
    }
    
    /**
     * Définir la couleur de bordure de la région
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }
    
    /**
     * Obtenir les limites de la région
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Vérifier si la région est visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Définir la visibilité de la région
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Vérifier si la région est dans la vue actuelle
     */
    public boolean isInView(Rectangle viewBounds) {
        return bounds.intersects(viewBounds);
    }
}

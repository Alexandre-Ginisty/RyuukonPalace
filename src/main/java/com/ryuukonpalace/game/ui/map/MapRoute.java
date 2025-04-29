package com.ryuukonpalace.game.ui.map;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * Représente une route entre deux emplacements sur la carte du monde.
 */
public class MapRoute {
    private String id;
    private String name;
    private MapLocation startLocation;
    private MapLocation endLocation;
    private String type;
    private boolean discovered;
    
    // Propriétés pour l'optimisation du rendu
    private int[] xPoints;
    private int[] yPoints;
    private int pointCount;
    private Color color;
    private float width;
    private Rectangle bounds;
    private boolean visible = true;
    
    /**
     * Constructeur pour une route de carte.
     */
    public MapRoute(String id, String name, MapLocation startLocation, MapLocation endLocation, 
                   String type, boolean discoveredByDefault) {
        this.id = id;
        this.name = name;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.type = type;
        this.discovered = discoveredByDefault;
        
        // Initialisation des propriétés pour le rendu optimisé
        initializeRenderProperties();
    }
    
    /**
     * Initialise les propriétés de rendu de la route
     */
    private void initializeRenderProperties() {
        // Créer un chemin simple entre les deux emplacements
        xPoints = new int[] { startLocation.getPosition().x, endLocation.getPosition().x };
        yPoints = new int[] { startLocation.getPosition().y, endLocation.getPosition().y };
        pointCount = 2;
        
        // Déterminer la couleur et l'épaisseur en fonction du type
        if ("main".equals(type)) {
            color = new Color(200, 150, 100);
            width = 4.0f;
        } else if ("secondary".equals(type)) {
            color = new Color(150, 120, 80);
            width = 2.5f;
        } else {
            color = new Color(120, 100, 60);
            width = 1.5f;
        }
        
        // Calculer les limites
        int minX = Math.min(xPoints[0], xPoints[1]);
        int minY = Math.min(yPoints[0], yPoints[1]);
        int maxX = Math.max(xPoints[0], xPoints[1]);
        int maxY = Math.max(yPoints[0], yPoints[1]);
        
        bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public MapLocation getStartLocation() {
        return startLocation;
    }
    
    public MapLocation getEndLocation() {
        return endLocation;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isDiscovered() {
        return discovered;
    }
    
    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }
    
    /**
     * Obtenir les points X du chemin
     */
    public int[] getXPoints() {
        return xPoints;
    }
    
    /**
     * Obtenir les points Y du chemin
     */
    public int[] getYPoints() {
        return yPoints;
    }
    
    /**
     * Obtenir le nombre de points dans le chemin
     */
    public int getPointCount() {
        return pointCount;
    }
    
    /**
     * Définir les points du chemin
     */
    public void setPoints(int[] xPoints, int[] yPoints, int pointCount) {
        if (xPoints.length != yPoints.length || pointCount > xPoints.length) {
            throw new IllegalArgumentException("Les tableaux de points doivent avoir la même taille");
        }
        
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.pointCount = pointCount;
        
        // Recalculer les limites
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for (int i = 0; i < pointCount; i++) {
            minX = Math.min(minX, xPoints[i]);
            minY = Math.min(minY, yPoints[i]);
            maxX = Math.max(maxX, xPoints[i]);
            maxY = Math.max(maxY, yPoints[i]);
        }
        
        bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
    
    /**
     * Obtenir la couleur de la route
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Définir la couleur de la route
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Obtenir l'épaisseur de la route
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * Définir l'épaisseur de la route
     */
    public void setWidth(float width) {
        this.width = width;
    }
    
    /**
     * Obtenir les limites de la route
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Vérifier si la route est visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Définir la visibilité de la route
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Vérifier si la route est dans la vue actuelle
     */
    public boolean isInView(Rectangle viewBounds) {
        return bounds.intersects(viewBounds);
    }
}

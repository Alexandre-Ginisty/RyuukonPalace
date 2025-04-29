package com.ryuukonpalace.game.ui.map;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Représente un emplacement sur la carte du monde.
 */
public class MapLocation {
    private String id;
    private String name;
    private String type;
    private String icon;
    private Point position;
    private String regionId;
    private boolean discovered;
    private boolean fastTravelEnabled;
    private String description;
    
    // Propriétés pour l'optimisation du rendu
    private Color color;
    private int iconSize;
    private Rectangle bounds;
    private boolean visible = true;
    
    /**
     * Constructeur pour un emplacement de carte.
     */
    public MapLocation(String id, String name, String type, String icon, Point position, 
                      String regionId, boolean discoveredByDefault, boolean fastTravelEnabled, 
                      String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.position = position;
        this.regionId = regionId;
        this.discovered = discoveredByDefault;
        this.fastTravelEnabled = fastTravelEnabled;
        this.description = description;
        
        // Initialisation des propriétés pour le rendu optimisé
        initializeRenderProperties();
    }
    
    /**
     * Initialise les propriétés de rendu de l'emplacement
     */
    private void initializeRenderProperties() {
        // Déterminer la couleur et la taille en fonction du type
        if ("city".equals(type)) {
            color = new Color(220, 220, 100);
            iconSize = 12;
        } else if ("village".equals(type)) {
            color = new Color(180, 180, 80);
            iconSize = 8;
        } else if ("dungeon".equals(type)) {
            color = new Color(150, 50, 50);
            iconSize = 10;
        } else if ("point_of_interest".equals(type)) {
            color = new Color(100, 150, 200);
            iconSize = 6;
        } else {
            color = new Color(150, 150, 150);
            iconSize = 6;
        }
        
        // Calculer les limites (zone d'influence de l'emplacement)
        int boundSize = iconSize * 2 + 40; // Taille supplémentaire pour le texte
        bounds = new Rectangle(position.x - boundSize/2, position.y - boundSize/2, boundSize, boundSize);
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public Point getPosition() {
        return position;
    }
    
    public String getRegionId() {
        return regionId;
    }
    
    public boolean isDiscovered() {
        return discovered;
    }
    
    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }
    
    public boolean isFastTravelEnabled() {
        return fastTravelEnabled;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtenir la couleur de l'emplacement
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Définir la couleur de l'emplacement
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Obtenir la taille de l'icône
     */
    public int getIconSize() {
        return iconSize;
    }
    
    /**
     * Définir la taille de l'icône
     */
    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        
        // Mettre à jour les limites
        int boundSize = iconSize * 2 + 40;
        bounds = new Rectangle(position.x - boundSize/2, position.y - boundSize/2, boundSize, boundSize);
    }
    
    /**
     * Obtenir les limites de l'emplacement
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Obtenir la position X de l'emplacement
     */
    public int getX() {
        return position.x;
    }
    
    /**
     * Obtenir la position Y de l'emplacement
     */
    public int getY() {
        return position.y;
    }
    
    /**
     * Vérifier si l'emplacement est visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Définir la visibilité de l'emplacement
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Vérifier si l'emplacement est dans la vue actuelle
     */
    public boolean isInView(Rectangle viewBounds) {
        return bounds.intersects(viewBounds);
    }
}

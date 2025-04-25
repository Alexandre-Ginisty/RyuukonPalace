package com.ryuukonpalace.game.ui.map;

import java.awt.Point;

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
}

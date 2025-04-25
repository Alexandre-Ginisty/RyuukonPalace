package com.ryuukonpalace.game.ui.map;

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
}

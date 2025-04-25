package com.ryuukonpalace.game.ui.map;

import java.awt.Point;

/**
 * Représente une région sur la carte du monde.
 */
public class MapRegion {
    private String id;
    private String name;
    private String texture;
    private Point position;
    private boolean discovered;
    
    /**
     * Constructeur pour une région de carte.
     */
    public MapRegion(String id, String name, String texture, Point position, boolean discoveredByDefault) {
        this.id = id;
        this.name = name;
        this.texture = texture;
        this.position = position;
        this.discovered = discoveredByDefault;
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
}

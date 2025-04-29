package com.ryuukonpalace.game.ui.map;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 * Classe qui gère l'état de rendu de la carte du monde.
 * Permet de suivre les changements et d'optimiser les mises à jour.
 */
public class MapRenderingState {
    
    // État du joueur
    private Point playerPosition;
    
    // État de la caméra
    private float zoom;
    private Point mapOffset;
    
    // État du brouillard de guerre
    private boolean fogOfWarEnabled;
    private int fogRevealRadius;
    private List<Point> discoveredAreas;
    
    // État des régions, routes et emplacements
    private List<MapRegion> visibleRegions;
    private List<MapRoute> visibleRoutes;
    private List<MapLocation> visibleLocations;
    
    // Limites de la vue
    private Rectangle viewBounds;
    
    // Flags de mise à jour
    private boolean playerMoved = false;
    private boolean cameraMoved = false;
    private boolean zoomChanged = false;
    private boolean fogOfWarChanged = false;
    private boolean visibilityChanged = false;
    
    /**
     * Constructeur par défaut.
     */
    public MapRenderingState() {
        playerPosition = new Point(0, 0);
        zoom = 1.0f;
        mapOffset = new Point(0, 0);
        fogOfWarEnabled = true;
        fogRevealRadius = 100;
        viewBounds = new Rectangle(0, 0, 800, 600);
    }
    
    /**
     * Met à jour la position du joueur.
     */
    public void updatePlayerPosition(Point newPosition) {
        if (!playerPosition.equals(newPosition)) {
            playerPosition = newPosition;
            playerMoved = true;
        }
    }
    
    /**
     * Met à jour le zoom de la caméra.
     */
    public void updateZoom(float newZoom) {
        if (zoom != newZoom) {
            zoom = newZoom;
            zoomChanged = true;
            updateViewBounds();
        }
    }
    
    /**
     * Met à jour la position de la caméra.
     */
    public void updateMapOffset(Point newOffset) {
        if (!mapOffset.equals(newOffset)) {
            mapOffset = newOffset;
            cameraMoved = true;
            updateViewBounds();
        }
    }
    
    /**
     * Met à jour les paramètres du brouillard de guerre.
     */
    public void updateFogOfWar(boolean enabled, int radius, List<Point> areas) {
        boolean changed = fogOfWarEnabled != enabled || fogRevealRadius != radius;
        
        fogOfWarEnabled = enabled;
        fogRevealRadius = radius;
        
        if (discoveredAreas == null || discoveredAreas.size() != areas.size()) {
            changed = true;
        } else {
            for (int i = 0; i < discoveredAreas.size(); i++) {
                if (!discoveredAreas.get(i).equals(areas.get(i))) {
                    changed = true;
                    break;
                }
            }
        }
        
        discoveredAreas = areas;
        
        if (changed) {
            fogOfWarChanged = true;
        }
    }
    
    /**
     * Met à jour les limites de la vue actuelle.
     */
    private void updateViewBounds() {
        int viewX = (int)(-mapOffset.x / zoom);
        int viewY = (int)(-mapOffset.y / zoom);
        int viewWidth = (int)(viewBounds.width / zoom);
        int viewHeight = (int)(viewBounds.height / zoom);
        viewBounds = new Rectangle(viewX, viewY, viewWidth, viewHeight);
    }
    
    /**
     * Met à jour les éléments visibles de la carte.
     */
    public void updateVisibleElements(List<MapRegion> regions, List<MapRoute> routes, List<MapLocation> locations) {
        // Filtrer les régions visibles
        List<MapRegion> newVisibleRegions = regions.stream()
            .filter(region -> region.isInView(viewBounds) && (region.isDiscovered() || !fogOfWarEnabled))
            .toList();
        
        // Filtrer les routes visibles
        List<MapRoute> newVisibleRoutes = routes.stream()
            .filter(route -> route.isInView(viewBounds) && (route.isDiscovered() || !fogOfWarEnabled))
            .toList();
        
        // Filtrer les emplacements visibles
        List<MapLocation> newVisibleLocations = locations.stream()
            .filter(location -> location.isInView(viewBounds) && (location.isDiscovered() || !fogOfWarEnabled))
            .toList();
        
        // Vérifier si la visibilité a changé
        if (visibleRegions == null || visibleRegions.size() != newVisibleRegions.size() ||
            visibleRoutes == null || visibleRoutes.size() != newVisibleRoutes.size() ||
            visibleLocations == null || visibleLocations.size() != newVisibleLocations.size()) {
            visibilityChanged = true;
        } else {
            // Vérifier si les éléments visibles ont changé
            for (int i = 0; i < visibleRegions.size(); i++) {
                if (!visibleRegions.get(i).equals(newVisibleRegions.get(i))) {
                    visibilityChanged = true;
                    break;
                }
            }
            
            if (!visibilityChanged) {
                for (int i = 0; i < visibleRoutes.size(); i++) {
                    if (!visibleRoutes.get(i).equals(newVisibleRoutes.get(i))) {
                        visibilityChanged = true;
                        break;
                    }
                }
            }
            
            if (!visibilityChanged) {
                for (int i = 0; i < visibleLocations.size(); i++) {
                    if (!visibleLocations.get(i).equals(newVisibleLocations.get(i))) {
                        visibilityChanged = true;
                        break;
                    }
                }
            }
        }
        
        visibleRegions = newVisibleRegions;
        visibleRoutes = newVisibleRoutes;
        visibleLocations = newVisibleLocations;
    }
    
    /**
     * Vérifie si la carte doit être mise à jour.
     */
    public boolean needsUpdate() {
        return playerMoved || cameraMoved || zoomChanged || fogOfWarChanged || visibilityChanged;
    }
    
    /**
     * Réinitialise les flags de mise à jour.
     */
    public void resetUpdateFlags() {
        playerMoved = false;
        cameraMoved = false;
        zoomChanged = false;
        fogOfWarChanged = false;
        visibilityChanged = false;
    }
    
    // Getters
    
    public Point getPlayerPosition() {
        return playerPosition;
    }
    
    public float getZoom() {
        return zoom;
    }
    
    public Point getMapOffset() {
        return mapOffset;
    }
    
    public boolean isFogOfWarEnabled() {
        return fogOfWarEnabled;
    }
    
    public int getFogRevealRadius() {
        return fogRevealRadius;
    }
    
    public List<Point> getDiscoveredAreas() {
        return discoveredAreas;
    }
    
    public Rectangle getViewBounds() {
        return viewBounds;
    }
    
    public List<MapRegion> getVisibleRegions() {
        return visibleRegions;
    }
    
    public List<MapRoute> getVisibleRoutes() {
        return visibleRoutes;
    }
    
    public List<MapLocation> getVisibleLocations() {
        return visibleLocations;
    }
    
    public boolean isPlayerMoved() {
        return playerMoved;
    }
    
    public boolean isCameraMoved() {
        return cameraMoved;
    }
    
    public boolean isZoomChanged() {
        return zoomChanged;
    }
    
    public boolean isFogOfWarChanged() {
        return fogOfWarChanged;
    }
    
    public boolean isVisibilityChanged() {
        return visibilityChanged;
    }
}

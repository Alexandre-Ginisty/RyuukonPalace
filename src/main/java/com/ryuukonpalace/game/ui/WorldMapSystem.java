package com.ryuukonpalace.game.ui;

import com.ryuukonpalace.game.ui.map.MapLocation;
import com.ryuukonpalace.game.ui.map.MapRegion;
import com.ryuukonpalace.game.ui.map.MapRoute;
import com.ryuukonpalace.game.utils.JsonLoader;
import org.json.JSONObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Système de carte du monde et mini-carte pour Ryuukon Palace.
 * Gère l'affichage de la carte du monde, les régions, les emplacements, les routes et la mini-carte.
 */
public class WorldMapSystem {
    private static final String CONFIG_PATH = "data/ui_world_map.json";
    
    private JSONObject config;
    private boolean isFullMapVisible;
    private boolean isMiniMapVisible;
    private Point playerPosition;
    private float zoom;
    private Point mapOffset;
    private List<MapRegion> regions;
    private List<MapLocation> locations;
    private List<MapRoute> routes;
    private Map<String, MapRegion> regionMap;
    private Map<String, MapLocation> locationMap;
    private Rectangle fullMapBounds;
    private Rectangle miniMapBounds;
    private boolean fogOfWarEnabled;
    private int fogRevealRadius;
    private List<Point> discoveredAreas;
    
    /**
     * Constructeur pour le système de carte du monde.
     */
    public WorldMapSystem() {
        this.regions = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.regionMap = new HashMap<>();
        this.locationMap = new HashMap<>();
        this.discoveredAreas = new ArrayList<>();
        this.isFullMapVisible = false;
        this.isMiniMapVisible = true;
        this.playerPosition = new Point(0, 0);
        this.zoom = 1.0f;
        this.mapOffset = new Point(0, 0);
        loadConfiguration();
    }
    
    /**
     * Charge la configuration depuis le fichier JSON.
     */
    private void loadConfiguration() {
        try {
            this.config = JsonLoader.loadJsonObject(CONFIG_PATH);
            JSONObject mapSystemConfig = config.getJSONObject("worldMapSystem");
            
            // Configurer la carte complète
            JSONObject fullMapConfig = mapSystemConfig.getJSONObject("fullMap");
            JSONObject dimensionsConfig = fullMapConfig.getJSONObject("dimensions");
            
            int width = dimensionsConfig.getInt("width");
            int height = dimensionsConfig.getInt("height");
            
            this.fullMapBounds = new Rectangle(0, 0, width, height);
            this.zoom = (float) fullMapConfig.getDouble("initialZoom");
            
            // Configurer la mini-carte
            if (mapSystemConfig.getJSONObject("miniMap").getBoolean("enabled")) {
                JSONObject miniMapConfig = mapSystemConfig.getJSONObject("miniMap");
                JSONObject positionConfig = miniMapConfig.getJSONObject("position");
                
                int screenWidth = 1280; // Largeur d'écran supposée
                int screenHeight = 720; // Hauteur d'écran supposée
                
                int x = parsePosition(positionConfig.getString("x"), screenWidth);
                int y = parsePosition(positionConfig.getString("y"), screenHeight);
                int w = parsePosition(positionConfig.getString("width"), screenWidth);
                int h = parsePosition(positionConfig.getString("height"), screenHeight);
                
                this.miniMapBounds = new Rectangle(x, y, w, h);
            }
            
            // Configurer le brouillard de guerre
            JSONObject fogConfig = fullMapConfig.getJSONObject("fogOfWar");
            this.fogOfWarEnabled = fogConfig.getBoolean("enabled");
            this.fogRevealRadius = fogConfig.getInt("revealRadius");
            
            // Charger les régions
            loadRegions(fullMapConfig.getJSONArray("regions"));
            
            // Charger les emplacements
            loadLocations(fullMapConfig.getJSONArray("locations"));
            
            // Charger les routes
            loadRoutes(fullMapConfig.getJSONArray("routes"));
            
            System.out.println("Configuration de la carte du monde chargée avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la configuration de la carte du monde: " + e.getMessage());
            e.printStackTrace();
            
            // Valeurs par défaut en cas d'erreur
            this.fullMapBounds = new Rectangle(0, 0, 1920, 1080);
            this.miniMapBounds = new Rectangle(1024, 100, 200, 200);
        }
    }
    
    /**
     * Parse une position qui peut être en pourcentage ou en pixels.
     */
    private int parsePosition(String position, int dimension) {
        if (position.endsWith("%")) {
            float percentage = Float.parseFloat(position.substring(0, position.length() - 1)) / 100f;
            return (int)(dimension * percentage);
        } else {
            return Integer.parseInt(position);
        }
    }
    
    /**
     * Charge les régions depuis la configuration.
     */
    private void loadRegions(org.json.JSONArray regionsConfig) {
        for (int i = 0; i < regionsConfig.length(); i++) {
            JSONObject regionConfig = regionsConfig.getJSONObject(i);
            
            String id = regionConfig.getString("id");
            String name = regionConfig.getString("name");
            String texture = regionConfig.getString("texture");
            JSONObject positionConfig = regionConfig.getJSONObject("position");
            int x = positionConfig.getInt("x");
            int y = positionConfig.getInt("y");
            boolean discoveredByDefault = regionConfig.getBoolean("discoveredByDefault");
            
            MapRegion region = new MapRegion(id, name, texture, new Point(x, y), discoveredByDefault);
            regions.add(region);
            regionMap.put(id, region);
        }
    }
    
    /**
     * Charge les emplacements depuis la configuration.
     */
    private void loadLocations(org.json.JSONArray locationsConfig) {
        for (int i = 0; i < locationsConfig.length(); i++) {
            JSONObject locationConfig = locationsConfig.getJSONObject(i);
            
            String id = locationConfig.getString("id");
            String name = locationConfig.getString("name");
            String type = locationConfig.getString("type");
            String icon = locationConfig.getString("icon");
            JSONObject positionConfig = locationConfig.getJSONObject("position");
            int x = positionConfig.getInt("x");
            int y = positionConfig.getInt("y");
            String regionId = locationConfig.getString("regionId");
            boolean discoveredByDefault = locationConfig.getBoolean("discoveredByDefault");
            boolean fastTravelEnabled = locationConfig.getBoolean("fastTravelEnabled");
            String description = locationConfig.getString("description");
            
            MapLocation location = new MapLocation(id, name, type, icon, new Point(x, y), 
                                                  regionId, discoveredByDefault, fastTravelEnabled, description);
            locations.add(location);
            locationMap.put(id, location);
        }
    }
    
    /**
     * Charge les routes depuis la configuration.
     */
    private void loadRoutes(org.json.JSONArray routesConfig) {
        for (int i = 0; i < routesConfig.length(); i++) {
            JSONObject routeConfig = routesConfig.getJSONObject(i);
            
            String id = routeConfig.getString("id");
            String name = routeConfig.getString("name");
            String start = routeConfig.getString("start");
            String end = routeConfig.getString("end");
            String type = routeConfig.getString("type");
            boolean discoveredByDefault = routeConfig.getBoolean("discoveredByDefault");
            
            MapLocation startLocation = locationMap.get(start);
            MapLocation endLocation = locationMap.get(end);
            
            if (startLocation != null && endLocation != null) {
                MapRoute route = new MapRoute(id, name, startLocation, endLocation, type, discoveredByDefault);
                routes.add(route);
            }
        }
    }
    
    /**
     * Affiche la carte du monde complète.
     */
    public void showFullMap() {
        this.isFullMapVisible = true;
        System.out.println("Carte du monde affichée");
    }
    
    /**
     * Ferme la carte du monde complète.
     */
    public void hideFullMap() {
        this.isFullMapVisible = false;
        System.out.println("Carte du monde masquée");
    }
    
    /**
     * Définit la position du joueur sur la carte.
     */
    public void setPlayerPosition(Point position) {
        this.playerPosition = position;
        
        // Découvrir la zone autour du joueur
        if (fogOfWarEnabled) {
            discoveredAreas.add(new Point(position));
        }
    }
    
    /**
     * Zoom sur la carte.
     */
    public void zoom(float factor) {
        float newZoom = zoom * factor;
        JSONObject fullMapConfig = config.getJSONObject("worldMapSystem").getJSONObject("fullMap");
        float minZoom = (float) fullMapConfig.getDouble("minZoom");
        float maxZoom = (float) fullMapConfig.getDouble("maxZoom");
        
        if (newZoom >= minZoom && newZoom <= maxZoom) {
            zoom = newZoom;
            System.out.println("Zoom ajusté à: " + zoom);
        }
    }
    
    /**
     * Déplace la vue de la carte.
     */
    public void pan(int deltaX, int deltaY) {
        if (!isFullMapVisible) return;
        
        mapOffset.x += deltaX;
        mapOffset.y += deltaY;
        
        // Limiter le déplacement pour ne pas sortir de la carte
        int maxOffsetX = (int)(fullMapBounds.width * (1 - zoom));
        int maxOffsetY = (int)(fullMapBounds.height * (1 - zoom));
        
        mapOffset.x = Math.max(Math.min(mapOffset.x, 0), -maxOffsetX);
        mapOffset.y = Math.max(Math.min(mapOffset.y, 0), -maxOffsetY);
    }
    
    /**
     * Sélectionne un emplacement sur la carte.
     */
    public void selectLocation(MapLocation location) {
        if (location == null) return;
        
        System.out.println("Emplacement sélectionné: " + location.getName());
        System.out.println("Description: " + location.getDescription());
        
        // Ici, on pourrait afficher une fenêtre d'information sur l'emplacement
        // ou proposer des options comme le voyage rapide si disponible
    }
    
    /**
     * Vérifie si une position est dans la portée de la mini-carte.
     */
    private boolean isInMiniMapRange(Point position) {
        int visibilityRadius = 300; // Rayon de visibilité de la mini-carte
        
        int dx = position.x - playerPosition.x;
        int dy = position.y - playerPosition.y;
        int distanceSquared = dx * dx + dy * dy;
        
        return distanceSquared <= visibilityRadius * visibilityRadius;
    }
    
    /**
     * Vérifie les clics sur les contrôles de la mini-carte.
     */
    private void checkMiniMapControlClicks(int x, int y) {
        JSONObject systemConfig = config.getJSONObject("worldMapSystem");
        JSONObject miniMapConfig = systemConfig.getJSONObject("miniMap");
        JSONObject controlsConfig = miniMapConfig.getJSONObject("controls");
        
        // Vérifier le bouton d'agrandissement
        if (controlsConfig.getJSONObject("expandButton").getBoolean("enabled")) {
            JSONObject expandButtonConfig = controlsConfig.getJSONObject("expandButton");
            JSONObject positionConfig = expandButtonConfig.getJSONObject("position");
            
            int buttonX = miniMapBounds.x + parsePosition(positionConfig.getString("x"), miniMapBounds.width);
            int buttonY = miniMapBounds.y + parsePosition(positionConfig.getString("y"), miniMapBounds.height);
            int size = expandButtonConfig.getInt("size");
            
            Rectangle expandButton = new Rectangle(buttonX - size / 2, buttonY - size / 2, size, size);
            if (expandButton.contains(x, y)) {
                showFullMap();
                return;
            }
        }
    }
    
    /**
     * Gère les clics de souris pour l'interaction avec la carte.
     */
    public void handleMouseClick(int x, int y) {
        // Gérer les clics sur la carte complète
        if (isFullMapVisible) {
            // Vérifier les clics sur les contrôles
            checkMapControlClicks(x, y);
            
            // Vérifier les clics sur les emplacements
            checkLocationClicks(x, y);
        }
        
        // Gérer les clics sur la mini-carte
        if (isMiniMapVisible && !isFullMapVisible) {
            // Vérifier les clics sur le bouton d'agrandissement
            checkMiniMapControlClicks(x, y);
        }
    }
    
    /**
     * Vérifie les clics sur les contrôles de la carte complète.
     */
    private void checkMapControlClicks(int x, int y) {
        JSONObject systemConfig = config.getJSONObject("worldMapSystem");
        JSONObject fullMapConfig = systemConfig.getJSONObject("fullMap");
        JSONObject controlsConfig = fullMapConfig.getJSONObject("controls");
        
        // Vérifier le bouton de zoom +
        if (controlsConfig.getJSONObject("zoomButtons").getBoolean("enabled")) {
            JSONObject zoomButtonsConfig = controlsConfig.getJSONObject("zoomButtons");
            JSONObject positionConfig = zoomButtonsConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int buttonX = parsePosition(positionConfig.getString("x"), screenWidth);
            int buttonY = parsePosition(positionConfig.getString("y"), screenHeight);
            
            // Bouton zoom +
            Rectangle zoomInButton = new Rectangle(buttonX - 20, buttonY - 30, 40, 30);
            if (zoomInButton.contains(x, y)) {
                zoom(1.2f);
                return;
            }
            
            // Bouton zoom -
            Rectangle zoomOutButton = new Rectangle(buttonX - 20, buttonY + 10, 40, 30);
            if (zoomOutButton.contains(x, y)) {
                zoom(0.8f);
                return;
            }
        }
        
        // Vérifier le bouton de fermeture
        if (controlsConfig.getJSONObject("closeButton").getBoolean("enabled")) {
            JSONObject closeButtonConfig = controlsConfig.getJSONObject("closeButton");
            JSONObject positionConfig = closeButtonConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int buttonX = parsePosition(positionConfig.getString("x"), screenWidth);
            int buttonY = parsePosition(positionConfig.getString("y"), screenHeight);
            
            Rectangle closeButton = new Rectangle(buttonX - 20, buttonY - 20, 40, 40);
            if (closeButton.contains(x, y)) {
                hideFullMap();
                return;
            }
        }
    }
    
    /**
     * Vérifie les clics sur les emplacements de la carte.
     */
    private void checkLocationClicks(int x, int y) {
        // Convertir les coordonnées de clic en coordonnées de carte
        int mapX = (int)((x - mapOffset.x) / zoom);
        int mapY = (int)((y - mapOffset.y) / zoom);
        
        for (MapLocation location : locations) {
            if (!location.isDiscovered() && fogOfWarEnabled) continue;
            
            int locX = location.getPosition().x;
            int locY = location.getPosition().y;
            int clickRadius = 20;
            
            if (Math.abs(mapX - locX) <= clickRadius && Math.abs(mapY - locY) <= clickRadius) {
                selectLocation(location);
                return;
            }
        }
    }
}

package com.ryuukonpalace.game.ui.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

/**
 * Classe responsable du rendu de la carte du monde et de la mini-carte.
 * Optimisée pour de meilleures performances avec mise en cache et rendu sélectif.
 */
public class WorldMapRenderer {
    private JSONObject config;
    private Rectangle fullMapBounds;
    private Rectangle miniMapBounds;
    private Point playerPosition;
    private float zoom;
    private Point mapOffset;
    private boolean fogOfWarEnabled;
    private int fogRevealRadius;
    private List<Point> discoveredAreas;
    
    // Système de mise en cache
    // Note: Ces champs sont conservés pour une utilisation future dans le système de rendu 2.5D
    protected Map<String, BufferedImage> tileCache;
    protected BufferedImage mapBuffer;
    protected BufferedImage miniMapBuffer;
    protected BufferedImage fogOfWarBuffer;
    protected BufferedImage mapControlsBuffer;
    protected BufferedImage miniMapControlsBuffer;
    
    // Flags pour le rafraîchissement des caches
    // Note: Ces flags seront utilisés lors de l'implémentation complète du système de rendu par couches
    protected boolean mapNeedsRefresh = true;
    protected boolean miniMapNeedsRefresh = true;
    protected boolean fogOfWarNeedsRefresh = true;
    protected boolean mapControlsNeedRefresh = true;
    protected boolean miniMapControlsNeedRefresh = true;
    
    // Paramètres de tuiles
    protected final int tileSize = 256; // Taille des tuiles en pixels
    
    // Suivi des performances
    private long lastRenderTime = 0;
    protected long totalRenderTime = 0;
    protected int frameCount = 0;
    
    // Multithreading pour le chargement des ressources
    protected ExecutorService mapLoadingExecutor;
    protected AtomicBoolean resourcesLoaded = new AtomicBoolean(false);
    
    /**
     * Constructeur pour le renderer de carte du monde.
     */
    public WorldMapRenderer(JSONObject config, Rectangle fullMapBounds, Rectangle miniMapBounds) {
        this.config = config;
        this.fullMapBounds = fullMapBounds;
        this.miniMapBounds = miniMapBounds;
        this.playerPosition = new Point(0, 0);
        this.zoom = 1.0f;
        this.mapOffset = new Point(0, 0);
        
        // Initialisation du système de mise en cache
        tileCache = new HashMap<>();
        mapBuffer = new BufferedImage(fullMapBounds.width, fullMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        miniMapBuffer = new BufferedImage(miniMapBounds.width, miniMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        fogOfWarBuffer = new BufferedImage(fullMapBounds.width, fullMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        mapControlsBuffer = new BufferedImage(fullMapBounds.width, fullMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        miniMapControlsBuffer = new BufferedImage(miniMapBounds.width, miniMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        
        // Initialisation du suivi des performances
        lastRenderTime = System.currentTimeMillis();
        
        // Initialisation du multithreading pour le chargement des ressources
        mapLoadingExecutor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * Définit les paramètres de rendu.
     */
    public void setRenderParams(Point playerPosition, float zoom, Point mapOffset, 
                               boolean fogOfWarEnabled, int fogRevealRadius, List<Point> discoveredAreas) {
        this.playerPosition = playerPosition;
        this.zoom = zoom;
        this.mapOffset = mapOffset;
        this.fogOfWarEnabled = fogOfWarEnabled;
        this.fogRevealRadius = fogRevealRadius;
        this.discoveredAreas = discoveredAreas;
        
        // Mettre à jour les flags de rafraîchissement des caches
        mapNeedsRefresh = true;
        miniMapNeedsRefresh = true;
        fogOfWarNeedsRefresh = true;
        mapControlsNeedRefresh = true;
        miniMapControlsNeedRefresh = true;
    }
    
    /**
     * Dessine la carte complète.
     */
    public void renderFullMap(Graphics2D g, List<MapRegion> regions, List<MapRoute> routes, List<MapLocation> locations) {
        // Sauvegarder la transformation actuelle
        // Appliquer le zoom et le déplacement
        g.translate(mapOffset.x, mapOffset.y);
        g.scale(zoom, zoom);
        
        // Dessiner le fond de la carte
        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, 0, fullMapBounds.width, fullMapBounds.height);
        
        // Dessiner le brouillard de guerre si activé
        if (fogOfWarEnabled) {
            renderFogOfWar(g);
        }
        
        // Dessiner les régions
        for (MapRegion region : regions) {
            if (region.isDiscovered() || !fogOfWarEnabled) {
                renderRegion(g, region);
            }
        }
        
        // Dessiner les routes
        for (MapRoute route : routes) {
            if (route.isDiscovered() || !fogOfWarEnabled) {
                renderRoute(g, route);
            }
        }
        
        // Dessiner les emplacements
        for (MapLocation location : locations) {
            if (location.isDiscovered() || !fogOfWarEnabled) {
                renderLocation(g, location);
            }
        }
        
        // Dessiner le joueur
        renderPlayer(g, playerPosition.x, playerPosition.y, true);
        
        // Dessiner les contrôles de la carte
        renderMapControls(g);
        
        // Restaurer la transformation
        g.translate(-mapOffset.x, -mapOffset.y);
        g.scale(1.0f / zoom, 1.0f / zoom);
        
        // Mettre à jour le suivi des performances
        long currentTime = System.currentTimeMillis();
        totalRenderTime += currentTime - lastRenderTime;
        lastRenderTime = currentTime;
        frameCount++;
    }
    
    /**
     * Dessine la mini-carte.
     */
    public void renderMiniMap(Graphics2D g, List<MapRegion> regions, List<MapRoute> routes, List<MapLocation> locations) {
        // Dessiner le fond de la mini-carte
        g.setColor(new Color(26, 26, 26, 179));
        g.fillRoundRect(miniMapBounds.x, miniMapBounds.y, miniMapBounds.width, miniMapBounds.height, 10, 10);
        
        // Dessiner la bordure
        g.setColor(new Color(58, 46, 33));
        g.drawRoundRect(miniMapBounds.x, miniMapBounds.y, miniMapBounds.width, miniMapBounds.height, 10, 10);
        
        // Calculer l'échelle de la mini-carte
        float miniMapScale = 0.3f; // Échelle de la mini-carte
        
        // Sauvegarder la transformation actuelle
        // Appliquer l'échelle et la position de la mini-carte
        g.translate(miniMapBounds.x + miniMapBounds.width / 2, miniMapBounds.y + miniMapBounds.height / 2);
        g.scale(miniMapScale, miniMapScale);
        g.translate(-playerPosition.x, -playerPosition.y);
        
        // Dessiner les régions visibles dans la mini-carte
        for (MapRegion region : regions) {
            if (isInMiniMapRange(region.getPosition())) {
                renderRegion(g, region);
            }
        }
        
        // Dessiner les routes visibles dans la mini-carte
        for (MapRoute route : routes) {
            if (isInMiniMapRange(route.getStartLocation().getPosition()) || 
                isInMiniMapRange(route.getEndLocation().getPosition())) {
                renderRoute(g, route);
            }
        }
        
        // Dessiner les emplacements visibles dans la mini-carte
        for (MapLocation location : locations) {
            if (isInMiniMapRange(location.getPosition())) {
                renderLocation(g, location);
            }
        }
        
        // Dessiner le joueur au centre de la mini-carte
        renderPlayer(g, playerPosition.x, playerPosition.y, false);
        
        // Restaurer la transformation
        g.translate(playerPosition.x, playerPosition.y);
        g.scale(1.0f / miniMapScale, 1.0f / miniMapScale);
        g.translate(-miniMapBounds.x - miniMapBounds.width / 2, -miniMapBounds.y - miniMapBounds.height / 2);
        
        // Dessiner les contrôles de la mini-carte
        renderMiniMapControls(g);
    }
    
    /**
     * Vérifie si une position est dans la portée de la mini-carte.
     */
    public boolean isInMiniMapRange(Point position) {
        int visibilityRadius = 300; // Rayon de visibilité de la mini-carte
        
        int dx = position.x - playerPosition.x;
        int dy = position.y - playerPosition.y;
        int distanceSquared = dx * dx + dy * dy;
        
        return distanceSquared <= visibilityRadius * visibilityRadius;
    }
    
    /**
     * Dessine le brouillard de guerre.
     */
    public void renderFogOfWar(Graphics2D g) {
        // Dessiner un rectangle noir sur toute la carte
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, fullMapBounds.width, fullMapBounds.height);
        
        // Dessiner des cercles transparents pour les zones découvertes
        // Restaurer le composite par défaut
        g.setColor(new Color(0, 0, 0, 0));
        for (Point discoveredPoint : discoveredAreas) {
            g.fillOval(discoveredPoint.x - fogRevealRadius, discoveredPoint.y - fogRevealRadius, 
                      fogRevealRadius * 2, fogRevealRadius * 2);
        }
        g.setColor(new Color(0, 0, 0, 200));
    }
    
    /**
     * Dessine une région sur la carte.
     */
    public void renderRegion(Graphics2D g, MapRegion region) {
        // Ici, nous chargerions et dessinerions la texture de la région
        // Pour l'instant, dessiner un placeholder
        g.setColor(new Color(40, 40, 40, 150));
        g.fillOval(region.getPosition().x - 100, region.getPosition().y - 100, 200, 200);
        
        g.setColor(new Color(60, 60, 60));
        g.drawOval(region.getPosition().x - 100, region.getPosition().y - 100, 200, 200);
        
        // Dessiner le nom de la région
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(region.getName());
        g.drawString(region.getName(), region.getPosition().x - textWidth / 2, region.getPosition().y);
    }
    
    /**
     * Dessine un emplacement sur la carte.
     */
    public void renderLocation(Graphics2D g, MapLocation location) {
        // Dessiner l'icône de l'emplacement
        int iconSize = 20;
        g.setColor(getLocationColor(location.getType()));
        g.fillRect(location.getPosition().x - iconSize / 2, location.getPosition().y - iconSize / 2, iconSize, iconSize);
        
        g.setColor(Color.WHITE);
        g.drawRect(location.getPosition().x - iconSize / 2, location.getPosition().y - iconSize / 2, iconSize, iconSize);
        
        // Dessiner le nom de l'emplacement
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(location.getName());
        g.drawString(location.getName(), location.getPosition().x - textWidth / 2, location.getPosition().y + iconSize / 2 + 15);
    }
    
    /**
     * Renvoie la couleur associée au type d'emplacement.
     */
    public Color getLocationColor(String type) {
        switch (type) {
            case "CITY": return new Color(255, 255, 150);
            case "VILLAGE": return new Color(150, 255, 150);
            case "LANDMARK": return new Color(150, 150, 255);
            case "DUNGEON": return new Color(255, 100, 100);
            case "FORTRESS": return new Color(200, 100, 100);
            case "RUINS": return new Color(150, 100, 50);
            case "VALLEY": return new Color(100, 200, 100);
            default: return new Color(200, 200, 200);
        }
    }
    
    /**
     * Dessine une route sur la carte.
     */
    public void renderRoute(Graphics2D g, MapRoute route) {
        Point start = route.getStartLocation().getPosition();
        Point end = route.getEndLocation().getPosition();
        
        // Dessiner la ligne de la route
        g.setColor(getRouteColor(route.getType()));
        g.setStroke(new java.awt.BasicStroke(3));
        g.drawLine(start.x, start.y, end.x, end.y);
        
        // Dessiner le nom de la route au milieu
        int midX = (start.x + end.x) / 2;
        int midY = (start.y + end.y) / 2;
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(route.getName());
        g.drawString(route.getName(), midX - textWidth / 2, midY - 5);
    }
    
    /**
     * Renvoie la couleur associée au type de route.
     */
    public Color getRouteColor(String type) {
        switch (type) {
            case "MAIN_ROAD": return new Color(200, 200, 200);
            case "MOUNTAIN_PATH": return new Color(150, 100, 50);
            case "FOREST_PATH": return new Color(100, 150, 100);
            case "DANGEROUS_PATH": return new Color(200, 50, 50);
            case "HIDDEN_PATH": return new Color(100, 100, 150);
            default: return new Color(150, 150, 150);
        }
    }
    
    /**
     * Dessine le joueur sur la carte.
     */
    public void renderPlayer(Graphics2D g, int x, int y, boolean isFullMap) {
        int playerSize = isFullMap ? 15 : 10;
        
        // Animation de pulsation
        double pulseScale = 1.0 + 0.2 * Math.sin(System.currentTimeMillis() / 200.0);
        int pulseSize = (int)(playerSize * pulseScale);
        
        // Dessiner le joueur
        g.setColor(new Color(255, 255, 255));
        g.fillOval(x - pulseSize / 2, y - pulseSize / 2, pulseSize, pulseSize);
        
        g.setColor(new Color(0, 0, 0));
        g.drawOval(x - pulseSize / 2, y - pulseSize / 2, pulseSize, pulseSize);
    }
    
    /**
     * Dessine les contrôles de la carte complète.
     */
    public void renderMapControls(Graphics2D g) {
        JSONObject systemConfig = config.getJSONObject("worldMapSystem");
        JSONObject fullMapConfig = systemConfig.getJSONObject("fullMap");
        JSONObject controlsConfig = fullMapConfig.getJSONObject("controls");
        
        // Dessiner les boutons de zoom
        if (controlsConfig.getJSONObject("zoomButtons").getBoolean("enabled")) {
            JSONObject zoomButtonsConfig = controlsConfig.getJSONObject("zoomButtons");
            JSONObject positionConfig = zoomButtonsConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int x = parsePosition(positionConfig.getString("x"), screenWidth);
            int y = parsePosition(positionConfig.getString("y"), screenHeight);
            
            // Bouton zoom +
            g.setColor(new Color(40, 40, 40, 200));
            g.fillRect(x - 20, y - 30, 40, 30);
            g.setColor(Color.WHITE);
            g.drawRect(x - 20, y - 30, 40, 30);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("+", x - 5, y - 10);
            
            // Bouton zoom -
            g.setColor(new Color(40, 40, 40, 200));
            g.fillRect(x - 20, y + 10, 40, 30);
            g.setColor(Color.WHITE);
            g.drawRect(x - 20, y + 10, 40, 30);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("-", x - 5, y + 30);
        }
        
        // Dessiner le bouton de fermeture
        if (controlsConfig.getJSONObject("closeButton").getBoolean("enabled")) {
            JSONObject closeButtonConfig = controlsConfig.getJSONObject("closeButton");
            JSONObject positionConfig = closeButtonConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int x = parsePosition(positionConfig.getString("x"), screenWidth);
            int y = parsePosition(positionConfig.getString("y"), screenHeight);
            
            g.setColor(new Color(40, 40, 40, 200));
            g.fillRect(x - 20, y - 20, 40, 40);
            g.setColor(Color.WHITE);
            g.drawRect(x - 20, y - 20, 40, 40);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("X", x - 5, y + 5);
        }
    }
    
    /**
     * Dessine les contrôles de la mini-carte.
     */
    public void renderMiniMapControls(Graphics2D g) {
        JSONObject systemConfig = config.getJSONObject("worldMapSystem");
        JSONObject miniMapConfig = systemConfig.getJSONObject("miniMap");
        JSONObject controlsConfig = miniMapConfig.getJSONObject("controls");
        
        // Dessiner le bouton d'agrandissement
        if (controlsConfig.getJSONObject("expandButton").getBoolean("enabled")) {
            JSONObject expandButtonConfig = controlsConfig.getJSONObject("expandButton");
            JSONObject positionConfig = expandButtonConfig.getJSONObject("position");
            
            int x = miniMapBounds.x + parsePosition(positionConfig.getString("x"), miniMapBounds.width);
            int y = miniMapBounds.y + parsePosition(positionConfig.getString("y"), miniMapBounds.height);
            int size = expandButtonConfig.getInt("size");
            
            g.setColor(new Color(40, 40, 40, 200));
            g.fillRect(x - size / 2, y - size / 2, size, size);
            g.setColor(Color.WHITE);
            g.drawRect(x - size / 2, y - size / 2, size, size);
            
            // Dessiner une icône d'agrandissement
            g.drawLine(x - 5, y, x + 5, y);
            g.drawLine(x, y - 5, x, y + 5);
        }
    }
    
    /**
     * Parse une position qui peut être en pourcentage ou en pixels.
     */
    public int parsePosition(String position, int dimension) {
        if (position.endsWith("%")) {
            float percentage = Float.parseFloat(position.substring(0, position.length() - 1)) / 100f;
            return (int)(dimension * percentage);
        } else {
            return Integer.parseInt(position);
        }
    }
    
    // Getters et setters
    public Point getMapOffset() {
        return mapOffset;
    }
    
    public void setMapOffset(Point mapOffset) {
        this.mapOffset = mapOffset;
    }
    
    public float getZoom() {
        return zoom;
    }
    
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
    
    public boolean isFogOfWarEnabled() {
        return fogOfWarEnabled;
    }
    
    public void setFogOfWarEnabled(boolean fogOfWarEnabled) {
        this.fogOfWarEnabled = fogOfWarEnabled;
    }
    
    public int getFogRevealRadius() {
        return fogRevealRadius;
    }
    
    public void setFogRevealRadius(int fogRevealRadius) {
        this.fogRevealRadius = fogRevealRadius;
    }
    
    public List<Point> getDiscoveredAreas() {
        return discoveredAreas;
    }
    
    public void setDiscoveredAreas(List<Point> discoveredAreas) {
        this.discoveredAreas = discoveredAreas;
    }
    
    public Point getPlayerPosition() {
        return playerPosition;
    }
    
    public void setPlayerPosition(Point playerPosition) {
        this.playerPosition = playerPosition;
    }
    
    public Rectangle getFullMapBounds() {
        return fullMapBounds;
    }
    
    public void setFullMapBounds(Rectangle fullMapBounds) {
        this.fullMapBounds = fullMapBounds;
    }
    
    public Rectangle getMiniMapBounds() {
        return miniMapBounds;
    }
    
    public void setMiniMapBounds(Rectangle miniMapBounds) {
        this.miniMapBounds = miniMapBounds;
    }
}

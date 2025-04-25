package com.ryuukonpalace.game.ui.map;

import com.ryuukonpalace.game.utils.JsonLoader;
import org.json.JSONObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Map;

/**
 * Classe responsable du rendu de la carte du monde et de la mini-carte.
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
    }
    
    /**
     * Dessine la carte complète.
     */
    public void renderFullMap(Graphics2D g, List<MapRegion> regions, List<MapRoute> routes, List<MapLocation> locations) {
        // Sauvegarder la transformation actuelle
        AffineTransform originalTransform = g.getTransform();
        
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
        g.setTransform(originalTransform);
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
        AffineTransform originalTransform = g.getTransform();
        
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
        g.setTransform(originalTransform);
        
        // Dessiner les contrôles de la mini-carte
        renderMiniMapControls(g);
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
     * Dessine le brouillard de guerre.
     */
    private void renderFogOfWar(Graphics2D g) {
        // Dessiner un rectangle noir sur toute la carte
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, fullMapBounds.width, fullMapBounds.height);
        
        // Dessiner des cercles transparents pour les zones découvertes
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        
        for (Point discoveredPoint : discoveredAreas) {
            g.fillOval(discoveredPoint.x - fogRevealRadius, discoveredPoint.y - fogRevealRadius, 
                      fogRevealRadius * 2, fogRevealRadius * 2);
        }
        
        // Restaurer le composite par défaut
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
    
    /**
     * Dessine une région sur la carte.
     */
    private void renderRegion(Graphics2D g, MapRegion region) {
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
    private void renderLocation(Graphics2D g, MapLocation location) {
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
    private Color getLocationColor(String type) {
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
    private void renderRoute(Graphics2D g, MapRoute route) {
        Point start = route.getStartLocation().getPosition();
        Point end = route.getEndLocation().getPosition();
        
        // Dessiner la ligne de la route
        g.setColor(getRouteColor(route.getType()));
        g.setStroke(new BasicStroke(3));
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
    private Color getRouteColor(String type) {
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
    private void renderPlayer(Graphics2D g, int x, int y, boolean isFullMap) {
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
    private void renderMapControls(Graphics2D g) {
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
    private void renderMiniMapControls(Graphics2D g) {
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
    private int parsePosition(String position, int dimension) {
        if (position.endsWith("%")) {
            float percentage = Float.parseFloat(position.substring(0, position.length() - 1)) / 100f;
            return (int)(dimension * percentage);
        } else {
            return Integer.parseInt(position);
        }
    }
}

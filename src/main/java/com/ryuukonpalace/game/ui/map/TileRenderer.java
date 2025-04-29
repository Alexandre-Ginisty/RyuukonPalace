package com.ryuukonpalace.game.ui.map;

import com.ryuukonpalace.game.utils.PerformanceOptimizer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsable du rendu optimisé des tuiles de la carte du monde.
 * Utilise un système de mise en cache pour améliorer les performances.
 */
public class TileRenderer {
    
    // Taille des tuiles en pixels
    private final int tileSize;
    
    // Cache des tuiles rendues
    private final Map<String, BufferedImage> tileCache;
    
    // Référence à l'optimiseur de performances
    private final PerformanceOptimizer performanceOptimizer;
    
    /**
     * Constructeur pour le renderer de tuiles
     * @param tileSize Taille des tuiles en pixels
     */
    public TileRenderer(int tileSize) {
        this.tileSize = tileSize;
        this.tileCache = new HashMap<>();
        this.performanceOptimizer = PerformanceOptimizer.getInstance();
    }
    
    /**
     * Générer une clé unique pour identifier une tuile dans le cache
     * @param x Position X de la tuile
     * @param y Position Y de la tuile
     * @param zoom Niveau de zoom
     * @param type Type de tuile (région, route, etc.)
     * @return Clé unique pour la tuile
     */
    private String generateTileKey(int x, int y, float zoom, String type) {
        return String.format("%s_%d_%d_%.2f", type, x, y, zoom);
    }
    
    /**
     * Rendre une tuile de région
     * @param g Contexte graphique
     * @param region Région à rendre
     * @param x Position X de la tuile
     * @param y Position Y de la tuile
     * @param zoom Niveau de zoom
     */
    public void renderRegionTile(Graphics2D g, MapRegion region, int x, int y, float zoom) {
        String tileKey = generateTileKey(x, y, zoom, "region_" + region.getId());
        
        // Vérifier si la tuile est déjà en cache
        BufferedImage cachedTile = performanceOptimizer.getCachedImage(tileKey);
        if (cachedTile != null) {
            // Utiliser la tuile mise en cache
            g.drawImage(cachedTile, x, y, null);
            return;
        }
        
        // Créer une nouvelle tuile
        BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tileG = tile.createGraphics();
        
        // Configurer le rendu de qualité
        tileG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        tileG.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Dessiner la région sur la tuile
        tileG.setColor(region.getColor());
        tileG.fillPolygon(region.getShape());
        
        // Dessiner la bordure de la région
        tileG.setColor(region.getBorderColor());
        tileG.setStroke(new BasicStroke(2.0f));
        tileG.drawPolygon(region.getShape());
        
        // Dessiner le nom de la région si le zoom est suffisant
        if (zoom >= 0.75f) {
            tileG.setColor(Color.WHITE);
            tileG.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = tileG.getFontMetrics();
            int textWidth = fm.stringWidth(region.getName());
            int textX = (tileSize - textWidth) / 2;
            int textY = tileSize / 2;
            tileG.drawString(region.getName(), textX, textY);
        }
        
        tileG.dispose();
        
        // Mettre la tuile en cache
        performanceOptimizer.cacheImage(tileKey, tile);
        
        // Dessiner la tuile
        g.drawImage(tile, x, y, null);
    }
    
    /**
     * Rendre une tuile de route
     * @param g Contexte graphique
     * @param route Route à rendre
     * @param x Position X de la tuile
     * @param y Position Y de la tuile
     * @param zoom Niveau de zoom
     */
    public void renderRouteTile(Graphics2D g, MapRoute route, int x, int y, float zoom) {
        String tileKey = generateTileKey(x, y, zoom, "route_" + route.getId());
        
        // Vérifier si la tuile est déjà en cache
        BufferedImage cachedTile = performanceOptimizer.getCachedImage(tileKey);
        if (cachedTile != null) {
            // Utiliser la tuile mise en cache
            g.drawImage(cachedTile, x, y, null);
            return;
        }
        
        // Créer une nouvelle tuile
        BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tileG = tile.createGraphics();
        
        // Configurer le rendu de qualité
        tileG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        tileG.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Dessiner la route sur la tuile
        tileG.setColor(route.getColor());
        tileG.setStroke(new BasicStroke(route.getWidth()));
        tileG.drawPolyline(route.getXPoints(), route.getYPoints(), route.getPointCount());
        
        tileG.dispose();
        
        // Mettre la tuile en cache
        performanceOptimizer.cacheImage(tileKey, tile);
        
        // Dessiner la tuile
        g.drawImage(tile, x, y, null);
    }
    
    /**
     * Rendre une tuile d'emplacement
     * @param g Contexte graphique
     * @param location Emplacement à rendre
     * @param x Position X de la tuile
     * @param y Position Y de la tuile
     * @param zoom Niveau de zoom
     */
    public void renderLocationTile(Graphics2D g, MapLocation location, int x, int y, float zoom) {
        String tileKey = generateTileKey(x, y, zoom, "location_" + location.getId());
        
        // Vérifier si la tuile est déjà en cache
        BufferedImage cachedTile = performanceOptimizer.getCachedImage(tileKey);
        if (cachedTile != null) {
            // Utiliser la tuile mise en cache
            g.drawImage(cachedTile, x, y, null);
            return;
        }
        
        // Créer une nouvelle tuile
        BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tileG = tile.createGraphics();
        
        // Configurer le rendu de qualité
        tileG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        tileG.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Dessiner l'emplacement sur la tuile
        int iconSize = (int)(10 * zoom);
        tileG.setColor(location.getColor());
        tileG.fillOval(location.getX() - iconSize/2, location.getY() - iconSize/2, iconSize, iconSize);
        
        // Dessiner la bordure de l'emplacement
        tileG.setColor(Color.WHITE);
        tileG.setStroke(new BasicStroke(1.0f));
        tileG.drawOval(location.getX() - iconSize/2, location.getY() - iconSize/2, iconSize, iconSize);
        
        // Dessiner le nom de l'emplacement si le zoom est suffisant
        if (zoom >= 0.5f) {
            tileG.setColor(Color.WHITE);
            tileG.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = tileG.getFontMetrics();
            int textWidth = fm.stringWidth(location.getName());
            int textX = location.getX() - textWidth / 2;
            int textY = location.getY() + iconSize + fm.getAscent();
            tileG.drawString(location.getName(), textX, textY);
        }
        
        tileG.dispose();
        
        // Mettre la tuile en cache
        performanceOptimizer.cacheImage(tileKey, tile);
        
        // Dessiner la tuile
        g.drawImage(tile, x, y, null);
    }
    
    /**
     * Vérifier si une tuile est visible dans la vue actuelle
     * @param tileX Position X de la tuile
     * @param tileY Position Y de la tuile
     * @param viewX Position X de la vue
     * @param viewY Position Y de la vue
     * @param viewWidth Largeur de la vue
     * @param viewHeight Hauteur de la vue
     * @return true si la tuile est visible, false sinon
     */
    public boolean isTileVisible(int tileX, int tileY, int viewX, int viewY, int viewWidth, int viewHeight) {
        return tileX + tileSize >= viewX && tileX <= viewX + viewWidth &&
               tileY + tileSize >= viewY && tileY <= viewY + viewHeight;
    }
    
    /**
     * Effacer le cache des tuiles
     */
    public void clearCache() {
        tileCache.clear();
    }
    
    /**
     * Invalider une partie du cache des tuiles
     * @param type Type de tuile à invalider
     */
    public void invalidateCache(String type) {
        tileCache.keySet().removeIf(key -> key.startsWith(type));
    }
}

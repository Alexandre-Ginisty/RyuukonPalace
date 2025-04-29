package com.ryuukonpalace.game.ui.map;

import com.ryuukonpalace.game.utils.PerformanceOptimizer;

import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Version optimisée du renderer de carte du monde.
 * Utilise des techniques avancées comme le rendu sélectif, la mise en cache et le multithreading
 * pour améliorer les performances.
 */
public class OptimizedWorldMapRenderer extends WorldMapRenderer {
    
    // Système de mise en cache
    private Map<String, BufferedImage> tileCache;
    private BufferedImage mapBuffer;
    private BufferedImage miniMapBuffer;
    private BufferedImage fogOfWarBuffer;
    private BufferedImage mapControlsBuffer;
    private BufferedImage miniMapControlsBuffer;
    
    // Flags pour le rafraîchissement des caches
    private boolean mapNeedsRefresh = true;
    private boolean miniMapNeedsRefresh = true;
    private boolean fogOfWarNeedsRefresh = true;
    private boolean mapControlsNeedRefresh = true;
    private boolean miniMapControlsNeedRefresh = true;
    
    // Paramètres de tuiles
    private final int tileSize = 256; // Taille des tuiles en pixels
    
    // Suivi des performances
    private PerformanceOptimizer performanceOptimizer;
    
    // Multithreading pour le chargement des ressources
    private ExecutorService mapLoadingExecutor;
    
    // Renderer de tuiles
    private TileRenderer tileRenderer;
    
    /**
     * Constructeur pour le renderer optimisé de carte du monde.
     */
    public OptimizedWorldMapRenderer(JSONObject config, Rectangle fullMapBounds, Rectangle miniMapBounds) {
        super(config, fullMapBounds, miniMapBounds);
        
        // Initialisation du système de mise en cache
        tileCache = new HashMap<>();
        mapBuffer = new BufferedImage(fullMapBounds.width, fullMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        miniMapBuffer = new BufferedImage(miniMapBounds.width, miniMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        fogOfWarBuffer = new BufferedImage(fullMapBounds.width, fullMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        mapControlsBuffer = new BufferedImage(fullMapBounds.width, fullMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        miniMapControlsBuffer = new BufferedImage(miniMapBounds.width, miniMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        
        // Initialisation du suivi des performances
        performanceOptimizer = PerformanceOptimizer.getInstance();
        
        // Initialisation du multithreading pour le chargement des ressources
        mapLoadingExecutor = Executors.newSingleThreadExecutor();
        
        // Initialisation du renderer de tuiles
        tileRenderer = new TileRenderer(tileSize);
        
        // Précharger les contrôles de la carte
        preloadControls();
    }
    
    /**
     * Précharger les contrôles de la carte pour améliorer les performances
     */
    private void preloadControls() {
        CompletableFuture.runAsync(() -> {
            // Précharger les contrôles de la carte complète
            Graphics2D g = mapControlsBuffer.createGraphics();
            super.renderMapControls(g);
            g.dispose();
            mapControlsNeedRefresh = false;
            
            // Précharger les contrôles de la mini-carte
            g = miniMapControlsBuffer.createGraphics();
            super.renderMiniMapControls(g);
            g.dispose();
            miniMapControlsNeedRefresh = false;
        }, mapLoadingExecutor);
    }
    
    /**
     * Dessine la carte complète avec optimisations.
     */
    @Override
    public void renderFullMap(Graphics2D g, List<MapRegion> regions, List<MapRoute> routes, List<MapLocation> locations) {
        performanceOptimizer.startFrame();
        
        if (mapNeedsRefresh) {
            // Effacer le buffer
            Graphics2D bufferG = mapBuffer.createGraphics();
            bufferG.setColor(new Color(20, 20, 20));
            bufferG.fillRect(0, 0, mapBuffer.getWidth(), mapBuffer.getHeight());
            
            // Calculer les limites de la vue actuelle
            Rectangle viewBounds = calculateViewBounds();
            
            // Dessiner le brouillard de guerre si activé
            if (isFogOfWarEnabled()) {
                renderFogOfWarOptimized(bufferG);
            }
            
            // Dessiner les régions visibles
            for (MapRegion region : regions) {
                if ((region.isDiscovered() || !isFogOfWarEnabled()) && region.isInView(viewBounds)) {
                    tileRenderer.renderRegionTile(bufferG, region, region.getPosition().x, region.getPosition().y, getZoom());
                }
            }
            
            // Dessiner les routes visibles
            for (MapRoute route : routes) {
                if ((route.isDiscovered() || !isFogOfWarEnabled()) && route.isInView(viewBounds)) {
                    tileRenderer.renderRouteTile(bufferG, route, route.getStartLocation().getPosition().x, route.getStartLocation().getPosition().y, getZoom());
                }
            }
            
            // Dessiner les emplacements visibles
            for (MapLocation location : locations) {
                if ((location.isDiscovered() || !isFogOfWarEnabled()) && location.isInView(viewBounds)) {
                    tileRenderer.renderLocationTile(bufferG, location, location.getPosition().x, location.getPosition().y, getZoom());
                }
            }
            
            // Dessiner le joueur
            renderPlayerOptimized(bufferG, getPlayerPosition().x, getPlayerPosition().y, true);
            
            bufferG.dispose();
            mapNeedsRefresh = false;
        }
        
        // Dessiner le buffer sur le contexte graphique
        g.drawImage(mapBuffer, getMapOffset().x, getMapOffset().y, null);
        
        // Dessiner les contrôles de la carte
        if (mapControlsNeedRefresh) {
            Graphics2D controlsG = mapControlsBuffer.createGraphics();
            super.renderMapControls(controlsG);
            controlsG.dispose();
            mapControlsNeedRefresh = false;
        }
        g.drawImage(mapControlsBuffer, 0, 0, null);
        
        performanceOptimizer.endFrame();
    }
    
    /**
     * Dessine la mini-carte avec optimisations.
     */
    @Override
    public void renderMiniMap(Graphics2D g, List<MapRegion> regions, List<MapRoute> routes, List<MapLocation> locations) {
        performanceOptimizer.startFrame();
        
        if (miniMapNeedsRefresh) {
            // Effacer le buffer
            Graphics2D bufferG = miniMapBuffer.createGraphics();
            
            // Dessiner le fond de la mini-carte
            bufferG.setColor(new Color(26, 26, 26, 179));
            bufferG.fillRoundRect(0, 0, miniMapBuffer.getWidth(), miniMapBuffer.getHeight(), 10, 10);
            
            // Dessiner la bordure
            bufferG.setColor(new Color(58, 46, 33));
            bufferG.drawRoundRect(0, 0, miniMapBuffer.getWidth(), miniMapBuffer.getHeight(), 10, 10);
            
            // Calculer l'échelle de la mini-carte
            float miniMapScale = 0.3f;
            
            // Appliquer l'échelle et la position de la mini-carte
            bufferG.translate(miniMapBuffer.getWidth() / 2, miniMapBuffer.getHeight() / 2);
            bufferG.scale(miniMapScale, miniMapScale);
            
            // Dessiner les régions visibles sur la mini-carte
            for (MapRegion region : regions) {
                if (region.isDiscovered() && isInMiniMapRange(region.getPosition())) {
                    bufferG.setColor(region.getColor());
                    bufferG.fillPolygon(region.getShape());
                }
            }
            
            // Dessiner les routes visibles sur la mini-carte
            for (MapRoute route : routes) {
                if (route.isDiscovered() && 
                    (isInMiniMapRange(route.getStartLocation().getPosition()) || 
                     isInMiniMapRange(route.getEndLocation().getPosition()))) {
                    bufferG.setColor(route.getColor());
                    bufferG.setStroke(new BasicStroke(route.getWidth() * 0.5f));
                    bufferG.drawPolyline(route.getXPoints(), route.getYPoints(), route.getPointCount());
                }
            }
            
            // Dessiner les emplacements visibles sur la mini-carte
            for (MapLocation location : locations) {
                if (location.isDiscovered() && isInMiniMapRange(location.getPosition())) {
                    int iconSize = (int)(location.getIconSize() * 0.5f);
                    bufferG.setColor(location.getColor());
                    bufferG.fillOval(location.getX() - iconSize/2, location.getY() - iconSize/2, iconSize, iconSize);
                }
            }
            
            // Dessiner le joueur sur la mini-carte
            renderPlayerOptimized(bufferG, getPlayerPosition().x, getPlayerPosition().y, false);
            
            // Restaurer la transformation
            bufferG.scale(1.0f / miniMapScale, 1.0f / miniMapScale);
            bufferG.translate(-miniMapBuffer.getWidth() / 2, -miniMapBuffer.getHeight() / 2);
            
            bufferG.dispose();
            miniMapNeedsRefresh = false;
        }
        
        // Dessiner le buffer sur le contexte graphique
        g.drawImage(miniMapBuffer, getMiniMapBounds().x, getMiniMapBounds().y, null);
        
        // Dessiner les contrôles de la mini-carte
        if (miniMapControlsNeedRefresh) {
            Graphics2D controlsG = miniMapControlsBuffer.createGraphics();
            super.renderMiniMapControls(controlsG);
            controlsG.dispose();
            miniMapControlsNeedRefresh = false;
        }
        g.drawImage(miniMapControlsBuffer, 0, 0, null);
        
        performanceOptimizer.endFrame();
    }
    
    /**
     * Dessine le brouillard de guerre avec optimisations.
     */
    private void renderFogOfWarOptimized(Graphics2D g) {
        if (fogOfWarNeedsRefresh) {
            // Effacer le buffer
            Graphics2D bufferG = fogOfWarBuffer.createGraphics();
            
            // Dessiner le brouillard de base
            bufferG.setColor(new Color(0, 0, 0, 200));
            bufferG.fillRect(0, 0, fogOfWarBuffer.getWidth(), fogOfWarBuffer.getHeight());
            
            // Dessiner les zones découvertes
            bufferG.setComposite(AlphaComposite.DstOut);
            for (Point area : getDiscoveredAreas()) {
                bufferG.fillOval(area.x - getFogRevealRadius(), area.y - getFogRevealRadius(), 
                               getFogRevealRadius() * 2, getFogRevealRadius() * 2);
            }
            
            // Restaurer le composite
            bufferG.setComposite(AlphaComposite.SrcOver);
            
            bufferG.dispose();
            fogOfWarNeedsRefresh = false;
        }
        
        // Dessiner le buffer sur le contexte graphique
        g.drawImage(fogOfWarBuffer, 0, 0, null);
    }
    
    /**
     * Dessine le joueur avec optimisations.
     */
    private void renderPlayerOptimized(Graphics2D g, int x, int y, boolean isFullMap) {
        int playerSize = isFullMap ? 16 : 8;
        
        // Dessiner le joueur
        g.setColor(Color.RED);
        g.fillOval(x - playerSize/2, y - playerSize/2, playerSize, playerSize);
        
        // Dessiner la bordure du joueur
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(x - playerSize/2, y - playerSize/2, playerSize, playerSize);
    }
    
    /**
     * Calcule les limites de la vue actuelle.
     */
    private Rectangle calculateViewBounds() {
        int viewX = (int)(-getMapOffset().x / getZoom());
        int viewY = (int)(-getMapOffset().y / getZoom());
        int viewWidth = (int)(getFullMapBounds().width / getZoom());
        int viewHeight = (int)(getFullMapBounds().height / getZoom());
        return new Rectangle(viewX, viewY, viewWidth, viewHeight);
    }
    
    /**
     * Définit les paramètres de rendu et invalide les caches.
     */
    @Override
    public void setRenderParams(Point playerPosition, float zoom, Point mapOffset, 
                               boolean fogOfWarEnabled, int fogRevealRadius, List<Point> discoveredAreas) {
        super.setRenderParams(playerPosition, zoom, mapOffset, fogOfWarEnabled, fogRevealRadius, discoveredAreas);
        
        // Invalider les caches
        mapNeedsRefresh = true;
        miniMapNeedsRefresh = true;
        fogOfWarNeedsRefresh = true;
    }
    
    /**
     * Libère les ressources utilisées par le renderer.
     */
    public void dispose() {
        mapLoadingExecutor.shutdown();
        tileCache.clear();
        mapBuffer = null;
        miniMapBuffer = null;
        fogOfWarBuffer = null;
        mapControlsBuffer = null;
        miniMapControlsBuffer = null;
    }
    
    // Getters pour accéder aux propriétés de la classe parente
    public Point getMapOffset() {
        return super.getMapOffset();
    }
    
    public float getZoom() {
        return super.getZoom();
    }
    
    public boolean isFogOfWarEnabled() {
        return super.isFogOfWarEnabled();
    }
    
    public int getFogRevealRadius() {
        return super.getFogRevealRadius();
    }
    
    public List<Point> getDiscoveredAreas() {
        return super.getDiscoveredAreas();
    }
    
    public Point getPlayerPosition() {
        return super.getPlayerPosition();
    }
    
    public Rectangle getFullMapBounds() {
        return super.getFullMapBounds();
    }
    
    public Rectangle getMiniMapBounds() {
        return super.getMiniMapBounds();
    }
    
    // Setters pour définir les propriétés de la classe parente
    public void setMapOffset(Point mapOffset) {
        super.setMapOffset(mapOffset);
    }
    
    public void setZoom(float zoom) {
        super.setZoom(zoom);
    }
    
    public void setFogOfWarEnabled(boolean fogOfWarEnabled) {
        super.setFogOfWarEnabled(fogOfWarEnabled);
    }
    
    public void setFogRevealRadius(int fogRevealRadius) {
        super.setFogRevealRadius(fogRevealRadius);
    }
    
    public void setDiscoveredAreas(List<Point> discoveredAreas) {
        super.setDiscoveredAreas(discoveredAreas);
    }
    
    public void setPlayerPosition(Point playerPosition) {
        super.setPlayerPosition(playerPosition);
    }
    
    public void setFullMapBounds(Rectangle fullMapBounds) {
        super.setFullMapBounds(fullMapBounds);
    }
    
    public void setMiniMapBounds(Rectangle miniMapBounds) {
        super.setMiniMapBounds(miniMapBounds);
    }
}

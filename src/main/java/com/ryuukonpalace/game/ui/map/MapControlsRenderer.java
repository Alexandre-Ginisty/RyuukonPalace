package com.ryuukonpalace.game.ui.map;

import com.ryuukonpalace.game.utils.PerformanceOptimizer;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Classe spécialisée pour le rendu optimisé des contrôles de la carte du monde.
 * Utilise des techniques de mise en cache pour améliorer les performances.
 */
public class MapControlsRenderer {
    
    // Buffers pour le rendu hors écran
    private BufferedImage mapControlsBuffer;
    private BufferedImage miniMapControlsBuffer;
    
    // Dimensions des buffers
    private Rectangle mapBounds;
    private Rectangle miniMapBounds;
    
    // Flags pour le rafraîchissement
    private boolean mapControlsNeedRefresh = true;
    private boolean miniMapControlsNeedRefresh = true;
    
    // Suivi des performances
    private PerformanceOptimizer performanceOptimizer;
    
    /**
     * Constructeur pour le renderer de contrôles de carte.
     */
    public MapControlsRenderer(Rectangle mapBounds, Rectangle miniMapBounds) {
        this.mapBounds = mapBounds;
        this.miniMapBounds = miniMapBounds;
        
        // Initialisation des buffers
        mapControlsBuffer = new BufferedImage(mapBounds.width, mapBounds.height, BufferedImage.TYPE_INT_ARGB);
        miniMapControlsBuffer = new BufferedImage(miniMapBounds.width, miniMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        
        // Initialisation du suivi des performances
        performanceOptimizer = PerformanceOptimizer.getInstance();
        
        // Générer les contrôles initiaux
        generateMapControls();
        generateMiniMapControls();
    }
    
    /**
     * Génère les contrôles de la carte complète.
     */
    private void generateMapControls() {
        performanceOptimizer.startTask("GenerateMapControls");
        
        Graphics2D g = mapControlsBuffer.createGraphics();
        
        // Effacer le buffer
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, mapBounds.width, mapBounds.height);
        g.setComposite(AlphaComposite.SrcOver);
        
        // Dessiner les contrôles de zoom
        drawZoomControls(g, mapBounds.width - 60, 20);
        
        // Dessiner la légende
        drawLegend(g, 20, mapBounds.height - 120);
        
        g.dispose();
        
        mapControlsNeedRefresh = false;
        
        performanceOptimizer.endTask("GenerateMapControls");
    }
    
    /**
     * Génère les contrôles de la mini-carte.
     */
    private void generateMiniMapControls() {
        performanceOptimizer.startTask("GenerateMiniMapControls");
        
        Graphics2D g = miniMapControlsBuffer.createGraphics();
        
        // Effacer le buffer
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, miniMapBounds.width, miniMapBounds.height);
        g.setComposite(AlphaComposite.SrcOver);
        
        // Dessiner le bouton d'agrandissement
        drawExpandButton(g, miniMapBounds.width - 25, 5);
        
        g.dispose();
        
        miniMapControlsNeedRefresh = false;
        
        performanceOptimizer.endTask("GenerateMiniMapControls");
    }
    
    /**
     * Dessine les contrôles de zoom.
     */
    private void drawZoomControls(Graphics2D g, int x, int y) {
        // Fond des contrôles
        g.setColor(new Color(40, 40, 40, 200));
        g.fillRoundRect(x, y, 50, 100, 10, 10);
        
        // Bordure
        g.setColor(new Color(80, 80, 80));
        g.drawRoundRect(x, y, 50, 100, 10, 10);
        
        // Bouton zoom +
        g.setColor(new Color(200, 200, 200));
        g.fillRoundRect(x + 10, y + 10, 30, 30, 5, 5);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(x + 25, y + 20, x + 25, y + 30);
        g.drawLine(x + 20, y + 25, x + 30, y + 25);
        
        // Bouton zoom -
        g.setColor(new Color(200, 200, 200));
        g.fillRoundRect(x + 10, y + 60, 30, 30, 5, 5);
        g.setColor(Color.BLACK);
        g.drawLine(x + 20, y + 75, x + 30, y + 75);
    }
    
    /**
     * Dessine la légende de la carte.
     */
    private void drawLegend(Graphics2D g, int x, int y) {
        // Fond de la légende
        g.setColor(new Color(40, 40, 40, 200));
        g.fillRoundRect(x, y, 150, 100, 10, 10);
        
        // Bordure
        g.setColor(new Color(80, 80, 80));
        g.drawRoundRect(x, y, 150, 100, 10, 10);
        
        // Titre
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Légende", x + 10, y + 20);
        
        // Éléments de la légende
        int itemY = y + 40;
        
        // Ville
        g.setColor(new Color(220, 220, 100));
        g.fillOval(x + 10, itemY - 5, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("Ville", x + 30, itemY);
        
        // Village
        itemY += 15;
        g.setColor(new Color(180, 180, 80));
        g.fillOval(x + 10, itemY - 5, 8, 8);
        g.setColor(Color.WHITE);
        g.drawString("Village", x + 30, itemY);
        
        // Donjon
        itemY += 15;
        g.setColor(new Color(150, 50, 50));
        g.fillOval(x + 10, itemY - 5, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString("Donjon", x + 30, itemY);
        
        // Point d'intérêt
        itemY += 15;
        g.setColor(new Color(100, 150, 200));
        g.fillOval(x + 10, itemY - 5, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString("Point d'intérêt", x + 30, itemY);
    }
    
    /**
     * Dessine le bouton d'agrandissement de la mini-carte.
     */
    private void drawExpandButton(Graphics2D g, int x, int y) {
        // Fond du bouton
        g.setColor(new Color(40, 40, 40, 200));
        g.fillRoundRect(x, y, 20, 20, 5, 5);
        
        // Bordure
        g.setColor(new Color(80, 80, 80));
        g.drawRoundRect(x, y, 20, 20, 5, 5);
        
        // Icône d'agrandissement
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(x + 5, y + 10, x + 15, y + 10);
        g.drawLine(x + 10, y + 5, x + 10, y + 15);
    }
    
    /**
     * Dessine les contrôles de la carte complète.
     */
    public void renderMapControls(Graphics2D g) {
        performanceOptimizer.startTask("RenderMapControls");
        
        if (mapControlsNeedRefresh) {
            generateMapControls();
        }
        
        // Dessiner le buffer sur le contexte graphique
        g.drawImage(mapControlsBuffer, 0, 0, null);
        
        performanceOptimizer.endTask("RenderMapControls");
    }
    
    /**
     * Dessine les contrôles de la mini-carte.
     */
    public void renderMiniMapControls(Graphics2D g) {
        performanceOptimizer.startTask("RenderMiniMapControls");
        
        if (miniMapControlsNeedRefresh) {
            generateMiniMapControls();
        }
        
        // Dessiner le buffer sur le contexte graphique
        g.drawImage(miniMapControlsBuffer, miniMapBounds.x, miniMapBounds.y, null);
        
        performanceOptimizer.endTask("RenderMiniMapControls");
    }
    
    /**
     * Invalide le cache des contrôles de la carte complète.
     */
    public void invalidateMapControls() {
        mapControlsNeedRefresh = true;
    }
    
    /**
     * Invalide le cache des contrôles de la mini-carte.
     */
    public void invalidateMiniMapControls() {
        miniMapControlsNeedRefresh = true;
    }
    
    /**
     * Redimensionne le renderer de contrôles.
     */
    public void resize(Rectangle mapBounds, Rectangle miniMapBounds) {
        this.mapBounds = mapBounds;
        this.miniMapBounds = miniMapBounds;
        
        // Recréer les buffers
        mapControlsBuffer = new BufferedImage(mapBounds.width, mapBounds.height, BufferedImage.TYPE_INT_ARGB);
        miniMapControlsBuffer = new BufferedImage(miniMapBounds.width, miniMapBounds.height, BufferedImage.TYPE_INT_ARGB);
        
        // Forcer le rafraîchissement
        mapControlsNeedRefresh = true;
        miniMapControlsNeedRefresh = true;
    }
    
    /**
     * Libère les ressources utilisées par le renderer.
     */
    public void dispose() {
        mapControlsBuffer = null;
        miniMapControlsBuffer = null;
    }
}

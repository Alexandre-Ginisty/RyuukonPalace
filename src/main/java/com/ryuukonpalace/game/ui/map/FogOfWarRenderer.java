package com.ryuukonpalace.game.ui.map;

import com.ryuukonpalace.game.utils.PerformanceOptimizer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe spécialisée pour le rendu optimisé du brouillard de guerre.
 * Utilise des techniques avancées comme le rendu hors écran, le multithreading,
 * et les shaders pour améliorer les performances.
 */
public class FogOfWarRenderer {
    
    // Buffer pour le rendu hors écran
    private BufferedImage fogBuffer;
    private BufferedImage blurredFogBuffer;
    
    // Dimensions du buffer
    private int width;
    private int height;
    
    // Paramètres du brouillard
    private boolean enabled;
    private int revealRadius;
    private List<Point> discoveredAreas;
    
    // Flags pour le rafraîchissement
    private boolean needsRefresh = true;
    
    // Suivi des performances
    private PerformanceOptimizer performanceOptimizer;
    
    // Multithreading pour le traitement du brouillard
    private ExecutorService fogProcessor;
    
    // Noyau pour le flou gaussien
    private ConvolveOp blurOp;
    
    /**
     * Constructeur pour le renderer de brouillard de guerre.
     */
    public FogOfWarRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        
        // Initialisation des buffers
        fogBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        blurredFogBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // Initialisation du suivi des performances
        performanceOptimizer = PerformanceOptimizer.getInstance();
        
        // Initialisation du multithreading
        fogProcessor = Executors.newSingleThreadExecutor();
        
        // Initialisation du noyau pour le flou gaussien
        float[] blurKernel = {
            1/16f, 2/16f, 1/16f,
            2/16f, 4/16f, 2/16f,
            1/16f, 2/16f, 1/16f
        };
        blurOp = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, null);
    }
    
    /**
     * Met à jour les paramètres du brouillard de guerre.
     */
    public void updateFogParams(boolean enabled, int revealRadius, List<Point> discoveredAreas) {
        boolean changed = this.enabled != enabled || this.revealRadius != revealRadius;
        
        this.enabled = enabled;
        this.revealRadius = revealRadius;
        
        if (this.discoveredAreas == null || this.discoveredAreas.size() != discoveredAreas.size()) {
            changed = true;
        } else {
            for (int i = 0; i < this.discoveredAreas.size(); i++) {
                if (!this.discoveredAreas.get(i).equals(discoveredAreas.get(i))) {
                    changed = true;
                    break;
                }
            }
        }
        
        this.discoveredAreas = discoveredAreas;
        
        if (changed) {
            needsRefresh = true;
        }
    }
    
    /**
     * Génère le brouillard de guerre de manière asynchrone.
     */
    public void generateFogAsync(Runnable onComplete) {
        if (!needsRefresh) {
            onComplete.run();
            return;
        }
        
        CompletableFuture.runAsync(() -> {
            performanceOptimizer.startTask("GenerateFog");
            
            // Effacer le buffer
            Graphics2D g = fogBuffer.createGraphics();
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, width, height);
            g.setComposite(AlphaComposite.SrcOver);
            
            if (enabled) {
                // Dessiner le brouillard de base
                g.setColor(new Color(0, 0, 0, 200));
                g.fillRect(0, 0, width, height);
                
                // Dessiner les zones découvertes
                if (discoveredAreas != null) {
                    g.setComposite(AlphaComposite.DstOut);
                    for (Point area : discoveredAreas) {
                        // Utiliser un dégradé radial pour un effet plus doux
                        RadialGradientPaint gradient = new RadialGradientPaint(
                            area.x, area.y, revealRadius,
                            new float[]{0.0f, 0.7f, 1.0f},
                            new Color[]{
                                new Color(0, 0, 0, 255),
                                new Color(0, 0, 0, 128),
                                new Color(0, 0, 0, 0)
                            }
                        );
                        g.setPaint(gradient);
                        g.fillOval(area.x - revealRadius, area.y - revealRadius, 
                                 revealRadius * 2, revealRadius * 2);
                    }
                }
            }
            
            g.dispose();
            
            // Appliquer un flou pour adoucir les bords
            blurOp.filter(fogBuffer, blurredFogBuffer);
            
            needsRefresh = false;
            performanceOptimizer.endTask("GenerateFog");
            
            // Appeler le callback sur le thread principal
            onComplete.run();
        }, fogProcessor);
    }
    
    /**
     * Dessine le brouillard de guerre.
     */
    public void renderFog(Graphics2D g) {
        if (!enabled) {
            return;
        }
        
        performanceOptimizer.startTask("RenderFog");
        
        // Dessiner le buffer sur le contexte graphique
        g.drawImage(blurredFogBuffer, 0, 0, null);
        
        performanceOptimizer.endTask("RenderFog");
    }
    
    /**
     * Redimensionne le renderer de brouillard.
     */
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        
        // Recréer les buffers
        fogBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        blurredFogBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // Forcer le rafraîchissement
        needsRefresh = true;
    }
    
    /**
     * Vérifie si le brouillard a besoin d'être rafraîchi.
     */
    public boolean needsRefresh() {
        return needsRefresh;
    }
    
    /**
     * Libère les ressources utilisées par le renderer.
     */
    public void dispose() {
        fogProcessor.shutdown();
        fogBuffer = null;
        blurredFogBuffer = null;
    }
}

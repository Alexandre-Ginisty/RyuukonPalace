package com.ryuukonpalace.game.ui.map;

import com.ryuukonpalace.game.utils.PerformanceOptimizer;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe qui gère les performances du rendu de la carte du monde.
 * Surveille les performances et ajuste dynamiquement les paramètres de rendu
 * pour maintenir un framerate cible.
 */
public class MapPerformanceManager {
    
    // Singleton
    private static MapPerformanceManager instance;
    
    // Optimiseur de performances général
    private PerformanceOptimizer performanceOptimizer;
    
    // Paramètres de performance
    private int targetFPS = 60;
    private int currentLOD = 0; // Niveau de détail actuel (0 = maximum, plus élevé = moins de détails)
    private boolean adaptiveLODEnabled = true;
    private int maxLOD = 3;
    
    // Métriques de performance
    private Map<String, Long> renderTimes;
    private long lastFrameTime;
    private double averageFrameTime;
    private int frameCount;
    private int fpsCounter;
    private long fpsTimestamp;
    private int currentFPS;
    
    // Seuils de performance
    private static final long LOW_PERFORMANCE_THRESHOLD = 33; // 30 FPS
    private static final long HIGH_PERFORMANCE_THRESHOLD = 16; // 60 FPS
    
    /**
     * Constructeur privé (singleton).
     */
    private MapPerformanceManager() {
        performanceOptimizer = PerformanceOptimizer.getInstance();
        renderTimes = new HashMap<>();
        lastFrameTime = System.nanoTime();
        averageFrameTime = 0;
        frameCount = 0;
        fpsCounter = 0;
        fpsTimestamp = System.currentTimeMillis();
        currentFPS = 0;
    }
    
    /**
     * Obtient l'instance unique du gestionnaire de performances.
     */
    public static synchronized MapPerformanceManager getInstance() {
        if (instance == null) {
            instance = new MapPerformanceManager();
        }
        return instance;
    }
    
    /**
     * Commence le suivi d'une frame.
     */
    public void startFrame() {
        lastFrameTime = System.nanoTime();
        performanceOptimizer.startFrame();
    }
    
    /**
     * Termine le suivi d'une frame et ajuste les paramètres si nécessaire.
     */
    public void endFrame() {
        long frameTime = System.nanoTime() - lastFrameTime;
        
        // Mettre à jour la moyenne mobile
        if (frameCount < 30) {
            averageFrameTime = (averageFrameTime * frameCount + frameTime) / (frameCount + 1);
            frameCount++;
        } else {
            averageFrameTime = averageFrameTime * 0.95 + frameTime * 0.05;
        }
        
        // Mettre à jour le compteur de FPS
        fpsCounter++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - fpsTimestamp >= 1000) {
            currentFPS = fpsCounter;
            fpsCounter = 0;
            fpsTimestamp = currentTime;
            
            // Ajuster le niveau de détail si nécessaire
            if (adaptiveLODEnabled) {
                adjustLOD();
            }
        }
        
        performanceOptimizer.endFrame();
    }
    
    /**
     * Commence le suivi d'une tâche de rendu.
     */
    public void startRenderTask(String taskName) {
        performanceOptimizer.startTask(taskName);
    }
    
    /**
     * Termine le suivi d'une tâche de rendu.
     */
    public void endRenderTask(String taskName) {
        long taskTime = performanceOptimizer.endTask(taskName);
        renderTimes.put(taskName, taskTime);
    }
    
    /**
     * Ajuste le niveau de détail en fonction des performances.
     */
    private void adjustLOD() {
        long frameTimeMs = (long)(averageFrameTime / 1_000_000);
        
        if (frameTimeMs > LOW_PERFORMANCE_THRESHOLD && currentLOD < maxLOD) {
            // Performances faibles, réduire le niveau de détail
            currentLOD++;
            System.out.println("Performances faibles (" + currentFPS + " FPS), réduction du niveau de détail à " + currentLOD);
        } else if (frameTimeMs < HIGH_PERFORMANCE_THRESHOLD && currentLOD > 0) {
            // Bonnes performances, augmenter le niveau de détail
            currentLOD--;
            System.out.println("Bonnes performances (" + currentFPS + " FPS), augmentation du niveau de détail à " + currentLOD);
        }
    }
    
    /**
     * Détermine si un élément doit être rendu en fonction de sa distance et du niveau de détail.
     */
    public boolean shouldRender(Rectangle elementBounds, Rectangle viewBounds, int elementPriority) {
        // Les éléments de haute priorité sont toujours rendus
        if (elementPriority == 0) {
            return true;
        }
        
        // Si l'élément n'est pas dans la vue, ne pas le rendre
        if (!elementBounds.intersects(viewBounds)) {
            return false;
        }
        
        // Si le niveau de détail est suffisamment bas, rendre tous les éléments visibles
        if (currentLOD == 0) {
            return true;
        }
        
        // Calculer la distance au centre de la vue
        int centerX = viewBounds.x + viewBounds.width / 2;
        int centerY = viewBounds.y + viewBounds.height / 2;
        int elementCenterX = elementBounds.x + elementBounds.width / 2;
        int elementCenterY = elementBounds.y + elementBounds.height / 2;
        
        double distance = Math.sqrt(
            Math.pow(centerX - elementCenterX, 2) + 
            Math.pow(centerY - elementCenterY, 2)
        );
        
        // Calculer le seuil de distance en fonction du niveau de détail et de la priorité
        double threshold = viewBounds.width / 2.0 * (1.0 - (currentLOD * 0.2)) / (elementPriority + 1);
        
        return distance <= threshold;
    }
    
    /**
     * Détermine le niveau de détail à utiliser pour un élément.
     */
    public int getElementLOD(Rectangle elementBounds, Rectangle viewBounds, int baseLOD) {
        // Calculer la distance au centre de la vue
        int centerX = viewBounds.x + viewBounds.width / 2;
        int centerY = viewBounds.y + viewBounds.height / 2;
        int elementCenterX = elementBounds.x + elementBounds.width / 2;
        int elementCenterY = elementBounds.y + elementBounds.height / 2;
        
        double distance = Math.sqrt(
            Math.pow(centerX - elementCenterX, 2) + 
            Math.pow(centerY - elementCenterY, 2)
        );
        
        // Calculer le niveau de détail en fonction de la distance
        int distanceLOD = (int)(distance / (viewBounds.width / 4.0));
        
        // Combiner avec le niveau de détail global et le niveau de base
        return Math.min(maxLOD, Math.max(0, currentLOD + distanceLOD + baseLOD));
    }
    
    /**
     * Obtient le temps de rendu moyen pour une tâche.
     */
    public long getAverageRenderTime(String taskName) {
        return renderTimes.getOrDefault(taskName, 0L);
    }
    
    /**
     * Obtient le FPS actuel.
     */
    public int getCurrentFPS() {
        return currentFPS;
    }
    
    /**
     * Obtient le niveau de détail actuel.
     */
    public int getCurrentLOD() {
        return currentLOD;
    }
    
    /**
     * Définit le niveau de détail actuel.
     */
    public void setCurrentLOD(int lod) {
        this.currentLOD = Math.max(0, Math.min(maxLOD, lod));
    }
    
    /**
     * Obtient le FPS cible.
     */
    public int getTargetFPS() {
        return targetFPS;
    }
    
    /**
     * Définit le FPS cible.
     */
    public void setTargetFPS(int targetFPS) {
        this.targetFPS = targetFPS;
    }
    
    /**
     * Vérifie si l'ajustement automatique du niveau de détail est activé.
     */
    public boolean isAdaptiveLODEnabled() {
        return adaptiveLODEnabled;
    }
    
    /**
     * Active ou désactive l'ajustement automatique du niveau de détail.
     */
    public void setAdaptiveLODEnabled(boolean enabled) {
        this.adaptiveLODEnabled = enabled;
    }
    
    /**
     * Obtient le niveau de détail maximum.
     */
    public int getMaxLOD() {
        return maxLOD;
    }
    
    /**
     * Définit le niveau de détail maximum.
     */
    public void setMaxLOD(int maxLOD) {
        this.maxLOD = Math.max(1, maxLOD);
    }
    
    /**
     * Réinitialise les métriques de performance.
     */
    public void resetMetrics() {
        renderTimes.clear();
        averageFrameTime = 0;
        frameCount = 0;
        fpsCounter = 0;
        fpsTimestamp = System.currentTimeMillis();
        currentFPS = 0;
        currentLOD = 0;
    }
}

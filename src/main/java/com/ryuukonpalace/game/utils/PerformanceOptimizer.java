package com.ryuukonpalace.game.utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Classe utilitaire pour optimiser les performances du jeu.
 * Fournit des mécanismes de mise en cache, de surveillance des performances et d'optimisation.
 */
public class PerformanceOptimizer {
    
    // Singleton instance
    private static PerformanceOptimizer instance;
    
    // Cache pour les images
    private final Map<String, BufferedImage> imageCache;
    
    // Statistiques de performance
    private long totalRenderTime = 0;
    private int frameCount = 0;
    private long lastFrameTime = 0;
    private double averageFrameTime = 0;
    private double minFrameTime = Double.MAX_VALUE;
    private double maxFrameTime = 0;
    private int fpsCounter = 0;
    private long lastFpsUpdateTime = 0;
    private int currentFps = 0;
    
    // Mesures de performance pour les tâches
    private final Map<String, Long> taskStartTimes;
    private final Map<String, Long> taskTotalTimes;
    private final Map<String, Integer> taskCounts;
    
    // Multithreading pour le chargement des ressources
    private ExecutorService resourceLoadingExecutor;
    
    /**
     * Constructeur privé pour le singleton
     */
    private PerformanceOptimizer() {
        imageCache = new HashMap<>();
        taskStartTimes = new HashMap<>();
        taskTotalTimes = new HashMap<>();
        taskCounts = new HashMap<>();
        
        resourceLoadingExecutor = Executors.newFixedThreadPool(2);
        lastFrameTime = System.nanoTime();
        lastFpsUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Obtenir l'instance unique de l'optimiseur de performances
     * @return L'instance du PerformanceOptimizer
     */
    public static PerformanceOptimizer getInstance() {
        if (instance == null) {
            instance = new PerformanceOptimizer();
        }
        return instance;
    }
    
    /**
     * Ajouter une image au cache
     * @param key Clé pour identifier l'image
     * @param image Image à mettre en cache
     */
    public void cacheImage(String key, BufferedImage image) {
        imageCache.put(key, image);
    }
    
    /**
     * Récupérer une image du cache
     * @param key Clé de l'image
     * @return L'image mise en cache, ou null si non trouvée
     */
    public BufferedImage getCachedImage(String key) {
        return imageCache.get(key);
    }
    
    /**
     * Vérifier si une image est dans le cache
     * @param key Clé de l'image
     * @return true si l'image est dans le cache, false sinon
     */
    public boolean isImageCached(String key) {
        return imageCache.containsKey(key);
    }
    
    /**
     * Effacer une image du cache
     * @param key Clé de l'image à effacer
     */
    public void clearCachedImage(String key) {
        imageCache.remove(key);
    }
    
    /**
     * Effacer tout le cache d'images
     */
    public void clearImageCache() {
        imageCache.clear();
    }
    
    /**
     * Marquer le début d'un frame pour les mesures de performance
     */
    public void startFrame() {
        lastFrameTime = System.nanoTime();
    }
    
    /**
     * Marquer la fin d'un frame et mettre à jour les statistiques
     */
    public void endFrame() {
        long currentTime = System.nanoTime();
        long frameTime = currentTime - lastFrameTime;
        double frameTimeMs = frameTime / 1_000_000.0;
        
        totalRenderTime += frameTime;
        frameCount++;
        fpsCounter++;
        
        // Mettre à jour les statistiques
        minFrameTime = Math.min(minFrameTime, frameTimeMs);
        maxFrameTime = Math.max(maxFrameTime, frameTimeMs);
        averageFrameTime = totalRenderTime / (frameCount * 1_000_000.0);
        
        // Mettre à jour le FPS toutes les secondes
        long now = System.currentTimeMillis();
        if (now - lastFpsUpdateTime >= 1000) {
            currentFps = fpsCounter;
            fpsCounter = 0;
            lastFpsUpdateTime = now;
            
            // Afficher les statistiques de performance toutes les 5 secondes
            if (now % 5000 < 1000) {
                printPerformanceStats();
            }
        }
    }
    
    /**
     * Obtenir le FPS actuel
     * @return Nombre de frames par seconde
     */
    public int getCurrentFps() {
        return currentFps;
    }
    
    /**
     * Obtenir le temps moyen par frame en millisecondes
     * @return Temps moyen par frame
     */
    public double getAverageFrameTime() {
        return averageFrameTime;
    }
    
    /**
     * Afficher les statistiques de performance dans la console
     */
    public void printPerformanceStats() {
        System.out.println("=== Performance Stats ===");
        System.out.println("FPS: " + currentFps);
        System.out.println("Avg Frame Time: " + String.format("%.2f", averageFrameTime) + " ms");
        System.out.println("Min Frame Time: " + String.format("%.2f", minFrameTime) + " ms");
        System.out.println("Max Frame Time: " + String.format("%.2f", maxFrameTime) + " ms");
        System.out.println("Image Cache Size: " + imageCache.size());
    }
    
    /**
     * Démarrer la mesure du temps d'exécution d'une tâche
     * @param taskName Nom de la tâche
     */
    public void startTask(String taskName) {
        taskStartTimes.put(taskName, System.nanoTime());
    }
    
    /**
     * Terminer la mesure du temps d'exécution d'une tâche
     * @param taskName Nom de la tâche
     * @return Temps d'exécution en millisecondes
     */
    public long endTask(String taskName) {
        if (taskStartTimes.containsKey(taskName)) {
            long startTime = taskStartTimes.get(taskName);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // Convertir en ms
            
            // Mettre à jour les statistiques
            taskTotalTimes.put(taskName, taskTotalTimes.getOrDefault(taskName, 0L) + duration);
            taskCounts.put(taskName, taskCounts.getOrDefault(taskName, 0) + 1);
            
            return duration;
        }
        return 0;
    }
    
    /**
     * Obtenir le temps moyen d'exécution d'une tâche
     * @param taskName Nom de la tâche
     * @return Temps moyen en millisecondes, ou 0 si la tâche n'a jamais été exécutée
     */
    public double getAverageTaskTime(String taskName) {
        if (taskCounts.containsKey(taskName) && taskCounts.get(taskName) > 0) {
            return (double) taskTotalTimes.get(taskName) / taskCounts.get(taskName);
        }
        return 0;
    }
    
    /**
     * Obtenir le nombre d'exécutions d'une tâche
     * @param taskName Nom de la tâche
     * @return Nombre d'exécutions
     */
    public int getTaskCount(String taskName) {
        return taskCounts.getOrDefault(taskName, 0);
    }
    
    /**
     * Exécuter une tâche en arrière-plan
     * @param task Tâche à exécuter
     */
    public void executeAsync(Runnable task) {
        resourceLoadingExecutor.submit(task);
    }
    
    /**
     * Libérer les ressources utilisées par l'optimiseur
     */
    public void dispose() {
        resourceLoadingExecutor.shutdown();
        try {
            if (!resourceLoadingExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                resourceLoadingExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            resourceLoadingExecutor.shutdownNow();
        }
        
        clearImageCache();
        System.out.println("Performance Optimizer disposed");
    }
}

package com.ryuukonpalace.game.ui.map;

import com.ryuukonpalace.game.utils.PerformanceOptimizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

/**
 * Classe qui gère le chargement asynchrone des ressources de la carte du monde.
 * Permet d'améliorer les performances lors du chargement initial et des transitions.
 */
public class AsyncMapResourceLoader {
    
    // Singleton
    private static AsyncMapResourceLoader instance;
    
    // Exécuteur pour les tâches asynchrones
    private ExecutorService executor;
    
    // Cache des ressources
    private Map<String, BufferedImage> imageCache;
    private Map<String, Object> dataCache;
    
    // Suivi des performances
    private PerformanceOptimizer performanceOptimizer;
    
    // Callbacks pour les événements de chargement
    private Map<String, Consumer<Object>> loadCallbacks;
    
    /**
     * Constructeur privé (singleton).
     */
    private AsyncMapResourceLoader() {
        executor = Executors.newFixedThreadPool(2); // Limité à 2 threads pour éviter de surcharger le système
        imageCache = new ConcurrentHashMap<>();
        dataCache = new ConcurrentHashMap<>();
        loadCallbacks = new HashMap<>();
        performanceOptimizer = PerformanceOptimizer.getInstance();
    }
    
    /**
     * Obtient l'instance unique du chargeur de ressources.
     */
    public static synchronized AsyncMapResourceLoader getInstance() {
        if (instance == null) {
            instance = new AsyncMapResourceLoader();
        }
        return instance;
    }
    
    /**
     * Charge une image de manière asynchrone.
     * @param path Chemin de l'image
     * @param callback Callback appelé lorsque l'image est chargée
     */
    public void loadImage(String path, Consumer<BufferedImage> callback) {
        // Vérifier si l'image est déjà en cache
        if (imageCache.containsKey(path)) {
            callback.accept(imageCache.get(path));
            return;
        }
        
        // Charger l'image de manière asynchrone
        CompletableFuture.supplyAsync(() -> {
            performanceOptimizer.startTask("LoadImage:" + path);
            try {
                BufferedImage image = ImageIO.read(new File(path));
                imageCache.put(path, image);
                return image;
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + path);
                e.printStackTrace();
                return null;
            } finally {
                performanceOptimizer.endTask("LoadImage:" + path);
            }
        }, executor).thenAccept(callback);
    }
    
    /**
     * Précharge une image en arrière-plan.
     * @param path Chemin de l'image
     */
    public void preloadImage(String path) {
        // Vérifier si l'image est déjà en cache
        if (imageCache.containsKey(path)) {
            return;
        }
        
        // Précharger l'image de manière asynchrone
        CompletableFuture.supplyAsync(() -> {
            performanceOptimizer.startTask("PreloadImage:" + path);
            try {
                BufferedImage image = ImageIO.read(new File(path));
                imageCache.put(path, image);
                return image;
            } catch (Exception e) {
                System.err.println("Erreur lors du préchargement de l'image: " + path);
                e.printStackTrace();
                return null;
            } finally {
                performanceOptimizer.endTask("PreloadImage:" + path);
            }
        }, executor);
    }
    
    /**
     * Charge des données de manière asynchrone.
     * @param key Clé des données
     * @param loader Fonction de chargement des données
     * @param callback Callback appelé lorsque les données sont chargées
     */
    public <T> void loadData(String key, DataLoader<T> loader, Consumer<T> callback) {
        // Vérifier si les données sont déjà en cache
        if (dataCache.containsKey(key)) {
            @SuppressWarnings("unchecked")
            T data = (T) dataCache.get(key);
            callback.accept(data);
            return;
        }
        
        // Charger les données de manière asynchrone
        CompletableFuture.supplyAsync(() -> {
            performanceOptimizer.startTask("LoadData:" + key);
            try {
                T data = loader.load();
                dataCache.put(key, data);
                return data;
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des données: " + key);
                e.printStackTrace();
                return null;
            } finally {
                performanceOptimizer.endTask("LoadData:" + key);
            }
        }, executor).thenAccept(callback);
    }
    
    /**
     * Précharge des données en arrière-plan.
     * @param key Clé des données
     * @param loader Fonction de chargement des données
     */
    public <T> void preloadData(String key, DataLoader<T> loader) {
        // Vérifier si les données sont déjà en cache
        if (dataCache.containsKey(key)) {
            return;
        }
        
        // Précharger les données de manière asynchrone
        CompletableFuture.supplyAsync(() -> {
            performanceOptimizer.startTask("PreloadData:" + key);
            try {
                T data = loader.load();
                dataCache.put(key, data);
                return data;
            } catch (Exception e) {
                System.err.println("Erreur lors du préchargement des données: " + key);
                e.printStackTrace();
                return null;
            } finally {
                performanceOptimizer.endTask("PreloadData:" + key);
            }
        }, executor);
    }
    
    /**
     * Enregistre un callback pour un événement de chargement.
     * @param event Nom de l'événement
     * @param callback Callback à appeler
     */
    public void registerLoadCallback(String event, Consumer<Object> callback) {
        loadCallbacks.put(event, callback);
    }
    
    /**
     * Déclenche un événement de chargement.
     * @param event Nom de l'événement
     * @param data Données à passer au callback
     */
    public void triggerLoadEvent(String event, Object data) {
        if (loadCallbacks.containsKey(event)) {
            loadCallbacks.get(event).accept(data);
        }
    }
    
    /**
     * Vide le cache d'images.
     */
    public void clearImageCache() {
        imageCache.clear();
    }
    
    /**
     * Vide le cache de données.
     */
    public void clearDataCache() {
        dataCache.clear();
    }
    
    /**
     * Supprime une image du cache.
     * @param path Chemin de l'image
     */
    public void removeFromImageCache(String path) {
        imageCache.remove(path);
    }
    
    /**
     * Supprime des données du cache.
     * @param key Clé des données
     */
    public void removeFromDataCache(String key) {
        dataCache.remove(key);
    }
    
    /**
     * Libère les ressources utilisées par le chargeur.
     */
    public void dispose() {
        executor.shutdown();
        imageCache.clear();
        dataCache.clear();
        loadCallbacks.clear();
    }
    
    /**
     * Interface fonctionnelle pour le chargement de données.
     */
    @FunctionalInterface
    public interface DataLoader<T> {
        T load() throws Exception;
    }
}

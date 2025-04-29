package com.ryuukonpalace.game.utils;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;

import com.ryuukonpalace.game.core.TextureManager;

/**
 * Classe utilitaire pour charger et gérer les sprites 2D et les spritesheets.
 * Remplace le ModelLoader pour une approche 2.5D style Pokémon Blanc.
 */
public class SpriteLoader {
    
    private static SpriteLoader instance;
    
    // Cache des sprites pour éviter de charger plusieurs fois le même sprite
    private Map<String, Integer> spriteCache;
    
    // Cache des spritesheets
    private Map<String, SpriteSheet> spriteSheetCache;
    
    // Cache des tuiles par défaut (générées à la volée)
    private Map<Integer, Integer> defaultTileCache;
    
    // Pool de threads pour le chargement asynchrone
    private ExecutorService loadingThreadPool;
    
    // Couleurs pour les tuiles par défaut
    private static final Color[] DEFAULT_TILE_COLORS = {
        new Color(200, 200, 200),  // Gris clair
        new Color(100, 180, 100),  // Vert
        new Color(180, 180, 100),  // Jaune-brun
        new Color(100, 100, 180),  // Bleu
        new Color(180, 100, 100),  // Rouge
        new Color(180, 100, 180),  // Violet
        new Color(100, 180, 180),  // Cyan
        new Color(50, 50, 50)      // Gris foncé
    };
    
    /**
     * Constructeur privé (singleton)
     */
    private SpriteLoader() {
        spriteCache = new ConcurrentHashMap<>();
        spriteSheetCache = new ConcurrentHashMap<>();
        defaultTileCache = new ConcurrentHashMap<>();
        loadingThreadPool = Executors.newFixedThreadPool(2);
    }
    
    /**
     * Obtenir l'instance unique du SpriteLoader
     * @return L'instance du SpriteLoader
     */
    public static SpriteLoader getInstance() {
        if (instance == null) {
            instance = new SpriteLoader();
        }
        return instance;
    }
    
    /**
     * Charger un sprite à partir d'un fichier
     * @param path Chemin du fichier
     * @return ID de la texture chargée
     */
    public int loadSprite(String path) {
        // Vérifier si le sprite est déjà dans le cache
        if (spriteCache.containsKey(path)) {
            return spriteCache.get(path);
        }
        
        try {
            // Charger l'image
            BufferedImage image = ImageIO.read(new File(path));
            
            // Obtenir l'ID de texture via le TextureManager
            int textureId = TextureManager.getInstance().loadTexture(image);
            
            // Ajouter au cache
            spriteCache.put(path, textureId);
            
            return textureId;
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du sprite: " + path);
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Charger un sprite de manière asynchrone
     * @param path Chemin du fichier
     * @return Future contenant l'ID de la texture
     */
    public Future<Integer> loadSpriteAsync(String path) {
        return loadingThreadPool.submit(() -> loadSprite(path));
    }
    
    /**
     * Charger une spritesheet
     * @param path Chemin du fichier
     * @param spriteWidth Largeur d'un sprite dans la spritesheet
     * @param spriteHeight Hauteur d'un sprite dans la spritesheet
     * @return SpriteSheet chargée
     */
    public SpriteSheet loadSpriteSheet(String path, int spriteWidth, int spriteHeight) {
        // Vérifier si la spritesheet est déjà dans le cache
        String key = path + "_" + spriteWidth + "_" + spriteHeight;
        if (spriteSheetCache.containsKey(key)) {
            return spriteSheetCache.get(key);
        }
        
        try {
            // Charger l'image
            BufferedImage image = ImageIO.read(new File(path));
            
            // Créer la spritesheet
            SpriteSheet spriteSheet = new SpriteSheet(image, spriteWidth, spriteHeight);
            
            // Ajouter au cache
            spriteSheetCache.put(key, spriteSheet);
            
            return spriteSheet;
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la spritesheet: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Charger une tuile par défaut (générée à la volée)
     * @param tileId ID de la tuile
     * @return ID de la texture
     */
    public int loadDefaultTile(int tileId) {
        // Vérifier si la tuile est déjà dans le cache
        if (defaultTileCache.containsKey(tileId)) {
            return defaultTileCache.get(tileId);
        }
        
        // Générer une tuile par défaut
        BufferedImage tileImage = generateDefaultTile(tileId);
        
        // Obtenir l'ID de texture via le TextureManager
        int textureId = TextureManager.getInstance().loadTexture(tileImage);
        
        // Ajouter au cache
        defaultTileCache.put(tileId, textureId);
        
        return textureId;
    }
    
    /**
     * Générer une tuile par défaut
     * @param tileId ID de la tuile
     * @return Image de la tuile
     */
    private BufferedImage generateDefaultTile(int tileId) {
        // Créer une image vide
        BufferedImage tileImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tileImage.createGraphics();
        
        // Choisir une couleur basée sur l'ID
        Color baseColor = DEFAULT_TILE_COLORS[tileId % DEFAULT_TILE_COLORS.length];
        
        // Dessiner le fond
        g.setColor(baseColor);
        g.fillRect(0, 0, 32, 32);
        
        // Dessiner une bordure
        g.setColor(baseColor.darker());
        g.drawRect(0, 0, 31, 31);
        
        // Dessiner l'ID au centre
        g.setColor(Color.WHITE);
        String idText = String.valueOf(tileId);
        int textWidth = g.getFontMetrics().stringWidth(idText);
        g.drawString(idText, (32 - textWidth) / 2, 20);
        
        g.dispose();
        
        return tileImage;
    }
    
    /**
     * Précharger un ensemble de tuiles
     * @param tileIds Liste des IDs de tuiles à précharger
     */
    public void preloadTiles(int[] tileIds) {
        for (int tileId : tileIds) {
            loadingThreadPool.submit(() -> loadDefaultTile(tileId));
        }
    }
    
    /**
     * Obtenir le chemin du sprite pour un type et un nom donnés
     * @param type Type d'entité (creature, npc, etc.)
     * @param name Nom de l'entité
     * @return Chemin du sprite
     */
    public String getSpritePath(String type, String name) {
        // Chemin de base pour les sprites
        return "src/main/resources/assets/textures/" + type + "/" + name + ".png";
    }
    
    /**
     * Obtenir le chemin de la spritesheet pour un type et un nom donnés
     * @param type Type d'entité (creature, npc, etc.)
     * @param name Nom de l'entité
     * @return Chemin de la spritesheet
     */
    public String getSpriteSheetPath(String type, String name) {
        // Chemin de base pour les spritesheets
        return "src/main/resources/assets/textures/" + type + "/" + name + "_sheet.png";
    }
    
    /**
     * Libérer les ressources
     */
    public void dispose() {
        // Arrêter le pool de threads
        loadingThreadPool.shutdown();
        
        // Libérer toutes les textures
        for (int textureId : spriteCache.values()) {
            TextureManager.getInstance().unloadTexture(textureId);
        }
        
        for (int textureId : defaultTileCache.values()) {
            TextureManager.getInstance().unloadTexture(textureId);
        }
        
        // Libérer les spritesheets
        for (SpriteSheet spriteSheet : spriteSheetCache.values()) {
            spriteSheet.dispose();
        }
        
        // Vider les caches
        spriteCache.clear();
        spriteSheetCache.clear();
        defaultTileCache.clear();
    }
}

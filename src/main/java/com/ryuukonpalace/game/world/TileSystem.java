package com.ryuukonpalace.game.world;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.ryuukonpalace.game.core.LayeredRenderer;
import com.ryuukonpalace.game.utils.SpriteLoader;

/**
 * Système de tuiles pour créer des cartes avec un effet de perspective 2.5D
 * similaire à Pokémon Blanc/Noir.
 */
public class TileSystem {
    
    private static TileSystem instance;
    
    // Dimensions des tuiles
    private int tileWidth = 32;
    private int tileHeight = 32;
    
    // Décalage pour la perspective isométrique
    // Note: isoOffsetX est conservé pour une utilisation future dans les tuiles en zigzag
    private float isoOffsetX = 0.0f;  // Utilisé pour le décalage horizontal dans certains types de tuiles
    private float isoOffsetY = 16.0f;  // Décalage vertical pour créer l'effet de perspective
    
    // Cache des textures de tuiles
    private Map<String, Integer> tileTextureCache;
    
    // Cache des tuiles rendues fréquemment (clé = "x,y,layer", valeur = ID de texture)
    private LinkedHashMap<String, Integer> renderedTileCache;
    private static final int MAX_CACHE_SIZE = 1000; // Nombre maximum d'entrées dans le cache
    
    // Statistiques de cache pour l'optimisation
    private int cacheHits = 0;
    private int cacheMisses = 0;
    
    // Données de la carte actuelle
    private int[][] terrainLayer;      // Couche de terrain de base
    private int[][] detailLayer;       // Couche de détails (herbe, fleurs, etc.)
    private int[][] objectBackLayer;   // Objets en arrière-plan
    private int[][] objectFrontLayer;  // Objets en avant-plan
    private int[][] collisionLayer;    // Informations de collision
    private int[][] heightLayer;       // Hauteur des tuiles pour l'effet 3D
    
    // Dimensions de la carte
    private int mapWidth;
    private int mapHeight;
    
    /**
     * Constructeur privé (singleton)
     */
    private TileSystem() {
        tileTextureCache = new HashMap<>();
        renderedTileCache = new LinkedHashMap<String, Integer>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }
    
    /**
     * Obtenir l'instance unique du TileSystem
     * @return L'instance du TileSystem
     */
    public static TileSystem getInstance() {
        if (instance == null) {
            instance = new TileSystem();
        }
        return instance;
    }
    
    /**
     * Définir les dimensions des tuiles
     * @param width Largeur d'une tuile
     * @param height Hauteur d'une tuile
     */
    public void setTileDimensions(int width, int height) {
        this.tileWidth = width;
        this.tileHeight = height;
    }
    
    /**
     * Définir les décalages pour la perspective isométrique
     * @param offsetX Décalage horizontal
     * @param offsetY Décalage vertical
     */
    public void setIsometricOffset(float offsetX, float offsetY) {
        this.isoOffsetX = offsetX;
        this.isoOffsetY = offsetY;
    }
    
    /**
     * Charger une carte à partir d'un fichier JSON
     * @param mapPath Chemin du fichier de carte
     * @return true si le chargement a réussi, false sinon
     */
    public boolean loadMapFromJson(String mapPath) {
        try {
            // Charger le fichier JSON
            JsonObject mapJson = JsonParser.parseReader(new FileReader(new File(mapPath))).getAsJsonObject();
            
            // Lire les dimensions de la carte
            mapWidth = mapJson.get("width").getAsInt();
            mapHeight = mapJson.get("height").getAsInt();
            
            // Initialiser les couches
            terrainLayer = new int[mapHeight][mapWidth];
            detailLayer = new int[mapHeight][mapWidth];
            objectBackLayer = new int[mapHeight][mapWidth];
            objectFrontLayer = new int[mapHeight][mapWidth];
            collisionLayer = new int[mapHeight][mapWidth];
            heightLayer = new int[mapHeight][mapWidth];
            
            // Charger les données des couches
            loadLayerData(mapJson.getAsJsonArray("terrainLayer"), terrainLayer);
            loadLayerData(mapJson.getAsJsonArray("detailLayer"), detailLayer);
            loadLayerData(mapJson.getAsJsonArray("objectBackLayer"), objectBackLayer);
            loadLayerData(mapJson.getAsJsonArray("objectFrontLayer"), objectFrontLayer);
            loadLayerData(mapJson.getAsJsonArray("collisionLayer"), collisionLayer);
            loadLayerData(mapJson.getAsJsonArray("heightLayer"), heightLayer);
            
            // Vider le cache lors du chargement d'une nouvelle carte
            clearRenderedTileCache();
            
            // Précharger les textures des tuiles si nécessaire
            // Note: Cette fonctionnalité sera utilisée lorsque les tilesets seront implémentés
            
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la carte: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Charger les données d'une couche
     * @param layerData Données de la couche au format JSON
     * @param targetLayer Tableau cible
     */
    private void loadLayerData(JsonArray layerData, int[][] targetLayer) {
        for (int y = 0; y < mapHeight; y++) {
            JsonArray row = layerData.get(y).getAsJsonArray();
            for (int x = 0; x < mapWidth; x++) {
                targetLayer[y][x] = row.get(x).getAsInt();
            }
        }
    }
    
    /**
     * Charger un tileset
     * @param tilesetName Nom du tileset
     * @param tilesetPath Chemin du fichier
     * @deprecated Cette méthode sera implémentée ultérieurement lorsque les tilesets seront ajoutés
     */
    @Deprecated
    private void loadTileset(String tilesetName, String tilesetPath) {
        // Cette méthode sera implémentée ultérieurement
        // Elle permettra de charger des tilesets à partir de fichiers image
    }
    
    /**
     * Dessiner la carte avec effet de perspective
     * @param cameraX Position X de la caméra
     * @param cameraY Position Y de la caméra
     * @param viewportWidth Largeur de la vue
     * @param viewportHeight Hauteur de la vue
     */
    public void renderMap(float cameraX, float cameraY, float viewportWidth, float viewportHeight) {
        // Obtenir l'instance du LayeredRenderer
        LayeredRenderer renderer = LayeredRenderer.getInstance();
        
        // Calculer les limites de la vue en tuiles
        int startX = Math.max(0, (int)((cameraX - viewportWidth / 2) / tileWidth) - 1);
        int endX = Math.min(mapWidth - 1, (int)((cameraX + viewportWidth / 2) / tileWidth) + 1);
        int startY = Math.max(0, (int)((cameraY - viewportHeight / 2) / tileHeight) - 1);
        int endY = Math.min(mapHeight - 1, (int)((cameraY + viewportHeight / 2) / tileHeight) + 1);
        
        // Dessiner les tuiles visibles
        // 1. Couche de terrain
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int tileId = terrainLayer[y][x];
                if (tileId != 0) {
                    int textureId = getTileTextureWithCache(tileId, x, y, 0);
                    float tileX = x * tileWidth;
                    float tileY = y * tileHeight;
                    float heightOffset = heightLayer[y][x] * isoOffsetY;
                    
                    renderer.addToLayer(
                        LayeredRenderer.Layer.TERRAIN,
                        textureId,
                        tileX,
                        tileY - heightOffset,
                        tileWidth,
                        tileHeight,
                        y + 0.1f  // Profondeur basée sur Y avec un petit décalage pour éviter les conflits
                    );
                }
            }
        }
        
        // 2. Couche de détails
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int tileId = detailLayer[y][x];
                if (tileId != 0) {
                    int textureId = getTileTextureWithCache(tileId, x, y, 1);
                    float tileX = x * tileWidth;
                    float tileY = y * tileHeight;
                    float heightOffset = heightLayer[y][x] * isoOffsetY;
                    
                    renderer.addToLayer(
                        LayeredRenderer.Layer.TERRAIN_DETAIL,
                        textureId,
                        tileX,
                        tileY - heightOffset,
                        tileWidth,
                        tileHeight,
                        y + 0.2f
                    );
                }
            }
        }
        
        // 3. Couche d'objets en arrière-plan
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int tileId = objectBackLayer[y][x];
                if (tileId != 0) {
                    int textureId = getTileTextureWithCache(tileId, x, y, 2);
                    float tileX = x * tileWidth;
                    float tileY = y * tileHeight;
                    float heightOffset = heightLayer[y][x] * isoOffsetY;
                    
                    renderer.addToLayer(
                        LayeredRenderer.Layer.OBJECT_BACK,
                        textureId,
                        tileX,
                        tileY - heightOffset,
                        tileWidth,
                        tileHeight,
                        y + 0.3f
                    );
                }
            }
        }
        
        // 4. Couche d'objets en avant-plan
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int tileId = objectFrontLayer[y][x];
                if (tileId != 0) {
                    int textureId = getTileTextureWithCache(tileId, x, y, 3);
                    float tileX = x * tileWidth;
                    float tileY = y * tileHeight;
                    float heightOffset = heightLayer[y][x] * isoOffsetY;
                    
                    renderer.addToLayer(
                        LayeredRenderer.Layer.OBJECT_FRONT,
                        textureId,
                        tileX,
                        tileY - heightOffset,
                        tileWidth,
                        tileHeight,
                        y + 0.4f
                    );
                }
            }
        }
    }
    
    /**
     * Obtenir la texture d'une tuile avec cache
     * @param tileId ID de la tuile
     * @param x Position X en tuiles
     * @param y Position Y en tuiles
     * @param layer Couche (0=terrain, 1=detail, 2=objectBack, 3=objectFront)
     * @return ID de la texture
     */
    private int getTileTextureWithCache(int tileId, int x, int y, int layer) {
        // Créer une clé unique pour cette tuile
        String cacheKey = x + "," + y + "," + layer + "," + tileId;
        
        // Vérifier si la tuile est dans le cache
        if (renderedTileCache.containsKey(cacheKey)) {
            cacheHits++;
            return renderedTileCache.get(cacheKey);
        }
        
        // Si non, obtenir la texture et l'ajouter au cache
        cacheMisses++;
        int textureId = getTileTexture(tileId);
        renderedTileCache.put(cacheKey, textureId);
        
        return textureId;
    }
    
    /**
     * Obtenir la texture d'une tuile
     * @param tileId ID de la tuile
     * @return ID de la texture
     */
    public int getTileTexture(int tileId) {
        // Convertir l'ID en String pour utiliser comme clé
        String tileKey = String.valueOf(tileId);
        
        // Vérifier si la texture est déjà dans le cache
        if (tileTextureCache.containsKey(tileKey)) {
            return tileTextureCache.get(tileKey);
        }
        
        // Si non, charger la texture et l'ajouter au cache
        // Note: Dans une implémentation réelle, il faudrait charger la texture à partir d'un tileset
        int textureId = SpriteLoader.getInstance().loadDefaultTile(tileId);
        tileTextureCache.put(tileKey, textureId);
        
        return textureId;
    }
    
    /**
     * Vérifier si une position est solide (collision)
     * @param x Position X en pixels
     * @param y Position Y en pixels
     * @return true si la position est solide, false sinon
     */
    public boolean isSolid(float x, float y) {
        // Convertir les coordonnées en indices de tuile
        int tileX = (int)(x / tileWidth);
        int tileY = (int)(y / tileHeight);
        
        // Vérifier les limites de la carte
        if (tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapHeight) {
            return true;  // En dehors de la carte = solide
        }
        
        // Vérifier la couche de collision
        return collisionLayer[tileY][tileX] != 0;
    }
    
    /**
     * Obtenir la hauteur d'une tuile
     * @param x Position X en pixels
     * @param y Position Y en pixels
     * @return Hauteur de la tuile
     */
    public int getTileHeight(float x, float y) {
        // Convertir les coordonnées en indices de tuile
        int tileX = (int)(x / tileWidth);
        int tileY = (int)(y / tileHeight);
        
        // Vérifier les limites de la carte
        if (tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapHeight) {
            return 0;
        }
        
        // Retourner la hauteur de la tuile
        return heightLayer[tileY][tileX];
    }
    
    /**
     * Créer une carte vide
     * @param width Largeur de la carte en tuiles
     * @param height Hauteur de la carte en tuiles
     */
    public void createEmptyMap(int width, int height) {
        mapWidth = width;
        mapHeight = height;
        
        // Initialiser les couches
        terrainLayer = new int[mapHeight][mapWidth];
        detailLayer = new int[mapHeight][mapWidth];
        objectBackLayer = new int[mapHeight][mapWidth];
        objectFrontLayer = new int[mapHeight][mapWidth];
        collisionLayer = new int[mapHeight][mapWidth];
        heightLayer = new int[mapHeight][mapWidth];
    }
    
    /**
     * Définir une tuile dans une couche
     * @param layer Couche (0=terrain, 1=detail, 2=objectBack, 3=objectFront, 4=collision, 5=height)
     * @param x Position X en tuiles
     * @param y Position Y en tuiles
     * @param tileId ID de la tuile
     */
    public void setTile(int layer, int x, int y, int tileId) {
        // Vérifier les limites de la carte
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) {
            return;
        }
        
        // Définir la tuile dans la couche appropriée
        switch (layer) {
            case 0:
                terrainLayer[y][x] = tileId;
                break;
            case 1:
                detailLayer[y][x] = tileId;
                break;
            case 2:
                objectBackLayer[y][x] = tileId;
                break;
            case 3:
                objectFrontLayer[y][x] = tileId;
                break;
            case 4:
                collisionLayer[y][x] = tileId;
                break;
            case 5:
                heightLayer[y][x] = tileId;
                break;
        }
    }
    
    /**
     * Obtenir l'ID d'une tuile dans une couche
     * @param layer Couche (0=terrain, 1=detail, 2=objectBack, 3=objectFront, 4=collision, 5=height)
     * @param x Position X en tuiles
     * @param y Position Y en tuiles
     * @return ID de la tuile
     */
    public int getTile(int layer, int x, int y) {
        // Vérifier les limites de la carte
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) {
            return 0;
        }
        
        // Obtenir la tuile de la couche appropriée
        switch (layer) {
            case 0:
                return terrainLayer[y][x];
            case 1:
                return detailLayer[y][x];
            case 2:
                return objectBackLayer[y][x];
            case 3:
                return objectFrontLayer[y][x];
            case 4:
                return collisionLayer[y][x];
            case 5:
                return heightLayer[y][x];
            default:
                return 0;
        }
    }
    
    /**
     * Vider le cache des tuiles rendues
     */
    public void clearRenderedTileCache() {
        renderedTileCache.clear();
        cacheHits = 0;
        cacheMisses = 0;
    }
    
    /**
     * Obtenir les statistiques du cache
     * @return Chaîne contenant les statistiques du cache
     */
    public String getCacheStats() {
        int total = cacheHits + cacheMisses;
        float hitRate = total > 0 ? (float)cacheHits / total * 100 : 0;
        return String.format("Cache: %d hits, %d misses, %.2f%% hit rate, %d entries", 
                             cacheHits, cacheMisses, hitRate, renderedTileCache.size());
    }
}

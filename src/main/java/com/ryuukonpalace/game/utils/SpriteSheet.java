package com.ryuukonpalace.game.utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.ryuukonpalace.game.core.TextureManager;

/**
 * Classe pour gérer les spritesheets (feuilles de sprites).
 * Permet de découper une grande image en plusieurs sprites individuels.
 */
public class SpriteSheet {
    
    // Image source
    private BufferedImage sourceImage;
    
    // Dimensions d'un sprite
    private int spriteWidth;
    private int spriteHeight;
    
    // Nombre de sprites en largeur et hauteur
    private int columns;
    private int rows;
    
    // Cache des textures pour chaque sprite
    private Map<Integer, Integer> textureCache;
    
    // Cache des animations
    private Map<String, int[]> animationCache;
    
    /**
     * Constructeur
     * @param sourceImage Image source
     * @param spriteWidth Largeur d'un sprite
     * @param spriteHeight Hauteur d'un sprite
     */
    public SpriteSheet(BufferedImage sourceImage, int spriteWidth, int spriteHeight) {
        this.sourceImage = sourceImage;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        
        // Calculer le nombre de sprites en largeur et hauteur
        this.columns = sourceImage.getWidth() / spriteWidth;
        this.rows = sourceImage.getHeight() / spriteHeight;
        
        this.textureCache = new HashMap<>();
        this.animationCache = new HashMap<>();
    }
    
    /**
     * Obtenir un sprite spécifique de la spritesheet
     * @param column Colonne (0-based)
     * @param row Ligne (0-based)
     * @return ID de la texture du sprite
     */
    public int getSprite(int column, int row) {
        // Vérifier les limites
        if (column < 0 || column >= columns || row < 0 || row >= rows) {
            System.err.println("Sprite hors limites: " + column + ", " + row);
            return -1;
        }
        
        // Calculer l'index du sprite
        int spriteIndex = row * columns + column;
        
        // Vérifier si le sprite est déjà dans le cache
        if (textureCache.containsKey(spriteIndex)) {
            return textureCache.get(spriteIndex);
        }
        
        // Extraire le sprite de l'image source
        BufferedImage spriteImage = sourceImage.getSubimage(
            column * spriteWidth, 
            row * spriteHeight, 
            spriteWidth, 
            spriteHeight
        );
        
        // Charger la texture
        int textureId = TextureManager.getInstance().loadTexture(spriteImage);
        
        // Ajouter au cache
        textureCache.put(spriteIndex, textureId);
        
        return textureId;
    }
    
    /**
     * Définir une animation comme une séquence de sprites
     * @param name Nom de l'animation
     * @param frames Indices des frames (format: [col1, row1, col2, row2, ...])
     */
    public void defineAnimation(String name, int[] frames) {
        animationCache.put(name, frames);
    }
    
    /**
     * Obtenir les frames d'une animation
     * @param name Nom de l'animation
     * @return Tableau des IDs de texture pour l'animation
     */
    public int[] getAnimationFrames(String name) {
        if (!animationCache.containsKey(name)) {
            System.err.println("Animation non trouvée: " + name);
            return new int[0];
        }
        
        int[] frameIndices = animationCache.get(name);
        int[] textureIds = new int[frameIndices.length / 2];
        
        for (int i = 0; i < frameIndices.length; i += 2) {
            int col = frameIndices[i];
            int row = frameIndices[i + 1];
            textureIds[i / 2] = getSprite(col, row);
        }
        
        return textureIds;
    }
    
    /**
     * Obtenir la largeur d'un sprite
     * @return Largeur du sprite
     */
    public int getSpriteWidth() {
        return spriteWidth;
    }
    
    /**
     * Obtenir la hauteur d'un sprite
     * @return Hauteur du sprite
     */
    public int getSpriteHeight() {
        return spriteHeight;
    }
    
    /**
     * Obtenir le nombre de colonnes
     * @return Nombre de colonnes
     */
    public int getColumns() {
        return columns;
    }
    
    /**
     * Obtenir le nombre de lignes
     * @return Nombre de lignes
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * Libérer les ressources
     */
    public void dispose() {
        // Libérer toutes les textures
        for (int textureId : textureCache.values()) {
            TextureManager.getInstance().unloadTexture(textureId);
        }
        
        // Vider le cache
        textureCache.clear();
        animationCache.clear();
    }
}

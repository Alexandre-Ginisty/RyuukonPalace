package com.ryuukonpalace.game.core;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de textures pour le jeu.
 * Charge, stocke et libère les textures utilisées par le moteur de rendu.
 */
public class TextureManager {
    
    private static TextureManager instance;
    
    // Cache des textures
    private Map<Integer, Texture> textureCache;
    
    // Compteur pour générer des IDs uniques
    private int nextTextureId = 1;
    
    /**
     * Constructeur privé (singleton)
     */
    private TextureManager() {
        textureCache = new HashMap<>();
    }
    
    /**
     * Obtenir l'instance unique du TextureManager
     * @return L'instance du TextureManager
     */
    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }
    
    /**
     * Charger une texture à partir d'une image
     * @param image Image source
     * @return ID de la texture chargée
     */
    public int loadTexture(BufferedImage image) {
        // Générer un nouvel ID
        int textureId = nextTextureId++;
        
        // Créer une nouvelle texture
        Texture texture = new Texture(textureId, image);
        
        // Ajouter au cache
        textureCache.put(textureId, texture);
        
        return textureId;
    }
    
    /**
     * Obtenir une texture par son ID
     * @param textureId ID de la texture
     * @return Texture correspondante ou null si non trouvée
     */
    public Texture getTexture(int textureId) {
        return textureCache.get(textureId);
    }
    
    /**
     * Décharger une texture
     * @param textureId ID de la texture à décharger
     */
    public void unloadTexture(int textureId) {
        Texture texture = textureCache.remove(textureId);
        if (texture != null) {
            texture.dispose();
        }
    }
    
    /**
     * Décharger toutes les textures
     */
    public void unloadAllTextures() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
    }
    
    /**
     * Classe interne représentant une texture
     */
    public static class Texture {
        private int id;
        private BufferedImage image;
        private int width;
        private int height;
        
        /**
         * Constructeur
         * @param id ID de la texture
         * @param image Image source
         */
        public Texture(int id, BufferedImage image) {
            this.id = id;
            this.image = image;
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
        
        /**
         * Obtenir l'ID de la texture
         * @return ID de la texture
         */
        public int getId() {
            return id;
        }
        
        /**
         * Obtenir l'image source
         * @return Image source
         */
        public BufferedImage getImage() {
            return image;
        }
        
        /**
         * Obtenir la largeur de la texture
         * @return Largeur en pixels
         */
        public int getWidth() {
            return width;
        }
        
        /**
         * Obtenir la hauteur de la texture
         * @return Hauteur en pixels
         */
        public int getHeight() {
            return height;
        }
        
        /**
         * Libérer les ressources
         */
        public void dispose() {
            // Libérer les ressources de l'image
            if (image != null) {
                image.flush();
                image = null;
            }
        }
    }
}

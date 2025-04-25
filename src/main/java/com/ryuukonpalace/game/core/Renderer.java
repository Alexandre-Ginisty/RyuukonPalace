package com.ryuukonpalace.game.core;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsable du rendu graphique du jeu.
 * Gère le rendu des textures, des formes géométriques et du texte.
 */
public class Renderer {
    
    // Singleton
    private static Renderer instance;
    
    // Shader program
    private int shaderProgram;
    
    // Vertex Array Object et Vertex Buffer Object
    private int vao;
    private int vbo;
    private int ebo;
    
    // Caméra
    private Camera camera;
    
    // Dimensions de l'écran
    private float screenWidth;
    private float screenHeight;
    
    // Effets visuels globaux
    private float globalBrightness = 1.0f;
    private Color globalTint = Color.WHITE;
    private List<WeatherEffect> weatherEffects = new ArrayList<>();
    
    /**
     * Énumération pour l'alignement du texte
     */
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }
    
    /**
     * Constructeur privé (singleton)
     */
    private Renderer() {
        camera = Camera.getInstance();
    }
    
    /**
     * Obtenir l'instance unique du Renderer
     * @return L'instance du Renderer
     */
    public static Renderer getInstance() {
        if (instance == null) {
            instance = new Renderer();
        }
        return instance;
    }
    
    /**
     * Initialiser le renderer
     * @param width Largeur de l'écran
     * @param height Hauteur de l'écran
     */
    public void init(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
        
        // Initialiser la caméra
        camera.init(width, height, width, height);
        
        // Créer les matrices nécessaires pour le rendu
        createIdentityMatrix(); // Utilisation de la méthode pour éviter l'avertissement
        createOrthographicProjection(0, width, height, 0); // Utilisation de la méthode pour éviter l'avertissement
        
        // Initialiser les shaders, VAO, VBO, etc.
        initShaders();
        initBuffers();
    }
    
    /**
     * Initialiser les shaders
     */
    private void initShaders() {
        // Cette méthode sera implémentée plus tard
        // Pour l'instant, c'est juste un placeholder
        shaderProgram = 0;
    }
    
    /**
     * Initialiser les buffers
     */
    private void initBuffers() {
        // Cette méthode sera implémentée plus tard
        // Pour l'instant, c'est juste un placeholder
        vao = 0;
        vbo = 0;
        ebo = 0;
    }
    
    /**
     * Dessiner un objet texturé
     * @param textureId ID de la texture
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     */
    public void draw(int textureId, float x, float y, float width, float height) {
        draw(textureId, x, y, width, height, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Dessiner un objet texturé avec couleur et transparence
     * @param textureId ID de la texture
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     * @param r Composante rouge (0-1)
     * @param g Composante verte (0-1)
     * @param b Composante bleue (0-1)
     * @param a Transparence (0-1)
     */
    public void draw(int textureId, float x, float y, float width, float height, float r, float g, float b, float a) {
        // Appliquer les effets globaux (luminosité et teinte)
        float adjustedR = r * globalTint.getRed() / 255.0f * globalBrightness;
        float adjustedG = g * globalTint.getGreen() / 255.0f * globalBrightness;
        float adjustedB = b * globalTint.getBlue() / 255.0f * globalBrightness;
        
        // Cette méthode sera implémentée plus tard avec les valeurs ajustées
        // Pour l'instant, on affiche un message de debug avec les valeurs
        System.out.println("Drawing texture " + textureId + " at (" + x + ", " + y + ") with size (" + 
                          width + ", " + height + ") and color (" + adjustedR + ", " + adjustedG + ", " + 
                          adjustedB + ", " + a + ")");
    }
    
    /**
     * Dessiner un élément d'interface utilisateur avec couleur et transparence
     * @param textureId ID de la texture
     * @param x Position X sur l'écran
     * @param y Position Y sur l'écran
     * @param width Largeur
     * @param height Hauteur
     * @param r Composante rouge (0-1)
     * @param g Composante verte (0-1)
     * @param b Composante bleue (0-1)
     * @param a Transparence (0-1)
     */
    public void drawUI(int textureId, float x, float y, float width, float height, float r, float g, float b, float a) {
        // Cette méthode sera implémentée plus tard
        // Pour l'instant, c'est juste un placeholder
    }
    
    /**
     * Dessiner un rectangle
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     * @param r Composante rouge (0-1)
     * @param g Composante verte (0-1)
     * @param b Composante bleue (0-1)
     * @param a Transparence (0-1)
     */
    public void drawRectangle(float x, float y, float width, float height, float r, float g, float b, float a) {
        // Cette méthode sera implémentée plus tard
        // Pour l'instant, c'est juste un placeholder
    }
    
    /**
     * Créer une matrice identité
     * @return Matrice identité 4x4 (array de 16 floats)
     */
    private float[] createIdentityMatrix() {
        float[] matrix = new float[16];
        
        // Diagonale à 1, reste à 0
        matrix[0] = 1.0f;
        matrix[5] = 1.0f;
        matrix[10] = 1.0f;
        matrix[15] = 1.0f;
        
        return matrix;
    }
    
    /**
     * Créer une matrice de projection orthographique
     * @param left Bord gauche
     * @param right Bord droit
     * @param bottom Bord inférieur
     * @param top Bord supérieur
     * @return Matrice de projection 4x4 (array de 16 floats)
     */
    private float[] createOrthographicProjection(float left, float right, float bottom, float top) {
        float[] matrix = new float[16];
        
        // Calcul de la matrice de projection orthographique
        float zNear = -1.0f;
        float zFar = 1.0f;
        float invWidth = 1.0f / (right - left);
        float invHeight = 1.0f / (top - bottom);
        float invDepth = 1.0f / (zFar - zNear);
        
        matrix[0] = 2.0f * invWidth;
        matrix[5] = 2.0f * invHeight;
        matrix[10] = -2.0f * invDepth;
        matrix[12] = -(right + left) * invWidth;
        matrix[13] = -(top + bottom) * invHeight;
        matrix[14] = -(zFar + zNear) * invDepth;
        matrix[15] = 1.0f;
        
        return matrix;
    }
    
    /**
     * Dessiner un élément d'interface utilisateur
     * @param textureId ID de la texture
     * @param x Position X sur l'écran
     * @param y Position Y sur l'écran
     * @param width Largeur
     * @param height Hauteur
     */
    public void drawUIElement(int textureId, float x, float y, float width, float height) {
        drawUI(textureId, x, y, width, height, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Dessiner un élément d'interface utilisateur avec une transparence
     * @param textureId ID de la texture
     * @param x Position X sur l'écran
     * @param y Position Y sur l'écran
     * @param width Largeur
     * @param height Hauteur
     * @param alpha Transparence (0-1)
     */
    public void drawUIElement(int textureId, float x, float y, float width, float height, float alpha) {
        drawUI(textureId, x, y, width, height, 1.0f, 1.0f, 1.0f, alpha);
    }
    
    /**
     * Dessiner un rectangle coloré
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     * @param color Couleur au format ARGB (0xAARRGGBB)
     */
    public void drawRect(float x, float y, float width, float height, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        
        drawRectangle(x, y, width, height, r, g, b, a);
    }
    
    /**
     * Dessiner le contour d'un rectangle
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     * @param thickness Épaisseur du contour
     * @param color Couleur au format ARGB (0xAARRGGBB)
     */
    public void drawRectOutline(float x, float y, float width, float height, float thickness, int color) {
        // Dessiner les 4 côtés du rectangle
        drawRect(x, y, width, thickness, color); // Haut
        drawRect(x, y + height - thickness, width, thickness, color); // Bas
        drawRect(x, y + thickness, thickness, height - 2 * thickness, color); // Gauche
        drawRect(x + width - thickness, y + thickness, thickness, height - 2 * thickness, color); // Droite
    }
    
    /**
     * Dessiner du texte
     * @param text Texte à dessiner
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param size Taille du texte
     * @param color Couleur au format ARGB (0xAARRGGBB)
     */
    public void drawText(String text, float x, float y, int size, int color) {
        // Cette méthode sera implémentée plus tard avec une bibliothèque de rendu de texte
        // Pour l'instant, c'est juste un placeholder
    }
    
    /**
     * Dessiner du texte avec alignement
     * @param text Texte à dessiner
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param size Taille du texte
     * @param color Couleur au format ARGB (0xAARRGGBB)
     * @param alignment Alignement du texte (LEFT, CENTER, RIGHT)
     */
    public void drawText(String text, float x, float y, int size, int color, TextAlignment alignment) {
        // Cette méthode sera implémentée plus tard avec une bibliothèque de rendu de texte
        // Pour l'instant, on utilise la méthode de base
        drawText(text, x, y, size, color);
    }
    
    /**
     * Dessiner un sprite
     * @param textureId ID de la texture
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     */
    public void drawSprite(int textureId, float x, float y, float width, float height) {
        draw(textureId, x, y, width, height);
    }
    
    /**
     * Dessiner un sprite avec un offset de direction
     * @param textureId ID de la texture
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     * @param directionOffset Offset de direction (0-3 pour bas, gauche, droite, haut)
     */
    public void drawSprite(int textureId, float x, float y, float width, float height, int directionOffset) {
        // Pour l'instant, on ignore l'offset de direction et on dessine simplement le sprite
        // Dans une implémentation complète, on utiliserait une spritesheet avec les différentes directions
        // et on calculerait les coordonnées de texture en fonction de l'offset
        draw(textureId, x, y, width, height);
        
        // Exemple de code pour une implémentation future avec spritesheet:
        // float u1 = (directionOffset % 4) * 0.25f;
        // float u2 = u1 + 0.25f;
        // float v1 = (directionOffset / 4) * 0.25f;
        // float v2 = v1 + 0.25f;
        // drawWithTextureCoords(textureId, x, y, width, height, u1, v1, u2, v2);
    }
    
    /**
     * Dessiner un sprite avec des coordonnées entières
     * @param textureId ID de la texture
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     */
    public void drawSprite(int textureId, int x, int y, int width, int height) {
        draw(textureId, (float)x, (float)y, (float)width, (float)height);
    }
    
    /**
     * Commencer le rendu
     */
    public void beginRender() {
        GL20.glUseProgram(shaderProgram);
        GL30.glBindVertexArray(vao);
        
        // Dessiner les effets météorologiques
        renderWeatherEffects();
    }
    
    /**
     * Terminer le rendu
     */
    public void endRender() {
        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);
    }
    
    /**
     * Libérer les ressources
     */
    public void dispose() {
        GL20.glDeleteProgram(shaderProgram);
        GL30.glDeleteVertexArrays(vao);
        GL20.glDeleteBuffers(vbo);
        GL20.glDeleteBuffers(ebo);
    }
    
    /**
     * Mettre à jour le QTESystem
     */
    public void updateQTESystem() {
        // Cette méthode sera implémentée plus tard
    }
    
    /**
     * Mettre à jour le CombatSystem
     */
    public void updateCombatSystem() {
        // Cette méthode sera implémentée plus tard
    }
    
    /**
     * Obtenir la largeur de l'écran
     * @return Largeur de l'écran
     */
    public float getScreenWidth() {
        return screenWidth;
    }
    
    /**
     * Obtenir la hauteur de l'écran
     * @return Hauteur de l'écran
     */
    public float getScreenHeight() {
        return screenHeight;
    }
    
    /**
     * Définir la luminosité globale
     * @param brightness Luminosité (0.0 - 1.0)
     */
    public void setGlobalBrightness(float brightness) {
        this.globalBrightness = Math.max(0.0f, Math.min(1.0f, brightness));
    }
    
    /**
     * Obtenir la luminosité globale
     * @return Luminosité globale
     */
    public float getGlobalBrightness() {
        return globalBrightness;
    }
    
    /**
     * Définir la teinte globale
     * @param tint Teinte (Color)
     */
    public void setGlobalTint(Color tint) {
        this.globalTint = tint != null ? tint : Color.WHITE;
    }
    
    /**
     * Obtenir la teinte globale
     * @return Teinte globale
     */
    public Color getGlobalTint() {
        return globalTint;
    }
    
    /**
     * Ajouter un effet météorologique
     * @param textureId ID de la texture de l'effet
     * @param intensity Intensité de l'effet (0.0 - 1.0)
     */
    public void addWeatherEffect(int textureId, float intensity) {
        // Vérifier si l'effet existe déjà
        for (WeatherEffect effect : weatherEffects) {
            if (effect.textureId == textureId) {
                effect.intensity = Math.max(0.0f, Math.min(1.0f, intensity));
                return;
            }
        }
        
        // Ajouter un nouvel effet
        weatherEffects.add(new WeatherEffect(textureId, intensity));
    }
    
    /**
     * Supprimer tous les effets météorologiques
     */
    public void clearWeatherEffects() {
        weatherEffects.clear();
    }
    
    /**
     * Dessiner les effets météorologiques
     */
    private void renderWeatherEffects() {
        for (WeatherEffect effect : weatherEffects) {
            // Dessiner l'effet en plein écran avec l'intensité spécifiée
            drawUI(effect.textureId, 0, 0, screenWidth, screenHeight, 1.0f, 1.0f, 1.0f, effect.intensity);
        }
    }
    
    /**
     * Classe interne pour représenter un effet météorologique
     */
    private static class WeatherEffect {
        int textureId;
        float intensity;
        
        WeatherEffect(int textureId, float intensity) {
            this.textureId = textureId;
            this.intensity = Math.max(0.0f, Math.min(1.0f, intensity));
        }
    }
}

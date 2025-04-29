package com.ryuukonpalace.game.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Système de rendu par couches pour créer un effet 2.5D similaire à Pokémon Blanc.
 * Utilise le Renderer de base en ajoutant la gestion de plusieurs couches de profondeur.
 */
public class LayeredRenderer {
    
    private static LayeredRenderer instance;
    
    // Instance du renderer de base
    private Renderer baseRenderer;
    
    // Couches de rendu (du plus loin au plus proche)
    public enum Layer {
        BACKGROUND,    // Arrière-plan (ciel, montagnes lointaines)
        TERRAIN,       // Terrain de base (sol, eau)
        TERRAIN_DETAIL, // Détails du terrain (herbe, fleurs)
        OBJECT_BACK,   // Objets en arrière-plan (derrière le joueur)
        CHARACTER,     // Personnages et créatures
        OBJECT_FRONT,  // Objets en avant-plan (devant le joueur)
        OVERLAY,       // Effets spéciaux, particules
        UI             // Interface utilisateur
    }
    
    // Éléments à dessiner pour chaque couche
    private Map<Layer, List<RenderElement>> renderLayers;
    
    // Décalage Y pour simuler la perspective (plus la couche est éloignée, plus elle est haute)
    private Map<Layer, Float> layerYOffset;
    
    // Facteur d'échelle pour simuler la perspective (plus la couche est éloignée, plus elle est petite)
    private Map<Layer, Float> layerScale;
    
    /**
     * Constructeur privé (singleton)
     */
    private LayeredRenderer() {
        this.baseRenderer = Renderer.getInstance();
        renderLayers = new HashMap<>();
        layerYOffset = new HashMap<>();
        layerScale = new HashMap<>();
        
        // Initialiser les couches
        for (Layer layer : Layer.values()) {
            renderLayers.put(layer, new ArrayList<>());
        }
        
        // Configurer les décalages Y et échelles par défaut pour la perspective
        layerYOffset.put(Layer.BACKGROUND, 0.0f);
        layerYOffset.put(Layer.TERRAIN, 0.0f);
        layerYOffset.put(Layer.TERRAIN_DETAIL, 0.0f);
        layerYOffset.put(Layer.OBJECT_BACK, -5.0f);
        layerYOffset.put(Layer.CHARACTER, 0.0f);
        layerYOffset.put(Layer.OBJECT_FRONT, 5.0f);
        layerYOffset.put(Layer.OVERLAY, 0.0f);
        layerYOffset.put(Layer.UI, 0.0f);
        
        layerScale.put(Layer.BACKGROUND, 1.0f);
        layerScale.put(Layer.TERRAIN, 1.0f);
        layerScale.put(Layer.TERRAIN_DETAIL, 1.0f);
        layerScale.put(Layer.OBJECT_BACK, 1.0f);
        layerScale.put(Layer.CHARACTER, 1.0f);
        layerScale.put(Layer.OBJECT_FRONT, 1.0f);
        layerScale.put(Layer.OVERLAY, 1.0f);
        layerScale.put(Layer.UI, 1.0f);
    }
    
    /**
     * Obtenir l'instance unique du LayeredRenderer
     * @return L'instance du LayeredRenderer
     */
    public static LayeredRenderer getInstance() {
        if (instance == null) {
            instance = new LayeredRenderer();
        }
        return instance;
    }
    
    /**
     * Définir le décalage Y pour une couche
     * @param layer Couche
     * @param offset Décalage Y
     */
    public void setLayerYOffset(Layer layer, float offset) {
        layerYOffset.put(layer, offset);
    }
    
    /**
     * Définir l'échelle pour une couche
     * @param layer Couche
     * @param scale Échelle
     */
    public void setLayerScale(Layer layer, float scale) {
        layerScale.put(layer, scale);
    }
    
    /**
     * Ajouter un élément à dessiner dans une couche spécifique
     * @param layer Couche
     * @param textureId ID de la texture
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param depth Profondeur dans la couche (pour le tri)
     */
    public void addToLayer(Layer layer, int textureId, float x, float y, float width, float height, float depth) {
        addToLayer(layer, textureId, x, y, width, height, 1.0f, 1.0f, 1.0f, 1.0f, depth);
    }
    
    /**
     * Ajouter un élément à dessiner dans une couche spécifique avec couleur et transparence
     * @param layer Couche
     * @param textureId ID de la texture
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param r Composante rouge (0-1)
     * @param g Composante verte (0-1)
     * @param b Composante bleue (0-1)
     * @param a Transparence (0-1)
     * @param depth Profondeur dans la couche (pour le tri)
     */
    public void addToLayer(Layer layer, int textureId, float x, float y, float width, float height, 
                           float r, float g, float b, float a, float depth) {
        RenderElement element = new RenderElement(textureId, x, y, width, height, r, g, b, a, depth);
        renderLayers.get(layer).add(element);
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
        addToLayer(Layer.UI, textureId, x, y, width, height, 0);
    }
    
    /**
     * Dessiner un sprite avec effet de perspective
     * @param layer Couche
     * @param textureId ID de la texture
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     */
    public void drawSpriteWithPerspective(Layer layer, int textureId, float x, float y, float width, float height) {
        // Appliquer le décalage Y et l'échelle de la couche
        float yOffset = layerYOffset.get(layer);
        float scale = layerScale.get(layer);
        
        // La profondeur est basée sur la position Y (plus Y est grand, plus l'objet est proche)
        float depth = y;
        
        // Ajouter à la couche avec les ajustements de perspective
        addToLayer(
            layer, 
            textureId, 
            x, 
            y + yOffset, 
            width * scale, 
            height * scale, 
            depth
        );
    }
    
    /**
     * Dessiner un bâtiment avec effet de perspective
     * @param textureId ID de la texture
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param baseHeight Hauteur de la base du bâtiment (partie au sol)
     */
    public void drawBuilding(int textureId, float x, float y, float width, float height, float baseHeight) {
        // La base du bâtiment est sur la couche OBJECT_BACK
        // Le reste du bâtiment est sur la couche OBJECT_FRONT si le joueur est derrière
        
        // Calculer la profondeur basée sur la position Y
        float depth = y;
        
        // Dessiner la base du bâtiment (toujours visible)
        addToLayer(
            Layer.OBJECT_BACK, 
            textureId, 
            x, 
            y, 
            width, 
            baseHeight, 
            depth
        );
        
        // Dessiner le reste du bâtiment (peut être caché par le joueur)
        addToLayer(
            Layer.OBJECT_FRONT, 
            textureId, 
            x, 
            y - (height - baseHeight), 
            width, 
            height - baseHeight, 
            depth
        );
    }
    
    /**
     * Dessiner toutes les couches
     */
    public void renderLayers() {
        // Trier les éléments de chaque couche par profondeur
        for (Layer layer : Layer.values()) {
            List<RenderElement> elements = renderLayers.get(layer);
            elements.sort((a, b) -> Float.compare(a.depth, b.depth));
        }
        
        // Dessiner les couches dans l'ordre (du plus loin au plus proche)
        for (Layer layer : Layer.values()) {
            for (RenderElement element : renderLayers.get(layer)) {
                baseRenderer.draw(
                    element.textureId, 
                    element.x, 
                    element.y, 
                    element.width, 
                    element.height, 
                    element.r, 
                    element.g, 
                    element.b, 
                    element.a
                );
            }
        }
        
        // Vider les couches après le rendu
        for (Layer layer : Layer.values()) {
            renderLayers.get(layer).clear();
        }
    }
    
    /**
     * Initialiser le renderer avec les dimensions de l'écran
     * @param width Largeur de l'écran
     * @param height Hauteur de l'écran
     */
    public void init(float width, float height) {
        // Déléguer à l'instance de base
        baseRenderer.init(width, height);
    }
    
    /**
     * Commencer le rendu
     */
    public void beginRender() {
        baseRenderer.beginRender();
    }
    
    /**
     * Terminer le rendu
     */
    public void endRender() {
        baseRenderer.endRender();
    }
    
    /**
     * Classe interne pour représenter un élément à dessiner
     */
    private static class RenderElement {
        int textureId;
        float x, y, width, height;
        float r, g, b, a;
        float depth;
        
        RenderElement(int textureId, float x, float y, float width, float height, 
                     float r, float g, float b, float a, float depth) {
            this.textureId = textureId;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.depth = depth;
        }
    }
}

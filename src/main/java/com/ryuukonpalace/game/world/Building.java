package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.core.LayeredRenderer;
import com.ryuukonpalace.game.utils.SpriteLoader;

/**
 * Classe représentant un bâtiment avec effet de perspective 2.5D.
 * Permet de créer des bâtiments similaires à ceux de Pokémon Blanc/Noir.
 */
public class Building {
    
    // Position du bâtiment
    private float x, y;
    
    // Dimensions du bâtiment
    private float width, height;
    
    // Hauteur de la base du bâtiment (partie au sol)
    private float baseHeight;
    
    // ID de la texture
    private int textureId;
    
    // Zone d'entrée du bâtiment
    private float entranceX, entranceY, entranceWidth;
    
    // Nom du bâtiment
    private String name;
    
    // Type de bâtiment (maison, magasin, centre Pokémon, etc.)
    private String type;
    
    /**
     * Constructeur
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param texturePath Chemin de la texture
     */
    public Building(float x, float y, float width, float height, String texturePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.baseHeight = height * 0.2f;  // Par défaut, 20% de la hauteur totale
        
        // Charger la texture
        this.textureId = SpriteLoader.getInstance().loadSprite(texturePath);
        
        // Définir l'entrée par défaut (centrée en bas)
        this.entranceX = x + width / 2 - width / 6;
        this.entranceY = y + height - baseHeight;
        this.entranceWidth = width / 3;
    }
    
    /**
     * Définir la hauteur de la base
     * @param baseHeight Hauteur de la base
     */
    public void setBaseHeight(float baseHeight) {
        this.baseHeight = baseHeight;
    }
    
    /**
     * Définir l'entrée du bâtiment
     * @param entranceX Position X de l'entrée
     * @param entranceY Position Y de l'entrée
     * @param entranceWidth Largeur de l'entrée
     */
    public void setEntrance(float entranceX, float entranceY, float entranceWidth) {
        this.entranceX = entranceX;
        this.entranceY = entranceY;
        this.entranceWidth = entranceWidth;
    }
    
    /**
     * Définir le nom du bâtiment
     * @param name Nom du bâtiment
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Définir le type du bâtiment
     * @param type Type du bâtiment
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Obtenir le nom du bâtiment
     * @return Nom du bâtiment
     */
    public String getName() {
        return name;
    }
    
    /**
     * Obtenir le type du bâtiment
     * @return Type du bâtiment
     */
    public String getType() {
        return type;
    }
    
    /**
     * Vérifier si un point est à l'entrée du bâtiment
     * @param pointX Position X du point
     * @param pointY Position Y du point
     * @return true si le point est à l'entrée, false sinon
     */
    public boolean isAtEntrance(float pointX, float pointY) {
        return pointX >= entranceX && pointX <= entranceX + entranceWidth &&
               pointY >= entranceY - 10 && pointY <= entranceY + 10;
    }
    
    /**
     * Vérifier si un point est en collision avec le bâtiment
     * @param pointX Position X du point
     * @param pointY Position Y du point
     * @return true si le point est en collision, false sinon
     */
    public boolean isColliding(float pointX, float pointY) {
        // Si le point est à l'entrée, pas de collision
        if (isAtEntrance(pointX, pointY)) {
            return false;
        }
        
        // Sinon, vérifier si le point est dans le bâtiment
        return pointX >= x && pointX <= x + width &&
               pointY >= y && pointY <= y + height;
    }
    
    /**
     * Dessiner le bâtiment avec effet de perspective
     * @param cameraX Position X de la caméra
     * @param cameraY Position Y de la caméra
     */
    public void render(float cameraX, float cameraY) {
        // Calculer la position relative à la caméra
        float screenX = x - cameraX;
        float screenY = y - cameraY;
        
        // Obtenir le renderer
        LayeredRenderer renderer = LayeredRenderer.getInstance();
        
        // Dessiner la base du bâtiment (toujours visible)
        renderer.addToLayer(
            LayeredRenderer.Layer.OBJECT_BACK,
            textureId,
            screenX,
            screenY + height - baseHeight,
            width,
            baseHeight,
            y  // Profondeur basée sur la position Y
        );
        
        // Dessiner le reste du bâtiment (peut être devant ou derrière le joueur)
        renderer.addToLayer(
            LayeredRenderer.Layer.OBJECT_FRONT,
            textureId,
            screenX,
            screenY,
            width,
            height - baseHeight,
            y - 1  // Profondeur légèrement inférieure pour que la base soit visible
        );
    }
    
    /**
     * Créer un bâtiment de type maison
     * @param x Position X
     * @param y Position Y
     * @return Bâtiment créé
     */
    public static Building createHouse(float x, float y) {
        Building house = new Building(x, y, 128, 160, "src/main/resources/images/buildings/house.png");
        house.setBaseHeight(32);
        house.setName("Maison");
        house.setType("house");
        return house;
    }
    
    /**
     * Créer un bâtiment de type magasin
     * @param x Position X
     * @param y Position Y
     * @return Bâtiment créé
     */
    public static Building createShop(float x, float y) {
        Building shop = new Building(x, y, 160, 192, "src/main/resources/images/buildings/shop.png");
        shop.setBaseHeight(40);
        shop.setName("Magasin");
        shop.setType("shop");
        return shop;
    }
    
    /**
     * Créer un bâtiment de type centre Pokémon (ou centre de variants dans votre jeu)
     * @param x Position X
     * @param y Position Y
     * @return Bâtiment créé
     */
    public static Building createVariantCenter(float x, float y) {
        Building center = new Building(x, y, 192, 224, "src/main/resources/images/buildings/variant_center.png");
        center.setBaseHeight(48);
        center.setName("Centre de Variants");
        center.setType("variant_center");
        return center;
    }
}

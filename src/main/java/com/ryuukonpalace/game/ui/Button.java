package com.ryuukonpalace.game.ui;

import java.util.function.Consumer;

import com.ryuukonpalace.game.utils.ResourceManager;

/**
 * Composant de bouton pour l'interface utilisateur.
 * Permet de déclencher une action lorsqu'il est cliqué.
 */
public class Button extends UIComponent {
    // Texte du bouton
    private String text;
    
    // Couleurs
    private int normalColor;
    private int hoverColor;
    private int pressedColor;
    private int disabledColor;
    private int textColor;
    
    // Textures
    private int normalTextureId;
    private int hoverTextureId;
    private int pressedTextureId;
    private int disabledTextureId;
    
    // Action à exécuter lors du clic
    private Consumer<Button> action;
    
    // Taille du texte
    private int textSize;
    
    // Tag pour stocker des données supplémentaires
    private Object tag;
    
    /**
     * Constructeur avec textures
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param text Texte du bouton
     * @param normalTexture Chemin de la texture normale
     * @param hoverTexture Chemin de la texture au survol
     * @param pressedTexture Chemin de la texture appuyée
     * @param disabledTexture Chemin de la texture désactivée
     * @param action Action à exécuter lors du clic
     */
    public Button(float x, float y, float width, float height, String text,
                 String normalTexture, String hoverTexture, String pressedTexture, String disabledTexture,
                 Consumer<Button> action) {
        super(x, y, width, height);
        this.text = text;
        this.action = action;
        this.textSize = 16;
        this.textColor = 0xFFFFFFFF; // Blanc
        
        // Charger les textures
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.normalTextureId = resourceManager.loadTexture(normalTexture, "btn_normal_" + text);
        this.hoverTextureId = resourceManager.loadTexture(hoverTexture, "btn_hover_" + text);
        this.pressedTextureId = resourceManager.loadTexture(pressedTexture, "btn_pressed_" + text);
        this.disabledTextureId = resourceManager.loadTexture(disabledTexture, "btn_disabled_" + text);
    }
    
    /**
     * Constructeur avec couleurs
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param text Texte du bouton
     * @param normalColor Couleur normale
     * @param hoverColor Couleur au survol
     * @param pressedColor Couleur appuyée
     * @param disabledColor Couleur désactivée
     * @param textColor Couleur du texte
     * @param action Action à exécuter lors du clic
     */
    public Button(float x, float y, float width, float height, String text,
                 int normalColor, int hoverColor, int pressedColor, int disabledColor, int textColor,
                 Consumer<Button> action) {
        super(x, y, width, height);
        this.text = text;
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;
        this.disabledColor = disabledColor;
        this.textColor = textColor;
        this.action = action;
        this.textSize = 16;
        
        // Pas de textures
        this.normalTextureId = -1;
        this.hoverTextureId = -1;
        this.pressedTextureId = -1;
        this.disabledTextureId = -1;
    }
    
    /**
     * Constructeur avec couleurs (paramètres int)
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param text Texte du bouton
     */
    public Button(int x, int y, int width, int height, String text) {
        this((float)x, (float)y, (float)width, (float)height, text, 
             0xFF4A4A4A, 0xFF5A5A5A, 0xFF3A3A3A, 0xFF2A2A2A, 0xFFFFFFFF, null);
    }
    
    @Override
    public void update(float deltaTime) {
        // Rien à faire pour l'instant
    }
    
    @Override
    public void render() {
        if (!visible) {
            return;
        }
        
        // Déterminer la texture ou la couleur à utiliser
        if (normalTextureId != -1) {
            // Rendu avec textures
            int textureId = normalTextureId;
            
            if (!enabled) {
                textureId = disabledTextureId;
            } else if (pressed) {
                textureId = pressedTextureId;
            } else if (hovered) {
                textureId = hoverTextureId;
            }
            
            renderer.drawUIElement(textureId, x, y, width, height);
        } else {
            // Rendu avec couleurs
            int color = normalColor;
            
            if (!enabled) {
                color = disabledColor;
            } else if (pressed) {
                color = pressedColor;
            } else if (hovered) {
                color = hoverColor;
            }
            
            renderer.drawRect(x, y, width, height, color);
        }
        
        // Dessiner le texte
        if (text != null && !text.isEmpty()) {
            // Calculer la position du texte pour le centrer
            float textX = x + width / 2; // Centre horizontal
            float textY = y + height / 2; // Centre vertical
            
            renderer.drawText(text, textX, textY, textSize, textColor);
        }
    }
    
    @Override
    protected void handleClick() {
        if (action != null && enabled) {
            action.accept(this);
        }
    }
    
    /**
     * Gérer les entrées utilisateur
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param mousePressed true si le bouton de la souris est appuyé, false sinon
     * @return true si l'entrée a été traitée, false sinon
     */
    public boolean handleInput(float mouseX, float mouseY, boolean mousePressed) {
        // Mettre à jour l'état de survol
        onHover(mouseX, mouseY);
        
        // Gérer l'appui
        boolean handled = onPress(mouseX, mouseY, mousePressed);
        
        // Si le bouton est relâché et qu'il était survolé, déclencher le clic
        if (!mousePressed && hovered && pressed) {
            onClick(mouseX, mouseY);
            handled = true;
        }
        
        return handled;
    }
    
    // Getters et setters supplémentaires
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getTextSize() {
        return textSize;
    }
    
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
    
    public int getTextColor() {
        return textColor;
    }
    
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    
    /**
     * Définir la position du bouton
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Définir la couleur de fond du bouton (état normal)
     * 
     * @param color Couleur de fond
     */
    public void setBackgroundColor(int color) {
        this.normalColor = color;
    }
    
    public void setAction(Consumer<Button> action) {
        this.action = action;
    }
    
    /**
     * Définir le tag du bouton (données supplémentaires)
     * 
     * @param tag Objet à associer au bouton
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }
    
    /**
     * Obtenir le tag du bouton
     * 
     * @return Tag associé au bouton
     */
    public Object getTag() {
        return tag;
    }
    
    /**
     * Vérifier si un point est à l'intérieur du bouton
     * 
     * @param x Position X du point
     * @param y Position Y du point
     * @return true si le point est à l'intérieur du bouton, false sinon
     */
    public boolean contains(float x, float y) {
        return this.x <= x && x <= this.x + this.width &&
               this.y <= y && y <= this.y + this.height;
    }
    
    /**
     * Vérifier si le bouton est cliqué
     * 
     * @return true si le bouton est cliqué, false sinon
     */
    public boolean isClicked() {
        return pressed;
    }
}

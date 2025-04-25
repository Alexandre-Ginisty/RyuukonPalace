package com.ryuukonpalace.game.ui;

/**
 * Composant d'étiquette pour l'interface utilisateur.
 * Affiche du texte.
 */
public class Label extends UIComponent {
    // Texte
    private String text;
    
    // Couleur du texte
    private int textColor;
    
    // Taille du texte
    private int textSize;
    
    // Alignement horizontal (0 = gauche, 1 = centre, 2 = droite)
    private int horizontalAlignment;
    
    // Alignement vertical (0 = haut, 1 = centre, 2 = bas)
    private int verticalAlignment;
    
    // Couleur de fond
    private int backgroundColor;
    
    // Indique si le fond doit être dessiné
    private boolean drawBackground;
    
    /**
     * Constructeur
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param text Texte
     * @param textColor Couleur du texte
     */
    public Label(float x, float y, float width, float height, String text, int textColor) {
        super(x, y, width, height);
        this.text = text;
        this.textColor = textColor;
        this.textSize = 16;
        this.horizontalAlignment = 1; // Centre par défaut
        this.verticalAlignment = 1; // Centre par défaut
        this.backgroundColor = 0x00000000; // Transparent
        this.drawBackground = false;
    }
    
    /**
     * Constructeur avec taille de texte
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param text Texte
     * @param textColor Couleur du texte
     * @param textSize Taille du texte
     */
    public Label(float x, float y, float width, float height, String text, int textColor, int textSize) {
        this(x, y, width, height, text, textColor);
        this.textSize = textSize;
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
        
        // Dessiner le fond si nécessaire
        if (drawBackground) {
            renderer.drawRect(x, y, width, height, backgroundColor);
        }
        
        // Dessiner le texte
        if (text != null && !text.isEmpty()) {
            // Calculer la position du texte selon l'alignement
            float textX = x;
            float textY = y;
            
            // Alignement horizontal
            switch (horizontalAlignment) {
                case 0: // Gauche
                    textX = x;
                    break;
                case 1: // Centre
                    textX = x + width / 2;
                    break;
                case 2: // Droite
                    textX = x + width;
                    break;
            }
            
            // Alignement vertical
            switch (verticalAlignment) {
                case 0: // Haut
                    textY = y;
                    break;
                case 1: // Centre
                    textY = y + height / 2;
                    break;
                case 2: // Bas
                    textY = y + height;
                    break;
            }
            
            renderer.drawText(text, textX, textY, textSize, textColor);
        }
    }
    
    @Override
    protected void handleClick() {
        // L'étiquette ne fait rien quand on clique dessus
    }
    
    // Getters et setters
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getTextColor() {
        return textColor;
    }
    
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    
    public int getTextSize() {
        return textSize;
    }
    
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
    
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }
    
    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = Math.max(0, Math.min(2, horizontalAlignment));
    }
    
    public int getVerticalAlignment() {
        return verticalAlignment;
    }
    
    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = Math.max(0, Math.min(2, verticalAlignment));
    }
    
    public int getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public boolean isDrawBackground() {
        return drawBackground;
    }
    
    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }
}

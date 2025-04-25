package com.ryuukonpalace.game.ui;

/**
 * Composant de barre de progression pour l'interface utilisateur.
 * Affiche une barre qui représente une valeur entre un minimum et un maximum.
 */
public class ProgressBar extends UIComponent {
    // Valeurs
    private float minValue;
    private float maxValue;
    private float currentValue;
    
    // Couleurs
    private int backgroundColor;
    private int fillColor;
    private int borderColor;
    
    // Bordure
    private boolean hasBorder;
    private float borderThickness;
    
    // Orientation (true = horizontal, false = vertical)
    private boolean horizontal;
    
    // Texte
    private boolean showText;
    private String format;
    private int textColor;
    private int textSize;
    
    /**
     * Constructeur
     * 
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param minValue Valeur minimale
     * @param maxValue Valeur maximale
     * @param currentValue Valeur actuelle
     * @param backgroundColor Couleur de fond
     * @param fillColor Couleur de remplissage
     */
    public ProgressBar(float x, float y, float width, float height, 
                      float minValue, float maxValue, float currentValue,
                      int backgroundColor, int fillColor) {
        super(x, y, width, height);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = currentValue;
        this.backgroundColor = backgroundColor;
        this.fillColor = fillColor;
        this.borderColor = 0xFF000000; // Noir
        this.hasBorder = true;
        this.borderThickness = 1.0f;
        this.horizontal = true;
        this.showText = false;
        this.format = "%.0f / %.0f";
        this.textColor = 0xFFFFFFFF; // Blanc
        this.textSize = 12;
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
        
        // Dessiner le fond
        renderer.drawRect(x, y, width, height, backgroundColor);
        
        // Calculer la taille de la barre de progression
        float progress = (currentValue - minValue) / (maxValue - minValue);
        progress = Math.max(0.0f, Math.min(1.0f, progress));
        
        // Dessiner la barre de progression
        if (horizontal) {
            float fillWidth = width * progress;
            renderer.drawRect(x, y, fillWidth, height, fillColor);
        } else {
            float fillHeight = height * progress;
            renderer.drawRect(x, y + (height - fillHeight), width, fillHeight, fillColor);
        }
        
        // Dessiner la bordure
        if (hasBorder && borderThickness > 0) {
            renderer.drawRectOutline(x, y, width, height, borderThickness, borderColor);
        }
        
        // Dessiner le texte
        if (showText) {
            String text = String.format(format, currentValue, maxValue);
            float textX = x + width / 2;
            float textY = y + height / 2;
            renderer.drawText(text, textX, textY, textSize, textColor);
        }
    }
    
    @Override
    protected void handleClick() {
        // La barre de progression ne fait rien quand on clique dessus
    }
    
    /**
     * Définir la valeur actuelle
     * 
     * @param value Nouvelle valeur
     */
    public void setValue(float value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
    }
    
    /**
     * Obtenir la valeur actuelle
     * 
     * @return Valeur actuelle
     */
    public float getValue() {
        return currentValue;
    }
    
    /**
     * Définir la valeur minimale
     * 
     * @param minValue Nouvelle valeur minimale
     */
    public void setMinValue(float minValue) {
        this.minValue = minValue;
        this.currentValue = Math.max(minValue, Math.min(maxValue, currentValue));
    }
    
    /**
     * Obtenir la valeur minimale
     * 
     * @return Valeur minimale
     */
    public float getMinValue() {
        return minValue;
    }
    
    /**
     * Définir la valeur maximale
     * 
     * @param maxValue Nouvelle valeur maximale
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        this.currentValue = Math.max(minValue, Math.min(maxValue, currentValue));
    }
    
    /**
     * Obtenir la valeur maximale
     * 
     * @return Valeur maximale
     */
    public float getMaxValue() {
        return maxValue;
    }
    
    /**
     * Définir la couleur de fond
     * 
     * @param backgroundColor Nouvelle couleur de fond
     */
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    /**
     * Définir la couleur de remplissage
     * 
     * @param fillColor Nouvelle couleur de remplissage
     */
    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }
    
    /**
     * Définir si la barre a une bordure
     * 
     * @param hasBorder true si la barre a une bordure, false sinon
     */
    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }
    
    /**
     * Définir la couleur de la bordure
     * 
     * @param borderColor Nouvelle couleur de bordure
     */
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }
    
    /**
     * Définir l'épaisseur de la bordure
     * 
     * @param borderThickness Nouvelle épaisseur de bordure
     */
    public void setBorderThickness(float borderThickness) {
        this.borderThickness = borderThickness;
    }
    
    /**
     * Définir l'orientation de la barre
     * 
     * @param horizontal true pour horizontale, false pour verticale
     */
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }
    
    /**
     * Définir si le texte doit être affiché
     * 
     * @param showText true pour afficher le texte, false sinon
     */
    public void setShowText(boolean showText) {
        this.showText = showText;
    }
    
    /**
     * Définir le format du texte
     * 
     * @param format Format du texte (ex: "%.0f / %.0f")
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    /**
     * Définir la couleur du texte
     * 
     * @param textColor Nouvelle couleur de texte
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    
    /**
     * Définir la taille du texte
     * 
     * @param textSize Nouvelle taille de texte
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}

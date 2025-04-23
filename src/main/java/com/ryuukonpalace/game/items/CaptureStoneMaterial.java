package com.ryuukonpalace.game.items;

/**
 * Énumération des matériaux possibles pour les pierres de capture.
 * Chaque matériau a des propriétés différentes qui affectent les chances de capture.
 */
public enum CaptureStoneMaterial {
    QUARTZ(1.0f, "Quartz", "Un matériau commun offrant des chances de capture standard."),
    AMETHYST(1.2f, "Améthyste", "Un cristal violet qui améliore légèrement les chances de capture."),
    SAPPHIRE(1.4f, "Saphir", "Une pierre précieuse bleue qui offre de bonnes chances de capture."),
    RUBY(1.6f, "Rubis", "Une pierre précieuse rouge qui offre d'excellentes chances de capture."),
    EMERALD(1.8f, "Émeraude", "Une pierre précieuse verte qui offre des chances de capture supérieures."),
    DIAMOND(2.0f, "Diamant", "Le matériau le plus rare et le plus efficace pour les pierres de capture.");
    
    // Multiplicateur de chance de capture
    private final float captureRateMultiplier;
    
    // Nom du matériau
    private final String name;
    
    // Description du matériau
    private final String description;
    
    /**
     * Constructeur
     * 
     * @param captureRateMultiplier Multiplicateur de chance de capture
     * @param name Nom du matériau
     * @param description Description du matériau
     */
    CaptureStoneMaterial(float captureRateMultiplier, String name, String description) {
        this.captureRateMultiplier = captureRateMultiplier;
        this.name = name;
        this.description = description;
    }
    
    /**
     * Obtenir le multiplicateur de chance de capture
     * 
     * @return Multiplicateur de chance de capture
     */
    public float getCaptureRateMultiplier() {
        return captureRateMultiplier;
    }
    
    /**
     * Obtenir le nom du matériau
     * 
     * @return Nom du matériau
     */
    public String getName() {
        return name;
    }
    
    /**
     * Obtenir la description du matériau
     * 
     * @return Description du matériau
     */
    public String getDescription() {
        return description;
    }
}

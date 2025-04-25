package com.ryuukonpalace.game.items;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureType;
import com.ryuukonpalace.game.utils.ResourceManager;

/**
 * Pierre de capture utilisée pour capturer et stocker les créatures.
 * Le joueur utilise des signes avec ses mains (QTE) pour activer la pierre et capturer une créature.
 */
public class CaptureStone extends Item {
    
    // Matériau de la pierre
    private CaptureStoneMaterial material;
    
    // Type de la pierre (peut affecter l'efficacité selon le type de créature)
    private CaptureStoneType type;
    
    // Créature stockée dans la pierre (null si vide)
    private Creature capturedCreature;
    
    // Indique si la pierre est active (contient une créature)
    private boolean active;
    
    // Bonus de capture spécifique à cette pierre
    private float captureBonus;
    
    /**
     * Constructeur pour une pierre vide
     * 
     * @param material Matériau de la pierre
     * @param type Type de la pierre
     */
    public CaptureStone(CaptureStoneMaterial material, CaptureStoneType type) {
        super(
            0, // ID temporaire, à remplacer par un ID unique
            material.getName() + " " + type.getName() + " Pierre de Capture",
            "Une pierre de " + material.getName() + " qui résonne avec l'énergie " + type.getDescription(),
            calculateValue(material, type),
            loadTextureForStone(material, type),
            getRarityForMaterial(material)
        );
        
        this.material = material;
        this.type = type;
        this.capturedCreature = null;
        this.active = false;
        this.captureBonus = 0.0f;
    }
    
    /**
     * Constructeur pour une pierre avec une créature
     * 
     * @param material Matériau de la pierre
     * @param type Type de la pierre
     * @param creature Créature capturée
     */
    public CaptureStone(CaptureStoneMaterial material, CaptureStoneType type, Creature creature) {
        this(material, type);
        this.capturedCreature = creature;
        this.active = true;
    }
    
    /**
     * Constructeur avec nom et description personnalisés
     * 
     * @param id ID de l'objet
     * @param name Nom de la pierre
     * @param description Description de la pierre
     * @param value Valeur de la pierre
     * @param material Matériau de la pierre
     * @param type Type de la pierre
     */
    public CaptureStone(int id, String name, String description, int value, CaptureStoneMaterial material, CaptureStoneType type) {
        super(id, name, description, value, loadTextureForStone(material, type), getRarityForMaterial(material));
        this.material = material;
        this.type = type;
        this.capturedCreature = null;
        this.active = false;
        this.captureBonus = 0.0f;
    }
    
    /**
     * Constructeur avec nom et description personnalisés (pour compatibilité)
     * 
     * @param name Nom de la pierre
     * @param description Description de la pierre
     * @param textureId ID de la texture
     * @param material Matériau de la pierre
     * @param type Type de la pierre
     */
    public CaptureStone(String name, String description, int textureId, CaptureStoneMaterial material, CaptureStoneType type) {
        super(0, name, description, calculateValue(material, type), textureId, getRarityForMaterial(material));
        this.material = material;
        this.type = type;
        this.capturedCreature = null;
        this.active = false;
        this.captureBonus = 0.0f;
    }
    
    /**
     * Obtenir le matériau de la pierre
     * 
     * @return Matériau de la pierre
     */
    public CaptureStoneMaterial getMaterial() {
        return material;
    }
    
    /**
     * Obtenir le type de la pierre
     * 
     * @return Type de la pierre
     */
    public CaptureStoneType getType() {
        return type;
    }
    
    /**
     * Obtenir la créature capturée
     * 
     * @return Créature capturée, ou null si la pierre est vide
     */
    public Creature getCapturedCreature() {
        return capturedCreature;
    }
    
    /**
     * Vérifier si la pierre est active (contient une créature)
     * 
     * @return true si la pierre contient une créature, false sinon
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Définir si la pierre est active
     * 
     * @param active État d'activation de la pierre
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Obtenir le bonus de capture
     * 
     * @return Bonus de capture
     */
    public float getCaptureBonus() {
        return captureBonus;
    }
    
    /**
     * Définir le bonus de capture
     * 
     * @param captureBonus Nouveau bonus de capture
     */
    public void setCaptureBonus(float captureBonus) {
        this.captureBonus = captureBonus;
    }
    
    /**
     * Capturer une créature dans la pierre
     * 
     * @param creature Créature à capturer
     * @return true si la capture a réussi, false sinon
     */
    public boolean captureCreature(Creature creature) {
        // Vérifier si la pierre est déjà utilisée
        if (active) {
            return false;
        }
        
        // Stocker la créature
        this.capturedCreature = creature;
        this.active = true;
        
        return true;
    }
    
    /**
     * Libérer la créature de la pierre
     * 
     * @return Créature libérée, ou null si la pierre était vide
     */
    public Creature releaseCreature() {
        if (!active) {
            return null;
        }
        
        Creature released = capturedCreature;
        capturedCreature = null;
        active = false;
        
        return released;
    }
    
    /**
     * Calculer le multiplicateur de capture total
     * 
     * @param creatureType Type de la créature à capturer
     * @return Multiplicateur de capture total
     */
    public float getTotalCaptureMultiplier(CreatureType creatureType) {
        float materialMultiplier = material.getCaptureRateMultiplier();
        float typeMultiplier = type.getTypeEffectiveness(creatureType);
        
        return materialMultiplier * typeMultiplier * (1.0f + captureBonus);
    }
    
    @Override
    public boolean use() {
        // L'utilisation d'une pierre de capture dépend du contexte
        // Dans un combat, elle tente de capturer une créature
        // En dehors d'un combat, elle invoque la créature capturée
        
        // Cette méthode sera implémentée par le système qui utilise la pierre
        return false;
    }
    
    /**
     * Charger la texture appropriée pour la pierre
     * 
     * @param material Matériau de la pierre
     * @param type Type de la pierre
     * @return ID de la texture
     */
    private static int loadTextureForStone(CaptureStoneMaterial material, CaptureStoneType type) {
        ResourceManager resourceManager = ResourceManager.getInstance();
        String texturePath = "src/main/resources/images/items/capture_stones/" + 
                             material.name().toLowerCase() + "_" + 
                             type.name().toLowerCase() + ".png";
        
        String textureKey = "capture_stone_" + material.name().toLowerCase() + "_" + type.name().toLowerCase();
        
        // Vérifier si la texture existe déjà
        int textureId = resourceManager.getTextureId(textureKey);
        if (textureId == -1) {
            // Charger la texture
            textureId = resourceManager.loadTexture(texturePath, textureKey);
        }
        
        return textureId;
    }
    
    /**
     * Déterminer la rareté en fonction du matériau
     * 
     * @param material Matériau de la pierre
     * @return Rareté correspondante
     */
    private static ItemRarity getRarityForMaterial(CaptureStoneMaterial material) {
        switch (material) {
            case QUARTZ:
                return ItemRarity.COMMON;
            case AMETHYST:
                return ItemRarity.UNCOMMON;
            case SAPPHIRE:
            case RUBY:
                return ItemRarity.RARE;
            case EMERALD:
                return ItemRarity.EPIC;
            case DIAMOND:
                return ItemRarity.LEGENDARY;
            default:
                return ItemRarity.COMMON;
        }
    }
    
    /**
     * Calculer la valeur de la pierre en fonction du matériau et du type
     * 
     * @param material Matériau de la pierre
     * @param type Type de la pierre
     * @return Valeur de la pierre
     */
    private static int calculateValue(CaptureStoneMaterial material, CaptureStoneType type) {
        int baseValue = 0;
        
        // Valeur de base selon le matériau
        switch (material) {
            case QUARTZ:
                baseValue = 100;
                break;
            case AMETHYST:
                baseValue = 250;
                break;
            case SAPPHIRE:
            case RUBY:
                baseValue = 500;
                break;
            case EMERALD:
                baseValue = 1000;
                break;
            case DIAMOND:
                baseValue = 2000;
                break;
            default:
                baseValue = 50;
        }
        
        // Multiplicateur selon le type
        float typeMultiplier = 1.0f;
        switch (type) {
            case NEUTRAL:
                typeMultiplier = 1.0f;
                break;
            case FIRE:
            case WATER:
            case EARTH:
            case AIR:
                typeMultiplier = 1.5f;
                break;
            case LIGHT:
                typeMultiplier = 2.0f;
                break;
            case DARK:
                typeMultiplier = 2.5f;
                break;
            default:
                typeMultiplier = 1.0f;
        }
        
        return (int)(baseValue * typeMultiplier);
    }
}

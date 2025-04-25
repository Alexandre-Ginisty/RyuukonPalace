package com.ryuukonpalace.game.data;

import com.ryuukonpalace.game.creatures.Ability;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.CaptureStone;
import com.ryuukonpalace.game.items.CaptureStoneMaterial;
import com.ryuukonpalace.game.items.CaptureStoneType;

/**
 * Factory pour créer des objets de jeu à partir des définitions de données.
 * Cette classe fait le lien entre les données JSON et les objets du jeu.
 */
public class GameDataFactory {
    private static GameDataFactory instance;
    private final DataManager dataManager;
    
    /**
     * Constructeur privé (singleton)
     */
    private GameDataFactory() {
        dataManager = DataManager.getInstance();
    }
    
    /**
     * Obtenir l'instance unique de la factory
     * 
     * @return Instance de GameDataFactory
     */
    public static synchronized GameDataFactory getInstance() {
        if (instance == null) {
            instance = new GameDataFactory();
        }
        return instance;
    }
    
    /**
     * Créer une créature à partir de sa définition
     * 
     * @param creatureId ID de la créature à créer
     * @param level Niveau de la créature (peut être différent de celui de la définition)
     * @return Créature créée, ou null si la définition n'existe pas
     */
    public Creature createCreature(int creatureId, int level) {
        CreatureDefinition definition = dataManager.getCreatureDefinition(creatureId);
        if (definition == null) {
            return null;
        }
        
        // Créer la créature avec les statistiques de base
        Creature creature = new Creature(
                definition.id,
                definition.name,
                definition.type,
                level,
                scaleStatByLevel(definition.baseStats.health, level),
                scaleStatByLevel(definition.baseStats.attack, level),
                scaleStatByLevel(definition.baseStats.defense, level),
                scaleStatByLevel(definition.baseStats.speed, level)
        );
        
        // Ajouter les capacités que la créature devrait avoir à ce niveau
        for (CreatureDefinition.AbilityDefinition abilityDef : definition.abilities) {
            if (abilityDef.levelLearned <= level) {
                Ability ability = createAbility(abilityDef);
                if (ability != null) {
                    creature.addAbility(ability);
                }
            }
        }
        
        return creature;
    }
    
    /**
     * Créer une capacité à partir de sa définition
     * 
     * @param abilityDef Définition de la capacité
     * @return Capacité créée
     */
    private Ability createAbility(CreatureDefinition.AbilityDefinition abilityDef) {
        // Utilisation du constructeur avec des valeurs par défaut pour les paramètres manquants
        return new Ability(
                abilityDef.id,
                abilityDef.name,
                abilityDef.description,
                abilityDef.type,
                abilityDef.power,
                abilityDef.accuracy,
                10, // maxUses par défaut
                Ability.EffectType.NONE, // effectType par défaut
                0, // effectChance par défaut
                Ability.DamageType.PHYSICAL // damageType par défaut
        );
    }
    
    /**
     * Créer une pierre de capture à partir des paramètres
     * 
     * @param material Matériau de la pierre
     * @param type Type de la pierre
     * @return Pierre de capture créée
     */
    public CaptureStone createCaptureStone(CaptureStoneMaterial material, CaptureStoneType type) {
        // Générer un ID unique pour la pierre
        int id = generateUniqueItemId();
        
        // Créer le nom et la description
        String name = material.getName() + " " + type.getName();
        String description = "Une pierre de capture " + material.getDescription() + " " + type.getDescription() + ".";
        
        // Calculer la valeur en fonction de la rareté
        int value = calculateCaptureStoneValue(material, type);
        
        return new CaptureStone(id, name, description, value, material, type);
    }
    
    /**
     * Calculer la valeur d'une pierre de capture en fonction de sa rareté
     * 
     * @param material Matériau de la pierre
     * @param type Type de la pierre
     * @return Valeur de la pierre
     */
    private int calculateCaptureStoneValue(CaptureStoneMaterial material, CaptureStoneType type) {
        // Base value
        int baseValue = 100;
        
        // Multiplicateur selon le matériau
        double materialMultiplier = material.getCaptureRateMultiplier() * 2;
        
        // Valeur finale
        return (int)(baseValue * materialMultiplier);
    }
    
    /**
     * Générer un ID unique pour un nouvel objet
     * 
     * @return ID unique
     */
    private int generateUniqueItemId() {
        // Dans une implémentation réelle, cela pourrait utiliser une base de données
        // ou un système plus sophistiqué pour garantir l'unicité
        return (int)(System.currentTimeMillis() % 1000000);
    }
    
    /**
     * Mettre à l'échelle une statistique en fonction du niveau
     * 
     * @param baseStat Statistique de base (niveau 1)
     * @param level Niveau cible
     * @return Statistique mise à l'échelle
     */
    private int scaleStatByLevel(int baseStat, int level) {
        // Formule simple pour l'échelle : baseStat + (level - 1) * (baseStat / 10)
        return baseStat + (level - 1) * (baseStat / 10);
    }
}

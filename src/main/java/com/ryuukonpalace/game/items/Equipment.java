package com.ryuukonpalace.game.items;

import com.ryuukonpalace.game.combat.CombatStats;
import com.ryuukonpalace.game.combat.StatusEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Représente un équipement qui peut être porté par un variant ou un personnage.
 * Les équipements peuvent modifier les statistiques de combat et offrir des effets spéciaux.
 */
public class Equipment {
    
    // Types d'équipement
    public enum EquipmentType {
        WEAPON,         // Arme
        ARMOR,          // Armure
        HELMET,         // Casque
        BOOTS,          // Bottes
        ACCESSORY,      // Accessoire
        RELIC           // Relique (objet ancien avec des pouvoirs spéciaux)
    }
    
    // Rareté de l'équipement
    public enum Rarity {
        COMMON,         // Commun
        UNCOMMON,       // Peu commun
        RARE,           // Rare
        EPIC,           // Épique
        LEGENDARY,      // Légendaire
        MYTHIC          // Mythique
    }
    
    private String name;
    private String description;
    private EquipmentType type;
    private Rarity rarity;
    private int level;
    private int textureId;
    
    // Bonus de statistiques
    private Map<String, Integer> statBonuses;
    
    // Bonus de pourcentage
    private Map<String, Float> percentBonuses;
    
    // Effets spéciaux
    private List<SpecialEffect> specialEffects;
    
    // Effets de statut que cet équipement peut infliger
    private Map<StatusEffect, Float> statusEffectChances;
    
    /**
     * Constructeur pour un équipement simple
     */
    public Equipment(String name, String description, EquipmentType type, Rarity rarity, int level) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.rarity = rarity;
        this.level = level;
        
        this.statBonuses = new HashMap<>();
        this.percentBonuses = new HashMap<>();
        this.specialEffects = new ArrayList<>();
        this.statusEffectChances = new HashMap<>();
    }
    
    /**
     * Ajouter un bonus de statistique
     * 
     * @param statName Nom de la statistique (ex: "physicalAttack", "magicalDefense")
     * @param value Valeur du bonus
     */
    public void addStatBonus(String statName, int value) {
        statBonuses.put(statName, statBonuses.getOrDefault(statName, 0) + value);
    }
    
    /**
     * Ajouter un bonus de pourcentage
     * 
     * @param bonusName Nom du bonus (ex: "lifeSteal", "criticalChance")
     * @param value Valeur du bonus en pourcentage
     */
    public void addPercentBonus(String bonusName, float value) {
        percentBonuses.put(bonusName, percentBonuses.getOrDefault(bonusName, 0f) + value);
    }
    
    /**
     * Ajouter un effet spécial
     * 
     * @param effect L'effet spécial à ajouter
     */
    public void addSpecialEffect(SpecialEffect effect) {
        specialEffects.add(effect);
    }
    
    /**
     * Ajouter une chance d'infliger un effet de statut
     * 
     * @param effect L'effet de statut
     * @param chance La chance en pourcentage (0-100)
     */
    public void addStatusEffectChance(StatusEffect effect, float chance) {
        statusEffectChances.put(effect, chance);
    }
    
    /**
     * Appliquer les bonus de cet équipement aux statistiques de combat
     * 
     * @param stats Les statistiques à modifier
     */
    public void applyBonuses(CombatStats stats) {
        // Appliquer les bonus de statistiques
        for (Map.Entry<String, Integer> entry : statBonuses.entrySet()) {
            String stat = entry.getKey();
            int value = entry.getValue();
            
            switch (stat) {
                case "maxHealth":
                    stats.setMaxHealth(stats.getMaxHealth() + value);
                    break;
                case "physicalAttack":
                    stats.setPhysicalAttack(stats.getPhysicalAttack() + value);
                    break;
                case "magicalAttack":
                    stats.setMagicalAttack(stats.getMagicalAttack() + value);
                    break;
                case "physicalDefense":
                    stats.setPhysicalDefense(stats.getPhysicalDefense() + value);
                    break;
                case "magicalDefense":
                    stats.setMagicalDefense(stats.getMagicalDefense() + value);
                    break;
                case "speed":
                    stats.setSpeed(stats.getSpeed() + value);
                    break;
                case "accuracy":
                    stats.setAccuracy(stats.getAccuracy() + value);
                    break;
                case "evasion":
                    stats.setEvasion(stats.getEvasion() + value);
                    break;
            }
        }
        
        // Appliquer les bonus de pourcentage
        for (Map.Entry<String, Float> entry : percentBonuses.entrySet()) {
            String bonus = entry.getKey();
            float value = entry.getValue();
            
            switch (bonus) {
                case "criticalChance":
                    stats.setCriticalChance(stats.getCriticalChance() + value);
                    break;
                case "criticalDamage":
                    stats.setCriticalDamage(stats.getCriticalDamage() + value);
                    break;
                case "lifeSteal":
                    stats.setLifeSteal(stats.getLifeSteal() + value);
                    break;
                case "spellVamp":
                    stats.setSpellVamp(stats.getSpellVamp() + value);
                    break;
                case "omnivamp":
                    stats.setOmnivamp(stats.getOmnivamp() + value);
                    break;
                case "tenacity":
                    stats.setTenacity(stats.getTenacity() + value);
                    break;
                case "armorPenetration":
                    stats.setArmorPenetration(stats.getArmorPenetration() + value);
                    break;
                case "magicPenetration":
                    stats.setMagicPenetration(stats.getMagicPenetration() + value);
                    break;
                case "healingBonus":
                    stats.setHealingBonus(stats.getHealingBonus() + value);
                    break;
                case "shieldStrength":
                    stats.setShieldStrength(stats.getShieldStrength() + value);
                    break;
            }
        }
    }
    
    /**
     * Vérifier si un effet de statut doit être appliqué
     * 
     * @param effect L'effet de statut à vérifier
     * @return true si l'effet doit être appliqué, false sinon
     */
    public boolean shouldApplyStatusEffect(StatusEffect effect) {
        if (statusEffectChances.containsKey(effect)) {
            float chance = statusEffectChances.get(effect);
            return Math.random() * 100 < chance;
        }
        return false;
    }
    
    /**
     * Activer les effets spéciaux de l'équipement
     * 
     * @param wearer Le porteur de l'équipement
     * @param target La cible (peut être null pour les effets sans cible)
     */
    public void activateSpecialEffects(CombatStats wearer, CombatStats target) {
        for (SpecialEffect effect : specialEffects) {
            effect.activate(wearer, target);
        }
    }
    
    // Getters et setters
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public EquipmentType getType() {
        return type;
    }
    
    public Rarity getRarity() {
        return rarity;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getTextureId() {
        return textureId;
    }
    
    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }
    
    public Map<String, Integer> getStatBonuses() {
        return statBonuses;
    }
    
    public Map<String, Float> getPercentBonuses() {
        return percentBonuses;
    }
    
    public List<SpecialEffect> getSpecialEffects() {
        return specialEffects;
    }
    
    public Map<StatusEffect, Float> getStatusEffectChances() {
        return statusEffectChances;
    }
    
    /**
     * Classe interne représentant un effet spécial d'équipement
     */
    public static abstract class SpecialEffect {
        private String name;
        private String description;
        
        public SpecialEffect(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        /**
         * Activer l'effet spécial
         * 
         * @param wearer Le porteur de l'équipement
         * @param target La cible (peut être null)
         */
        public abstract void activate(CombatStats wearer, CombatStats target);
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

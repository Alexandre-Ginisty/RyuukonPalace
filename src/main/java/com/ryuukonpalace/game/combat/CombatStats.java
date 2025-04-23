package com.ryuukonpalace.game.combat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Classe représentant les statistiques de combat d'un variant ou d'un personnage.
 * Inspirée des systèmes de MOBA comme League of Legends, avec des mécaniques avancées.
 */
public class CombatStats {
    
    // Statistiques de base
    private int maxHealth;           // Points de vie maximum
    private int currentHealth;       // Points de vie actuels
    private int physicalAttack;      // Attaque physique
    private int magicalAttack;       // Attaque magique
    private int physicalDefense;     // Défense physique
    private int magicalDefense;      // Défense magique
    private int speed;               // Vitesse (détermine l'ordre d'action)
    private int accuracy;            // Précision (chances de toucher)
    private int evasion;             // Esquive (chances d'éviter une attaque)
    
    // Statistiques avancées
    private float criticalChance;    // Chances de coup critique (%)
    private float criticalDamage;    // Multiplicateur de dégâts critiques
    private float lifeSteal;         // Vol de vie (% des dégâts physiques convertis en santé)
    private float spellVamp;         // Vampirisme de sorts (% des dégâts magiques convertis en santé)
    private float omnivamp;          // Omnivampirisme (% de TOUS les dégâts convertis en santé, physiques et magiques)
    private float tenacity;          // Ténacité (réduction de la durée des effets de contrôle)
    private float armorPenetration;  // Pénétration d'armure (ignore % de la défense physique)
    private float magicPenetration;  // Pénétration magique (ignore % de la défense magique)
    private float healingBonus;      // Bonus aux soins reçus (%)
    private float shieldStrength;    // Force des boucliers (%)
    
    // Boucliers et protections temporaires
    private int physicalShield;      // Bouclier contre les dégâts physiques
    private int magicalShield;       // Bouclier contre les dégâts magiques
    private int adaptiveShield;      // Bouclier adaptatif (absorbe tous types de dégâts)
    
    // Gestion des temps de récupération (cooldowns)
    private int[] skillCooldowns;    // Nombre de tours restants avant de pouvoir réutiliser chaque capacité
    
    // Gestion des effets de statut
    private Map<StatusEffect, Integer> statusEffects; // Effets de statut actifs et leur durée restante
    
    /**
     * Constructeur avec les statistiques de base
     */
    public CombatStats(int maxHealth, int physicalAttack, int magicalAttack, 
                       int physicalDefense, int magicalDefense, int speed) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.physicalAttack = physicalAttack;
        this.magicalAttack = magicalAttack;
        this.physicalDefense = physicalDefense;
        this.magicalDefense = magicalDefense;
        this.speed = speed;
        this.accuracy = 95;          // 95% de précision par défaut
        this.evasion = 5;            // 5% d'esquive par défaut
        
        // Valeurs par défaut pour les statistiques avancées
        this.criticalChance = 5.0f;  // 5% de chances de critique
        this.criticalDamage = 1.5f;  // 150% de dégâts sur critique
        this.lifeSteal = 0.0f;       // 0% de vol de vie par défaut
        this.spellVamp = 0.0f;       // 0% de vampirisme de sorts par défaut
        this.omnivamp = 0.0f;        // 0% d'omnivampirisme par défaut
        this.tenacity = 0.0f;        // 0% de ténacité
        this.armorPenetration = 0.0f; // 0% de pénétration d'armure
        this.magicPenetration = 0.0f; // 0% de pénétration magique
        this.healingBonus = 0.0f;    // 0% de bonus aux soins
        this.shieldStrength = 0.0f;  // 0% de bonus aux boucliers
        
        // Initialisation des cooldowns (4 capacités par défaut)
        this.skillCooldowns = new int[4];
        
        // Initialisation des effets de statut
        this.statusEffects = new HashMap<>();
    }
    
    /**
     * Infliger des dégâts à cette entité
     * 
     * @param damage Quantité de dégâts à infliger
     */
    public void takeDamage(int damage) {
        // Appliquer les boucliers d'abord
        int remainingDamage = damage;
        
        // Bouclier adaptatif (tous types de dégâts)
        if (adaptiveShield > 0) {
            int absorbedDamage = Math.min(adaptiveShield, remainingDamage);
            adaptiveShield -= absorbedDamage;
            remainingDamage -= absorbedDamage;
        }
        
        // Si des dégâts restent, réduire les points de vie
        if (remainingDamage > 0) {
            currentHealth = Math.max(0, currentHealth - remainingDamage);
        }
    }
    
    /**
     * Infliger des dégâts physiques à cette entité
     * 
     * @param damage Quantité de dégâts physiques à infliger
     */
    public void takePhysicalDamage(int damage) {
        // Appliquer les boucliers physiques d'abord
        int remainingDamage = damage;
        
        if (physicalShield > 0) {
            int absorbedDamage = Math.min(physicalShield, remainingDamage);
            physicalShield -= absorbedDamage;
            remainingDamage -= absorbedDamage;
        }
        
        // Puis le bouclier adaptatif
        if (remainingDamage > 0 && adaptiveShield > 0) {
            int absorbedDamage = Math.min(adaptiveShield, remainingDamage);
            adaptiveShield -= absorbedDamage;
            remainingDamage -= absorbedDamage;
        }
        
        // Si des dégâts restent, réduire les points de vie
        if (remainingDamage > 0) {
            currentHealth = Math.max(0, currentHealth - remainingDamage);
        }
    }
    
    /**
     * Infliger des dégâts magiques à cette entité
     * 
     * @param damage Quantité de dégâts magiques à infliger
     */
    public void takeMagicalDamage(int damage) {
        // Appliquer les boucliers magiques d'abord
        int remainingDamage = damage;
        
        if (magicalShield > 0) {
            int absorbedDamage = Math.min(magicalShield, remainingDamage);
            magicalShield -= absorbedDamage;
            remainingDamage -= absorbedDamage;
        }
        
        // Puis le bouclier adaptatif
        if (remainingDamage > 0 && adaptiveShield > 0) {
            int absorbedDamage = Math.min(adaptiveShield, remainingDamage);
            adaptiveShield -= absorbedDamage;
            remainingDamage -= absorbedDamage;
        }
        
        // Si des dégâts restent, réduire les points de vie
        if (remainingDamage > 0) {
            currentHealth = Math.max(0, currentHealth - remainingDamage);
        }
    }
    
    /**
     * Infliger des dégâts vrais à cette entité (ignorent les défenses et boucliers)
     * 
     * @param damage Quantité de dégâts vrais à infliger
     */
    public void takeTrueDamage(int damage) {
        currentHealth = Math.max(0, currentHealth - damage);
    }
    
    /**
     * Soigner cette entité
     * 
     * @param amount Quantité de points de vie à restaurer
     */
    public void heal(int amount) {
        // Appliquer le bonus de soins
        int actualHeal = (int)(amount * (1 + healingBonus / 100.0f));
        currentHealth = Math.min(maxHealth, currentHealth + actualHeal);
    }
    
    /**
     * Ajouter un bouclier physique
     * 
     * @param amount Quantité de bouclier à ajouter
     */
    public void addPhysicalShield(int amount) {
        // Appliquer le bonus de force de bouclier
        int actualShield = (int)(amount * (1 + shieldStrength / 100.0f));
        physicalShield += actualShield;
    }
    
    /**
     * Ajouter un bouclier magique
     * 
     * @param amount Quantité de bouclier à ajouter
     */
    public void addMagicalShield(int amount) {
        // Appliquer le bonus de force de bouclier
        int actualShield = (int)(amount * (1 + shieldStrength / 100.0f));
        magicalShield += actualShield;
    }
    
    /**
     * Ajouter un bouclier adaptatif
     * 
     * @param amount Quantité de bouclier à ajouter
     */
    public void addAdaptiveShield(int amount) {
        // Appliquer le bonus de force de bouclier
        int actualShield = (int)(amount * (1 + shieldStrength / 100.0f));
        adaptiveShield += actualShield;
    }
    
    /**
     * Ajouter un effet de statut
     * 
     * @param effect L'effet à ajouter
     * @param duration La durée en nombre de tours
     */
    public void addStatusEffect(StatusEffect effect, int duration) {
        // Si l'effet est déjà présent, prendre la durée la plus longue
        if (statusEffects.containsKey(effect)) {
            int currentDuration = statusEffects.get(effect);
            statusEffects.put(effect, Math.max(currentDuration, duration));
        } else {
            // Limiter la durée à la durée maximale de l'effet
            int maxDuration = effect.getMaxDuration();
            statusEffects.put(effect, Math.min(duration, maxDuration));
        }
        
        // Appliquer les effets immédiats selon le type d'effet
        applyStatusEffectImpact(effect, true);
    }
    
    /**
     * Retirer un effet de statut
     * 
     * @param effect L'effet à retirer
     */
    public void removeStatusEffect(StatusEffect effect) {
        if (statusEffects.containsKey(effect)) {
            // Annuler les effets avant de retirer
            applyStatusEffectImpact(effect, false);
            statusEffects.remove(effect);
        }
    }
    
    /**
     * Vérifier si un effet de statut est actif
     * 
     * @param effect L'effet à vérifier
     * @return true si l'effet est actif, false sinon
     */
    public boolean hasStatusEffect(StatusEffect effect) {
        return statusEffects.containsKey(effect);
    }
    
    /**
     * Obtenir la durée restante d'un effet de statut
     * 
     * @param effect L'effet à vérifier
     * @return La durée restante, ou 0 si l'effet n'est pas actif
     */
    public int getStatusEffectDuration(StatusEffect effect) {
        return statusEffects.getOrDefault(effect, 0);
    }
    
    /**
     * Vérifier si la créature a au moins un effet de statut actif
     * 
     * @return true si au moins un effet de statut est actif, false sinon
     */
    public boolean hasAnyStatusEffect() {
        return !statusEffects.isEmpty();
    }
    
    /**
     * Mettre à jour tous les effets de statut (réduire leur durée)
     */
    public void updateStatusEffects() {
        Iterator<Map.Entry<StatusEffect, Integer>> iterator = statusEffects.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<StatusEffect, Integer> entry = iterator.next();
            StatusEffect effect = entry.getKey();
            int duration = entry.getValue() - 1;
            
            if (duration <= 0) {
                // L'effet expire, annuler ses impacts
                applyStatusEffectImpact(effect, false);
                iterator.remove();
            } else {
                entry.setValue(duration);
            }
        }
    }
    
    /**
     * Appliquer ou annuler les impacts immédiats d'un effet de statut
     * 
     * @param effect L'effet à appliquer/annuler
     * @param apply true pour appliquer, false pour annuler
     */
    private void applyStatusEffectImpact(StatusEffect effect, boolean apply) {
        // Multiplicateur: 1 pour appliquer, -1 pour annuler
        int multiplier = apply ? 1 : -1;
        
        switch (effect) {
            case NONE:
                // Aucun effet
                break;
                
            case CONFUS:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case DÉSORIENTÉ:
                // -30% précision
                accuracy -= (int)(30 * multiplier);
                break;
                
            case PRÉVISIBLE:
                // -30% esquive
                evasion -= (int)(30 * multiplier);
                break;
                
            case ENRAGÉ:
                // +30% ATT, -20% DEF
                physicalAttack += (int)(physicalAttack * 0.3f * multiplier);
                physicalDefense -= (int)(physicalDefense * 0.2f * multiplier);
                break;
                
            case ÉPUISÉ:
                // -40% vitesse
                speed -= (int)(speed * 0.4f * multiplier);
                break;
                
            case MAUDIT:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case POSSÉDÉ:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case SURCHARGÉ:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case DYSFONCTIONNEL:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case MÉDITATIF:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case DÉSÉQUILIBRÉ:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case INSTABLE:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case ENTROPIQUE:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case PRÉMONITIF:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case TEMPORELLEMENT_DÉCALÉ:
                // -50% vitesse
                speed -= (int)(speed * 0.5f * multiplier);
                break;
                
            case ASSOMBRI:
                // -30% ATT
                physicalAttack -= (int)(physicalAttack * 0.3f * multiplier);
                break;
                
            case CONSUMÉ:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case ILLUMINÉ:
                // +20% coups critiques
                criticalChance += (20 * multiplier);
                break;
                
            case AVEUGLANT:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case ENRACINÉ:
                // +30% DEF
                physicalDefense += (int)(physicalDefense * 0.3f * multiplier);
                magicalDefense += (int)(magicalDefense * 0.3f * multiplier);
                break;
                
            case SYMBIOTIQUE:
                // +15% à toutes les stats
                physicalAttack += (int)(physicalAttack * 0.15f * multiplier);
                magicalAttack += (int)(magicalAttack * 0.15f * multiplier);
                physicalDefense += (int)(physicalDefense * 0.15f * multiplier);
                magicalDefense += (int)(magicalDefense * 0.15f * multiplier);
                speed += (int)(speed * 0.15f * multiplier);
                break;
                
            case GELÉ:
                // -50% vitesse
                speed -= (int)(speed * 0.5f * multiplier);
                break;
                
            case ENCHANTÉ:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
                
            case CORROMPU:
                // +20% ATT et MAG
                physicalAttack += (int)(physicalAttack * 0.2f * multiplier);
                magicalAttack += (int)(magicalAttack * 0.2f * multiplier);
                break;
                
            case PROTÉGÉ:
                // Pas d'effet immédiat, géré dans la logique de combat
                break;
        }
    }
    
    /**
     * Définir un temps de récupération pour une capacité
     * 
     * @param abilityIndex Index de la capacité
     * @param cooldown Nombre de tours de récupération
     */
    public void setCooldown(int abilityIndex, int cooldown) {
        if (abilityIndex >= 0 && abilityIndex < skillCooldowns.length) {
            skillCooldowns[abilityIndex] = cooldown;
        }
    }
    
    /**
     * Obtenir le temps de récupération restant pour une capacité
     * 
     * @param abilityIndex Index de la capacité
     * @return Nombre de tours restants avant de pouvoir réutiliser la capacité
     */
    public int getCooldown(int abilityIndex) {
        if (abilityIndex >= 0 && abilityIndex < skillCooldowns.length) {
            return skillCooldowns[abilityIndex];
        }
        return 0;
    }
    
    /**
     * Vérifier si une capacité est prête à être utilisée
     * 
     * @param abilityIndex Index de la capacité
     * @return true si la capacité est prête, false sinon
     */
    public boolean isSkillReady(int abilityIndex) {
        if (abilityIndex >= 0 && abilityIndex < skillCooldowns.length) {
            return skillCooldowns[abilityIndex] <= 0;
        }
        return true;
    }
    
    /**
     * Mettre à jour les temps de récupération (réduire de 1 à chaque tour)
     */
    public void updateCooldowns() {
        for (int i = 0; i < skillCooldowns.length; i++) {
            if (skillCooldowns[i] > 0) {
                skillCooldowns[i]--;
            }
        }
    }
    
    /**
     * Vérifier si le combattant est vaincu
     * 
     * @return true si les PV sont à 0, false sinon
     */
    public boolean isDefeated() {
        return currentHealth <= 0;
    }
    
    // Getters et setters
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }
    
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.min(maxHealth, Math.max(0, currentHealth));
    }
    
    public int getPhysicalAttack() {
        return physicalAttack;
    }
    
    public void setPhysicalAttack(int physicalAttack) {
        this.physicalAttack = physicalAttack;
    }
    
    public int getMagicalAttack() {
        return magicalAttack;
    }
    
    public void setMagicalAttack(int magicalAttack) {
        this.magicalAttack = magicalAttack;
    }
    
    public int getPhysicalDefense() {
        return physicalDefense;
    }
    
    public void setPhysicalDefense(int physicalDefense) {
        this.physicalDefense = physicalDefense;
    }
    
    public int getMagicalDefense() {
        return magicalDefense;
    }
    
    public void setMagicalDefense(int magicalDefense) {
        this.magicalDefense = magicalDefense;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public int getAccuracy() {
        return accuracy;
    }
    
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }
    
    public int getEvasion() {
        return evasion;
    }
    
    public void setEvasion(int evasion) {
        this.evasion = evasion;
    }
    
    public float getCriticalChance() {
        return criticalChance;
    }
    
    public void setCriticalChance(float criticalChance) {
        this.criticalChance = criticalChance;
    }
    
    public float getCriticalDamage() {
        return criticalDamage;
    }
    
    public void setCriticalDamage(float criticalDamage) {
        this.criticalDamage = criticalDamage;
    }
    
    public float getLifeSteal() {
        return lifeSteal;
    }
    
    public void setLifeSteal(float lifeSteal) {
        this.lifeSteal = lifeSteal;
    }
    
    public float getSpellVamp() {
        return spellVamp;
    }
    
    public void setSpellVamp(float spellVamp) {
        this.spellVamp = spellVamp;
    }
    
    public float getOmnivamp() {
        return omnivamp;
    }
    
    public void setOmnivamp(float omnivamp) {
        this.omnivamp = omnivamp;
    }
    
    public float getTenacity() {
        return tenacity;
    }
    
    public void setTenacity(float tenacity) {
        this.tenacity = tenacity;
    }
    
    public float getArmorPenetration() {
        return armorPenetration;
    }
    
    public void setArmorPenetration(float armorPenetration) {
        this.armorPenetration = armorPenetration;
    }
    
    public float getMagicPenetration() {
        return magicPenetration;
    }
    
    public void setMagicPenetration(float magicPenetration) {
        this.magicPenetration = magicPenetration;
    }
    
    public float getHealingBonus() {
        return healingBonus;
    }
    
    public void setHealingBonus(float healingBonus) {
        this.healingBonus = healingBonus;
    }
    
    public float getShieldStrength() {
        return shieldStrength;
    }
    
    public void setShieldStrength(float shieldStrength) {
        this.shieldStrength = shieldStrength;
    }
    
    public int getPhysicalShield() {
        return physicalShield;
    }
    
    public void setPhysicalShield(int physicalShield) {
        this.physicalShield = Math.max(0, physicalShield);
    }
    
    public int getMagicalShield() {
        return magicalShield;
    }
    
    public void setMagicalShield(int magicalShield) {
        this.magicalShield = Math.max(0, magicalShield);
    }
    
    public int getAdaptiveShield() {
        return adaptiveShield;
    }
    
    public void setAdaptiveShield(int adaptiveShield) {
        this.adaptiveShield = Math.max(0, adaptiveShield);
    }
    
    public int[] getSkillCooldowns() {
        return skillCooldowns;
    }
}

package com.ryuukonpalace.game.creatures;

import java.util.HashMap;
import java.util.Map;

/**
 * Système de gestion des niveaux et de l'expérience pour les créatures
 */
public class LevelSystem {
    // Singleton
    private static LevelSystem instance;
    
    // Niveau maximum
    private static final int MAX_LEVEL = 100;
    
    // Table d'expérience (niveau -> expérience nécessaire pour ce niveau)
    private Map<Integer, Integer> experienceTable;
    
    // Table des capacités par niveau et par créature
    private Map<Integer, Map<Integer, Ability>> abilityUnlocks;
    
    /**
     * Constructeur privé (singleton)
     */
    private LevelSystem() {
        initExperienceTable();
        initAbilityUnlocks();
    }
    
    /**
     * Obtenir l'instance unique du système de niveaux
     * 
     * @return L'instance du LevelSystem
     */
    public static LevelSystem getInstance() {
        if (instance == null) {
            instance = new LevelSystem();
        }
        return instance;
    }
    
    /**
     * Initialiser la table d'expérience
     * Formule: niveau^2 * 50 pour une progression non-linéaire
     */
    private void initExperienceTable() {
        experienceTable = new HashMap<>();
        
        // Niveau 1 ne nécessite pas d'expérience
        experienceTable.put(1, 0);
        
        // Calculer l'expérience nécessaire pour chaque niveau
        for (int level = 2; level <= MAX_LEVEL; level++) {
            int expNeeded = (int)(Math.pow(level, 2) * 50);
            experienceTable.put(level, expNeeded);
        }
    }
    
    /**
     * Initialiser la table des capacités débloquées par niveau
     * Format: Map<CreatureId, Map<Level, Ability>>
     */
    private void initAbilityUnlocks() {
        abilityUnlocks = new HashMap<>();
        
        // Exemple pour la créature 1 (à compléter avec les vraies données)
        Map<Integer, Ability> creature1Abilities = new HashMap<>();
        creature1Abilities.put(5, new Ability("Frappe Rapide", "Une attaque rapide qui touche presque toujours", CreatureType.MYTHICAL, 40, 95, 30, Ability.EffectType.NONE, 0));
        creature1Abilities.put(10, new Ability("Coup Puissant", "Une attaque puissante mais moins précise", CreatureType.MYTHICAL, 70, 80, 15, Ability.EffectType.NONE, 0));
        creature1Abilities.put(15, new Ability("Frappe Critique", "Une attaque avec un taux critique élevé", CreatureType.MYTHICAL, 50, 90, 20, Ability.EffectType.NONE, 0));
        
        abilityUnlocks.put(1, creature1Abilities);
        
        // Exemple pour la créature 2
        Map<Integer, Ability> creature2Abilities = new HashMap<>();
        creature2Abilities.put(5, new Ability("Jet d'Eau", "Projette de l'eau sur l'adversaire", CreatureType.WATER, 40, 100, 25, Ability.EffectType.NONE, 0));
        creature2Abilities.put(12, new Ability("Tsunami", "Crée une vague dévastatrice", CreatureType.WATER, 90, 80, 10, Ability.EffectType.NONE, 0));
        creature2Abilities.put(18, new Ability("Brume", "Crée un brouillard qui réduit la précision", CreatureType.WATER, 0, 100, 20, Ability.EffectType.STAT_REDUCE, 100));
        
        abilityUnlocks.put(2, creature2Abilities);
        
        // Ajouter d'autres créatures selon les besoins
    }
    
    /**
     * Obtenir l'expérience nécessaire pour atteindre un niveau
     * 
     * @param level Le niveau cible
     * @return L'expérience nécessaire pour ce niveau
     */
    public int getExperienceForLevel(int level) {
        if (level <= 0 || level > MAX_LEVEL) {
            return 0;
        }
        return experienceTable.getOrDefault(level, 0);
    }
    
    /**
     * Calculer l'expérience totale nécessaire pour atteindre un niveau depuis le niveau 1
     * 
     * @param level Le niveau cible
     * @return L'expérience totale nécessaire
     */
    public int getTotalExperienceForLevel(int level) {
        int totalExp = 0;
        for (int i = 1; i <= level; i++) {
            totalExp += getExperienceForLevel(i);
        }
        return totalExp;
    }
    
    /**
     * Vérifier si une créature peut apprendre une nouvelle capacité à son niveau actuel
     * 
     * @param creatureId L'ID de la créature
     * @param level Le niveau actuel de la créature
     * @return La capacité à apprendre, ou null si aucune
     */
    public Ability getAbilityForLevel(int creatureId, int level) {
        Map<Integer, Ability> creatureAbilities = abilityUnlocks.get(creatureId);
        if (creatureAbilities == null) {
            return null;
        }
        
        return creatureAbilities.get(level);
    }
    
    /**
     * Calculer les bonus de statistiques pour un niveau donné
     * 
     * @param creatureId L'ID de la créature
     * @param level Le niveau actuel
     * @return Un tableau de bonus [santé, attaque, défense, vitesse]
     */
    public int[] getStatBonusesForLevel(int creatureId, int level) {
        // Bonus de base pour toutes les créatures
        int healthBonus = 5;
        int attackBonus = 2;
        int defenseBonus = 2;
        int speedBonus = 2;
        
        // Bonus supplémentaires tous les 5 niveaux
        if (level % 5 == 0) {
            healthBonus += 3;
            attackBonus += 1;
            defenseBonus += 1;
            speedBonus += 1;
        }
        
        // Bonus supplémentaires tous les 10 niveaux
        if (level % 10 == 0) {
            healthBonus += 5;
            attackBonus += 2;
            defenseBonus += 2;
            speedBonus += 2;
        }
        
        // Ajustements spécifiques selon le type de créature
        CreatureFactory factory = CreatureFactory.getInstance();
        CreatureFactory.CreatureDefinition definition = factory.getCreatureDefinition(creatureId);
        
        if (definition != null) {
            switch (definition.type) {
                case FIRE:
                    attackBonus += 1; // Les créatures de feu gagnent plus en attaque
                    break;
                case WATER:
                    defenseBonus += 1; // Les créatures d'eau gagnent plus en défense
                    break;
                case NATURE:
                    healthBonus += 2; // Les créatures plantes gagnent plus en santé
                    break;
                case ELECTRIC:
                    speedBonus += 1; // Les créatures électriques gagnent plus en vitesse
                    break;
                case AIR:
                    speedBonus += 1; // Les créatures d'air gagnent plus en vitesse
                    break;
                case EARTH:
                    defenseBonus += 1; // Les créatures de terre gagnent plus en défense
                    break;
                case ICE:
                    attackBonus += 1; // Les créatures de glace gagnent plus en attaque
                    break;
                case LIGHT:
                    attackBonus += 1; // Les créatures de lumière gagnent plus en attaque
                    break;
                case SHADOW:
                    attackBonus += 1; // Les créatures d'ombre gagnent plus en attaque
                    break;
                case METAL:
                    defenseBonus += 2; // Les créatures de métal gagnent plus en défense
                    break;
                case PSYCHIC:
                    attackBonus += 1; // Les créatures psychiques gagnent plus en attaque
                    break;
                case MYTHICAL:
                    // Les créatures mythiques gagnent plus dans toutes les statistiques
                    healthBonus += 1;
                    attackBonus += 1;
                    defenseBonus += 1;
                    speedBonus += 1;
                    break;
            }
        }
        
        return new int[] { healthBonus, attackBonus, defenseBonus, speedBonus };
    }
    
    /**
     * Calculer l'expérience gagnée en battant une créature
     * 
     * @param defeatedCreature La créature vaincue
     * @param winnerLevel Le niveau de la créature victorieuse
     * @return L'expérience gagnée
     */
    public int calculateExperienceGain(Creature defeatedCreature, int winnerLevel) {
        int baseExp = 50; // Expérience de base
        int defLevel = defeatedCreature.getLevel();
        
        // Bonus pour avoir battu une créature de niveau supérieur
        double levelRatio = (double) defLevel / winnerLevel;
        double levelBonus = Math.max(1.0, levelRatio * 1.5);
        
        // Bonus pour les créatures rares ou spéciales
        double rarityBonus = 1.0;
        // Ici, on pourrait ajouter une logique pour les créatures rares
        
        return (int)(baseExp * levelBonus * rarityBonus);
    }
    
    /**
     * Déterminer le niveau d'une créature en fonction de son expérience totale
     * 
     * @param totalExp L'expérience totale accumulée
     * @return Le niveau correspondant
     */
    public int getLevelFromExperience(int totalExp) {
        for (int level = MAX_LEVEL; level >= 1; level--) {
            if (totalExp >= getTotalExperienceForLevel(level)) {
                return level;
            }
        }
        return 1; // Par défaut, niveau 1
    }
}

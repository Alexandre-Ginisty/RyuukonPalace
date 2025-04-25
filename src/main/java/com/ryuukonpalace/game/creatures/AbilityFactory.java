package com.ryuukonpalace.game.creatures;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory pour créer et gérer les capacités des créatures.
 * Utilise le pattern Singleton pour garantir une instance unique.
 */
public class AbilityFactory {
    
    // Instance unique (pattern Singleton)
    private static AbilityFactory instance;
    
    // Cache des capacités par ID
    private Map<Integer, Ability> abilitiesCache;
    
    /**
     * Constructeur privé pour empêcher l'instanciation directe
     */
    private AbilityFactory() {
        abilitiesCache = new HashMap<>();
        initializeDefaultAbilities();
    }
    
    /**
     * Obtenir l'instance unique de la factory
     * 
     * @return L'instance unique
     */
    public static AbilityFactory getInstance() {
        if (instance == null) {
            instance = new AbilityFactory();
        }
        return instance;
    }
    
    /**
     * Initialiser les capacités par défaut
     */
    private void initializeDefaultAbilities() {
        // Capacités de type FEU
        registerAbility(new Ability(
                1, 
                "Flamme Ardente", 
                "Une attaque de feu basique qui peut brûler l'adversaire.", 
                CreatureType.FIRE, 
                40, 
                95, 
                15, 
                Ability.EffectType.BURN, 
                10
        ));
        
        registerAbility(new Ability(
                2, 
                "Explosion Solaire", 
                "Une puissante attaque de feu qui puise son énergie du soleil.", 
                CreatureType.FIRE, 
                120, 
                80, 
                5, 
                Ability.EffectType.BURN, 
                30
        ));
        
        // Capacités de type EAU
        registerAbility(new Ability(
                3, 
                "Jet d'Eau", 
                "Une attaque d'eau basique qui peut repousser l'adversaire.", 
                CreatureType.WATER, 
                40, 
                100, 
                20, 
                Ability.EffectType.NONE, 
                0
        ));
        
        registerAbility(new Ability(
                4, 
                "Tsunami", 
                "Une vague géante qui frappe tous les adversaires.", 
                CreatureType.WATER, 
                90, 
                85, 
                10, 
                Ability.EffectType.STAT_REDUCE, 
                20
        ));
        
        // Capacités de type TERRE
        registerAbility(new Ability(
                5, 
                "Séisme", 
                "Une secousse puissante qui ébranle le sol.", 
                CreatureType.EARTH, 
                100, 
                90, 
                10, 
                Ability.EffectType.FLINCH, 
                20
        ));
        
        registerAbility(new Ability(
                6, 
                "Projection de Roche", 
                "Lance des roches pointues sur l'adversaire.", 
                CreatureType.EARTH, 
                60, 
                95, 
                15, 
                Ability.EffectType.NONE, 
                0
        ));
        
        // Capacités de type AIR
        registerAbility(new Ability(
                7, 
                "Tornade", 
                "Crée une tornade qui emporte l'adversaire.", 
                CreatureType.AIR, 
                80, 
                90, 
                10, 
                Ability.EffectType.CONFUSE, 
                20
        ));
        
        registerAbility(new Ability(
                8, 
                "Rafale", 
                "Une rafale de vent qui peut déséquilibrer l'adversaire.", 
                CreatureType.AIR, 
                40, 
                100, 
                20, 
                Ability.EffectType.STAT_REDUCE, 
                10
        ));
        
        // Capacités de type NATURE
        registerAbility(new Ability(
                9, 
                "Fouet Lianes", 
                "Frappe l'adversaire avec des lianes puissantes.", 
                CreatureType.NATURE, 
                45, 
                100, 
                20, 
                Ability.EffectType.NONE, 
                0
        ));
        
        registerAbility(new Ability(
                10, 
                "Tempête Florale", 
                "Déchaîne une tempête de pétales tranchants.", 
                CreatureType.NATURE, 
                90, 
                85, 
                10, 
                Ability.EffectType.STAT_REDUCE, 
                15
        ));
        
        // Capacités de type ÉLECTRIQUE
        registerAbility(new Ability(
                11, 
                "Éclair", 
                "Lance un éclair sur l'adversaire.", 
                CreatureType.ELECTRIC, 
                40, 
                100, 
                20, 
                Ability.EffectType.PARALYZE, 
                10
        ));
        
        registerAbility(new Ability(
                12, 
                "Tonnerre", 
                "Frappe l'adversaire avec la puissance du tonnerre.", 
                CreatureType.ELECTRIC, 
                90, 
                90, 
                10, 
                Ability.EffectType.PARALYZE, 
                30
        ));
        
        // Capacités de type GLACE
        registerAbility(new Ability(
                13, 
                "Souffle Glacial", 
                "Un souffle d'air glacé qui peut geler l'adversaire.", 
                CreatureType.ICE, 
                40, 
                95, 
                20, 
                Ability.EffectType.FREEZE, 
                10
        ));
        
        registerAbility(new Ability(
                14, 
                "Blizzard", 
                "Déchaîne une tempête de neige dévastatrice.", 
                CreatureType.ICE, 
                110, 
                70, 
                5, 
                Ability.EffectType.FREEZE, 
                30
        ));
        
        // Capacités de type LUMIÈRE
        registerAbility(new Ability(
                15, 
                "Rayon Lumineux", 
                "Un rayon de lumière pure qui aveugle l'adversaire.", 
                CreatureType.LIGHT, 
                60, 
                95, 
                15, 
                Ability.EffectType.STAT_REDUCE, 
                20
        ));
        
        registerAbility(new Ability(
                16, 
                "Jugement Céleste", 
                "Invoque la puissance divine pour frapper l'adversaire.", 
                CreatureType.LIGHT, 
                100, 
                85, 
                5, 
                Ability.EffectType.NONE, 
                0
        ));
        
        // Capacités de type TÉNÈBRES
        registerAbility(new Ability(
                17, 
                "Ombre Nocturne", 
                "Enveloppe l'adversaire dans les ténèbres.", 
                CreatureType.SHADOW, 
                60, 
                95, 
                15, 
                Ability.EffectType.STAT_REDUCE, 
                20
        ));
        
        registerAbility(new Ability(
                18, 
                "Éclipse", 
                "Plonge le champ de bataille dans l'obscurité totale.", 
                CreatureType.SHADOW, 
                100, 
                85, 
                5, 
                Ability.EffectType.CONFUSE, 
                30
        ));
        
        // Capacités de type MÉTAL
        registerAbility(new Ability(
                19, 
                "Griffe d'Acier", 
                "Frappe l'adversaire avec des griffes métalliques.", 
                CreatureType.METAL, 
                50, 
                95, 
                15, 
                Ability.EffectType.NONE, 
                0
        ));
        
        registerAbility(new Ability(
                20, 
                "Canon Métallique", 
                "Tire des projectiles métalliques à haute vitesse.", 
                CreatureType.METAL, 
                80, 
                90, 
                10, 
                Ability.EffectType.FLINCH, 
                10
        ));
        
        // Capacités de type ESPRIT
        registerAbility(new Ability(
                21, 
                "Choc Mental", 
                "Attaque l'esprit de l'adversaire.", 
                CreatureType.PSYCHIC, 
                50, 
                100, 
                15, 
                Ability.EffectType.CONFUSE, 
                10
        ));
        
        registerAbility(new Ability(
                22, 
                "Psychokinésie", 
                "Manipule l'adversaire par la force de l'esprit.", 
                CreatureType.PSYCHIC, 
                90, 
                90, 
                10, 
                Ability.EffectType.STAT_REDUCE, 
                30
        ));
        
        // Capacités de type COMBAT
        registerAbility(new Ability(
                23, 
                "Coup Rapide", 
                "Un coup rapide et précis.", 
                CreatureType.MYTHICAL, 
                40, 
                100, 
                20, 
                Ability.EffectType.NONE, 
                0
        ));
        
        registerAbility(new Ability(
                24, 
                "Frappe Héroïque", 
                "Une attaque puissante qui demande beaucoup de force.", 
                CreatureType.MYTHICAL, 
                90, 
                85, 
                10, 
                Ability.EffectType.STAT_BOOST, 
                20
        ));
        
        // Capacités de type POISON (utilisant NATURE comme type le plus proche)
        registerAbility(new Ability(
                25, 
                "Dard Toxique", 
                "Injecte un poison dans l'adversaire.", 
                CreatureType.NATURE, 
                40, 
                95, 
                20, 
                Ability.EffectType.POISON, 
                30
        ));
        
        registerAbility(new Ability(
                26, 
                "Nuage Toxique", 
                "Crée un nuage de gaz toxique autour de l'adversaire.", 
                CreatureType.NATURE, 
                80, 
                90, 
                10, 
                Ability.EffectType.POISON, 
                50
        ));
        
        // Capacités de type NORMAL (utilisant MYTHICAL comme type le plus générique)
        registerAbility(new Ability(
                27, 
                "Charge", 
                "Une attaque basique qui fonce sur l'adversaire.", 
                CreatureType.MYTHICAL, 
                40, 
                100, 
                30, 
                Ability.EffectType.NONE, 
                0
        ));
        
        registerAbility(new Ability(
                28, 
                "Coup de Tête", 
                "Frappe l'adversaire avec sa tête.", 
                CreatureType.MYTHICAL, 
                70, 
                90, 
                15, 
                Ability.EffectType.FLINCH, 
                30
        ));
        
        // Capacités de soin
        registerAbility(new Ability(
                29, 
                "Récupération", 
                "Restaure une partie de ses PV.", 
                CreatureType.MYTHICAL, 
                0, 
                100, 
                10, 
                Ability.EffectType.HEAL, 
                100
        ));
        
        // Capacités de boost de statistiques
        registerAbility(new Ability(
                30, 
                "Méditation", 
                "Augmente l'attaque et la défense.", 
                CreatureType.PSYCHIC, 
                0, 
                100, 
                10, 
                Ability.EffectType.STAT_BOOST, 
                100
        ));
    }
    
    /**
     * Enregistrer une capacité dans le cache
     * 
     * @param ability Capacité à enregistrer
     */
    public void registerAbility(Ability ability) {
        abilitiesCache.put(ability.getId(), ability);
    }
    
    /**
     * Obtenir une capacité par son ID
     * 
     * @param id ID de la capacité
     * @return La capacité correspondante, ou null si non trouvée
     */
    public Ability getAbility(int id) {
        return abilitiesCache.get(id);
    }
    
    /**
     * Obtenir toutes les capacités enregistrées
     * 
     * @return Map des capacités par ID
     */
    public Map<Integer, Ability> getAllAbilities() {
        return new HashMap<>(abilitiesCache);
    }
    
    /**
     * Réinitialiser le cache des capacités
     * Utile pour les tests ou pour recharger les données
     */
    public void resetCache() {
        abilitiesCache.clear();
        initializeDefaultAbilities();
    }
}

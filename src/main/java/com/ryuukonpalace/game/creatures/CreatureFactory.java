package com.ryuukonpalace.game.creatures;

import java.util.HashMap;
import java.util.Map;

/**
 * Fabrique responsable de la création des créatures du jeu.
 * Stocke les définitions des créatures et permet de les instancier.
 */
public class CreatureFactory {
    
    // Singleton instance
    private static CreatureFactory instance;
    
    // Map des définitions de créatures (ID -> CreatureDefinition)
    private final Map<Integer, CreatureDefinition> creatureDefinitions;
    
    /**
     * Constructeur privé pour le singleton
     */
    private CreatureFactory() {
        creatureDefinitions = new HashMap<>();
        initializeCreatureDefinitions();
    }
    
    /**
     * Obtenir l'instance unique de la fabrique
     * @return L'instance du CreatureFactory
     */
    public static CreatureFactory getInstance() {
        if (instance == null) {
            instance = new CreatureFactory();
        }
        return instance;
    }
    
    /**
     * Initialiser les définitions des créatures
     * Cette méthode ajoute toutes les créatures disponibles dans le jeu
     */
    private void initializeCreatureDefinitions() {
        // Créature 1: Flameling (Type FEU)
        addCreatureDefinition(
            1,                              // ID
            "Flameling",                    // Nom
            CreatureType.FIRE,              // Type
            45, 50, 40, 35,                 // Stats de base (santé, attaque, défense, vitesse)
            "Un petit être de flamme vif et enjoué. Sa queue brûlante peut illuminer les endroits sombres.",
            "creatures/flameling.png",      // Sprite
            3                               // ID de l'évolution (0 si pas d'évolution)
        );
        
        // Créature 2: Aquaflux (Type EAU)
        addCreatureDefinition(
            2,                              // ID
            "Aquaflux",                     // Nom
            CreatureType.WATER,             // Type
            50, 40, 45, 40,                 // Stats de base (santé, attaque, défense, vitesse)
            "Une créature aquatique capable de se faufiler dans les moindres recoins. Son corps translucide reflète la lumière.",
            "creatures/aquaflux.png",       // Sprite
            4                               // ID de l'évolution
        );
        
        // Créature 3: Infernus (Évolution de Flameling, Type FEU)
        addCreatureDefinition(
            3,                              // ID
            "Infernus",                     // Nom
            CreatureType.FIRE,              // Type
            75, 85, 65, 60,                 // Stats de base (santé, attaque, défense, vitesse)
            "Évolution de Flameling. Sa maîtrise du feu lui permet de créer des explosions contrôlées.",
            "creatures/infernus.png",       // Sprite
            0                               // Pas d'évolution
        );
        
        // Créature 4: Tsunamis (Évolution d'Aquaflux, Type EAU)
        addCreatureDefinition(
            4,                              // ID
            "Tsunamis",                     // Nom
            CreatureType.WATER,             // Type
            85, 70, 80, 65,                 // Stats de base (santé, attaque, défense, vitesse)
            "Évolution d'Aquaflux. Peut générer des vagues puissantes et manipuler les courants marins.",
            "creatures/tsunamis.png",       // Sprite
            0                               // Pas d'évolution
        );
        
        // Créature 5: Terravolt (Type EARTH)
        addCreatureDefinition(
            5,                              // ID
            "Terravolt",                    // Nom
            CreatureType.EARTH,             // Type
            60, 55, 65, 30,                 // Stats de base (santé, attaque, défense, vitesse)
            "Une créature terrestre avec une carapace rocheuse. Peut générer des champs électriques à partir des minéraux.",
            "creatures/terravolt.png",      // Sprite
            6                               // ID de l'évolution
        );
        
        // Créature 6: Seismoroc (Évolution de Terravolt, Type EARTH)
        addCreatureDefinition(
            6,                              // ID
            "Seismoroc",                    // Nom
            CreatureType.EARTH,             // Type
            95, 85, 100, 45,                // Stats de base (santé, attaque, défense, vitesse)
            "Évolution de Terravolt. Sa maîtrise des ondes sismiques lui permet de prévoir les tremblements de terre.",
            "creatures/seismoroc.png",      // Sprite
            0                               // Pas d'évolution
        );
        
        // Créature 7: Zephyrus (Type AIR)
        addCreatureDefinition(
            7,                              // ID
            "Zephyrus",                     // Nom
            CreatureType.AIR,               // Type
            40, 45, 35, 70,                 // Stats de base (santé, attaque, défense, vitesse)
            "Une créature aérienne rapide et agile. Peut créer des mini-tornades pour déstabiliser ses adversaires.",
            "creatures/zephyrus.png",       // Sprite
            8                               // ID de l'évolution
        );
        
        // Créature 8: Tempestus (Évolution de Zephyrus, Type AIR)
        addCreatureDefinition(
            8,                              // ID
            "Tempestus",                    // Nom
            CreatureType.AIR,               // Type
            65, 75, 60, 110,                // Stats de base (santé, attaque, défense, vitesse)
            "Évolution de Zephyrus. Maître des vents, il peut voler à des vitesses supersoniques.",
            "creatures/tempestus.png",      // Sprite
            0                               // Pas d'évolution
        );
        
        // Créature 9: Luminar (Type LIGHT)
        addCreatureDefinition(
            9,                              // ID
            "Luminar",                      // Nom
            CreatureType.LIGHT,             // Type
            50, 60, 50, 55,                 // Stats de base (santé, attaque, défense, vitesse)
            "Une créature lumineuse qui émet une aura apaisante. Peut soigner les blessures mineures.",
            "creatures/luminar.png",        // Sprite
            10                              // ID de l'évolution
        );
        
        // Créature 10: Solarius (Évolution de Luminar, Type LIGHT)
        addCreatureDefinition(
            10,                             // ID
            "Solarius",                     // Nom
            CreatureType.LIGHT,             // Type
            80, 95, 80, 85,                 // Stats de base (santé, attaque, défense, vitesse)
            "Évolution de Luminar. Sa lumière est si intense qu'elle peut temporairement aveugler ses adversaires.",
            "creatures/solarius.png",       // Sprite
            0                               // Pas d'évolution
        );
        
        // Créature 11: Umbra (Type SHADOW)
        addCreatureDefinition(
            11,                             // ID
            "Umbra",                        // Nom
            CreatureType.SHADOW,            // Type
            45, 65, 45, 60,                 // Stats de base (santé, attaque, défense, vitesse)
            "Une créature des ombres capable de se fondre dans l'obscurité. Peut absorber la lumière environnante.",
            "creatures/umbra.png",          // Sprite
            12                              // ID de l'évolution
        );
        
        // Créature 12: Nocturnix (Évolution d'Umbra, Type SHADOW)
        addCreatureDefinition(
            12,                             // ID
            "Nocturnix",                    // Nom
            CreatureType.SHADOW,            // Type
            75, 100, 75, 90,                // Stats de base (santé, attaque, défense, vitesse)
            "Évolution d'Umbra. Maître des ténèbres, il peut créer des zones d'obscurité totale.",
            "creatures/nocturnix.png",      // Sprite
            0                               // Pas d'évolution
        );
    }
    
    /**
     * Ajouter une définition de créature
     * 
     * @param id ID unique de la créature
     * @param name Nom de la créature
     * @param type Type de la créature
     * @param baseHealth Santé de base
     * @param baseAttack Attaque de base
     * @param baseDefense Défense de base
     * @param baseSpeed Vitesse de base
     * @param description Description de la créature
     * @param spritePath Chemin vers le sprite de la créature
     * @param evolutionId ID de l'évolution (0 si pas d'évolution)
     */
    private void addCreatureDefinition(int id, String name, CreatureType type, 
                                      int baseHealth, int baseAttack, int baseDefense, int baseSpeed,
                                      String description, String spritePath, int evolutionId) {
        CreatureDefinition definition = new CreatureDefinition(
            id, name, type, baseHealth, baseAttack, baseDefense, baseSpeed, description, spritePath, evolutionId
        );
        creatureDefinitions.put(id, definition);
    }
    
    /**
     * Créer une créature à partir de son ID
     * 
     * @param creatureId ID de la créature à créer
     * @return La créature créée, ou null si l'ID est invalide
     */
    public static Creature createCreature(int creatureId) {
        return createCreature(creatureId, 5); // Niveau par défaut: 5
    }
    
    /**
     * Créer une créature à partir de son ID et de son niveau
     * 
     * @param creatureId ID de la créature à créer
     * @param level Niveau de la créature
     * @return La créature créée, ou null si l'ID est invalide
     */
    public static Creature createCreature(int creatureId, int level) {
        CreatureFactory factory = getInstance();
        CreatureDefinition definition = factory.getCreatureDefinition(creatureId);
        
        if (definition == null) {
            return null;
        }
        
        // Calculer les stats en fonction du niveau
        int health = calculateStat(definition.baseHealth, level);
        int attack = calculateStat(definition.baseAttack, level);
        int defense = calculateStat(definition.baseDefense, level);
        int speed = calculateStat(definition.baseSpeed, level);
        
        // Créer la créature
        Creature creature = new Creature(
            definition.id,
            definition.name,
            definition.type,
            level,
            health,
            attack,
            defense,
            speed
        );
        
        // Ajouter l'évolution si elle existe
        if (definition.evolutionId > 0) {
            CreatureDefinition evolutionDef = factory.getCreatureDefinition(definition.evolutionId);
            if (evolutionDef != null) {
                // Le niveau requis pour évoluer est généralement entre 20 et 30
                int requiredLevel = 20 + (definition.id % 10);
                
                // Créer l'évolution (niveau +5 par rapport au niveau requis)
                Creature evolvedForm = createCreature(definition.evolutionId, requiredLevel + 5);
                
                if (evolvedForm != null) {
                    Evolution evolution = new Evolution(requiredLevel, evolvedForm);
                    creature.setEvolution(evolution);
                }
            }
        }
        
        // Ajouter des capacités à la créature
        addAbilitiesToCreature(creature, definition, level);
        
        return creature;
    }
    
    /**
     * Calculer une statistique en fonction du niveau
     * 
     * @param baseStat Statistique de base
     * @param level Niveau de la créature
     * @return La statistique calculée
     */
    private static int calculateStat(int baseStat, int level) {
        // Formule simple: stat = baseStat + (level - 1) * (baseStat / 10)
        return baseStat + (level - 1) * (baseStat / 10);
    }
    
    /**
     * Ajouter des capacités à une créature
     * 
     * @param creature La créature à laquelle ajouter des capacités
     * @param definition La définition de la créature
     * @param level Le niveau de la créature
     */
    private static void addAbilitiesToCreature(Creature creature, CreatureDefinition definition, int level) {
        // Capacité de base (toujours disponible)
        String baseAbilityName = "Attaque " + definition.type.name().toLowerCase();
        String baseAbilityDesc = "Une attaque de base de type " + definition.type.name().toLowerCase() + ".";
        
        Ability baseAbility = new Ability(
            baseAbilityName,
            baseAbilityDesc,
            definition.type,
            40,                                     // Puissance
            95,                                     // Précision
            30,                                     // Utilisations max
            Ability.EffectType.NONE,                // Pas d'effet supplémentaire
            0                                       // Chance d'effet
        );
        
        creature.addAbility(baseAbility);
        
        // Capacité avancée (disponible à partir du niveau 10)
        if (level >= 10) {
            String advAbilityName = "Rayon " + definition.type.name().toLowerCase();
            String advAbilityDesc = "Un rayon concentré de type " + definition.type.name().toLowerCase() + ".";
            
            Ability advAbility = new Ability(
                advAbilityName,
                advAbilityDesc,
                definition.type,
                65,                                 // Puissance
                85,                                 // Précision
                15,                                 // Utilisations max
                getEffectTypeForType(definition.type), // Effet basé sur le type
                20                                  // 20% de chance d'effet
            );
            
            creature.addAbility(advAbility);
        }
        
        // Capacité ultime (disponible à partir du niveau 20)
        if (level >= 20) {
            String ultAbilityName = "Tempête " + definition.type.name().toLowerCase();
            String ultAbilityDesc = "Une attaque dévastatrice de type " + definition.type.name().toLowerCase() + ".";
            
            Ability ultAbility = new Ability(
                ultAbilityName,
                ultAbilityDesc,
                definition.type,
                90,                                 // Puissance
                75,                                 // Précision
                5,                                  // Utilisations max
                getEffectTypeForType(definition.type), // Effet basé sur le type
                40                                  // 40% de chance d'effet
            );
            
            creature.addAbility(ultAbility);
        }
    }
    
    /**
     * Obtenir un type d'effet en fonction du type de la créature
     * 
     * @param type Type de la créature
     * @return Type d'effet approprié
     */
    private static Ability.EffectType getEffectTypeForType(CreatureType type) {
        switch (type) {
            case FIRE:
                return Ability.EffectType.BURN;
            case WATER:
                return Ability.EffectType.STAT_REDUCE;
            case EARTH:
                return Ability.EffectType.FLINCH;
            case AIR:
                return Ability.EffectType.STAT_BOOST;
            case LIGHT:
                return Ability.EffectType.HEAL;
            case SHADOW:
                return Ability.EffectType.CONFUSE;
            case METAL:
                return Ability.EffectType.STAT_BOOST;
            case NATURE:
                return Ability.EffectType.DRAIN;
            case ELECTRIC:
                return Ability.EffectType.PARALYZE;
            case ICE:
                return Ability.EffectType.FREEZE;
            case PSYCHIC:
                return Ability.EffectType.CONFUSE;
            case MYTHICAL:
                return Ability.EffectType.SLEEP;
            default:
                return Ability.EffectType.NONE;
        }
    }
    
    /**
     * Obtenir une définition de créature par son ID
     * 
     * @param creatureId ID de la créature
     * @return La définition de la créature, ou null si l'ID est invalide
     */
    public CreatureDefinition getCreatureDefinition(int creatureId) {
        return creatureDefinitions.get(creatureId);
    }
    
    /**
     * Obtenir toutes les définitions de créatures
     * 
     * @return Map des définitions de créatures (ID -> CreatureDefinition)
     */
    public Map<Integer, CreatureDefinition> getAllCreatureDefinitions() {
        return new HashMap<>(creatureDefinitions);
    }
    
    /**
     * Classe interne pour représenter la définition d'une créature
     */
    public static class CreatureDefinition {
        public final int id;
        public final String name;
        public final CreatureType type;
        public final int baseHealth;
        public final int baseAttack;
        public final int baseDefense;
        public final int baseSpeed;
        public final String description;
        public final String spritePath;
        public final int evolutionId;
        
        public CreatureDefinition(int id, String name, CreatureType type, 
                                 int baseHealth, int baseAttack, int baseDefense, int baseSpeed,
                                 String description, String spritePath, int evolutionId) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.baseHealth = baseHealth;
            this.baseAttack = baseAttack;
            this.baseDefense = baseDefense;
            this.baseSpeed = baseSpeed;
            this.description = description;
            this.spritePath = spritePath;
            this.evolutionId = evolutionId;
        }
    }
}

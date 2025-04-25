package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.core.GameState;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureFactory;
import com.ryuukonpalace.game.creatures.CreatureType;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.items.ItemFactory;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.world.TimeSystem.TimeOfDay;
import com.ryuukonpalace.game.world.WeatherSystem.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Système d'événements aléatoires pour le jeu.
 * Gère la génération et le déclenchement d'événements aléatoires en fonction
 * de diverses conditions (temps, météo, zone, etc.).
 */
public class RandomEventSystem {
    
    // Instance singleton
    private static RandomEventSystem instance;
    
    // Liste des événements aléatoires
    private List<RandomEvent> events;
    
    // Événements actuellement actifs
    private List<RandomEvent> activeEvents;
    
    // Événements récemment déclenchés (pour éviter les répétitions)
    private Map<String, Float> recentlyTriggeredEvents;
    
    // Temps minimum entre deux déclenchements du même événement (en secondes)
    private static final float MIN_EVENT_COOLDOWN = 3600.0f; // 1 heure de jeu
    
    // Générateur de nombres aléatoires
    private Random random;
    
    // Callback pour les événements
    private RandomEventCallback eventCallback;
    
    /**
     * Constructeur privé (singleton)
     */
    private RandomEventSystem() {
        this.events = new ArrayList<>();
        this.activeEvents = new ArrayList<>();
        this.recentlyTriggeredEvents = new HashMap<>();
        this.random = new Random();
        
        // Initialiser les événements
        initializeEvents();
    }
    
    /**
     * Obtenir l'instance unique du système d'événements aléatoires
     * 
     * @return Instance du RandomEventSystem
     */
    public static RandomEventSystem getInstance() {
        if (instance == null) {
            instance = new RandomEventSystem();
        }
        return instance;
    }
    
    /**
     * Initialiser les événements aléatoires
     */
    private void initializeEvents() {
        // Événements liés à la météo
        setupWeatherEvents();
        
        // Événements liés aux créatures
        setupCreatureEvents();
        
        // Événements liés aux objets
        setupItemEvents();
        
        // Événements spéciaux
        setupSpecialEvents();
        
        // Événement du marchand ambulant
        setupMerchantEvent();
    }
    
    /**
     * Événements liés à la météo
     */
    private void setupWeatherEvents() {
        // Tempête soudaine
        RandomEvent suddenStorm = new RandomEvent(
                "sudden_storm",
                "Tempête soudaine",
                "Une tempête violente éclate soudainement !",
                EventType.WEATHER,
                0.5f
        );
        suddenStorm.addWeatherCondition(Weather.CLEAR, 0.7f);
        suddenStorm.addWeatherCondition(Weather.SUNNY, 0.8f);
        suddenStorm.addTimeCondition(TimeOfDay.AFTERNOON, 1.5f);
        suddenStorm.addTimeCondition(TimeOfDay.EVENING, 1.2f);
        suddenStorm.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Changer la météo en tempête
                WeatherSystem.getInstance().setCurrentWeather(Weather.STORMY);
                
                // Ajouter un message dans le journal du joueur
                player.addJournalEntry("Une tempête violente a éclaté soudainement alors que j'explorais.");
            }
        });
        events.add(suddenStorm);
        
        // Brouillard mystérieux
        RandomEvent mysteriousFog = new RandomEvent(
                "mysterious_fog",
                "Brouillard mystérieux",
                "Un brouillard épais et mystérieux vous enveloppe...",
                EventType.WEATHER,
                0.4f
        );
        mysteriousFog.addWeatherCondition(Weather.CLEAR, 0.5f);
        mysteriousFog.addWeatherCondition(Weather.CLOUDY, 0.8f);
        mysteriousFog.addTimeCondition(TimeOfDay.MORNING, 0.7f);
        mysteriousFog.addTimeCondition(TimeOfDay.EVENING, 1.5f);
        mysteriousFog.addTimeCondition(TimeOfDay.NIGHT, 2.0f);
        mysteriousFog.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Changer la météo en brouillard
                WeatherSystem.getInstance().setCurrentWeather(Weather.FOGGY);
                
                // Ajouter un message dans le journal du joueur
                player.addJournalEntry("Un brouillard mystérieux s'est levé. J'ai l'impression d'être observé...");
                
                // 20% de chance de faire apparaître une créature rare
                if (random.nextFloat() < 0.2f) {
                    // Créer une créature rare adaptée au brouillard (type SHADOW ou PSYCHIC)
                    CreatureType type = random.nextBoolean() ? CreatureType.SHADOW : CreatureType.PSYCHIC;
                    int level = player.getLevel() + random.nextInt(3) - 1; // Niveau du joueur +/- 1
                    
                    // Trouver un ID de créature correspondant au type
                    int creatureId = getRandomCreatureIdByType(type);
                    
                    if (creatureId > 0) {
                        Creature creature = CreatureFactory.createCreature(creatureId, level);
                        if (creature != null) {
                            // Notifier le callback pour faire apparaître la créature
                            if (eventCallback != null) {
                                eventCallback.onCreatureAppear(creature);
                            }
                        }
                    }
                }
            }
        });
        events.add(mysteriousFog);
    }
    
    /**
     * Événements liés aux créatures
     */
    private void setupCreatureEvents() {
        // Migration de créatures
        RandomEvent creatureMigration = new RandomEvent(
                "creature_migration",
                "Migration de créatures",
                "Un groupe de créatures migre à travers la région !",
                EventType.CREATURE,
                0.3f
        );
        creatureMigration.addTimeCondition(TimeOfDay.MORNING, 1.2f);
        creatureMigration.addTimeCondition(TimeOfDay.AFTERNOON, 1.0f);
        creatureMigration.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Déterminer le type de créature qui migre
                CreatureType[] commonTypes = {
                    CreatureType.AIR, CreatureType.EARTH, CreatureType.WATER, CreatureType.FIRE
                };
                CreatureType migrationType = commonTypes[random.nextInt(commonTypes.length)];
                
                // Ajouter un message dans le journal du joueur
                player.addJournalEntry("J'ai observé une migration de créatures de type " + 
                        migrationType.name() + ". C'est une bonne occasion pour en capturer !");
                
                // Notifier le callback pour augmenter temporairement le taux d'apparition de ce type
                if (eventCallback != null) {
                    eventCallback.onCreatureSpawnRateChange(migrationType, 3.0f, 600.0f); // x3 pendant 10 minutes
                }
            }
        });
        events.add(creatureMigration);
        
        // Créature légendaire aperçue
        RandomEvent legendaryCreature = new RandomEvent(
                "legendary_creature",
                "Créature légendaire aperçue",
                "Une créature légendaire a été aperçue dans les environs !",
                EventType.CREATURE,
                0.1f
        );
        legendaryCreature.setMinPlayerLevel(15); // Nécessite un niveau minimum
        legendaryCreature.addTimeCondition(TimeOfDay.NIGHT, 1.5f);
        legendaryCreature.addWeatherCondition(Weather.STORMY, 1.5f);
        legendaryCreature.addWeatherCondition(Weather.FOGGY, 1.2f);
        legendaryCreature.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Déterminer le type de créature légendaire
                CreatureType[] legendaryTypes = {
                    CreatureType.MYTHICAL, CreatureType.LIGHT, CreatureType.SHADOW
                };
                CreatureType legendaryType = legendaryTypes[random.nextInt(legendaryTypes.length)];
                
                // Ajouter un message dans le journal du joueur
                player.addJournalEntry("Des rumeurs circulent à propos d'une créature légendaire de type " + 
                        legendaryType.name() + " aperçue dans la région. Je devrais enquêter !");
                
                // Ajouter une quête spéciale pour trouver cette créature
                if (eventCallback != null) {
                    eventCallback.onSpecialQuestAvailable("legendary_hunt_" + legendaryType.name().toLowerCase());
                }
            }
        });
        events.add(legendaryCreature);
    }
    
    /**
     * Événements liés aux objets
     */
    private void setupItemEvents() {
        // Marchand ambulant
        RandomEvent wanderingMerchant = new RandomEvent(
                "wandering_merchant",
                "Marchand ambulant",
                "Un marchand ambulant propose des objets rares !",
                EventType.ITEM,
                0.4f
        );
        wanderingMerchant.addTimeCondition(TimeOfDay.AFTERNOON, 1.5f);
        wanderingMerchant.addTimeCondition(TimeOfDay.EVENING, 1.2f);
        wanderingMerchant.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Ajouter un message dans le journal du joueur
                player.addJournalEntry("J'ai rencontré un marchand ambulant qui propose des objets rares. Je devrais voir ce qu'il a à vendre.");
                
                // Générer 3-5 objets rares pour le marchand
                int itemCount = 3 + random.nextInt(3);
                List<Item> merchantItems = new ArrayList<>();
                
                for (int i = 0; i < itemCount; i++) {
                    // Générer un ID d'objet rare aléatoire
                    int itemId = 100 + random.nextInt(50); // IDs 100-149 pour les objets rares
                    Item item = ItemFactory.getInstance().createItem(itemId);
                    if (item != null) {
                        merchantItems.add(item);
                    }
                }
                
                // Notifier le callback pour afficher le marchand
                if (eventCallback != null) {
                    eventCallback.onMerchantAppear(merchantItems);
                }
            }
        });
        events.add(wanderingMerchant);
        
        // Trésor caché
        RandomEvent hiddenTreasure = new RandomEvent(
                "hidden_treasure",
                "Trésor caché",
                "Vous avez découvert un indice sur un trésor caché !",
                EventType.ITEM,
                0.2f
        );
        hiddenTreasure.addWeatherCondition(Weather.RAINY, 1.3f);
        hiddenTreasure.addWeatherCondition(Weather.STORMY, 1.5f);
        hiddenTreasure.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Ajouter un message dans le journal du joueur
                player.addJournalEntry("J'ai trouvé un vieux parchemin qui semble indiquer l'emplacement d'un trésor caché. Je devrais suivre ces indices !");
                
                // Créer une quête de chasse au trésor
                if (eventCallback != null) {
                    eventCallback.onSpecialQuestAvailable("treasure_hunt");
                }
            }
        });
        events.add(hiddenTreasure);
    }
    
    /**
     * Événements spéciaux
     */
    private void setupSpecialEvents() {
        // Éclipse lunaire
        RandomEvent lunarEclipse = new RandomEvent(
                "lunar_eclipse",
                "Éclipse lunaire",
                "Une éclipse lunaire rare va se produire cette nuit !",
                EventType.SPECIAL,
                0.05f
        );
        lunarEclipse.addTimeCondition(TimeOfDay.EVENING, 2.0f);
        lunarEclipse.addWeatherCondition(Weather.CLEAR, 2.0f);
        lunarEclipse.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Ajouter un message dans le journal du joueur
                player.addJournalEntry("Une éclipse lunaire rare va se produire cette nuit. Les créatures de type SHADOW et PSYCHIC seront plus actives.");
                
                // Augmenter temporairement le taux d'apparition des créatures SHADOW et PSYCHIC
                if (eventCallback != null) {
                    eventCallback.onCreatureSpawnRateChange(CreatureType.SHADOW, 5.0f, 1800.0f); // x5 pendant 30 minutes
                    eventCallback.onCreatureSpawnRateChange(CreatureType.PSYCHIC, 3.0f, 1800.0f); // x3 pendant 30 minutes
                    
                    // Déclencher un effet visuel spécial
                    eventCallback.onSpecialVisualEffect("eclipse");
                }
            }
        });
        events.add(lunarEclipse);
        
        // Festival des variants
        RandomEvent variantFestival = new RandomEvent(
                "variant_festival",
                "Festival des variants",
                "Un festival en l'honneur des variants se tient dans la région !",
                EventType.SPECIAL,
                0.1f
        );
        variantFestival.addTimeCondition(TimeOfDay.AFTERNOON, 1.5f);
        variantFestival.addTimeCondition(TimeOfDay.EVENING, 1.5f);
        variantFestival.addWeatherCondition(Weather.CLEAR, 1.3f);
        variantFestival.addWeatherCondition(Weather.SUNNY, 1.5f);
        variantFestival.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Ajouter un message dans le journal du joueur
                player.addJournalEntry("Un festival en l'honneur des variants se tient dans la région ! C'est l'occasion de participer à des combats amicaux et de gagner des prix.");
                
                // Créer une quête spéciale pour le festival
                if (eventCallback != null) {
                    eventCallback.onSpecialQuestAvailable("variant_festival");
                    
                    // Ajouter des PNJ spéciaux pour le festival
                    eventCallback.onSpecialNPCsAppear("festival");
                }
            }
        });
        events.add(variantFestival);
    }
    
    /**
     * Événement spécial : Marchand ambulant
     */
    private void setupMerchantEvent() {
        RandomEvent event = new RandomEvent(
                "merchant_1",
                "Marchand ambulant",
                "Un marchand ambulant est arrivé avec des objets rares à vendre.",
                EventType.SPECIAL,
                0.05f
        );
        
        // Plus probable pendant la journée et par beau temps
        event.addTimeCondition(TimeOfDay.MORNING, 1.5f);
        event.addTimeCondition(TimeOfDay.AFTERNOON, 2.0f);
        event.addTimeCondition(TimeOfDay.EVENING, 1.5f);
        event.addWeatherCondition(Weather.CLEAR, 1.5f);
        event.addWeatherCondition(Weather.CLOUDY, 1.0f);
        event.addWeatherCondition(Weather.RAINY, 0.5f);
        event.addWeatherCondition(Weather.STORMY, 0.1f);
        
        // Niveau minimum du joueur
        event.setMinPlayerLevel(5);
        
        // Action à exécuter
        event.setAction(new RandomEventAction() {
            @Override
            public void execute(Player player, GameState gameState) {
                // Générer 3-5 objets rares
                Random random = new Random();
                int itemCount = random.nextInt(3) + 3; // 3 à 5 objets
                
                // Créer des objets rares (rareté 5-8)
                List<Item> items = ItemFactory.getInstance().createRandomItems(itemCount, 5, 8);
                
                // Ajouter l'événement au journal du joueur
                player.addJournalEntry("Un marchand ambulant est apparu avec " + itemCount + " objets rares à vendre.");
                
                // Déclencher l'apparition du marchand
                if (eventCallback != null) {
                    eventCallback.onMerchantAppear(items);
                }
            }
        });
        
        // Ajouter l'événement à la liste
        events.add(event);
    }
    
    /**
     * Définir le callback pour les événements
     * 
     * @param callback Callback à appeler lors du déclenchement d'un événement
     */
    public void setEventCallback(RandomEventCallback callback) {
        this.eventCallback = callback;
    }
    
    /**
     * Mettre à jour le système d'événements aléatoires
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour en secondes
     * @param player Joueur
     * @param gameState État du jeu
     */
    public void update(float deltaTime, Player player, GameState gameState) {
        // Mettre à jour les événements récemment déclenchés (réduire le temps de cooldown)
        updateRecentlyTriggeredEvents(deltaTime);
        
        // Mettre à jour les événements actifs
        updateActiveEvents(deltaTime, player, gameState);
        
        // Vérifier si on peut déclencher un nouvel événement
        checkForNewEvents(player, gameState);
    }
    
    /**
     * Mettre à jour les événements récemment déclenchés
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour en secondes
     */
    private void updateRecentlyTriggeredEvents(float deltaTime) {
        List<String> eventsToRemove = new ArrayList<>();
        
        for (Map.Entry<String, Float> entry : recentlyTriggeredEvents.entrySet()) {
            float remainingCooldown = entry.getValue() - deltaTime;
            
            if (remainingCooldown <= 0.0f) {
                // Le cooldown est terminé, supprimer l'événement de la map
                eventsToRemove.add(entry.getKey());
            } else {
                // Mettre à jour le cooldown
                entry.setValue(remainingCooldown);
            }
        }
        
        // Supprimer les événements dont le cooldown est terminé
        for (String eventId : eventsToRemove) {
            recentlyTriggeredEvents.remove(eventId);
        }
    }
    
    /**
     * Mettre à jour les événements actifs
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour en secondes
     * @param player Joueur
     * @param gameState État du jeu
     */
    private void updateActiveEvents(float deltaTime, Player player, GameState gameState) {
        List<RandomEvent> finishedEvents = new ArrayList<>();
        
        for (RandomEvent event : activeEvents) {
            // Mettre à jour la durée de l'événement
            event.update(deltaTime);
            
            // Vérifier si l'événement est terminé
            if (event.isFinished()) {
                finishedEvents.add(event);
                
                // Notifier la fin de l'événement
                if (eventCallback != null) {
                    eventCallback.onEventEnded(event);
                }
            }
        }
        
        // Supprimer les événements terminés
        activeEvents.removeAll(finishedEvents);
    }
    
    /**
     * Vérifier si on peut déclencher un nouvel événement
     * 
     * @param player Joueur
     * @param gameState État du jeu
     */
    private void checkForNewEvents(Player player, GameState gameState) {
        // Obtenir les conditions actuelles
        TimeOfDay currentTimeOfDay = TimeSystem.getInstance().getCurrentTimeOfDay();
        Weather currentWeather = WeatherSystem.getInstance().getCurrentWeather();
        String currentZone = WorldManager.getInstance().getCurrentZoneName();
        int playerLevel = player.getLevel();
        
        // Liste des événements possibles avec leur poids
        List<RandomEvent> eligibleEvents = new ArrayList<>();
        Map<RandomEvent, Float> eventWeights = new HashMap<>();
        
        // Parcourir tous les événements possibles
        for (RandomEvent event : events) {
            // Vérifier si l'événement est en cooldown
            if (recentlyTriggeredEvents.containsKey(event.getId())) {
                continue;
            }
            
            // Vérifier le niveau minimum du joueur
            if (playerLevel < event.getMinPlayerLevel()) {
                continue;
            }
            
            // Calculer le poids de l'événement en fonction des conditions actuelles
            float weight = event.getBaseProbability();
            
            // Modifier le poids en fonction du moment de la journée
            Float timeModifier = event.getTimeConditions().get(currentTimeOfDay);
            if (timeModifier != null) {
                weight *= timeModifier;
            }
            
            // Modifier le poids en fonction de la météo
            Float weatherModifier = event.getWeatherConditions().get(currentWeather);
            if (weatherModifier != null) {
                weight *= weatherModifier;
            }
            
            // Modifier le poids en fonction de la zone
            Float zoneModifier = event.getZoneConditions().get(currentZone);
            if (zoneModifier != null) {
                weight *= zoneModifier;
            }
            
            // Si le poids est supérieur à zéro, ajouter l'événement à la liste des événements éligibles
            if (weight > 0.0f) {
                eligibleEvents.add(event);
                eventWeights.put(event, weight);
            }
        }
        
        // Si aucun événement n'est éligible, retourner
        if (eligibleEvents.isEmpty()) {
            return;
        }
        
        // Générer un nombre aléatoire entre 0 et 1
        float randomValue = random.nextFloat();
        
        // Calculer la probabilité de base de déclencher un événement (par exemple, 5% de chance)
        float baseProbability = 0.05f;
        
        // Si le nombre aléatoire est inférieur à la probabilité de base, déclencher un événement
        if (randomValue < baseProbability) {
            // Sélectionner un événement en fonction de son poids
            RandomEvent selectedEvent = selectRandomEventByWeight(eligibleEvents, eventWeights);
            
            if (selectedEvent != null) {
                // Déclencher l'événement
                triggerEvent(selectedEvent, player, gameState);
            }
        }
    }
    
    /**
     * Sélectionner un événement aléatoire en fonction de son poids
     * 
     * @param eligibleEvents Liste des événements éligibles
     * @param eventWeights Poids des événements
     * @return Événement sélectionné
     */
    private RandomEvent selectRandomEventByWeight(List<RandomEvent> eligibleEvents, Map<RandomEvent, Float> eventWeights) {
        // Calculer la somme totale des poids
        float totalWeight = 0.0f;
        for (float weight : eventWeights.values()) {
            totalWeight += weight;
        }
        
        // Générer un nombre aléatoire entre 0 et la somme totale des poids
        float randomValue = random.nextFloat() * totalWeight;
        
        // Sélectionner un événement en fonction de son poids
        float currentWeight = 0.0f;
        for (RandomEvent event : eligibleEvents) {
            currentWeight += eventWeights.get(event);
            if (randomValue <= currentWeight) {
                return event;
            }
        }
        
        // Par défaut, retourner le premier événement éligible
        return eligibleEvents.get(0);
    }
    
    /**
     * Déclencher un événement
     * 
     * @param event Événement à déclencher
     * @param player Joueur
     * @param gameState État du jeu
     */
    private void triggerEvent(RandomEvent event, Player player, GameState gameState) {
        // Ajouter l'événement à la liste des événements actifs
        activeEvents.add(event);
        
        // Ajouter l'événement à la map des événements récemment déclenchés avec un cooldown
        recentlyTriggeredEvents.put(event.getId(), MIN_EVENT_COOLDOWN);
        
        // Exécuter l'action de l'événement
        if (event.getAction() != null) {
            event.getAction().execute(player, gameState);
        }
        
        // Notifier le callback
        if (eventCallback != null) {
            eventCallback.onEventTriggered(event);
        }
    }
    
    /**
     * Obtenir un ID de créature aléatoire en fonction du type
     * 
     * @param type Type de créature
     * @return ID de créature, ou -1 si aucune créature n'est trouvée
     */
    private int getRandomCreatureIdByType(CreatureType type) {
        // Cette méthode devrait être implémentée pour retourner un ID de créature
        // correspondant au type spécifié. Pour l'instant, on utilise des valeurs fictives.
        switch (type) {
            case FIRE:
                return random.nextInt(3) + 1; // IDs 1-3 pour les créatures de feu
            case WATER:
                return random.nextInt(3) + 4; // IDs 4-6 pour les créatures d'eau
            case EARTH:
                return random.nextInt(3) + 7; // IDs 7-9 pour les créatures de terre
            case AIR:
                return random.nextInt(3) + 10; // IDs 10-12 pour les créatures d'air
            case LIGHT:
                return random.nextInt(3) + 13; // IDs 13-15 pour les créatures de lumière
            case SHADOW:
                return random.nextInt(3) + 16; // IDs 16-18 pour les créatures d'ombre
            case NATURE:
                return random.nextInt(3) + 19; // IDs 19-21 pour les créatures de nature
            case ELECTRIC:
                return random.nextInt(3) + 22; // IDs 22-24 pour les créatures électriques
            case ICE:
                return random.nextInt(3) + 25; // IDs 25-27 pour les créatures de glace
            case METAL:
                return random.nextInt(3) + 28; // IDs 28-30 pour les créatures de métal
            case PSYCHIC:
                return random.nextInt(3) + 31; // IDs 31-33 pour les créatures psychiques
            case MYTHICAL:
                return random.nextInt(3) + 34; // IDs 34-36 pour les créatures mythiques
            default:
                return -1;
        }
    }
    
    /**
     * Types d'événements
     */
    public enum EventType {
        WEATHER,    // Événements liés à la météo
        CREATURE,   // Événements liés aux créatures
        ITEM,       // Événements liés aux objets
        SPECIAL     // Événements spéciaux
    }
    
    /**
     * Interface pour les actions d'événements
     */
    public interface RandomEventAction {
        /**
         * Exécuter l'action de l'événement
         * 
         * @param player Joueur
         * @param gameState État du jeu
         */
        void execute(Player player, GameState gameState);
    }
    
    /**
     * Interface pour les callbacks d'événements
     */
    public interface RandomEventCallback {
        /**
         * Appelé lors du déclenchement d'un événement
         * 
         * @param event Événement déclenché
         */
        void onEventTriggered(RandomEvent event);
        
        /**
         * Appelé lors de la fin d'un événement
         * 
         * @param event Événement terminé
         */
        void onEventEnded(RandomEvent event);
        
        /**
         * Appelé lors de l'apparition d'une créature suite à un événement
         * 
         * @param creature Créature qui apparaît
         */
        void onCreatureAppear(Creature creature);
        
        /**
         * Appelé lorsque le taux d'apparition d'un type de créature change
         * 
         * @param type Type de créature
         * @param multiplier Multiplicateur du taux d'apparition
         * @param duration Durée de l'effet en secondes
         */
        void onCreatureSpawnRateChange(CreatureType type, float multiplier, float duration);
        
        /**
         * Appelé lors de l'apparition d'un marchand suite à un événement
         * 
         * @param items Liste des objets proposés par le marchand
         */
        void onMerchantAppear(List<Item> items);
        
        /**
         * Appelé lorsque une quête spéciale est disponible
         * 
         * @param questId ID de la quête
         */
        void onSpecialQuestAvailable(String questId);
        
        /**
         * Appelé lorsque des PNJ spéciaux apparaissent
         * 
         * @param npcGroup Groupe de PNJ
         */
        void onSpecialNPCsAppear(String npcGroup);
        
        /**
         * Appelé lorsque un effet visuel spécial doit être affiché
         * 
         * @param effectId ID de l'effet
         */
        void onSpecialVisualEffect(String effectId);
    }
}

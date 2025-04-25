package com.ryuukonpalace.game.core;

import com.ryuukonpalace.game.combat.CombatSystem;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureType;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.save.SaveManager;
import com.ryuukonpalace.game.ui.UIManager;
import com.ryuukonpalace.game.world.RandomEvent;
import com.ryuukonpalace.game.world.RandomEventSystem;
import com.ryuukonpalace.game.world.TimeSystem;
import com.ryuukonpalace.game.world.WeatherSystem;
import com.ryuukonpalace.game.world.WorldManager;

import java.util.List;

/**
 * Gestionnaire principal du jeu.
 * Gère l'état global du jeu, les transitions entre les états et la coordination des différents systèmes.
 */
public class GameManager {
    // Singleton
    private static GameManager instance;
    
    // Systèmes
    private CombatSystem combatSystem;
    private SaveManager saveManager;
    private UIManager uiManager;
    private RandomEventSystem randomEventSystem;
    private TimeSystem timeSystem;
    private WeatherSystem weatherSystem;
    private WorldManager worldManager;
    
    // État du jeu
    private GameState gameState;
    private GameState.State currentState;
    
    // Joueur
    private Player player;
    
    /**
     * Constructeur privé (singleton)
     */
    private GameManager() {
        this.currentState = GameState.State.MAIN_MENU;
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire de jeu
     * 
     * @return Instance de GameManager
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
    
    /**
     * Initialiser le gestionnaire de jeu
     */
    public void init() {
        // Initialiser les systèmes
        this.combatSystem = CombatSystem.getInstance();
        this.saveManager = SaveManager.getInstance();
        this.uiManager = UIManager.getInstance();
        this.randomEventSystem = RandomEventSystem.getInstance();
        this.timeSystem = TimeSystem.getInstance();
        this.weatherSystem = WeatherSystem.getInstance();
        this.worldManager = WorldManager.getInstance();
        
        // Obtenir l'instance du GameState
        this.gameState = GameState.getInstance();
        
        // Créer un joueur par défaut
        this.player = new Player();
        
        // Configurer le callback pour les événements aléatoires
        setupRandomEventCallback();
    }
    
    /**
     * Configurer le callback pour les événements aléatoires
     */
    private void setupRandomEventCallback() {
        randomEventSystem.setEventCallback(new RandomEventSystem.RandomEventCallback() {
            @Override
            public void onEventTriggered(RandomEvent event) {
                // Afficher une notification à l'utilisateur
                uiManager.showNotification(event.getName(), event.getDescription(), 5.0f);
                
                // Ajouter l'événement au journal du joueur
                player.addJournalEntry(event.getDescription());
                
                System.out.println("Événement déclenché : " + event.getName());
            }
            
            @Override
            public void onEventEnded(RandomEvent event) {
                // Afficher une notification de fin d'événement si nécessaire
                if (event.getDuration() > 0.0f) {
                    uiManager.showNotification("Fin d'événement", "L'événement " + event.getName() + " est terminé.", 3.0f);
                    System.out.println("Événement terminé : " + event.getName());
                }
            }
            
            @Override
            public void onCreatureAppear(Creature creature) {
                // Faire apparaître la créature dans le monde
                worldManager.spawnCreature(creature);
                
                // Afficher une notification
                uiManager.showNotification("Créature apparue", "Une créature " + creature.getName() + " est apparue !", 3.0f);
                System.out.println("Créature apparue : " + creature.getName());
            }
            
            @Override
            public void onCreatureSpawnRateChange(CreatureType type, float multiplier, float duration) {
                // Modifier temporairement le taux d'apparition des créatures
                worldManager.setCreatureSpawnRateMultiplier(type, multiplier, duration);
                
                // Afficher une notification
                String message = "Les créatures de type " + type.name() + " apparaissent " + 
                        (multiplier > 1.0f ? "plus" : "moins") + " fréquemment pendant " + 
                        (int)(duration / 60.0f) + " minutes.";
                uiManager.showNotification("Taux d'apparition modifié", message, 3.0f);
                System.out.println(message);
            }
            
            @Override
            public void onMerchantAppear(List<Item> items) {
                // Faire apparaître un marchand avec les objets spécifiés
                uiManager.showMerchant(items);
                
                // Afficher une notification
                uiManager.showNotification("Marchand ambulant", "Un marchand ambulant est arrivé avec des objets rares !", 3.0f);
                System.out.println("Marchand apparu avec " + items.size() + " objets");
            }
            
            @Override
            public void onSpecialQuestAvailable(String questId) {
                // Ajouter une quête spéciale
                gameState.addSpecialQuest(questId);
                
                // Afficher une notification
                uiManager.showNotification("Nouvelle quête", "Une quête spéciale est disponible !", 3.0f);
                System.out.println("Quête spéciale disponible : " + questId);
            }
            
            @Override
            public void onSpecialNPCsAppear(String npcGroup) {
                // Faire apparaître des PNJ spéciaux
                worldManager.spawnSpecialNPCs(npcGroup);
                
                // Afficher une notification
                uiManager.showNotification("PNJ spéciaux", "Des PNJ spéciaux sont apparus dans la région !", 3.0f);
                System.out.println("PNJ spéciaux apparus : " + npcGroup);
            }
            
            @Override
            public void onSpecialVisualEffect(String effectId) {
                // Afficher un effet visuel spécial
                uiManager.showSpecialEffect(effectId);
                
                // Afficher une notification
                uiManager.showNotification("Effet spécial", "Un phénomène étrange se produit...", 3.0f);
                System.out.println("Effet visuel spécial : " + effectId);
            }
        });
    }
    
    /**
     * Mettre à jour le jeu
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        // Mettre à jour les systèmes de temps et de météo
        timeSystem.update(deltaTime);
        weatherSystem.update(deltaTime);
        
        // Mettre à jour le système d'événements aléatoires si le joueur est en exploration
        if (currentState == GameState.State.PLAYING) {
            randomEventSystem.update(deltaTime, player, gameState);
        }
        
        // Mettre à jour selon l'état actuel
        switch (currentState) {
            case MAIN_MENU:
                // Logique du menu principal
                break;
            case PLAYING:
                // Logique d'exploration
                worldManager.update(deltaTime);
                break;
            case COMBAT:
                // Mettre à jour le système de combat
                combatSystem.update(deltaTime);
                break;
            case DIALOG:
                // Logique de dialogue
                break;
            case PAUSED:
                // Logique de pause
                break;
            case SAVE_LOAD:
                // Logique de sauvegarde/chargement
                break;
            case CUTSCENE:
                // Logique de cinématique
                break;
            case GAME_OVER:
                // Logique de game over
                break;
        }
    }
    
    /**
     * Changer l'état du jeu
     * 
     * @param newState Nouvel état
     */
    public void changeState(GameState.State newState) {
        // Quitter l'état actuel
        exitState(currentState);
        
        // Changer l'état
        currentState = newState;
        
        // Entrer dans le nouvel état
        enterState(newState);
    }
    
    /**
     * Actions à effectuer en quittant un état
     * 
     * @param state État à quitter
     */
    private void exitState(GameState.State state) {
        switch (state) {
            case COMBAT:
                // Nettoyer le système de combat
                break;
            default:
                // Rien à faire pour les autres états
                break;
        }
    }
    
    /**
     * Actions à effectuer en entrant dans un état
     * 
     * @param state État à entrer
     */
    private void enterState(GameState.State state) {
        switch (state) {
            case COMBAT:
                // Initialiser le système de combat
                break;
            default:
                // Rien à faire pour les autres états
                break;
        }
    }
    
    /**
     * Obtenir l'état actuel du jeu
     * 
     * @return État actuel
     */
    public GameState.State getCurrentState() {
        return currentState;
    }
    
    /**
     * Obtenir le joueur
     * 
     * @return Joueur
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Définir le joueur
     * 
     * @param player Joueur
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    /**
     * Obtenir l'état du jeu
     * 
     * @return État du jeu
     */
    public GameState getGameState() {
        return gameState;
    }
    
    /**
     * Définir l'état du jeu
     * 
     * @param gameState Nouvel état du jeu
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}

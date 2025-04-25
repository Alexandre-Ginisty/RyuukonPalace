package com.ryuukonpalace.game.ui;

import com.ryuukonpalace.game.core.GameState;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.items.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Gestionnaire d'interface utilisateur.
 * Gère toutes les interfaces du jeu et coordonne leur affichage et leurs interactions.
 */
public class UIManager {
    // Singleton
    private static UIManager instance;
    
    // Références
    private Renderer renderer;
    private InputManager inputManager;
    
    // Interfaces
    private CombatInterface combatInterface;
    private SaveLoadInterface saveLoadInterface;
    private InventoryInterface inventoryInterface;
    private CreatureStatusScreen creatureStatusScreen;
    
    // Système de notification
    private List<Notification> activeNotifications;
    private float notificationDisplayTime = 5.0f; // Temps d'affichage par défaut en secondes
    
    // Effets visuels spéciaux
    private List<VisualEffect> activeEffects;
    
    // Interface du marchand
    private MerchantInterface merchantInterface;
    
    /**
     * Constructeur privé (singleton)
     */
    private UIManager() {
        this.renderer = Renderer.getInstance();
        this.inputManager = InputManager.getInstance();
        this.activeNotifications = new ArrayList<>();
        this.activeEffects = new ArrayList<>();
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire d'interface
     * 
     * @return Instance de UIManager
     */
    public static UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }
    
    /**
     * Initialiser le gestionnaire d'interface
     */
    public void init() {
        // Initialiser les interfaces
        this.combatInterface = CombatInterface.getInstance();
        this.combatInterface.init();
        
        this.saveLoadInterface = new SaveLoadInterface();
        
        this.inventoryInterface = InventoryInterface.getInstance();
        this.inventoryInterface.init();
        
        this.creatureStatusScreen = new CreatureStatusScreen();
        
        this.merchantInterface = new MerchantInterface();
    }
    
    /**
     * Mettre à jour les interfaces
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        // Mettre à jour les interfaces selon l'état du jeu
        GameState.State currentState = GameState.getInstance().getCurrentState();
        
        // Mettre à jour les notifications
        updateNotifications(deltaTime);
        
        // Mettre à jour les effets visuels
        updateVisualEffects(deltaTime);
        
        switch (currentState) {
            case COMBAT:
                if (combatInterface.isInitialized()) {
                    combatInterface.update(deltaTime);
                }
                break;
            case SAVE_LOAD:
                saveLoadInterface.update(deltaTime);
                break;
            case PAUSED:
                // Mettre à jour l'interface de pause
                break;
            default:
                // Mettre à jour les interfaces communes à tous les états
                if (merchantInterface.isVisible()) {
                    merchantInterface.update(deltaTime);
                }
                break;
        }
    }
    
    /**
     * Mettre à jour les notifications
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    private void updateNotifications(float deltaTime) {
        Iterator<Notification> iterator = activeNotifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            notification.update(deltaTime);
            if (notification.isExpired()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Mettre à jour les effets visuels
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    private void updateVisualEffects(float deltaTime) {
        Iterator<VisualEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            VisualEffect effect = iterator.next();
            effect.update(deltaTime);
            if (effect.isFinished()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Dessiner les interfaces
     */
    public void render() {
        // Dessiner les interfaces selon l'état du jeu
        GameState.State currentState = GameState.getInstance().getCurrentState();
        
        // Dessiner les notifications (toujours au-dessus de tout)
        renderNotifications();
        
        // Dessiner les effets visuels
        renderVisualEffects();
        
        switch (currentState) {
            case COMBAT:
                if (combatInterface.isInitialized() && combatInterface.isVisible()) {
                    combatInterface.render();
                }
                break;
            case SAVE_LOAD:
                saveLoadInterface.render();
                break;
            case PAUSED:
                // Dessiner l'interface de pause
                break;
            default:
                // Dessiner les interfaces communes à tous les états
                if (merchantInterface.isVisible()) {
                    merchantInterface.render();
                }
                break;
        }
    }
    
    /**
     * Dessiner les notifications actives
     */
    private void renderNotifications() {
        float yOffset = 10.0f; // Marge supérieure
        for (Notification notification : activeNotifications) {
            notification.render(yOffset);
            yOffset += notification.getHeight() + 5.0f; // Espacement entre les notifications
        }
    }
    
    /**
     * Dessiner les effets visuels actifs
     */
    private void renderVisualEffects() {
        for (VisualEffect effect : activeEffects) {
            effect.render();
        }
    }
    
    /**
     * Gérer les entrées de l'utilisateur
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param mousePressed true si le bouton de la souris est appuyé, false sinon
     */
    public void handleInput(float mouseX, float mouseY, boolean mousePressed) {
        // Gérer les entrées selon l'état du jeu
        GameState.State currentState = GameState.getInstance().getCurrentState();
        
        switch (currentState) {
            case COMBAT:
                if (combatInterface.isInitialized() && combatInterface.isVisible()) {
                    combatInterface.handleInput(mouseX, mouseY, mousePressed);
                }
                break;
            case SAVE_LOAD:
                saveLoadInterface.handleInput(mouseX, mouseY, mousePressed);
                break;
            case PAUSED:
                // Gérer les entrées de l'interface de pause
                break;
            default:
                // Gérer les entrées communes à tous les états
                break;
        }
    }
    
    /**
     * Obtenir l'interface de combat
     * 
     * @return Interface de combat
     */
    public CombatInterface getCombatInterface() {
        return combatInterface;
    }
    
    /**
     * Obtenir l'interface de sauvegarde/chargement
     * 
     * @return Interface de sauvegarde/chargement
     */
    public SaveLoadInterface getSaveLoadInterface() {
        return saveLoadInterface;
    }
    
    /**
     * Obtenir l'interface d'inventaire
     * 
     * @return Interface d'inventaire
     */
    public InventoryInterface getInventoryInterface() {
        return inventoryInterface;
    }
    
    /**
     * Obtenir l'écran de statut des créatures
     * 
     * @return Écran de statut des créatures
     */
    public CreatureStatusScreen getCreatureStatusScreen() {
        return creatureStatusScreen;
    }
    
    /**
     * Afficher une notification à l'utilisateur
     * 
     * @param title Titre de la notification
     * @param message Message de la notification
     * @param duration Durée d'affichage en secondes
     */
    public void showNotification(String title, String message, float duration) {
        Notification notification = new Notification(title, message, duration);
        activeNotifications.add(notification);
        
        // Limiter le nombre de notifications simultanées
        if (activeNotifications.size() > 5) {
            activeNotifications.remove(0); // Supprimer la plus ancienne
        }
        
        System.out.println("[Notification] " + title + ": " + message);
    }
    
    /**
     * Afficher un effet visuel spécial
     * 
     * @param effectId Identifiant de l'effet à afficher
     */
    public void showSpecialEffect(String effectId) {
        VisualEffect effect = new VisualEffect(effectId);
        activeEffects.add(effect);
        
        System.out.println("[Effet visuel] " + effectId);
    }
    
    /**
     * Afficher l'interface du marchand avec des objets
     * 
     * @param items Liste des objets à vendre
     */
    public void showMerchant(List<Item> items) {
        merchantInterface.setItems(items);
        merchantInterface.setVisible(true);
        
        System.out.println("[Marchand] " + items.size() + " objets disponibles");
    }
    
    /**
     * Fermer l'interface du marchand
     */
    public void hideMerchant() {
        merchantInterface.setVisible(false);
    }
    
    /**
     * Classe interne représentant une notification
     */
    private class Notification {
        private String title;
        private String message;
        private float duration;
        private float remainingTime;
        private float alpha; // Pour l'effet de fondu
        private float height = 80.0f; // Hauteur de la notification en pixels
        
        public Notification(String title, String message, float duration) {
            this.title = title;
            this.message = message;
            this.duration = duration;
            this.remainingTime = duration;
            this.alpha = 1.0f;
        }
        
        public void update(float deltaTime) {
            remainingTime -= deltaTime;
            
            // Effet de fondu en entrée/sortie
            if (remainingTime <= 1.0f) {
                alpha = remainingTime; // Fondu en sortie
            } else if (duration - remainingTime <= 1.0f) {
                alpha = duration - remainingTime; // Fondu en entrée
            } else {
                alpha = 1.0f;
            }
        }
        
        public void render(float yOffset) {
            // TODO: Implémenter le rendu de la notification
            // Utiliser renderer pour dessiner un rectangle semi-transparent
            // et afficher le titre et le message
        }
        
        public boolean isExpired() {
            return remainingTime <= 0;
        }
        
        public float getHeight() {
            return height;
        }
    }
    
    /**
     * Classe interne représentant un effet visuel spécial
     */
    private class VisualEffect {
        private String effectId;
        private float duration;
        private float elapsedTime;
        private boolean finished;
        
        public VisualEffect(String effectId) {
            this.effectId = effectId;
            this.duration = 5.0f; // Durée par défaut en secondes
            this.elapsedTime = 0.0f;
            this.finished = false;
            
            // Définir la durée en fonction de l'effet
            switch (effectId) {
                case "lightning":
                    duration = 2.0f;
                    break;
                case "rain":
                    duration = 10.0f;
                    break;
                case "fog":
                    duration = 15.0f;
                    break;
                case "aurora":
                    duration = 20.0f;
                    break;
                case "earthquake":
                    duration = 3.0f;
                    break;
                default:
                    duration = 5.0f;
                    break;
            }
        }
        
        public void update(float deltaTime) {
            elapsedTime += deltaTime;
            if (elapsedTime >= duration) {
                finished = true;
            }
        }
        
        public void render() {
            // TODO: Implémenter le rendu de l'effet visuel
            // Utiliser renderer pour dessiner l'effet en fonction de son type
        }
        
        public boolean isFinished() {
            return finished;
        }
    }
    
    /**
     * Classe interne représentant l'interface du marchand
     */
    private class MerchantInterface {
        private List<Item> items;
        private boolean visible;
        
        public MerchantInterface() {
            this.items = new ArrayList<>();
            this.visible = false;
        }
        
        public void update(float deltaTime) {
            // Mettre à jour l'interface du marchand
        }
        
        public void render() {
            // TODO: Implémenter le rendu de l'interface du marchand
            // Afficher les objets disponibles et leurs prix
        }
        
        public void setItems(List<Item> items) {
            this.items = new ArrayList<>(items);
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }
}

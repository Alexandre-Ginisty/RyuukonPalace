package com.ryuukonpalace.game.ui;

import com.ryuukonpalace.game.core.GameState;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.dialogue.DialogueOption;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.quest.Quest;
import com.ryuukonpalace.game.player.Player;

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
    // Utilisé pour les contrôles avancés qui seront implémentés dans une future mise à jour
    @SuppressWarnings("unused")
    private InputManager inputManager; // Utilisé pour les contrôles avancés
    
    // Interfaces
    private CombatInterface combatInterface;
    private SaveLoadInterface saveLoadInterface;
    private InventoryInterface inventoryInterface;
    private CreatureStatusScreen creatureStatusScreen;
    private DialogueInterface dialogueInterface;
    private QuestJournalInterface questJournalInterface;
    private MysticalSignsInterface mysticalSignsInterface;
    
    // Système de notification
    private List<Notification> activeNotifications;
    // Cette valeur sera utilisée dans une future mise à jour pour personnaliser la durée d'affichage
    @SuppressWarnings("unused")
    private float notificationDisplayTime = 5.0f; // Temps d'affichage par défaut en secondes
    
    // Effets visuels spéciaux
    private List<VisualEffect> activeEffects;
    
    // Interface du marchand
    private MerchantInterface merchantInterface;
    
    // État d'initialisation
    private boolean initialized = false;
    
    // État de visibilité des interfaces
    private boolean inventoryVisible = false;
    private boolean mysticalSignsInterfaceVisible = false;
    private boolean dialogVisible = false;
    private boolean menuVisible = false;
    
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
        
        this.dialogueInterface = new DialogueInterface();
        
        this.questJournalInterface = QuestJournalInterface.getInstance();
        this.questJournalInterface.init();
        
        this.mysticalSignsInterface = MysticalSignsInterface.getInstance();
        this.mysticalSignsInterface.init();
        
        this.initialized = true;
    }
    
    /**
     * Vérifie si le gestionnaire d'interface utilisateur est initialisé
     * 
     * @return true si le gestionnaire est initialisé, false sinon
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Affiche l'interface d'inventaire pour le joueur spécifié
     * 
     * @param player Le joueur dont l'inventaire doit être affiché
     */
    public void showInventory(Player player) {
        if (player != null && inventoryInterface != null) {
            inventoryInterface.setPlayer(player);
            inventoryInterface.setVisible(true);
            inventoryVisible = true;
        }
    }
    
    /**
     * Masque l'interface d'inventaire
     */
    public void hideInventory() {
        if (inventoryInterface != null) {
            inventoryInterface.setVisible(false);
            inventoryVisible = false;
        }
    }
    
    /**
     * Vérifie si l'interface d'inventaire est visible
     * 
     * @return true si l'interface d'inventaire est visible, false sinon
     */
    public boolean isInventoryVisible() {
        return inventoryVisible;
    }
    
    /**
     * Affiche l'interface des signes mystiques pour le joueur spécifié
     * 
     * @param player Le joueur dont les signes mystiques doivent être affichés
     */
    public void showMysticalSignsInterface(Player player) {
        if (player != null && mysticalSignsInterface != null) {
            mysticalSignsInterface.setPlayer(player);
            mysticalSignsInterface.setVisible(true);
            mysticalSignsInterfaceVisible = true;
        }
    }
    
    /**
     * Masque l'interface des signes mystiques
     */
    public void hideMysticalSignsInterface() {
        if (mysticalSignsInterface != null) {
            mysticalSignsInterface.setVisible(false);
            mysticalSignsInterfaceVisible = false;
        }
    }
    
    /**
     * Vérifie si l'interface des signes mystiques est visible
     * 
     * @return true si l'interface des signes mystiques est visible, false sinon
     */
    public boolean isMysticalSignsInterfaceVisible() {
        return mysticalSignsInterfaceVisible;
    }
    
    /**
     * Affiche un dialogue avec le texte spécifié
     * 
     * @param text Le texte du dialogue à afficher
     */
    public void showDialog(String text) {
        if (dialogueInterface != null) {
            dialogueInterface.setText(text);
            dialogueInterface.setVisible(true);
            dialogVisible = true;
        }
    }
    
    /**
     * Masque l'interface de dialogue
     */
    public void hideDialog() {
        if (dialogueInterface != null) {
            dialogueInterface.setVisible(false);
            dialogVisible = false;
        }
    }
    
    /**
     * Vérifie si l'interface de dialogue est visible
     * 
     * @return true si l'interface de dialogue est visible, false sinon
     */
    public boolean isDialogVisible() {
        return dialogVisible;
    }
    
    /**
     * Récupère le texte du dialogue actuel
     * 
     * @return Le texte du dialogue actuel
     */
    public String getDialogText() {
        if (dialogueInterface != null) {
            return dialogueInterface.getText();
        }
        return "";
    }
    
    /**
     * Affiche le menu principal
     */
    public void showMenu() {
        // Implémentation à compléter
        menuVisible = true;
    }
    
    /**
     * Masque le menu principal
     */
    public void hideMenu() {
        // Implémentation à compléter
        menuVisible = false;
    }
    
    /**
     * Vérifie si le menu principal est visible
     * 
     * @return true si le menu principal est visible, false sinon
     */
    public boolean isMenuVisible() {
        return menuVisible;
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
                if (inventoryInterface.isVisible()) {
                    inventoryInterface.update(deltaTime);
                }
                
                if (questJournalInterface.isVisible()) {
                    questJournalInterface.update(deltaTime);
                }
                
                if (mysticalSignsInterface.isVisible()) {
                    mysticalSignsInterface.update(deltaTime);
                }
                
                if (dialogueInterface.isVisible()) {
                    dialogueInterface.update(deltaTime);
                }
                
                if (merchantInterface.isVisible()) {
                    merchantInterface.update(deltaTime);
                }
                break;
        }
    }
    
    /**
     * Dessiner les interfaces
     */
    public void render() {
        // Dessiner les interfaces selon l'état du jeu
        GameState.State currentState = GameState.getInstance().getCurrentState();
        
        switch (currentState) {
            case COMBAT:
                if (combatInterface.isInitialized()) {
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
                if (inventoryInterface.isVisible()) {
                    inventoryInterface.render();
                }
                
                if (questJournalInterface.isVisible()) {
                    questJournalInterface.render();
                }
                
                if (mysticalSignsInterface.isVisible()) {
                    mysticalSignsInterface.render();
                }
                
                if (dialogueInterface.isVisible()) {
                    dialogueInterface.render();
                }
                
                if (merchantInterface.isVisible()) {
                    merchantInterface.render();
                }
                break;
        }
        
        // Dessiner les notifications
        renderNotifications();
        
        // Dessiner les effets visuels
        renderVisualEffects();
    }
    
    /**
     * Gérer les entrées de l'utilisateur
     * 
     * @param mouseX Position X de la souris
     * @param mouseY Position Y de la souris
     * @param mousePressed true si le bouton de la souris est appuyé, false sinon
     * @return true si l'entrée a été traitée, false sinon
     */
    public boolean handleInput(float mouseX, float mouseY, boolean mousePressed) {
        // Gérer les entrées selon l'état du jeu
        GameState.State currentState = GameState.getInstance().getCurrentState();
        
        switch (currentState) {
            case COMBAT:
                if (combatInterface.isInitialized()) {
                    return combatInterface.handleInput(mouseX, mouseY, mousePressed);
                }
                break;
            case SAVE_LOAD:
                return saveLoadInterface.handleInput(mouseX, mouseY, mousePressed);
            case PAUSED:
                // Gérer les entrées de l'interface de pause
                break;
            default:
                // Gérer les entrées des interfaces communes à tous les états
                if (inventoryInterface.isVisible()) {
                    inventoryInterface.handleInput(mouseX, mouseY, mousePressed);
                    return true;
                }
                
                if (questJournalInterface.isVisible()) {
                    questJournalInterface.handleInput(mouseX, mouseY, mousePressed);
                    return true;
                }
                
                if (mysticalSignsInterface.isVisible()) {
                    mysticalSignsInterface.handleInput(mouseX, mouseY, mousePressed);
                    return true;
                }
                
                if (dialogueInterface.isVisible()) {
                    return dialogueInterface.handleInput(mouseX, mouseY, mousePressed);
                }
                
                if (merchantInterface.isVisible()) {
                    return merchantInterface.handleInput(mouseX, mouseY, mousePressed);
                }
                break;
        }
        
        return false;
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
     * Dessiner les notifications actives
     */
    private void renderNotifications() {
        float y = 50;
        
        for (Notification notification : activeNotifications) {
            notification.render(y);
            y += 60; // Espacement entre les notifications
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
     * Dessiner les effets visuels actifs
     */
    private void renderVisualEffects() {
        for (VisualEffect effect : activeEffects) {
            effect.render();
        }
    }
    
    /**
     * Afficher un dialogue
     * 
     * @param text Texte du dialogue
     * @param options Options de dialogue
     */
    public void showDialogue(String text, List<DialogueOption> options) {
        if (dialogueInterface != null) {
            dialogueInterface.setText(text);
            dialogueInterface.setOptions(options);
            dialogueInterface.setVisible(true);
        }
    }
    
    /**
     * Masquer le dialogue
     */
    public void hideDialogue() {
        if (dialogueInterface != null) {
            dialogueInterface.setVisible(false);
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
     * Obtenir l'interface du journal de quêtes
     * 
     * @return Interface du journal de quêtes
     */
    public QuestJournalInterface getQuestJournalInterface() {
        return questJournalInterface;
    }
    
    /**
     * Afficher le journal de quêtes
     */
    public void showQuestJournal() {
        if (questJournalInterface != null) {
            questJournalInterface.setVisible(true);
        }
    }
    
    /**
     * Masquer le journal de quêtes
     */
    public void hideQuestJournal() {
        if (questJournalInterface != null) {
            questJournalInterface.setVisible(false);
        }
    }
    
    /**
     * Afficher une notification pour une nouvelle quête
     * 
     * @param quest Quête à notifier
     */
    public void showQuestNotification(Quest quest) {
        if (quest == null) return;
        
        String title = quest.isMainQuest() ? "Nouvelle Quête Principale" : "Nouvelle Quête";
        String message = quest.getTitle();
        
        showNotification(title, message, 5.0f);
    }
    
    /**
     * Afficher une notification pour une quête mise à jour
     * 
     * @param quest Quête mise à jour
     * @param objectiveDescription Description de l'objectif mis à jour
     */
    public void showQuestUpdateNotification(Quest quest, String objectiveDescription) {
        if (quest == null) return;
        
        String title = "Quête Mise à Jour";
        String message = quest.getTitle() + " : " + objectiveDescription;
        
        showNotification(title, message, 5.0f);
    }
    
    /**
     * Afficher une notification pour une quête complétée
     * 
     * @param quest Quête complétée
     */
    public void showQuestCompletedNotification(Quest quest) {
        if (quest == null) return;
        
        String title = quest.isMainQuest() ? "Quête Principale Terminée" : "Quête Terminée";
        String message = quest.getTitle();
        
        showNotification(title, message, 5.0f);
    }
    
    /**
     * Afficher une notification pour une quête échouée
     * 
     * @param quest Quête échouée
     */
    public void showQuestFailedNotification(Quest quest) {
        if (quest == null) return;
        
        String title = "Quête Échouée";
        String message = quest.getTitle();
        
        showNotification(title, message, 5.0f);
    }
    
    /**
     * Obtenir l'interface des signes mystiques
     * 
     * @return Interface des signes mystiques
     */
    public MysticalSignsInterface getMysticalSignsInterface() {
        return mysticalSignsInterface;
    }
    
    /**
     * Afficher l'interface des signes mystiques
     * 
     * @param player Joueur
     */
    public void showMysticalSigns(Player player) {
        if (mysticalSignsInterface.isInitialized()) {
            mysticalSignsInterface.show(player);
        }
    }
    
    /**
     * Masquer l'interface des signes mystiques
     */
    public void hideMysticalSigns() {
        if (mysticalSignsInterface.isInitialized()) {
            mysticalSignsInterface.hide();
        }
    }
    
    /**
     * Classe interne représentant une notification
     */
    private class Notification {
        private String title;
        private String message;
        // Cette valeur sera utilisée dans une future mise à jour pour personnaliser la durée d'affichage
        @SuppressWarnings("unused")
        private float duration;
        private float remainingTime;
        private float alpha;
        
        public Notification(String title, String message, float duration) {
            this.title = title;
            this.message = message;
            this.duration = duration;
            this.remainingTime = duration;
            this.alpha = 1.0f;
        }
        
        public void update(float deltaTime) {
            remainingTime -= deltaTime;
            
            // Fade out effect when notification is about to expire
            if (remainingTime < 1.0f) {
                alpha = remainingTime;
            }
            
            // Ensure alpha stays between 0 and 1
            alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        }
        
        public void render(float y) {
            // Utiliser la valeur alpha pour la transparence
            int bgColor = (int)(alpha * 255) << 24 | 0x323232;
            int textColor = (int)(alpha * 255) << 24 | 0xFFFFFF;
            
            // Dessiner le fond de la notification
            renderer.drawRect(10, y, 300, 60, bgColor);
            
            // Dessiner le titre et le message
            renderer.drawText(title, 20, y + 15, 16, textColor);
            renderer.drawText(message, 20, y + 40, 12, textColor);
        }
        
        public boolean isExpired() {
            return remainingTime <= 0;
        }
    }
    
    /**
     * Classe interne représentant un effet visuel
     */
    private class VisualEffect {
        private String effectId;
        private float duration;
        private float elapsedTime;
        private boolean finished;
        
        public VisualEffect(String effectId) {
            this.effectId = effectId;
            this.elapsedTime = 0;
            this.finished = false;
            
            // Définir la durée en fonction du type d'effet
            switch (effectId) {
                case "levelup":
                    duration = 2.0f;
                    break;
                case "evolution":
                    duration = 5.0f;
                    break;
                case "capture":
                    duration = 3.0f;
                    break;
                case "critical":
                    duration = 1.0f;
                    break;
                case "victory":
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
            // Implémenter le rendu de l'effet visuel
            float progress = elapsedTime / duration; // Progression de 0 à 1
            
            switch (effectId) {
                case "levelup":
                    renderLevelUpEffect(progress);
                    break;
                case "evolution":
                    renderEvolutionEffect(progress);
                    break;
                case "capture":
                    renderCaptureEffect(progress);
                    break;
                case "critical":
                    renderCriticalEffect(progress);
                    break;
                case "victory":
                    renderVictoryEffect(progress);
                    break;
                case "earthquake":
                    renderEarthquakeEffect(progress);
                    break;
                default:
                    // Effet générique
                    renderGenericEffect(progress);
                    break;
            }
        }
        
        private void renderLevelUpEffect(float progress) {
            // Exemple d'effet de montée de niveau
            int alpha = (int)(255 * (1 - progress));
            int color = 0xFFFF00 | (alpha << 24);
            renderer.drawText("NIVEAU SUPÉRIEUR !", 400, 300, 24, color);
        }
        
        private void renderEvolutionEffect(float progress) {
            // Exemple d'effet d'évolution
            int alpha = (int)(255 * (progress < 0.5f ? progress * 2 : (1 - progress) * 2));
            int color = 0x00FFFF | (alpha << 24);
            renderer.drawText("ÉVOLUTION !", 400, 300, 28, color);
        }
        
        private void renderCaptureEffect(float progress) {
            // Effet de capture
            int size = (int)(100 * (1 - progress));
            int alpha = (int)(255 * (1 - progress));
            int color = 0xFF0000 | (alpha << 24);
            // Dessiner un cercle (à implémenter dans Renderer si nécessaire)
            // Pour l'instant, on dessine un rectangle comme approximation
            renderer.drawRect(400 - size/2, 300 - size/2, size, size, color);
        }
        
        private void renderCriticalEffect(float progress) {
            // Effet de coup critique
            int alpha = (int)(255 * (1 - progress));
            int color = 0xFF0000 | (alpha << 24);
            renderer.drawText("CRITIQUE !", 400, 300, 20, color);
        }
        
        private void renderVictoryEffect(float progress) {
            // Effet de victoire
            if (progress < 0.1f || (progress > 0.2f && progress < 0.3f) || (progress > 0.4f && progress < 0.5f)) {
                int color = 0xFFD700; // Or
                renderer.drawText("VICTOIRE !", 400, 300, 32, color);
            }
        }
        
        private void renderEarthquakeEffect(float progress) {
            // Effet de tremblement de terre
            // Pour l'instant, on ne fait rien car setOffset n'est pas disponible
            // On pourrait implémenter cet effet différemment plus tard
        }
        
        private void renderGenericEffect(float progress) {
            // Effet générique
            int alpha = (int)(255 * (1 - progress));
            int color = 0xFFFFFF | (alpha << 24);
            renderer.drawText("EFFET SPÉCIAL", 400, 300, 20, color);
        }
        
        public boolean isFinished() {
            return finished;
        }
    }
    
    /**
     * Classe interne pour représenter l'interface du marchand
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
            if (!visible || items.isEmpty()) return;
            
            // Dessiner le fond de l'interface
            renderer.drawRect(100, 100, 600, 400, 0x80000000);
            
            // Dessiner le titre
            renderer.drawText("MARCHAND", 400, 120, 24, 0xFFFFD700);
            
            // Dessiner les objets disponibles
            int y = 160;
            for (Item item : items) {
                renderer.drawText(item.getName(), 150, y, 16, 0xFFFFFFFF);
                renderer.drawText(item.getValue() + " pièces", 500, y, 16, 0xFFFFFF00);
                y += 30;
                
                if (y > 450) break; // Limiter le nombre d'objets affichés
            }
            
            // Dessiner les boutons d'action
            renderer.drawRect(150, 470, 200, 40, 0xFF323232);
            renderer.drawText("ACHETER", 250, 490, 16, 0xFFFFFFFF);
            
            renderer.drawRect(450, 470, 200, 40, 0xFF323232);
            renderer.drawText("FERMER", 550, 490, 16, 0xFFFFFFFF);
        }
        
        public boolean handleInput(float mouseX, float mouseY, boolean mousePressed) {
            if (!visible) return false;
            
            if (mousePressed) {
                // Vérifier si le bouton "FERMER" a été cliqué
                if (mouseX >= 450 && mouseX <= 650 && mouseY >= 470 && mouseY <= 510) {
                    setVisible(false);
                    return true;
                }
                
                // Vérifier si le bouton "ACHETER" a été cliqué
                if (mouseX >= 150 && mouseX <= 350 && mouseY >= 470 && mouseY <= 510) {
                    // Logique d'achat à implémenter
                    return true;
                }
                
                // Vérifier si un objet a été sélectionné
                int y = 160;
                for (int i = 0; i < items.size() && y <= 450; i++) {
                    if (mouseX >= 150 && mouseX <= 600 && mouseY >= y && mouseY < y + 30) {
                        // Sélectionner l'objet
                        return true;
                    }
                    y += 30;
                }
            }
            
            return false;
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
    
    /**
     * Classe interne pour représenter l'interface de dialogue
     */
    private class DialogueInterface {
        private String text;
        private List<DialogueOption> options;
        private boolean visible;
        
        public DialogueInterface() {
            this.text = "";
            this.options = new ArrayList<>();
            this.visible = false;
        }
        
        public void update(float deltaTime) {
            // Mettre à jour l'interface de dialogue
        }
        
        public void render() {
            if (!visible) return;
            
            // Dessiner un fond semi-transparent
            renderer.drawRect(100, 100, 600, 400, 0);
            
            // Dessiner le texte du dialogue
            renderer.drawText(text, 120, 120, 16, 0);
            
            // Dessiner les options de dialogue
            int y = 250;
            for (int i = 0; i < options.size(); i++) {
                DialogueOption option = options.get(i);
                renderer.drawText((i + 1) + ". " + option.getText(), 120, y, 14, 0);
                y += 30;
            }
        }
        
        public boolean handleInput(float mouseX, float mouseY, boolean mousePressed) {
            // Gérer les entrées de l'interface de dialogue
            if (!visible) return false;
            
            // Vérifier si une option a été sélectionnée
            if (mousePressed) {
                int y = 250;
                for (int i = 0; i < options.size(); i++) {
                    if (mouseY >= y && mouseY < y + 30 && mouseX >= 120 && mouseX < 700) {
                        // Option sélectionnée
                        DialogueOption option = options.get(i);
                        option.executeActions();
                        return true;
                    }
                    y += 30;
                }
            }
            
            return false;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public String getText() {
            return this.text;
        }
        
        public void setOptions(List<DialogueOption> options) {
            this.options = new ArrayList<>(options);
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }
}

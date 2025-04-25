package com.ryuukonpalace.game.save;

import com.ryuukonpalace.game.core.GameState;

/**
 * Système d'auto-sauvegarde pour Ryuukon Palace.
 * Gère les sauvegardes automatiques à intervalles réguliers.
 */
public class AutoSaveSystem {
    
    // Singleton instance
    private static AutoSaveSystem instance;
    
    // Gestionnaire de sauvegarde
    private SaveManager saveManager;
    
    // Intervalle d'auto-sauvegarde en millisecondes (5 minutes par défaut)
    private long autoSaveInterval;
    
    // Dernier temps d'auto-sauvegarde
    private long lastAutoSaveTime;
    
    // État d'auto-sauvegarde
    private boolean autoSaveEnabled;
    
    // Slot d'auto-sauvegarde
    private int autoSaveSlot;
    
    /**
     * Constructeur privé (singleton)
     */
    private AutoSaveSystem() {
        this.saveManager = SaveManager.getInstance();
        this.autoSaveInterval = 5 * 60 * 1000; // 5 minutes
        this.lastAutoSaveTime = System.currentTimeMillis();
        this.autoSaveEnabled = true;
        this.autoSaveSlot = 0; // Slot réservé pour l'auto-sauvegarde
    }
    
    /**
     * Obtenir l'instance unique du système d'auto-sauvegarde
     * @return L'instance du AutoSaveSystem
     */
    public static AutoSaveSystem getInstance() {
        if (instance == null) {
            instance = new AutoSaveSystem();
        }
        return instance;
    }
    
    /**
     * Initialiser le système d'auto-sauvegarde
     */
    public void init() {
        this.lastAutoSaveTime = System.currentTimeMillis();
    }
    
    /**
     * Mettre à jour le système d'auto-sauvegarde
     * Appelé à chaque frame pour vérifier si une auto-sauvegarde est nécessaire
     */
    public void update() {
        // Ne pas auto-sauvegarder si désactivé ou si le jeu n'est pas en cours
        if (!autoSaveEnabled || GameState.getInstance().getCurrentState() != GameState.State.PLAYING) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAutoSaveTime >= autoSaveInterval) {
            // Effectuer une auto-sauvegarde
            performAutoSave();
            lastAutoSaveTime = currentTime;
        }
    }
    
    /**
     * Effectuer une auto-sauvegarde
     * @return true si l'auto-sauvegarde a réussi, false sinon
     */
    public boolean performAutoSave() {
        System.out.println("Auto-sauvegarde en cours...");
        return saveManager.saveGame(autoSaveSlot);
    }
    
    /**
     * Activer ou désactiver l'auto-sauvegarde
     * @param enabled true pour activer, false pour désactiver
     */
    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
    }
    
    /**
     * Vérifier si l'auto-sauvegarde est activée
     * @return true si l'auto-sauvegarde est activée, false sinon
     */
    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }
    
    /**
     * Définir l'intervalle d'auto-sauvegarde
     * @param intervalMinutes Intervalle en minutes
     */
    public void setAutoSaveInterval(int intervalMinutes) {
        this.autoSaveInterval = intervalMinutes * 60 * 1000;
    }
    
    /**
     * Obtenir l'intervalle d'auto-sauvegarde en minutes
     * @return Intervalle d'auto-sauvegarde en minutes
     */
    public int getAutoSaveIntervalMinutes() {
        return (int) (autoSaveInterval / (60 * 1000));
    }
    
    /**
     * Définir le slot d'auto-sauvegarde
     * @param slot Numéro du slot d'auto-sauvegarde
     */
    public void setAutoSaveSlot(int slot) {
        if (slot >= 0 && slot < SaveManager.MAX_SAVE_SLOTS) {
            this.autoSaveSlot = slot;
        }
    }
    
    /**
     * Obtenir le slot d'auto-sauvegarde
     * @return Numéro du slot d'auto-sauvegarde
     */
    public int getAutoSaveSlot() {
        return autoSaveSlot;
    }
    
    /**
     * Forcer une auto-sauvegarde immédiate
     * @return true si l'auto-sauvegarde a réussi, false sinon
     */
    public boolean forceAutoSave() {
        boolean result = performAutoSave();
        lastAutoSaveTime = System.currentTimeMillis();
        return result;
    }
}

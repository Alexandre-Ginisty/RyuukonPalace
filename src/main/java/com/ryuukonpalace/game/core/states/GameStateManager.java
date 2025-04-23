package com.ryuukonpalace.game.core.states;

import java.util.Stack;

/**
 * Gestionnaire des états du jeu.
 * Utilise une pile pour gérer les états (menus, jeu, pause, etc.)
 */
public class GameStateManager {
    
    // Singleton instance
    private static GameStateManager instance;
    
    // Pile des états
    private Stack<GameState> states;
    
    /**
     * Constructeur privé (singleton)
     */
    private GameStateManager() {
        states = new Stack<>();
    }
    
    /**
     * Obtenir l'instance unique du GameStateManager
     * @return L'instance du GameStateManager
     */
    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }
    
    /**
     * Ajouter un état au sommet de la pile
     * @param state État à ajouter
     */
    public void pushState(GameState state) {
        states.push(state);
    }
    
    /**
     * Retirer l'état au sommet de la pile
     */
    public void popState() {
        if (!states.empty()) {
            GameState state = states.pop();
            state.dispose();
        }
    }
    
    /**
     * Remplacer l'état au sommet de la pile
     * @param state Nouvel état
     */
    public void setState(GameState state) {
        if (!states.empty()) {
            GameState oldState = states.pop();
            oldState.dispose();
        }
        states.push(state);
    }
    
    /**
     * Ajouter un état au gestionnaire
     * @param state État à ajouter
     */
    public void addState(GameState state) {
        setState(state);
    }
    
    /**
     * Mettre à jour l'état actuel
     * @param dt Delta time
     */
    public void update(float dt) {
        if (!states.empty()) {
            states.peek().update(dt);
        }
    }
    
    /**
     * Rendre l'état actuel
     */
    public void render() {
        if (!states.empty()) {
            states.peek().render();
        }
    }
    
    /**
     * Obtenir l'état actuel
     * @return État actuel
     */
    public GameState getCurrentState() {
        if (!states.empty()) {
            return states.peek();
        }
        return null;
    }
}

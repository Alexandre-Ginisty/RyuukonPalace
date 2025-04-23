package com.ryuukonpalace.game.core;

import com.ryuukonpalace.game.core.states.GameState;

import java.util.Stack;

/**
 * Manages the different states of the game (menu, battle, exploration, etc.)
 * Uses a stack to handle state transitions.
 * 
 * @deprecated Use com.ryuukonpalace.game.core.states.GameStateManager instead
 */
@Deprecated
public class GameStateManager {
    
    private Stack<GameState> states;
    
    public GameStateManager() {
        states = new Stack<>();
        // No longer auto-initialize with MainMenuState
    }
    
    /**
     * Pushes a new state onto the stack
     * @param state The new state to push
     */
    public void pushState(GameState state) {
        states.push(state);
        state.init();
    }
    
    /**
     * Pops the current state off the stack
     */
    public void popState() {
        if (!states.isEmpty()) {
            GameState state = states.pop();
            state.dispose();
        }
    }
    
    /**
     * Changes the current state by popping the current one and pushing a new one
     * @param state The new state to change to
     */
    public void setState(GameState state) {
        popState();
        pushState(state);
    }
    
    /**
     * Updates the current state
     * @param dt Delta time in seconds
     */
    public void update(float dt) {
        if (!states.isEmpty()) {
            states.peek().update(dt);
        }
    }
    
    /**
     * Renders the current state
     */
    public void render() {
        if (!states.isEmpty()) {
            states.peek().render();
        }
    }
    
    /**
     * Gets the current state
     * @return The current state
     */
    public GameState getCurrentState() {
        return states.isEmpty() ? null : states.peek();
    }
}

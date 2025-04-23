package com.ryuukonpalace.game.core.states;

/**
 * Interface for all game states (menu, battle, exploration, etc.)
 */
public interface GameState {
    
    /**
     * Initialize the state
     */
    void init();
    
    /**
     * Update the state logic
     * @param dt Delta time in seconds
     */
    void update(float dt);
    
    /**
     * Render the state
     */
    void render();
    
    /**
     * Clean up resources when the state is removed
     */
    void dispose();
}

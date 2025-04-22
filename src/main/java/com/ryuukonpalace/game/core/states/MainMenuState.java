package com.ryuukonpalace.game.core.states;

import com.ryuukonpalace.game.core.GameStateManager;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

/**
 * Main menu state for the game
 * Shows title, options to start new game, load game, settings, etc.
 */
public class MainMenuState implements GameState {
    
    private GameStateManager gsm;
    private String[] menuItems;
    private int selectedItem;
    
    public MainMenuState(GameStateManager gsm) {
        this.gsm = gsm;
        this.menuItems = new String[] {
            "New Game",
            "Load Game",
            "Settings",
            "Exit"
        };
        this.selectedItem = 0;
    }
    
    @Override
    public void init() {
        System.out.println("Initializing Main Menu State");
    }
    
    @Override
    public void update() {
        // Menu navigation logic would go here
        // For now, just a placeholder
    }
    
    @Override
    public void render() {
        // Clear the screen
        glClearColor(0.2f, 0.3f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        
        // In a real implementation, we would render the menu items
        // For now, just a placeholder
        System.out.println("Rendering Main Menu");
    }
    
    @Override
    public void dispose() {
        System.out.println("Disposing Main Menu State");
    }
    
    /**
     * Handles key input for menu navigation
     * @param key The key that was pressed
     */
    public void handleInput(int key) {
        if (key == GLFW.GLFW_KEY_UP) {
            selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
        } else if (key == GLFW.GLFW_KEY_DOWN) {
            selectedItem = (selectedItem + 1) % menuItems.length;
        } else if (key == GLFW.GLFW_KEY_ENTER) {
            selectMenuItem();
        }
    }
    
    /**
     * Processes the selected menu item
     */
    private void selectMenuItem() {
        switch (selectedItem) {
            case 0: // New Game
                // Start a new game
                System.out.println("Starting new game...");
                // gsm.setState(new GamePlayState(gsm));
                break;
            case 1: // Load Game
                System.out.println("Loading game...");
                break;
            case 2: // Settings
                System.out.println("Opening settings...");
                break;
            case 3: // Exit
                System.out.println("Exiting game...");
                GLFW.glfwSetWindowShouldClose(GLFW.glfwGetCurrentContext(), true);
                break;
        }
    }
}

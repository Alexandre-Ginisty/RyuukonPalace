package com.ryuukonpalace.game.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire des entrées utilisateur (clavier, souris).
 * Permet de détecter les touches pressées, relâchées et maintenues.
 * Gère également les entrées de la souris.
 */
public class InputManager {
    
    // Singleton instance
    private static InputManager instance;
    
    // Window handle
    private long window;
    
    // Key states
    private final Map<Integer, Boolean> keyPressed = new HashMap<>();
    private final Map<Integer, Boolean> keyJustPressed = new HashMap<>();
    private final Map<Integer, Boolean> keyJustReleased = new HashMap<>();
    
    // Mouse states
    private final Map<Integer, Boolean> mouseButtonPressed = new HashMap<>();
    private final Map<Integer, Boolean> mouseButtonJustPressed = new HashMap<>();
    private final Map<Integer, Boolean> mouseButtonJustReleased = new HashMap<>();
    
    // Mouse position
    private double mouseX;
    private double mouseY;
    private double lastMouseX;
    private double lastMouseY;
    
    // Callbacks
    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    
    /**
     * Constructeur privé pour le singleton
     */
    private InputManager() {
        // Sera initialisé plus tard avec setWindow()
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire d'entrées
     * @return L'instance de l'InputManager
     */
    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }
    
    /**
     * Définir la fenêtre à surveiller pour les entrées
     * @param window Handle de la fenêtre GLFW
     */
    public void setWindow(long window) {
        this.window = window;
        setupCallbacks();
    }
    
    /**
     * Configurer les callbacks GLFW pour les entrées
     */
    private void setupCallbacks() {
        // Key callback
        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action == GLFW.GLFW_PRESS) {
                    keyPressed.put(key, true);
                    keyJustPressed.put(key, true);
                } else if (action == GLFW.GLFW_RELEASE) {
                    keyPressed.put(key, false);
                    keyJustReleased.put(key, true);
                }
            }
        };
        
        // Mouse button callback
        mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (action == GLFW.GLFW_PRESS) {
                    mouseButtonPressed.put(button, true);
                    mouseButtonJustPressed.put(button, true);
                } else if (action == GLFW.GLFW_RELEASE) {
                    mouseButtonPressed.put(button, false);
                    mouseButtonJustReleased.put(button, true);
                }
            }
        };
        
        // Cursor position callback
        cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                mouseX = xpos;
                mouseY = ypos;
            }
        };
        
        // Set the callbacks
        GLFW.glfwSetKeyCallback(window, keyCallback);
        GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);
        GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
    }
    
    /**
     * Mettre à jour l'état des entrées
     * Doit être appelé à chaque frame
     */
    public void update() {
        // Reset the "just" states
        keyJustPressed.clear();
        keyJustReleased.clear();
        mouseButtonJustPressed.clear();
        mouseButtonJustReleased.clear();
    }
    
    /**
     * Vérifier si une touche est actuellement pressée
     * @param key Code de la touche GLFW
     * @return true si la touche est pressée, false sinon
     */
    public boolean isKeyPressed(int key) {
        return keyPressed.getOrDefault(key, false);
    }
    
    /**
     * Vérifier si une touche vient d'être pressée
     * @param key Code de la touche GLFW
     * @return true si la touche vient d'être pressée, false sinon
     */
    public boolean isKeyJustPressed(int key) {
        return keyJustPressed.getOrDefault(key, false);
    }
    
    /**
     * Vérifier si une touche vient d'être relâchée
     * @param key Code de la touche GLFW
     * @return true si la touche vient d'être relâchée, false sinon
     */
    public boolean isKeyJustReleased(int key) {
        return keyJustReleased.getOrDefault(key, false);
    }
    
    /**
     * Vérifier si un bouton de souris est actuellement pressé
     * @param button Code du bouton de souris GLFW
     * @return true si le bouton est pressé, false sinon
     */
    public boolean isMouseButtonPressed(int button) {
        return mouseButtonPressed.getOrDefault(button, false);
    }
    
    /**
     * Vérifier si un bouton de souris vient d'être pressé
     * @param button Code du bouton de souris GLFW
     * @return true si le bouton vient d'être pressé, false sinon
     */
    public boolean isMouseButtonJustPressed(int button) {
        return mouseButtonJustPressed.getOrDefault(button, false);
    }
    
    /**
     * Vérifier si un bouton de souris vient d'être relâché
     * @param button Code du bouton de souris GLFW
     * @return true si le bouton vient d'être relâché, false sinon
     */
    public boolean isMouseButtonJustReleased(int button) {
        return mouseButtonJustReleased.getOrDefault(button, false);
    }
    
    /**
     * Obtenir la position X actuelle de la souris
     * @return Position X de la souris
     */
    public double getMouseX() {
        return mouseX;
    }
    
    /**
     * Obtenir la position Y actuelle de la souris
     * @return Position Y de la souris
     */
    public double getMouseY() {
        return mouseY;
    }
    
    /**
     * Obtenir le déplacement X de la souris depuis la dernière frame
     * @return Déplacement X de la souris
     */
    public double getMouseDeltaX() {
        return mouseX - lastMouseX;
    }
    
    /**
     * Obtenir le déplacement Y de la souris depuis la dernière frame
     * @return Déplacement Y de la souris
     */
    public double getMouseDeltaY() {
        return mouseY - lastMouseY;
    }
    
    /**
     * Libérer les ressources
     */
    public void dispose() {
        if (keyCallback != null) {
            keyCallback.free();
        }
        if (mouseButtonCallback != null) {
            mouseButtonCallback.free();
        }
        if (cursorPosCallback != null) {
            cursorPosCallback.free();
        }
    }
}

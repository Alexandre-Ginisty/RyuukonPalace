package com.ryuukonpalace.game.core;

import com.ryuukonpalace.game.core.physics.CollisionManager;
import com.ryuukonpalace.game.utils.ResourceManager;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Main class for Ryuukon Palace game.
 * This game is a Pokémon-like RPG where players can capture creatures using QTE (Quick Time Events).
 * The game features creature evolution, battles, and exploration.
 */
public class RyuukonPalace {

    // Window handle
    private long window;
    
    // Window dimensions
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    
    // Game title
    private final String TITLE = "Ryuukon Palace";
    
    // Game state manager
    private GameStateManager gameStateManager;
    
    // Resource manager
    private ResourceManager resourceManager;
    
    // Renderer
    private Renderer renderer;
    
    // Input manager
    private InputManager inputManager;
    
    // Collision manager
    private CollisionManager collisionManager;
    
    // Caméra
    private Camera camera;
    
    // Liste des objets du jeu
    private List<GameObject> gameObjects;
    
    // Temps de la dernière frame
    private double lastFrameTime;

    public void run() {
        System.out.println("Starting " + TITLE + " using LWJGL " + Version.getVersion());

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
        
        // Initialize OpenGL
        GL.createCapabilities();
        
        // Initialize managers
        resourceManager = ResourceManager.getInstance();
        renderer = Renderer.getInstance();
        inputManager = InputManager.getInstance();
        inputManager.setWindow(window);
        collisionManager = CollisionManager.getInstance();
        camera = Camera.getInstance();
        
        // Initialiser la caméra
        camera.init(WIDTH, HEIGHT, 2000, 2000); // Dimensions du monde: 2000x2000
        
        // Initialiser la liste des objets du jeu
        gameObjects = new ArrayList<>();
        
        // Configurer les groupes de collision
        setupCollisionGroups();
        
        // Initialize game state manager
        gameStateManager = new GameStateManager();
        
        // Initialiser le temps de la dernière frame
        lastFrameTime = glfwGetTime();
    }
    
    /**
     * Configurer les groupes de collision
     */
    private void setupCollisionGroups() {
        // Définir les relations de collision entre les différents groupes
        collisionManager.setCollision("player", "obstacles");
        collisionManager.setCollision("player", "npcs");
        collisionManager.setCollision("player", "creatures");
        collisionManager.setCollision("player", "items");
        collisionManager.setCollision("creatures", "obstacles");
    }

    private void loop() {
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            // Calculer le deltaTime
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastFrameTime);
            lastFrameTime = currentTime;
            
            // Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Update input
            inputManager.update();
            
            // Update game objects
            updateGameObjects(deltaTime);
            
            // Update camera
            camera.update();
            
            // Update collisions
            updateCollisions();
            
            // Update game state
            gameStateManager.update();
            
            // Render game objects
            renderGameObjects();
            
            // Render game state
            gameStateManager.render();

            // Swap the color buffers
            glfwSwapBuffers(window);

            // Poll for window events
            glfwPollEvents();
        }
        
        // Dispose resources
        disposeGameObjects();
        resourceManager.dispose();
        renderer.dispose();
        inputManager.dispose();
    }
    
    /**
     * Mettre à jour tous les objets du jeu
     * @param deltaTime Temps écoulé depuis la dernière frame en secondes
     */
    private void updateGameObjects(float deltaTime) {
        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            GameObject obj = gameObjects.get(i);
            if (obj.isActive()) {
                obj.update(deltaTime);
            }
        }
    }
    
    /**
     * Mettre à jour les collisions entre objets
     */
    private void updateCollisions() {
        // Mettre à jour toutes les collisions
        var collisions = collisionManager.updateCollisions();
        
        // Traiter les collisions
        for (var entry : collisions.entrySet()) {
            for (var pair : entry.getValue()) {
                // Trouver les GameObjects associés aux colliders
                GameObject obj1 = findGameObjectByCollider(pair.getCollider1());
                GameObject obj2 = findGameObjectByCollider(pair.getCollider2());
                
                if (obj1 != null && obj2 != null) {
                    // Notifier les objets de la collision
                    obj1.onCollision(obj2);
                    obj2.onCollision(obj1);
                }
            }
        }
    }
    
    /**
     * Trouver un GameObject par son collider
     * @param collider Le collider à rechercher
     * @return Le GameObject associé, ou null si non trouvé
     */
    private GameObject findGameObjectByCollider(com.ryuukonpalace.game.core.physics.Collider collider) {
        for (GameObject obj : gameObjects) {
            if (obj.getCollider() == collider) {
                return obj;
            }
        }
        return null;
    }
    
    /**
     * Dessiner tous les objets du jeu
     */
    private void renderGameObjects() {
        for (GameObject obj : gameObjects) {
            if (obj.isActive()) {
                obj.render();
            }
        }
    }
    
    /**
     * Libérer les ressources des objets du jeu
     */
    private void disposeGameObjects() {
        for (GameObject obj : gameObjects) {
            obj.dispose();
        }
        gameObjects.clear();
    }
    
    /**
     * Ajouter un objet au jeu
     * @param obj L'objet à ajouter
     */
    public void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }
    
    /**
     * Supprimer un objet du jeu
     * @param obj L'objet à supprimer
     * @return true si l'objet a été supprimé, false sinon
     */
    public boolean removeGameObject(GameObject obj) {
        obj.dispose();
        return gameObjects.remove(obj);
    }
    
    /**
     * Obtenir la caméra du jeu
     * @return La caméra
     */
    public Camera getCamera() {
        return camera;
    }

    public static void main(String[] args) {
        new RyuukonPalace().run();
    }
}

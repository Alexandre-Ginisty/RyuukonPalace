package com.ryuukonpalace.game.core;

import com.ryuukonpalace.game.combat.CombatSystem;
import com.ryuukonpalace.game.core.physics.CollisionManager;
import com.ryuukonpalace.game.core.states.GameState;
import com.ryuukonpalace.game.core.states.GameStateManager;
import com.ryuukonpalace.game.core.states.MainMenuState;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.qte.QTESystem;
import com.ryuukonpalace.game.utils.ResourceManager;
import com.ryuukonpalace.game.world.GameObject;
import com.ryuukonpalace.game.world.SpawnZone;
import com.ryuukonpalace.game.world.WorldManager;
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
 * Main game class for Ryuukon Palace.
 * Handles window creation, game loop, and state management.
 */
public class RyuukonPalace {

    // Window handle
    private long window;
    
    // Window dimensions
    private int width = 1280;
    private int height = 720;
    
    // Game title
    private String title = "Ryuukon Palace";
    
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
    
    // Camera
    private Camera camera;
    
    // World manager
    private WorldManager worldManager;
    
    // Player
    private Player player;
    
    // QTE system
    private QTESystem qteSystem;
    
    // Combat system
    private CombatSystem combatSystem;
    
    // Game objects
    private List<GameObject> gameObjects;
    
    // World dimensions
    private float worldWidth = 2000.0f;
    private float worldHeight = 2000.0f;
    
    // Delta time
    private float deltaTime = 0.0f;
    private long lastFrameTime = 0;

    /**
     * Run the game
     */
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * Initialize the game
     */
    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
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
        
        // Initialize game systems
        initGameSystems();
        
        // Initialize game states
        initGameStates();
        
        // Initialize game objects
        initGameObjects();
    }

    /**
     * Initialize game systems
     */
    private void initGameSystems() {
        // Initialize resource manager
        resourceManager = ResourceManager.getInstance();
        resourceManager.init();
        
        // Initialize input manager
        inputManager = InputManager.getInstance();
        inputManager.setWindow(window);
        
        // Initialize game state manager
        gameStateManager = GameStateManager.getInstance();
        
        // Initialize renderer
        renderer = Renderer.getInstance();
        renderer.init(width, height);
        
        // Initialize QTE system
        qteSystem = QTESystem.getInstance();
        qteSystem.init();
        
        // Initialize combat system
        combatSystem = CombatSystem.getInstance();
        combatSystem.init();
        
        // Initialize collision manager
        collisionManager = CollisionManager.getInstance();
        
        // Configurer les groupes de collision
        collisionManager.setCollision("player", "spawnZone");
        collisionManager.setCollision("player", "obstacle");
        
        // Initialize camera
        camera = Camera.getInstance();
        camera.init(width, height, worldWidth, worldHeight);
        
        // Initialize world manager
        worldManager = WorldManager.getInstance();
        
        // Initialize game objects list
        gameObjects = new ArrayList<>();
    }

    /**
     * Initialize game states
     */
    private void initGameStates() {
        // Create main menu state
        GameState mainMenuState = new MainMenuState(gameStateManager);
        mainMenuState.init();
        
        // Add states to manager
        gameStateManager.addState(mainMenuState);
        
        // Set initial state
        gameStateManager.setState(mainMenuState);
    }
    
    /**
     * Initialize game objects
     */
    private void initGameObjects() {
        // Create player at the center of the world
        player = new Player(worldWidth / 2, worldHeight / 2);
        gameObjects.add(player);
        
        // Set camera to follow player
        camera.follow(player);
    }

    /**
     * Main game loop
     */
    private void loop() {
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        // Set initial time
        lastFrameTime = System.nanoTime();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            // Calculate delta time
            long currentTime = System.nanoTime();
            deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
            lastFrameTime = currentTime;
            
            // Cap delta time to prevent physics issues after pausing
            if (deltaTime > 0.1f) {
                deltaTime = 0.1f;
            }
            
            // Update input
            inputManager.update();
            
            // Check if in combat
            if (combatSystem.isActive()) {
                // Update combat
                updateCombat();
            } else {
                // Update game
                updateGame();
            }
            
            // Render
            render();
            
            // Swap the color buffers
            glfwSwapBuffers(window);

            // Poll for window events
            glfwPollEvents();
        }
    }
    
    /**
     * Update the game
     */
    private void updateGame() {
        // Update game state
        gameStateManager.update(deltaTime);
        
        // Update world
        worldManager.update(deltaTime);
        
        // Update game objects
        for (GameObject obj : gameObjects) {
            obj.update(deltaTime);
        }
        
        // Check collisions
        checkCollisions();
        
        // Update camera
        camera.update();
    }
    
    /**
     * Update the combat
     */
    private void updateCombat() {
        // Update QTE system
        qteSystem.update(deltaTime);
        
        // Update combat system
        combatSystem.update(deltaTime);
        
        // Check if combat is completed
        if (combatSystem.isCompleted()) {
            // End combat
            CombatSystem.CombatState state = combatSystem.getState();
            
            if (state == CombatSystem.CombatState.CAPTURE) {
                // Creature captured
                player.endBattle(true);
            } else {
                // Creature not captured
                player.endBattle(false);
            }
            
            // End combat
            combatSystem.endCombat();
        }
    }
    
    /**
     * Check collisions between game objects
     */
    private void checkCollisions() {
        // Ajouter le joueur au groupe "player" pour la détection de collision
        if (player.getCollider() != null) {
            collisionManager.addCollider(player.getCollider(), "player");
        }
        
        // Check collisions between player and spawn zones
        for (GameObject obj : gameObjects) {
            if (obj != player && obj.getCollider() != null) {
                // Ajouter l'objet au groupe approprié pour la détection de collision
                if (obj instanceof SpawnZone) {
                    collisionManager.addCollider(obj.getCollider(), "spawnZone");
                } else {
                    collisionManager.addCollider(obj.getCollider(), "obstacle");
                }
                
                // Vérifier la collision manuellement (pour compatibilité avec le code existant)
                if (player.getCollider() != null && player.getCollider().collidesWith(obj.getCollider())) {
                    player.onCollision(obj);
                    obj.onCollision(player);
                }
            }
        }
        
        // Mettre à jour toutes les collisions via le gestionnaire de collisions
        collisionManager.updateCollisions();
        
        // Add world spawn zones to game objects
        List<SpawnZone> spawnZones = worldManager.getSpawnZones();
        for (SpawnZone spawnZone : spawnZones) {
            if (!gameObjects.contains(spawnZone)) {
                gameObjects.add((GameObject) spawnZone);
            }
        }
    }
    
    /**
     * Render the game
     */
    private void render() {
        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Check if in combat
        if (combatSystem.isActive()) {
            // Render combat
            renderCombat();
        } else {
            // Render game
            renderGame();
        }
    }
    
    /**
     * Render the game
     */
    private void renderGame() {
        // Render world
        worldManager.render();
        
        // Render game objects
        for (GameObject obj : gameObjects) {
            // Only render objects that are visible on screen
            if (camera.isVisible(obj)) {
                obj.render();
            }
        }
        
        // Render game state
        gameStateManager.render();
    }
    
    /**
     * Render the combat
     */
    private void renderCombat() {
        // Render combat system
        combatSystem.render();
        
        // Render QTE system if active
        if (qteSystem.isActive()) {
            qteSystem.render();
        }
    }

    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        new RyuukonPalace().run();
    }
}

package com.ryuukonpalace.game.core.states;

import com.ryuukonpalace.game.core.Camera;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.player.Player.CombatStartCallback;
import com.ryuukonpalace.game.world.WorldManager;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * État principal du jeu où le joueur explore le monde
 */
public class GamePlayState implements GameState, CombatStartCallback {
    
    private GameStateManager gsm;
    private Player player;
    private Camera camera;
    private WorldManager worldManager;
    
    /**
     * Constructeur
     * @param gsm Gestionnaire d'états du jeu
     */
    public GamePlayState(GameStateManager gsm) {
        this.gsm = gsm;
    }
    
    @Override
    public void init() {
        System.out.println("Initializing GamePlay State");
        
        // Initialiser le monde
        worldManager = WorldManager.getInstance();
        
        // Initialiser le joueur (position x, position y, collision group)
        player = new Player(100, 100);
        
        // Définir le callback pour le démarrage des combats
        player.setCombatStartCallback(this);
        
        // Initialiser la caméra
        camera = Camera.getInstance();
        camera.follow(player);
    }
    
    @Override
    public void update(float dt) {
        // Mettre à jour le joueur
        player.update(dt);
        
        // Mettre à jour la caméra
        camera.update();
        
        // Mettre à jour le monde
        worldManager.update(dt);
        
        // Gérer les entrées
        handleInput();
    }
    
    @Override
    public void render() {
        // Effacer l'écran
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        
        // Rendre le monde
        worldManager.render();
        
        // Rendre le joueur
        player.render();
    }
    
    @Override
    public void dispose() {
        System.out.println("Disposing GamePlay State");
    }
    
    /**
     * Gérer les entrées utilisateur
     */
    private void handleInput() {
        InputManager inputManager = InputManager.getInstance();
        
        // Échap pour revenir au menu principal
        if (inputManager.isKeyJustPressed(GLFW_KEY_ESCAPE)) {
            gsm.setState(new MainMenuState(gsm));
        }
        
        // Touches de mouvement gérées par le joueur
        // Le joueur gère ses propres entrées dans sa méthode update
    }
    
    /**
     * Callback appelé lorsqu'un combat démarre
     */
    @Override
    public void onCombatStart(Player player, Creature creature) {
        // Créer un nouvel état de combat
        CombatState combatState = new CombatState(gsm, player, creature, this);
        
        // Initialiser l'état de combat
        combatState.init();
        
        // Changer l'état actuel pour l'état de combat
        gsm.pushState(combatState);
    }
}

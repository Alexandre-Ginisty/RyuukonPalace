package com.ryuukonpalace.game.core.states;

import com.ryuukonpalace.game.combat.CombatSystem;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.capture.CaptureSystem;

import static org.lwjgl.opengl.GL11.*;

/**
 * État du jeu représentant un combat entre le joueur et une créature sauvage.
 * Gère l'intégration entre le système de combat et le système de créatures.
 */
public class CombatState implements GameState {
    
    private GameStateManager gsm;
    private CombatSystem combatSystem;
    private CaptureSystem captureSystem;
    private Player player;
    private Creature enemyCreature;
    private GameState previousState;
    
    /**
     * Constructeur
     * @param gsm Gestionnaire d'états du jeu
     * @param player Joueur
     * @param enemyCreature Créature ennemie
     * @param previousState État précédent (pour y revenir après le combat)
     */
    public CombatState(GameStateManager gsm, Player player, Creature enemyCreature, GameState previousState) {
        this.gsm = gsm;
        this.player = player;
        this.enemyCreature = enemyCreature;
        this.previousState = previousState;
        this.combatSystem = CombatSystem.getInstance();
        this.captureSystem = CaptureSystem.getInstance();
    }
    
    @Override
    public void init() {
        System.out.println("Initializing Combat State");
        
        // Initialiser le système de combat
        combatSystem.init();
        
        // Initialiser le système de capture
        captureSystem.init();
        
        // Démarrer le combat
        combatSystem.startCombat(player, enemyCreature);
    }
    
    @Override
    public void update(float dt) {
        // Mettre à jour le système de combat
        combatSystem.update(dt);
        
        // Mettre à jour le système de capture si actif
        if (captureSystem.isActive()) {
            captureSystem.update(dt);
        }
        
        // Vérifier si le combat est terminé
        if (combatSystem.isCompleted()) {
            // Attendre un peu avant de revenir à l'état précédent
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Terminer le combat
            combatSystem.endCombat();
            
            // Revenir à l'état précédent
            gsm.setState(previousState);
        }
    }
    
    @Override
    public void render() {
        // Effacer l'écran
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        
        // Rendre le système de combat
        combatSystem.render();
        
        // Rendre le système de capture si actif
        if (captureSystem.isActive()) {
            captureSystem.render();
        }
    }
    
    @Override
    public void dispose() {
        System.out.println("Disposing Combat State");
        
        // Terminer le combat si ce n'est pas déjà fait
        if (combatSystem.isActive()) {
            combatSystem.endCombat();
        }
        
        // Réinitialiser le système de capture
        if (captureSystem.isActive()) {
            captureSystem.reset();
        }
    }
}

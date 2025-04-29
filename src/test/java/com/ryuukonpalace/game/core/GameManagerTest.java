package com.ryuukonpalace.game.core;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitaires pour le gestionnaire de jeu
 */
public class GameManagerTest {
    
    private GameManager gameManager;
    
    @Before
    public void setUp() {
        gameManager = GameManager.getInstance();
    }
    
    @Test
    public void testSingleton() {
        // Vérifier que l'instance n'est pas null
        assertNotNull("Le gestionnaire de jeu ne devrait pas être null", gameManager);
        
        // Vérifier que c'est bien un singleton
        GameManager anotherInstance = GameManager.getInstance();
        assertSame("Les deux instances devraient être identiques (singleton)", gameManager, anotherInstance);
    }
    
    @Test
    public void testInitialization() {
        // Vérifier que le gestionnaire est correctement initialisé
        assertTrue("Le gestionnaire de jeu devrait être initialisé", gameManager.isInitialized());
    }
    
    @Test
    public void testGameState() {
        // Vérifier l'état initial du jeu
        assertEquals("L'état initial du jeu devrait être MAIN_MENU", GameState.State.MAIN_MENU, gameManager.getCurrentState());
        
        // Changer l'état du jeu
        gameManager.changeState(GameState.State.PLAYING);
        assertEquals("L'état du jeu devrait être PLAYING après changement", GameState.State.PLAYING, gameManager.getCurrentState());
        
        // Revenir à l'état initial pour les autres tests
        gameManager.changeState(GameState.State.MAIN_MENU);
    }
    
    @Test
    public void testResourceLoading() {
        // Vérifier que les ressources sont chargées
        assertTrue("Les ressources du jeu devraient être chargées", gameManager.areResourcesLoaded());
    }
    
    @Test
    public void testGameLoop() {
        // Tester le démarrage et l'arrêt de la boucle de jeu
        // Note: Ceci est un test simplifié car la boucle de jeu est difficile à tester complètement
        
        // Vérifier que la boucle de jeu n'est pas en cours d'exécution initialement
        assertFalse("La boucle de jeu ne devrait pas être en cours d'exécution initialement", gameManager.isGameLoopRunning());
        
        // Démarrer la boucle de jeu
        gameManager.startGameLoop();
        
        // Vérifier que la boucle de jeu est en cours d'exécution
        assertTrue("La boucle de jeu devrait être en cours d'exécution après démarrage", gameManager.isGameLoopRunning());
        
        // Arrêter la boucle de jeu
        gameManager.stopGameLoop();
        
        // Vérifier que la boucle de jeu est arrêtée
        assertFalse("La boucle de jeu ne devrait pas être en cours d'exécution après arrêt", gameManager.isGameLoopRunning());
    }
    
    @Test
    public void testPlayerManagement() {
        // Vérifier que le joueur est correctement géré
        assertNotNull("Le joueur ne devrait pas être null", gameManager.getPlayer());
    }
    
    @Test
    public void testAudioSystem() {
        // Vérifier que le système audio est correctement initialisé
        assertNotNull("Le gestionnaire audio ne devrait pas être null", gameManager.getAudioManager());
    }
}

package com.ryuukonpalace.game.core;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitaires pour le moteur de rendu
 */
public class RendererTest {
    
    private Renderer renderer;
    
    @Before
    public void setUp() {
        renderer = Renderer.getInstance();
    }
    
    @Test
    public void testSingleton() {
        // Vérifier que l'instance n'est pas null
        assertNotNull("Le moteur de rendu ne devrait pas être null", renderer);
        
        // Vérifier que c'est bien un singleton
        Renderer anotherInstance = Renderer.getInstance();
        assertSame("Les deux instances devraient être identiques (singleton)", renderer, anotherInstance);
    }
    
    @Test
    public void testInitialization() {
        // Vérifier que le moteur de rendu est correctement initialisé
        assertTrue("Le moteur de rendu devrait être initialisé", renderer.isInitialized());
    }
    
    @Test
    public void testScreenDimensions() {
        // Vérifier les dimensions de l'écran
        assertTrue("La largeur de l'écran devrait être positive", renderer.getScreenWidth() > 0);
        assertTrue("La hauteur de l'écran devrait être positive", renderer.getScreenHeight() > 0);
    }
    
    @Test
    public void testCameraPosition() {
        // Vérifier la position initiale de la caméra
        assertEquals("La position X initiale de la caméra devrait être 0", 0, renderer.getCameraX(), 0.001);
        assertEquals("La position Y initiale de la caméra devrait être 0", 0, renderer.getCameraY(), 0.001);
        
        // Déplacer la caméra
        float newX = 100.0f;
        float newY = 200.0f;
        renderer.setCameraPosition(newX, newY);
        
        // Vérifier la nouvelle position de la caméra
        assertEquals("La position X de la caméra devrait être mise à jour", newX, renderer.getCameraX(), 0.001);
        assertEquals("La position Y de la caméra devrait être mise à jour", newY, renderer.getCameraY(), 0.001);
        
        // Réinitialiser la position de la caméra pour les autres tests
        renderer.setCameraPosition(0, 0);
    }
    
    @Test
    public void testCameraZoom() {
        // Vérifier le zoom initial de la caméra
        assertEquals("Le zoom initial de la caméra devrait être 1.0", 1.0f, renderer.getCameraZoom(), 0.001);
        
        // Modifier le zoom
        float newZoom = 2.0f;
        renderer.setCameraZoom(newZoom);
        
        // Vérifier le nouveau zoom
        assertEquals("Le zoom de la caméra devrait être mis à jour", newZoom, renderer.getCameraZoom(), 0.001);
        
        // Réinitialiser le zoom pour les autres tests
        renderer.setCameraZoom(1.0f);
    }
    
    @Test
    public void testRenderingModes() {
        // Vérifier le mode de rendu initial
        assertEquals("Le mode de rendu initial devrait être NORMAL", RenderingMode.NORMAL, renderer.getRenderingMode());
        
        // Changer le mode de rendu
        renderer.setRenderingMode(RenderingMode.DEBUG);
        
        // Vérifier le nouveau mode de rendu
        assertEquals("Le mode de rendu devrait être DEBUG après changement", RenderingMode.DEBUG, renderer.getRenderingMode());
        
        // Revenir au mode normal pour les autres tests
        renderer.setRenderingMode(RenderingMode.NORMAL);
    }
    
    @Test
    public void testShaderPrograms() {
        // Vérifier que les programmes de shader sont chargés
        assertTrue("Les programmes de shader devraient être chargés", renderer.areShadersLoaded());
    }
}

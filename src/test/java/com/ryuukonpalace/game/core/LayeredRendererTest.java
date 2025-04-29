package com.ryuukonpalace.game.core;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitaires pour la classe LayeredRenderer.
 */
public class LayeredRendererTest {
    
    private LayeredRenderer layeredRenderer;
    
    @Before
    public void setUp() {
        layeredRenderer = LayeredRenderer.getInstance();
    }
    
    @Test
    public void testSingleton() {
        assertNotNull("Le renderer par couches ne devrait pas être null", layeredRenderer);
        
        LayeredRenderer anotherInstance = LayeredRenderer.getInstance();
        assertSame("Les deux instances devraient être identiques (singleton)", layeredRenderer, anotherInstance);
    }
    
    @Test
    public void testLayerYOffset() {
        // Valeur par défaut
        float initialOffset = -5.0f; // Valeur par défaut pour OBJECT_BACK
        
        // Nouvelle valeur
        float newOffset = -10.0f;
        layeredRenderer.setLayerYOffset(LayeredRenderer.Layer.OBJECT_BACK, newOffset);
        
        // Test avec une méthode indirecte puisque getLayerYOffset n'existe pas
        // On peut vérifier que le renderer ne lance pas d'exception
        layeredRenderer.addToLayer(
            LayeredRenderer.Layer.OBJECT_BACK, 
            1, // textureId fictif
            0, // x
            0, // y
            32, // width
            32, // height
            0 // depth
        );
        
        // Remettre la valeur par défaut
        layeredRenderer.setLayerYOffset(LayeredRenderer.Layer.OBJECT_BACK, initialOffset);
    }
    
    @Test
    public void testLayerScale() {
        // Valeur par défaut
        float initialScale = 1.0f; // Valeur par défaut
        
        // Nouvelle valeur
        float newScale = 2.0f;
        layeredRenderer.setLayerScale(LayeredRenderer.Layer.OBJECT_BACK, newScale);
        
        // Test avec une méthode indirecte puisque getLayerScale n'existe pas
        // On peut vérifier que le renderer ne lance pas d'exception
        layeredRenderer.addToLayer(
            LayeredRenderer.Layer.OBJECT_BACK, 
            1, // textureId fictif
            0, // x
            0, // y
            32, // width
            32, // height
            0 // depth
        );
        
        // Remettre la valeur par défaut
        layeredRenderer.setLayerScale(LayeredRenderer.Layer.OBJECT_BACK, initialScale);
    }
    
    @Test
    public void testDrawSpriteWithPerspective() {
        // Vérifier que la méthode ne lance pas d'exception
        layeredRenderer.drawSpriteWithPerspective(
            LayeredRenderer.Layer.CHARACTER,
            1, // textureId fictif
            100, // x
            100, // y
            32, // width
            32 // height
        );
    }
    
    @Test
    public void testDrawBuilding() {
        // Vérifier que la méthode ne lance pas d'exception
        layeredRenderer.drawBuilding(
            1, // textureId fictif
            100, // x
            100, // y
            64, // width
            96, // height
            32 // baseHeight
        );
    }
}

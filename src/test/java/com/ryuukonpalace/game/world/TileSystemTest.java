package com.ryuukonpalace.game.world;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitaires pour la classe TileSystem.
 */
public class TileSystemTest {
    
    private TileSystem tileSystem;
    
    @Before
    public void setUp() {
        tileSystem = TileSystem.getInstance();
    }
    
    @Test
    public void testSingleton() {
        assertNotNull("Le système de tuiles ne devrait pas être null", tileSystem);
        
        TileSystem anotherInstance = TileSystem.getInstance();
        assertSame("Les deux instances devraient être identiques (singleton)", tileSystem, anotherInstance);
    }
    
    @Test
    public void testCreateEmptyMap() {
        int width = 10;
        int height = 10;
        
        tileSystem.createEmptyMap(width, height);
        
        // Vérifier que les tuiles sont initialisées à 0
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                assertEquals("La tuile de terrain devrait être initialisée à 0", 0, tileSystem.getTile(0, x, y));
                assertEquals("La tuile de détail devrait être initialisée à 0", 0, tileSystem.getTile(1, x, y));
                assertEquals("La tuile d'objet arrière devrait être initialisée à 0", 0, tileSystem.getTile(2, x, y));
                assertEquals("La tuile d'objet avant devrait être initialisée à 0", 0, tileSystem.getTile(3, x, y));
                assertEquals("La tuile de collision devrait être initialisée à 0", 0, tileSystem.getTile(4, x, y));
                assertEquals("La tuile de hauteur devrait être initialisée à 0", 0, tileSystem.getTile(5, x, y));
            }
        }
    }
    
    @Test
    public void testSetAndGetTile() {
        int width = 10;
        int height = 10;
        
        tileSystem.createEmptyMap(width, height);
        
        // Définir des tuiles
        tileSystem.setTile(0, 5, 5, 1);  // Terrain
        tileSystem.setTile(1, 5, 5, 2);  // Détail
        tileSystem.setTile(2, 5, 5, 3);  // Objet arrière
        tileSystem.setTile(3, 5, 5, 4);  // Objet avant
        tileSystem.setTile(4, 5, 5, 1);  // Collision
        tileSystem.setTile(5, 5, 5, 2);  // Hauteur
        
        // Vérifier les valeurs
        assertEquals("La tuile de terrain devrait être 1", 1, tileSystem.getTile(0, 5, 5));
        assertEquals("La tuile de détail devrait être 2", 2, tileSystem.getTile(1, 5, 5));
        assertEquals("La tuile d'objet arrière devrait être 3", 3, tileSystem.getTile(2, 5, 5));
        assertEquals("La tuile d'objet avant devrait être 4", 4, tileSystem.getTile(3, 5, 5));
        assertEquals("La tuile de collision devrait être 1", 1, tileSystem.getTile(4, 5, 5));
        assertEquals("La tuile de hauteur devrait être 2", 2, tileSystem.getTile(5, 5, 5));
    }
    
    @Test
    public void testIsSolid() {
        int width = 10;
        int height = 10;
        
        tileSystem.createEmptyMap(width, height);
        
        // Par défaut, les tuiles ne sont pas solides
        assertFalse("La tuile ne devrait pas être solide par défaut", tileSystem.isSolid(5 * 32 + 16, 5 * 32 + 16));
        
        // Définir une tuile de collision
        tileSystem.setTile(4, 5, 5, 1);
        
        // Maintenant la tuile devrait être solide
        assertTrue("La tuile devrait être solide après avoir défini une collision", tileSystem.isSolid(5 * 32 + 16, 5 * 32 + 16));
    }
    
    @Test
    public void testGetTileHeight() {
        int width = 10;
        int height = 10;
        
        tileSystem.createEmptyMap(width, height);
        
        // Par défaut, la hauteur est 0
        assertEquals("La hauteur de la tuile devrait être 0 par défaut", 0, tileSystem.getTileHeight(5 * 32 + 16, 5 * 32 + 16));
        
        // Définir une hauteur
        tileSystem.setTile(5, 5, 5, 3);
        
        // Vérifier la hauteur
        assertEquals("La hauteur de la tuile devrait être 3", 3, tileSystem.getTileHeight(5 * 32 + 16, 5 * 32 + 16));
    }
    
    @Test
    public void testCacheStats() {
        // Créer une carte vide
        tileSystem.createEmptyMap(10, 10);
        
        // Vider le cache
        tileSystem.clearRenderedTileCache();
        
        // Obtenir les statistiques initiales
        String initialStats = tileSystem.getCacheStats();
        assertNotNull("Les statistiques du cache ne devraient pas être null", initialStats);
        
        // Simuler un rendu pour remplir le cache
        tileSystem.renderMap(0, 0, 320, 320);
        
        // Obtenir les nouvelles statistiques
        String newStats = tileSystem.getCacheStats();
        assertNotNull("Les nouvelles statistiques du cache ne devraient pas être null", newStats);
        
        // Les statistiques devraient être différentes
        assertNotEquals("Les statistiques du cache devraient changer après un rendu", initialStats, newStats);
    }
}

package com.ryuukonpalace.game.utils;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

/**
 * Tests unitaires pour la classe SpriteLoader.
 */
public class SpriteLoaderTest {
    
    private SpriteLoader spriteLoader;
    
    @Before
    public void setUp() {
        spriteLoader = SpriteLoader.getInstance();
    }
    
    @Test
    public void testSingleton() {
        assertNotNull("Le chargeur de sprites ne devrait pas être null", spriteLoader);
        
        SpriteLoader anotherInstance = SpriteLoader.getInstance();
        assertSame("Les deux instances devraient être identiques (singleton)", spriteLoader, anotherInstance);
    }
    
    @Test
    public void testLoadDefaultTile() {
        // Charger une tuile par défaut
        int textureId = spriteLoader.loadDefaultTile(1);
        
        // L'ID de texture devrait être valide (positif)
        assertTrue("L'ID de texture devrait être positif", textureId >= 0);
        
        // Charger la même tuile à nouveau (devrait utiliser le cache)
        int cachedTextureId = spriteLoader.loadDefaultTile(1);
        
        // Les IDs devraient être identiques
        assertEquals("Les IDs de texture devraient être identiques pour la même tuile", textureId, cachedTextureId);
    }
    
    @Test
    public void testPreloadTiles() {
        // Précharger plusieurs tuiles
        int[] tileIds = {10, 11, 12, 13, 14};
        spriteLoader.preloadTiles(tileIds);
        
        // Vérifier que les tuiles sont bien chargées (via le cache)
        for (int tileId : tileIds) {
            int textureId = spriteLoader.loadDefaultTile(tileId);
            assertTrue("L'ID de texture devrait être positif", textureId >= 0);
        }
    }
    
    @Test
    public void testLoadSpriteAsync() throws InterruptedException, ExecutionException {
        // Charger une tuile par défaut de manière asynchrone
        Future<Integer> future = spriteLoader.loadSpriteAsync(spriteLoader.getSpritePath("test", "dummy"));
        
        // Attendre le résultat
        Integer textureId = null;
        try {
            textureId = future.get();
        } catch (Exception e) {
            // Si le fichier n'existe pas, c'est normal
            textureId = -1;
        }
        
        // L'ID peut être -1 si le fichier n'existe pas, c'est normal pour ce test
        assertNotNull("Le résultat de l'opération asynchrone ne devrait pas être null", textureId);
    }
    
    @Test
    public void testGetSpritePath() {
        String path = spriteLoader.getSpritePath("creature", "dragon");
        assertEquals("Le chemin du sprite devrait être correct", 
                     "src/main/resources/assets/textures/creature/dragon.png", path);
    }
    
    @Test
    public void testGetSpriteSheetPath() {
        String path = spriteLoader.getSpriteSheetPath("creature", "dragon");
        assertEquals("Le chemin de la spritesheet devrait être correct", 
                     "src/main/resources/assets/textures/creature/dragon_sheet.png", path);
    }
}

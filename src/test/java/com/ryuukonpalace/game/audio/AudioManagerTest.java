package com.ryuukonpalace.game.audio;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitaires pour le gestionnaire audio
 */
public class AudioManagerTest {
    
    private AudioManager audioManager;
    
    @Before
    public void setUp() {
        audioManager = AudioManager.getInstance();
    }
    
    @Test
    public void testSingleton() {
        // Vérifier que l'instance n'est pas null
        assertNotNull("Le gestionnaire audio ne devrait pas être null", audioManager);
        
        // Vérifier que c'est bien un singleton
        AudioManager anotherInstance = AudioManager.getInstance();
        assertSame("Les deux instances devraient être identiques (singleton)", audioManager, anotherInstance);
    }
    
    @Test
    public void testInitialization() {
        // Vérifier que le gestionnaire audio est correctement initialisé
        assertTrue("Le gestionnaire audio devrait être initialisé", audioManager.isInitialized());
    }
    
    @Test
    public void testSoundLoading() {
        // Vérifier le chargement d'un son
        String soundId = "ui_click";
        boolean loaded = audioManager.loadSound(soundId, "sounds/ui/click.wav");
        assertTrue("Le son devrait être chargé avec succès", loaded);
        
        // Vérifier que le son est bien chargé
        assertTrue("Le son devrait être disponible après chargement", audioManager.isSoundLoaded(soundId));
    }
    
    @Test
    public void testSoundPlayback() {
        // Charger un son pour le test
        String soundId = "ui_hover";
        audioManager.loadSound(soundId, "sounds/ui/hover.wav");
        
        // Jouer le son
        boolean played = audioManager.playSound(soundId);
        assertTrue("Le son devrait être joué avec succès", played);
    }
    
    @Test
    public void testMusicLoading() {
        // Vérifier le chargement d'une musique
        String musicId = "main_theme";
        boolean loaded = audioManager.loadMusic(musicId, "music/main_theme.wav");
        assertTrue("La musique devrait être chargée avec succès", loaded);
        
        // Vérifier que la musique est bien chargée
        assertTrue("La musique devrait être disponible après chargement", audioManager.isMusicLoaded(musicId));
    }
    
    @Test
    public void testMusicPlayback() {
        // Charger une musique pour le test
        String musicId = "battle_theme";
        audioManager.loadMusic(musicId, "music/battle_theme.wav");
        
        // Jouer la musique
        boolean played = audioManager.playMusic(musicId);
        assertTrue("La musique devrait être jouée avec succès", played);
        
        // Vérifier que la musique est en cours de lecture
        assertTrue("La musique devrait être en cours de lecture", audioManager.isMusicPlaying(musicId));
        
        // Arrêter la musique
        audioManager.stopMusic();
        
        // Vérifier que la musique est arrêtée
        assertFalse("La musique ne devrait plus être en cours de lecture", audioManager.isMusicPlaying(musicId));
    }
    
    @Test
    public void testVolumeControl() {
        // Vérifier le volume initial
        float initialMusicVolume = audioManager.getMusicVolume();
        float initialSoundVolume = audioManager.getSoundVolume();
        
        // Modifier les volumes
        float newMusicVolume = 0.5f;
        float newSoundVolume = 0.7f;
        audioManager.setMusicVolume(newMusicVolume);
        audioManager.setSoundVolume(newSoundVolume);
        
        // Vérifier les nouveaux volumes
        assertEquals("Le volume de la musique devrait être mis à jour", newMusicVolume, audioManager.getMusicVolume(), 0.001);
        assertEquals("Le volume des sons devrait être mis à jour", newSoundVolume, audioManager.getSoundVolume(), 0.001);
        
        // Restaurer les volumes initiaux
        audioManager.setMusicVolume(initialMusicVolume);
        audioManager.setSoundVolume(initialSoundVolume);
    }
    
    @Test
    public void testMuteControl() {
        // Vérifier l'état initial
        boolean initialMusicMuted = audioManager.isMusicMuted();
        boolean initialSoundMuted = audioManager.isSoundMuted();
        
        // Couper le son
        audioManager.setMusicMuted(true);
        audioManager.setSoundMuted(true);
        
        // Vérifier que le son est coupé
        assertTrue("La musique devrait être coupée", audioManager.isMusicMuted());
        assertTrue("Les sons devraient être coupés", audioManager.isSoundMuted());
        
        // Rétablir le son
        audioManager.setMusicMuted(false);
        audioManager.setSoundMuted(false);
        
        // Vérifier que le son est rétabli
        assertFalse("La musique ne devrait plus être coupée", audioManager.isMusicMuted());
        assertFalse("Les sons ne devraient plus être coupés", audioManager.isSoundMuted());
        
        // Restaurer l'état initial
        audioManager.setMusicMuted(initialMusicMuted);
        audioManager.setSoundMuted(initialSoundMuted);
    }
}

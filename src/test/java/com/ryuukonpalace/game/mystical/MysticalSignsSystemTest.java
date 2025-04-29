package com.ryuukonpalace.game.mystical;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.json.JSONObject;
import org.json.JSONArray;

import com.ryuukonpalace.game.player.Player;

/**
 * Tests unitaires pour le système de signes mystiques
 */
public class MysticalSignsSystemTest {
    
    private MysticalSignsSystem mysticalSignsSystem;
    
    @Mock
    private Player mockPlayer;
    
    @Before
    public void setUp() {
        // Initialiser les mocks
        MockitoAnnotations.initMocks(this);
        
        // Configurer le mock du joueur
        when(mockPlayer.getId()).thenReturn("player1");
        when(mockPlayer.getEnergy()).thenReturn(100);
        
        // Initialiser le système de signes mystiques
        mysticalSignsSystem = MysticalSignsSystem.getInstance();
    }
    
    @Test
    public void testInitialization() {
        // Vérifier que le système est correctement initialisé
        assertNotNull("Le système de signes mystiques ne devrait pas être null", mysticalSignsSystem);
    }
    
    @Test
    public void testLearnSign() {
        // Tester l'apprentissage d'un signe
        String signId = "traditional_fire";
        boolean result = mysticalSignsSystem.learnSign(mockPlayer, signId);
        
        // Vérifier que le signe a été appris
        assertTrue("Le joueur devrait pouvoir apprendre un signe", result);
        assertTrue("Le système devrait reconnaître que le joueur connaît le signe", 
                  mysticalSignsSystem.getKnownSignsByCategory(mockPlayer, "traditional").stream()
                  .anyMatch(sign -> sign.getString("id").equals(signId)));
    }
    
    @Test
    public void testUseSign() {
        // Apprendre d'abord le signe
        String signId = "traditional_water";
        mysticalSignsSystem.learnSign(mockPlayer, signId);
        
        // Configurer le mock pour permettre l'utilisation d'énergie
        doNothing().when(mockPlayer).useEnergy(anyInt());
        
        // Utiliser le signe
        boolean result = mysticalSignsSystem.useSign(mockPlayer, signId);
        
        // Vérifier que le signe a été utilisé
        assertTrue("Le joueur devrait pouvoir utiliser un signe qu'il connaît", result);
        
        // Vérifier que l'énergie a été consommée
        verify(mockPlayer).useEnergy(anyInt());
    }
    
    @Test
    public void testUseSignWithInsufficientEnergy() {
        // Apprendre d'abord le signe
        String signId = "traditional_earth";
        mysticalSignsSystem.learnSign(mockPlayer, signId);
        
        // Configurer le mock pour simuler un manque d'énergie
        when(mockPlayer.getEnergy()).thenReturn(0);
        
        // Tenter d'utiliser le signe
        boolean result = mysticalSignsSystem.useSign(mockPlayer, signId);
        
        // Vérifier que le signe n'a pas été utilisé
        assertFalse("Le joueur ne devrait pas pouvoir utiliser un signe sans énergie suffisante", result);
    }
    
    @Test
    public void testUseUnknownSign() {
        // Tenter d'utiliser un signe que le joueur ne connaît pas
        String signId = "forbidden_power";
        
        // Utiliser le signe
        boolean result = mysticalSignsSystem.useSign(mockPlayer, signId);
        
        // Vérifier que le signe n'a pas été utilisé
        assertFalse("Le joueur ne devrait pas pouvoir utiliser un signe qu'il ne connaît pas", result);
    }
    
    @Test
    public void testPracticeSign() {
        // Apprendre d'abord le signe
        String signId = "spiritual_healing";
        mysticalSignsSystem.learnSign(mockPlayer, signId);
        
        // Configurer le mock pour permettre l'utilisation d'énergie
        doNothing().when(mockPlayer).useEnergy(anyInt());
        
        // Pratiquer le signe
        boolean result = mysticalSignsSystem.practiceSign(mockPlayer, signId);
        
        // Vérifier que le signe a été pratiqué
        assertTrue("Le joueur devrait pouvoir pratiquer un signe qu'il connaît", result);
        
        // Vérifier que l'énergie a été consommée (moitié du coût normal)
        verify(mockPlayer).useEnergy(anyInt());
    }
    
    @Test
    public void testSignCooldown() {
        // Apprendre d'abord le signe
        String signId = "combat_shield";
        mysticalSignsSystem.learnSign(mockPlayer, signId);
        
        // Configurer le mock pour permettre l'utilisation d'énergie
        doNothing().when(mockPlayer).useEnergy(anyInt());
        
        // Utiliser le signe
        mysticalSignsSystem.useSign(mockPlayer, signId);
        
        // Vérifier que le signe est en temps de recharge
        assertTrue("Le signe devrait être en temps de recharge après utilisation", 
                  mysticalSignsSystem.isSignOnCooldown(mockPlayer, signId));
        
        // Vérifier que le temps de recharge restant est positif
        assertTrue("Le temps de recharge restant devrait être positif", 
                  mysticalSignsSystem.getRemainingCooldown(mockPlayer, signId) > 0);
    }
    
    @Test
    public void testGetSignMasteryLevel() {
        // Apprendre d'abord le signe
        String signId = "experimental_fusion";
        mysticalSignsSystem.learnSign(mockPlayer, signId);
        
        // Vérifier le niveau de maîtrise initial
        int initialLevel = mysticalSignsSystem.getSignMasteryLevel(mockPlayer, signId);
        assertEquals("Le niveau de maîtrise initial devrait être 1", 1, initialLevel);
        
        // Configurer le mock pour permettre l'utilisation d'énergie
        doNothing().when(mockPlayer).useEnergy(anyInt());
        
        // Pratiquer le signe plusieurs fois
        for (int i = 0; i < 5; i++) {
            mysticalSignsSystem.practiceSign(mockPlayer, signId);
        }
        
        // Vérifier que le niveau de maîtrise a augmenté
        int newLevel = mysticalSignsSystem.getSignMasteryLevel(mockPlayer, signId);
        assertTrue("Le niveau de maîtrise devrait augmenter après pratique", newLevel > initialLevel);
    }
}

package com.ryuukonpalace.game.player;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.ryuukonpalace.game.items.ItemType;
import com.ryuukonpalace.game.items.ConcreteItem;

/**
 * Tests unitaires pour le joueur
 */
public class PlayerTest {
    
    private Player player;
    
    @Before
    public void setUp() {
        // Créer un nouveau joueur pour chaque test
        player = new Player(100, 100);
        player.setEnergy(100);
    }
    
    @Test
    public void testInitialization() {
        // Vérifier que le joueur est correctement initialisé
        assertNotNull("Le joueur ne devrait pas être null", player);
        assertEquals("La position X initiale du joueur devrait être 100", 100, player.getX(), 0.001);
        assertEquals("La position Y initiale du joueur devrait être 100", 100, player.getY(), 0.001);
    }
    
    @Test
    public void testMovement() {
        // Position initiale
        float initialX = player.getX();
        float initialY = player.getY();
        
        // Déplacer le joueur
        float deltaX = 10.0f;
        float deltaY = 5.0f;
        player.move(deltaX, deltaY);
        
        // Vérifier la nouvelle position
        assertEquals("La position X du joueur devrait être mise à jour", initialX + deltaX, player.getX(), 0.001);
        assertEquals("La position Y du joueur devrait être mise à jour", initialY + deltaY, player.getY(), 0.001);
    }
    
    @Test
    public void testEnergyManagement() {
        // Vérifier l'énergie initiale
        assertEquals("L'énergie initiale du joueur devrait être 100", 100, player.getEnergy());
        
        // Utiliser de l'énergie
        int energyUsed = 30;
        player.useEnergy(energyUsed);
        
        // Vérifier l'énergie restante
        assertEquals("L'énergie du joueur devrait être réduite", 70, player.getEnergy());
        
        // Récupérer de l'énergie
        int energyRecovered = 20;
        player.recoverEnergy(energyRecovered);
        
        // Vérifier l'énergie après récupération
        assertEquals("L'énergie du joueur devrait être augmentée", 90, player.getEnergy());
    }
    
    @Test
    public void testStatusEffects() {
        // Vérifier qu'aucun effet de statut n'est actif initialement
        assertFalse("Le joueur ne devrait pas avoir d'effet de statut initialement", player.hasStatusEffect("strength"));
        
        // Ajouter un effet de statut
        String effectName = "strength";
        int duration = 5;
        player.addStatusEffect(effectName, duration);
        
        // Vérifier que l'effet est actif
        assertTrue("Le joueur devrait avoir l'effet de statut après ajout", player.hasStatusEffect(effectName));
        assertEquals("La durée de l'effet devrait être correcte", duration, player.getStatusEffectDuration(effectName));
        
        // Mettre à jour les effets (simuler le passage du temps)
        for (int i = 0; i < duration; i++) {
            player.updateStatusEffects();
        }
        
        // Vérifier que l'effet est terminé
        assertFalse("L'effet de statut devrait être terminé après la durée", player.hasStatusEffect(effectName));
    }
    
    @Test
    public void testInventory() {
        // Vérifier que l'inventaire est initialement vide
        assertTrue("L'inventaire du joueur devrait être initialement vide", player.getInventory().isEmpty());
        
        // Ajouter un item à l'inventaire
        ConcreteItem item = new ConcreteItem("potion", "Potion de vie", ItemType.CONSUMABLE);
        player.addToInventory(item);
        
        // Vérifier que l'item est dans l'inventaire
        assertTrue("L'item devrait être dans l'inventaire après ajout", player.hasItem("potion"));
        assertEquals("L'inventaire devrait contenir 1 item", 1, player.getInventory().size());
        
        // Retirer l'item de l'inventaire
        player.removeFromInventory("potion");
        
        // Vérifier que l'inventaire est à nouveau vide
        assertFalse("L'item ne devrait plus être dans l'inventaire après retrait", player.hasItem("potion"));
        assertTrue("L'inventaire du joueur devrait être vide après retrait", player.getInventory().isEmpty());
    }
    
    @Test
    public void testCollision() {
        // Vérifier la détection de collision
        float obstacleX = 110.0f;
        float obstacleY = 110.0f;
        float obstacleWidth = 20.0f;
        float obstacleHeight = 20.0f;
        
        // Le joueur devrait être en collision avec l'obstacle
        assertTrue("Le joueur devrait être en collision avec l'obstacle", 
                  player.isCollidingWith(obstacleX, obstacleY, obstacleWidth, obstacleHeight));
        
        // Déplacer le joueur loin de l'obstacle
        player.setPosition(200, 200);
        
        // Le joueur ne devrait plus être en collision avec l'obstacle
        assertFalse("Le joueur ne devrait plus être en collision avec l'obstacle après déplacement", 
                   player.isCollidingWith(obstacleX, obstacleY, obstacleWidth, obstacleHeight));
    }
    
    @Test
    public void testPlayerState() {
        // Vérifier l'état initial du joueur
        assertEquals("L'état initial du joueur devrait être IDLE", PlayerState.IDLE, player.getState());
        
        // Changer l'état du joueur
        player.setState(PlayerState.WALKING);
        
        // Vérifier le nouvel état
        assertEquals("L'état du joueur devrait être WALKING après changement", PlayerState.WALKING, player.getState());
    }
}

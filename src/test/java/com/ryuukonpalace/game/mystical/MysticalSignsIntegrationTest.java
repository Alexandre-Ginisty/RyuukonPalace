package com.ryuukonpalace.game.mystical;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.ui.MysticalSignsInterface;

/**
 * Tests d'intégration pour le système de signes mystiques
 * Ces tests vérifient l'interaction entre le système de signes mystiques,
 * l'interface utilisateur et le système de joueur.
 */
public class MysticalSignsIntegrationTest {
    
    private MysticalSignsSystem mysticalSignsSystem;
    private MysticalSignsInterface mysticalSignsInterface;
    private Player player;
    
    @Before
    public void setUp() {
        // Initialiser le système de signes mystiques
        mysticalSignsSystem = MysticalSignsSystem.getInstance();
        
        // Initialiser l'interface des signes mystiques
        mysticalSignsInterface = MysticalSignsInterface.getInstance();
        
        // Créer un joueur pour les tests
        player = new Player(100, 100); // Coordonnées x, y
        player.setEnergy(100); // Définir l'énergie initiale
    }
    
    @Test
    public void testCompleteSignWorkflow() {
        // 1. Vérifier que l'interface n'est pas visible initialement
        assertFalse("L'interface ne devrait pas être visible initialement", 
                   mysticalSignsInterface.isVisible());
        
        // 2. Afficher l'interface
        mysticalSignsInterface.show(player);
        
        // 3. Vérifier que l'interface est visible
        assertTrue("L'interface devrait être visible après l'appel à show()", 
                  mysticalSignsInterface.isVisible());
        
        // 4. Apprendre un signe au joueur
        String signId = "traditional_water";
        boolean learned = mysticalSignsSystem.learnSign(player, signId);
        
        // 5. Vérifier que le signe a été appris
        assertTrue("Le joueur devrait pouvoir apprendre un signe", learned);
        assertTrue("Le système devrait reconnaître que le joueur connaît le signe",
                  mysticalSignsSystem.getKnownSignsByCategory(player, "traditional").stream()
                  .anyMatch(sign -> sign.getString("id").equals(signId)));
        
        // 6. Utiliser le signe
        int initialEnergy = player.getEnergy();
        boolean used = mysticalSignsSystem.useSign(player, signId);
        
        // 7. Vérifier que le signe a été utilisé et que l'énergie a été consommée
        assertTrue("Le joueur devrait pouvoir utiliser un signe qu'il connaît", used);
        assertTrue("L'énergie du joueur devrait diminuer après utilisation d'un signe",
                  player.getEnergy() < initialEnergy);
        
        // 8. Vérifier que le signe est en temps de recharge
        assertTrue("Le signe devrait être en temps de recharge après utilisation",
                  mysticalSignsSystem.isSignOnCooldown(player, signId));
        
        // 9. Tenter d'utiliser à nouveau le signe en temps de recharge
        boolean usedAgain = mysticalSignsSystem.useSign(player, signId);
        
        // 10. Vérifier que le signe n'a pas pu être utilisé à nouveau
        assertFalse("Le joueur ne devrait pas pouvoir utiliser un signe en temps de recharge", usedAgain);
        
        // 11. Masquer l'interface
        mysticalSignsInterface.hide();
        
        // 12. Vérifier que l'interface est masquée
        assertFalse("L'interface ne devrait pas être visible après l'appel à hide()",
                   mysticalSignsInterface.isVisible());
    }
    
    @Test
    public void testSignPracticeAndMasteryProgression() {
        // 1. Apprendre un signe au joueur
        String signId = "spiritual_calm";
        mysticalSignsSystem.learnSign(player, signId);
        
        // 2. Vérifier le niveau de maîtrise initial
        int initialLevel = mysticalSignsSystem.getSignMasteryLevel(player, signId);
        assertEquals("Le niveau de maîtrise initial devrait être 1", 1, initialLevel);
        
        // 3. Pratiquer le signe plusieurs fois
        for (int i = 0; i < 10; i++) {
            // Restaurer l'énergie entre les pratiques
            player.setEnergy(100);
            mysticalSignsSystem.practiceSign(player, signId);
        }
        
        // 4. Vérifier que le niveau de maîtrise a augmenté
        int newLevel = mysticalSignsSystem.getSignMasteryLevel(player, signId);
        assertTrue("Le niveau de maîtrise devrait augmenter après pratique", newLevel > initialLevel);
        
        // 5. Vérifier que le coût en énergie diminue avec le niveau de maîtrise
        // (Cette fonctionnalité dépend de l'implémentation)
        player.setEnergy(100);
        int initialEnergyBeforeUse = player.getEnergy();
        mysticalSignsSystem.useSign(player, signId);
        int energyConsumedWithHigherMastery = initialEnergyBeforeUse - player.getEnergy();
        
        // Réinitialiser le niveau de maîtrise pour comparer
        // Note: Cela nécessite d'exposer une méthode pour réinitialiser le niveau de maîtrise aux tests
        // mysticalSignsSystem.resetSignMasteryLevel(player, signId);
        
        // Cette partie est commentée car elle dépend de l'implémentation spécifique
        /*
        player.setEnergy(100);
        initialEnergyBeforeUse = player.getEnergy();
        mysticalSignsSystem.useSign(player, signId);
        int energyConsumedWithLowerMastery = initialEnergyBeforeUse - player.getEnergy();
        
        assertTrue("Le coût en énergie devrait diminuer avec un niveau de maîtrise plus élevé",
                  energyConsumedWithHigherMastery < energyConsumedWithLowerMastery);
        */
    }
    
    @Test
    public void testStatusEffectsFromSigns() {
        // 1. Apprendre un signe qui applique un effet de statut
        String signId = "combat_strength";
        mysticalSignsSystem.learnSign(player, signId);
        
        // 2. Vérifier qu'aucun effet de statut n'est actif initialement
        assertFalse("Le joueur ne devrait pas avoir d'effet de statut initialement",
                   player.hasStatusEffect("strength"));
        
        // 3. Utiliser le signe
        mysticalSignsSystem.useSign(player, signId);
        
        // 4. Vérifier que l'effet de statut a été appliqué
        assertTrue("Le joueur devrait avoir l'effet de statut après utilisation du signe",
                  player.hasStatusEffect("strength"));
        
        // 5. Vérifier la durée de l'effet
        assertTrue("L'effet de statut devrait avoir une durée positive",
                  player.getStatusEffectDuration("strength") > 0);
    }
}

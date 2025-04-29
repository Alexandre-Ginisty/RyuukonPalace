package com.ryuukonpalace.game.mystical;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.json.JSONObject;

import com.ryuukonpalace.game.player.Player;

/**
 * Tests unitaires pour les effets des signes mystiques
 */
public class MysticalSignEffectsTest {
    
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
        doNothing().when(mockPlayer).useEnergy(anyInt());
        doNothing().when(mockPlayer).addStatusEffect(anyString(), anyInt());
        doNothing().when(mockPlayer).recoverEnergy(anyInt());
        
        // Initialiser le système de signes mystiques
        mysticalSignsSystem = MysticalSignsSystem.getInstance();
    }
    
    @Test
    public void testApplyCombatSignEffects() {
        // Créer un signe de combat pour le test
        JSONObject combatSign = new JSONObject();
        combatSign.put("id", "combat_shield");
        combatSign.put("name", "Bouclier de Combat");
        combatSign.put("category", "combat");
        combatSign.put("description", "Crée un bouclier protecteur");
        combatSign.put("effect", "Augmente la défense pendant 5 tours");
        combatSign.put("cost", "20 points d'énergie");
        combatSign.put("cooldown", "30 secondes");
        combatSign.put("risks", "Aucun");
        combatSign.put("lore", "Un signe traditionnel utilisé par les guerriers");
        
        // Apprendre le signe au joueur
        mysticalSignsSystem.learnSign(mockPlayer, "combat_shield");
        
        // Utiliser le signe
        mysticalSignsSystem.useSign(mockPlayer, "combat_shield");
        
        // Vérifier que l'effet de statut a été ajouté
        verify(mockPlayer).addStatusEffect(eq("protection"), anyInt());
    }
    
    @Test
    public void testApplySpiritualSignEffects() {
        // Créer un signe spirituel pour le test
        JSONObject spiritualSign = new JSONObject();
        spiritualSign.put("id", "spiritual_healing");
        spiritualSign.put("name", "Guérison Spirituelle");
        spiritualSign.put("category", "spiritual");
        spiritualSign.put("description", "Restaure l'énergie vitale");
        spiritualSign.put("effect", "Récupère de l'énergie");
        spiritualSign.put("cost", "10 points d'énergie");
        spiritualSign.put("cooldown", "60 secondes");
        spiritualSign.put("risks", "Aucun");
        spiritualSign.put("lore", "Un signe spirituel utilisé par les guérisseurs");
        
        // Apprendre le signe au joueur
        mysticalSignsSystem.learnSign(mockPlayer, "spiritual_healing");
        
        // Utiliser le signe
        mysticalSignsSystem.useSign(mockPlayer, "spiritual_healing");
        
        // Vérifier que de l'énergie a été récupérée
        verify(mockPlayer).recoverEnergy(anyInt());
    }
    
    @Test
    public void testApplyForbiddenSignEffects() {
        // Créer un signe interdit pour le test
        JSONObject forbiddenSign = new JSONObject();
        forbiddenSign.put("id", "forbidden_power");
        forbiddenSign.put("name", "Pouvoir Interdit");
        forbiddenSign.put("category", "forbidden");
        forbiddenSign.put("description", "Libère une puissance interdite");
        forbiddenSign.put("effect", "Augmente considérablement la puissance");
        forbiddenSign.put("cost", "40 points d'énergie");
        forbiddenSign.put("cooldown", "120 secondes");
        forbiddenSign.put("risks", "Risque d'épuisement ou de corruption");
        forbiddenSign.put("lore", "Un signe interdit dont l'usage est proscrit");
        
        // Apprendre le signe au joueur
        mysticalSignsSystem.learnSign(mockPlayer, "forbidden_power");
        
        // Utiliser le signe
        mysticalSignsSystem.useSign(mockPlayer, "forbidden_power");
        
        // Vérifier qu'un effet a été ajouté (soit positif, soit négatif)
        verify(mockPlayer, atLeastOnce()).addStatusEffect(anyString(), anyInt());
    }
    
    @Test
    public void testApplyExperimentalSignEffects() {
        // Créer un signe expérimental pour le test
        JSONObject experimentalSign = new JSONObject();
        experimentalSign.put("id", "experimental_fusion");
        experimentalSign.put("name", "Fusion Expérimentale");
        experimentalSign.put("category", "experimental");
        experimentalSign.put("description", "Fusionne les énergies élémentaires");
        experimentalSign.put("effect", "Effets variables et imprévisibles");
        experimentalSign.put("cost", "30 points d'énergie");
        experimentalSign.put("cooldown", "90 secondes");
        experimentalSign.put("risks", "Effets secondaires imprévisibles");
        experimentalSign.put("lore", "Un signe créé par des chercheurs en magie");
        
        // Apprendre le signe au joueur
        mysticalSignsSystem.learnSign(mockPlayer, "experimental_fusion");
        
        // Utiliser le signe
        mysticalSignsSystem.useSign(mockPlayer, "experimental_fusion");
        
        // Vérifier qu'un effet a été ajouté
        verify(mockPlayer, atLeastOnce()).addStatusEffect(anyString(), anyInt());
    }
    
    @Test
    public void testApplyTraditionalSignEffects() {
        // Créer un signe traditionnel pour le test
        JSONObject traditionalSign = new JSONObject();
        traditionalSign.put("id", "traditional_fire");
        traditionalSign.put("name", "Signe du Feu");
        traditionalSign.put("category", "traditional");
        traditionalSign.put("description", "Invoque l'élément du feu");
        traditionalSign.put("effect", "Harmonie avec le feu pendant 5 tours");
        traditionalSign.put("cost", "15 points d'énergie");
        traditionalSign.put("cooldown", "45 secondes");
        traditionalSign.put("risks", "Aucun");
        traditionalSign.put("lore", "Un signe traditionnel transmis de génération en génération");
        
        // Apprendre le signe au joueur
        mysticalSignsSystem.learnSign(mockPlayer, "traditional_fire");
        
        // Utiliser le signe
        mysticalSignsSystem.useSign(mockPlayer, "traditional_fire");
        
        // Vérifier que l'effet d'harmonie avec le feu a été ajouté
        verify(mockPlayer).addStatusEffect(eq("fire_attunement"), anyInt());
    }
}

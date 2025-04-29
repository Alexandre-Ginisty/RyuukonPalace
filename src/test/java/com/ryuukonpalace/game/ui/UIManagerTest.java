package com.ryuukonpalace.game.ui;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.ryuukonpalace.game.player.Player;

/**
 * Tests unitaires pour le gestionnaire d'interface utilisateur
 */
public class UIManagerTest {
    
    private UIManager uiManager;
    private Player player;
    
    @Before
    public void setUp() {
        uiManager = UIManager.getInstance();
        player = new Player(100, 100);
    }
    
    @Test
    public void testSingleton() {
        // Vérifier que l'instance n'est pas null
        assertNotNull("Le gestionnaire d'interface utilisateur ne devrait pas être null", uiManager);
        
        // Vérifier que c'est bien un singleton
        UIManager anotherInstance = UIManager.getInstance();
        assertSame("Les deux instances devraient être identiques (singleton)", uiManager, anotherInstance);
    }
    
    @Test
    public void testInitialization() {
        // Vérifier que le gestionnaire d'interface utilisateur est correctement initialisé
        assertTrue("Le gestionnaire d'interface utilisateur devrait être initialisé", uiManager.isInitialized());
    }
    
    @Test
    public void testInventoryInterface() {
        // Vérifier que l'interface d'inventaire n'est pas visible initialement
        assertFalse("L'interface d'inventaire ne devrait pas être visible initialement", uiManager.isInventoryVisible());
        
        // Afficher l'interface d'inventaire
        uiManager.showInventory(player);
        
        // Vérifier que l'interface d'inventaire est visible
        assertTrue("L'interface d'inventaire devrait être visible après l'appel à showInventory()", uiManager.isInventoryVisible());
        
        // Masquer l'interface d'inventaire
        uiManager.hideInventory();
        
        // Vérifier que l'interface d'inventaire est masquée
        assertFalse("L'interface d'inventaire ne devrait pas être visible après l'appel à hideInventory()", uiManager.isInventoryVisible());
    }
    
    @Test
    public void testMysticalSignsInterface() {
        // Vérifier que l'interface des signes mystiques n'est pas visible initialement
        assertFalse("L'interface des signes mystiques ne devrait pas être visible initialement", uiManager.isMysticalSignsInterfaceVisible());
        
        // Afficher l'interface des signes mystiques
        uiManager.showMysticalSignsInterface(player);
        
        // Vérifier que l'interface des signes mystiques est visible
        assertTrue("L'interface des signes mystiques devrait être visible après l'appel à showMysticalSignsInterface()", uiManager.isMysticalSignsInterfaceVisible());
        
        // Masquer l'interface des signes mystiques
        uiManager.hideMysticalSignsInterface();
        
        // Vérifier que l'interface des signes mystiques est masquée
        assertFalse("L'interface des signes mystiques ne devrait pas être visible après l'appel à hideMysticalSignsInterface()", uiManager.isMysticalSignsInterfaceVisible());
    }
    
    @Test
    public void testDialogInterface() {
        // Vérifier que l'interface de dialogue n'est pas visible initialement
        assertFalse("L'interface de dialogue ne devrait pas être visible initialement", uiManager.isDialogVisible());
        
        // Afficher un dialogue
        String dialogText = "Ceci est un test de dialogue.";
        uiManager.showDialog(dialogText);
        
        // Vérifier que l'interface de dialogue est visible
        assertTrue("L'interface de dialogue devrait être visible après l'appel à showDialog()", uiManager.isDialogVisible());
        
        // Vérifier le texte du dialogue
        assertEquals("Le texte du dialogue devrait correspondre", dialogText, uiManager.getDialogText());
        
        // Masquer l'interface de dialogue
        uiManager.hideDialog();
        
        // Vérifier que l'interface de dialogue est masquée
        assertFalse("L'interface de dialogue ne devrait pas être visible après l'appel à hideDialog()", uiManager.isDialogVisible());
    }
    
    @Test
    public void testMenuInterface() {
        // Vérifier que l'interface de menu n'est pas visible initialement
        assertFalse("L'interface de menu ne devrait pas être visible initialement", uiManager.isMenuVisible());
        
        // Afficher le menu
        uiManager.showMenu();
        
        // Vérifier que l'interface de menu est visible
        assertTrue("L'interface de menu devrait être visible après l'appel à showMenu()", uiManager.isMenuVisible());
        
        // Masquer l'interface de menu
        uiManager.hideMenu();
        
        // Vérifier que l'interface de menu est masquée
        assertFalse("L'interface de menu ne devrait pas être visible après l'appel à hideMenu()", uiManager.isMenuVisible());
    }
    
    @Test
    public void testButtonInteraction() {
        // Créer un bouton pour le test
        Button button = new Button(100, 100, 200, 50, "Test Button");
        
        // Vérifier que le bouton n'est pas cliqué initialement
        assertFalse("Le bouton ne devrait pas être cliqué initialement", button.isClicked());
        
        // Simuler un clic sur le bouton
        float mouseX = 150;
        float mouseY = 125;
        boolean mousePressed = true;
        button.handleInput(mouseX, mouseY, mousePressed);
        
        // Vérifier que le bouton est cliqué
        assertTrue("Le bouton devrait être cliqué après interaction", button.isClicked());
        
        // Simuler le relâchement du bouton
        mousePressed = false;
        button.handleInput(mouseX, mouseY, mousePressed);
        
        // Vérifier que le bouton n'est plus cliqué
        assertFalse("Le bouton ne devrait plus être cliqué après relâchement", button.isClicked());
    }
    
    @Test
    public void testPanelInteraction() {
        // Créer un panneau pour le test
        Panel panel = new Panel(100, 100, 300, 200);
        
        // Ajouter un bouton au panneau
        Button button = new Button(50, 50, 100, 30, "Test Button");
        panel.addChild(button);
        
        // Vérifier que le bouton est bien ajouté au panneau
        assertEquals("Le panneau devrait contenir 1 enfant", 1, panel.getChildCount());
        
        // Simuler un clic sur le bouton dans le panneau
        float mouseX = 175; // 100 (position X du panneau) + 50 (position X relative du bouton) + 25 (centre du bouton)
        float mouseY = 165; // 100 (position Y du panneau) + 50 (position Y relative du bouton) + 15 (centre du bouton)
        boolean mousePressed = true;
        panel.handleInput(mouseX, mouseY, mousePressed);
        
        // Vérifier que le bouton est cliqué
        assertTrue("Le bouton dans le panneau devrait être cliqué après interaction", button.isClicked());
    }
}

package com.ryuukonpalace.game;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Classe principale pour exécuter tous les tests unitaires
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("===== Exécution des tests unitaires pour Ryuukon Palace =====");
        
        // Exécuter les tests du noyau du jeu
        runTestSuite(com.ryuukonpalace.game.core.GameManagerTest.class, "Tests du gestionnaire de jeu");
        runTestSuite(com.ryuukonpalace.game.core.RendererTest.class, "Tests du moteur de rendu");
        
        // Exécuter les tests du système de joueur
        runTestSuite(com.ryuukonpalace.game.player.PlayerTest.class, "Tests du joueur");
        
        // Exécuter les tests du système de signes mystiques
        runTestSuite(com.ryuukonpalace.game.mystical.MysticalSignsSystemTest.class, "Tests du système de signes mystiques");
        runTestSuite(com.ryuukonpalace.game.mystical.MysticalSignEffectsTest.class, "Tests des effets des signes mystiques");
        
        // Exécuter les tests de l'interface utilisateur
        runTestSuite(com.ryuukonpalace.game.ui.MysticalSignsInterfaceTest.class, "Tests de l'interface des signes mystiques");
        runTestSuite(com.ryuukonpalace.game.ui.UIManagerTest.class, "Tests du gestionnaire d'interface utilisateur");
        
        // Exécuter les tests du système audio
        runTestSuite(com.ryuukonpalace.game.audio.AudioManagerTest.class, "Tests du gestionnaire audio");
        
        // Exécuter les tests d'intégration
        runTestSuite(com.ryuukonpalace.game.mystical.MysticalSignsIntegrationTest.class, "Tests d'intégration des signes mystiques");
        
        System.out.println("===== Fin de l'exécution des tests =====");
    }
    
    /**
     * Exécuter une suite de tests
     * 
     * @param testClass Classe de test à exécuter
     * @param description Description de la suite de tests
     */
    private static void runTestSuite(Class<?> testClass, String description) {
        System.out.println("\n----- " + description + " -----");
        Result result = JUnitCore.runClasses(testClass);
        
        if (result.wasSuccessful()) {
            System.out.println("Tous les tests ont réussi (" + result.getRunCount() + " tests)");
        } else {
            System.out.println("Échec de " + result.getFailureCount() + " tests sur " + result.getRunCount());
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
}

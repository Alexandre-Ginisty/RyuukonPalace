package com.ryuukonpalace.game.demo;

import com.ryuukonpalace.game.core.GameManager;
import com.ryuukonpalace.game.core.Renderer;

/**
 * Lanceur pour le démonstrateur de Ryuukon Palace
 * Permet de tester le déplacement d'un personnage dans un environnement 2.5D
 */
public class DemoLauncher {
    
    /**
     * Point d'entrée principal
     */
    public static void main(String[] args) {
        System.out.println("Initialisation du démonstrateur Ryuukon Palace...");
        
        try {
            // Initialiser le gestionnaire de jeu
            GameManager gameManager = GameManager.getInstance();
            gameManager.init();
            
            // Initialiser le renderer
            Renderer renderer = Renderer.getInstance();
            renderer.init(800, 600);
            
            // Lancer la démo de personnage
            CharacterDemo demo = CharacterDemo.getInstance();
            demo.start();
            
            // Nettoyer les ressources
            gameManager.dispose();
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du démonstrateur : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Fin du démonstrateur Ryuukon Palace.");
    }
}

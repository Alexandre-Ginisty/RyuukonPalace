package com.ryuukonpalace.game.demo;

import com.ryuukonpalace.game.core.GameManager;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.utils.SpriteLoader;
import com.ryuukonpalace.game.world.TileSystem;

/**
 * Démonstrateur permettant de déplacer un personnage dans un espace 2.5D
 * Utilise les assets existants et le système de rendu optimisé
 */
public class CharacterDemo {
    
    // Singleton
    private static CharacterDemo instance;
    
    // Gestionnaires
    private GameManager gameManager;
    private Renderer renderer;
    private TileSystem tileSystem;
    private InputManager inputManager;
    
    // Personnage
    private float playerX = 400;
    private float playerY = 300;
    private float playerSpeed = 3.0f;
    private int playerDirection = 0; // 0: down, 1: left, 2: right, 3: up
    private int playerAnimFrame = 0;
    private int playerAnimTimer = 0;
    private boolean isMoving = false;
    
    // Sprites du personnage
    private int[] idleSprites = new int[4]; // down, left, right, up
    private int[][] walkSprites = new int[4][2]; // [direction][frame]
    
    // Carte
    private int[][] map;
    private int mapWidth = 20;
    private int mapHeight = 20;
    private int tileSize = 32;
    
    // Temps écoulé
    private float deltaTime = 0.016f; // 60 FPS par défaut
    
    // Constantes pour les touches (remplaçant les constantes GLFW)
    private static final int KEY_UP = 265;    // Équivalent à GLFW_KEY_UP
    private static final int KEY_DOWN = 264;  // Équivalent à GLFW_KEY_DOWN
    private static final int KEY_LEFT = 263;  // Équivalent à GLFW_KEY_LEFT
    private static final int KEY_RIGHT = 262; // Équivalent à GLFW_KEY_RIGHT
    private static final int KEY_W = 87;      // Équivalent à GLFW_KEY_W
    private static final int KEY_A = 65;      // Équivalent à GLFW_KEY_A
    private static final int KEY_S = 83;      // Équivalent à GLFW_KEY_S
    private static final int KEY_D = 68;      // Équivalent à GLFW_KEY_D
    
    /**
     * Constructeur privé (singleton)
     */
    private CharacterDemo() {
        // Initialisation des gestionnaires
        gameManager = GameManager.getInstance();
        renderer = Renderer.getInstance();
        tileSystem = TileSystem.getInstance();
        inputManager = InputManager.getInstance();
        
        // Création d'une carte simple
        initMap();
        
        // Chargement des sprites du personnage
        loadCharacterSprites();
    }
    
    /**
     * Obtenir l'instance unique
     */
    public static CharacterDemo getInstance() {
        if (instance == null) {
            instance = new CharacterDemo();
        }
        return instance;
    }
    
    /**
     * Initialiser la carte
     */
    private void initMap() {
        map = new int[mapHeight][mapWidth];
        
        // Remplir la carte avec de l'herbe (ID 1)
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                map[y][x] = 1;
            }
        }
        
        // Ajouter un chemin (ID 2)
        for (int x = 5; x < 15; x++) {
            map[10][x] = 2;
        }
        
        // Ajouter quelques arbres (ID 3)
        map[5][5] = 3;
        map[5][15] = 3;
        map[15][5] = 3;
        map[15][15] = 3;
        
        // Ajouter de l'eau (ID 4)
        for (int y = 16; y < 19; y++) {
            for (int x = 16; x < 19; x++) {
                map[y][x] = 4;
            }
        }
    }
    
    /**
     * Charger les sprites du personnage
     */
    private void loadCharacterSprites() {
        SpriteLoader spriteLoader = SpriteLoader.getInstance();
        
        // Sprites idle
        idleSprites[0] = spriteLoader.loadSprite("assets/textures/Nate_Idle_Down.bmp");
        idleSprites[1] = spriteLoader.loadSprite("assets/textures/Nate_Idle_Left.bmp");
        idleSprites[2] = spriteLoader.loadSprite("assets/textures/Nate_Idle_Right.bmp");
        idleSprites[3] = spriteLoader.loadSprite("assets/textures/Nate_Idle_Up.bmp");
        
        // Sprites de marche - Down
        walkSprites[0][0] = spriteLoader.loadSprite("assets/textures/Nate_Walk_Down_1.bmp");
        walkSprites[0][1] = spriteLoader.loadSprite("assets/textures/Nate_Walk_Down_2.bmp");
        
        // Sprites de marche - Left
        walkSprites[1][0] = spriteLoader.loadSprite("assets/textures/Nate_Walk_Left_1.bmp");
        walkSprites[1][1] = spriteLoader.loadSprite("assets/textures/Nate_Walk_Left_2.bmp");
        
        // Sprites de marche - Right
        walkSprites[2][0] = spriteLoader.loadSprite("assets/textures/Nate_Walk_Right_1.bmp");
        walkSprites[2][1] = spriteLoader.loadSprite("assets/textures/Nate_Walk_Right_2.bmp");
        
        // Sprites de marche - Up
        walkSprites[3][0] = spriteLoader.loadSprite("assets/textures/Nate_Walk_Up_1.bmp");
        walkSprites[3][1] = spriteLoader.loadSprite("assets/textures/Nate_Walk_Up_2.bmp");
    }
    
    /**
     * Mettre à jour l'état du jeu
     */
    public void update() {
        // Gestion des entrées
        handleInput();
        
        // Animation du personnage
        if (isMoving) {
            playerAnimTimer++;
            if (playerAnimTimer >= 10) {
                playerAnimTimer = 0;
                playerAnimFrame = (playerAnimFrame + 1) % 2;
            }
        }
    }
    
    /**
     * Gérer les entrées utilisateur
     */
    private void handleInput() {
        isMoving = false;
        
        // Mouvement vers le bas
        if (inputManager.isKeyPressed(KEY_DOWN) || inputManager.isKeyPressed(KEY_S)) {
            playerY += playerSpeed;
            playerDirection = 0;
            isMoving = true;
        }
        
        // Mouvement vers la gauche
        if (inputManager.isKeyPressed(KEY_LEFT) || inputManager.isKeyPressed(KEY_A)) {
            playerX -= playerSpeed;
            playerDirection = 1;
            isMoving = true;
        }
        
        // Mouvement vers la droite
        if (inputManager.isKeyPressed(KEY_RIGHT) || inputManager.isKeyPressed(KEY_D)) {
            playerX += playerSpeed;
            playerDirection = 2;
            isMoving = true;
        }
        
        // Mouvement vers le haut
        if (inputManager.isKeyPressed(KEY_UP) || inputManager.isKeyPressed(KEY_W)) {
            playerY -= playerSpeed;
            playerDirection = 3;
            isMoving = true;
        }
        
        // Limites de l'écran
        if (playerX < 0) playerX = 0;
        if (playerX > 800 - 32) playerX = 800 - 32;
        if (playerY < 0) playerY = 0;
        if (playerY > 600 - 32) playerY = 600 - 32;
    }
    
    /**
     * Dessiner la scène
     */
    public void render() {
        // Dessiner la carte
        renderMap();
        
        // Dessiner le personnage
        renderPlayer();
        
        // Dessiner l'interface utilisateur
        renderUI();
    }
    
    /**
     * Dessiner la carte
     */
    private void renderMap() {
        SpriteLoader spriteLoader = SpriteLoader.getInstance();
        
        // Sprites de la carte
        int grassSprite = spriteLoader.loadSprite("assets/textures/Grass_Tile_1.bmp");
        int pathSprite = spriteLoader.loadSprite("assets/textures/Path_Tile_M.bmp");
        int treeSprite = spriteLoader.loadSprite("assets/textures/Tree_1.bmp");
        int waterSprite = spriteLoader.loadSprite("assets/textures/Water_Tile_1.bmp");
        
        // Calculer les tuiles visibles
        int startX = Math.max(0, (int)(playerX / tileSize) - 15);
        int startY = Math.max(0, (int)(playerY / tileSize) - 10);
        int endX = Math.min(mapWidth, startX + 30);
        int endY = Math.min(mapHeight, startY + 20);
        
        // Dessiner les tuiles visibles
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                float drawX = x * tileSize;
                float drawY = y * tileSize;
                
                // Appliquer l'effet de perspective 2.5D
                float perspectiveY = drawY - (y * tileSystem.getIsometricOffsetY());
                
                // Dessiner la tuile en fonction de son type
                switch (map[y][x]) {
                    case 1: // Herbe
                        renderer.drawSprite(grassSprite, (int)drawX, (int)perspectiveY, tileSize, tileSize);
                        break;
                    case 2: // Chemin
                        renderer.drawSprite(pathSprite, (int)drawX, (int)perspectiveY, tileSize, tileSize);
                        break;
                    case 3: // Arbre
                        renderer.drawSprite(grassSprite, (int)drawX, (int)perspectiveY, tileSize, tileSize);
                        renderer.drawSprite(treeSprite, (int)drawX, (int)perspectiveY - 32, tileSize, tileSize * 2);
                        break;
                    case 4: // Eau
                        renderer.drawSprite(waterSprite, (int)drawX, (int)perspectiveY, tileSize, tileSize);
                        break;
                }
            }
        }
    }
    
    /**
     * Dessiner le personnage
     */
    private void renderPlayer() {
        // Choisir le sprite en fonction de l'état du personnage
        int spriteId;
        if (isMoving) {
            spriteId = walkSprites[playerDirection][playerAnimFrame];
        } else {
            spriteId = idleSprites[playerDirection];
        }
        
        // Appliquer l'effet de perspective 2.5D
        float perspectiveY = playerY - 16; // Ajustement pour la perspective
        
        // Dessiner le personnage
        renderer.drawSprite(spriteId, (int)playerX, (int)perspectiveY, 32, 48);
    }
    
    /**
     * Dessiner l'interface utilisateur
     */
    private void renderUI() {
        // Pour l'interface utilisateur, nous utilisons des méthodes de dessin de texte directement
        // Note: Dans un jeu réel, nous utiliserions un système d'UI complet
        System.out.println("Ryuukon Palace - Character Demo");
        System.out.println("Utilisez les flèches ou WASD pour déplacer le personnage");
        System.out.println("ESC pour quitter");
    }
    
    /**
     * Démarrer la démo
     */
    public void start() {
        System.out.println("Démarrage de la démo de personnage...");
        
        // Boucle principale
        while (!gameManager.isGameLoopRunning()) {
            // Effacer l'écran
            renderer.beginRender();
            
            // Mettre à jour l'état du jeu
            update();
            
            // Dessiner la scène
            render();
            
            // Terminer le rendu
            renderer.endRender();
            
            // Mettre à jour l'affichage
            gameManager.update(deltaTime);
        }
        
        System.out.println("Fin de la démo de personnage.");
    }
    
    /**
     * Obtenir le décalage isométrique Y du système de tuiles
     * @return Décalage isométrique Y
     */
    public float getIsometricOffsetY() {
        return tileSystem.getIsometricOffsetY();
    }
}

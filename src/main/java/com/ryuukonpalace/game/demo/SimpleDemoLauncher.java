package com.ryuukonpalace.game.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Version simplifiée du démonstrateur qui utilise Swing au lieu de LWJGL
 * pour éviter les problèmes de dépendances.
 */
public class SimpleDemoLauncher {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ryuukon Palace - Démonstrateur");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel, BorderLayout.CENTER);
            
            frame.setVisible(true);
            gamePanel.requestFocus();
        });
    }
    
    /**
     * Panneau de jeu qui gère le rendu et les entrées
     */
    static class GamePanel extends JPanel {
        
        // Personnage
        private int playerX = 400;
        private int playerY = 300;
        private int playerSpeed = 5;
        private int playerDirection = 0; // 0: down, 1: left, 2: right, 3: up
        private int playerAnimFrame = 0;
        private int animCounter = 0;
        private boolean isMoving = false;
        
        // Sprites
        private BufferedImage[] playerSprites = new BufferedImage[8]; // 4 directions x 2 frames
        private BufferedImage grassTile;
        private BufferedImage pathTile;
        private BufferedImage waterTile;
        private BufferedImage treeTile;
        private BufferedImage rockTile;
        private BufferedImage voidTile;
        
        // Carte
        private int[][] map;
        private int[][] collisionMap; // 0 = passable, 1 = collision
        private int mapWidth = 40;
        private int mapHeight = 40;
        private int tileSize = 32;
        
        // Caméra
        private int cameraX = 0;
        private int cameraY = 0;
        
        // Touches pressées
        private boolean upPressed = false;
        private boolean downPressed = false;
        private boolean leftPressed = false;
        private boolean rightPressed = false;
        
        /**
         * Constructeur
         */
        public GamePanel() {
            setPreferredSize(new Dimension(800, 600));
            setBackground(Color.BLACK);
            setFocusable(true);
            
            // Charger les sprites ou créer des sprites par défaut
            loadSprites();
            
            // Créer la carte
            createMap();
            
            // Ajouter l'écouteur de clavier
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_Z: // ZQSD pour clavier français
                            upPressed = true;
                            break;
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_S:
                            downPressed = true;
                            break;
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_Q: // ZQSD pour clavier français
                            leftPressed = true;
                            break;
                        case KeyEvent.VK_RIGHT:
                        case KeyEvent.VK_D:
                            rightPressed = true;
                            break;
                        case KeyEvent.VK_ESCAPE:
                            System.exit(0);
                            break;
                    }
                }
                
                @Override
                public void keyReleased(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_Z: // ZQSD pour clavier français
                            upPressed = false;
                            break;
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_S:
                            downPressed = false;
                            break;
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_Q: // ZQSD pour clavier français
                            leftPressed = false;
                            break;
                        case KeyEvent.VK_RIGHT:
                        case KeyEvent.VK_D:
                            rightPressed = false;
                            break;
                    }
                }
            });
            
            // Démarrer la boucle de jeu
            new Thread(() -> {
                while (true) {
                    update();
                    repaint();
                    try {
                        Thread.sleep(16); // ~60 FPS
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        
        /**
         * Charger les sprites
         */
        private void loadSprites() {
            try {
                // Essayer de charger les sprites depuis les fichiers
                File resourceDir = new File("src/main/resources/assets/textures");
                File infernusDir = new File("src/main/resources/Infernus");
                
                // Vérifier si les dossiers existent
                if (!resourceDir.exists() && !infernusDir.exists()) {
                    System.out.println("Dossiers de ressources non trouvés. Utilisation de sprites par défaut.");
                    createDefaultSprites();
                    return;
                }
                
                // Essayer d'abord les sprites d'Infernus
                if (infernusDir.exists()) {
                    System.out.println("Chargement des sprites d'Infernus...");
                    
                    // Chercher des sprites de personnage
                    File[] characterFiles = new File(infernusDir, "characters").listFiles();
                    if (characterFiles != null && characterFiles.length > 0) {
                        // Utiliser le premier personnage trouvé
                        File characterDir = characterFiles[0];
                        System.out.println("Utilisation du personnage: " + characterDir.getName());
                        
                        // Chercher des sprites de direction
                        File[] directionFiles = characterDir.listFiles();
                        if (directionFiles != null && directionFiles.length > 0) {
                            // Créer des sprites par défaut d'abord
                            createDefaultSprites();
                            
                            // Remplacer par les sprites trouvés
                            for (File dirFile : directionFiles) {
                                String name = dirFile.getName().toLowerCase();
                                BufferedImage img = ImageIO.read(dirFile);
                                
                                if (name.contains("down")) {
                                    playerSprites[0] = img;
                                    playerSprites[1] = img;
                                } else if (name.contains("left")) {
                                    playerSprites[2] = img;
                                    playerSprites[3] = img;
                                } else if (name.contains("right")) {
                                    playerSprites[4] = img;
                                    playerSprites[5] = img;
                                } else if (name.contains("up")) {
                                    playerSprites[6] = img;
                                    playerSprites[7] = img;
                                }
                            }
                        }
                    }
                    
                    // Chercher des tuiles
                    File tilesDir = new File(infernusDir, "tiles");
                    if (tilesDir.exists()) {
                        File[] tileFiles = tilesDir.listFiles();
                        if (tileFiles != null) {
                            for (File tileFile : tileFiles) {
                                String name = tileFile.getName().toLowerCase();
                                BufferedImage img = ImageIO.read(tileFile);
                                
                                if (name.contains("grass")) {
                                    grassTile = img;
                                } else if (name.contains("path") || name.contains("road")) {
                                    pathTile = img;
                                } else if (name.contains("water")) {
                                    waterTile = img;
                                } else if (name.contains("tree")) {
                                    treeTile = img;
                                } else if (name.contains("rock") || name.contains("stone")) {
                                    rockTile = img;
                                }
                            }
                        }
                    }
                }
                
                // Si on n'a pas trouvé tous les sprites, essayer le dossier assets
                if (resourceDir.exists()) {
                    // Charger les sprites du joueur si nécessaire
                    if (playerSprites[0] == null) {
                        File downSprite = new File(resourceDir, "Nate_Idle_Down.bmp");
                        File leftSprite = new File(resourceDir, "Nate_Idle_Left.bmp");
                        File rightSprite = new File(resourceDir, "Nate_Idle_Right.bmp");
                        File upSprite = new File(resourceDir, "Nate_Idle_Up.bmp");
                        
                        File downWalk1 = new File(resourceDir, "Nate_Walk_Down_1.bmp");
                        File leftWalk1 = new File(resourceDir, "Nate_Walk_Left_1.bmp");
                        File rightWalk1 = new File(resourceDir, "Nate_Walk_Right_1.bmp");
                        File upWalk1 = new File(resourceDir, "Nate_Walk_Up_1.bmp");
                        
                        if (downSprite.exists()) playerSprites[0] = ImageIO.read(downSprite);
                        if (downWalk1.exists()) playerSprites[1] = ImageIO.read(downWalk1);
                        if (leftSprite.exists()) playerSprites[2] = ImageIO.read(leftSprite);
                        if (leftWalk1.exists()) playerSprites[3] = ImageIO.read(leftWalk1);
                        if (rightSprite.exists()) playerSprites[4] = ImageIO.read(rightSprite);
                        if (rightWalk1.exists()) playerSprites[5] = ImageIO.read(rightWalk1);
                        if (upSprite.exists()) playerSprites[6] = ImageIO.read(upSprite);
                        if (upWalk1.exists()) playerSprites[7] = ImageIO.read(upWalk1);
                    }
                    
                    // Charger les tuiles si nécessaire
                    if (grassTile == null) {
                        File grassFile = new File(resourceDir, "Grass_Tile_1.bmp");
                        if (grassFile.exists()) grassTile = ImageIO.read(grassFile);
                    }
                    
                    if (pathTile == null) {
                        File pathFile = new File(resourceDir, "Path_Tile_M.bmp");
                        if (pathFile.exists()) pathTile = ImageIO.read(pathFile);
                    }
                    
                    if (waterTile == null) {
                        File waterFile = new File(resourceDir, "Water_Tile_1.bmp");
                        if (waterFile.exists()) waterTile = ImageIO.read(waterFile);
                    }
                    
                    if (treeTile == null) {
                        File treeFile = new File(resourceDir, "Tree_1.bmp");
                        if (treeFile.exists()) treeTile = ImageIO.read(treeFile);
                    }
                }
                
                // Créer des tuiles par défaut pour celles qui n'ont pas été trouvées
                if (grassTile == null) grassTile = createColoredTile(new Color(34, 139, 34)); // Vert forêt
                if (pathTile == null) pathTile = createColoredTile(new Color(210, 180, 140)); // Beige
                if (waterTile == null) waterTile = createColoredTile(new Color(30, 144, 255)); // Bleu
                if (rockTile == null) rockTile = createColoredTile(new Color(128, 128, 128)); // Gris
                if (voidTile == null) voidTile = createColoredTile(new Color(0, 0, 0)); // Noir
                
                if (treeTile == null) {
                    treeTile = new BufferedImage(tileSize, tileSize * 2, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = treeTile.createGraphics();
                    g.setColor(new Color(139, 69, 19)); // Marron
                    g.fillRect(14, 32, 4, 32); // Tronc
                    g.setColor(new Color(0, 100, 0)); // Vert foncé
                    g.fillOval(6, 8, 20, 30); // Feuillage
                    g.dispose();
                }
                
                // Vérifier si tous les sprites du joueur sont chargés
                boolean allPlayerSpritesLoaded = true;
                for (BufferedImage sprite : playerSprites) {
                    if (sprite == null) {
                        allPlayerSpritesLoaded = false;
                        break;
                    }
                }
                
                if (!allPlayerSpritesLoaded) {
                    System.out.println("Certains sprites du joueur n'ont pas été trouvés. Utilisation de sprites par défaut.");
                    createDefaultSprites();
                } else {
                    System.out.println("Sprites chargés avec succès !");
                }
                
            } catch (IOException e) {
                System.out.println("Erreur lors du chargement des sprites : " + e.getMessage());
                createDefaultSprites();
            }
        }
        
        /**
         * Créer des sprites par défaut
         */
        private void createDefaultSprites() {
            // Créer des sprites de joueur par défaut
            for (int i = 0; i < 8; i++) {
                playerSprites[i] = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = playerSprites[i].createGraphics();
                
                // Corps
                g.setColor(Color.BLUE);
                g.fillRect(10, 8, 12, 20);
                
                // Tête
                g.setColor(new Color(255, 200, 150)); // Couleur chair
                g.fillOval(10, 0, 12, 12);
                
                // Direction (yeux)
                g.setColor(Color.BLACK);
                switch (i / 2) {
                    case 0: // Down
                        g.fillRect(13, 4, 2, 2);
                        g.fillRect(17, 4, 2, 2);
                        break;
                    case 1: // Left
                        g.fillRect(11, 4, 2, 2);
                        break;
                    case 2: // Right
                        g.fillRect(19, 4, 2, 2);
                        break;
                    case 3: // Up
                        g.fillRect(13, 2, 2, 2);
                        g.fillRect(17, 2, 2, 2);
                        break;
                }
                
                // Animation de marche (jambes)
                if (i % 2 == 1) {
                    g.setColor(Color.BLUE);
                    g.fillRect(10, 28, 5, 4);
                    g.fillRect(17, 28, 5, 4);
                }
                
                g.dispose();
            }
            
            // Créer des tuiles par défaut
            grassTile = createColoredTile(new Color(34, 139, 34)); // Vert forêt
            pathTile = createColoredTile(new Color(210, 180, 140)); // Beige
            waterTile = createColoredTile(new Color(30, 144, 255)); // Bleu
            rockTile = createColoredTile(new Color(128, 128, 128)); // Gris
            voidTile = createColoredTile(new Color(0, 0, 0)); // Noir
            
            treeTile = new BufferedImage(tileSize, tileSize * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = treeTile.createGraphics();
            g.setColor(new Color(139, 69, 19)); // Marron
            g.fillRect(14, 32, 4, 32); // Tronc
            g.setColor(new Color(0, 100, 0)); // Vert foncé
            g.fillOval(6, 8, 20, 30); // Feuillage
            g.dispose();
        }
        
        /**
         * Créer une tuile de couleur unie
         */
        private BufferedImage createColoredTile(Color color) {
            BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = tile.createGraphics();
            g.setColor(color);
            g.fillRect(0, 0, tileSize, tileSize);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, tileSize - 1, tileSize - 1);
            g.dispose();
            return tile;
        }
        
        /**
         * Créer la carte
         */
        private void createMap() {
            map = new int[mapHeight][mapWidth];
            collisionMap = new int[mapHeight][mapWidth];
            
            // Remplir la carte avec du vide (ID 0)
            for (int y = 0; y < mapHeight; y++) {
                for (int x = 0; x < mapWidth; x++) {
                    map[y][x] = 0;
                    collisionMap[y][x] = 1; // Par défaut, tout est solide
                }
            }
            
            // Créer une île centrale avec de l'herbe (ID 1)
            int centerX = mapWidth / 2;
            int centerY = mapHeight / 2;
            int radius = 15;
            
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    if (y >= 0 && y < mapHeight && x >= 0 && x < mapWidth) {
                        // Distance au centre
                        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                        
                        if (distance <= radius) {
                            map[y][x] = 1; // Herbe
                            collisionMap[y][x] = 0; // Passable
                        } else if (distance <= radius + 1) {
                            map[y][x] = 4; // Eau (bordure)
                            collisionMap[y][x] = 1; // Solide
                        }
                    }
                }
            }
            
            // Ajouter un chemin (ID 2)
            for (int x = centerX - 10; x <= centerX + 10; x++) {
                if (x >= 0 && x < mapWidth) {
                    map[centerY][x] = 2;
                    collisionMap[centerY][x] = 0; // Passable
                }
            }
            
            // Ajouter quelques arbres (ID 3) avec collision
            addObjectWithCollision(centerX - 8, centerY - 8, 3);
            addObjectWithCollision(centerX + 8, centerY - 8, 3);
            addObjectWithCollision(centerX - 8, centerY + 8, 3);
            addObjectWithCollision(centerX + 8, centerY + 8, 3);
            
            // Ajouter des rochers (ID 5) avec collision
            addObjectWithCollision(centerX - 5, centerY - 5, 5);
            addObjectWithCollision(centerX + 5, centerY - 5, 5);
            addObjectWithCollision(centerX - 5, centerY + 5, 5);
            addObjectWithCollision(centerX + 5, centerY + 5, 5);
            
            // Positionner le joueur au centre
            playerX = centerX * tileSize;
            playerY = centerY * tileSize;
        }
        
        /**
         * Ajouter un objet avec collision
         */
        private void addObjectWithCollision(int x, int y, int objectId) {
            if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
                map[y][x] = objectId;
                collisionMap[y][x] = 1; // Solide
            }
        }
        
        /**
         * Vérifier s'il y a une collision à une position donnée
         */
        private boolean checkCollision(int x, int y) {
            // Convertir les coordonnées en pixels en indices de tuile
            int tileX = x / tileSize;
            int tileY = y / tileSize;
            
            // Vérifier les limites de la carte
            if (tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapHeight) {
                return true; // Collision avec les bords de la carte
            }
            
            // Vérifier la collision avec la tuile
            return collisionMap[tileY][tileX] == 1;
        }
        
        /**
         * Mettre à jour l'état du jeu
         */
        private void update() {
            // Réinitialiser le mouvement
            isMoving = false;
            
            // Sauvegarder la position actuelle pour vérifier les collisions
            int newPlayerX = playerX;
            int newPlayerY = playerY;
            
            // Gérer les entrées
            if (upPressed) {
                newPlayerY -= playerSpeed;
                playerDirection = 3;
                isMoving = true;
            }
            if (downPressed) {
                newPlayerY += playerSpeed;
                playerDirection = 0;
                isMoving = true;
            }
            if (leftPressed) {
                newPlayerX -= playerSpeed;
                playerDirection = 1;
                isMoving = true;
            }
            if (rightPressed) {
                newPlayerX += playerSpeed;
                playerDirection = 2;
                isMoving = true;
            }
            
            // Vérifier les collisions pour les mouvements horizontaux
            if (!checkCollision(newPlayerX, playerY) && 
                !checkCollision(newPlayerX + tileSize - 1, playerY) &&
                !checkCollision(newPlayerX, playerY + tileSize - 1) &&
                !checkCollision(newPlayerX + tileSize - 1, playerY + tileSize - 1)) {
                playerX = newPlayerX;
            }
            
            // Vérifier les collisions pour les mouvements verticaux
            if (!checkCollision(playerX, newPlayerY) && 
                !checkCollision(playerX + tileSize - 1, newPlayerY) &&
                !checkCollision(playerX, newPlayerY + tileSize - 1) &&
                !checkCollision(playerX + tileSize - 1, newPlayerY + tileSize - 1)) {
                playerY = newPlayerY;
            }
            
            // Mettre à jour la caméra pour suivre le joueur
            cameraX = playerX - getWidth() / 2 + tileSize / 2;
            cameraY = playerY - getHeight() / 2 + tileSize / 2;
            
            // Limiter la caméra aux bords de la carte
            cameraX = Math.max(0, Math.min(cameraX, mapWidth * tileSize - getWidth()));
            cameraY = Math.max(0, Math.min(cameraY, mapHeight * tileSize - getHeight()));
            
            // Animation
            if (isMoving) {
                animCounter++;
                if (animCounter >= 10) {
                    animCounter = 0;
                    playerAnimFrame = (playerAnimFrame + 1) % 2;
                }
            } else {
                playerAnimFrame = 0;
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Calculer les tuiles visibles
            int startX = Math.max(0, cameraX / tileSize);
            int startY = Math.max(0, cameraY / tileSize);
            int endX = Math.min(mapWidth, startX + getWidth() / tileSize + 2);
            int endY = Math.min(mapHeight, startY + getHeight() / tileSize + 2);
            
            // Dessiner la carte
            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    int drawX = x * tileSize - cameraX;
                    int drawY = y * tileSize - cameraY;
                    
                    // Appliquer l'effet de perspective 2.5D
                    int perspectiveY = drawY - (y * 8); // Décalage pour la perspective
                    
                    // Dessiner la tuile en fonction de son type
                    switch (map[y][x]) {
                        case 0: // Vide
                            g.drawImage(voidTile, drawX, perspectiveY, null);
                            break;
                        case 1: // Herbe
                            g.drawImage(grassTile, drawX, perspectiveY, null);
                            break;
                        case 2: // Chemin
                            g.drawImage(pathTile, drawX, perspectiveY, null);
                            break;
                        case 3: // Arbre
                            g.drawImage(grassTile, drawX, perspectiveY, null);
                            g.drawImage(treeTile, drawX, perspectiveY - tileSize, null);
                            break;
                        case 4: // Eau
                            g.drawImage(waterTile, drawX, perspectiveY, null);
                            break;
                        case 5: // Rocher
                            g.drawImage(grassTile, drawX, perspectiveY, null);
                            g.drawImage(rockTile, drawX, perspectiveY, null);
                            break;
                    }
                    
                    // Afficher les collisions en mode debug (commenté par défaut)
                    /*if (collisionMap[y][x] == 1) {
                        g.setColor(new Color(255, 0, 0, 100));
                        g.fillRect(drawX, perspectiveY, tileSize, tileSize);
                    }*/
                }
            }
            
            // Dessiner le joueur
            int spriteIndex = playerDirection * 2 + playerAnimFrame;
            int drawX = playerX - cameraX;
            int drawY = playerY - cameraY - 8; // -8 pour l'ajustement de perspective
            g.drawImage(playerSprites[spriteIndex], drawX, drawY, null);
            
            // Dessiner les instructions
            g.setColor(Color.WHITE);
            g.drawString("Ryuukon Palace - Démonstrateur", 20, 20);
            g.drawString("Utilisez les flèches ou ZQSD pour déplacer le personnage", 20, getHeight() - 40);
            g.drawString("ESC pour quitter", 20, getHeight() - 20);
        }
    }
}

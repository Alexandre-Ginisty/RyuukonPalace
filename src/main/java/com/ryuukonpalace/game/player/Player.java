package com.ryuukonpalace.game.player;

import com.ryuukonpalace.game.core.Camera;
import com.ryuukonpalace.game.world.GameObject;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.core.physics.RectangleCollider;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.utils.ResourceManager;
import com.ryuukonpalace.game.world.SpawnZone;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Représente le joueur dans le jeu.
 * Gère le déplacement, les collisions et l'interaction avec les créatures.
 */
public class Player extends GameObject {
    
    // Vitesse de déplacement
    private float moveSpeed;
    
    // Direction actuelle
    private Direction direction;
    
    // État de mouvement
    private boolean isMoving;
    
    // Compteur de pas
    private float stepCounter;
    
    // Distance entre deux pas
    private final float stepDistance = 0.5f;
    
    // Inventaire des créatures capturées
    private List<Creature> capturedCreatures;
    
    // Inventaire des objets
    private Inventory inventory;
    
    // Créature actuellement en combat
    private Creature currentBattleCreature;
    
    // Zone d'apparition actuelle
    private SpawnZone currentSpawnZone;
    
    // ID de la texture du joueur
    private int textureId;
    
    // Gestionnaire d'entrées
    private InputManager inputManager;
    
    // Renderer
    private Renderer renderer;
    
    // Callback pour le démarrage d'un combat
    private CombatStartCallback combatStartCallback;
    
    /**
     * Constructeur pour le joueur
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public Player(float x, float y) {
        super(x, y, 32, 48);
        this.moveSpeed = 150.0f; // Pixels par seconde
        this.direction = Direction.DOWN;
        this.isMoving = false;
        this.stepCounter = 0.0f;
        this.capturedCreatures = new ArrayList<>();
        this.inventory = new Inventory();
        this.currentBattleCreature = null;
        this.currentSpawnZone = null;
        
        // Créer un collider pour le joueur
        this.collider = new RectangleCollider(x, y, 32, 48);
        
        // Obtenir les instances des gestionnaires
        this.inputManager = InputManager.getInstance();
        this.renderer = Renderer.getInstance();
        
        // Charger la texture du joueur
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.textureId = resourceManager.getTextureId("player");
    }
    
    @Override
    public void update(float deltaTime) {
        // Ne pas mettre à jour si en combat
        if (currentBattleCreature != null) {
            return;
        }
        
        // Position précédente pour détecter les mouvements
        float oldX = x;
        float oldY = y;
        
        // Gérer les entrées de déplacement
        handleMovementInput(deltaTime);
        
        // Mettre à jour le compteur de pas si le joueur se déplace
        if (isMoving) {
            float distanceMoved = (float) Math.sqrt(Math.pow(x - oldX, 2) + Math.pow(y - oldY, 2));
            stepCounter += distanceMoved;
            
            // Vérifier si un pas complet a été effectué
            if (stepCounter >= stepDistance) {
                stepCounter -= stepDistance;
                onStepTaken();
            }
        }
        
        // Mettre à jour la position du collider
        if (collider != null) {
            collider.setPosition(x, y);
        }
        
        // Faire suivre la caméra
        Camera.getInstance().follow(this);
    }
    
    @Override
    public void render() {
        // Dessiner le joueur avec la texture appropriée selon la direction
        int directionOffset = 0;
        
        // Déterminer l'offset de texture en fonction de la direction
        switch (direction) {
            case DOWN:
                directionOffset = 0;
                break;
            case LEFT:
                directionOffset = 1;
                break;
            case RIGHT:
                directionOffset = 2;
                break;
            case UP:
                directionOffset = 3;
                break;
        }
        
        // Dessiner le joueur avec la texture correspondant à sa direction
        // Note: Ceci suppose que les textures du joueur sont organisées en une seule image
        // avec différentes directions (comme une spritesheet)
        renderer.drawSprite(textureId, x, y, width, height, directionOffset);
    }
    
    @Override
    public void onCollision(GameObject other) {
        // Gérer les collisions avec les zones d'apparition
        if (other instanceof SpawnZone) {
            SpawnZone spawnZone = (SpawnZone) other;
            
            // Si c'est une nouvelle zone d'apparition
            if (currentSpawnZone != spawnZone) {
                currentSpawnZone = spawnZone;
                System.out.println("Entré dans une zone d'apparition de type: " + spawnZone.getType());
            }
        }
    }
    
    /**
     * Gérer les entrées de déplacement
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    private void handleMovementInput(float deltaTime) {
        float moveX = 0.0f;
        float moveY = 0.0f;
        
        // Déplacement horizontal
        if (inputManager.isKeyPressed(GLFW_KEY_LEFT) || inputManager.isKeyPressed(GLFW_KEY_A)) {
            moveX -= moveSpeed * deltaTime;
            direction = Direction.LEFT;
        } else if (inputManager.isKeyPressed(GLFW_KEY_RIGHT) || inputManager.isKeyPressed(GLFW_KEY_D)) {
            moveX += moveSpeed * deltaTime;
            direction = Direction.RIGHT;
        }
        
        // Déplacement vertical
        if (inputManager.isKeyPressed(GLFW_KEY_UP) || inputManager.isKeyPressed(GLFW_KEY_W)) {
            moveY -= moveSpeed * deltaTime;
            direction = Direction.UP;
        } else if (inputManager.isKeyPressed(GLFW_KEY_DOWN) || inputManager.isKeyPressed(GLFW_KEY_S)) {
            moveY += moveSpeed * deltaTime;
            direction = Direction.DOWN;
        }
        
        // Mettre à jour la position
        x += moveX;
        y += moveY;
        
        // Mettre à jour l'état de mouvement
        isMoving = moveX != 0.0f || moveY != 0.0f;
    }
    
    /**
     * Appelé lorsqu'un pas complet est effectué
     */
    private void onStepTaken() {
        // Si le joueur est dans une zone d'apparition, vérifier si une créature apparaît
        if (currentSpawnZone != null) {
            Creature spawnedCreature = currentSpawnZone.incrementSteps();
            if (spawnedCreature != null) {
                startBattle(spawnedCreature);
            }
        }
    }
    
    /**
     * Démarrer un combat avec une créature
     * 
     * @param creature La créature à combattre
     */
    public void startBattle(Creature creature) {
        currentBattleCreature = creature;
        System.out.println("Combat démarré contre " + creature.getName() + " (Niveau " + creature.getLevel() + ")");
        
        // Si la créature était visible, la désactiver pendant le combat
        if (currentSpawnZone != null && currentSpawnZone.hasVisibleCreature()) {
            currentSpawnZone.setVisibleCreatureActive(false);
        }
        
        // Notifier le callback pour démarrer le combat
        if (combatStartCallback != null) {
            combatStartCallback.onCombatStart(this, creature);
        }
    }
    
    /**
     * Terminer le combat actuel
     * 
     * @param captured true si la créature a été capturée, false sinon
     */
    public void endBattle(boolean captured) {
        if (currentBattleCreature != null) {
            if (captured) {
                capturedCreatures.add(currentBattleCreature);
                System.out.println(currentBattleCreature.getName() + " a été capturé !");
            } else {
                System.out.println(currentBattleCreature.getName() + " s'est échappé !");
            }
            
            currentBattleCreature = null;
            
            // Si la créature était visible, elle reste désactivée après la capture
            // Sinon, elle pourrait être réactivée plus tard
        }
    }
    
    /**
     * Définir le callback pour le démarrage d'un combat
     * 
     * @param callback Callback à appeler lorsqu'un combat démarre
     */
    public void setCombatStartCallback(CombatStartCallback callback) {
        this.combatStartCallback = callback;
    }
    
    /**
     * Vérifier si le joueur est en combat
     * 
     * @return true si le joueur est en combat, false sinon
     */
    public boolean isInBattle() {
        return currentBattleCreature != null;
    }
    
    /**
     * Obtenir la créature actuellement en combat
     * 
     * @return La créature en combat, ou null si pas de combat
     */
    public Creature getCurrentBattleCreature() {
        return currentBattleCreature;
    }
    
    /**
     * Obtenir l'inventaire du joueur
     * 
     * @return L'inventaire du joueur
     */
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Obtenir la liste des créatures capturées
     * 
     * @return Liste des créatures capturées
     */
    public List<Creature> getCapturedCreatures() {
        // Combiner les créatures de l'inventaire et de l'ancienne liste
        List<Creature> allCreatures = new ArrayList<>(capturedCreatures);
        allCreatures.addAll(inventory.getCapturedCreatures());
        return allCreatures;
    }
    
    /**
     * Directions possibles pour le joueur
     */
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    
    /**
     * Interface pour le callback de démarrage de combat
     */
    public interface CombatStartCallback {
        void onCombatStart(Player player, Creature creature);
    }
}

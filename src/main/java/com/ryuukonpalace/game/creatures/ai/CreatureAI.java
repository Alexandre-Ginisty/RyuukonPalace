package com.ryuukonpalace.game.creatures.ai;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.player.Player;

import java.util.Random;

/**
 * Gère l'intelligence artificielle des créatures sauvages.
 * Permet aux créatures de se déplacer, de fuir ou d'affronter le joueur.
 */
public class CreatureAI {
    
    // Types de comportement possibles pour les créatures
    public enum BehaviorType {
        IDLE,           // La créature reste immobile
        RANDOM_MOVE,    // La créature se déplace aléatoirement
        FLEE,           // La créature fuit le joueur
        AGGRESSIVE      // La créature se dirige vers le joueur pour l'affronter
    }
    
    // Créature contrôlée par cette IA
    private Creature creature;
    
    // Type de comportement actuel
    private BehaviorType currentBehavior;
    
    // Temps écoulé dans l'état actuel
    private float stateTimer;
    
    // Durée maximale d'un état
    private float maxStateTime;
    
    // Direction actuelle de déplacement (en radians)
    private float moveDirection;
    
    // Vitesse de déplacement
    private float moveSpeed;
    
    // Distance à laquelle la créature détecte le joueur
    private float detectionRange;
    
    // Distance à laquelle la créature commence à fuir le joueur
    private float fleeRange;
    
    // Distance à laquelle la créature attaque le joueur
    private float attackRange;
    
    // Générateur de nombres aléatoires
    private Random random;
    
    /**
     * Constructeur pour l'IA d'une créature
     * 
     * @param creature La créature à contrôler
     * @param behaviorType Le type de comportement initial
     * @param moveSpeed La vitesse de déplacement
     * @param detectionRange La distance de détection du joueur
     */
    public CreatureAI(Creature creature, BehaviorType behaviorType, float moveSpeed, float detectionRange) {
        this.creature = creature;
        this.currentBehavior = behaviorType;
        this.stateTimer = 0;
        this.maxStateTime = 3.0f; // 3 secondes par défaut
        this.moveDirection = 0;
        this.moveSpeed = moveSpeed;
        this.detectionRange = detectionRange;
        this.fleeRange = detectionRange * 0.8f;
        this.attackRange = detectionRange * 0.5f;
        this.random = new Random();
        
        // Initialiser une direction aléatoire
        this.moveDirection = random.nextFloat() * (float) (2 * Math.PI);
    }
    
    /**
     * Mettre à jour l'IA de la créature
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param player Le joueur (pour la détection)
     */
    public void update(float deltaTime, Player player) {
        // Incrémenter le timer d'état
        stateTimer += deltaTime;
        
        // Vérifier si nous devons changer d'état
        if (stateTimer >= maxStateTime) {
            // Réinitialiser le timer
            stateTimer = 0;
            
            // Choisir un nouveau comportement si le comportement actuel est aléatoire
            if (currentBehavior == BehaviorType.RANDOM_MOVE || currentBehavior == BehaviorType.IDLE) {
                // 70% de chance de continuer à se déplacer, 30% de rester immobile
                if (random.nextFloat() < 0.7f) {
                    currentBehavior = BehaviorType.RANDOM_MOVE;
                    // Nouvelle direction aléatoire
                    moveDirection = random.nextFloat() * (float) (2 * Math.PI);
                } else {
                    currentBehavior = BehaviorType.IDLE;
                }
                
                // Durée aléatoire pour le prochain état (1 à 4 secondes)
                maxStateTime = 1.0f + random.nextFloat() * 3.0f;
            }
        }
        
        // Calculer la distance au joueur
        float dx = player.getX() - creature.getX();
        float dy = player.getY() - creature.getY();
        float distanceToPlayer = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Détecter le joueur et ajuster le comportement si nécessaire
        if (distanceToPlayer <= detectionRange) {
            // Calculer l'angle vers le joueur
            float angleToPlayer = (float) Math.atan2(dy, dx);
            
            // Déterminer le comportement en fonction de la personnalité de la créature et de la distance
            if (currentBehavior == BehaviorType.AGGRESSIVE && distanceToPlayer <= attackRange) {
                // La créature est agressive et le joueur est à portée d'attaque
                // Déclencher un combat (géré par le système de jeu)
                // Pour l'instant, on se contente de s'approcher du joueur
                moveDirection = angleToPlayer;
            } else if (currentBehavior == BehaviorType.FLEE || distanceToPlayer <= fleeRange) {
                // La créature fuit le joueur
                // Direction opposée au joueur
                moveDirection = angleToPlayer + (float) Math.PI;
                currentBehavior = BehaviorType.FLEE;
                // Augmenter la vitesse de fuite
                float fleeSpeed = moveSpeed * 1.5f;
                
                // Déplacer la créature dans la direction de fuite
                float moveX = (float) Math.cos(moveDirection) * fleeSpeed * deltaTime;
                float moveY = (float) Math.sin(moveDirection) * fleeSpeed * deltaTime;
                creature.setPosition(creature.getX() + moveX, creature.getY() + moveY);
                
                // Réinitialiser le timer pour continuer à fuir
                stateTimer = 0;
                maxStateTime = 2.0f;
            } else if (currentBehavior == BehaviorType.AGGRESSIVE) {
                // La créature est agressive mais le joueur n'est pas à portée d'attaque
                // Se diriger vers le joueur
                moveDirection = angleToPlayer;
                
                // Déplacer la créature vers le joueur
                float moveX = (float) Math.cos(moveDirection) * moveSpeed * deltaTime;
                float moveY = (float) Math.sin(moveDirection) * moveSpeed * deltaTime;
                creature.setPosition(creature.getX() + moveX, creature.getY() + moveY);
                
                // Réinitialiser le timer pour continuer à poursuivre
                stateTimer = 0;
                maxStateTime = 2.0f;
            }
        }
        
        // Appliquer le comportement actuel
        switch (currentBehavior) {
            case RANDOM_MOVE:
                // Déplacer la créature dans la direction actuelle
                float moveX = (float) Math.cos(moveDirection) * moveSpeed * deltaTime;
                float moveY = (float) Math.sin(moveDirection) * moveSpeed * deltaTime;
                creature.setPosition(creature.getX() + moveX, creature.getY() + moveY);
                break;
                
            case IDLE:
                // La créature reste immobile
                break;
                
            case FLEE:
                // Ce cas est déjà géré dans la détection du joueur
                // Mais on l'ajoute ici pour éviter l'avertissement de compilation
                break;
                
            case AGGRESSIVE:
                // Ce cas est déjà géré dans la détection du joueur
                // Mais on l'ajoute ici pour éviter l'avertissement de compilation
                break;
        }
    }
    
    /**
     * Définir le type de comportement de la créature
     * 
     * @param behaviorType Nouveau type de comportement
     */
    public void setBehavior(BehaviorType behaviorType) {
        this.currentBehavior = behaviorType;
        this.stateTimer = 0;
        
        // Réinitialiser la direction si on passe en déplacement aléatoire
        if (behaviorType == BehaviorType.RANDOM_MOVE) {
            this.moveDirection = random.nextFloat() * (float) (2 * Math.PI);
        }
    }
    
    /**
     * Obtenir le type de comportement actuel
     * 
     * @return Type de comportement actuel
     */
    public BehaviorType getCurrentBehavior() {
        return currentBehavior;
    }
    
    /**
     * Définir la vitesse de déplacement
     * 
     * @param moveSpeed Nouvelle vitesse de déplacement
     */
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    
    /**
     * Définir la distance de détection du joueur
     * 
     * @param detectionRange Nouvelle distance de détection
     */
    public void setDetectionRange(float detectionRange) {
        this.detectionRange = detectionRange;
        this.fleeRange = detectionRange * 0.8f;
        this.attackRange = detectionRange * 0.5f;
    }
}

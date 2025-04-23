package com.ryuukonpalace.game.world;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureFactory;
import com.ryuukonpalace.game.creatures.ai.CreatureAI;
import com.ryuukonpalace.game.creatures.ai.CreatureAIManager;
import com.ryuukonpalace.game.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Représente une zone où des créatures peuvent apparaître.
 * Peut être une zone de hautes herbes où les créatures apparaissent aléatoirement,
 * ou une zone où une créature spécifique est visible et interactive.
 */
public class SpawnZone extends GameObject {
    
    // Type de zone d'apparition
    private SpawnZoneType type;
    
    // Liste des types de créatures qui peuvent apparaître dans cette zone
    private List<SpawnEntry> possibleCreatures;
    
    // Créature actuellement visible (pour les zones avec créature visible)
    private Creature visibleCreature;
    
    // Probabilité d'apparition d'une créature par pas dans la zone
    private float spawnChancePerStep;
    
    // Nombre de pas effectués dans la zone
    private int stepsInZone;
    
    // Nombre de pas nécessaires pour réinitialiser le compteur
    private int stepsToReset;
    
    // Générateur de nombres aléatoires
    private Random random;
    
    // Indique si le joueur est actuellement dans la zone
    private boolean isPlayerInZone;
    
    /**
     * Constructeur pour une zone d'apparition aléatoire (comme les hautes herbes)
     * 
     * @param x Position X de la zone
     * @param y Position Y de la zone
     * @param width Largeur de la zone
     * @param height Hauteur de la zone
     * @param spawnChancePerStep Probabilité d'apparition par pas (0-1)
     * @param stepsToReset Nombre de pas avant réinitialisation du compteur
     */
    public SpawnZone(float x, float y, float width, float height, float spawnChancePerStep, int stepsToReset) {
        super(x, y, width, height, "spawn_zones");
        this.type = SpawnZoneType.RANDOM_SPAWN;
        this.possibleCreatures = new ArrayList<>();
        this.spawnChancePerStep = spawnChancePerStep;
        this.stepsInZone = 0;
        this.stepsToReset = stepsToReset;
        this.random = new Random();
        this.isPlayerInZone = false;
    }
    
    /**
     * Constructeur pour une zone avec une créature visible
     * 
     * @param x Position X de la zone
     * @param y Position Y de la zone
     * @param width Largeur de la zone
     * @param height Hauteur de la zone
     * @param creatureId ID de la créature visible
     */
    public SpawnZone(float x, float y, float width, float height, int creatureId) {
        super(x, y, width, height, "spawn_zones");
        this.type = SpawnZoneType.VISIBLE_CREATURE;
        this.possibleCreatures = new ArrayList<>();
        
        // Créer la créature visible
        this.visibleCreature = CreatureFactory.createCreature(creatureId);
        
        // Positionner la créature au centre de la zone
        if (visibleCreature != null) {
            float creatureX = x + (width - visibleCreature.getWidth()) / 2;
            float creatureY = y + (height - visibleCreature.getHeight()) / 2;
            visibleCreature.setPosition(creatureX, creatureY);
        }
        
        this.random = new Random();
        this.isPlayerInZone = false;
    }
    
    @Override
    public void update(float deltaTime) {
        // Mise à jour de la créature visible si elle existe
        if (type == SpawnZoneType.VISIBLE_CREATURE && visibleCreature != null) {
            visibleCreature.update(deltaTime);
        }
    }
    
    @Override
    public void render() {
        // Rendu de la créature visible si elle existe
        if (type == SpawnZoneType.VISIBLE_CREATURE && visibleCreature != null) {
            visibleCreature.render();
        }
        
        // Pour le débogage, on peut dessiner les limites de la zone
        // renderer.drawRectangle(x, y, width, height, 0.0f, 1.0f, 0.0f, 0.3f);
    }
    
    @Override
    public void onCollision(GameObject other) {
        // Réagir aux collisions avec le joueur
        if (other.getCollisionGroup().equals("player")) {
            // Marquer que le joueur est dans la zone
            isPlayerInZone = true;
            
            // Si c'est une zone d'apparition aléatoire, incrémenter le compteur de pas
            if (type == SpawnZoneType.RANDOM_SPAWN) {
                Creature creature = incrementSteps();
                if (creature != null) {
                    // Notifier le jeu qu'une créature est apparue
                    // Note: Ceci sera géré par un callback ou un événement
                }
            }
        } else {
            // Si la collision n'est pas avec le joueur, marquer que le joueur n'est pas dans la zone
            isPlayerInZone = false;
        }
    }
    
    /**
     * Ajouter un type de créature qui peut apparaître dans cette zone
     * 
     * @param creatureId ID de la créature
     * @param spawnWeight Poids d'apparition (plus le poids est élevé, plus la créature a de chances d'apparaître)
     * @param minLevel Niveau minimum de la créature
     * @param maxLevel Niveau maximum de la créature
     */
    public void addPossibleCreature(int creatureId, int spawnWeight, int minLevel, int maxLevel) {
        possibleCreatures.add(new SpawnEntry(creatureId, spawnWeight, minLevel, maxLevel));
    }
    
    /**
     * Incrémenter le compteur de pas dans la zone
     * 
     * @return Une créature si une apparition est déclenchée, null sinon
     */
    public Creature incrementSteps() {
        if (type != SpawnZoneType.RANDOM_SPAWN) {
            return null;
        }
        
        stepsInZone++;
        
        // Vérifier si le compteur doit être réinitialisé
        if (stepsInZone >= stepsToReset) {
            stepsInZone = 0;
        }
        
        // Vérifier si une créature apparaît
        if (random.nextFloat() < spawnChancePerStep && !possibleCreatures.isEmpty()) {
            // Sélectionner une créature en fonction des poids d'apparition
            int totalWeight = possibleCreatures.stream().mapToInt(entry -> entry.spawnWeight).sum();
            int randomWeight = random.nextInt(totalWeight);
            
            int currentWeight = 0;
            for (SpawnEntry entry : possibleCreatures) {
                currentWeight += entry.spawnWeight;
                if (randomWeight < currentWeight) {
                    // Créer la créature avec un niveau aléatoire entre min et max
                    int level = entry.minLevel + random.nextInt(entry.maxLevel - entry.minLevel + 1);
                    Creature creature = CreatureFactory.createCreature(entry.creatureId, level);
                    
                    if (creature != null) {
                        // Marquer la créature comme sauvage
                        creature.setWild(true);
                        
                        // Déterminer le comportement de la créature (70% fuite, 30% agressive)
                        CreatureAI.BehaviorType behaviorType = random.nextFloat() < 0.7f ? 
                            CreatureAI.BehaviorType.FLEE : CreatureAI.BehaviorType.AGGRESSIVE;
                        
                        // Créer et attacher l'IA à la créature
                        float moveSpeed = 50.0f + random.nextFloat() * 30.0f; // Vitesse entre 50 et 80
                        float detectionRange = 150.0f + random.nextFloat() * 50.0f; // Portée entre 150 et 200
                        CreatureAI ai = new CreatureAI(creature, behaviorType, moveSpeed, detectionRange);
                        creature.setAI(ai);
                        
                        // Activer la créature
                        creature.setActive(true);
                    }
                    
                    return creature;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Mettre à jour la créature visible dans la zone d'apparition
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param player Joueur (pour la détection de proximité)
     */
    public void updateVisibleCreature(float deltaTime, Player player) {
        // Si le joueur est dans la zone et qu'une créature est visible
        if (isPlayerInZone && visibleCreature != null) {
            // Si la créature n'a pas d'IA, lui en ajouter une
            if (visibleCreature.getAI() == null) {
                // Utiliser le gestionnaire d'IA pour ajouter une IA à la créature
                CreatureAIManager.getInstance().addWildCreatureWithRandomBehavior(visibleCreature);
            }
            
            // La créature est déjà mise à jour par le CreatureAIManager
        }
        
        // Si le joueur n'est plus dans la zone et qu'une créature est visible
        if (!isPlayerInZone && visibleCreature != null) {
            // Désactiver la créature visible
            visibleCreature.setActive(false);
            visibleCreature = null;
        }
    }
    
    /**
     * Obtenir la créature visible dans cette zone
     * 
     * @return La créature visible, ou null si aucune
     */
    public Creature getVisibleCreature() {
        return visibleCreature;
    }
    
    /**
     * Définir si la créature visible est active ou non
     * 
     * @param active true pour activer, false pour désactiver
     */
    public void setVisibleCreatureActive(boolean active) {
        if (visibleCreature != null) {
            visibleCreature.setActive(active);
        }
    }
    
    /**
     * Vérifier si cette zone a une créature visible
     * 
     * @return true si la zone a une créature visible, false sinon
     */
    public boolean hasVisibleCreature() {
        return type == SpawnZoneType.VISIBLE_CREATURE && visibleCreature != null && visibleCreature.isActive();
    }
    
    /**
     * Obtenir le type de cette zone d'apparition
     * 
     * @return Le type de zone d'apparition
     */
    public SpawnZoneType getType() {
        return type;
    }
    
    /**
     * Types de zones d'apparition
     */
    public enum SpawnZoneType {
        RANDOM_SPAWN,    // Zone où les créatures apparaissent aléatoirement (hautes herbes)
        VISIBLE_CREATURE // Zone avec une créature visible et interactive
    }
    
    /**
     * Classe interne pour représenter une entrée dans la liste des créatures possibles
     */
    private static class SpawnEntry {
        int creatureId;
        int spawnWeight;
        int minLevel;
        int maxLevel;
        
        public SpawnEntry(int creatureId, int spawnWeight, int minLevel, int maxLevel) {
            this.creatureId = creatureId;
            this.spawnWeight = spawnWeight;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }
    }
}

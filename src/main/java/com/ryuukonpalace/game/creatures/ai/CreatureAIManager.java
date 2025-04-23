package com.ryuukonpalace.game.creatures.ai;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.world.GameObject;
import com.ryuukonpalace.game.world.SpawnZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gestionnaire central pour l'IA des créatures.
 * Cette classe gère toutes les créatures sauvages et leur IA, évitant ainsi les problèmes
 * de casting entre GameObject et Creature.
 */
public class CreatureAIManager {
    
    // Singleton instance
    private static CreatureAIManager instance;
    
    // Liste des créatures sauvages gérées par ce manager
    private List<Creature> wildCreatures;
    
    // Générateur de nombres aléatoires
    private Random random;
    
    /**
     * Constructeur privé pour le singleton
     */
    private CreatureAIManager() {
        wildCreatures = new ArrayList<>();
        random = new Random();
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire d'IA
     * @return L'instance du CreatureAIManager
     */
    public static CreatureAIManager getInstance() {
        if (instance == null) {
            instance = new CreatureAIManager();
        }
        return instance;
    }
    
    /**
     * Ajouter une créature sauvage au gestionnaire
     * 
     * @param creature La créature à ajouter
     * @param behaviorType Le type de comportement initial
     * @param moveSpeed La vitesse de déplacement
     * @param detectionRange La distance de détection du joueur
     */
    public void addWildCreature(Creature creature, CreatureAI.BehaviorType behaviorType, float moveSpeed, float detectionRange) {
        if (creature != null) {
            // Créer et attacher l'IA à la créature
            CreatureAI ai = new CreatureAI(creature, behaviorType, moveSpeed, detectionRange);
            creature.setAI(ai);
            
            // Marquer la créature comme sauvage
            creature.setWild(true);
            
            // Activer la créature
            creature.setActive(true);
            
            // Ajouter à la liste des créatures sauvages
            wildCreatures.add(creature);
        }
    }
    
    /**
     * Ajouter une créature sauvage au gestionnaire avec des paramètres aléatoires
     * 
     * @param creature La créature à ajouter
     */
    public void addWildCreatureWithRandomBehavior(Creature creature) {
        if (creature != null) {
            // Déterminer le comportement de la créature (70% fuite, 30% agressive)
            CreatureAI.BehaviorType behaviorType = random.nextFloat() < 0.7f ? 
                CreatureAI.BehaviorType.FLEE : CreatureAI.BehaviorType.AGGRESSIVE;
            
            // Vitesse et portée aléatoires
            float moveSpeed = 50.0f + random.nextFloat() * 30.0f; // Vitesse entre 50 et 80
            float detectionRange = 150.0f + random.nextFloat() * 50.0f; // Portée entre 150 et 200
            
            // Ajouter la créature
            addWildCreature(creature, behaviorType, moveSpeed, detectionRange);
        }
    }
    
    /**
     * Supprimer une créature sauvage du gestionnaire
     * 
     * @param creature La créature à supprimer
     */
    public void removeWildCreature(Creature creature) {
        wildCreatures.remove(creature);
    }
    
    /**
     * Mettre à jour toutes les créatures sauvages
     * 
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param player Le joueur (pour la détection)
     */
    public void updateWildCreatures(float deltaTime, Player player) {
        for (Creature creature : wildCreatures) {
            if (creature.isActive() && creature.getAI() != null) {
                creature.updateAI(deltaTime, player);
            }
        }
    }
    
    /**
     * Gérer l'apparition d'une créature depuis une zone d'apparition
     * 
     * @param creature La créature apparue
     * @param spawnZone La zone d'apparition
     */
    public void handleCreatureSpawn(Creature creature, SpawnZone spawnZone) {
        if (creature != null && spawnZone != null) {
            // Positionner la créature à l'emplacement de la zone d'apparition
            float creatureX = spawnZone.getX() + (spawnZone.getWidth() - creature.getWidth()) / 2;
            float creatureY = spawnZone.getY() + (spawnZone.getHeight() - creature.getHeight()) / 2;
            creature.setPosition(creatureX, creatureY);
            
            // Ajouter la créature au gestionnaire d'IA avec un comportement aléatoire
            addWildCreatureWithRandomBehavior(creature);
        }
    }
    
    /**
     * Vérifier si un GameObject est une créature sauvage gérée par ce manager
     * 
     * @param obj L'objet à vérifier
     * @return true si l'objet est une créature sauvage gérée, false sinon
     */
    public boolean isWildCreature(GameObject obj) {
        if (obj instanceof Creature) {
            return wildCreatures.contains((Creature) obj);
        }
        return false;
    }
    
    /**
     * Obtenir la liste des créatures sauvages
     * 
     * @return Liste des créatures sauvages
     */
    public List<Creature> getWildCreatures() {
        return wildCreatures;
    }
}

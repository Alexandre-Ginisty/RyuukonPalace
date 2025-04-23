package com.ryuukonpalace.game.core.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire de collision pour le jeu.
 * Gère l'enregistrement des colliders et la détection des collisions entre eux.
 */
public class CollisionManager {
    
    // Singleton instance
    private static CollisionManager instance;
    
    // Liste des colliders par groupe
    private final Map<String, List<Collider>> colliderGroups;
    
    // Matrice de collision entre groupes
    private final Map<String, List<String>> collisionMatrix;
    
    /**
     * Constructeur privé pour le singleton
     */
    private CollisionManager() {
        colliderGroups = new HashMap<>();
        collisionMatrix = new HashMap<>();
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire de collision
     * @return L'instance du CollisionManager
     */
    public static CollisionManager getInstance() {
        if (instance == null) {
            instance = new CollisionManager();
        }
        return instance;
    }
    
    /**
     * Ajouter un collider à un groupe
     * @param collider Le collider à ajouter
     * @param groupName Le nom du groupe
     */
    public void addCollider(Collider collider, String groupName) {
        if (!colliderGroups.containsKey(groupName)) {
            colliderGroups.put(groupName, new ArrayList<>());
        }
        colliderGroups.get(groupName).add(collider);
    }
    
    /**
     * Supprimer un collider d'un groupe
     * @param collider Le collider à supprimer
     * @param groupName Le nom du groupe
     * @return true si le collider a été supprimé, false sinon
     */
    public boolean removeCollider(Collider collider, String groupName) {
        if (colliderGroups.containsKey(groupName)) {
            return colliderGroups.get(groupName).remove(collider);
        }
        return false;
    }
    
    /**
     * Définir une relation de collision entre deux groupes
     * @param group1 Le premier groupe
     * @param group2 Le second groupe
     */
    public void setCollision(String group1, String group2) {
        if (!collisionMatrix.containsKey(group1)) {
            collisionMatrix.put(group1, new ArrayList<>());
        }
        if (!collisionMatrix.get(group1).contains(group2)) {
            collisionMatrix.get(group1).add(group2);
        }
        
        // Relation symétrique
        if (!collisionMatrix.containsKey(group2)) {
            collisionMatrix.put(group2, new ArrayList<>());
        }
        if (!collisionMatrix.get(group2).contains(group1)) {
            collisionMatrix.get(group2).add(group1);
        }
    }
    
    /**
     * Supprimer une relation de collision entre deux groupes
     * @param group1 Le premier groupe
     * @param group2 Le second groupe
     */
    public void removeCollision(String group1, String group2) {
        if (collisionMatrix.containsKey(group1)) {
            collisionMatrix.get(group1).remove(group2);
        }
        if (collisionMatrix.containsKey(group2)) {
            collisionMatrix.get(group2).remove(group1);
        }
    }
    
    /**
     * Vérifier si un collider est en collision avec un groupe spécifique
     * @param collider Le collider à tester
     * @param groupName Le nom du groupe
     * @return true si le collider est en collision avec au moins un membre du groupe, false sinon
     */
    public boolean isCollidingWithGroup(Collider collider, String groupName) {
        if (!colliderGroups.containsKey(groupName)) {
            return false;
        }
        
        List<Collider> groupColliders = colliderGroups.get(groupName);
        for (Collider other : groupColliders) {
            if (collider != other && collider.collidesWith(other)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Obtenir tous les colliders d'un groupe qui sont en collision avec un collider donné
     * @param collider Le collider à tester
     * @param groupName Le nom du groupe
     * @return Liste des colliders en collision
     */
    public List<Collider> getCollidingWith(Collider collider, String groupName) {
        List<Collider> result = new ArrayList<>();
        
        if (!colliderGroups.containsKey(groupName)) {
            return result;
        }
        
        List<Collider> groupColliders = colliderGroups.get(groupName);
        for (Collider other : groupColliders) {
            if (collider != other && collider.collidesWith(other)) {
                result.add(other);
            }
        }
        
        return result;
    }
    
    /**
     * Mettre à jour toutes les collisions dans le jeu
     * Cette méthode doit être appelée à chaque frame
     * @return Map des collisions détectées (clé: nom du groupe, valeur: liste des paires de colliders en collision)
     */
    public Map<String, List<CollisionPair>> updateCollisions() {
        Map<String, List<CollisionPair>> collisions = new HashMap<>();
        
        // Pour chaque groupe dans la matrice de collision
        for (String group1 : collisionMatrix.keySet()) {
            if (!colliderGroups.containsKey(group1)) {
                continue;
            }
            
            List<String> collidableGroups = collisionMatrix.get(group1);
            List<Collider> group1Colliders = colliderGroups.get(group1);
            
            for (String group2 : collidableGroups) {
                if (!colliderGroups.containsKey(group2)) {
                    continue;
                }
                
                // Éviter de vérifier les mêmes paires deux fois
                if (group1.equals(group2) || (collisions.containsKey(group2) && collisions.get(group2).stream()
                        .anyMatch(pair -> pair.getGroup1().equals(group2) && pair.getGroup2().equals(group1)))) {
                    continue;
                }
                
                List<Collider> group2Colliders = colliderGroups.get(group2);
                
                // Vérifier les collisions entre les deux groupes
                for (Collider collider1 : group1Colliders) {
                    for (Collider collider2 : group2Colliders) {
                        if (collider1.collidesWith(collider2)) {
                            // Ajouter la collision à la liste
                            if (!collisions.containsKey(group1)) {
                                collisions.put(group1, new ArrayList<>());
                            }
                            collisions.get(group1).add(new CollisionPair(group1, group2, collider1, collider2));
                        }
                    }
                }
            }
        }
        
        return collisions;
    }
    
    /**
     * Classe interne pour représenter une paire de colliders en collision
     */
    public static class CollisionPair {
        private final String group1;
        private final String group2;
        private final Collider collider1;
        private final Collider collider2;
        
        public CollisionPair(String group1, String group2, Collider collider1, Collider collider2) {
            this.group1 = group1;
            this.group2 = group2;
            this.collider1 = collider1;
            this.collider2 = collider2;
        }
        
        public String getGroup1() {
            return group1;
        }
        
        public String getGroup2() {
            return group2;
        }
        
        public Collider getCollider1() {
            return collider1;
        }
        
        public Collider getCollider2() {
            return collider2;
        }
    }
}

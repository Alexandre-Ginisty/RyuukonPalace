package com.ryuukonpalace.game.creatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Système de stockage des créatures capturées.
 * Permet au joueur de stocker ses créatures et d'y accéder.
 */
public class CreatureStorage {
    
    // Instance singleton
    private static CreatureStorage instance;
    
    // Nombre maximum de créatures par boîte
    private static final int MAX_CREATURES_PER_BOX = 30;
    
    // Nombre maximum de boîtes
    private static final int MAX_BOXES = 8;
    
    // Boîtes de stockage (Map de boîtes, chaque boîte étant une Map d'emplacements)
    private Map<Integer, Map<Integer, Creature>> boxes;
    
    // Noms des boîtes
    private Map<Integer, String> boxNames;
    
    /**
     * Constructeur privé pour le singleton
     */
    private CreatureStorage() {
        boxes = new HashMap<>();
        boxNames = new HashMap<>();
        
        // Initialiser les boîtes
        for (int i = 0; i < MAX_BOXES; i++) {
            boxes.put(i, new HashMap<>());
            boxNames.put(i, "Boîte " + (i + 1));
        }
    }
    
    /**
     * Obtenir l'instance unique du système de stockage
     * 
     * @return L'instance du CreatureStorage
     */
    public static CreatureStorage getInstance() {
        if (instance == null) {
            instance = new CreatureStorage();
        }
        return instance;
    }
    
    /**
     * Ajouter une créature dans le stockage
     * 
     * @param creature Créature à stocker
     * @return true si la créature a été stockée, false si le stockage est plein
     */
    public boolean addCreature(Creature creature) {
        // Chercher un emplacement libre dans les boîtes
        for (int boxIndex = 0; boxIndex < MAX_BOXES; boxIndex++) {
            Map<Integer, Creature> box = boxes.get(boxIndex);
            
            // Si la boîte n'est pas pleine
            if (box.size() < MAX_CREATURES_PER_BOX) {
                // Trouver le premier emplacement libre
                for (int slot = 0; slot < MAX_CREATURES_PER_BOX; slot++) {
                    if (!box.containsKey(slot)) {
                        // Stocker la créature
                        box.put(slot, creature);
                        return true;
                    }
                }
            }
        }
        
        // Toutes les boîtes sont pleines
        return false;
    }
    
    /**
     * Ajouter une créature dans une boîte spécifique
     * 
     * @param creature Créature à stocker
     * @param boxIndex Index de la boîte
     * @param slot Emplacement dans la boîte
     * @return true si la créature a été stockée, false si l'emplacement est déjà occupé ou invalide
     */
    public boolean addCreature(Creature creature, int boxIndex, int slot) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return false;
        }
        
        // Vérifier si l'emplacement est valide
        if (slot < 0 || slot >= MAX_CREATURES_PER_BOX) {
            return false;
        }
        
        // Vérifier si l'emplacement est libre
        Map<Integer, Creature> box = boxes.get(boxIndex);
        if (box.containsKey(slot)) {
            return false;
        }
        
        // Stocker la créature
        box.put(slot, creature);
        return true;
    }
    
    /**
     * Récupérer une créature du stockage
     * 
     * @param boxIndex Index de la boîte
     * @param slot Emplacement dans la boîte
     * @return La créature, ou null si l'emplacement est vide ou invalide
     */
    public Creature getCreature(int boxIndex, int slot) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return null;
        }
        
        // Vérifier si l'emplacement est valide
        if (slot < 0 || slot >= MAX_CREATURES_PER_BOX) {
            return null;
        }
        
        // Récupérer la créature
        return boxes.get(boxIndex).get(slot);
    }
    
    /**
     * Retirer une créature du stockage
     * 
     * @param boxIndex Index de la boîte
     * @param slot Emplacement dans la boîte
     * @return La créature retirée, ou null si l'emplacement est vide ou invalide
     */
    public Creature removeCreature(int boxIndex, int slot) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return null;
        }
        
        // Vérifier si l'emplacement est valide
        if (slot < 0 || slot >= MAX_CREATURES_PER_BOX) {
            return null;
        }
        
        // Récupérer et retirer la créature
        Map<Integer, Creature> box = boxes.get(boxIndex);
        return box.remove(slot);
    }
    
    /**
     * Déplacer une créature d'un emplacement à un autre
     * 
     * @param sourceBoxIndex Index de la boîte source
     * @param sourceSlot Emplacement source
     * @param targetBoxIndex Index de la boîte cible
     * @param targetSlot Emplacement cible
     * @return true si la créature a été déplacée, false si l'opération a échoué
     */
    public boolean moveCreature(int sourceBoxIndex, int sourceSlot, int targetBoxIndex, int targetSlot) {
        // Vérifier si les boîtes existent
        if (sourceBoxIndex < 0 || sourceBoxIndex >= MAX_BOXES || targetBoxIndex < 0 || targetBoxIndex >= MAX_BOXES) {
            return false;
        }
        
        // Vérifier si les emplacements sont valides
        if (sourceSlot < 0 || sourceSlot >= MAX_CREATURES_PER_BOX || targetSlot < 0 || targetSlot >= MAX_CREATURES_PER_BOX) {
            return false;
        }
        
        // Récupérer les boîtes
        Map<Integer, Creature> sourceBox = boxes.get(sourceBoxIndex);
        Map<Integer, Creature> targetBox = boxes.get(targetBoxIndex);
        
        // Vérifier si l'emplacement source contient une créature
        if (!sourceBox.containsKey(sourceSlot)) {
            return false;
        }
        
        // Si l'emplacement cible est occupé, échanger les créatures
        if (targetBox.containsKey(targetSlot)) {
            Creature sourceCreature = sourceBox.get(sourceSlot);
            Creature targetCreature = targetBox.get(targetSlot);
            
            sourceBox.put(sourceSlot, targetCreature);
            targetBox.put(targetSlot, sourceCreature);
        } else {
            // Sinon, déplacer la créature
            Creature creature = sourceBox.remove(sourceSlot);
            targetBox.put(targetSlot, creature);
        }
        
        return true;
    }
    
    /**
     * Obtenir toutes les créatures d'une boîte
     * 
     * @param boxIndex Index de la boîte
     * @return Liste des créatures dans la boîte, ou liste vide si la boîte est invalide
     */
    public List<Creature> getCreaturesInBox(int boxIndex) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return new ArrayList<>();
        }
        
        // Récupérer les créatures
        return new ArrayList<>(boxes.get(boxIndex).values());
    }
    
    /**
     * Obtenir toutes les créatures stockées
     * 
     * @return Liste de toutes les créatures stockées
     */
    public List<Creature> getAllCreatures() {
        List<Creature> allCreatures = new ArrayList<>();
        
        // Parcourir toutes les boîtes
        for (int boxIndex = 0; boxIndex < MAX_BOXES; boxIndex++) {
            allCreatures.addAll(boxes.get(boxIndex).values());
        }
        
        return allCreatures;
    }
    
    /**
     * Obtenir le nombre de créatures stockées
     * 
     * @return Nombre total de créatures stockées
     */
    public int getCreatureCount() {
        int count = 0;
        
        // Parcourir toutes les boîtes
        for (int boxIndex = 0; boxIndex < MAX_BOXES; boxIndex++) {
            count += boxes.get(boxIndex).size();
        }
        
        return count;
    }
    
    /**
     * Obtenir le nombre de créatures dans une boîte
     * 
     * @param boxIndex Index de la boîte
     * @return Nombre de créatures dans la boîte, ou -1 si la boîte est invalide
     */
    public int getCreatureCountInBox(int boxIndex) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return -1;
        }
        
        return boxes.get(boxIndex).size();
    }
    
    /**
     * Définir le nom d'une boîte
     * 
     * @param boxIndex Index de la boîte
     * @param name Nouveau nom
     * @return true si le nom a été défini, false si la boîte est invalide
     */
    public boolean setBoxName(int boxIndex, String name) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return false;
        }
        
        boxNames.put(boxIndex, name);
        return true;
    }
    
    /**
     * Obtenir le nom d'une boîte
     * 
     * @param boxIndex Index de la boîte
     * @return Nom de la boîte, ou null si la boîte est invalide
     */
    public String getBoxName(int boxIndex) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return null;
        }
        
        return boxNames.get(boxIndex);
    }
    
    /**
     * Obtenir le nombre maximum de créatures par boîte
     * 
     * @return Nombre maximum de créatures par boîte
     */
    public int getMaxCreaturesPerBox() {
        return MAX_CREATURES_PER_BOX;
    }
    
    /**
     * Obtenir le nombre maximum de boîtes
     * 
     * @return Nombre maximum de boîtes
     */
    public int getMaxBoxes() {
        return MAX_BOXES;
    }
    
    /**
     * Rechercher des créatures par type
     * 
     * @param type Type de créature à rechercher
     * @return Liste des créatures du type spécifié
     */
    public List<Creature> searchCreaturesByType(CreatureType type) {
        List<Creature> result = new ArrayList<>();
        
        // Parcourir toutes les boîtes
        for (int boxIndex = 0; boxIndex < MAX_BOXES; boxIndex++) {
            Map<Integer, Creature> box = boxes.get(boxIndex);
            
            // Parcourir toutes les créatures de la boîte
            for (Creature creature : box.values()) {
                if (creature.getType() == type) {
                    result.add(creature);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Rechercher des créatures par nom
     * 
     * @param name Nom ou partie du nom à rechercher
     * @return Liste des créatures dont le nom contient la chaîne spécifiée
     */
    public List<Creature> searchCreaturesByName(String name) {
        List<Creature> result = new ArrayList<>();
        String searchName = name.toLowerCase();
        
        // Parcourir toutes les boîtes
        for (int boxIndex = 0; boxIndex < MAX_BOXES; boxIndex++) {
            Map<Integer, Creature> box = boxes.get(boxIndex);
            
            // Parcourir toutes les créatures de la boîte
            for (Creature creature : box.values()) {
                if (creature.getName().toLowerCase().contains(searchName)) {
                    result.add(creature);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Rechercher des créatures par niveau
     * 
     * @param minLevel Niveau minimum
     * @param maxLevel Niveau maximum
     * @return Liste des créatures dont le niveau est compris entre minLevel et maxLevel
     */
    public List<Creature> searchCreaturesByLevel(int minLevel, int maxLevel) {
        List<Creature> result = new ArrayList<>();
        
        // Parcourir toutes les boîtes
        for (int boxIndex = 0; boxIndex < MAX_BOXES; boxIndex++) {
            Map<Integer, Creature> box = boxes.get(boxIndex);
            
            // Parcourir toutes les créatures de la boîte
            for (Creature creature : box.values()) {
                int level = creature.getLevel();
                if (level >= minLevel && level <= maxLevel) {
                    result.add(creature);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Trier les créatures dans une boîte par niveau
     * 
     * @param boxIndex Index de la boîte
     * @param ascending true pour trier par ordre croissant, false pour décroissant
     * @return true si le tri a été effectué, false si la boîte est invalide
     */
    public boolean sortCreaturesByLevel(int boxIndex, boolean ascending) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return false;
        }
        
        // Récupérer la boîte
        Map<Integer, Creature> box = boxes.get(boxIndex);
        
        // Extraire les créatures
        List<Creature> creatures = new ArrayList<>(box.values());
        
        // Trier les créatures par niveau
        if (ascending) {
            creatures.sort((c1, c2) -> Integer.compare(c1.getLevel(), c2.getLevel()));
        } else {
            creatures.sort((c1, c2) -> Integer.compare(c2.getLevel(), c1.getLevel()));
        }
        
        // Vider la boîte
        box.clear();
        
        // Remettre les créatures triées
        for (int i = 0; i < creatures.size(); i++) {
            box.put(i, creatures.get(i));
        }
        
        return true;
    }
    
    /**
     * Trier les créatures dans une boîte par nom
     * 
     * @param boxIndex Index de la boîte
     * @return true si le tri a été effectué, false si la boîte est invalide
     */
    public boolean sortCreaturesByName(int boxIndex) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return false;
        }
        
        // Récupérer la boîte
        Map<Integer, Creature> box = boxes.get(boxIndex);
        
        // Extraire les créatures
        List<Creature> creatures = new ArrayList<>(box.values());
        
        // Trier les créatures par nom
        creatures.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
        
        // Vider la boîte
        box.clear();
        
        // Remettre les créatures triées
        for (int i = 0; i < creatures.size(); i++) {
            box.put(i, creatures.get(i));
        }
        
        return true;
    }
    
    /**
     * Trier les créatures dans une boîte par type
     * 
     * @param boxIndex Index de la boîte
     * @return true si le tri a été effectué, false si la boîte est invalide
     */
    public boolean sortCreaturesByType(int boxIndex) {
        // Vérifier si la boîte existe
        if (boxIndex < 0 || boxIndex >= MAX_BOXES) {
            return false;
        }
        
        // Récupérer la boîte
        Map<Integer, Creature> box = boxes.get(boxIndex);
        
        // Extraire les créatures
        List<Creature> creatures = new ArrayList<>(box.values());
        
        // Trier les créatures par type
        creatures.sort((c1, c2) -> c1.getType().name().compareTo(c2.getType().name()));
        
        // Vider la boîte
        box.clear();
        
        // Remettre les créatures triées
        for (int i = 0; i < creatures.size(); i++) {
            box.put(i, creatures.get(i));
        }
        
        return true;
    }
}

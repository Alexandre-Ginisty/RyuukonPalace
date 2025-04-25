package com.ryuukonpalace.game.player;

import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.items.CaptureStone;
import com.ryuukonpalace.game.creatures.Creature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe gérant l'inventaire du joueur.
 * Contient les pierres de capture et autres objets.
 */
public class Inventory {
    
    // Liste des objets dans l'inventaire
    private List<Item> items;
    
    // Liste des pierres de capture dans l'inventaire
    private List<CaptureStone> captureStones;
    
    // Liste des équipements du joueur
    private List<Item> equipment;
    
    // Pierres de capture équipées (collier)
    private Map<Integer, CaptureStone> equippedStones;
    
    // Nombre maximum de pierres équipées
    private int maxEquippedStones;
    
    /**
     * Constructeur
     */
    public Inventory() {
        this.items = new ArrayList<>();
        this.captureStones = new ArrayList<>();
        this.equipment = new ArrayList<>();
        this.equippedStones = new HashMap<>();
        this.maxEquippedStones = 6; // Par défaut, le joueur peut équiper 6 pierres
    }
    
    /**
     * Ajouter un objet à l'inventaire
     * 
     * @param item Objet à ajouter
     */
    public void addItem(Item item) {
        items.add(item);
        
        // Si c'est une pierre de capture, l'ajouter aussi à la liste des pierres
        if (item instanceof CaptureStone) {
            captureStones.add((CaptureStone) item);
        }
    }
    
    /**
     * Retirer un objet de l'inventaire
     * 
     * @param item Objet à retirer
     * @return true si l'objet a été retiré, false sinon
     */
    public boolean removeItem(Item item) {
        // Si c'est une pierre de capture, la retirer aussi de la liste des pierres
        if (item instanceof CaptureStone) {
            captureStones.remove(item);
            
            // Vérifier si la pierre est équipée
            for (int i = 0; i < maxEquippedStones; i++) {
                if (equippedStones.get(i) == item) {
                    equippedStones.remove(i);
                    break;
                }
            }
        }
        
        return items.remove(item);
    }
    
    /**
     * Retirer un objet de l'inventaire par son ID
     * 
     * @param itemId ID de l'objet à retirer
     * @param quantity Quantité à retirer
     * @return true si l'objet a été retiré, false sinon
     */
    public boolean removeItem(int itemId, int quantity) {
        int removed = 0;
        List<Item> itemsToRemove = new ArrayList<>();
        
        // Trouver tous les objets correspondant à l'ID
        for (Item item : items) {
            if (item.getId() == itemId) {
                itemsToRemove.add(item);
                removed++;
                
                if (removed >= quantity) {
                    break;
                }
            }
        }
        
        // Si on n'a pas trouvé assez d'objets, retourner false
        if (removed < quantity) {
            return false;
        }
        
        // Retirer les objets
        for (Item item : itemsToRemove) {
            removeItem(item);
        }
        
        return true;
    }
    
    /**
     * Obtenir la quantité d'un objet dans l'inventaire
     * 
     * @param itemId ID de l'objet
     * @return Quantité de l'objet
     */
    public int getItemQuantity(int itemId) {
        int count = 0;
        
        for (Item item : items) {
            if (item.getId() == itemId) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Obtenir la liste des objets dans l'inventaire
     * 
     * @return Liste des objets
     */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * Obtenir la liste des pierres de capture dans l'inventaire
     * 
     * @return Liste des pierres de capture
     */
    public List<CaptureStone> getCaptureStones() {
        return new ArrayList<>(captureStones);
    }
    
    /**
     * Équiper une pierre de capture à un emplacement spécifique du collier
     * 
     * @param stone Pierre à équiper
     * @param position Position dans le collier (0-5)
     * @return true si la pierre a été équipée, false sinon
     */
    public boolean equipStone(CaptureStone stone, int position) {
        // Vérifier si la position est valide
        if (position < 0 || position >= maxEquippedStones) {
            return false;
        }
        
        // Vérifier si la pierre est dans l'inventaire
        if (!captureStones.contains(stone)) {
            return false;
        }
        
        // Équiper la pierre
        equippedStones.put(position, stone);
        return true;
    }
    
    /**
     * Déséquiper une pierre de capture
     * 
     * @param position Position dans le collier (0-5)
     * @return Pierre déséquipée, ou null si aucune pierre n'était équipée à cette position
     */
    public CaptureStone unequipStone(int position) {
        // Vérifier si la position est valide
        if (position < 0 || position >= maxEquippedStones) {
            return null;
        }
        
        // Déséquiper la pierre
        return equippedStones.remove(position);
    }
    
    /**
     * Obtenir la pierre équipée à une position spécifique
     * 
     * @param position Position dans le collier (0-5)
     * @return Pierre équipée, ou null si aucune pierre n'est équipée à cette position
     */
    public CaptureStone getEquippedStone(int position) {
        return equippedStones.get(position);
    }
    
    /**
     * Obtenir toutes les pierres équipées
     * 
     * @return Map des pierres équipées (clé = position, valeur = pierre)
     */
    public Map<Integer, CaptureStone> getEquippedStones() {
        return new HashMap<>(equippedStones);
    }
    
    /**
     * Obtenir le nombre maximum de pierres équipées
     * 
     * @return Nombre maximum de pierres équipées
     */
    public int getMaxEquippedStones() {
        return maxEquippedStones;
    }
    
    /**
     * Définir le nombre maximum de pierres équipées
     * 
     * @param maxEquippedStones Nouveau nombre maximum
     */
    public void setMaxEquippedStones(int maxEquippedStones) {
        this.maxEquippedStones = maxEquippedStones;
    }
    
    /**
     * Obtenir la liste des créatures capturées (dans les pierres)
     * 
     * @return Liste des créatures capturées
     */
    public List<Creature> getCapturedCreatures() {
        List<Creature> creatures = new ArrayList<>();
        
        for (CaptureStone stone : captureStones) {
            if (stone.isActive() && stone.getCapturedCreature() != null) {
                creatures.add(stone.getCapturedCreature());
            }
        }
        
        return creatures;
    }
    
    /**
     * Obtenir la liste des équipements du joueur
     * 
     * @return Liste des équipements
     */
    public List<Item> getEquipment() {
        return new ArrayList<>(equipment);
    }
    
    /**
     * Définir la liste des équipements du joueur
     * 
     * @param equipment Nouvelle liste d'équipements
     */
    public void setEquipment(List<Item> equipment) {
        this.equipment = new ArrayList<>(equipment);
    }
    
    /**
     * Ajouter un équipement à l'inventaire
     * 
     * @param item Équipement à ajouter
     */
    public void addEquipment(Item item) {
        equipment.add(item);
    }
    
    /**
     * Retirer un équipement de l'inventaire
     * 
     * @param item Équipement à retirer
     * @return true si l'équipement a été retiré, false sinon
     */
    public boolean removeEquipment(Item item) {
        return equipment.remove(item);
    }
    
    /**
     * Définir la liste des objets dans l'inventaire
     * 
     * @param items Nouvelle liste d'objets
     */
    public void setItems(List<Item> items) {
        this.items = new ArrayList<>(items);
        
        // Mettre à jour la liste des pierres de capture
        this.captureStones.clear();
        for (Item item : items) {
            if (item instanceof CaptureStone) {
                this.captureStones.add((CaptureStone) item);
            }
        }
    }
}

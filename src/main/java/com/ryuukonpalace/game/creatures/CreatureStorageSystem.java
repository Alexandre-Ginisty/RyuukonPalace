package com.ryuukonpalace.game.creatures;

import com.ryuukonpalace.game.items.CaptureStone;
import com.ryuukonpalace.game.player.Inventory;
import com.ryuukonpalace.game.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Système de gestion du stockage des créatures.
 * Fait le lien entre le stockage des créatures et l'inventaire du joueur.
 */
public class CreatureStorageSystem {
    
    // Instance singleton
    private static CreatureStorageSystem instance;
    
    // Référence au stockage des créatures
    private CreatureStorage creatureStorage;
    
    // Référence à l'évolution des créatures
    private EvolutionManager evolutionManager;
    
    /**
     * Constructeur privé pour le singleton
     */
    private CreatureStorageSystem() {
        this.creatureStorage = CreatureStorage.getInstance();
        this.evolutionManager = EvolutionManager.getInstance();
    }
    
    /**
     * Obtenir l'instance unique du système de stockage
     * 
     * @return L'instance du CreatureStorageSystem
     */
    public static CreatureStorageSystem getInstance() {
        if (instance == null) {
            instance = new CreatureStorageSystem();
        }
        return instance;
    }
    
    /**
     * Déposer une créature capturée dans le stockage
     * 
     * @param player Joueur qui dépose la créature
     * @param stone Pierre de capture contenant la créature
     * @return true si la créature a été déposée, false sinon
     */
    public boolean depositCreature(Player player, CaptureStone stone) {
        // Vérifier si la pierre contient une créature
        if (!stone.isActive() || stone.getCapturedCreature() == null) {
            return false;
        }
        
        // Récupérer la créature
        Creature creature = stone.getCapturedCreature();
        
        // Ajouter la créature au stockage
        boolean success = creatureStorage.addCreature(creature);
        
        // Si la créature a été ajoutée au stockage, la retirer de la pierre
        if (success) {
            stone.releaseCreature();
        }
        
        return success;
    }
    
    /**
     * Déposer une créature dans une boîte et un emplacement spécifiques
     * 
     * @param player Joueur qui dépose la créature
     * @param stone Pierre de capture contenant la créature
     * @param boxIndex Index de la boîte
     * @param slot Emplacement dans la boîte
     * @return true si la créature a été déposée, false sinon
     */
    public boolean depositCreature(Player player, CaptureStone stone, int boxIndex, int slot) {
        // Vérifier si la pierre contient une créature
        if (!stone.isActive() || stone.getCapturedCreature() == null) {
            return false;
        }
        
        // Récupérer la créature
        Creature creature = stone.getCapturedCreature();
        
        // Ajouter la créature au stockage
        boolean success = creatureStorage.addCreature(creature, boxIndex, slot);
        
        // Si la créature a été ajoutée au stockage, la retirer de la pierre
        if (success) {
            stone.releaseCreature();
        }
        
        return success;
    }
    
    /**
     * Récupérer une créature du stockage
     * 
     * @param player Joueur qui récupère la créature
     * @param boxIndex Index de la boîte
     * @param slot Emplacement dans la boîte
     * @param stone Pierre de capture à utiliser
     * @return true si la créature a été récupérée, false sinon
     */
    public boolean withdrawCreature(Player player, int boxIndex, int slot, CaptureStone stone) {
        // Vérifier si la pierre est déjà active
        if (stone.isActive()) {
            return false;
        }
        
        // Récupérer la créature
        Creature creature = creatureStorage.getCreature(boxIndex, slot);
        if (creature == null) {
            return false;
        }
        
        // Capturer la créature dans la pierre
        boolean success = stone.captureCreature(creature);
        
        // Si la créature a été capturée, la retirer du stockage
        if (success) {
            creatureStorage.removeCreature(boxIndex, slot);
        }
        
        return success;
    }
    
    /**
     * Faire évoluer une créature dans le stockage
     * 
     * @param boxIndex Index de la boîte
     * @param slot Emplacement dans la boîte
     * @param player Joueur qui fait évoluer la créature
     * @return true si la créature a évolué, false sinon
     */
    public boolean evolveCreature(int boxIndex, int slot, Player player) {
        // Récupérer la créature
        Creature creature = creatureStorage.getCreature(boxIndex, slot);
        if (creature == null) {
            return false;
        }
        
        // Vérifier si la créature peut évoluer
        if (!evolutionManager.canEvolve(creature)) {
            return false;
        }
        
        // Faire évoluer la créature
        Creature evolvedCreature = evolutionManager.evolveCreature(creature);
        
        // Si la créature a évolué, la remplacer dans le stockage
        if (evolvedCreature != creature) {
            // Retirer l'ancienne créature
            creatureStorage.removeCreature(boxIndex, slot);
            
            // Ajouter la nouvelle créature
            creatureStorage.addCreature(evolvedCreature, boxIndex, slot);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Obtenir toutes les créatures stockées
     * 
     * @return Liste de toutes les créatures stockées
     */
    public List<Creature> getAllStoredCreatures() {
        return creatureStorage.getAllCreatures();
    }
    
    /**
     * Obtenir toutes les créatures d'une boîte
     * 
     * @param boxIndex Index de la boîte
     * @return Liste des créatures dans la boîte
     */
    public List<Creature> getCreaturesInBox(int boxIndex) {
        return creatureStorage.getCreaturesInBox(boxIndex);
    }
    
    /**
     * Obtenir le nom d'une boîte
     * 
     * @param boxIndex Index de la boîte
     * @return Nom de la boîte
     */
    public String getBoxName(int boxIndex) {
        return creatureStorage.getBoxName(boxIndex);
    }
    
    /**
     * Définir le nom d'une boîte
     * 
     * @param boxIndex Index de la boîte
     * @param name Nouveau nom
     * @return true si le nom a été défini, false sinon
     */
    public boolean setBoxName(int boxIndex, String name) {
        return creatureStorage.setBoxName(boxIndex, name);
    }
    
    /**
     * Obtenir le nombre de boîtes disponibles
     * 
     * @return Nombre de boîtes
     */
    public int getMaxBoxes() {
        return creatureStorage.getMaxBoxes();
    }
    
    /**
     * Obtenir le nombre maximum de créatures par boîte
     * 
     * @return Nombre maximum de créatures par boîte
     */
    public int getMaxCreaturesPerBox() {
        return creatureStorage.getMaxCreaturesPerBox();
    }
    
    /**
     * Obtenir toutes les créatures capturées (dans l'inventaire et le stockage)
     * 
     * @param player Joueur
     * @return Liste de toutes les créatures capturées
     */
    public List<Creature> getAllCapturedCreatures(Player player) {
        List<Creature> allCreatures = new ArrayList<>();
        
        // Ajouter les créatures dans l'inventaire
        Inventory inventory = player.getInventory();
        allCreatures.addAll(inventory.getCapturedCreatures());
        
        // Ajouter les créatures dans le stockage
        allCreatures.addAll(creatureStorage.getAllCreatures());
        
        return allCreatures;
    }
    
    /**
     * Rechercher des créatures par type
     * 
     * @param type Type de créature
     * @return Liste des créatures du type spécifié
     */
    public List<Creature> searchCreaturesByType(CreatureType type) {
        return creatureStorage.searchCreaturesByType(type);
    }
    
    /**
     * Rechercher des créatures par nom
     * 
     * @param name Nom ou partie du nom
     * @return Liste des créatures dont le nom contient la chaîne spécifiée
     */
    public List<Creature> searchCreaturesByName(String name) {
        return creatureStorage.searchCreaturesByName(name);
    }
    
    /**
     * Rechercher des créatures par niveau
     * 
     * @param minLevel Niveau minimum
     * @param maxLevel Niveau maximum
     * @return Liste des créatures dont le niveau est compris entre minLevel et maxLevel
     */
    public List<Creature> searchCreaturesByLevel(int minLevel, int maxLevel) {
        return creatureStorage.searchCreaturesByLevel(minLevel, maxLevel);
    }
    
    /**
     * Trier les créatures dans une boîte par niveau
     * 
     * @param boxIndex Index de la boîte
     * @param ascending true pour trier par ordre croissant, false pour décroissant
     * @return true si le tri a été effectué, false sinon
     */
    public boolean sortCreaturesByLevel(int boxIndex, boolean ascending) {
        return creatureStorage.sortCreaturesByLevel(boxIndex, ascending);
    }
    
    /**
     * Trier les créatures dans une boîte par nom
     * 
     * @param boxIndex Index de la boîte
     * @return true si le tri a été effectué, false sinon
     */
    public boolean sortCreaturesByName(int boxIndex) {
        return creatureStorage.sortCreaturesByName(boxIndex);
    }
    
    /**
     * Trier les créatures dans une boîte par type
     * 
     * @param boxIndex Index de la boîte
     * @return true si le tri a été effectué, false sinon
     */
    public boolean sortCreaturesByType(int boxIndex) {
        return creatureStorage.sortCreaturesByType(boxIndex);
    }
}

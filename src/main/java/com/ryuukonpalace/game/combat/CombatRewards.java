package com.ryuukonpalace.game.combat;

import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.items.CaptureStone;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.creatures.CreatureType;
import com.ryuukonpalace.game.items.Equipment;
import com.ryuukonpalace.game.items.SpecialEquipments;
import com.ryuukonpalace.game.items.CommonItem;
import com.ryuukonpalace.game.items.CaptureStoneMaterial;
import com.ryuukonpalace.game.items.CaptureStoneType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe gérant les récompenses après un combat victorieux
 */
public class CombatRewards {
    
    // Singleton
    private static CombatRewards instance;
    
    // Générateur de nombres aléatoires
    private Random random;
    
    /**
     * Constructeur privé (singleton)
     */
    private CombatRewards() {
        random = new Random();
    }
    
    /**
     * Obtenir l'instance unique de CombatRewards
     * 
     * @return L'instance de CombatRewards
     */
    public static CombatRewards getInstance() {
        if (instance == null) {
            instance = new CombatRewards();
        }
        return instance;
    }
    
    /**
     * Générer les récompenses pour un combat victorieux
     * 
     * @param defeatedCreature La créature vaincue
     * @param playerLevel Le niveau du joueur
     * @return Les récompenses générées
     */
    public Rewards generateRewards(Creature defeatedCreature, int playerLevel) {
        Rewards rewards = new Rewards();
        
        // Calculer la base de crystaux en fonction du niveau de la créature vaincue
        int baseReward = 10 + (defeatedCreature.getLevel() * 5);
        
        // Bonus pour les créatures de niveau supérieur au joueur
        if (defeatedCreature.getLevel() > playerLevel) {
            baseReward += (defeatedCreature.getLevel() - playerLevel) * 10;
        }
        
        // Variation aléatoire (+/- 20%)
        int variation = (int)(baseReward * 0.2);
        int crystalReward = baseReward + random.nextInt(variation * 2 + 1) - variation;
        
        rewards.setCrystals(crystalReward);
        
        // Chance de trouver des objets (40% de chance)
        if (random.nextDouble() < 0.4) {
            List<Item> items = generateItems(defeatedCreature);
            rewards.setItems(items);
        }
        
        // Chance de trouver une pierre de capture (5% de chance)
        if (random.nextDouble() < 0.05) {
            CaptureStone stone = generateCaptureStone(defeatedCreature);
            if (stone != null) {
                rewards.addItem(stone);
            }
        }
        
        // Chance de trouver un équipement (15% de chance)
        if (random.nextDouble() < 0.15) {
            Equipment equipment = generateEquipment(defeatedCreature);
            if (equipment != null) {
                rewards.addEquipment(equipment);
            }
        }
        
        return rewards;
    }
    
    /**
     * Générer des objets en fonction de la créature vaincue
     * 
     * @param defeatedCreature La créature vaincue
     * @return Liste d'objets générés
     */
    private List<Item> generateItems(Creature defeatedCreature) {
        List<Item> items = new ArrayList<>();
        
        // Logique pour générer des objets en fonction du type de créature
        CreatureType type = defeatedCreature.getType();
        
        // Exemple: chaque type de créature peut donner des objets spécifiques
        switch (type) {
            case FIRE:
                // Objets liés au feu
                items.add(new CommonItem("Écaille de feu", "Une écaille brûlante d'une créature de feu.", 10));
                break;
            case WATER:
                // Objets liés à l'eau
                items.add(new CommonItem("Perle d'eau", "Une perle brillante d'une créature aquatique.", 12));
                break;
            case EARTH:
                // Objets liés à la terre
                items.add(new CommonItem("Pierre précieuse", "Une pierre précieuse trouvée sur une créature terrestre.", 15));
                break;
            case NATURE:
                // Objets liés à la nature
                items.add(new CommonItem("Herbe médicinale", "Une herbe rare aux propriétés curatives.", 8));
                break;
            case ELECTRIC:
                // Objets liés à l'électricité
                items.add(new CommonItem("Cristal électrique", "Un cristal qui émet une légère charge électrique.", 14));
                break;
            case AIR:
                // Objets liés à l'air
                items.add(new CommonItem("Plume légère", "Une plume extrêmement légère d'une créature aérienne.", 7));
                break;
            default:
                // Objet générique
                items.add(new CommonItem("Essence mystérieuse", "Une substance étrange d'origine inconnue.", 20));
                break;
        }
        
        // Chance de trouver une potion de soin (30% de chance)
        if (random.nextDouble() < 0.3) {
            items.add(new CommonItem("Potion de soin", "Restaure 50 points de vie à une créature.", 20));
        }
        
        return items;
    }
    
    /**
     * Générer une pierre de capture en fonction de la créature vaincue
     * 
     * @param defeatedCreature La créature vaincue
     * @return Pierre de capture générée, ou null
     */
    private CaptureStone generateCaptureStone(Creature defeatedCreature) {
        int level = defeatedCreature.getLevel();
        
        // Déterminer le matériau de la pierre en fonction du niveau
        CaptureStoneMaterial material;
        if (level >= 30) {
            material = CaptureStoneMaterial.DIAMOND;
        } else if (level >= 20) {
            material = CaptureStoneMaterial.EMERALD;
        } else if (level >= 10) {
            material = CaptureStoneMaterial.SAPPHIRE;
        } else {
            material = CaptureStoneMaterial.QUARTZ;
        }
        
        // Déterminer le type de pierre en fonction du type de créature
        CaptureStoneType stoneType = CaptureStoneType.NEUTRAL;
        CreatureType creatureType = defeatedCreature.getType();
        
        // Associer le type de créature au type de pierre approprié
        switch (creatureType) {
            case FIRE:
                stoneType = CaptureStoneType.FIRE;
                break;
            case WATER:
                stoneType = CaptureStoneType.WATER;
                break;
            case EARTH:
                stoneType = CaptureStoneType.EARTH;
                break;
            case AIR:
                stoneType = CaptureStoneType.AIR;
                break;
            case LIGHT:
                stoneType = CaptureStoneType.LIGHT;
                break;
            case SHADOW:
                stoneType = CaptureStoneType.DARK;
                break;
            default:
                stoneType = CaptureStoneType.NEUTRAL;
                break;
        }
        
        // Créer la pierre de capture
        return new CaptureStone("Pierre de capture", "Une pierre permettant de capturer des créatures.", 0, material, stoneType);
    }
    
    /**
     * Générer un équipement en fonction de la créature vaincue
     * 
     * @param defeatedCreature La créature vaincue
     * @return Équipement généré, ou null
     */
    private Equipment generateEquipment(Creature defeatedCreature) {
        int level = defeatedCreature.getLevel();
        CreatureType type = defeatedCreature.getType();
        
        // Déterminer la rareté en fonction du niveau
        Equipment.Rarity rarity;
        double rarityRoll = random.nextDouble();
        
        if (level >= 30 && rarityRoll < 0.1) {
            rarity = Equipment.Rarity.LEGENDARY;
        } else if (level >= 20 && rarityRoll < 0.3) {
            rarity = Equipment.Rarity.RARE;
        } else if (level >= 10 && rarityRoll < 0.6) {
            rarity = Equipment.Rarity.UNCOMMON;
        } else {
            rarity = Equipment.Rarity.COMMON;
        }
        
        // Déterminer le type d'équipement
        Equipment.EquipmentType equipType;
        double typeRoll = random.nextDouble();
        
        if (typeRoll < 0.25) {
            equipType = Equipment.EquipmentType.WEAPON;
        } else if (typeRoll < 0.5) {
            equipType = Equipment.EquipmentType.ARMOR;
        } else if (typeRoll < 0.75) {
            equipType = Equipment.EquipmentType.ACCESSORY;
        } else {
            equipType = Equipment.EquipmentType.AMULET;
        }
        
        // Créer l'équipement en fonction du type de créature
        switch (type) {
            case FIRE:
                return SpecialEquipments.createFireEquipment(equipType, rarity, level);
            case WATER:
                return SpecialEquipments.createWaterEquipment(equipType, rarity, level);
            case EARTH:
                return SpecialEquipments.createEarthEquipment(equipType, rarity, level);
            case ELECTRIC:
                return SpecialEquipments.createElectricEquipment(equipType, rarity, level);
            case NATURE:
                return SpecialEquipments.createNatureEquipment(equipType, rarity, level);
            default:
                // Équipement générique
                return new Equipment(
                    "Équipement mystique", 
                    "Un équipement aux propriétés mystérieuses.", 
                    equipType, 
                    rarity, 
                    level
                );
        }
    }
    
    /**
     * Classe interne représentant les récompenses d'un combat
     */
    public static class Rewards {
        private int crystals;
        private List<Item> items;
        
        /**
         * Constructeur
         */
        public Rewards() {
            this.crystals = 0;
            this.items = new ArrayList<>();
        }
        
        /**
         * Obtenir le nombre de crystaux
         * 
         * @return Nombre de crystaux
         */
        public int getCrystals() {
            return crystals;
        }
        
        /**
         * Définir le nombre de crystaux
         * 
         * @param crystals Nombre de crystaux
         */
        public void setCrystals(int crystals) {
            this.crystals = crystals;
        }
        
        /**
         * Obtenir la liste des objets
         * 
         * @return Liste des objets
         */
        public List<Item> getItems() {
            return new ArrayList<>(items);
        }
        
        /**
         * Définir la liste des objets
         * 
         * @param items Liste des objets
         */
        public void setItems(List<Item> items) {
            this.items = new ArrayList<>(items);
        }
        
        /**
         * Ajouter un objet aux récompenses
         * 
         * @param item Objet à ajouter
         */
        public void addItem(Item item) {
            this.items.add(item);
        }
        
        /**
         * Ajouter un équipement aux récompenses
         * Convertit l'équipement en Item compatible
         * 
         * @param equipment Équipement à ajouter
         */
        public void addEquipment(Equipment equipment) {
            // Créer un CommonItem basé sur l'équipement
            CommonItem itemEquipment = new CommonItem(
                equipment.getName(),
                equipment.getDescription(),
                equipment.getTextureId(),
                Item.ItemRarity.valueOf(equipment.getRarity().name()),
                equipment.getValue()
            );
            this.items.add(itemEquipment);
        }
        
        /**
         * Vérifier si les récompenses contiennent des objets
         * 
         * @return true si les récompenses contiennent des objets, false sinon
         */
        public boolean hasItems() {
            return !items.isEmpty();
        }
        
        /**
         * Obtenir une description textuelle des récompenses
         * 
         * @return Description des récompenses
         */
        public String getDescription() {
            StringBuilder description = new StringBuilder();
            
            description.append(crystals).append(" crystaux");
            
            if (hasItems()) {
                description.append(" et ").append(items.size()).append(" objet(s)");
            }
            
            return description.toString();
        }
    }
}

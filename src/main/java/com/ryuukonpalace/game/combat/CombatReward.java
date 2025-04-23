package com.ryuukonpalace.game.combat;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.items.CommonItem;
import com.ryuukonpalace.game.items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe gérant les récompenses après un combat victorieux
 */
public class CombatReward {
    
    // Générateur de nombres aléatoires
    private static final Random random = new Random();
    
    /**
     * Générer les récompenses pour un combat victorieux
     * 
     * @param defeatedCreature La créature vaincue
     * @param playerLevel Le niveau du joueur
     * @return Les récompenses générées
     */
    public static Reward generateReward(Creature defeatedCreature, int playerLevel) {
        Reward reward = new Reward();
        
        // Calculer la base de crystaux en fonction du niveau de la créature vaincue
        int baseReward = 10 + (defeatedCreature.getLevel() * 5);
        
        // Bonus pour les créatures de niveau supérieur au joueur
        if (defeatedCreature.getLevel() > playerLevel) {
            baseReward += (defeatedCreature.getLevel() - playerLevel) * 10;
        }
        
        // Variation aléatoire (+/- 20%)
        int variation = (int)(baseReward * 0.2);
        int crystalReward = baseReward + random.nextInt(variation * 2 + 1) - variation;
        
        reward.setCrystals(crystalReward);
        
        // Chance de trouver des objets (40% de chance)
        if (random.nextDouble() < 0.4) {
            List<Item> items = generateItems(defeatedCreature);
            reward.setItems(items);
        }
        
        return reward;
    }
    
    /**
     * Générer des objets en fonction de la créature vaincue
     * 
     * @param defeatedCreature La créature vaincue
     * @return Liste d'objets générés
     */
    private static List<Item> generateItems(Creature defeatedCreature) {
        List<Item> items = new ArrayList<>();
        
        // Logique pour générer des objets en fonction du type de créature
        switch (defeatedCreature.getType()) {
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
                items.add(new CommonItem("Graine rare", "Une graine d'une plante rare.", 8));
                break;
            case ELECTRIC:
                // Objets liés à l'électricité
                items.add(new CommonItem("Cristal d'énergie", "Un cristal chargé d'électricité.", 14));
                break;
            default:
                // Objets génériques
                items.add(new CommonItem("Essence mystique", "Une essence mystérieuse extraite d'une créature.", 5));
                break;
        }
        
        // Chance de trouver un objet de soin (30% de chance)
        if (random.nextDouble() < 0.3) {
            items.add(new CommonItem("Potion de soin", "Restaure 50 points de vie à une créature.", 20));
        }
        
        return items;
    }
    
    /**
     * Classe interne représentant les récompenses d'un combat
     */
    public static class Reward {
        private int crystals;
        private List<Item> items;
        
        /**
         * Constructeur
         */
        public Reward() {
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

package com.ryuukonpalace.game.items;

import com.ryuukonpalace.game.combat.CombatStats;
import com.ryuukonpalace.game.combat.StatusEffect;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire pour créer des équipements spéciaux prédéfinis
 * avec des effets uniques inspirés des types de variants.
 */
public class SpecialEquipments {

    /**
     * Crée l'épée Lame de Stratège
     * @return L'équipement créé
     */
    public static Equipment createStrategistBlade() {
        Equipment equipment = new Equipment(
            "Lame de Stratège",
            "Une épée forgée par les plus grands stratèges d'Elderglen. Permet d'anticiper les mouvements ennemis.",
            Equipment.EquipmentType.WEAPON,
            Equipment.Rarity.EPIC,
            30
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("physicalAttack", 45);
        equipment.addStatBonus("magicalAttack", 15);
        equipment.addStatBonus("accuracy", 10);
        
        // Bonus de pourcentage
        equipment.addPercentBonus("criticalChance", 15.0f);
        
        // Effet spécial: Prévoyance
        equipment.addSpecialEffect(new SpecialEffect("Prévoyance", "20% de chances d'esquiver complètement une attaque") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                if (Math.random() < 0.2) {
                    // L'effet est passif et sera géré par le système de combat
                    // Ceci est juste un placeholder pour l'implémentation
                    System.out.println(wearer + " a esquivé une attaque grâce à Prévoyance!");
                }
            }
        });
        
        return equipment;
    }
    
    /**
     * Crée l'armure Cuirasse du Furieux
     * @return L'équipement créé
     */
    public static Equipment createRageArmor() {
        Equipment equipment = new Equipment(
            "Cuirasse du Furieux",
            "Une armure imprégnée de la rage des guerriers de Stormpeak. Devient plus puissante à mesure que son porteur subit des dégâts.",
            Equipment.EquipmentType.ARMOR,
            Equipment.Rarity.EPIC,
            35
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("physicalDefense", 50);
        equipment.addStatBonus("magicalDefense", 30);
        equipment.addStatBonus("maxHealth", 200);
        
        // Bonus de pourcentage
        equipment.addPercentBonus("tenacity", 20.0f);
        
        // Effet spécial: Fureur Croissante
        equipment.addSpecialEffect(new SpecialEffect("Fureur Croissante", "Gagne +2 d'attaque physique pour chaque 5% de PV perdus") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                int healthPercentLost = 100 - (wearer.getCurrentHealth() * 100 / wearer.getMaxHealth());
                int attackBonus = (healthPercentLost / 5) * 2;
                
                // Appliquer le bonus temporairement (sera réinitialisé après le combat)
                wearer.setPhysicalAttack(wearer.getPhysicalAttack() + attackBonus);
            }
        });
        
        // Chance d'infliger l'effet Enragé
        equipment.addStatusEffectChance(StatusEffect.ENRAGED, 15.0f);
        
        return equipment;
    }
    
    /**
     * Crée le casque Couronne Mystique
     * @return L'équipement créé
     */
    public static Equipment createMysticCrown() {
        Equipment equipment = new Equipment(
            "Couronne Mystique",
            "Un ancien artefact des mages de Whisperwood. Amplifie les pouvoirs magiques et permet de drainer l'énergie vitale.",
            Equipment.EquipmentType.HELMET,
            Equipment.Rarity.LEGENDARY,
            40
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("magicalAttack", 60);
        equipment.addStatBonus("magicalDefense", 25);
        equipment.addStatBonus("maxHealth", 150);
        
        // Bonus de pourcentage
        equipment.addPercentBonus("healingBonus", 12.0f);
        equipment.addPercentBonus("shieldStrength", 15.0f);
        
        // Effet spécial: Drain Arcanique
        equipment.addSpecialEffect(new SpecialEffect("Drain Arcanique", "Les sorts ont 30% de chances de restaurer 5% des PV max") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                if (Math.random() < 0.3) {
                    int healing = (int)(wearer.getMaxHealth() * 0.05);
                    wearer.heal(healing);
                }
            }
        });
        
        // Chance d'infliger l'effet Maudit
        equipment.addStatusEffectChance(StatusEffect.CURSED, 10.0f);
        
        return equipment;
    }
    
    /**
     * Crée les bottes Semelles de l'Inventeur
     * @return L'équipement créé
     */
    public static Equipment createInventorBoots() {
        Equipment equipment = new Equipment(
            "Semelles de l'Inventeur",
            "Des bottes mécaniques créées par un génie de Fort Eisenberg. Augmente la vitesse et permet d'esquiver les pièges.",
            Equipment.EquipmentType.BOOTS,
            Equipment.Rarity.RARE,
            25
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("speed", 30);
        equipment.addStatBonus("evasion", 15);
        
        // Effet spécial: Propulsion
        equipment.addSpecialEffect(new SpecialEffect("Propulsion", "15% de chances d'agir deux fois dans un même tour") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                // L'effet sera géré par le système de combat
            }
        });
        
        return equipment;
    }
    
    /**
     * Crée l'amulette Pendentif de Sérénité
     * @return L'équipement créé
     */
    public static Equipment createSerenityAmulet() {
        Equipment equipment = new Equipment(
            "Pendentif de Sérénité",
            "Un talisman béni par Elder Mira du Temple de l'Aube. Apporte calme et guérison à son porteur.",
            Equipment.EquipmentType.ACCESSORY,
            Equipment.Rarity.EPIC,
            30
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("magicalDefense", 35);
        equipment.addStatBonus("maxHealth", 150);
        
        // Bonus de pourcentage
        equipment.addPercentBonus("healingBonus", 25.0f);
        equipment.addPercentBonus("shieldStrength", 15.0f);
        
        // Effet spécial: Aura Apaisante
        equipment.addSpecialEffect(new SpecialEffect("Aura Apaisante", "Soigne 3% des PV max à chaque tour et a 20% de chances de dissiper un effet de statut négatif") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                int healing = (int)(wearer.getMaxHealth() * 0.03);
                wearer.heal(healing);
                
                // La dissipation d'effet sera gérée par le système de combat
            }
        });
        
        return equipment;
    }
    
    /**
     * Crée la relique Fragment du Chaos
     * @return L'équipement créé
     */
    public static Equipment createChaosFragment() {
        Equipment equipment = new Equipment(
            "Fragment du Chaos",
            "Un éclat de pure énergie chaotique trouvé dans les Shadow Caverns. Ses effets sont imprévisibles mais puissants.",
            Equipment.EquipmentType.RELIC,
            Equipment.Rarity.MYTHIC,
            50
        );
        
        // Bonus aléatoires à chaque combat
        equipment.addSpecialEffect(new SpecialEffect("Essence Chaotique", "Confère des bonus aléatoires au début de chaque combat") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                // Choisir 3 stats aléatoires à améliorer
                String[] possibleStats = {"physicalAttack", "magicalAttack", "physicalDefense", 
                                         "magicalDefense", "speed", "maxHealth", "accuracy"};
                
                for (int i = 0; i < 3; i++) {
                    int statIndex = (int)(Math.random() * possibleStats.length);
                    String stat = possibleStats[statIndex];
                    int bonus = 10 + (int)(Math.random() * 40); // Bonus entre 10 et 50
                    
                    switch (stat) {
                        case "physicalAttack":
                            wearer.setPhysicalAttack(wearer.getPhysicalAttack() + bonus);
                            break;
                        case "magicalAttack":
                            wearer.setMagicalAttack(wearer.getMagicalAttack() + bonus);
                            break;
                        case "physicalDefense":
                            wearer.setPhysicalDefense(wearer.getPhysicalDefense() + bonus);
                            break;
                        case "magicalDefense":
                            wearer.setMagicalDefense(wearer.getMagicalDefense() + bonus);
                            break;
                        case "speed":
                            wearer.setSpeed(wearer.getSpeed() + bonus);
                            break;
                        case "maxHealth":
                            wearer.setMaxHealth(wearer.getMaxHealth() + bonus * 5);
                            break;
                        case "accuracy":
                            wearer.setAccuracy(wearer.getAccuracy() + bonus / 2);
                            break;
                    }
                }
            }
        });
        
        // Effet spécial: Déstabilisation
        equipment.addSpecialEffect(new SpecialEffect("Déstabilisation", "25% de chances d'infliger un effet de statut aléatoire à la cible") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                if (target != null && Math.random() < 0.25) {
                    StatusEffect[] effects = {
                        StatusEffect.CONFUSED, StatusEffect.DISORIENTED, StatusEffect.ENRAGED,
                        StatusEffect.CURSED, StatusEffect.UNSTABLE, StatusEffect.DAZED
                    };
                    
                    // Sélectionner un effet aléatoire et l'appliquer
                    // Note: L'application réelle de l'effet sera gérée par le système de combat
                    StatusEffect selectedEffect = effects[(int)(Math.random() * effects.length)];
                    System.out.println("Effet " + selectedEffect.getName() + " sélectionné pour être appliqué");
                }
            }
        });
        
        // Bonus de pourcentage aléatoires
        equipment.addPercentBonus("omnivamp", 8.0f);
        equipment.addPercentBonus("criticalChance", 10.0f);
        equipment.addPercentBonus("criticalDamage", 20.0f);
        
        return equipment;
    }
    
    /**
     * Crée l'arme Lame Ombreuse
     * @return L'équipement créé
     */
    public static Equipment createShadowBlade() {
        Equipment equipment = new Equipment(
            "Lame Ombreuse",
            "Une épée forgée dans les ténèbres des Shadow Caverns. Absorbe l'essence vitale des ennemis.",
            Equipment.EquipmentType.WEAPON,
            Equipment.Rarity.LEGENDARY,
            45
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("physicalAttack", 65);
        equipment.addStatBonus("magicalAttack", 30);
        
        // Bonus de pourcentage
        equipment.addPercentBonus("lifeSteal", 15.0f);
        equipment.addPercentBonus("armorPenetration", 25.0f);
        
        // Effet spécial: Frappe Ombreuse
        equipment.addSpecialEffect(new SpecialEffect("Frappe Ombreuse", "20% de chances d'infliger des dégâts supplémentaires égaux à 10% des PV max de la cible") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                if (target != null && Math.random() < 0.2) {
                    int damage = (int)(target.getMaxHealth() * 0.1);
                    target.takeTrueDamage(damage);
                }
            }
        });
        
        // Chance d'infliger l'effet DAZED
        equipment.addStatusEffectChance(StatusEffect.DAZED, 20.0f);
        
        return equipment;
    }
    
    /**
     * Crée le casque Diadème Lumineux
     * @return L'équipement créé
     */
    public static Equipment createLuminousDiadem() {
        Equipment equipment = new Equipment(
            "Diadème Lumineux",
            "Un artefact sacré du Temple of Dawn. Irradie une lumière purificatrice qui révèle la vérité.",
            Equipment.EquipmentType.HELMET,
            Equipment.Rarity.LEGENDARY,
            45
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("magicalAttack", 50);
        equipment.addStatBonus("magicalDefense", 40);
        equipment.addStatBonus("maxHealth", 200);
        
        // Bonus de pourcentage
        equipment.addPercentBonus("healingBonus", 20.0f);
        equipment.addPercentBonus("shieldStrength", 25.0f);
        
        // Effet spécial: Révélation
        equipment.addSpecialEffect(new SpecialEffect("Révélation", "Révèle les faiblesses des ennemis, augmentant les dégâts de 20% contre les types vulnérables") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                // Cet effet sera géré par le système de combat
            }
        });
        
        // Effet spécial: Purification
        equipment.addSpecialEffect(new SpecialEffect("Purification", "15% de chances de dissiper tous les effets de statut négatifs et de créer un bouclier adaptatif égal à 10% des PV max") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                if (Math.random() < 0.15) {
                    int shieldAmount = (int)(wearer.getMaxHealth() * 0.1);
                    wearer.addAdaptiveShield(shieldAmount);
                    // La dissipation des effets sera gérée par le système de combat
                }
            }
        });
        
        // Chance d'infliger l'effet ILLUMINATED
        equipment.addStatusEffectChance(StatusEffect.ILLUMINATED, 25.0f);
        
        return equipment;
    }
    
    /**
     * Crée l'armure Écorce Naturelle
     * @return L'équipement créé
     */
    public static Equipment createNaturalBark() {
        Equipment equipment = new Equipment(
            "Écorce Naturelle",
            "Une armure vivante tissée à partir des arbres millénaires de Whisperwood Forest. Se régénère avec le temps.",
            Equipment.EquipmentType.ARMOR,
            Equipment.Rarity.EPIC,
            35
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("physicalDefense", 45);
        equipment.addStatBonus("magicalDefense", 35);
        equipment.addStatBonus("maxHealth", 250);
        
        // Bonus de pourcentage
        equipment.addPercentBonus("healingBonus", 15.0f);
        
        // Effet spécial: Régénération Naturelle
        equipment.addSpecialEffect(new SpecialEffect("Régénération Naturelle", "Récupère 2% des PV max à chaque tour") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                int healing = (int)(wearer.getMaxHealth() * 0.02);
                wearer.heal(healing);
            }
        });
        
        // Effet spécial: Symbiose
        equipment.addSpecialEffect(new SpecialEffect("Symbiose", "Lorsque les PV tombent sous 30%, crée un bouclier adaptatif égal à 20% des PV max") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                int healthPercent = wearer.getCurrentHealth() * 100 / wearer.getMaxHealth();
                if (healthPercent < 30) {
                    int shieldAmount = (int)(wearer.getMaxHealth() * 0.2);
                    wearer.addAdaptiveShield(shieldAmount);
                }
            }
        });
        
        // Chance d'infliger l'effet ROOTED
        equipment.addStatusEffectChance(StatusEffect.ROOTED, 15.0f);
        
        return equipment;
    }
    
    /**
     * Crée l'accessoire Œil du Visionnaire
     * @return L'équipement créé
     */
    public static Equipment createVisionaryEye() {
        Equipment equipment = new Equipment(
            "Œil du Visionnaire",
            "Un cristal mystique qui permet de voir au-delà du temps. Offre des aperçus du futur proche.",
            Equipment.EquipmentType.ACCESSORY,
            Equipment.Rarity.MYTHIC,
            50
        );
        
        // Bonus de statistiques de base
        equipment.addStatBonus("accuracy", 30);
        equipment.addStatBonus("evasion", 20);
        equipment.addStatBonus("speed", 25);
        
        // Bonus de pourcentage
        equipment.addPercentBonus("criticalChance", 10.0f);
        
        // Effet spécial: Prescience
        equipment.addSpecialEffect(new SpecialEffect("Prescience", "Au début du combat, révèle l'ordre des actions pour les 3 premiers tours") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                // Cet effet sera géré par l'interface de combat
            }
        });
        
        // Effet spécial: Distorsion Temporelle
        equipment.addSpecialEffect(new SpecialEffect("Distorsion Temporelle", "10% de chances d'agir deux fois dans un même tour") {
            @Override
            public void activate(CombatStats wearer, CombatStats target) {
                // Cet effet sera géré par le système de combat
            }
        });
        
        // Chance d'infliger l'effet PREMONITORY
        equipment.addStatusEffectChance(StatusEffect.PREMONITORY, 20.0f);
        
        return equipment;
    }
    
    /**
     * Créer un équipement de type feu
     * 
     * @param type Type d'équipement
     * @param rarity Rareté de l'équipement
     * @param level Niveau de l'équipement
     * @return Équipement créé
     */
    public static Equipment createFireEquipment(Equipment.EquipmentType type, Equipment.Rarity rarity, int level) {
        String name;
        String description;
        Map<String, Integer> statBonuses = new HashMap<>();
        Map<String, Float> percentBonuses = new HashMap<>();
        
        switch (type) {
            case WEAPON:
                name = "Lame de flamme";
                description = "Une épée forgée dans les flammes éternelles.";
                statBonuses.put("attack", 5 + level);
                percentBonuses.put("fire_resistance", 0.1f + (level * 0.01f));
                break;
            case ARMOR:
                name = "Armure de magma";
                description = "Une armure qui brûle quiconque ose frapper son porteur.";
                statBonuses.put("defense", 3 + level);
                statBonuses.put("maxHealth", 10 + (level * 2));
                break;
            case ACCESSORY:
                name = "Bracelet de braise";
                description = "Un bracelet qui augmente la puissance des attaques de feu.";
                statBonuses.put("attack", 2 + (level / 2));
                percentBonuses.put("fire_damage", 0.15f + (level * 0.01f));
                break;
            case RELIC:
                name = "Amulette du phénix";
                description = "Une amulette qui protège son porteur des effets de statut.";
                statBonuses.put("maxHealth", 15 + (level * 3));
                break;
            default:
                name = "Relique de feu";
                description = "Un objet mystérieux imprégné de l'essence du feu.";
                statBonuses.put("attack", 1 + (level / 3));
                break;
        }
        
        // Créer l'équipement
        Equipment equipment = new Equipment(name, description, type, rarity, level);
        
        // Ajouter les bonus de statistiques
        for (Map.Entry<String, Integer> entry : statBonuses.entrySet()) {
            equipment.addStatBonus(entry.getKey(), entry.getValue());
        }
        
        // Ajouter les bonus de pourcentage
        for (Map.Entry<String, Float> entry : percentBonuses.entrySet()) {
            equipment.addPercentBonus(entry.getKey(), entry.getValue());
        }
        
        // Ajouter des effets spéciaux selon la rareté
        if (rarity == Equipment.Rarity.RARE || rarity == Equipment.Rarity.LEGENDARY) {
            equipment.addSpecialEffect(new HealthRegeneration(1 + (level / 5)));
        }
        
        return equipment;
    }
    
    /**
     * Effet spécial: Chance de paralyser
     */
    public static class ParalysisChance extends Equipment.SpecialEffect {
        private float chance;
        
        public ParalysisChance(float chance) {
            super("Paralysie", "A une chance de " + (int)(chance * 100) + "% de paralyser l'adversaire");
            this.chance = chance;
        }
        
        @Override
        public void activate(CombatStats wearer, CombatStats target) {
            // Chance de paralyser l'adversaire
            if (Math.random() < chance) {
                target.addStatusEffect(CombatStats.StatusEffect.PARALYSIS, 2);
            }
        }
    }
    
    /**
     * Régénération de santé
     */
    public static class HealthRegeneration extends Equipment.SpecialEffect {
        private int amount;

        public HealthRegeneration(int amount) {
            super("Régénération de Santé", "Régénère " + amount + " points de santé à chaque tour");
            this.amount = amount;
        }

        @Override
        public void activate(CombatStats wearer, CombatStats target) {
            // Régénérer la santé
            int currentHealth = wearer.getCurrentHealth();
            int maxHealth = wearer.getMaxHealth();
            int newHealth = Math.min(currentHealth + amount, maxHealth);
            wearer.setCurrentHealth(newHealth);
        }
    }
}

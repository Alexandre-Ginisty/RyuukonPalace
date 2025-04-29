package com.ryuukonpalace.game.mystical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ryuukonpalace.game.audio.AudioManager;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.utils.JsonLoader;
import com.ryuukonpalace.game.ui.UIManager;

/**
 * Système de gestion des signes mystiques.
 * Permet au joueur d'apprendre, d'utiliser et de pratiquer les signes mystiques.
 */
public class MysticalSignsSystem {
    // Singleton
    private static MysticalSignsSystem instance;
    
    // Données
    private JSONObject mysticalSignsData;
    private Map<String, JSONObject> signsById;
    private Map<String, List<JSONObject>> signsByCategory;
    
    // Signes connus par le joueur
    private Map<String, Map<String, Boolean>> playerKnownSigns; // playerId -> (signId -> known)
    private Map<String, Map<String, Integer>> playerSignMastery; // playerId -> (signId -> mastery level)
    
    // Temps de recharge des signes
    private Map<String, Map<String, Long>> signCooldowns; // playerId -> (signId -> cooldown end time)
    
    /**
     * Constructeur privé (singleton)
     */
    private MysticalSignsSystem() {
        this.signsById = new HashMap<>();
        this.signsByCategory = new HashMap<>();
        this.playerKnownSigns = new HashMap<>();
        this.playerSignMastery = new HashMap<>();
        this.signCooldowns = new HashMap<>();
        
        loadMysticalSignsData();
    }
    
    /**
     * Obtenir l'instance unique du système de signes mystiques
     * 
     * @return Instance de MysticalSignsSystem
     */
    public static MysticalSignsSystem getInstance() {
        if (instance == null) {
            instance = new MysticalSignsSystem();
        }
        return instance;
    }
    
    /**
     * Charger les données des signes mystiques
     */
    private void loadMysticalSignsData() {
        try {
            mysticalSignsData = JsonLoader.loadJsonObject("src/main/resources/data/mystical_signs.json");
            if (mysticalSignsData != null) {
                // Charger les catégories
                JSONArray categories = mysticalSignsData.getJSONArray("signs_categories");
                for (int i = 0; i < categories.length(); i++) {
                    JSONObject category = categories.getJSONObject(i);
                    signsByCategory.put(category.getString("id"), new ArrayList<>());
                }
                
                // Charger les signes
                JSONArray signs = mysticalSignsData.getJSONArray("mystical_signs");
                for (int i = 0; i < signs.length(); i++) {
                    JSONObject sign = signs.getJSONObject(i);
                    String signId = sign.getString("id");
                    String categoryId = sign.getString("category");
                    
                    signsById.put(signId, sign);
                    signsByCategory.get(categoryId).add(sign);
                }
                
                System.out.println("Données des signes mystiques chargées: " + signsById.size() + " signes dans " + signsByCategory.size() + " catégories");
            } else {
                System.err.println("Erreur: Impossible de charger les données des signes mystiques");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données des signes mystiques: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialiser les données des signes mystiques pour un joueur
     * 
     * @param player Joueur
     */
    public void initPlayerData(Player player) {
        String playerId = player.getId();
        
        // Initialiser les signes connus
        if (!playerKnownSigns.containsKey(playerId)) {
            playerKnownSigns.put(playerId, new HashMap<>());
            playerSignMastery.put(playerId, new HashMap<>());
            signCooldowns.put(playerId, new HashMap<>());
            
            // Par défaut, le joueur connaît quelques signes traditionnels de base
            for (JSONObject sign : signsByCategory.get("traditional")) {
                if (sign.getInt("quest_requirement") <= 100) { // Signes de base
                    playerKnownSigns.get(playerId).put(sign.getString("id"), true);
                    playerSignMastery.get(playerId).put(sign.getString("id"), 1); // Niveau de maîtrise initial
                }
            }
        }
    }
    
    /**
     * Vérifier si un joueur connaît un signe
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @return true si le joueur connaît le signe, false sinon
     */
    public boolean playerKnowsSign(Player player, String signId) {
        String playerId = player.getId();
        if (!playerKnownSigns.containsKey(playerId)) {
            initPlayerData(player);
        }
        
        Map<String, Boolean> knownSigns = playerKnownSigns.get(playerId);
        return knownSigns.containsKey(signId) && knownSigns.get(signId);
    }
    
    /**
     * Faire apprendre un signe à un joueur
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @return true si le joueur a appris le signe, false s'il le connaissait déjà
     */
    public boolean learnSign(Player player, String signId) {
        String playerId = player.getId();
        if (!playerKnownSigns.containsKey(playerId)) {
            initPlayerData(player);
        }
        
        Map<String, Boolean> knownSigns = playerKnownSigns.get(playerId);
        if (knownSigns.containsKey(signId) && knownSigns.get(signId)) {
            return false; // Le joueur connaît déjà ce signe
        }
        
        // Faire apprendre le signe au joueur
        knownSigns.put(signId, true);
        playerSignMastery.get(playerId).put(signId, 1); // Niveau de maîtrise initial
        
        return true;
    }
    
    /**
     * Obtenir le niveau de maîtrise d'un signe pour un joueur
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @return Niveau de maîtrise (0 si le joueur ne connaît pas le signe)
     */
    public int getSignMasteryLevel(Player player, String signId) {
        String playerId = player.getId();
        if (!playerSignMastery.containsKey(playerId)) {
            initPlayerData(player);
        }
        
        Map<String, Integer> masteryLevels = playerSignMastery.get(playerId);
        return masteryLevels.getOrDefault(signId, 0);
    }
    
    /**
     * Augmenter le niveau de maîtrise d'un signe pour un joueur
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @param amount Montant de l'augmentation
     * @return Nouveau niveau de maîtrise
     */
    public int increaseSignMastery(Player player, String signId, int amount) {
        String playerId = player.getId();
        if (!playerSignMastery.containsKey(playerId)) {
            initPlayerData(player);
        }
        
        Map<String, Integer> masteryLevels = playerSignMastery.get(playerId);
        int currentLevel = masteryLevels.getOrDefault(signId, 0);
        int newLevel = Math.min(10, currentLevel + amount); // Maximum de 10 niveaux
        
        masteryLevels.put(signId, newLevel);
        return newLevel;
    }
    
    /**
     * Vérifier si un signe est en temps de recharge pour un joueur
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @return true si le signe est en temps de recharge, false sinon
     */
    public boolean isSignOnCooldown(Player player, String signId) {
        String playerId = player.getId();
        if (!signCooldowns.containsKey(playerId)) {
            return false;
        }
        
        Map<String, Long> cooldowns = signCooldowns.get(playerId);
        if (!cooldowns.containsKey(signId)) {
            return false;
        }
        
        long cooldownEndTime = cooldowns.get(signId);
        return System.currentTimeMillis() < cooldownEndTime;
    }
    
    /**
     * Obtenir le temps de recharge restant pour un signe en secondes
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @return Temps de recharge restant en secondes (0 si le signe n'est pas en temps de recharge)
     */
    public int getRemainingCooldown(Player player, String signId) {
        String playerId = player.getId();
        if (!signCooldowns.containsKey(playerId)) {
            return 0;
        }
        
        Map<String, Long> cooldowns = signCooldowns.get(playerId);
        if (!cooldowns.containsKey(signId)) {
            return 0;
        }
        
        long cooldownEndTime = cooldowns.get(signId);
        long currentTime = System.currentTimeMillis();
        
        if (currentTime >= cooldownEndTime) {
            return 0;
        }
        
        return (int)((cooldownEndTime - currentTime) / 1000);
    }
    
    /**
     * Mettre un signe en temps de recharge pour un joueur
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @param cooldownSeconds Durée du temps de recharge en secondes
     */
    public void setSignOnCooldown(Player player, String signId, int cooldownSeconds) {
        String playerId = player.getId();
        if (!signCooldowns.containsKey(playerId)) {
            signCooldowns.put(playerId, new HashMap<>());
        }
        
        Map<String, Long> cooldowns = signCooldowns.get(playerId);
        long cooldownEndTime = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        cooldowns.put(signId, cooldownEndTime);
    }
    
    /**
     * Utiliser un signe mystique
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @return true si le signe a été utilisé avec succès, false sinon
     */
    public boolean useSign(Player player, String signId) {
        // Vérifier si le joueur connaît le signe
        if (!playerKnowsSign(player, signId)) {
            System.out.println("Le joueur ne connaît pas ce signe");
            return false;
        }
        
        // Vérifier si le signe est en temps de recharge
        if (isSignOnCooldown(player, signId)) {
            System.out.println("Le signe est en temps de recharge");
            return false;
        }
        
        // Obtenir les données du signe
        JSONObject sign = signsById.get(signId);
        if (sign == null) {
            System.out.println("Signe inconnu: " + signId);
            return false;
        }
        
        // Vérifier si le joueur a assez d'énergie
        String costStr = sign.getString("cost");
        int energyCost = parseEnergyCost(costStr);
        if (player.getEnergy() < energyCost) {
            System.out.println("Pas assez d'énergie pour utiliser ce signe");
            return false;
        }
        
        // Appliquer le coût
        player.useEnergy(energyCost);
        
        // Appliquer les effets du signe
        applySignEffects(player, sign);
        
        // Mettre le signe en temps de recharge
        String cooldownStr = sign.optString("cooldown", "0 secondes");
        int cooldownSeconds = parseCooldownTime(cooldownStr);
        setSignOnCooldown(player, signId, cooldownSeconds);
        
        // Augmenter légèrement la maîtrise du signe
        increaseSignMastery(player, signId, 1);
        
        return true;
    }
    
    /**
     * Pratiquer un signe mystique (augmente la maîtrise sans appliquer les effets)
     * 
     * @param player Joueur
     * @param signId ID du signe
     * @return true si le signe a été pratiqué avec succès, false sinon
     */
    public boolean practiceSign(Player player, String signId) {
        // Vérifier si le joueur connaît le signe
        if (!playerKnowsSign(player, signId)) {
            System.out.println("Le joueur ne connaît pas ce signe");
            return false;
        }
        
        // Obtenir les données du signe
        JSONObject sign = signsById.get(signId);
        if (sign == null) {
            System.out.println("Signe inconnu: " + signId);
            return false;
        }
        
        // Vérifier si le joueur a assez d'énergie (la pratique coûte moins que l'utilisation)
        String costStr = sign.getString("cost");
        int energyCost = parseEnergyCost(costStr) / 2; // La pratique coûte la moitié
        if (player.getEnergy() < energyCost) {
            System.out.println("Pas assez d'énergie pour pratiquer ce signe");
            return false;
        }
        
        // Appliquer le coût
        player.useEnergy(energyCost);
        
        // Augmenter la maîtrise du signe
        increaseSignMastery(player, signId, 2);
        
        return true;
    }
    
    /**
     * Analyser le coût en énergie d'un signe
     * 
     * @param costStr Chaîne de coût (ex: "30 points d'énergie")
     * @return Coût en énergie
     */
    private int parseEnergyCost(String costStr) {
        try {
            // Extraire le nombre du début de la chaîne
            String[] parts = costStr.split(" ");
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return 10; // Valeur par défaut en cas d'erreur
        }
    }
    
    /**
     * Analyser le temps de recharge d'un signe
     * 
     * @param cooldownStr Chaîne de temps de recharge (ex: "5 minutes", "24 heures")
     * @return Temps de recharge en secondes
     */
    private int parseCooldownTime(String cooldownStr) {
        try {
            String[] parts = cooldownStr.split(" ");
            int value = Integer.parseInt(parts[0]);
            String unit = parts[1].toLowerCase();
            
            switch (unit) {
                case "seconde":
                case "secondes":
                    return value;
                case "minute":
                case "minutes":
                    return value * 60;
                case "heure":
                case "heures":
                    return value * 3600;
                case "jour":
                case "jours":
                    return value * 86400;
                default:
                    return 60; // Valeur par défaut en cas d'unité inconnue
            }
        } catch (Exception e) {
            return 60; // Valeur par défaut en cas d'erreur
        }
    }
    
    /**
     * Appliquer les effets d'un signe
     * 
     * @param player Joueur
     * @param sign Données du signe
     */
    private void applySignEffects(Player player, JSONObject sign) {
        // Obtenir la catégorie du signe
        String category = sign.getString("category");
        
        // Appliquer les effets en fonction de la catégorie
        switch (category) {
            case "combat":
                applyCombatSignEffects(player, sign);
                break;
            case "spiritual":
                applySpiritualSignEffects(player, sign);
                break;
            case "forbidden":
                applyForbiddenSignEffects(player, sign);
                break;
            case "experimental":
                applyExperimentalSignEffects(player, sign);
                break;
            case "traditional":
                applyTraditionalSignEffects(player, sign);
                break;
            default:
                System.out.println("Catégorie de signe inconnue: " + category);
                break;
        }
        
        // Jouer un son en fonction de la catégorie
        String soundId = "mystical_" + category;
        AudioManager.getInstance().playSound(soundId);
    }
    
    /**
     * Appliquer les effets d'un signe de combat
     * 
     * @param player Joueur
     * @param sign Données du signe
     */
    private void applyCombatSignEffects(Player player, JSONObject sign) {
        String signId = sign.getString("id");
        String effect = sign.getString("effect");
        
        System.out.println("Application des effets du signe de combat: " + sign.getString("name"));
        
        // Effets spécifiques aux signes de combat
        if (signId.equals("combat_shield")) {
            // Ajouter un effet de protection au joueur
            player.addStatusEffect("protection", 5);
            System.out.println("Vous êtes protégé pendant 5 tours.");
        } else if (signId.equals("combat_strength")) {
            // Ajouter un effet de force au joueur
            player.addStatusEffect("strength", 3);
            System.out.println("Votre force est augmentée pendant 3 tours.");
        } else if (signId.equals("combat_speed")) {
            // Ajouter un effet de vitesse au joueur
            player.addStatusEffect("speed", 4);
            System.out.println("Votre vitesse est augmentée pendant 4 tours.");
        } else {
            // Effet générique basé sur la description
            if (effect.contains("feu") || effect.contains("flamme")) {
                player.addStatusEffect("fire_aura", 3);
                System.out.println("Une aura de feu vous entoure pendant 3 tours.");
            } else if (effect.contains("glace") || effect.contains("gel")) {
                player.addStatusEffect("ice_aura", 3);
                System.out.println("Une aura de glace vous entoure pendant 3 tours.");
            } else if (effect.contains("foudre") || effect.contains("éclair")) {
                player.addStatusEffect("lightning_aura", 3);
                System.out.println("Une aura électrique vous entoure pendant 3 tours.");
            }
        }
    }
    
    /**
     * Appliquer les effets d'un signe spirituel
     * 
     * @param player Joueur
     * @param sign Données du signe
     */
    private void applySpiritualSignEffects(Player player, JSONObject sign) {
        String signId = sign.getString("id");
        String effect = sign.getString("effect");
        
        System.out.println("Application des effets du signe spirituel: " + sign.getString("name"));
        
        // Effets spécifiques aux signes spirituels
        if (signId.equals("spiritual_healing")) {
            // Récupérer de l'énergie
            int healAmount = 20 + (int)(Math.random() * 10);
            player.recoverEnergy(healAmount);
            System.out.println("Vous récupérez " + healAmount + " points d'énergie.");
        } else if (signId.equals("spiritual_vision")) {
            // Ajouter un effet de vision au joueur
            player.addStatusEffect("enhanced_vision", 10);
            System.out.println("Votre vision est améliorée pendant 10 tours.");
        } else if (signId.equals("spiritual_calm")) {
            // Ajouter un effet de calme au joueur
            player.addStatusEffect("calm", 8);
            System.out.println("Votre esprit est apaisé pendant 8 tours.");
        } else {
            // Effet générique basé sur la description
            if (effect.contains("guérison") || effect.contains("soin")) {
                int healAmount = 10 + (int)(Math.random() * 10);
                player.recoverEnergy(healAmount);
                System.out.println("Vous récupérez " + healAmount + " points d'énergie.");
            } else if (effect.contains("méditation") || effect.contains("concentration")) {
                player.addStatusEffect("focus", 5);
                System.out.println("Votre concentration est améliorée pendant 5 tours.");
            } else if (effect.contains("protection") || effect.contains("barrière")) {
                player.addStatusEffect("spiritual_barrier", 4);
                System.out.println("Une barrière spirituelle vous protège pendant 4 tours.");
            }
        }
    }
    
    /**
     * Appliquer les effets d'un signe interdit
     * 
     * @param player Joueur
     * @param sign Données du signe
     */
    private void applyForbiddenSignEffects(Player player, JSONObject sign) {
        String signId = sign.getString("id");
        String effect = sign.getString("effect");
        String risks = sign.getString("risks");
        
        System.out.println("Application des effets du signe interdit: " + sign.getString("name"));
        
        // Les signes interdits ont des effets puissants mais aussi des risques
        boolean backfire = Math.random() < 0.2; // 20% de chance d'effet négatif
        
        if (!backfire) {
            // Effets positifs
            if (signId.equals("forbidden_power")) {
                // Ajouter un effet de puissance au joueur
                player.addStatusEffect("forbidden_power", 3);
                System.out.println("Une puissance interdite coule en vous pendant 3 tours.");
            } else if (signId.equals("forbidden_sight")) {
                // Ajouter un effet de vision interdite au joueur
                player.addStatusEffect("forbidden_sight", 5);
                System.out.println("Vos yeux peuvent voir au-delà des limites pendant 5 tours.");
            } else {
                // Effet générique basé sur la description
                if (effect.contains("temps") || effect.contains("ralentir")) {
                    player.addStatusEffect("time_manipulation", 2);
                    System.out.println("Vous manipulez le temps pendant 2 tours.");
                } else if (effect.contains("âme") || effect.contains("esprit")) {
                    player.addStatusEffect("soul_sight", 4);
                    System.out.println("Vous pouvez voir les âmes pendant 4 tours.");
                }
            }
        } else {
            // Effet négatif (backfire)
            System.out.println("Le signe interdit se retourne contre vous!");
            
            if (risks.contains("énergie") || risks.contains("épuisement")) {
                int energyLoss = 10 + (int)(Math.random() * 20);
                player.useEnergy(energyLoss);
                System.out.println("Vous perdez " + energyLoss + " points d'énergie.");
            } else if (risks.contains("confusion") || risks.contains("désorientation")) {
                player.addStatusEffect("confusion", 3);
                System.out.println("Vous êtes confus pendant 3 tours.");
            } else if (risks.contains("corruption") || risks.contains("ténèbres")) {
                player.addStatusEffect("corruption", 2);
                System.out.println("Vous êtes corrompu pendant 2 tours.");
            } else {
                // Effet négatif générique
                player.addStatusEffect("weakness", 4);
                System.out.println("Vous êtes affaibli pendant 4 tours.");
            }
        }
    }
    
    /**
     * Appliquer les effets d'un signe expérimental
     * 
     * @param player Joueur
     * @param sign Données du signe
     */
    private void applyExperimentalSignEffects(Player player, JSONObject sign) {
        String signId = sign.getString("id");
        String effect = sign.getString("effect");
        
        System.out.println("Application des effets du signe expérimental: " + sign.getString("name"));
        
        // Les signes expérimentaux ont des effets aléatoires
        double randomFactor = Math.random();
        
        if (randomFactor < 0.6) { // 60% de chance d'effet normal
            if (signId.equals("experimental_fusion")) {
                // Ajouter un effet de fusion élémentaire au joueur
                player.addStatusEffect("elemental_fusion", 4);
                System.out.println("Vos attaques sont imprégnées de fusion élémentaire pendant 4 tours.");
            } else if (signId.equals("experimental_phase")) {
                // Ajouter un effet de déphasage au joueur
                player.addStatusEffect("phase_shift", 3);
                System.out.println("Vous êtes partiellement déphasé pendant 3 tours.");
            } else {
                // Effet générique basé sur la description
                if (effect.contains("transformation") || effect.contains("métamorphose")) {
                    player.addStatusEffect("transformation", 2);
                    System.out.println("Vous pouvez vous transformer pendant 2 tours.");
                } else if (effect.contains("gravité") || effect.contains("lévitation")) {
                    player.addStatusEffect("gravity_control", 3);
                    System.out.println("Vous contrôlez la gravité pendant 3 tours.");
                }
            }
        } else if (randomFactor < 0.9) { // 30% de chance d'effet amélioré
            System.out.println("Le signe expérimental produit un effet amélioré!");
            
            if (signId.equals("experimental_fusion")) {
                // Ajouter un effet de fusion élémentaire amélioré au joueur
                player.addStatusEffect("enhanced_elemental_fusion", 6);
                System.out.println("Vos attaques sont imprégnées de fusion élémentaire améliorée pendant 6 tours.");
            } else if (signId.equals("experimental_phase")) {
                // Ajouter un effet de déphasage amélioré au joueur
                player.addStatusEffect("complete_phase_shift", 4);
                System.out.println("Vous êtes complètement déphasé pendant 4 tours.");
            } else {
                // Récupération d'énergie bonus
                int energyBonus = 20 + (int)(Math.random() * 20);
                player.recoverEnergy(energyBonus);
                System.out.println("Vous récupérez " + energyBonus + " points d'énergie bonus.");
            }
        } else { // 10% de chance d'effet instable
            System.out.println("Le signe expérimental est instable!");
            
            // Effet aléatoire
            int randomEffect = (int)(Math.random() * 4);
            switch (randomEffect) {
                case 0:
                    player.addStatusEffect("random_elemental", 3);
                    System.out.println("Vous êtes entouré d'une aura élémentaire aléatoire pendant 3 tours.");
                    break;
                case 1:
                    player.addStatusEffect("unstable_power", 2);
                    System.out.println("Vous êtes imprégné d'une puissance instable pendant 2 tours.");
                    break;
                case 2:
                    int energyChange = -10 + (int)(Math.random() * 40); // Entre -10 et +30
                    if (energyChange >= 0) {
                        player.recoverEnergy(energyChange);
                        System.out.println("Vous récupérez " + energyChange + " points d'énergie.");
                    } else {
                        player.useEnergy(-energyChange);
                        System.out.println("Vous perdez " + (-energyChange) + " points d'énergie.");
                    }
                    break;
                case 3:
                    player.addStatusEffect("dimensional_shift", 1);
                    System.out.println("Vous êtes brièvement déplacé dans une autre dimension pendant 1 tour.");
                    break;
            }
        }
    }
    
    /**
     * Appliquer les effets d'un signe traditionnel
     * 
     * @param player Joueur
     * @param sign Données du signe
     */
    private void applyTraditionalSignEffects(Player player, JSONObject sign) {
        String signId = sign.getString("id");
        String effect = sign.getString("effect");
        
        System.out.println("Application des effets du signe traditionnel: " + sign.getString("name"));
        
        // Les signes traditionnels ont des effets fiables et prévisibles
        if (signId.equals("traditional_fire")) {
            // Ajouter un effet de feu au joueur
            player.addStatusEffect("fire_attunement", 5);
            System.out.println("Vous êtes en harmonie avec le feu pendant 5 tours.");
        } else if (signId.equals("traditional_water")) {
            // Ajouter un effet d'eau au joueur
            player.addStatusEffect("water_attunement", 5);
            System.out.println("Vous êtes en harmonie avec l'eau pendant 5 tours.");
        } else if (signId.equals("traditional_earth")) {
            // Ajouter un effet de terre au joueur
            player.addStatusEffect("earth_attunement", 5);
            System.out.println("Vous êtes en harmonie avec la terre pendant 5 tours.");
        } else if (signId.equals("traditional_air")) {
            // Ajouter un effet d'air au joueur
            player.addStatusEffect("air_attunement", 5);
            System.out.println("Vous êtes en harmonie avec l'air pendant 5 tours.");
        } else if (signId.equals("traditional_light")) {
            // Ajouter un effet de lumière au joueur
            player.addStatusEffect("light_attunement", 5);
            System.out.println("Vous êtes en harmonie avec la lumière pendant 5 tours.");
        } else if (signId.equals("traditional_shadow")) {
            // Ajouter un effet d'ombre au joueur
            player.addStatusEffect("shadow_attunement", 5);
            System.out.println("Vous êtes en harmonie avec l'ombre pendant 5 tours.");
        } else {
            // Effet générique basé sur la description
            if (effect.contains("harmonie") || effect.contains("équilibre")) {
                player.addStatusEffect("harmony", 6);
                System.out.println("Vous êtes en harmonie avec les éléments pendant 6 tours.");
            } else if (effect.contains("protection") || effect.contains("défense")) {
                player.addStatusEffect("elemental_protection", 4);
                System.out.println("Vous êtes protégé contre les éléments pendant 4 tours.");
            } else if (effect.contains("perception") || effect.contains("sens")) {
                player.addStatusEffect("enhanced_perception", 7);
                System.out.println("Vos sens sont aiguisés pendant 7 tours.");
            }
        }
    }
    
    /**
     * Afficher l'interface des signes mystiques
     * 
     * @param player Joueur
     */
    public void showMysticalSignsInterface(Player player) {
        UIManager.getInstance().showMysticalSigns(player);
    }
    
    /**
     * Obtenir la liste des signes connus par un joueur
     * 
     * @param player Joueur
     * @return Liste des signes connus
     */
    public List<JSONObject> getKnownSigns(Player player) {
        String playerId = player.getId();
        if (!playerKnownSigns.containsKey(playerId)) {
            initPlayerData(player);
        }
        
        List<JSONObject> knownSigns = new ArrayList<>();
        Map<String, Boolean> knownSignsMap = playerKnownSigns.get(playerId);
        
        for (String signId : knownSignsMap.keySet()) {
            if (knownSignsMap.get(signId) && signsById.containsKey(signId)) {
                knownSigns.add(signsById.get(signId));
            }
        }
        
        return knownSigns;
    }
    
    /**
     * Obtenir la liste des signes d'une catégorie connus par un joueur
     * 
     * @param player Joueur
     * @param categoryId ID de la catégorie
     * @return Liste des signes connus dans cette catégorie
     */
    public List<JSONObject> getKnownSignsByCategory(Player player, String categoryId) {
        List<JSONObject> knownSigns = getKnownSigns(player);
        List<JSONObject> categorySigns = new ArrayList<>();
        
        for (JSONObject sign : knownSigns) {
            if (sign.getString("category").equals(categoryId)) {
                categorySigns.add(sign);
            }
        }
        
        return categorySigns;
    }
    
    /**
     * Obtenir toutes les catégories de signes
     * 
     * @return Liste des catégories
     */
    public JSONArray getSignCategories() {
        return mysticalSignsData.getJSONArray("signs_categories");
    }
    
    /**
     * Obtenir un signe par son ID
     * 
     * @param signId ID du signe
     * @return Données du signe, ou null s'il n'existe pas
     */
    public JSONObject getSignById(String signId) {
        return signsById.get(signId);
    }
}

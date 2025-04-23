package com.ryuukonpalace.game.creatures;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire des évolutions des créatures.
 * Définit et gère les chaînes d'évolution pour toutes les créatures du jeu.
 */
public class EvolutionManager {
    
    // Instance singleton
    private static EvolutionManager instance;
    
    // Map des évolutions par ID de créature
    private Map<Integer, Evolution> evolutionMap;
    
    // Map des chaînes d'évolution complètes par ID de créature de base
    private Map<Integer, List<Integer>> evolutionChains;
    
    /**
     * Constructeur privé pour le singleton
     */
    private EvolutionManager() {
        this.evolutionMap = new HashMap<>();
        this.evolutionChains = new HashMap<>();
        
        // Initialiser les évolutions
        initializeEvolutions();
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire d'évolutions
     * 
     * @return L'instance du EvolutionManager
     */
    public static EvolutionManager getInstance() {
        if (instance == null) {
            instance = new EvolutionManager();
        }
        return instance;
    }
    
    /**
     * Initialiser toutes les évolutions des créatures
     */
    private void initializeEvolutions() {
        // Définir les évolutions pour chaque créature
        // Format: registerEvolution(idCreatureBase, idCreatureEvoluee, niveauRequis, conditionsSpeciales)
        
        // Exemple d'évolution simple: Créature 1 -> Créature 2 au niveau 16
        registerEvolution(1, 2, 16, null);
        
        // Exemple d'évolution avec condition: Créature 2 -> Créature 3 au niveau 32 avec condition spéciale
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("item", "pierre_feu"); // Nécessite une pierre de feu
        registerEvolution(2, 3, 32, conditions);
        
        // Exemple d'évolution avec embranchement: Créature 4 peut évoluer en Créature 5 ou 6
        registerEvolution(4, 5, 20, null); // Évolution normale
        
        conditions = new HashMap<>();
        conditions.put("friendship", 220); // Nécessite un niveau d'amitié élevé
        registerEvolution(4, 6, 20, conditions); // Évolution alternative
        
        // Ajouter d'autres évolutions selon les besoins du jeu
        // ...
        
        // Construire les chaînes d'évolution
        buildEvolutionChains();
    }
    
    /**
     * Enregistrer une évolution
     * 
     * @param baseCreatureId ID de la créature de base
     * @param evolvedCreatureId ID de la créature évoluée
     * @param requiredLevel Niveau requis pour l'évolution
     * @param specialConditions Conditions spéciales pour l'évolution (peut être null)
     */
    public void registerEvolution(int baseCreatureId, int evolvedCreatureId, int requiredLevel, Map<String, Object> specialConditions) {
        // Créer l'objet Evolution
        Creature evolvedForm = CreatureFactory.createCreature(evolvedCreatureId);
        Evolution evolution = new Evolution(requiredLevel, evolvedForm);
        
        // Ajouter les conditions spéciales si présentes
        if (specialConditions != null) {
            for (Map.Entry<String, Object> entry : specialConditions.entrySet()) {
                evolution.addSpecialCondition(entry.getKey(), entry.getValue());
            }
        }
        
        // Enregistrer l'évolution
        evolutionMap.put(baseCreatureId, evolution);
    }
    
    /**
     * Construire les chaînes d'évolution complètes
     */
    private void buildEvolutionChains() {
        // Pour chaque créature ayant une évolution
        for (Integer creatureId : evolutionMap.keySet()) {
            // Si cette créature n'est pas déjà dans une chaîne d'évolution
            if (!isInEvolutionChain(creatureId)) {
                // Créer une nouvelle chaîne d'évolution
                List<Integer> chain = new ArrayList<>();
                
                // Ajouter la créature de base
                chain.add(creatureId);
                
                // Suivre la chaîne d'évolution
                Integer currentId = creatureId;
                while (evolutionMap.containsKey(currentId)) {
                    // Obtenir l'ID de la forme évoluée
                    Creature evolvedForm = evolutionMap.get(currentId).getEvolvedForm();
                    Integer evolvedId = evolvedForm.getId();
                    
                    // Ajouter à la chaîne
                    chain.add(evolvedId);
                    
                    // Passer à la prochaine évolution
                    currentId = evolvedId;
                }
                
                // Enregistrer la chaîne d'évolution
                evolutionChains.put(creatureId, chain);
            }
        }
    }
    
    /**
     * Vérifier si une créature est déjà dans une chaîne d'évolution
     * 
     * @param creatureId ID de la créature à vérifier
     * @return true si la créature est déjà dans une chaîne d'évolution, false sinon
     */
    private boolean isInEvolutionChain(int creatureId) {
        for (List<Integer> chain : evolutionChains.values()) {
            if (chain.contains(creatureId) && chain.get(0) != creatureId) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtenir l'évolution d'une créature
     * 
     * @param creatureId ID de la créature
     * @return L'évolution de la créature, ou null si elle n'en a pas
     */
    public Evolution getEvolution(int creatureId) {
        return evolutionMap.get(creatureId);
    }
    
    /**
     * Obtenir la chaîne d'évolution complète d'une créature
     * 
     * @param creatureId ID de la créature
     * @return La chaîne d'évolution complète, ou null si elle n'en a pas
     */
    public List<Integer> getEvolutionChain(int creatureId) {
        // Chercher la chaîne d'évolution directe
        if (evolutionChains.containsKey(creatureId)) {
            return evolutionChains.get(creatureId);
        }
        
        // Chercher dans quelle chaîne cette créature se trouve
        for (Map.Entry<Integer, List<Integer>> entry : evolutionChains.entrySet()) {
            List<Integer> chain = entry.getValue();
            if (chain.contains(creatureId)) {
                // Retourner la sous-chaîne à partir de cette créature
                int index = chain.indexOf(creatureId);
                return chain.subList(index, chain.size());
            }
        }
        
        return null;
    }
    
    /**
     * Vérifier si une créature peut évoluer
     * 
     * @param creature Créature à vérifier
     * @return true si la créature peut évoluer, false sinon
     */
    public boolean canEvolve(Creature creature) {
        // Obtenir l'évolution de la créature
        Evolution evolution = getEvolution(creature.getId());
        
        // Vérifier si la créature a une évolution
        if (evolution == null) {
            return false;
        }
        
        // Vérifier si le niveau est suffisant
        if (creature.getLevel() < evolution.getRequiredLevel()) {
            return false;
        }
        
        // Vérifier les conditions spéciales
        return evolution.checkSpecialConditions(creature);
    }
    
    /**
     * Faire évoluer une créature
     * 
     * @param creature Créature à faire évoluer
     * @return La créature évoluée, ou la même créature si elle ne peut pas évoluer
     */
    public Creature evolveCreature(Creature creature) {
        // Vérifier si la créature peut évoluer
        if (!canEvolve(creature)) {
            return creature;
        }
        
        // Obtenir l'évolution
        Evolution evolution = getEvolution(creature.getId());
        
        // Créer une nouvelle instance de la forme évoluée
        Creature evolvedForm = evolution.getEvolvedForm();
        
        // Transférer les statistiques importantes
        transferStats(creature, evolvedForm);
        
        return evolvedForm;
    }
    
    /**
     * Transférer les statistiques importantes d'une créature à sa forme évoluée
     * 
     * @param baseForm Forme de base
     * @param evolvedForm Forme évoluée
     */
    private void transferStats(Creature baseForm, Creature evolvedForm) {
        // Conserver le niveau
        // Note: La forme évoluée a déjà des stats de base plus élevées
        evolvedForm.setLevel(baseForm.getLevel());
        
        // Transférer l'expérience
        evolvedForm.setExperience(baseForm.getExperience());
        
        // Transférer les capacités apprises
        for (Ability ability : baseForm.getAbilities()) {
            evolvedForm.addAbility(ability);
        }
        
        // Transférer le niveau d'amitié
        evolvedForm.setFriendship(baseForm.getFriendship());
        
        // Autres transferts selon les besoins du jeu
        // ...
    }
}

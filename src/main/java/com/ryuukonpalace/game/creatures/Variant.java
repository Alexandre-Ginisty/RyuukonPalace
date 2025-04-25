package com.ryuukonpalace.game.creatures;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un variant, une créature capturée et entraînée par un Tacticien.
 * Les variants sont l'équivalent des Pokémon dans ce jeu.
 */
public class Variant extends Creature {
    // Niveau d'affinité avec le Tacticien (0-100)
    private int affinity;
    
    // Expérience actuelle
    private int experience;
    
    // Expérience nécessaire pour le prochain niveau
    private int experienceToNextLevel;
    
    // Date de capture
    private long captureDate;
    
    // Lieu de capture
    private String captureLocation;
    
    // Statistiques de combat (victoires, défaites)
    private int victories;
    private int defeats;
    
    /**
     * Constructeur pour un variant nouvellement capturé
     * 
     * @param baseCreature Créature de base
     * @param captureLocation Lieu de capture
     */
    public Variant(Creature baseCreature, String captureLocation) {
        super(baseCreature.getId(), baseCreature.getName(), baseCreature.getType(), baseCreature.getLevel(),
              baseCreature.getHealth(), baseCreature.getAttack(), baseCreature.getDefense(), baseCreature.getSpeed());
        
        // Copier les statistiques et capacités de la créature de base
        this.setCombatStats(baseCreature.getCombatStats());
        this.setAbilities(new ArrayList<>(baseCreature.getAbilities()));
        
        // Initialiser les valeurs spécifiques aux variants
        this.affinity = 50; // Affinité de départ moyenne
        this.experience = 0;
        this.calculateExperienceToNextLevel();
        this.captureDate = System.currentTimeMillis();
        this.captureLocation = captureLocation;
        this.victories = 0;
        this.defeats = 0;
    }
    
    /**
     * Calculer l'expérience nécessaire pour le prochain niveau
     */
    private void calculateExperienceToNextLevel() {
        // Formule simple: 100 * niveau actuel
        this.experienceToNextLevel = 100 * this.getLevel();
    }
    
    /**
     * Ajouter de l'expérience au variant
     * 
     * @param amount Quantité d'expérience à ajouter
     * @return true si le variant a gagné un niveau, false sinon
     */
    public boolean addExperience(int amount) {
        this.experience += amount;
        
        // Vérifier si le variant a gagné un niveau
        if (this.experience >= this.experienceToNextLevel) {
            levelUp();
            return true;
        }
        
        return false;
    }
    
    /**
     * Faire gagner un niveau au variant
     */
    private void levelUp() {
        // Augmenter le niveau
        this.setLevel(this.getLevel() + 1);
        
        // Réinitialiser l'expérience
        this.experience -= this.experienceToNextLevel;
        
        // Calculer l'expérience nécessaire pour le prochain niveau
        calculateExperienceToNextLevel();
        
        // Augmenter les statistiques
        this.getCombatStats().increaseWithLevel(this.getLevel());
        
        // Vérifier si le variant apprend de nouvelles capacités
        checkNewAbilities();
        
        // Vérifier si le variant évolue
        checkEvolution();
    }
    
    /**
     * Vérifier si le variant apprend de nouvelles capacités à ce niveau
     */
    private void checkNewAbilities() {
        // TODO: Implémenter l'apprentissage de nouvelles capacités
    }
    
    /**
     * Vérifier si le variant peut évoluer à ce niveau
     */
    private void checkEvolution() {
        // TODO: Implémenter l'évolution des variants
    }
    
    /**
     * Augmenter l'affinité avec le Tacticien
     * 
     * @param amount Quantité d'affinité à ajouter
     */
    public void increaseAffinity(int amount) {
        this.affinity = Math.min(100, this.affinity + amount);
    }
    
    /**
     * Diminuer l'affinité avec le Tacticien
     * 
     * @param amount Quantité d'affinité à retirer
     */
    public void decreaseAffinity(int amount) {
        this.affinity = Math.max(0, this.affinity - amount);
    }
    
    /**
     * Enregistrer une victoire pour ce variant
     */
    public void addVictory() {
        this.victories++;
        // Augmenter l'affinité lors d'une victoire
        increaseAffinity(2);
    }
    
    /**
     * Enregistrer une défaite pour ce variant
     */
    public void addDefeat() {
        this.defeats++;
        // Diminuer légèrement l'affinité lors d'une défaite
        decreaseAffinity(1);
    }
    
    /**
     * Définir les capacités du variant
     * 
     * @param abilities Liste des capacités
     */
    public void setAbilities(List<Ability> abilities) {
        // Remplacer les capacités existantes par les nouvelles
        super.getAbilities().clear();
        super.getAbilities().addAll(abilities);
    }
    
    // Getters et setters
    
    public int getAffinity() {
        return affinity;
    }
    
    public void setAffinity(int affinity) {
        this.affinity = Math.max(0, Math.min(100, affinity));
    }
    
    public int getExperience() {
        return experience;
    }
    
    public void setExperience(int experience) {
        this.experience = experience;
    }
    
    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    }
    
    public long getCaptureDate() {
        return captureDate;
    }
    
    public String getCaptureLocation() {
        return captureLocation;
    }
    
    public void setCaptureLocation(String captureLocation) {
        this.captureLocation = captureLocation;
    }
    
    public int getVictories() {
        return victories;
    }
    
    public void setVictories(int victories) {
        this.victories = victories;
    }
    
    public int getDefeats() {
        return defeats;
    }
    
    public void setDefeats(int defeats) {
        this.defeats = defeats;
    }
}

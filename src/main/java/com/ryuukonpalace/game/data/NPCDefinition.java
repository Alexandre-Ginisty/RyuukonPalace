package com.ryuukonpalace.game.data;

import java.util.List;

/**
 * Définition d'un PNJ (Personnage Non-Joueur) pour le chargement/sauvegarde des données.
 * Cette classe est utilisée pour la sérialisation/désérialisation JSON.
 */
public class NPCDefinition {
    // Identifiant unique du PNJ
    public int id;
    
    // Nom du PNJ
    public String name;
    
    // Rôle du PNJ (ex: Mentor, Commerçant, etc.)
    public String role;
    
    // Emplacement du PNJ dans le monde
    public String location;
    
    // Description physique du PNJ
    public String description;
    
    // Histoire/contexte du PNJ
    public String background;
    
    // Dialogues du PNJ
    public DialogueSet dialogue;
    
    // IDs des quêtes proposées par le PNJ
    public List<Integer> quests;
    
    // IDs des créatures possédées par le PNJ
    public List<Integer> creatures;
    
    // Objets vendus par le PNJ
    public List<String> items_for_sale;
    
    // Signes de pouvoir enseignés par le PNJ
    public List<String> teaches_signs;
    
    // Chemin vers la texture du PNJ
    public String texture;
    
    /**
     * Ensemble de dialogues pour différentes situations
     */
    public static class DialogueSet {
        // Salutations
        public List<String> greeting;
        
        // Offres de quêtes
        public List<String> quest_offer;
        
        // Dialogues commerciaux
        public List<String> business;
        
        // Dialogues d'entraînement
        public List<String> training;
        
        // Adieux
        public List<String> farewell;
    }
}

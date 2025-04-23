package com.ryuukonpalace.game.combat;

/**
 * Représente les différents effets de statut qui peuvent affecter les variants pendant le combat.
 * Chaque statut a des effets spécifiques sur les statistiques ou le comportement du variant.
 */
public enum StatusEffect {
    
    // Statuts basiques
    NONE("Aucun", "Aucun effet de statut", 0),
    CONFUS("Confus", "25% de chances d'attaquer au hasard ou de se blesser", 3),
    
    // Statuts liés au type Stratège
    DÉSORIENTÉ("Désorienté", "Précision réduite de 30%, peut attaquer ses alliés", 3),
    PRÉVISIBLE("Prévisible", "Esquive réduite de 30%, vulnérable aux coups critiques", 4),
    
    // Statuts liés au type Furieux
    ENRAGÉ("Enragé", "Attaque +30%, Défense -20%, ne peut pas fuir", 3),
    ÉPUISÉ("Épuisé", "Vitesse réduite de 40%, ne peut pas utiliser d'attaques puissantes", 4),
    
    // Statuts liés au type Mystique
    MAUDIT("Maudit", "Perd 10% de PV à chaque tour, capacités spéciales bloquées", 4),
    POSSÉDÉ("Possédé", "Contrôlé par l'adversaire pendant 1-3 tours", 2),
    
    // Statuts liés au type Inventeur
    SURCHARGÉ("Surchargé", "50% de chances d'infliger des dégâts supplémentaires, 20% de chances de s'autodétruire", 3),
    DYSFONCTIONNEL("Dysfonctionnel", "Capacités techniques désactivées, attaques aléatoires", 4),
    
    // Statuts liés au type Serein
    MÉDITATIF("Méditatif", "Ne peut pas attaquer mais récupère 15% de PV par tour", 3),
    DÉSÉQUILIBRÉ("Déséquilibré", "Alternance entre états de furie (+50% ATT) et de léthargie (-50% VIT)", 4),
    
    // Statuts liés au type Chaotique
    INSTABLE("Instable", "Statistiques aléatoirement modifiées à chaque tour (-30% à +30%)", 3),
    ENTROPIQUE("Entropique", "Attaques affectent aléatoirement amis, ennemis ou soi-même", 4),
    
    // Statuts liés au type Visionnaire
    PRÉMONITIF("Prémonitif", "Esquive la première attaque de chaque adversaire, mais -20% de précision", 3),
    TEMPORELLEMENT_DÉCALÉ("Temporellement Décalé", "Agit en dernier, mais voit les actions futures des adversaires", 4),
    
    // Statuts liés au type Ombreux
    ASSOMBRI("Assombri", "Invisible aux adversaires pendant 1-2 tours, mais -30% d'ATT", 3),
    CONSUMÉ("Consumé", "Absorbe 20% des dégâts infligés, mais perd 5% de PV max par tour", 4),
    
    // Statuts liés au type Lumineux
    ILLUMINÉ("Illuminé", "Révèle les faiblesses des adversaires, +20% aux coups critiques", 3),
    AVEUGLANT("Aveuglant", "Réduit la précision des adversaires de 25%, mais attire toutes les attaques", 4),
    
    // Statuts liés au type Naturel
    ENRACINÉ("Enraciné", "Ne peut pas bouger, mais récupère 10% PV et +30% DEF par tour", 3),
    SYMBIOTIQUE("Symbiotique", "Partage 50% des dégâts avec un allié, les deux gagnent +15% à toutes les stats", 4),
    
    // Statuts environnementaux (liés aux lieux d'Elderglen)
    GELÉ("Gelé", "Vitesse réduite de 50%, 20% de chances de sauter un tour (Frostpeak Mountains)", 3),
    ENCHANTÉ("Enchanté", "Effets de statut aléatoires à chaque tour (Whisperwood Forest)", 4),
    CORROMPU("Corrompu", "Perd 5% PV par tour, mais gagne +20% ATT et MAG (Ancient Ruins)", 4),
    PROTÉGÉ("Protégé", "Immunisé contre les effets de statut négatifs pendant 3 tours (Temple of Dawn)", 3);
    
    private final String name;
    private final String description;
    private final int maxDuration;
    
    StatusEffect(String name, String description, int maxDuration) {
        this.name = name;
        this.description = description;
        this.maxDuration = maxDuration;
    }
    
    /**
     * Obtenir le nom de l'effet de statut
     * @return Nom de l'effet
     */
    public String getName() {
        return name;
    }
    
    /**
     * Obtenir la description de l'effet de statut
     * @return Description de l'effet
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtenir la durée maximale de l'effet en nombre de tours
     * @return Durée maximale
     */
    public int getMaxDuration() {
        return maxDuration;
    }
}

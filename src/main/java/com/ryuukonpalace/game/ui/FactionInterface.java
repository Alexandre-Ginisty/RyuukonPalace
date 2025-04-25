package com.ryuukonpalace.game.ui;

import com.ryuukonpalace.game.faction.Faction;
import com.ryuukonpalace.game.faction.FactionManager;
import com.ryuukonpalace.game.faction.FactionReward;
import com.ryuukonpalace.game.faction.ReputationLevel;
import com.ryuukonpalace.game.utils.JsonLoader;
import org.json.JSONObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface utilisateur pour afficher et interagir avec les factions du jeu.
 */
public class FactionInterface {
    // Configuration de l'interface
    private JSONObject config;
    private Rectangle mainPanel;
    private Rectangle factionListPanel;
    // Ces panneaux seront utilisés dans une future mise à jour pour afficher plus d'informations
    @SuppressWarnings("unused")
    private Rectangle factionInfoPanel;
    @SuppressWarnings("unused")
    private Rectangle reputationPanel;
    private Rectangle rewardsPanel;
    private Map<String, Rectangle> buttons;
    
    // État de l'interface
    private boolean visible;
    private String selectedFactionId;
    private int rewardsScrollOffset;
    private FactionManager factionManager;
    
    // Constantes
    private static final int MAX_REWARDS_VISIBLE = 4;
    
    /**
     * Constructeur pour l'interface de faction.
     */
    public FactionInterface() {
        this.visible = false;
        this.rewardsScrollOffset = 0;
        this.buttons = new HashMap<>();
        this.factionManager = FactionManager.getInstance();
        loadConfig();
    }
    
    /**
     * Charge la configuration depuis le fichier JSON.
     */
    private void loadConfig() {
        try {
            config = JsonLoader.loadJsonFromFile("data/ui_faction_interface.json");
            
            // Charge les dimensions des panneaux
            JSONObject layout = config.getJSONObject("layout");
            mainPanel = parseRect(layout.getJSONObject("mainPanel"));
            factionListPanel = parseRect(layout.getJSONObject("factionListPanel"));
            factionInfoPanel = parseRect(layout.getJSONObject("factionInfoPanel"));
            reputationPanel = parseRect(layout.getJSONObject("reputationPanel"));
            rewardsPanel = parseRect(layout.getJSONObject("rewardsPanel"));
            
            // Charge les boutons
            JSONObject buttonsConfig = layout.getJSONObject("buttons");
            for (String key : buttonsConfig.keySet()) {
                buttons.put(key, parseRect(buttonsConfig.getJSONObject(key)));
            }
            
            // Sélectionne la première faction par défaut
            List<Faction> factions = factionManager.getAllFactions();
            if (!factions.isEmpty()) {
                selectedFactionId = factions.get(0).getId();
            }
            
            System.out.println("Configuration de l'interface de faction chargée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la configuration de l'interface de faction: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Analyse un rectangle à partir d'un objet JSON.
     */
    private Rectangle parseRect(JSONObject rectObj) {
        int x = parsePosition(rectObj.getString("x"), true);
        int y = parsePosition(rectObj.getString("y"), false);
        int width = parsePosition(rectObj.getString("width"), true);
        int height = parsePosition(rectObj.getString("height"), false);
        return new Rectangle(x, y, width, height);
    }
    
    /**
     * Analyse une position qui peut être en pixels ou en pourcentage.
     */
    private int parsePosition(String pos, boolean isHorizontal) {
        if (pos.endsWith("%")) {
            float percentage = Float.parseFloat(pos.substring(0, pos.length() - 1)) / 100f;
            // Supposons une résolution de base de 1280x720
            return Math.round(percentage * (isHorizontal ? 1280 : 720));
        } else {
            return Integer.parseInt(pos);
        }
    }
    
    /**
     * Affiche l'interface de faction.
     */
    public void show() {
        this.visible = true;
        System.out.println("Interface de faction affichée");
    }
    
    /**
     * Cache l'interface de faction.
     */
    public void hide() {
        this.visible = false;
        System.out.println("Interface de faction cachée");
    }
    
    /**
     * Bascule la visibilité de l'interface de faction.
     */
    public void toggle() {
        if (visible) {
            hide();
        } else {
            show();
        }
    }
    
    /**
     * Vérifie si un clic a été effectué sur l'interface et le traite.
     */
    public boolean handleClick(int x, int y) {
        if (!visible) {
            return false;
        }
        
        Point click = new Point(x, y);
        
        // Vérifie si le clic est sur le bouton de fermeture
        if (buttons.containsKey("close") && buttons.get("close").contains(click)) {
            hide();
            return true;
        }
        
        // Vérifie si le clic est sur la liste des factions
        if (factionListPanel.contains(click)) {
            handleFactionListClick(x, y);
            return true;
        }
        
        // Vérifie si le clic est sur les boutons de défilement des récompenses
        if (buttons.containsKey("scrollUp") && buttons.get("scrollUp").contains(click)) {
            scrollRewardsUp();
            return true;
        }
        
        if (buttons.containsKey("scrollDown") && buttons.get("scrollDown").contains(click)) {
            scrollRewardsDown();
            return true;
        }
        
        // Vérifie si le clic est sur une récompense
        if (rewardsPanel.contains(click)) {
            handleRewardClick(x, y);
            return true;
        }
        
        return mainPanel.contains(click);
    }
    
    /**
     * Gère un clic sur la liste des factions.
     */
    private void handleFactionListClick(int x, int y) {
        List<Faction> factions = factionManager.getAllFactions();
        int factionHeight = 50; // Hauteur estimée pour chaque faction dans la liste
        int index = (y - factionListPanel.y) / factionHeight;
        
        if (index >= 0 && index < factions.size()) {
            selectedFactionId = factions.get(index).getId();
            rewardsScrollOffset = 0; // Réinitialise le défilement des récompenses
            System.out.println("Faction sélectionnée: " + factions.get(index).getName());
        }
    }
    
    /**
     * Gère un clic sur une récompense.
     */
    private void handleRewardClick(int x, int y) {
        if (selectedFactionId == null) {
            return;
        }
        
        List<FactionReward> rewards = factionManager.getAvailableRewardsForFaction(selectedFactionId);
        int rewardHeight = 80; // Hauteur estimée pour chaque récompense
        int index = (y - rewardsPanel.y) / rewardHeight + rewardsScrollOffset;
        
        if (index >= 0 && index < rewards.size()) {
            FactionReward reward = rewards.get(index);
            if (!reward.isClaimed()) {
                reward.claim();
                System.out.println("Récompense réclamée: " + reward.getName());
            }
        }
    }
    
    /**
     * Fait défiler les récompenses vers le haut.
     */
    private void scrollRewardsUp() {
        if (rewardsScrollOffset > 0) {
            rewardsScrollOffset--;
        }
    }
    
    /**
     * Fait défiler les récompenses vers le bas.
     */
    private void scrollRewardsDown() {
        if (selectedFactionId == null) {
            return;
        }
        
        List<FactionReward> rewards = factionManager.getAvailableRewardsForFaction(selectedFactionId);
        if (rewardsScrollOffset < Math.max(0, rewards.size() - MAX_REWARDS_VISIBLE)) {
            rewardsScrollOffset++;
        }
    }
    
    /**
     * Rend l'interface de faction.
     */
    public void render() {
        if (!visible) {
            return;
        }
        
        // Dessine le panneau principal
        System.out.println("Rendu du panneau principal");
        
        // Dessine la liste des factions
        renderFactionList();
        
        // Dessine les informations de la faction sélectionnée
        if (selectedFactionId != null) {
            renderFactionInfo();
            renderReputationInfo();
            renderRewards();
        }
        
        // Dessine les boutons
        for (String key : buttons.keySet()) {
            Rectangle button = buttons.get(key);
            System.out.println("Rendu du bouton " + key + " à " + button.x + "," + button.y);
        }
    }
    
    /**
     * Rend la liste des factions.
     */
    private void renderFactionList() {
        List<Faction> factions = factionManager.getAllFactions();
        // La variable factionHeight sera utilisée pour calculer l'espacement dans une future mise à jour
        // où nous afficherons réellement les factions dans l'interface
        @SuppressWarnings("unused")
        int factionHeight = 50; // Hauteur estimée pour chaque faction dans la liste
        
        for (Faction faction : factions) {
            boolean isSelected = faction.getId().equals(selectedFactionId);
            
            System.out.println("Rendu de la faction " + faction.getName() + 
                              (isSelected ? " (sélectionnée)" : ""));
        }
    }
    
    /**
     * Rend les informations de la faction sélectionnée.
     */
    private void renderFactionInfo() {
        Faction faction = factionManager.getFaction(selectedFactionId);
        if (faction == null) {
            return;
        }
        
        System.out.println("Rendu des informations de la faction " + faction.getName());
        System.out.println("Description: " + faction.getDescription());
        System.out.println("Centres: " + String.join(", ", faction.getCenters()));
        System.out.println("Leaders: " + String.join(", ", faction.getLeaders()));
    }
    
    /**
     * Rend les informations de réputation.
     */
    private void renderReputationInfo() {
        Faction faction = factionManager.getFaction(selectedFactionId);
        if (faction == null) {
            return;
        }
        
        ReputationLevel currentLevel = faction.getCurrentReputationLevel();
        ReputationLevel nextLevel = faction.getNextReputationLevel();
        
        System.out.println("Rendu des informations de réputation");
        System.out.println("Réputation actuelle: " + faction.getPlayerReputation());
        
        if (currentLevel != null) {
            System.out.println("Niveau actuel: " + currentLevel.getName());
            System.out.println("Description: " + currentLevel.getDescription());
            
            System.out.println("Bénéfices:");
            for (String benefit : currentLevel.getBenefits()) {
                System.out.println("- " + benefit);
            }
            
            System.out.println("Restrictions:");
            for (String restriction : currentLevel.getRestrictions()) {
                System.out.println("- " + restriction);
            }
        }
        
        if (nextLevel != null) {
            System.out.println("Prochain niveau: " + nextLevel.getName());
            System.out.println("Seuil: " + nextLevel.getThreshold());
        }
    }
    
    /**
     * Rend les récompenses disponibles.
     */
    private void renderRewards() {
        if (selectedFactionId == null) {
            return;
        }
        
        List<FactionReward> rewards = factionManager.getAvailableRewardsForFaction(selectedFactionId);
        // La variable rewardHeight sera utilisée pour calculer l'espacement dans une future mise à jour
        // où nous afficherons réellement les récompenses dans l'interface
        @SuppressWarnings("unused")
        int rewardHeight = 80; // Hauteur estimée pour chaque récompense
        
        System.out.println("Rendu des récompenses (" + rewards.size() + " disponibles)");
        
        int end = Math.min(rewardsScrollOffset + MAX_REWARDS_VISIBLE, rewards.size());
        for (int i = rewardsScrollOffset; i < end; i++) {
            FactionReward reward = rewards.get(i);
            
            System.out.println("Rendu de la récompense " + reward.getName());
            System.out.println("Description: " + reward.getDescription());
            System.out.println("Type: " + reward.getType());
            System.out.println("Coût: " + reward.getCost());
        }
    }
    
    /**
     * Vérifie si l'interface de faction est visible.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Obtient la faction actuellement sélectionnée.
     */
    public Faction getSelectedFaction() {
        return factionManager.getFaction(selectedFactionId);
    }
}

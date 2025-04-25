package com.ryuukonpalace.game.ui;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.utils.JsonLoader;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Écran de statut des créatures pour afficher les informations détaillées sur un variant.
 * Implémente un système d'onglets pour naviguer entre différentes catégories d'informations.
 */
public class CreatureStatusScreen {
    private static final String CONFIG_PATH = "data/ui_creature_status.json";
    
    private JSONObject config;
    private Creature currentCreature;
    private String currentTab;
    private Map<String, UITab> tabs;
    private List<UIElement> commonElements;
    private Point position;
    private Dimension size;
    private boolean isVisible;
    
    /**
     * Constructeur pour l'écran de statut des créatures.
     */
    public CreatureStatusScreen() {
        this.tabs = new HashMap<>();
        this.commonElements = new ArrayList<>();
        this.isVisible = false;
        loadConfiguration();
        initializeTabs();
    }
    
    /**
     * Charge la configuration depuis le fichier JSON.
     */
    private void loadConfiguration() {
        try {
            this.config = JsonLoader.loadJsonObject(CONFIG_PATH);
            JSONObject screenConfig = config.getJSONObject("creatureStatusScreen");
            
            // Définir la taille et la position par défaut
            this.position = new Point(0, 0);
            this.size = new Dimension(600, 600);
            
            System.out.println("Configuration de l'écran de statut des créatures chargée avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la configuration de l'écran de statut des créatures: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialise les onglets à partir de la configuration.
     */
    private void initializeTabs() {
        try {
            JSONObject screenConfig = config.getJSONObject("creatureStatusScreen");
            org.json.JSONArray tabsConfig = screenConfig.getJSONArray("tabs");
            
            for (int i = 0; i < tabsConfig.length(); i++) {
                JSONObject tabConfig = tabsConfig.getJSONObject(i);
                String tabId = tabConfig.getString("id");
                String tabName = tabConfig.getString("name");
                String tabIcon = tabConfig.getString("icon");
                
                UITab tab = new UITab(tabId, tabName, tabIcon);
                
                // Ajouter les éléments de l'onglet
                org.json.JSONArray elementsConfig = tabConfig.getJSONArray("elements");
                for (int j = 0; j < elementsConfig.length(); j++) {
                    JSONObject elementConfig = elementsConfig.getJSONObject(j);
                    String elementType = elementConfig.getString("type");
                    JSONObject positionConfig = elementConfig.getJSONObject("position");
                    
                    Rectangle bounds = new Rectangle(
                        positionConfig.getInt("x"),
                        positionConfig.getInt("y"),
                        positionConfig.getInt("width"),
                        positionConfig.getInt("height")
                    );
                    
                    UIElement element = createUIElement(elementType, bounds, elementConfig);
                    tab.addElement(element);
                }
                
                tabs.put(tabId, tab);
            }
            
            // Définir l'onglet par défaut
            if (!tabs.isEmpty()) {
                currentTab = tabs.keySet().iterator().next();
            }
            
            System.out.println("Onglets initialisés avec succès: " + tabs.size() + " onglets créés.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des onglets: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crée un élément d'interface utilisateur en fonction du type.
     */
    private UIElement createUIElement(String type, Rectangle bounds, JSONObject config) {
        switch (type) {
            case "CREATURE_PORTRAIT":
                return new CreaturePortraitElement(bounds, config);
            case "NAME_DISPLAY":
                return new TextElement(bounds, config);
            case "TYPE_DISPLAY":
                return new TypeDisplayElement(bounds, config);
            case "LEVEL_DISPLAY":
                return new LevelDisplayElement(bounds, config);
            case "STATS_BASIC":
                return new StatsDisplayElement(bounds, config);
            case "STATS_ADVANCED":
                return new AdvancedStatsElement(bounds, config);
            case "EVOLUTION_INFO":
                return new EvolutionInfoElement(bounds, config);
            case "ABILITIES_LIST":
                return new AbilitiesListElement(bounds, config);
            case "ABILITY_DETAILS":
                return new AbilityDetailsElement(bounds, config);
            case "TYPE_EFFECTIVENESS":
                return new TypeEffectivenessElement(bounds, config);
            case "CAPTURE_INFO":
                return new CaptureInfoElement(bounds, config);
            case "BATTLE_HISTORY":
                return new BattleHistoryElement(bounds, config);
            case "EVOLUTION_HISTORY":
                return new EvolutionHistoryElement(bounds, config);
            case "FRIENDSHIP_METER":
                return new FriendshipMeterElement(bounds, config);
            default:
                System.err.println("Type d'élément inconnu: " + type);
                return new UIElement(bounds, config);
        }
    }
    
    /**
     * Affiche l'écran de statut pour une créature spécifique.
     */
    public void showForCreature(Creature creature) {
        this.currentCreature = creature;
        this.isVisible = true;
        
        // Mettre à jour tous les éléments avec les données de la créature
        for (UITab tab : tabs.values()) {
            for (UIElement element : tab.getElements()) {
                element.updateWithCreature(creature);
            }
        }
        
        System.out.println("Affichage de l'écran de statut pour: " + creature.getName());
    }
    
    /**
     * Change l'onglet actif.
     */
    public void switchToTab(String tabId) {
        if (tabs.containsKey(tabId)) {
            this.currentTab = tabId;
            System.out.println("Changement vers l'onglet: " + tabId);
        } else {
            System.err.println("Onglet non trouvé: " + tabId);
        }
    }
    
    /**
     * Ferme l'écran de statut.
     */
    public void close() {
        this.isVisible = false;
        System.out.println("Fermeture de l'écran de statut");
    }
    
    /**
     * Dessine l'écran de statut.
     */
    public void render(Graphics2D g) {
        if (!isVisible) return;
        
        // Dessiner le fond
        g.setColor(new Color(26, 26, 26, 230));
        g.fillRect(position.x, position.y, size.width, size.height);
        
        // Dessiner la bordure
        g.setColor(new Color(58, 46, 33));
        g.drawRect(position.x, position.y, size.width, size.height);
        
        // Dessiner les onglets
        drawTabs(g);
        
        // Dessiner les éléments de l'onglet actif
        if (currentTab != null && tabs.containsKey(currentTab)) {
            UITab activeTab = tabs.get(currentTab);
            for (UIElement element : activeTab.getElements()) {
                element.render(g);
            }
        }
        
        // Dessiner les éléments communs
        for (UIElement element : commonElements) {
            element.render(g);
        }
    }
    
    /**
     * Dessine les onglets en haut de l'écran.
     */
    private void drawTabs(Graphics2D g) {
        int tabWidth = 120;
        int tabHeight = 30;
        int x = position.x + 10;
        int y = position.y + 10;
        
        for (Map.Entry<String, UITab> entry : tabs.entrySet()) {
            String tabId = entry.getKey();
            UITab tab = entry.getValue();
            
            // Onglet actif ou inactif
            if (tabId.equals(currentTab)) {
                g.setColor(new Color(58, 46, 33));
            } else {
                g.setColor(new Color(40, 40, 40));
            }
            
            g.fillRect(x, y, tabWidth, tabHeight);
            g.setColor(Color.WHITE);
            g.drawRect(x, y, tabWidth, tabHeight);
            
            // Texte de l'onglet
            g.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(tab.getName());
            int textX = x + (tabWidth - textWidth) / 2;
            int textY = y + (tabHeight - fm.getHeight()) / 2 + fm.getAscent();
            
            g.drawString(tab.getName(), textX, textY);
            
            x += tabWidth + 5;
        }
    }
    
    /**
     * Gère les clics de souris pour l'interaction avec l'écran.
     */
    public void handleMouseClick(int x, int y) {
        if (!isVisible) return;
        
        // Vérifier les clics sur les onglets
        int tabWidth = 120;
        int tabHeight = 30;
        int tabX = position.x + 10;
        int tabY = position.y + 10;
        
        for (String tabId : tabs.keySet()) {
            Rectangle tabBounds = new Rectangle(tabX, tabY, tabWidth, tabHeight);
            if (tabBounds.contains(x, y)) {
                switchToTab(tabId);
                return;
            }
            tabX += tabWidth + 5;
        }
        
        // Vérifier les clics sur les éléments de l'onglet actif
        if (currentTab != null && tabs.containsKey(currentTab)) {
            UITab activeTab = tabs.get(currentTab);
            for (UIElement element : activeTab.getElements()) {
                if (element.getBounds().contains(x, y)) {
                    element.handleClick();
                    return;
                }
            }
        }
        
        // Vérifier les clics sur le bouton de fermeture
        // Supposons que le bouton de fermeture est en haut à droite
        Rectangle closeButtonBounds = new Rectangle(
            position.x + size.width - 40,
            position.y + 10,
            30,
            30
        );
        
        if (closeButtonBounds.contains(x, y)) {
            close();
        }
    }
    
    // Classes internes pour les éléments d'interface
    
    /**
     * Classe de base pour les onglets.
     */
    private class UITab {
        private String id;
        private String name;
        private String icon;
        private List<UIElement> elements;
        
        public UITab(String id, String name, String icon) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.elements = new ArrayList<>();
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getIcon() {
            return icon;
        }
        
        public List<UIElement> getElements() {
            return elements;
        }
        
        public void addElement(UIElement element) {
            elements.add(element);
        }
    }
    
    /**
     * Classe de base pour les éléments d'interface.
     */
    private class UIElement {
        protected Rectangle bounds;
        protected JSONObject config;
        
        public UIElement(Rectangle bounds, JSONObject config) {
            this.bounds = bounds;
            this.config = config;
        }
        
        public Rectangle getBounds() {
            return bounds;
        }
        
        public void updateWithCreature(Creature creature) {
            // À implémenter dans les sous-classes
        }
        
        public void render(Graphics2D g) {
            // À implémenter dans les sous-classes
        }
        
        public void handleClick() {
            // À implémenter dans les sous-classes
        }
    }
    
    // Implémentations spécifiques des éléments d'interface
    
    private class CreaturePortraitElement extends UIElement {
        private boolean animationEnabled;
        
        public CreaturePortraitElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
            this.animationEnabled = config.optBoolean("animationEnabled", false);
        }
        
        @Override
        public void render(Graphics2D g) {
            // Dessiner le cadre du portrait
            g.setColor(new Color(58, 46, 33));
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            
            // Si nous avons une créature, dessiner son portrait
            if (currentCreature != null) {
                // Ici, nous utiliserions l'image de la créature
                // Pour l'instant, dessiner un placeholder
                g.setColor(new Color(80, 80, 80));
                g.fillRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                String text = "Portrait de " + currentCreature.getName();
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textX = bounds.x + (bounds.width - textWidth) / 2;
                int textY = bounds.y + bounds.height / 2;
                
                g.drawString(text, textX, textY);
            }
        }
    }
    
    private class TextElement extends UIElement {
        private String font;
        private int fontSize;
        
        public TextElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
            this.font = config.optString("font", "main_bold");
            this.fontSize = config.optInt("fontSize", 24);
        }
        
        @Override
        public void render(Graphics2D g) {
            if (currentCreature != null) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, fontSize));
                g.drawString(currentCreature.getName(), bounds.x, bounds.y + 30);
            }
        }
    }
    
    // Implémentations des autres éléments d'interface
    // Ces classes seraient complétées avec les fonctionnalités spécifiques
    
    private class TypeDisplayElement extends UIElement {
        private boolean showIcons;
        
        public TypeDisplayElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
            this.showIcons = config.optBoolean("showIcons", true);
        }
        
        @Override
        public void render(Graphics2D g) {
            if (currentCreature != null) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.drawString("Type: " + currentCreature.getType(), bounds.x, bounds.y + 20);
            }
        }
    }
    
    private class LevelDisplayElement extends UIElement {
        private boolean showExperienceBar;
        
        public LevelDisplayElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
            this.showExperienceBar = config.optBoolean("showExperienceBar", true);
        }
        
        @Override
        public void render(Graphics2D g) {
            if (currentCreature != null) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.drawString("Niveau: " + currentCreature.getLevel(), bounds.x, bounds.y + 20);
                
                if (showExperienceBar) {
                    // Dessiner la barre d'expérience
                    int barWidth = bounds.width;
                    int barHeight = 10;
                    int barY = bounds.y + 25;
                    
                    // Fond de la barre
                    g.setColor(new Color(40, 40, 40));
                    g.fillRect(bounds.x, barY, barWidth, barHeight);
                    
                    // Expérience actuelle
                    float expRatio = currentCreature.getExperienceRatio();
                    int filledWidth = (int)(barWidth * expRatio);
                    g.setColor(new Color(0, 150, 255));
                    g.fillRect(bounds.x, barY, filledWidth, barHeight);
                    
                    // Bordure de la barre
                    g.setColor(Color.WHITE);
                    g.drawRect(bounds.x, barY, barWidth, barHeight);
                }
            }
        }
    }
    
    private class StatsDisplayElement extends UIElement {
        private String[] stats;
        private boolean showBars;
        private boolean showValues;
        
        public StatsDisplayElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
            
            org.json.JSONArray statsArray = config.optJSONArray("stats");
            if (statsArray != null) {
                stats = new String[statsArray.length()];
                for (int i = 0; i < statsArray.length(); i++) {
                    stats[i] = statsArray.getString(i);
                }
            } else {
                stats = new String[]{"HP", "ATTACK", "DEFENSE", "SPECIAL", "SPEED"};
            }
            
            this.showBars = config.optBoolean("showBars", true);
            this.showValues = config.optBoolean("showValues", true);
        }
        
        @Override
        public void render(Graphics2D g) {
            if (currentCreature == null) return;
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Statistiques", bounds.x, bounds.y + 20);
            
            int y = bounds.y + 40;
            int barWidth = 200;
            int barHeight = 15;
            int spacing = 30;
            
            for (String stat : stats) {
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString(stat, bounds.x, y + 12);
                
                if (showBars) {
                    // Dessiner le fond de la barre
                    g.setColor(new Color(40, 40, 40));
                    g.fillRect(bounds.x + 100, y, barWidth, barHeight);
                    
                    // Dessiner la valeur de la statistique
                    int statValue = getStatValue(stat);
                    int maxStatValue = 100; // Valeur maximale pour l'échelle
                    int filledWidth = (int)((float)statValue / maxStatValue * barWidth);
                    
                    // Couleur basée sur la valeur
                    Color statColor;
                    if (statValue < 30) {
                        statColor = new Color(255, 50, 50); // Rouge pour faible
                    } else if (statValue < 60) {
                        statColor = new Color(255, 255, 50); // Jaune pour moyen
                    } else {
                        statColor = new Color(50, 255, 50); // Vert pour élevé
                    }
                    
                    g.setColor(statColor);
                    g.fillRect(bounds.x + 100, y, filledWidth, barHeight);
                    
                    // Bordure de la barre
                    g.setColor(Color.WHITE);
                    g.drawRect(bounds.x + 100, y, barWidth, barHeight);
                }
                
                if (showValues) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.PLAIN, 14));
                    g.drawString(String.valueOf(getStatValue(stat)), bounds.x + 310, y + 12);
                }
                
                y += spacing;
            }
        }
        
        private int getStatValue(String stat) {
            switch (stat) {
                case "HP": return currentCreature.getHealth();
                case "ATTACK": return currentCreature.getAttack();
                case "DEFENSE": return currentCreature.getDefense();
                case "SPECIAL": return currentCreature.getCombatStats().getMagicalAttack();
                case "SPEED": return currentCreature.getSpeed();
                default: return 0;
            }
        }
    }
    
    // Implémentations des autres éléments (simplifiées pour l'exemple)
    
    private class AdvancedStatsElement extends UIElement {
        public AdvancedStatsElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Statistiques avancées", bounds.x, bounds.y + 20);
        }
    }
    
    private class EvolutionInfoElement extends UIElement {
        public EvolutionInfoElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Informations d'évolution", bounds.x, bounds.y + 20);
        }
    }
    
    private class AbilitiesListElement extends UIElement {
        public AbilitiesListElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Liste des capacités", bounds.x, bounds.y + 20);
        }
    }
    
    private class AbilityDetailsElement extends UIElement {
        public AbilityDetailsElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Détails de la capacité", bounds.x, bounds.y + 20);
        }
    }
    
    private class TypeEffectivenessElement extends UIElement {
        public TypeEffectivenessElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Efficacité des types", bounds.x, bounds.y + 20);
        }
    }
    
    private class CaptureInfoElement extends UIElement {
        public CaptureInfoElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Informations de capture", bounds.x, bounds.y + 20);
        }
    }
    
    private class BattleHistoryElement extends UIElement {
        public BattleHistoryElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Historique de combat", bounds.x, bounds.y + 20);
        }
    }
    
    private class EvolutionHistoryElement extends UIElement {
        public EvolutionHistoryElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Historique d'évolution", bounds.x, bounds.y + 20);
        }
    }
    
    private class FriendshipMeterElement extends UIElement {
        public FriendshipMeterElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Niveau d'amitié", bounds.x, bounds.y + 20);
        }
    }
}

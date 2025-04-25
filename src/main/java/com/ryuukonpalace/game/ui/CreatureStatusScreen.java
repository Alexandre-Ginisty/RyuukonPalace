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
            // Récupérer la configuration de l'écran pour une utilisation future
            JSONObject screenConfig = config.getJSONObject("creatureStatusScreen");
            
            // Définir la taille et la position par défaut à partir de la configuration
            if (screenConfig.has("position")) {
                JSONObject posConfig = screenConfig.getJSONObject("position");
                this.position = new Point(
                    posConfig.optInt("x", 0),
                    posConfig.optInt("y", 0)
                );
            } else {
                this.position = new Point(0, 0);
            }
            
            if (screenConfig.has("size")) {
                JSONObject sizeConfig = screenConfig.getJSONObject("size");
                this.size = new Dimension(
                    sizeConfig.optInt("width", 600),
                    sizeConfig.optInt("height", 600)
                );
            } else {
                this.size = new Dimension(600, 600);
            }
            
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
            case "creaturePortrait":
                return new CreaturePortraitElement(bounds, config);
            case "basicStats":
                return new StatsElement(bounds, config);
            case "textInfo":
                return new TextElement(bounds, config);
            case "typeDisplay":
                return new TypeDisplayElement(bounds, config);
            case "advancedStats":
                return new AdvancedStatsElement(bounds, config);
            case "evolutionInfo":
                return new EvolutionInfoElement(bounds, config);
            case "abilitiesList":
                return new AbilitiesListElement(bounds, config);
            case "abilityDetails":
                return new AbilityDetailsElement(bounds, config);
            case "typeEffectiveness":
                return new TypeEffectivenessElement(bounds, config);
            case "captureInfo":
                return new CaptureInfoElement(bounds, config);
            case "battleHistory":
                return new BattleHistoryElement(bounds, config);
            case "evolutionHistory":
                return new EvolutionHistoryElement(bounds, config);
            case "friendshipMeter":
                return new FriendshipMeterElement(bounds, config);
            default:
                System.err.println("Type d'élément inconnu: " + type);
                return new StatsElement(bounds, config); // Élément par défaut
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
        
        // Cette méthode est utilisée pour l'identification des onglets lors de la navigation
        @SuppressWarnings("unused")
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        // Cette méthode sera utilisée dans une future implémentation pour afficher des icônes d'onglet
        @SuppressWarnings("unused")
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
    private abstract class UIElement {
        protected Rectangle bounds;
        // Le champ config contient des paramètres de configuration qui seront utilisés
        // dans les implémentations futures pour personnaliser le rendu des éléments
        protected JSONObject config;
        
        public UIElement(Rectangle bounds, JSONObject config) {
            this.bounds = bounds;
            this.config = config;
        }
        
        public Rectangle getBounds() {
            return bounds;
        }
        
        public void updateWithCreature(Creature creature) {
            // À surcharger dans les sous-classes
        }
        
        public abstract void render(Graphics2D g);
        
        public void handleClick() {
            // À surcharger dans les sous-classes
        }
    }
    
    /**
     * Élément affichant les statistiques de base de la créature.
     */
    private class StatsElement extends UIElement {
        public StatsElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
        }
        
        @Override
        public void render(Graphics2D g) {
            if (currentCreature != null) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                
                int y = bounds.y + 20;
                g.drawString("Niveau: " + currentCreature.getLevel(), bounds.x, y);
                y += 20;
                g.drawString("PV: " + currentCreature.getCurrentHealth() + "/" + currentCreature.getMaxHealth(), bounds.x, y);
                y += 20;
                g.drawString("Attaque: " + currentCreature.getAttack(), bounds.x, y);
                y += 20;
                g.drawString("Défense: " + currentCreature.getDefense(), bounds.x, y);
                y += 20;
                g.drawString("Vitesse: " + currentCreature.getSpeed(), bounds.x, y);
            }
        }
    }
    
    private class CreaturePortraitElement extends UIElement {
        // Cette option permettra d'activer les animations du portrait dans une future mise à jour
        @SuppressWarnings("unused")
        private boolean animationEnabled;
        
        public CreaturePortraitElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
            this.animationEnabled = config.optBoolean("animationEnabled", true);
        }
        
        @Override
        public void render(Graphics2D g) {
            if (currentCreature != null) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("Portrait de " + currentCreature.getName(), bounds.x, bounds.y + 20);
                
                // Dans une future mise à jour, nous utiliserons animationEnabled pour
                // déterminer si le portrait doit être animé ou statique
            }
        }
    }
    
    private class TextElement extends UIElement {
        private String text;
        // Ce champ sera utilisé dans une future mise à jour pour déterminer le type de données à afficher
        @SuppressWarnings("unused")
        private String dataType;
        // Cette police sera utilisée dans une future mise à jour pour personnaliser l'affichage
        @SuppressWarnings("unused")
        private Font font;
        
        public TextElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
            this.text = config.optString("defaultText", "");
            this.dataType = config.optString("dataType", "");
            
            // Chargement de la police qui sera utilisée dans une future mise à jour
            String fontName = config.optString("fontName", "Arial");
            int fontSize = config.optInt("fontSize", 12);
            int fontStyle = config.optInt("fontStyle", Font.PLAIN);
            this.font = new Font(fontName, fontStyle, fontSize);
        }
        
        @Override
        public void updateWithCreature(Creature creature) {
            if (config.has("textKey")) {
                String key = config.getString("textKey");
                switch (key) {
                    case "name":
                        text = creature.getName();
                        break;
                    case "type":
                        text = "Type: " + creature.getType();
                        break;
                    case "level":
                        text = "Niveau: " + creature.getLevel();
                        break;
                    // Autres cas à ajouter selon les besoins
                }
            }
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setColor(Color.WHITE);
            // Dans une future mise à jour, nous utiliserons la police personnalisée
            // g.setFont(font);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(text, bounds.x, bounds.y + 15);
        }
    }
    
    private class TypeDisplayElement extends UIElement {
        // Cette option permettra d'afficher des icônes pour les types dans une future mise à jour
        @SuppressWarnings("unused")
        private boolean showIcons;
        
        public TypeDisplayElement(Rectangle bounds, JSONObject config) {
            super(bounds, config);
            this.showIcons = config.optBoolean("showIcons", true);
        }
        
        @Override
        public void render(Graphics2D g) {
            if (currentCreature != null) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString("Type: " + currentCreature.getType(), bounds.x, bounds.y + 20);
                
                // Dans une future mise à jour, nous utiliserons showIcons pour
                // déterminer si nous affichons des icônes ou juste du texte
            }
        }
    }
    
    // Note: Les classes LevelDisplayElement et StatsDisplayElement ont été supprimées
    // car elles ne sont plus utilisées. Leurs fonctionnalités ont été intégrées
    // dans la classe StatsElement qui affiche toutes les statistiques.
    
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

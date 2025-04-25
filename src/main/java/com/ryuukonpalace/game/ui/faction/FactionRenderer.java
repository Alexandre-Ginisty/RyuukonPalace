package com.ryuukonpalace.game.ui.faction;

import com.ryuukonpalace.game.faction.Faction;
import com.ryuukonpalace.game.faction.FactionReward;
import com.ryuukonpalace.game.faction.ReputationLevel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * Classe responsable du rendu graphique de l'interface de faction.
 */
public class FactionRenderer {
    // Constantes de style
    private static final Color BG_COLOR = new Color(20, 20, 40, 220);
    private static final Color PANEL_COLOR = new Color(30, 30, 60, 200);
    private static final Color HIGHLIGHT_COLOR = new Color(60, 60, 120, 200);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color TITLE_COLOR = new Color(255, 255, 200);
    private static final Color POSITIVE_COLOR = new Color(100, 200, 100);
    private static final Color NEGATIVE_COLOR = new Color(200, 100, 100);
    private static final Color NEUTRAL_COLOR = new Color(200, 200, 200);
    
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);
    
    // Dimensions et espacements
    private static final int FACTION_ITEM_HEIGHT = 50;
    private static final int REWARD_ITEM_HEIGHT = 80;
    private static final int PADDING = 10;
    private static final int LINE_HEIGHT = 20;
    
    // Images - commentées pour éviter les erreurs de lint puisqu'elles ne sont pas utilisées actuellement
    // private Map<String, BufferedImage> factionIcons;
    // private Map<String, BufferedImage> factionBanners;
    // private Map<String, BufferedImage> rewardIcons;
    
    /**
     * Constructeur du renderer de faction.
     */
    public FactionRenderer() {
        // Ici, on pourrait charger les images des factions et des récompenses
        // factionIcons = new HashMap<>(); // À implémenter: chargement des icônes
        // factionBanners = new HashMap<>(); // À implémenter: chargement des bannières
        // rewardIcons = new HashMap<>(); // À implémenter: chargement des icônes de récompenses
    }
    
    /**
     * Rend l'interface de faction complète.
     */
    public void render(Graphics2D g, Rectangle mainPanel, Rectangle factionListPanel, 
                      Rectangle factionInfoPanel, Rectangle reputationPanel, Rectangle rewardsPanel,
                      List<Faction> factions, Faction selectedFaction, 
                      List<FactionReward> rewards, int rewardsScrollOffset, Map<String, Rectangle> buttons) {
        // Dessine le fond principal
        drawPanel(g, mainPanel, BG_COLOR);
        
        // Dessine les panneaux
        drawPanel(g, factionListPanel, PANEL_COLOR);
        drawPanel(g, factionInfoPanel, PANEL_COLOR);
        drawPanel(g, reputationPanel, PANEL_COLOR);
        drawPanel(g, rewardsPanel, PANEL_COLOR);
        
        // Dessine les titres des panneaux
        g.setFont(TITLE_FONT);
        g.setColor(TITLE_COLOR);
        g.drawString("Factions", factionListPanel.x + PADDING, factionListPanel.y + 25);
        
        if (selectedFaction != null) {
            g.drawString(selectedFaction.getName(), factionInfoPanel.x + PADDING, factionInfoPanel.y + 25);
            g.drawString("Réputation", reputationPanel.x + PADDING, reputationPanel.y + 25);
            g.drawString("Récompenses", rewardsPanel.x + PADDING, rewardsPanel.y + 25);
            
            // Dessine les détails
            renderFactionList(g, factionListPanel, factions, selectedFaction);
            renderFactionInfo(g, factionInfoPanel, selectedFaction);
            renderReputationInfo(g, reputationPanel, selectedFaction);
            renderRewards(g, rewardsPanel, rewards, rewardsScrollOffset);
        } else {
            renderFactionList(g, factionListPanel, factions, null);
            
            // Message si aucune faction n'est sélectionnée
            g.setFont(NORMAL_FONT);
            g.setColor(TEXT_COLOR);
            g.drawString("Sélectionnez une faction pour voir ses détails", 
                        factionInfoPanel.x + PADDING, factionInfoPanel.y + 50);
        }
        
        // Dessine les boutons
        for (Map.Entry<String, Rectangle> entry : buttons.entrySet()) {
            drawButton(g, entry.getValue(), entry.getKey());
        }
    }
    
    /**
     * Dessine un panneau avec une couleur de fond.
     */
    private void drawPanel(Graphics2D g, Rectangle rect, Color color) {
        g.setColor(color);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
        
        // Bordure
        g.setColor(color.brighter());
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
    }
    
    /**
     * Dessine un bouton.
     */
    private void drawButton(Graphics2D g, Rectangle rect, String label) {
        g.setColor(PANEL_COLOR);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 5, 5);
        
        g.setColor(HIGHLIGHT_COLOR);
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 5, 5);
        
        g.setFont(NORMAL_FONT);
        g.setColor(TEXT_COLOR);
        
        // Centre le texte dans le bouton
        int textWidth = g.getFontMetrics().stringWidth(label);
        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + rect.height / 2 + 5;
        
        g.drawString(label, textX, textY);
    }
    
    /**
     * Rend la liste des factions.
     */
    private void renderFactionList(Graphics2D g, Rectangle panel, List<Faction> factions, Faction selected) {
        int y = panel.y + 40; // Commence après le titre
        
        g.setFont(NORMAL_FONT);
        
        for (Faction faction : factions) {
            boolean isSelected = selected != null && faction.getId().equals(selected.getId());
            Rectangle itemRect = new Rectangle(panel.x + 5, y, panel.width - 10, FACTION_ITEM_HEIGHT);
            
            // Fond de l'élément
            g.setColor(isSelected ? HIGHLIGHT_COLOR : PANEL_COLOR);
            g.fillRoundRect(itemRect.x, itemRect.y, itemRect.width, itemRect.height, 5, 5);
            
            // Bordure
            g.setColor(isSelected ? HIGHLIGHT_COLOR.brighter() : PANEL_COLOR.brighter());
            g.drawRoundRect(itemRect.x, itemRect.y, itemRect.width, itemRect.height, 5, 5);
            
            // Nom de la faction
            g.setColor(TEXT_COLOR);
            g.drawString(faction.getName(), itemRect.x + PADDING, itemRect.y + 30);
            
            y += FACTION_ITEM_HEIGHT + 5;
        }
    }
    
    /**
     * Rend les informations de la faction sélectionnée.
     */
    private void renderFactionInfo(Graphics2D g, Rectangle panel, Faction faction) {
        int y = panel.y + 40; // Commence après le titre
        
        // Bannière (si disponible)
        // Si on avait chargé les images, on pourrait faire:
        // BufferedImage banner = factionBanners.get(faction.getBanner());
        // if (banner != null) {
        //     g.drawImage(banner, panel.x + PADDING, y, panel.width - PADDING * 2, 80, null);
        //     y += 90;
        // }
        
        g.setFont(NORMAL_FONT);
        g.setColor(TEXT_COLOR);
        
        // Description
        String description = faction.getDescription();
        y = drawWrappedText(g, description, panel.x + PADDING, y, panel.width - PADDING * 2);
        y += LINE_HEIGHT;
        
        // Centres
        g.setFont(SUBTITLE_FONT);
        g.drawString("Centres", panel.x + PADDING, y);
        y += LINE_HEIGHT;
        
        g.setFont(NORMAL_FONT);
        for (String center : faction.getCenters()) {
            g.drawString("• " + center, panel.x + PADDING * 2, y);
            y += LINE_HEIGHT;
        }
        y += LINE_HEIGHT / 2;
        
        // Leaders
        g.setFont(SUBTITLE_FONT);
        g.drawString("Leaders", panel.x + PADDING, y);
        y += LINE_HEIGHT;
        
        g.setFont(NORMAL_FONT);
        for (String leader : faction.getLeaders()) {
            g.drawString("• " + leader, panel.x + PADDING * 2, y);
            y += LINE_HEIGHT;
        }
    }
    
    /**
     * Rend les informations de réputation.
     */
    private void renderReputationInfo(Graphics2D g, Rectangle panel, Faction faction) {
        int y = panel.y + 40; // Commence après le titre
        
        ReputationLevel currentLevel = faction.getCurrentReputationLevel();
        ReputationLevel nextLevel = faction.getNextReputationLevel();
        int reputation = faction.getPlayerReputation();
        
        g.setFont(SUBTITLE_FONT);
        g.setColor(TEXT_COLOR);
        
        // Réputation actuelle
        g.drawString("Réputation: " + reputation, panel.x + PADDING, y);
        y += LINE_HEIGHT * 1.5;
        
        if (currentLevel != null) {
            // Niveau actuel
            g.setFont(SUBTITLE_FONT);
            g.drawString("Niveau actuel: " + currentLevel.getName(), panel.x + PADDING, y);
            y += LINE_HEIGHT;
            
            // Couleur selon le type de réputation
            switch (currentLevel.getType()) {
                case POSITIVE:
                    g.setColor(POSITIVE_COLOR);
                    break;
                case NEGATIVE:
                    g.setColor(NEGATIVE_COLOR);
                    break;
                default:
                    g.setColor(NEUTRAL_COLOR);
            }
            
            // Barre de réputation
            int barWidth = panel.width - PADDING * 2;
            int barHeight = 20;
            g.fillRect(panel.x + PADDING, y, barWidth, barHeight);
            y += barHeight + LINE_HEIGHT;
            
            g.setColor(TEXT_COLOR);
            g.setFont(NORMAL_FONT);
            
            // Description du niveau
            y = drawWrappedText(g, currentLevel.getDescription(), panel.x + PADDING, y, panel.width - PADDING * 2);
            y += LINE_HEIGHT;
            
            // Bénéfices
            if (!currentLevel.getBenefits().isEmpty()) {
                g.setFont(SUBTITLE_FONT);
                g.drawString("Bénéfices:", panel.x + PADDING, y);
                y += LINE_HEIGHT;
                
                g.setFont(SMALL_FONT);
                g.setColor(POSITIVE_COLOR);
                for (String benefit : currentLevel.getBenefits()) {
                    g.drawString("+ " + benefit, panel.x + PADDING * 2, y);
                    y += LINE_HEIGHT;
                }
                y += LINE_HEIGHT / 2;
            }
            
            // Restrictions
            if (!currentLevel.getRestrictions().isEmpty()) {
                g.setFont(SUBTITLE_FONT);
                g.setColor(TEXT_COLOR);
                g.drawString("Restrictions:", panel.x + PADDING, y);
                y += LINE_HEIGHT;
                
                g.setFont(SMALL_FONT);
                g.setColor(NEGATIVE_COLOR);
                for (String restriction : currentLevel.getRestrictions()) {
                    g.drawString("- " + restriction, panel.x + PADDING * 2, y);
                    y += LINE_HEIGHT;
                }
                y += LINE_HEIGHT / 2;
            }
        }
        
        // Prochain niveau
        if (nextLevel != null) {
            g.setFont(SUBTITLE_FONT);
            g.setColor(TEXT_COLOR);
            g.drawString("Prochain niveau: " + nextLevel.getName(), panel.x + PADDING, y);
            y += LINE_HEIGHT;
            
            g.setFont(NORMAL_FONT);
            g.drawString("Seuil: " + nextLevel.getThreshold() + " points", panel.x + PADDING * 2, y);
        }
    }
    
    /**
     * Rend les récompenses disponibles.
     */
    private void renderRewards(Graphics2D g, Rectangle panel, List<FactionReward> rewards, int scrollOffset) {
        int y = panel.y + 40; // Commence après le titre
        
        g.setFont(NORMAL_FONT);
        g.setColor(TEXT_COLOR);
        
        if (rewards.isEmpty()) {
            g.drawString("Aucune récompense disponible", panel.x + PADDING, y + 30);
            return;
        }
        
        int maxVisible = Math.min(4, rewards.size() - scrollOffset);
        for (int i = 0; i < maxVisible; i++) {
            int index = i + scrollOffset;
            if (index >= rewards.size()) {
                break;
            }
            
            FactionReward reward = rewards.get(index);
            Rectangle itemRect = new Rectangle(panel.x + 5, y, panel.width - 10, REWARD_ITEM_HEIGHT);
            
            // Fond de l'élément
            g.setColor(PANEL_COLOR);
            g.fillRoundRect(itemRect.x, itemRect.y, itemRect.width, itemRect.height, 5, 5);
            
            // Bordure
            g.setColor(PANEL_COLOR.brighter());
            g.drawRoundRect(itemRect.x, itemRect.y, itemRect.width, itemRect.height, 5, 5);
            
            // Icône (si disponible)
            // Si on avait chargé les images, on pourrait faire:
            // BufferedImage icon = rewardIcons.get(reward.getIcon());
            // if (icon != null) {
            //     g.drawImage(icon, itemRect.x + PADDING, itemRect.y + 10, 60, 60, null);
            // }
            
            // Nom de la récompense
            g.setFont(SUBTITLE_FONT);
            g.setColor(TITLE_COLOR);
            g.drawString(reward.getName(), itemRect.x + PADDING + 70, itemRect.y + 25);
            
            // Type et coût
            g.setFont(SMALL_FONT);
            g.setColor(TEXT_COLOR);
            g.drawString("Type: " + reward.getType() + " | Coût: " + reward.getCost(), 
                        itemRect.x + PADDING + 70, itemRect.y + 45);
            
            // Description
            g.setFont(SMALL_FONT);
            g.drawString(reward.getDescription(), itemRect.x + PADDING + 70, itemRect.y + 65);
            
            y += REWARD_ITEM_HEIGHT + 5;
        }
        
        // Indicateurs de défilement
        if (scrollOffset > 0) {
            g.setColor(HIGHLIGHT_COLOR);
            int[] xPoints = {panel.x + panel.width / 2, panel.x + panel.width / 2 - 10, panel.x + panel.width / 2 + 10};
            int[] yPoints = {panel.y + 30, panel.y + 40, panel.y + 40};
            g.fillPolygon(xPoints, yPoints, 3);
        }
        
        if (scrollOffset + maxVisible < rewards.size()) {
            g.setColor(HIGHLIGHT_COLOR);
            int baseY = panel.y + panel.height - 20;
            int[] xPoints = {panel.x + panel.width / 2, panel.x + panel.width / 2 - 10, panel.x + panel.width / 2 + 10};
            int[] yPoints = {baseY + 10, baseY, baseY};
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }
    
    /**
     * Dessine un texte avec retour à la ligne automatique.
     * @return La nouvelle position Y après le texte
     */
    private int drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return y;
        }
        
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;
        
        for (String word : words) {
            String testLine = line.toString() + (line.length() > 0 ? " " : "") + word;
            int width = g.getFontMetrics().stringWidth(testLine);
            
            if (width <= maxWidth) {
                line.append(line.length() > 0 ? " " : "").append(word);
            } else {
                g.drawString(line.toString(), x, currentY);
                currentY += LINE_HEIGHT;
                line = new StringBuilder(word);
            }
        }
        
        if (line.length() > 0) {
            g.drawString(line.toString(), x, currentY);
            currentY += LINE_HEIGHT;
        }
        
        return currentY;
    }
}

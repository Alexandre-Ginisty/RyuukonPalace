package com.ryuukonpalace.game.ui;

import com.ryuukonpalace.game.utils.JsonLoader;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Système de dialogue pour Ryuukon Palace.
 * Gère l'affichage des dialogues, les choix, les émotions et l'historique des conversations.
 */
public class DialogueSystem {
    private static final String CONFIG_PATH = "data/ui_dialogue_system.json";
    
    private JSONObject config;
    private Rectangle dialogueBoxBounds;
    private Rectangle speakerDisplayBounds;
    private Rectangle portraitDisplayBounds;
    private String currentDialogue;
    private String currentSpeaker;
    private String currentPortrait;
    private String currentEmotion;
    private List<String> dialogueHistory;
    private List<DialogueChoice> currentChoices;
    private int currentCharIndex;
    private long lastCharTime;
    private int textSpeed;
    private boolean isActive;
    private boolean isWaitingForInput;
    private boolean isAutoMode;
    private Map<String, Color> emotionColors;
    
    /**
     * Constructeur pour le système de dialogue.
     */
    public DialogueSystem() {
        this.dialogueHistory = new ArrayList<>();
        this.currentChoices = new ArrayList<>();
        this.isActive = false;
        this.isWaitingForInput = false;
        this.isAutoMode = false;
        this.emotionColors = new HashMap<>();
        loadConfiguration();
    }
    
    /**
     * Charge la configuration depuis le fichier JSON.
     */
    private void loadConfiguration() {
        try {
            this.config = JsonLoader.loadJsonObject(CONFIG_PATH);
            JSONObject systemConfig = config.getJSONObject("dialogueSystem");
            
            // Configurer les zones d'affichage
            JSONObject dialogueBoxConfig = systemConfig.getJSONObject("dialogueBox");
            JSONObject positionConfig = dialogueBoxConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            // Calculer les positions en pourcentage ou en pixels
            int x = parsePosition(positionConfig.getString("x"), screenWidth);
            int y = parsePosition(positionConfig.getString("y"), screenHeight);
            int width = parsePosition(positionConfig.getString("width"), screenWidth);
            int height = parsePosition(positionConfig.getString("height"), screenWidth);
            
            this.dialogueBoxBounds = new Rectangle(x, y, width, height);
            
            // Configurer l'affichage du locuteur
            JSONObject speakerConfig = systemConfig.getJSONObject("speakerDisplay");
            JSONObject speakerPosConfig = speakerConfig.getJSONObject("position");
            
            x = parsePosition(speakerPosConfig.getString("x"), screenWidth);
            y = parsePosition(speakerPosConfig.getString("y"), screenHeight);
            width = parsePosition(speakerPosConfig.getString("width"), screenWidth);
            height = parsePosition(speakerPosConfig.getString("height"), screenWidth);
            
            this.speakerDisplayBounds = new Rectangle(x, y, width, height);
            
            // Configurer l'affichage du portrait
            if (systemConfig.getJSONObject("portraitDisplay").getBoolean("enabled")) {
                JSONObject portraitConfig = systemConfig.getJSONObject("portraitDisplay");
                JSONObject portraitPosConfig = portraitConfig.getJSONObject("position");
                
                x = parsePosition(portraitPosConfig.getString("x"), screenWidth);
                y = parsePosition(portraitPosConfig.getString("y"), screenHeight);
                width = parsePosition(portraitPosConfig.getString("width"), screenWidth);
                height = parsePosition(portraitPosConfig.getString("height"), screenWidth);
                
                this.portraitDisplayBounds = new Rectangle(x, y, width, height);
            }
            
            // Configurer la vitesse du texte
            this.textSpeed = systemConfig.getJSONObject("textDisplay").getInt("textSpeed");
            
            // Configurer les couleurs d'émotion
            if (systemConfig.has("emotionSystem") && systemConfig.getJSONObject("emotionSystem").getBoolean("enabled")) {
                JSONObject emotionSystem = systemConfig.getJSONObject("emotionSystem");
                if (emotionSystem.has("textEffects")) {
                    JSONObject textEffects = emotionSystem.getJSONObject("textEffects");
                    for (String emotion : textEffects.keySet()) {
                        JSONObject effectConfig = textEffects.getJSONObject(emotion);
                        if (effectConfig.has("color")) {
                            String colorHex = effectConfig.getString("color");
                            emotionColors.put(emotion, Color.decode(colorHex));
                        }
                    }
                }
            }
            
            System.out.println("Configuration du système de dialogue chargée avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la configuration du système de dialogue: " + e.getMessage());
            e.printStackTrace();
            
            // Valeurs par défaut en cas d'erreur
            this.dialogueBoxBounds = new Rectangle(100, 500, 1080, 180);
            this.speakerDisplayBounds = new Rectangle(100, 470, 300, 30);
            this.portraitDisplayBounds = new Rectangle(50, 300, 200, 200);
            this.textSpeed = 30;
        }
    }
    
    /**
     * Parse une position qui peut être en pourcentage ou en pixels.
     */
    private int parsePosition(String position, int dimension) {
        if (position.endsWith("%")) {
            float percentage = Float.parseFloat(position.substring(0, position.length() - 1)) / 100f;
            return (int)(dimension * percentage);
        } else {
            return Integer.parseInt(position);
        }
    }
    
    /**
     * Démarre un nouveau dialogue.
     */
    public void startDialogue(String speaker, String dialogue, String portrait, String emotion) {
        this.currentSpeaker = speaker;
        this.currentDialogue = dialogue;
        this.currentPortrait = portrait;
        this.currentEmotion = emotion;
        this.currentCharIndex = 0;
        this.lastCharTime = System.currentTimeMillis();
        this.isActive = true;
        this.isWaitingForInput = false;
        
        // Ajouter au début de l'historique
        this.dialogueHistory.add(0, speaker + ": " + dialogue);
        if (this.dialogueHistory.size() > 50) {
            this.dialogueHistory.remove(this.dialogueHistory.size() - 1);
        }
        
        System.out.println("Dialogue démarré: " + speaker + " - " + dialogue);
    }
    
    /**
     * Ajoute des choix au dialogue actuel.
     */
    public void setChoices(List<DialogueChoice> choices) {
        this.currentChoices = choices;
        this.isWaitingForInput = true;
        System.out.println("Choix ajoutés au dialogue: " + choices.size() + " options");
    }
    
    /**
     * Met à jour l'affichage du texte caractère par caractère.
     */
    public void update() {
        if (!isActive) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentCharIndex < currentDialogue.length() && currentTime - lastCharTime > 1000 / textSpeed) {
            currentCharIndex++;
            lastCharTime = currentTime;
            
            // Jouer un son de texte si configuré
            if (config.getJSONObject("dialogueSystem").getJSONObject("specialEffects").getJSONObject("textReveal").getBoolean("enabled")) {
                // Jouer le son ici
            }
        }
        
        // Si tout le texte est affiché
        if (currentCharIndex >= currentDialogue.length() && !isWaitingForInput && !isAutoMode) {
            isWaitingForInput = true;
        }
        
        // Mode auto: passer au dialogue suivant après un délai
        if (isAutoMode && currentCharIndex >= currentDialogue.length() && !isWaitingForInput) {
            // Attendre quelques secondes puis continuer
            // Cette logique serait implémentée avec un timer
        }
    }
    
    /**
     * Dessine le système de dialogue.
     */
    public void render(Graphics2D g) {
        if (!isActive) return;
        
        // Dessiner la boîte de dialogue
        drawDialogueBox(g);
        
        // Dessiner l'affichage du locuteur
        drawSpeakerDisplay(g);
        
        // Dessiner le portrait si activé
        if (portraitDisplayBounds != null) {
            drawPortrait(g);
        }
        
        // Dessiner le texte
        drawDialogueText(g);
        
        // Dessiner l'indicateur de continuation si nécessaire
        if (isWaitingForInput && currentChoices.isEmpty()) {
            drawContinueIndicator(g);
        }
        
        // Dessiner les choix si présents
        if (!currentChoices.isEmpty()) {
            drawChoices(g);
        }
    }
    
    /**
     * Dessine la boîte de dialogue.
     */
    private void drawDialogueBox(Graphics2D g) {
        // Fond de la boîte de dialogue
        g.setColor(new Color(26, 26, 26, 217)); // Couleur avec alpha
        g.fillRoundRect(dialogueBoxBounds.x, dialogueBoxBounds.y, dialogueBoxBounds.width, dialogueBoxBounds.height, 10, 10);
        
        // Bordure de la boîte de dialogue
        g.setColor(new Color(58, 46, 33));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(dialogueBoxBounds.x, dialogueBoxBounds.y, dialogueBoxBounds.width, dialogueBoxBounds.height, 10, 10);
    }
    
    /**
     * Dessine l'affichage du locuteur.
     */
    private void drawSpeakerDisplay(Graphics2D g) {
        if (currentSpeaker == null || currentSpeaker.isEmpty()) return;
        
        // Fond de l'affichage du locuteur
        g.setColor(new Color(46, 46, 46, 230));
        g.fillRoundRect(speakerDisplayBounds.x, speakerDisplayBounds.y, speakerDisplayBounds.width, speakerDisplayBounds.height, 5, 5);
        
        // Bordure de l'affichage du locuteur
        g.setColor(new Color(58, 46, 33));
        g.drawRoundRect(speakerDisplayBounds.x, speakerDisplayBounds.y, speakerDisplayBounds.width, speakerDisplayBounds.height, 5, 5);
        
        // Texte du locuteur
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int textX = speakerDisplayBounds.x + 10;
        int textY = speakerDisplayBounds.y + (speakerDisplayBounds.height - fm.getHeight()) / 2 + fm.getAscent();
        
        g.drawString(currentSpeaker, textX, textY);
    }
    
    /**
     * Dessine le portrait du locuteur.
     */
    private void drawPortrait(Graphics2D g) {
        if (currentPortrait == null) return;
        
        // Fond du portrait
        g.setColor(new Color(40, 40, 40));
        g.fillRect(portraitDisplayBounds.x, portraitDisplayBounds.y, portraitDisplayBounds.width, portraitDisplayBounds.height);
        
        // Bordure du portrait
        g.setColor(new Color(58, 46, 33));
        g.setStroke(new BasicStroke(3));
        g.drawRect(portraitDisplayBounds.x, portraitDisplayBounds.y, portraitDisplayBounds.width, portraitDisplayBounds.height);
        
        // Ici, nous chargerions et dessinerions l'image du portrait
        // Pour l'instant, dessiner un placeholder
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String text = "Portrait de " + currentSpeaker;
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = portraitDisplayBounds.x + (portraitDisplayBounds.width - textWidth) / 2;
        int textY = portraitDisplayBounds.y + portraitDisplayBounds.height / 2;
        
        g.drawString(text, textX, textY);
    }
    
    /**
     * Dessine le texte du dialogue.
     */
    private void drawDialogueText(Graphics2D g) {
        if (currentDialogue == null) return;
        
        // Configurer la police et la couleur
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Utiliser la couleur d'émotion si définie
        if (currentEmotion != null && emotionColors.containsKey(currentEmotion)) {
            g.setColor(emotionColors.get(currentEmotion));
        } else {
            g.setColor(Color.WHITE);
        }
        
        // Calculer les marges
        int marginLeft = 20;
        int marginTop = 20;
        
        // Dessiner le texte avec word wrapping
        String visibleText = currentDialogue.substring(0, currentCharIndex);
        drawWrappedText(g, visibleText, dialogueBoxBounds.x + marginLeft, dialogueBoxBounds.y + marginTop, dialogueBoxBounds.width - 2 * marginLeft);
    }
    
    /**
     * Dessine du texte avec retour à la ligne automatique.
     */
    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();
        
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        int currentY = y;
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            int testWidth = fm.stringWidth(testLine);
            
            if (testWidth > maxWidth) {
                g.drawString(currentLine.toString(), x, currentY);
                currentY += lineHeight;
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }
        
        // Dessiner la dernière ligne
        if (currentLine.length() > 0) {
            g.drawString(currentLine.toString(), x, currentY);
        }
    }
    
    /**
     * Dessine l'indicateur de continuation.
     */
    private void drawContinueIndicator(Graphics2D g) {
        JSONObject systemConfig = config.getJSONObject("dialogueSystem");
        JSONObject controlsConfig = systemConfig.getJSONObject("controls");
        JSONObject indicatorConfig = controlsConfig.getJSONObject("continueIndicator");
        
        if (indicatorConfig.getBoolean("enabled")) {
            JSONObject positionConfig = indicatorConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int x = parsePosition(positionConfig.getString("x"), screenWidth);
            int y = parsePosition(positionConfig.getString("y"), screenHeight);
            int width = parsePosition(positionConfig.getString("width"), screenWidth);
            int height = parsePosition(positionConfig.getString("height"), screenHeight);
            
            // Animation de pulsation
            double pulseScale = 1.0 + 0.2 * Math.sin(System.currentTimeMillis() / 200.0);
            int pulseWidth = (int)(width * pulseScale);
            int pulseHeight = (int)(height * pulseScale);
            int pulseX = x - (pulseWidth - width) / 2;
            int pulseY = y - (pulseHeight - height) / 2;
            
            // Dessiner l'indicateur (flèche simple pour l'exemple)
            g.setColor(Color.WHITE);
            int[] xPoints = {pulseX, pulseX + pulseWidth / 2, pulseX + pulseWidth};
            int[] yPoints = {pulseY + pulseHeight, pulseY, pulseY + pulseHeight};
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }
    
    /**
     * Dessine les choix de dialogue.
     */
    private void drawChoices(Graphics2D g) {
        if (currentChoices == null || currentChoices.isEmpty()) {
            return;
        }
        
        JSONObject positionConfig = config.getJSONObject("choicesPosition");
        JSONObject choiceBoxConfig = config.getJSONObject("choiceBox");
        
        int screenWidth = 1280; // Largeur d'écran supposée
        int screenHeight = 720; // Hauteur d'écran supposée
        
        int baseX = parsePosition(positionConfig.getString("x"), screenWidth);
        int baseY = parsePosition(positionConfig.getString("y"), screenHeight);
        int choiceWidth = parsePosition(choiceBoxConfig.getString("width"), screenWidth);
        int choiceHeight = parsePosition(choiceBoxConfig.getString("height"), screenWidth);
        int spacing = choiceBoxConfig.getInt("spacing");
        
        for (int i = 0; i < currentChoices.size(); i++) {
            int choiceY = baseY + i * (choiceHeight + spacing);
            
            // Fond du choix
            g.setColor(new Color(46, 46, 46, 230));
            g.fillRoundRect(baseX, choiceY, choiceWidth, choiceHeight, 5, 5);
            
            // Bordure du choix
            g.setColor(Color.WHITE);
            g.drawRoundRect(baseX, choiceY, choiceWidth, choiceHeight, 5, 5);
            
            // Texte du choix
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            FontMetrics fm = g.getFontMetrics();
            int textY = choiceY + (choiceHeight - fm.getHeight()) / 2 + fm.getAscent();
            
            // Ajouter un indicateur de choix
            String displayText = (i + 1) + ". " + currentChoices.get(i).getText();
            g.drawString(displayText, baseX + 10, textY);
        }
    }
    
    /**
     * Gère les clics de souris pour l'interaction avec le dialogue.
     */
    public void handleMouseClick(int x, int y) {
        if (!isActive) return;
        
        // Si on attend une entrée et qu'il n'y a pas de choix
        if (isWaitingForInput && currentChoices.isEmpty()) {
            if (dialogueBoxBounds.contains(x, y)) {
                continueDialogue();
            }
        }
        
        // Vérifier les clics sur les choix
        if (!currentChoices.isEmpty()) {
            JSONObject positionConfig = config.getJSONObject("choicesPosition");
            JSONObject choiceBoxConfig = config.getJSONObject("choiceBox");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int baseX = parsePosition(positionConfig.getString("x"), screenWidth);
            int baseY = parsePosition(positionConfig.getString("y"), screenHeight);
            int choiceWidth = parsePosition(choiceBoxConfig.getString("width"), screenWidth);
            int choiceHeight = parsePosition(choiceBoxConfig.getString("height"), screenWidth);
            int spacing = choiceBoxConfig.getInt("spacing");
            
            for (int i = 0; i < currentChoices.size(); i++) {
                int choiceY = baseY + i * (choiceHeight + spacing);
                
                Rectangle choiceBounds = new Rectangle(baseX, choiceY, choiceWidth, choiceHeight);
                if (choiceBounds.contains(x, y)) {
                    selectChoice(i);
                    return;
                }
            }
        }
        
        // Vérifier les clics sur les boutons de contrôle
        checkControlButtonClicks(x, y);
    }
    
    /**
     * Vérifie les clics sur les boutons de contrôle.
     */
    private void checkControlButtonClicks(int x, int y) {
        JSONObject systemConfig = config.getJSONObject("dialogueSystem");
        JSONObject controlsConfig = systemConfig.getJSONObject("controls");
        
        // Vérifier le bouton Skip
        if (controlsConfig.getJSONObject("skipButton").getBoolean("enabled")) {
            JSONObject skipButtonConfig = controlsConfig.getJSONObject("skipButton");
            JSONObject positionConfig = skipButtonConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int x1 = parsePosition(positionConfig.getString("x"), screenWidth);
            int y1 = parsePosition(positionConfig.getString("y"), screenHeight);
            int width = parsePosition(positionConfig.getString("width"), screenWidth);
            int height = parsePosition(positionConfig.getString("height"), screenHeight);
            
            Rectangle skipButtonBounds = new Rectangle(x1, y1, width, height);
            if (skipButtonBounds.contains(x, y)) {
                skipDialogue();
                return;
            }
        }
        
        // Vérifier le bouton Auto
        if (controlsConfig.getJSONObject("autoButton").getBoolean("enabled")) {
            JSONObject autoButtonConfig = controlsConfig.getJSONObject("autoButton");
            JSONObject positionConfig = autoButtonConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int x1 = parsePosition(positionConfig.getString("x"), screenWidth);
            int y1 = parsePosition(positionConfig.getString("y"), screenHeight);
            int width = parsePosition(positionConfig.getString("width"), screenWidth);
            int height = parsePosition(positionConfig.getString("height"), screenHeight);
            
            Rectangle autoButtonBounds = new Rectangle(x1, y1, width, height);
            if (autoButtonBounds.contains(x, y)) {
                toggleAutoMode();
                return;
            }
        }
        
        // Vérifier le bouton Historique
        if (controlsConfig.getJSONObject("historyButton").getBoolean("enabled")) {
            JSONObject historyButtonConfig = controlsConfig.getJSONObject("historyButton");
            JSONObject positionConfig = historyButtonConfig.getJSONObject("position");
            
            int screenWidth = 1280; // Largeur d'écran supposée
            int screenHeight = 720; // Hauteur d'écran supposée
            
            int x1 = parsePosition(positionConfig.getString("x"), screenWidth);
            int y1 = parsePosition(positionConfig.getString("y"), screenHeight);
            int width = parsePosition(positionConfig.getString("width"), screenWidth);
            int height = parsePosition(positionConfig.getString("height"), screenHeight);
            
            Rectangle historyButtonBounds = new Rectangle(x1, y1, width, height);
            if (historyButtonBounds.contains(x, y)) {
                showHistory();
                return;
            }
        }
    }
    
    /**
     * Continue le dialogue après une entrée utilisateur.
     */
    public void continueDialogue() {
        if (currentCharIndex < currentDialogue.length()) {
            // Si le texte n'est pas entièrement affiché, l'afficher complètement
            currentCharIndex = currentDialogue.length();
        } else {
            // Sinon, fermer le dialogue actuel
            isActive = false;
            isWaitingForInput = false;
            System.out.println("Dialogue terminé");
            
            // Ici, on pourrait appeler un callback pour indiquer que le dialogue est terminé
        }
    }
    
    /**
     * Sélectionne un choix de dialogue.
     */
    public void selectChoice(int index) {
        if (index >= 0 && index < currentChoices.size()) {
            DialogueChoice choice = currentChoices.get(index);
            System.out.println("Choix sélectionné: " + choice.getText());
            
            // Ajouter le choix à l'historique
            dialogueHistory.add(0, "Vous: " + choice.getText());
            
            // Exécuter l'action associée au choix
            if (choice.getAction() != null) {
                choice.getAction().run();
            }
            
            // Effacer les choix actuels
            currentChoices.clear();
            isWaitingForInput = false;
            
            // Fermer le dialogue si nécessaire
            if (choice.shouldCloseDialogue()) {
                isActive = false;
            }
        }
    }
    
    /**
     * Passe le dialogue actuel.
     */
    public void skipDialogue() {
        isActive = false;
        System.out.println("Dialogue passé");
    }
    
    /**
     * Active ou désactive le mode automatique.
     */
    public void toggleAutoMode() {
        isAutoMode = !isAutoMode;
        System.out.println("Mode auto: " + (isAutoMode ? "activé" : "désactivé"));
    }
    
    /**
     * Affiche l'historique des dialogues.
     */
    public void showHistory() {
        System.out.println("Affichage de l'historique des dialogues");
        // Ici, on afficherait une interface pour voir l'historique complet
    }
    
    /**
     * Vérifie si le système de dialogue est actif.
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Ferme le système de dialogue.
     */
    public void close() {
        isActive = false;
        currentChoices.clear();
        System.out.println("Système de dialogue fermé");
    }
    
    /**
     * Classe pour représenter un choix de dialogue.
     */
    public static class DialogueChoice {
        private String text;
        private Runnable action;
        private boolean closeDialogue;
        
        public DialogueChoice(String text, Runnable action, boolean closeDialogue) {
            this.text = text;
            this.action = action;
            this.closeDialogue = closeDialogue;
        }
        
        public String getText() {
            return text;
        }
        
        public Runnable getAction() {
            return action;
        }
        
        public boolean shouldCloseDialogue() {
            return closeDialogue;
        }
    }
}

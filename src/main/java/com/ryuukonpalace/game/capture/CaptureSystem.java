package com.ryuukonpalace.game.capture;

import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.player.Player;
import com.ryuukonpalace.game.qte.QTESystem;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.utils.ResourceManager;
import com.ryuukonpalace.game.items.CaptureStone;

/**
 * Système de capture des créatures.
 * Gère l'interaction entre le système QTE et la capture des créatures.
 */
public class CaptureSystem {
    
    // Singleton instance
    private static CaptureSystem instance;
    
    // Système QTE
    private QTESystem qteSystem;
    
    // Renderer
    private Renderer renderer;
    
    // État de la capture
    private CaptureState state;
    
    // Créature en cours de capture
    private Creature targetCreature;
    
    // Joueur qui tente la capture
    private Player player;
    
    // Callback pour la fin de la capture
    private CaptureCallback captureCallback;
    
    // Difficulté de base pour la capture
    private int baseDifficulty;
    
    // ID de la texture de l'animation de capture
    private int captureAnimationTextureId;
    
    // Temps écoulé pour l'animation
    private float animationTime;
    
    // Durée totale de l'animation
    private float animationDuration;
    
    // Nombre de frames dans l'animation
    private int animationFrames;
    
    // Pierre de capture utilisée pour la tentative actuelle
    private CaptureStone currentCaptureStone;
    
    // ID de la texture des mains faisant des signes
    private int handSignsTextureId;
    
    // Nombre de signes réussis dans la séquence
    private int successfulSigns;
    
    // Nombre total de signes dans la séquence
    private int totalSigns;
    
    /**
     * Constructeur privé pour le singleton
     */
    private CaptureSystem() {
        this.qteSystem = QTESystem.getInstance();
        this.renderer = Renderer.getInstance();
        this.state = CaptureState.INACTIVE;
        this.baseDifficulty = 5;
        this.animationTime = 0.0f;
        this.animationDuration = 3.0f;
        this.animationFrames = 8;
        this.successfulSigns = 0;
        this.totalSigns = 5; // Par défaut, 5 signes sont nécessaires pour une capture
        
        // Charger les textures
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.captureAnimationTextureId = resourceManager.loadTexture(
            "src/main/resources/images/capture/capture_animation.png", "capture_animation");
        this.handSignsTextureId = resourceManager.loadTexture(
            "src/main/resources/images/capture/hand_signs.png", "hand_signs");
    }
    
    /**
     * Obtenir l'instance unique du système de capture
     * @return L'instance du CaptureSystem
     */
    public static CaptureSystem getInstance() {
        if (instance == null) {
            instance = new CaptureSystem();
        }
        return instance;
    }
    
    /**
     * Initialiser le système de capture
     */
    public void init() {
        // Réinitialiser l'état
        reset();
    }
    
    /**
     * Démarrer une tentative de capture
     * 
     * @param player Joueur qui tente la capture
     * @param creature Créature à capturer
     * @param callback Callback à appeler à la fin de la capture
     * @return true si la capture a été démarrée avec succès, false sinon
     */
    public boolean startCapture(Player player, Creature creature, CaptureCallback callback) {
        return startCapture(player, creature, callback, null);
    }
    
    /**
     * Démarrer une tentative de capture avec une pierre spécifique
     * 
     * @param player Joueur qui tente la capture
     * @param creature Créature à capturer
     * @param callback Callback à appeler à la fin de la capture
     * @param captureStone Pierre de capture à utiliser (null pour utiliser une pierre standard)
     * @return true si la capture a été démarrée avec succès, false sinon
     */
    public boolean startCapture(Player player, Creature creature, CaptureCallback callback, CaptureStone captureStone) {
        // Vérifier si une capture est déjà en cours
        if (state != CaptureState.INACTIVE) {
            return false;
        }
        
        // Initialiser les paramètres de la capture
        this.player = player;
        this.targetCreature = creature;
        this.captureCallback = callback;
        this.state = CaptureState.STARTING;
        this.animationTime = 0.0f;
        this.currentCaptureStone = captureStone;
        this.successfulSigns = 0;
        
        // Calculer la difficulté du QTE en fonction de la santé de la créature
        int healthFactor = (int)(5.0f * creature.getHealth() / creature.getMaxHealth());
        int levelFactor = (int)(creature.getLevel() / 5.0f);
        int qteDifficulty = baseDifficulty + healthFactor + levelFactor;
        
        // Appliquer le modificateur de la pierre de capture si disponible
        if (captureStone != null) {
            float stoneMultiplier = captureStone.getTotalCaptureMultiplier(creature.getType());
            qteDifficulty = (int)(qteDifficulty / stoneMultiplier);
        }
        
        // Limiter la difficulté entre 1 et 10
        qteDifficulty = Math.max(1, Math.min(10, qteDifficulty));
        
        // Démarrer le QTE
        if (qteSystem.startQTE(qteDifficulty)) {
            System.out.println("Tentative de capture de " + creature.getName() + " (Difficulté: " + qteDifficulty + ")");
            return true;
        } else {
            // Échec du démarrage du QTE
            reset();
            return false;
        }
    }
    
    /**
     * Mettre à jour le système de capture
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        if (state == CaptureState.INACTIVE) {
            return;
        }
        
        // Mettre à jour le temps d'animation
        animationTime += deltaTime;
        
        // Mettre à jour le QTE si actif
        if (qteSystem.isActive()) {
            qteSystem.update(deltaTime);
        }
        
        // Vérifier si le QTE est terminé
        if (qteSystem.isCompleted()) {
            handleQTEResult();
            
            // Si nous n'avons pas encore fait tous les signes, démarrer un nouveau QTE
            if (state == CaptureState.HAND_SIGNS && successfulSigns < totalSigns) {
                // Calculer la difficulté du prochain QTE
                int nextDifficulty = baseDifficulty + (int)(successfulSigns * 0.5f);
                
                // Appliquer le modificateur de la pierre de capture si disponible
                if (currentCaptureStone != null) {
                    float stoneMultiplier = currentCaptureStone.getTotalCaptureMultiplier(targetCreature.getType());
                    nextDifficulty = (int)(nextDifficulty / stoneMultiplier);
                }
                
                // Limiter la difficulté entre 1 et 10
                nextDifficulty = Math.max(1, Math.min(10, nextDifficulty));
                
                // Démarrer le prochain QTE
                qteSystem.startQTE(nextDifficulty);
            }
        }
        
        // Gérer l'animation de capture
        if (state == CaptureState.ANIMATION) {
            if (animationTime >= animationDuration) {
                // Animation terminée
                finishCapture();
            }
        }
    }
    
    /**
     * Dessiner le système de capture
     */
    public void render() {
        if (state == CaptureState.INACTIVE) {
            return;
        }
        
        // Dessiner le QTE si actif
        if (qteSystem.isActive()) {
            qteSystem.render();
            
            // Dessiner les signes des mains en cours
            renderHandSigns();
        }
        
        // Dessiner l'animation de capture si en cours
        if (state == CaptureState.ANIMATION) {
            renderCaptureAnimation();
        }
    }
    
    /**
     * Dessiner les signes des mains
     */
    private void renderHandSigns() {
        // Calculer les dimensions et la position
        float screenWidth = renderer.getScreenWidth();
        float screenHeight = renderer.getScreenHeight();
        float signSize = screenWidth * 0.1f;
        float startX = (screenWidth - (signSize * totalSigns)) / 2.0f;
        float y = screenHeight * 0.7f;
        
        // Dessiner les signes réussis
        for (int i = 0; i < successfulSigns; i++) {
            float x = startX + (i * signSize);
            renderer.drawSprite(handSignsTextureId, x, y, signSize, signSize);
        }
        
        // Dessiner le signe en cours
        if (successfulSigns < totalSigns) {
            float x = startX + (successfulSigns * signSize);
            renderer.drawSprite(handSignsTextureId, x, y, signSize, signSize);
        }
    }
    
    /**
     * Dessiner l'animation de capture
     */
    private void renderCaptureAnimation() {
        // Calculer le frame actuel de l'animation
        float frameProgress = animationTime / animationDuration;
        int currentFrame = (int)(frameProgress * animationFrames);
        currentFrame = Math.min(currentFrame, animationFrames - 1);
        
        // Calculer les dimensions de l'animation
        float screenWidth = renderer.getScreenWidth();
        float screenHeight = renderer.getScreenHeight();
        float animWidth = screenWidth * 0.5f;
        float animHeight = screenHeight * 0.5f;
        float animX = (screenWidth - animWidth) / 2.0f;
        float animY = (screenHeight - animHeight) / 2.0f;
        
        // Dessiner le frame actuel de l'animation
        renderer.drawSprite(captureAnimationTextureId, animX, animY, animWidth, animHeight);
        
        // Dessiner la créature au centre de l'écran
        if (targetCreature != null) {
            float creatureSize = Math.min(animWidth, animHeight) * 0.5f;
            float creatureX = (screenWidth - creatureSize) / 2.0f;
            float creatureY = (screenHeight - creatureSize) / 2.0f;
            renderer.drawSprite(targetCreature.getTextureId(), creatureX, creatureY, creatureSize, creatureSize);
        
            // Dessiner la pierre de capture si disponible
            if (currentCaptureStone != null) {
                float stoneSize = Math.min(animWidth, animHeight) * 0.2f;
                float stoneX = (screenWidth - stoneSize) / 2.0f;
                float stoneY = (screenHeight + creatureSize) / 2.0f - stoneSize;
                renderer.drawSprite(currentCaptureStone.getTextureId(), stoneX, stoneY, stoneSize, stoneSize);
            }
        }
    }
    
    /**
     * Gérer le résultat du QTE
     */
    private void handleQTEResult() {
        boolean success = qteSystem.isSuccess();
        
        if (state == CaptureState.STARTING) {
            // Premier QTE réussi, passer aux signes des mains
            state = CaptureState.HAND_SIGNS;
        }
        
        if (success) {
            // Incrémenter le compteur de signes réussis
            successfulSigns++;
        }
        
        // Vérifier si tous les signes ont été faits
        if (successfulSigns >= totalSigns) {
            // Calculer la probabilité de capture
            float captureRate = calculateCaptureRate();
            
            // Déterminer si la capture est réussie
            float random = (float) Math.random();
            captureSuccess = random <= captureRate;
            
            // Passer à l'animation de capture
            state = CaptureState.ANIMATION;
            animationTime = 0.0f;
        }
        
        // Réinitialiser le QTE
        qteSystem.reset();
    }
    
    /**
     * Calculer le taux de réussite de la capture
     * 
     * @return Taux de réussite (0.0 - 1.0)
     */
    private float calculateCaptureRate() {
        // Taux de base basé sur le nombre de signes réussis
        float baseRate = (float) successfulSigns / totalSigns;
        
        // Facteur basé sur la santé de la créature
        float healthFactor = 1.0f - (targetCreature.getHealth() / targetCreature.getMaxHealth());
        
        // Facteur basé sur le niveau de la créature
        float levelFactor = 1.0f - (targetCreature.getLevel() / 50.0f); // Supposons un niveau max de 50
        
        // Multiplicateur de la pierre de capture
        float stoneMultiplier = 1.0f;
        if (currentCaptureStone != null) {
            stoneMultiplier = currentCaptureStone.getTotalCaptureMultiplier(targetCreature.getType());
        }
        
        // Calculer le taux final
        float captureRate = baseRate * (0.5f + 0.3f * healthFactor + 0.2f * levelFactor) * stoneMultiplier;
        
        // Limiter entre 0.1 et 0.9
        return Math.max(0.1f, Math.min(0.9f, captureRate));
    }
    
    /**
     * Terminer la capture
     */
    private void finishCapture() {
        // Si la capture est réussie et qu'une pierre est disponible, stocker la créature dans la pierre
        if (captureSuccess && currentCaptureStone != null) {
            currentCaptureStone.captureCreature(targetCreature);
        }
        
        // Appeler le callback avec le résultat
        if (captureCallback != null) {
            captureCallback.onCaptureResult(player, targetCreature, captureSuccess);
        }
        
        // Réinitialiser l'état
        reset();
    }
    
    /**
     * Réinitialiser le système de capture
     */
    public void reset() {
        state = CaptureState.INACTIVE;
        targetCreature = null;
        player = null;
        captureCallback = null;
        animationTime = 0.0f;
        currentCaptureStone = null;
        successfulSigns = 0;
        
        // Réinitialiser le QTE si nécessaire
        if (qteSystem.isActive() || qteSystem.isCompleted()) {
            qteSystem.reset();
        }
    }
    
    /**
     * Vérifier si une capture est en cours
     * 
     * @return true si une capture est en cours, false sinon
     */
    public boolean isActive() {
        return state != CaptureState.INACTIVE;
    }
    
    /**
     * Définir la difficulté de base pour la capture
     * 
     * @param difficulty Difficulté de base (1-10)
     */
    public void setBaseDifficulty(int difficulty) {
        this.baseDifficulty = Math.max(1, Math.min(10, difficulty));
    }
    
    /**
     * Définir le nombre total de signes nécessaires pour une capture
     * 
     * @param totalSigns Nombre total de signes
     */
    public void setTotalSigns(int totalSigns) {
        this.totalSigns = Math.max(1, totalSigns);
    }
    
    // Résultat de la capture
    private boolean captureSuccess;
    
    /**
     * États possibles de la capture
     */
    public enum CaptureState {
        INACTIVE,    // Capture inactive
        STARTING,    // Capture en démarrage
        HAND_SIGNS,  // Séquence de signes des mains
        ANIMATION,   // Animation de capture en cours
    }
    
    /**
     * Interface pour le callback de fin de capture
     */
    public interface CaptureCallback {
        void onCaptureResult(Player player, Creature creature, boolean success);
    }
}

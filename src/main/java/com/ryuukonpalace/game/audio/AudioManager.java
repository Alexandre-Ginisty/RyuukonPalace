package com.ryuukonpalace.game.audio;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ryuukonpalace.game.utils.JsonLoader;

/**
 * Gestionnaire audio pour le jeu Ryuukon Palace.
 * Gère le chargement, la lecture et le mixage des sons et musiques.
 */
public class AudioManager {
    // Singleton
    private static AudioManager instance;
    
    // Catégories audio
    public static final String CATEGORY_MUSIC = "music";
    public static final String CATEGORY_SFX = "sfx";
    public static final String CATEGORY_UI = "ui";
    public static final String CATEGORY_AMBIENT = "ambient";
    public static final String CATEGORY_VOICE = "voice";
    
    // Volumes par défaut pour chaque catégorie (0.0f à 1.0f)
    private Map<String, Float> categoryVolumes;
    
    // Cache des clips audio
    private Map<String, Clip> audioClips;
    
    // Clips actuellement en lecture
    private Map<String, Clip> playingClips;
    
    // Musique de fond actuelle
    private String currentMusic;
    
    // Exécuteur pour les opérations audio asynchrones
    private ExecutorService audioExecutor;
    
    // Chemin vers les ressources audio
    private static final String AUDIO_PATH = "src/main/resources/sounds/";
    
    // Configuration audio
    private JSONObject audioConfig;
    
    // État d'initialisation
    private boolean initialized = false;
    
    // État de mute
    private boolean musicMuted = false;
    private boolean soundMuted = false;
    
    /**
     * Constructeur privé (pattern Singleton)
     */
    private AudioManager() {
        categoryVolumes = new HashMap<>();
        audioClips = new HashMap<>();
        playingClips = new HashMap<>();
        audioExecutor = Executors.newFixedThreadPool(2);
        
        // Initialiser les volumes par défaut
        categoryVolumes.put(CATEGORY_MUSIC, 0.7f);
        categoryVolumes.put(CATEGORY_SFX, 0.8f);
        categoryVolumes.put(CATEGORY_UI, 0.6f);
        categoryVolumes.put(CATEGORY_AMBIENT, 0.5f);
        categoryVolumes.put(CATEGORY_VOICE, 1.0f);
        
        // Charger la configuration audio
        loadAudioConfig();
        
        initialized = true;
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire audio
     * @return L'instance du AudioManager
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Vérifier si le gestionnaire audio est initialisé
     * @return true si initialisé, false sinon
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Charger la configuration audio depuis le fichier JSON
     */
    private void loadAudioConfig() {
        try {
            audioConfig = JsonLoader.loadJsonObject("src/main/resources/data/audio_config.json");
            System.out.println("Configuration audio chargée avec succès.");
            
            // Charger les volumes personnalisés s'ils existent
            if (audioConfig.has("volumes")) {
                JSONObject volumes = audioConfig.getJSONObject("volumes");
                for (String category : categoryVolumes.keySet()) {
                    if (volumes.has(category)) {
                        float volume = (float) volumes.getDouble(category);
                        categoryVolumes.put(category, volume);
                        System.out.println("Volume " + category + " défini à " + volume);
                    }
                }
            }
            
            // Précharger les sons fréquemment utilisés
            if (audioConfig.has("preload")) {
                JSONArray preloadSounds = audioConfig.getJSONArray("preload");
                for (int i = 0; i < preloadSounds.length(); i++) {
                    String soundId = preloadSounds.getString(i);
                    preloadSound(soundId);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la configuration audio: " + e.getMessage());
            createDefaultAudioConfig();
        }
    }
    
    /**
     * Créer un fichier de configuration audio par défaut
     */
    private void createDefaultAudioConfig() {
        try {
            audioConfig = new JSONObject();
            
            // Volumes par défaut
            JSONObject volumes = new JSONObject();
            for (Map.Entry<String, Float> entry : categoryVolumes.entrySet()) {
                volumes.put(entry.getKey(), entry.getValue());
            }
            audioConfig.put("volumes", volumes);
            
            // Sons à précharger
            JSONArray preload = new JSONArray();
            preload.put("ui_click");
            preload.put("ui_hover");
            preload.put("ui_back");
            preload.put("music_main_theme");
            audioConfig.put("preload", preload);
            
            // Sauvegarder la configuration
            JsonLoader.saveJsonObject(audioConfig, "src/main/resources/data/audio_config.json", true);
            System.out.println("Configuration audio par défaut créée.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la configuration audio par défaut: " + e.getMessage());
        }
    }
    
    /**
     * Précharger un son dans le cache
     * @param soundId Identifiant du son
     */
    private void preloadSound(String soundId) {
        try {
            String filePath = getSoundFilePath(soundId);
            File soundFile = new File(filePath);
            
            if (!soundFile.exists()) {
                System.err.println("Fichier audio introuvable: " + filePath);
                return;
            }
            
            // Charger le clip audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // Stocker dans le cache
            audioClips.put(soundId, clip);
            
            System.out.println("Son préchargé: " + soundId);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erreur lors du préchargement du son " + soundId + ": " + e.getMessage());
        }
    }
    
    /**
     * Obtenir le chemin du fichier audio à partir de son ID
     * @param soundId Identifiant du son
     * @return Chemin complet du fichier audio
     */
    private String getSoundFilePath(String soundId) {
        String category = getCategoryFromSoundId(soundId);
        return AUDIO_PATH + category + "/" + soundId + ".wav";
    }
    
    /**
     * Déterminer la catégorie d'un son à partir de son ID
     * @param soundId Identifiant du son
     * @return Catégorie du son
     */
    private String getCategoryFromSoundId(String soundId) {
        if (soundId.startsWith("music_")) return CATEGORY_MUSIC;
        if (soundId.startsWith("sfx_")) return CATEGORY_SFX;
        if (soundId.startsWith("ui_")) return CATEGORY_UI;
        if (soundId.startsWith("amb_")) return CATEGORY_AMBIENT;
        if (soundId.startsWith("voice_")) return CATEGORY_VOICE;
        
        // Par défaut, considérer comme un effet sonore
        return CATEGORY_SFX;
    }
    
    /**
     * Jouer un son
     * @param soundId Identifiant du son
     * @return true si le son a été joué avec succès, false sinon
     */
    public boolean playSound(String soundId) {
        return playSound(soundId, false);
    }
    
    /**
     * Jouer un son en boucle
     * @param soundId Identifiant du son
     * @return true si le son a été joué avec succès, false sinon
     */
    public boolean playSoundLoop(String soundId) {
        return playSound(soundId, true);
    }
    
    /**
     * Jouer une musique
     * @param musicId Identifiant de la musique
     * @return true si la musique a été jouée avec succès, false sinon
     */
    public boolean playMusic(String musicId) {
        // Si la même musique est déjà en cours, ne rien faire
        if (musicId.equals(currentMusic)) {
            return true;
        }
        
        // Arrêter la musique actuelle
        stopMusic();
        
        // Jouer la nouvelle musique en boucle
        return playSound(CATEGORY_MUSIC + "_" + musicId, true);
    }
    
    /**
     * Jouer un son avec option de boucle
     * @param soundId Identifiant du son
     * @param loop true pour jouer en boucle, false sinon
     * @return true si le son a été joué avec succès, false sinon
     */
    private boolean playSound(String soundId, boolean loop) {
        if (!initialized) {
            System.err.println("AudioManager non initialisé");
            return false;
        }
        
        // Récupérer le clip audio
        Clip clip = audioClips.get(soundId);
        
        if (clip == null) {
            try {
                String filePath = getSoundFilePath(soundId);
                File soundFile = new File(filePath);
                
                if (!soundFile.exists()) {
                    System.err.println("Fichier audio introuvable: " + filePath);
                    return false;
                }
                
                // Charger le clip audio
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                
                // Stocker dans le cache
                audioClips.put(soundId, clip);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Erreur lors du chargement du son " + soundId + ": " + e.getMessage());
                return false;
            }
        }
        
        // Réinitialiser le clip s'il était déjà joué
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        
        // Configurer la lecture en boucle
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        
        // Appliquer le volume
        String category = getCategoryFromSoundId(soundId);
        float volume = categoryVolumes.get(category);
        
        // Vérifier si la catégorie est en mode muet
        if ((category.equals(CATEGORY_MUSIC) && musicMuted) || 
            (!category.equals(CATEGORY_MUSIC) && soundMuted)) {
            volume = 0.0f;
        }
        
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
        
        // Jouer le son
        clip.start();
        
        // Enregistrer dans les clips en cours de lecture
        playingClips.put(soundId, clip);
        
        // Si c'est une musique, mettre à jour la musique actuelle
        if (category.equals(CATEGORY_MUSIC)) {
            currentMusic = soundId;
        }
        
        System.out.println("Son joué: " + soundId + (loop ? " (en boucle)" : ""));
        return true;
    }
    
    /**
     * Arrêter un son spécifique
     * @param soundId Identifiant du son
     */
    public void stopSound(String soundId) {
        Clip clip = playingClips.get(soundId);
        if (clip != null && clip.isRunning()) {
            clip.stop();
            playingClips.remove(soundId);
            System.out.println("Son arrêté: " + soundId);
        }
    }
    
    /**
     * Arrêter la musique actuelle
     */
    public void stopMusic() {
        if (currentMusic != null) {
            stopSound(currentMusic);
            currentMusic = null;
        }
    }
    
    /**
     * Arrêter tous les sons d'une catégorie
     * @param category Catégorie de sons à arrêter
     */
    public void stopCategory(String category) {
        for (String soundId : new HashMap<>(playingClips).keySet()) {
            if (getCategoryFromSoundId(soundId).equals(category)) {
                stopSound(soundId);
            }
        }
    }
    
    /**
     * Arrêter tous les sons
     */
    public void stopAll() {
        for (String soundId : new HashMap<>(playingClips).keySet()) {
            stopSound(soundId);
        }
    }
    
    /**
     * Définir le volume pour une catégorie de sons
     * @param category Catégorie de sons
     * @param volume Volume (0.0f à 1.0f)
     */
    public void setCategoryVolume(String category, float volume) {
        // Limiter le volume entre 0 et 1
        volume = Math.max(0.0f, Math.min(1.0f, volume));
        categoryVolumes.put(category, volume);
        
        // Mettre à jour le volume des sons en cours de lecture
        for (Map.Entry<String, Clip> entry : playingClips.entrySet()) {
            String soundId = entry.getKey();
            if (getCategoryFromSoundId(soundId).equals(category)) {
                Clip clip = entry.getValue();
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }
            }
        }
        
        // Mettre à jour la configuration
        if (audioConfig.has("volumes")) {
            audioConfig.getJSONObject("volumes").put(category, volume);
        } else {
            JSONObject volumes = new JSONObject();
            volumes.put(category, volume);
            audioConfig.put("volumes", volumes);
        }
        
        // Sauvegarder la configuration
        try {
            JsonLoader.saveJsonObject(audioConfig, "src/main/resources/data/audio_config.json", true);
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de la configuration audio: " + e.getMessage());
        }
        
        System.out.println("Volume " + category + " défini à " + volume);
    }
    
    /**
     * Obtenir le volume actuel d'une catégorie de sons
     * @param category Catégorie de sons
     * @return Volume actuel (0.0f à 1.0f)
     */
    public float getCategoryVolume(String category) {
        return categoryVolumes.getOrDefault(category, 1.0f);
    }
    
    /**
     * Jouer un son d'interface utilisateur
     * @param uiSoundId Identifiant du son UI
     */
    public void playUISound(String uiSoundId) {
        playSound("ui_" + uiSoundId);
    }
    
    /**
     * Jouer un effet sonore
     * @param sfxId Identifiant de l'effet sonore
     */
    public void playSFX(String sfxId) {
        playSound("sfx_" + sfxId);
    }
    
    /**
     * Jouer un son ambiant
     * @param ambientId Identifiant du son ambiant
     * @param loop Si true, le son sera joué en boucle
     */
    public void playAmbient(String ambientId, boolean loop) {
        playSound("amb_" + ambientId, loop);
    }
    
    /**
     * Jouer un dialogue vocal
     * @param voiceId Identifiant de la voix
     */
    public void playVoice(String voiceId) {
        // Arrêter toute voix en cours
        stopCategory(CATEGORY_VOICE);
        
        // Jouer la nouvelle voix
        playSound("voice_" + voiceId);
    }
    
    /**
     * Libérer les ressources audio
     */
    public void dispose() {
        // Arrêter tous les sons
        stopAll();
        
        // Fermer tous les clips
        for (Clip clip : audioClips.values()) {
            clip.close();
        }
        
        // Vider les collections
        audioClips.clear();
        playingClips.clear();
        
        // Arrêter l'exécuteur
        audioExecutor.shutdown();
        
        System.out.println("Ressources audio libérées.");
    }
    
    /**
     * Charger un son depuis un fichier
     * @param soundId Identifiant du son
     * @param filePath Chemin du fichier
     * @return true si le chargement a réussi, false sinon
     */
    public boolean loadSound(String soundId, String filePath) {
        try {
            File soundFile = new File(filePath);
            
            if (!soundFile.exists()) {
                System.err.println("Fichier audio introuvable: " + filePath);
                return false;
            }
            
            // Charger le clip audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // Stocker dans le cache
            audioClips.put(soundId, clip);
            
            System.out.println("Son chargé: " + soundId);
            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erreur lors du chargement du son " + soundId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Charger une musique depuis un fichier
     * @param musicId Identifiant de la musique
     * @param filePath Chemin du fichier
     * @return true si le chargement a réussi, false sinon
     */
    public boolean loadMusic(String musicId, String filePath) {
        return loadSound(musicId, filePath);
    }
    
    /**
     * Vérifier si un son est chargé
     * @param soundId Identifiant du son
     * @return true si le son est chargé, false sinon
     */
    public boolean isSoundLoaded(String soundId) {
        return audioClips.containsKey(soundId);
    }
    
    /**
     * Vérifier si une musique est chargée
     * @param musicId Identifiant de la musique
     * @return true si la musique est chargée, false sinon
     */
    public boolean isMusicLoaded(String musicId) {
        return audioClips.containsKey(musicId);
    }
    
    /**
     * Vérifier si une musique est en cours de lecture
     * @param musicId Identifiant de la musique
     * @return true si la musique est en cours de lecture, false sinon
     */
    public boolean isMusicPlaying(String musicId) {
        Clip clip = playingClips.get(musicId);
        return clip != null && clip.isRunning();
    }
    
    /**
     * Obtenir le volume de la musique
     * @return Volume de la musique (0.0f à 1.0f)
     */
    public float getMusicVolume() {
        return getCategoryVolume(CATEGORY_MUSIC);
    }
    
    /**
     * Définir le volume de la musique
     * @param volume Volume (0.0f à 1.0f)
     */
    public void setMusicVolume(float volume) {
        setCategoryVolume(CATEGORY_MUSIC, volume);
    }
    
    /**
     * Obtenir le volume des effets sonores
     * @return Volume des effets sonores (0.0f à 1.0f)
     */
    public float getSoundVolume() {
        return getCategoryVolume(CATEGORY_SFX);
    }
    
    /**
     * Définir le volume des effets sonores
     * @param volume Volume (0.0f à 1.0f)
     */
    public void setSoundVolume(float volume) {
        setCategoryVolume(CATEGORY_SFX, volume);
    }
    
    /**
     * Vérifier si la musique est en mode muet
     * @return true si la musique est en mode muet, false sinon
     */
    public boolean isMusicMuted() {
        return musicMuted;
    }
    
    /**
     * Définir si la musique est en mode muet
     * @param muted true pour mettre en mode muet, false sinon
     */
    public void setMusicMuted(boolean muted) {
        this.musicMuted = muted;
        
        // Appliquer le changement aux musiques en cours de lecture
        float volume = muted ? 0.0f : getCategoryVolume(CATEGORY_MUSIC);
        for (Map.Entry<String, Clip> entry : playingClips.entrySet()) {
            String soundId = entry.getKey();
            if (getCategoryFromSoundId(soundId).equals(CATEGORY_MUSIC)) {
                Clip clip = entry.getValue();
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }
            }
        }
    }
    
    /**
     * Vérifier si les effets sonores sont en mode muet
     * @return true si les effets sonores sont en mode muet, false sinon
     */
    public boolean isSoundMuted() {
        return soundMuted;
    }
    
    /**
     * Définir si les effets sonores sont en mode muet
     * @param muted true pour mettre en mode muet, false sinon
     */
    public void setSoundMuted(boolean muted) {
        this.soundMuted = muted;
        
        // Appliquer le changement aux sons en cours de lecture
        for (Map.Entry<String, Clip> entry : playingClips.entrySet()) {
            String soundId = entry.getKey();
            String category = getCategoryFromSoundId(soundId);
            if (!category.equals(CATEGORY_MUSIC)) {
                float volume = muted ? 0.0f : getCategoryVolume(category);
                Clip clip = entry.getValue();
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }
            }
        }
    }
}

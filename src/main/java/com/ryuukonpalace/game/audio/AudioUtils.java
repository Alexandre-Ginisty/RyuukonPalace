package com.ryuukonpalace.game.audio;

import java.io.File;

/**
 * Classe utilitaire pour la gestion audio
 * Permet de vérifier et générer les fichiers audio manquants
 */
public class AudioUtils {
    
    /**
     * Vérifie si tous les fichiers audio nécessaires existent
     * et génère ceux qui sont manquants
     */
    public static void checkAndGenerateAudioFiles() {
        System.out.println("Vérification des fichiers audio...");
        
        // Vérifier si le dossier de sons existe
        File soundsDir = new File("src/main/resources/sounds");
        if (!soundsDir.exists()) {
            soundsDir.mkdirs();
            System.out.println("Dossier de sons créé: " + soundsDir.getAbsolutePath());
        }
        
        // Vérifier les sous-dossiers
        checkAndCreateDirectory("src/main/resources/sounds/ui");
        checkAndCreateDirectory("src/main/resources/sounds/sfx");
        checkAndCreateDirectory("src/main/resources/sounds/music");
        checkAndCreateDirectory("src/main/resources/sounds/ambient");
        checkAndCreateDirectory("src/main/resources/sounds/voice");
        
        // Vérifier les fichiers audio de base
        boolean needGeneration = false;
        
        // Sons UI
        needGeneration |= checkAudioFile("src/main/resources/sounds/ui/click.wav");
        needGeneration |= checkAudioFile("src/main/resources/sounds/ui/hover.wav");
        needGeneration |= checkAudioFile("src/main/resources/sounds/ui/back.wav");
        
        // Sons SFX
        needGeneration |= checkAudioFile("src/main/resources/sounds/sfx/attack.wav");
        needGeneration |= checkAudioFile("src/main/resources/sounds/sfx/attack_fire.wav");
        needGeneration |= checkAudioFile("src/main/resources/sounds/sfx/attack_water.wav");
        
        // Sons ambiants
        needGeneration |= checkAudioFile("src/main/resources/sounds/ambient/forest.wav");
        needGeneration |= checkAudioFile("src/main/resources/sounds/ambient/cave.wav");
        
        // Musiques
        needGeneration |= checkAudioFile("src/main/resources/sounds/music/title_theme.wav");
        needGeneration |= checkAudioFile("src/main/resources/sounds/music/battle_normal.wav");
        
        // Si des fichiers sont manquants, générer tous les sons
        if (needGeneration) {
            System.out.println("Certains fichiers audio sont manquants. Génération en cours...");
            SoundGenerator.generateAllSounds();
        } else {
            System.out.println("Tous les fichiers audio sont présents.");
        }
    }
    
    /**
     * Vérifie si un dossier existe et le crée si nécessaire
     * @param directoryPath Chemin du dossier
     */
    private static void checkAndCreateDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("Dossier créé: " + directory.getAbsolutePath());
        }
    }
    
    /**
     * Vérifie si un fichier audio existe
     * @param filePath Chemin du fichier
     * @return true si le fichier n'existe pas
     */
    private static boolean checkAudioFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Fichier audio manquant: " + filePath);
            return true;
        }
        return false;
    }
}

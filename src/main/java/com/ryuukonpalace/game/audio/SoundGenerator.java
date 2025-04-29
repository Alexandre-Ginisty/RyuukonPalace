package com.ryuukonpalace.game.audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.ryuukonpalace.game.creatures.CreatureType;

/**
 * Générateur de sons procéduraux pour le jeu.
 * Permet de créer des effets sonores de base pour le développement.
 */
public class SoundGenerator {
    
    // Paramètres audio
    private static final float SAMPLE_RATE = 44100.0f;
    private static final int SAMPLE_SIZE_BITS = 16;
    private static final int CHANNELS = 1;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;
    
    // Dossier de sortie
    private static final String OUTPUT_DIR = "src/main/resources/sounds/";
    
    // Générateur de nombres aléatoires
    private static final Random random = new Random();
    
    /**
     * Générer tous les sons de base pour le jeu
     */
    public static void generateAllSounds() {
        // Créer les dossiers nécessaires
        createDirectories();
        
        // Générer les sons UI
        generateUISounds();
        
        // Générer les sons d'attaque pour chaque type
        generateAttackSounds();
        
        // Générer les sons ambiants
        generateAmbientSounds();
        
        // Générer les musiques de base
        generateBasicMusic();
        
        System.out.println("Génération des sons terminée !");
    }
    
    /**
     * Créer les dossiers nécessaires pour les sons
     */
    private static void createDirectories() {
        createDirectory(OUTPUT_DIR);
        createDirectory(OUTPUT_DIR + "ui/");
        createDirectory(OUTPUT_DIR + "sfx/");
        createDirectory(OUTPUT_DIR + "music/");
        createDirectory(OUTPUT_DIR + "ambient/");
        createDirectory(OUTPUT_DIR + "voice/");
    }
    
    /**
     * Créer un dossier s'il n'existe pas
     * @param directory Chemin du dossier
     */
    private static void createDirectory(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Dossier créé: " + directory);
        }
    }
    
    /**
     * Générer les sons d'interface utilisateur
     */
    private static void generateUISounds() {
        // Son de clic
        generateClickSound(OUTPUT_DIR + "ui/click.wav");
        
        // Son de survol
        generateHoverSound(OUTPUT_DIR + "ui/hover.wav");
        
        // Son de retour
        generateBackSound(OUTPUT_DIR + "ui/back.wav");
        
        // Son de confirmation
        generateConfirmSound(OUTPUT_DIR + "ui/confirm.wav");
        
        // Son d'annulation
        generateCancelSound(OUTPUT_DIR + "ui/cancel.wav");
        
        // Son d'ouverture de menu
        generateMenuOpenSound(OUTPUT_DIR + "ui/menu_open.wav");
        
        // Son de fermeture de menu
        generateMenuCloseSound(OUTPUT_DIR + "ui/menu_close.wav");
        
        // Son de notification
        generateNotificationSound(OUTPUT_DIR + "ui/notification.wav");
        
        // Son de quête complétée
        generateQuestCompleteSound(OUTPUT_DIR + "ui/quest_complete.wav");
        
        // Son de nouvelle quête
        generateQuestNewSound(OUTPUT_DIR + "ui/quest_new.wav");
    }
    
    /**
     * Générer les sons d'attaque pour chaque type de créature
     */
    private static void generateAttackSounds() {
        // Son d'attaque de base
        generateAttackSound(OUTPUT_DIR + "sfx/attack.wav");
        
        // Sons d'attaque par type
        for (CreatureType type : CreatureType.values()) {
            String typeName = type.name().toLowerCase();
            String fileName = OUTPUT_DIR + "sfx/attack_" + typeName + ".wav";
            generateElementalAttackSound(fileName, type);
        }
        
        // Autres sons de combat
        generateBattleStartSound(OUTPUT_DIR + "sfx/battle_start.wav");
        generateLevelUpSound(OUTPUT_DIR + "sfx/level_up.wav");
        generateCaptureAttemptSound(OUTPUT_DIR + "sfx/capture_attempt.wav");
        generateCaptureSuccessSound(OUTPUT_DIR + "sfx/capture_success.wav");
        generateCaptureFailSound(OUTPUT_DIR + "sfx/capture_fail.wav");
        generateHealSound(OUTPUT_DIR + "sfx/heal.wav");
        generateItemUseSound(OUTPUT_DIR + "sfx/item_use.wav");
        generateEvolutionSound(OUTPUT_DIR + "sfx/evolution.wav");
    }
    
    /**
     * Générer les sons ambiants
     */
    private static void generateAmbientSounds() {
        generateForestAmbientSound(OUTPUT_DIR + "ambient/forest.wav");
        generateCaveAmbientSound(OUTPUT_DIR + "ambient/cave.wav");
        generateTownAmbientSound(OUTPUT_DIR + "ambient/town.wav");
        generateRainAmbientSound(OUTPUT_DIR + "ambient/rain.wav");
        generateThunderAmbientSound(OUTPUT_DIR + "ambient/thunder.wav");
        generateWindAmbientSound(OUTPUT_DIR + "ambient/wind.wav");
        generateNightAmbientSound(OUTPUT_DIR + "ambient/night.wav");
        generateWaterAmbientSound(OUTPUT_DIR + "ambient/water.wav");
        generateFireAmbientSound(OUTPUT_DIR + "ambient/fire.wav");
    }
    
    /**
     * Générer les musiques de base
     */
    private static void generateBasicMusic() {
        generateTitleMusic(OUTPUT_DIR + "music/title_theme.wav");
        generateOverworldMusic(OUTPUT_DIR + "music/overworld_theme.wav");
        generateBattleNormalMusic(OUTPUT_DIR + "music/battle_normal.wav");
        generateBattleBossMusic(OUTPUT_DIR + "music/battle_boss.wav");
        generateVictoryMusic(OUTPUT_DIR + "music/victory.wav");
        generateDefeatMusic(OUTPUT_DIR + "music/defeat.wav");
        generateCaveMusic(OUTPUT_DIR + "music/cave_theme.wav");
        generateTownMusic(OUTPUT_DIR + "music/town_theme.wav");
        generateFactionTacticienMusic(OUTPUT_DIR + "music/faction_tacticien.wav");
        generateFactionGuerrierMusic(OUTPUT_DIR + "music/faction_guerrier.wav");
    }
    
    /**
     * Générer un son de clic pour l'interface utilisateur
     * @param fileName Nom du fichier de sortie
     */
    private static void generateClickSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.1f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son court et sec
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5 * Math.exp(-20 * t);
                double frequency = 1000 + 500 * Math.exp(-10 * t);
                double sample = amplitude * Math.sin(2 * Math.PI * frequency * t);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de survol pour l'interface utilisateur
     * @param fileName Nom du fichier de sortie
     */
    private static void generateHoverSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.08f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son doux et subtil
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.3 * Math.exp(-15 * t);
                double frequency = 800 + 200 * Math.exp(-5 * t);
                double sample = amplitude * Math.sin(2 * Math.PI * frequency * t);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de retour pour l'interface utilisateur
     * @param fileName Nom du fichier de sortie
     */
    private static void generateBackSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.15f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son descendant
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.4 * Math.exp(-10 * t);
                double frequency = 700 - 300 * t;
                double sample = amplitude * Math.sin(2 * Math.PI * frequency * t);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de confirmation pour l'interface utilisateur
     * @param fileName Nom du fichier de sortie
     */
    private static void generateConfirmSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.3f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son ascendant positif
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5 * Math.exp(-5 * t);
                double frequency1 = 400 + 200 * t;
                double frequency2 = 600 + 300 * t;
                double sample = amplitude * (0.7 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.3 * Math.sin(2 * Math.PI * frequency2 * t));
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son d'annulation pour l'interface utilisateur
     * @param fileName Nom du fichier de sortie
     */
    private static void generateCancelSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.2f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son descendant négatif
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.4 * Math.exp(-8 * t);
                double frequency1 = 600 - 300 * t;
                double frequency2 = 400 - 200 * t;
                double sample = amplitude * (0.7 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.3 * Math.sin(2 * Math.PI * frequency2 * t));
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son d'attaque de base
     * @param fileName Nom du fichier de sortie
     */
    private static void generateAttackSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.4f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son d'impact
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.8 * Math.exp(-10 * t);
                double noise = 2 * random.nextDouble() - 1;
                double frequency = 300 + 100 * Math.exp(-5 * t);
                double sample = amplitude * (0.7 * Math.sin(2 * Math.PI * frequency * t) + 0.3 * noise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son d'attaque élémentaire en fonction du type
     * @param fileName Nom du fichier de sortie
     * @param type Type de créature
     */
    private static void generateElementalAttackSound(String fileName, CreatureType type) {
        try {
            // Paramètres du son
            float duration = 0.6f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Paramètres spécifiques au type
            double baseFrequency = 0;
            double modulationDepth = 0;
            double noiseAmount = 0;
            
            // Définir les paramètres en fonction du type
            switch (type) {
                case FIRE: // Stratège
                    baseFrequency = 800;
                    modulationDepth = 300;
                    noiseAmount = 0.4;
                    break;
                case WATER: // Furieux
                    baseFrequency = 400;
                    modulationDepth = 100;
                    noiseAmount = 0.2;
                    break;
                case EARTH: // Mystique
                    baseFrequency = 300;
                    modulationDepth = 50;
                    noiseAmount = 0.5;
                    break;
                case AIR: // Serein
                    baseFrequency = 600;
                    modulationDepth = 200;
                    noiseAmount = 0.1;
                    break;
                case LIGHT: // Chaotique
                    baseFrequency = 1000;
                    modulationDepth = 400;
                    noiseAmount = 0.3;
                    break;
                case SHADOW: // Ombreux
                    baseFrequency = 200;
                    modulationDepth = 150;
                    noiseAmount = 0.6;
                    break;
                case METAL: // Lumineux
                    baseFrequency = 700;
                    modulationDepth = 250;
                    noiseAmount = 0.25;
                    break;
                case NATURE: // Terrestre
                    baseFrequency = 350;
                    modulationDepth = 80;
                    noiseAmount = 0.35;
                    break;
                case ELECTRIC: // Aérien
                    baseFrequency = 900;
                    modulationDepth = 350;
                    noiseAmount = 0.45;
                    break;
                case ICE: // Aquatique
                    baseFrequency = 500;
                    modulationDepth = 120;
                    noiseAmount = 0.15;
                    break;
                case PSYCHIC: // Spirituel
                    baseFrequency = 750;
                    modulationDepth = 280;
                    noiseAmount = 0.3;
                    break;
                case MYTHICAL: // Ancestral
                    baseFrequency = 450;
                    modulationDepth = 200;
                    noiseAmount = 0.5;
                    break;
                default:
                    baseFrequency = 500;
                    modulationDepth = 150;
                    noiseAmount = 0.3;
                    break;
            }
            
            // Générer le son
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.7 * Math.exp(-5 * t);
                double noise = 2 * random.nextDouble() - 1;
                double frequency = baseFrequency + modulationDepth * Math.sin(2 * Math.PI * 5 * t);
                double sample = amplitude * ((1 - noiseAmount) * Math.sin(2 * Math.PI * frequency * t) + noiseAmount * noise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son élémentaire généré pour " + type.name() + ": " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son élémentaire: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son d'ouverture de menu
     * @param fileName Nom du fichier de sortie
     */
    private static void generateMenuOpenSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.4f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son ascendant
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5 * Math.exp(-3 * t);
                double frequency1 = 300 + 400 * t;
                double frequency2 = 500 + 600 * t;
                double sample = amplitude * (0.6 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.4 * Math.sin(2 * Math.PI * frequency2 * t));
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de fermeture de menu
     * @param fileName Nom du fichier de sortie
     */
    private static void generateMenuCloseSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.3f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son descendant
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5 * Math.exp(-5 * t);
                double frequency1 = 700 - 300 * t;
                double frequency2 = 500 - 200 * t;
                double sample = amplitude * (0.6 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.4 * Math.sin(2 * Math.PI * frequency2 * t));
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de notification
     * @param fileName Nom du fichier de sortie
     */
    private static void generateNotificationSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.5f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de notification
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.6 * Math.exp(-4 * t);
                double frequency1 = 800 + 200 * Math.sin(2 * Math.PI * 10 * t);
                double frequency2 = 1200 + 300 * Math.sin(2 * Math.PI * 8 * t);
                double sample = amplitude * (0.5 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.5 * Math.sin(2 * Math.PI * frequency2 * t));
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de quête complétée
     * @param fileName Nom du fichier de sortie
     */
    private static void generateQuestCompleteSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 1.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de fanfare
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.7;
                if (t < 0.3) {
                    amplitude *= t / 0.3;
                } else if (t > 0.7) {
                    amplitude *= (1.0 - t) / 0.3;
                }
                
                double frequency1 = 523.25; // Do
                double frequency2 = 659.25; // Mi
                double frequency3 = 783.99; // Sol
                
                double phase = 0;
                if (t < 0.3) {
                    phase = 0;
                } else if (t < 0.6) {
                    phase = 1;
                } else {
                    phase = 2;
                }
                
                double sample = 0;
                if (phase == 0) {
                    sample = amplitude * Math.sin(2 * Math.PI * frequency1 * t);
                } else if (phase == 1) {
                    sample = amplitude * Math.sin(2 * Math.PI * frequency2 * t);
                } else {
                    sample = amplitude * Math.sin(2 * Math.PI * frequency3 * t);
                }
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de nouvelle quête
     * @param fileName Nom du fichier de sortie
     */
    private static void generateQuestNewSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.7f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son mystérieux
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.6;
                if (t < 0.1) {
                    amplitude *= t / 0.1;
                } else if (t > 0.6) {
                    amplitude *= (0.7 - t) / 0.1;
                }
                
                double frequency1 = 400 + 100 * Math.sin(2 * Math.PI * 3 * t);
                double frequency2 = 600 + 150 * Math.sin(2 * Math.PI * 2 * t);
                
                double sample = amplitude * (0.6 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.4 * Math.sin(2 * Math.PI * frequency2 * t));
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de début de combat
     * @param fileName Nom du fichier de sortie
     */
    private static void generateBattleStartSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 1.2f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son dramatique
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.8;
                if (t < 0.1) {
                    amplitude *= t / 0.1;
                } else if (t > 1.0) {
                    amplitude *= (1.2 - t) / 0.2;
                }
                
                double noise = 0.2 * (2 * random.nextDouble() - 1);
                double frequency1 = 200 + 50 * Math.sin(2 * Math.PI * 0.5 * t);
                double frequency2 = 300 + 80 * Math.sin(2 * Math.PI * 0.3 * t);
                
                double sample = amplitude * (0.5 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.3 * Math.sin(2 * Math.PI * frequency2 * t) +
                                           0.2 * noise);
                
                // Ajouter un coup de tambour
                if (t > 0.8 && t < 0.9) {
                    double drumAmplitude = 0.9 * (1.0 - Math.abs((t - 0.85) / 0.05));
                    double drumNoise = drumAmplitude * (2 * random.nextDouble() - 1);
                    sample += drumNoise;
                }
                
                // Convertir en 16 bits
                short value = (short) (Math.max(-1.0, Math.min(1.0, sample)) * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de montée de niveau
     * @param fileName Nom du fichier de sortie
     */
    private static void generateLevelUpSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 1.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son ascendant joyeux
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.7;
                if (t < 0.1) {
                    amplitude *= t / 0.1;
                } else if (t > 0.9) {
                    amplitude *= (1.0 - t) / 0.1;
                }
                
                double frequency1 = 400 + 600 * t;
                double frequency2 = 600 + 800 * t;
                
                double sample = amplitude * (0.6 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.4 * Math.sin(2 * Math.PI * frequency2 * t));
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de tentative de capture
     * @param fileName Nom du fichier de sortie
     */
    private static void generateCaptureAttemptSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.8f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de capture
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.6;
                if (t < 0.1) {
                    amplitude *= t / 0.1;
                } else if (t > 0.7) {
                    amplitude *= (0.8 - t) / 0.1;
                }
                
                // Son oscillant
                double frequency = 500 + 300 * Math.sin(2 * Math.PI * 3 * t);
                double sample = amplitude * Math.sin(2 * Math.PI * frequency * t);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de capture réussie
     * @param fileName Nom du fichier de sortie
     */
    private static void generateCaptureSuccessSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 1.5f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de capture réussie
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.7;
                if (t < 0.1) {
                    amplitude *= t / 0.1;
                } else if (t > 1.4) {
                    amplitude *= (1.5 - t) / 0.1;
                }
                
                double sample = 0;
                
                // Première partie : son oscillant
                if (t < 0.5) {
                    double localT = t / 0.5;
                    double frequency = 400 + 200 * Math.sin(2 * Math.PI * 4 * localT);
                    sample = amplitude * Math.sin(2 * Math.PI * frequency * t);
                }
                // Deuxième partie : fanfare
                else {
                    double localT = (t - 0.5) / 1.0;
                    double frequency1 = 523.25; // Do
                    double frequency2 = 659.25; // Mi
                    double frequency3 = 783.99; // Sol
                    
                    if (localT < 0.3) {
                        sample = amplitude * Math.sin(2 * Math.PI * frequency1 * t);
                    } else if (localT < 0.6) {
                        sample = amplitude * Math.sin(2 * Math.PI * frequency2 * t);
                    } else {
                        sample = amplitude * Math.sin(2 * Math.PI * frequency3 * t);
                    }
                }
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de capture échouée
     * @param fileName Nom du fichier de sortie
     */
    private static void generateCaptureFailSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.6f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de capture échouée
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.6;
                if (t < 0.1) {
                    amplitude *= t / 0.1;
                } else if (t > 0.5) {
                    amplitude *= (0.6 - t) / 0.1;
                }
                
                double frequency = 300 - 100 * t;
                double sample = amplitude * Math.sin(2 * Math.PI * frequency * t);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de soin
     * @param fileName Nom du fichier de sortie
     */
    private static void generateHealSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.8f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de soin
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5;
                if (t < 0.1) {
                    amplitude *= t / 0.1;
                } else if (t > 0.7) {
                    amplitude *= (0.8 - t) / 0.1;
                }
                
                double frequency1 = 600 + 200 * Math.sin(2 * Math.PI * 2 * t);
                double frequency2 = 900 + 300 * Math.sin(2 * Math.PI * 3 * t);
                
                double sample = amplitude * (0.7 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                           0.3 * Math.sin(2 * Math.PI * frequency2 * t));
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son d'utilisation d'objet
     * @param fileName Nom du fichier de sortie
     */
    private static void generateItemUseSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 0.4f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son d'utilisation d'objet
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5;
                if (t < 0.05) {
                    amplitude *= t / 0.05;
                } else if (t > 0.35) {
                    amplitude *= (0.4 - t) / 0.05;
                }
                
                double frequency = 700 + 200 * Math.exp(-5 * t);
                double sample = amplitude * Math.sin(2 * Math.PI * frequency * t);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son d'évolution
     * @param fileName Nom du fichier de sortie
     */
    private static void generateEvolutionSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 2.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son d'évolution
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.7;
                if (t < 0.2) {
                    amplitude *= t / 0.2;
                } else if (t > 1.8) {
                    amplitude *= (2.0 - t) / 0.2;
                }
                
                double sample = 0;
                
                // Première partie : montée
                if (t < 1.0) {
                    double localT = t;
                    double frequency1 = 300 + 500 * localT;
                    double frequency2 = 450 + 750 * localT;
                    
                    sample = amplitude * (0.6 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                        0.4 * Math.sin(2 * Math.PI * frequency2 * t));
                }
                // Deuxième partie : explosion
                else {
                    double localT = t - 1.0;
                    double noise = 0.3 * (2 * random.nextDouble() - 1);
                    double frequency1 = 800 + 200 * Math.sin(2 * Math.PI * 5 * localT);
                    double frequency2 = 1200 + 300 * Math.sin(2 * Math.PI * 7 * localT);
                    
                    sample = amplitude * (0.4 * Math.sin(2 * Math.PI * frequency1 * t) + 
                                        0.3 * Math.sin(2 * Math.PI * frequency2 * t) +
                                        0.3 * noise);
                }
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son ambiant de forêt
     * @param fileName Nom du fichier de sortie
     */
    private static void generateForestAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 10.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son ambiant de forêt
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.3;
                
                // Bruits de feuilles
                double leafNoise = 0.1 * (2 * random.nextDouble() - 1) * Math.exp(-Math.sin(2 * Math.PI * 0.1 * t) - 0.5);
                
                // Bruits d'oiseaux
                double birdNoise = 0;
                if ((t % 2.0) > 1.8 && (t % 2.0) < 1.9) {
                    double birdT = (t % 2.0 - 1.8) / 0.1;
                    double birdFreq = 2000 + 500 * Math.sin(2 * Math.PI * 10 * birdT);
                    birdNoise = 0.2 * Math.sin(2 * Math.PI * birdFreq * birdT) * Math.exp(-80 * (t % 2.0));
                }
                
                // Bruits de vent
                double windNoise = 0.15 * (2 * random.nextDouble() - 1) * (0.5 + 0.5 * Math.sin(2 * Math.PI * 0.05 * t));
                
                double sample = amplitude * (leafNoise + birdNoise + windNoise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son ambiant de caverne
     * @param fileName Nom du fichier de sortie
     */
    private static void generateCaveAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 10.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son ambiant de caverne
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.4;
                
                // Réverbération
                double reverb = 0.2 * Math.sin(2 * Math.PI * 50 * t) * Math.exp(-3 * (t % 1.0));
                
                // Gouttes d'eau
                double dropNoise = 0;
                if ((t % 3.0) > 2.9 && (t % 3.0) < 2.95) {
                    double dropT = (t % 3.0 - 2.9) / 0.05;
                    dropNoise = 0.3 * Math.sin(2 * Math.PI * 1000 * dropT) * Math.exp(-20 * dropT);
                }
                
                // Échos
                double echoNoise = 0.1 * (2 * random.nextDouble() - 1) * Math.exp(-Math.sin(2 * Math.PI * 0.2 * t));
                
                double sample = amplitude * (reverb + dropNoise + echoNoise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son ambiant de ville
     * @param fileName Nom du fichier de sortie
     */
    private static void generateTownAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 10.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son ambiant de ville
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.3;
                
                // Bruits de foule
                double crowdNoise = 0.15 * (2 * random.nextDouble() - 1) * (0.7 + 0.3 * Math.sin(2 * Math.PI * 0.1 * t));
                
                // Bruits de pas
                double footstepsNoise = 0;
                if ((t % 0.5) < 0.05 || ((t % 0.5) > 0.25 && (t % 0.5) < 0.3)) {
                    footstepsNoise = 0.1 * (2 * random.nextDouble() - 1);
                }
                
                // Bruits de commerce
                double marketNoise = 0.1 * (2 * random.nextDouble() - 1) * (0.5 + 0.5 * Math.sin(2 * Math.PI * 0.05 * t + 1.0));
                
                double sample = amplitude * (crowdNoise + footstepsNoise + marketNoise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de pluie
     * @param fileName Nom du fichier de sortie
     */
    private static void generateRainAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 10.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de pluie
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.4;
                
                // Bruit de pluie continu
                double rainNoise = 0.3 * (2 * random.nextDouble() - 1);
                
                // Gouttes plus fortes occasionnelles
                double heavyDrops = 0;
                if (random.nextDouble() < 0.01) {
                    heavyDrops = 0.2 * (2 * random.nextDouble() - 1);
                }
                
                // Tonnerre distant occasionnel
                double thunder = 0;
                if (random.nextDouble() < 0.001) {
                    thunder = 0.5 * Math.exp(-5 * (t % 0.5)) * (2 * random.nextDouble() - 1);
                }
                
                double sample = amplitude * (rainNoise + heavyDrops + thunder);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de tonnerre
     * @param fileName Nom du fichier de sortie
     */
    private static void generateThunderAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 5.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de tonnerre
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0;
                
                // Éclair initial
                if (t < 0.1) {
                    amplitude = 0.9 * t / 0.1;
                } else if (t < 0.2) {
                    amplitude = 0.9 * (0.2 - t) / 0.1;
                }
                
                // Grondement du tonnerre
                if (t > 0.5) {
                    double thunderT = t - 0.5;
                    amplitude = 0.8 * Math.exp(-0.5 * thunderT);
                }
                
                // Bruit
                double noise = 2 * random.nextDouble() - 1;
                
                double sample = amplitude * noise;
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de vent
     * @param fileName Nom du fichier de sortie
     */
    private static void generateWindAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 10.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de vent
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.4;
                
                // Modulation du vent
                double windModulation = 0.5 + 0.5 * Math.sin(2 * Math.PI * 0.1 * t);
                
                // Bruit de vent
                double windNoise = 0.3 * (2 * random.nextDouble() - 1) * windModulation;
                
                // Sifflements occasionnels
                double whistling = 0;
                if ((t % 4.0) > 3.0 && (t % 4.0) < 3.5) {
                    double whistleT = (t % 4.0 - 3.0) / 0.5;
                    double whistleFreq = 500 + 300 * Math.sin(2 * Math.PI * 1 * whistleT);
                    whistling = 0.2 * Math.sin(2 * Math.PI * whistleFreq * whistleT) * Math.exp(-2 * Math.pow(whistleT - 0.25, 2));
                }
                
                double sample = amplitude * (windNoise + whistling);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son ambiant nocturne
     * @param fileName Nom du fichier de sortie
     */
    private static void generateNightAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 10.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son ambiant nocturne
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.3;
                
                // Bruits d'insectes nocturnes (grillons)
                double cricketNoise = 0;
                if ((t % 0.2) < 0.05) {
                    cricketNoise = 0.15 * Math.sin(2 * Math.PI * 4000 * t) * Math.exp(-80 * (t % 0.2));
                }
                
                // Bruits de hibou
                double owlNoise = 0;
                if ((t % 5.0) > 4.8 && (t % 5.0) < 4.9) {
                    double owlT = (t % 5.0 - 4.8) / 0.1;
                    owlNoise = 0.2 * Math.sin(2 * Math.PI * 300 * owlT) * Math.exp(-5 * Math.pow(owlT - 0.5, 2));
                }
                
                // Bruits de vent léger
                double windNoise = 0.1 * (2 * random.nextDouble() - 1) * (0.5 + 0.5 * Math.sin(2 * Math.PI * 0.05 * t));
                
                double sample = amplitude * (cricketNoise + owlNoise + windNoise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son d'eau
     * @param fileName Nom du fichier de sortie
     */
    private static void generateWaterAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 10.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son d'eau
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.4;
                
                // Bruit d'eau qui coule
                double flowNoise = 0.25 * (2 * random.nextDouble() - 1) * (0.7 + 0.3 * Math.sin(2 * Math.PI * 0.2 * t));
                
                // Éclaboussures occasionnelles
                double splashNoise = 0;
                if (random.nextDouble() < 0.005) {
                    splashNoise = 0.3 * (2 * random.nextDouble() - 1);
                }
                
                // Bulles
                double bubbleNoise = 0;
                if (random.nextDouble() < 0.01) {
                    bubbleNoise = 0.15 * Math.sin(2 * Math.PI * 1000 * (t % 0.01)) * Math.exp(-100 * (t % 0.01));
                }
                
                double sample = amplitude * (flowNoise + splashNoise + bubbleNoise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de feu
     * @param fileName Nom du fichier de sortie
     */
    private static void generateFireAmbientSound(String fileName) {
        try {
            // Paramètres du son
            float duration = 10.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer un son de feu
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.4;
                
                // Crépitement continu
                double crackleNoise = 0.3 * (2 * random.nextDouble() - 1) * (0.6 + 0.4 * Math.sin(2 * Math.PI * 0.3 * t));
                
                // Craquements plus forts
                double loudCrackle = 0;
                if (random.nextDouble() < 0.02) {
                    loudCrackle = 0.25 * (2 * random.nextDouble() - 1);
                }
                
                // Bruit de flammes
                double flameNoise = 0.15 * (2 * random.nextDouble() - 1) * Math.exp(-Math.sin(2 * Math.PI * 0.5 * t));
                
                double sample = amplitude * (crackleNoise + loudCrackle + flameNoise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Son généré: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du son: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique d'écran titre
     * @param fileName Nom du fichier de sortie
     */
    private static void generateTitleMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 20.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique d'écran titre
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5;
                
                // Mélodie principale
                double melody = 0;
                int section = (int) (t / 5.0); // Changer de section toutes les 5 secondes
                double sectionT = t % 5.0;
                
                // Définir les notes en fonction de la section
                double[] frequencies;
                switch (section % 4) {
                    case 0: // Introduction
                        frequencies = new double[] {261.63, 329.63, 392.00, 523.25}; // Do, Mi, Sol, Do
                        break;
                    case 1: // Développement
                        frequencies = new double[] {293.66, 349.23, 440.00, 587.33}; // Ré, Fa, La, Ré
                        break;
                    case 2: // Climax
                        frequencies = new double[] {329.63, 415.30, 493.88, 659.25}; // Mi, Sol#, Si, Mi
                        break;
                    case 3: // Conclusion
                        frequencies = new double[] {392.00, 466.16, 523.25, 622.25}; // Sol, Si♭, Do, Ré#
                        break;
                    default:
                        frequencies = new double[] {261.63, 329.63, 392.00, 523.25}; // Do, Mi, Sol, Do
                }
                
                // Jouer les notes en séquence
                int noteIndex = (int) (sectionT * 2) % frequencies.length;
                double noteFreq = frequencies[noteIndex];
                double noteDuration = 0.25; // Durée de chaque note
                double noteT = sectionT % noteDuration;
                double noteAmplitude = Math.exp(-4 * noteT / noteDuration);
                
                melody = 0.6 * noteAmplitude * Math.sin(2 * Math.PI * noteFreq * t);
                
                // Accompagnement
                double accompaniment = 0;
                double bassFreq = frequencies[0] / 2; // Une octave plus bas
                accompaniment = 0.3 * Math.sin(2 * Math.PI * bassFreq * t);
                
                // Percussion
                double percussion = 0;
                if ((t % 1.0) < 0.05) {
                    percussion = 0.2 * (2 * random.nextDouble() - 1) * Math.exp(-20 * (t % 1.0));
                }
                
                double sample = amplitude * (melody + accompaniment + percussion);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique d'exploration
     * @param fileName Nom du fichier de sortie
     */
    private static void generateOverworldMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 20.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique d'exploration
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5;
                
                // Mélodie principale - plus légère et enjouée
                double melody = 0;
                int section = (int) (t / 4.0); // Changer de section toutes les 4 secondes
                double sectionT = t % 4.0;
                
                // Définir les notes en fonction de la section
                double[] frequencies;
                switch (section % 5) {
                    case 0:
                        frequencies = new double[] {392.00, 440.00, 493.88, 523.25}; // Sol, La, Si, Do
                        break;
                    case 1:
                        frequencies = new double[] {349.23, 392.00, 440.00, 493.88}; // Fa, Sol, La, Si
                        break;
                    case 2:
                        frequencies = new double[] {329.63, 349.23, 392.00, 440.00}; // Mi, Fa, Sol, La
                        break;
                    case 3:
                        frequencies = new double[] {293.66, 329.63, 349.23, 392.00}; // Ré, Mi, Fa, Sol
                        break;
                    case 4:
                        frequencies = new double[] {261.63, 293.66, 329.63, 349.23}; // Do, Ré, Mi, Fa
                        break;
                    default:
                        frequencies = new double[] {392.00, 440.00, 493.88, 523.25}; // Sol, La, Si, Do
                }
                
                // Jouer les notes en séquence
                int noteIndex = (int) (sectionT * 4) % frequencies.length;
                double noteFreq = frequencies[noteIndex];
                double noteDuration = 0.25; // Durée de chaque note
                double noteT = sectionT % noteDuration;
                double noteAmplitude = Math.exp(-3 * noteT / noteDuration);
                
                melody = 0.5 * noteAmplitude * Math.sin(2 * Math.PI * noteFreq * t);
                
                // Accompagnement - plus rythmé
                double accompaniment = 0;
                double chordFreq1 = frequencies[0] / 2; // Basse
                double chordFreq2 = frequencies[1] / 2; // Accord
                double chordFreq3 = frequencies[2] / 2; // Accord
                
                if ((t % 0.5) < 0.25) {
                    accompaniment = 0.2 * Math.sin(2 * Math.PI * chordFreq1 * t);
                } else {
                    accompaniment = 0.15 * (Math.sin(2 * Math.PI * chordFreq2 * t) + 
                                          Math.sin(2 * Math.PI * chordFreq3 * t));
                }
                
                // Percussion légère
                double percussion = 0;
                if ((t % 0.5) < 0.05) {
                    percussion = 0.15 * (2 * random.nextDouble() - 1) * Math.exp(-20 * (t % 0.5));
                }
                
                double sample = amplitude * (melody + accompaniment + percussion);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique de combat normal
     * @param fileName Nom du fichier de sortie
     */
    private static void generateBattleNormalMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 20.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique de combat
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.6;
                
                // Mélodie principale - plus intense et rapide
                double melody = 0;
                int section = (int) (t / 5.0); // Changer de section toutes les 5 secondes
                double sectionT = t % 5.0;
                
                // Définir les notes en fonction de la section
                double[] frequencies;
                switch (section % 4) {
                    case 0:
                        frequencies = new double[] {329.63, 392.00, 440.00, 493.88}; // Mi, Sol, La, Si
                        break;
                    case 1:
                        frequencies = new double[] {293.66, 349.23, 392.00, 440.00}; // Ré, Fa, Sol, La
                        break;
                    case 2:
                        frequencies = new double[] {261.63, 311.13, 349.23, 392.00}; // Do, Ré#, Fa, Sol
                        break;
                    case 3:
                        frequencies = new double[] {233.08, 277.18, 329.63, 349.23}; // Si♭, Do#, Mi, Fa
                        break;
                    default:
                        frequencies = new double[] {329.63, 392.00, 440.00, 493.88}; // Mi, Sol, La, Si
                }
                
                // Jouer les notes en séquence rapide
                int noteIndex = (int) (sectionT * 8) % frequencies.length;
                double noteFreq = frequencies[noteIndex];
                double noteDuration = 0.125; // Durée de chaque note (plus courte)
                double noteT = sectionT % noteDuration;
                double noteAmplitude = Math.exp(-2 * noteT / noteDuration);
                
                melody = 0.5 * noteAmplitude * Math.sin(2 * Math.PI * noteFreq * t);
                
                // Accompagnement - plus agressif
                double accompaniment = 0;
                double bassFreq = frequencies[0] / 2; // Basse
                
                if ((t % 0.25) < 0.125) {
                    accompaniment = 0.3 * Math.sin(2 * Math.PI * bassFreq * t);
                } else {
                    accompaniment = 0.2 * Math.sin(2 * Math.PI * (bassFreq * 1.5) * t);
                }
                
                // Percussion intense
                double percussion = 0;
                if ((t % 0.25) < 0.03) {
                    percussion = 0.25 * (2 * random.nextDouble() - 1) * Math.exp(-30 * (t % 0.25));
                }
                
                double sample = amplitude * (melody + accompaniment + percussion);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique de combat de boss
     * @param fileName Nom du fichier de sortie
     */
    private static void generateBattleBossMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 20.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique de combat de boss
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.7;
                
                // Mélodie principale - épique et dramatique
                double melody = 0;
                int section = (int) (t / 5.0); // Changer de section toutes les 5 secondes
                double sectionT = t % 5.0;
                
                // Définir les notes en fonction de la section
                double[] frequencies;
                switch (section % 4) {
                    case 0:
                        frequencies = new double[] {196.00, 233.08, 261.63, 311.13}; // Sol, Si♭, Do, Ré#
                        break;
                    case 1:
                        frequencies = new double[] {174.61, 207.65, 233.08, 277.18}; // Fa, Sol#, Si♭, Do#
                        break;
                    case 2:
                        frequencies = new double[] {164.81, 196.00, 220.00, 261.63}; // Mi, Sol, La, Do
                        break;
                    case 3:
                        frequencies = new double[] {146.83, 174.61, 196.00, 233.08}; // Ré, Fa, Sol, Si♭
                        break;
                    default:
                        frequencies = new double[] {196.00, 233.08, 261.63, 311.13}; // Sol, Si♭, Do, Ré#
                }
                
                // Jouer les notes en séquence dramatique
                int noteIndex = (int) (sectionT * 2) % frequencies.length;
                double noteFreq = frequencies[noteIndex];
                double noteDuration = 0.5; // Durée de chaque note (plus longue)
                double noteT = sectionT % noteDuration;
                double noteAmplitude = Math.exp(-1 * noteT / noteDuration);
                
                melody = 0.4 * noteAmplitude * Math.sin(2 * Math.PI * noteFreq * t);
                
                // Accompagnement - lourd et menaçant
                double accompaniment = 0;
                double bassFreq = frequencies[0] / 4; // Très grave
                
                accompaniment = 0.3 * Math.sin(2 * Math.PI * bassFreq * t) * (0.8 + 0.2 * Math.sin(2 * Math.PI * 0.25 * t));
                
                // Percussion lourde
                double percussion = 0;
                if ((t % 0.5) < 0.05) {
                    percussion = 0.3 * (2 * random.nextDouble() - 1) * Math.exp(-10 * (t % 0.5));
                }
                
                // Choeurs
                double choir = 0;
                if (section % 2 == 1) { // Ajouter des choeurs dans certaines sections
                    double choirFreq = noteFreq * 2;
                    choir = 0.2 * Math.sin(2 * Math.PI * choirFreq * t) * (0.5 + 0.5 * Math.sin(2 * Math.PI * 0.1 * t));
                }
                
                double sample = amplitude * (melody + accompaniment + percussion + choir);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique de victoire
     * @param fileName Nom du fichier de sortie
     */
    private static void generateVictoryMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 5.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique de victoire
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.6;
                
                // Fanfare de victoire
                double fanfare = 0;
                
                // Séquence de notes triomphales
                if (t < 1.0) {
                    // Do
                    double freq = 523.25;
                    fanfare = Math.sin(2 * Math.PI * freq * t);
                } else if (t < 2.0) {
                    // Mi
                    double freq = 659.25;
                    fanfare = Math.sin(2 * Math.PI * freq * t);
                } else if (t < 3.0) {
                    // Sol
                    double freq = 783.99;
                    fanfare = Math.sin(2 * Math.PI * freq * t);
                } else if (t < 4.0) {
                    // Do (octave supérieure)
                    double freq = 1046.50;
                    fanfare = Math.sin(2 * Math.PI * freq * t);
                } else {
                    // Accord final
                    double freq1 = 523.25; // Do
                    double freq2 = 659.25; // Mi
                    double freq3 = 783.99; // Sol
                    double freq4 = 1046.50; // Do (octave supérieure)
                    fanfare = 0.25 * (Math.sin(2 * Math.PI * freq1 * t) +
                                    Math.sin(2 * Math.PI * freq2 * t) +
                                    Math.sin(2 * Math.PI * freq3 * t) +
                                    Math.sin(2 * Math.PI * freq4 * t));
                }
                
                // Enveloppe d'amplitude
                double env = 1.0;
                if (t < 0.1) {
                    env = t / 0.1; // Attaque
                } else if (t > 4.5) {
                    env = (5.0 - t) / 0.5; // Relâchement
                }
                
                double sample = amplitude * env * fanfare;
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique de défaite
     * @param fileName Nom du fichier de sortie
     */
    private static void generateDefeatMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 5.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique de défaite
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5;
                
                // Mélodie triste
                double melody = 0;
                
                // Séquence de notes descendantes
                if (t < 1.0) {
                    // La
                    double freq = 440.00;
                    melody = Math.sin(2 * Math.PI * freq * t);
                } else if (t < 2.0) {
                    // Fa
                    double freq = 349.23;
                    melody = Math.sin(2 * Math.PI * freq * t);
                } else if (t < 3.0) {
                    // Ré
                    double freq = 293.66;
                    melody = Math.sin(2 * Math.PI * freq * t);
                } else if (t < 4.0) {
                    // Si bémol
                    double freq = 233.08;
                    melody = Math.sin(2 * Math.PI * freq * t);
                } else {
                    // Accord final mineur
                    double freq1 = 220.00; // La
                    double freq2 = 261.63; // Do
                    double freq3 = 329.63; // Mi
                    melody = 0.33 * (Math.sin(2 * Math.PI * freq1 * t) +
                                   Math.sin(2 * Math.PI * freq2 * t) +
                                   Math.sin(2 * Math.PI * freq3 * t));
                }
                
                // Enveloppe d'amplitude
                double env = 1.0;
                if (t < 0.2) {
                    env = t / 0.2; // Attaque lente
                } else if (t > 4.0) {
                    env = (5.0 - t) / 1.0; // Relâchement long
                }
                
                double sample = amplitude * env * melody;
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique de caverne
     * @param fileName Nom du fichier de sortie
     */
    private static void generateCaveMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 20.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique de caverne
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.4;
                
                // Mélodie mystérieuse
                double melody = 0;
                int section = (int) (t / 5.0); // Changer de section toutes les 5 secondes
                double sectionT = t % 5.0;
                
                // Définir les notes en fonction de la section
                double[] frequencies;
                switch (section % 4) {
                    case 0:
                        frequencies = new double[] {146.83, 174.61, 196.00, 220.00}; // Ré, Fa, Sol, La (graves)
                        break;
                    case 1:
                        frequencies = new double[] {130.81, 146.83, 164.81, 196.00}; // Do, Ré, Mi, Sol (graves)
                        break;
                    case 2:
                        frequencies = new double[] {123.47, 146.83, 174.61, 196.00}; // Si, Ré, Fa, Sol (graves)
                        break;
                    case 3:
                        frequencies = new double[] {110.00, 130.81, 146.83, 164.81}; // La, Do, Ré, Mi (graves)
                        break;
                    default:
                        frequencies = new double[] {146.83, 174.61, 196.00, 220.00}; // Ré, Fa, Sol, La (graves)
                }
                
                // Jouer les notes lentement
                int noteIndex = (int) (sectionT * 0.8) % frequencies.length;
                double noteFreq = frequencies[noteIndex];
                double noteDuration = 1.25; // Durée de chaque note (longue)
                double noteT = sectionT % noteDuration;
                double noteAmplitude = 0.8;
                if (noteT < 0.2) {
                    noteAmplitude *= noteT / 0.2; // Attaque lente
                } else if (noteT > 1.0) {
                    noteAmplitude *= (1.25 - noteT) / 0.25; // Relâchement
                }
                
                melody = 0.4 * noteAmplitude * Math.sin(2 * Math.PI * noteFreq * t);
                
                // Réverbération
                double reverb = 0.2 * Math.sin(2 * Math.PI * noteFreq * (t - 0.3)) * Math.exp(-2 * (t % 1.25));
                
                // Bruits d'eau occasionnels
                double waterDrop = 0;
                if (random.nextDouble() < 0.005) {
                    waterDrop = 0.15 * Math.sin(2 * Math.PI * 1000 * (t % 0.1)) * Math.exp(-20 * (t % 0.1));
                }
                
                double sample = amplitude * (melody + reverb + waterDrop);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique de ville
     * @param fileName Nom du fichier de sortie
     */
    private static void generateTownMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 20.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique de ville
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.5;
                
                // Mélodie joyeuse et accueillante
                double melody = 0;
                int section = (int) (t / 4.0); // Changer de section toutes les 4 secondes
                double sectionT = t % 4.0;
                
                // Définir les notes en fonction de la section
                double[] frequencies;
                switch (section % 3) {
                    case 0:
                        frequencies = new double[] {261.63, 293.66, 329.63, 349.23, 392.00}; // Do, Ré, Mi, Fa, Sol
                        break;
                    case 1:
                        frequencies = new double[] {293.66, 329.63, 349.23, 392.00, 440.00}; // Ré, Mi, Fa, Sol, La
                        break;
                    case 2:
                        frequencies = new double[] {329.63, 349.23, 392.00, 440.00, 493.88}; // Mi, Fa, Sol, La, Si
                        break;
                    default:
                        frequencies = new double[] {261.63, 293.66, 329.63, 349.23, 392.00}; // Do, Ré, Mi, Fa, Sol
                }
                
                // Jouer les notes en séquence
                int noteIndex = (int) (sectionT * 2.5) % frequencies.length;
                double noteFreq = frequencies[noteIndex];
                double noteDuration = 0.4; // Durée de chaque note
                double noteT = sectionT % noteDuration;
                double noteAmplitude = Math.exp(-3 * noteT / noteDuration);
                
                melody = 0.5 * noteAmplitude * Math.sin(2 * Math.PI * noteFreq * t);
                
                // Accompagnement - style valse
                double accompaniment = 0;
                double bassFreq = frequencies[0] / 2; // Basse
                double chordFreq1 = frequencies[2] / 2; // Accord
                double chordFreq2 = frequencies[4] / 2; // Accord
                
                if ((t % 0.6) < 0.2) {
                    accompaniment = 0.3 * Math.sin(2 * Math.PI * bassFreq * t);
                } else {
                    accompaniment = 0.2 * (Math.sin(2 * Math.PI * chordFreq1 * t) + 
                                         Math.sin(2 * Math.PI * chordFreq2 * t));
                }
                
                // Bruits de foule occasionnels
                double crowdNoise = 0.05 * (2 * random.nextDouble() - 1) * (0.5 + 0.5 * Math.sin(2 * Math.PI * 0.1 * t));
                
                double sample = amplitude * (melody + accompaniment + crowdNoise);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique pour la faction des Tacticiens
     * @param fileName Nom du fichier de sortie
     */
    private static void generateFactionTacticienMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 20.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique pour la faction des Tacticiens
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.6;
                
                // Mélodie stratégique et réfléchie
                double melody = 0;
                int section = (int) (t / 5.0); // Changer de section toutes les 5 secondes
                double sectionT = t % 5.0;
                
                // Définir les notes en fonction de la section
                double[] frequencies;
                switch (section % 4) {
                    case 0:
                        frequencies = new double[] {329.63, 392.00, 440.00, 523.25}; // Mi, Sol, La, Do
                        break;
                    case 1:
                        frequencies = new double[] {349.23, 415.30, 466.16, 587.33}; // Fa, Sol#, Si♭, Do#
                        break;
                    case 2:
                        frequencies = new double[] {369.99, 440.00, 493.88, 659.25}; // Fa#, La, Si, Mi
                        break;
                    case 3:
                        frequencies = new double[] {392.00, 466.16, 523.25, 622.25}; // Sol, Si♭, Do, Ré#
                        break;
                    default:
                        frequencies = new double[] {329.63, 392.00, 440.00, 523.25}; // Mi, Sol, La, Do
                }
                
                // Jouer les notes en séquence complexe
                int noteIndex = (int) (sectionT * 1.6) % frequencies.length;
                double noteFreq = frequencies[noteIndex];
                double noteDuration = 0.625; // Durée de chaque note
                double noteT = sectionT % noteDuration;
                double noteAmplitude = Math.exp(-2 * noteT / noteDuration);
                
                melody = 0.5 * noteAmplitude * Math.sin(2 * Math.PI * noteFreq * t);
                
                // Accompagnement - harmonies complexes
                double accompaniment = 0;
                double chordFreq1 = frequencies[0] / 2; // Basse
                double chordFreq2 = frequencies[1] / 2; // Accord
                double chordFreq3 = frequencies[3] / 2; // Accord
                
                accompaniment = 0.2 * (Math.sin(2 * Math.PI * chordFreq1 * t) + 
                                     0.7 * Math.sin(2 * Math.PI * chordFreq2 * t) +
                                     0.5 * Math.sin(2 * Math.PI * chordFreq3 * t));
                
                // Percussion légère
                double percussion = 0;
                if ((t % 1.0) < 0.05) {
                    percussion = 0.15 * (2 * random.nextDouble() - 1) * Math.exp(-20 * (t % 1.0));
                }
                
                double sample = amplitude * (melody + accompaniment + percussion);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer une musique pour la faction des Guerriers
     * @param fileName Nom du fichier de sortie
     */
    private static void generateFactionGuerrierMusic(String fileName) {
        try {
            // Paramètres du son
            float duration = 20.0f; // Durée en secondes
            int numSamples = (int) (SAMPLE_RATE * duration);
            byte[] buffer = new byte[numSamples * 2]; // 16 bits = 2 bytes par échantillon
            
            // Générer une musique pour la faction des Guerriers
            for (int i = 0; i < numSamples; i++) {
                double t = i / SAMPLE_RATE;
                double amplitude = 0.6;
                
                // Mélodie puissante et martiale
                double melody = 0;
                int section = (int) (t / 4.0); // Changer de section toutes les 4 secondes
                double sectionT = t % 4.0;
                
                // Définir les notes en fonction de la section
                double[] frequencies;
                switch (section % 4) {
                    case 0:
                        frequencies = new double[] {196.00, 233.08, 261.63, 311.13}; // Sol, Si♭, Do, Ré#
                        break;
                    case 1:
                        frequencies = new double[] {220.00, 261.63, 293.66, 349.23}; // La, Do, Ré, Fa
                        break;
                    case 2:
                        frequencies = new double[] {246.94, 293.66, 329.63, 392.00}; // Si, Ré, Mi, Sol
                        break;
                    case 3:
                        frequencies = new double[] {261.63, 311.13, 349.23, 415.30}; // Do, Ré#, Fa, Sol#
                        break;
                    default:
                        frequencies = new double[] {196.00, 233.08, 261.63, 311.13}; // Sol, Si♭, Do, Ré#
                }
                
                // Jouer les notes en séquence rythmique
                int noteIndex = (int) (sectionT * 2) % frequencies.length;
                double noteFreq = frequencies[noteIndex];
                double noteDuration = 0.5; // Durée de chaque note
                double noteT = sectionT % noteDuration;
                double noteAmplitude = 0.9;
                if (noteT < 0.05) {
                    noteAmplitude *= noteT / 0.05; // Attaque rapide
                } else if (noteT > 0.4) {
                    noteAmplitude *= (0.5 - noteT) / 0.1; // Relâchement
                }
                
                melody = 0.5 * noteAmplitude * Math.sin(2 * Math.PI * noteFreq * t);
                
                // Tambours de guerre
                double drums = 0;
                if ((t % 0.5) < 0.05) {
                    drums = 0.4 * (2 * random.nextDouble() - 1) * Math.exp(-30 * (t % 0.5));
                } else if (((t % 0.5) > 0.25) && ((t % 0.5) < 0.3)) {
                    drums = 0.2 * (2 * random.nextDouble() - 1) * Math.exp(-30 * ((t % 0.5) - 0.25));
                }
                
                // Choeurs masculins
                double choir = 0;
                if (section % 2 == 0) { // Ajouter des choeurs dans certaines sections
                    double choirFreq = noteFreq / 2;
                    choir = 0.3 * Math.sin(2 * Math.PI * choirFreq * t) * (0.7 + 0.3 * Math.sin(2 * Math.PI * 0.25 * t));
                }
                
                double sample = amplitude * (melody + drums + choir);
                
                // Convertir en 16 bits
                short value = (short) (sample * Short.MAX_VALUE);
                buffer[i * 2] = (byte) (value & 0xFF);
                buffer[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
            }
            
            // Sauvegarder le son
            saveWavFile(buffer, fileName);
            System.out.println("Musique générée: " + fileName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la musique: " + e.getMessage());
        }
    }
    
    /**
     * Générer un son de type "bip" pour les interfaces utilisateur
     * @param frequency Fréquence du bip (Hz)
     * @param duration Durée du son (secondes)
     * @return Tableau d'échantillons audio
     */
    public static double[] generateUiBeep(double frequency, double duration) {
        int sampleRate = 44100;
        int numSamples = (int) (duration * sampleRate);
        double[] samples = new double[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            double time = i / (double) sampleRate;
            double amplitude = Math.max(0, 1.0 - time / duration);
            samples[i] = amplitude * Math.sin(2 * Math.PI * frequency * time);
        }
        
        return samples;
    }
    
    /**
     * Sauvegarder un tableau d'octets en fichier WAV
     * @param buffer Tableau d'octets contenant les données audio
     * @param fileName Nom du fichier de sortie
     * @throws IOException En cas d'erreur d'écriture
     */
    private static void saveWavFile(byte[] buffer, String fileName) throws IOException {
        // Créer le format audio
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        
        // Créer le flux audio
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream ais = new AudioInputStream(bais, format, buffer.length / format.getFrameSize());
        
        // Créer le dossier parent si nécessaire
        Path path = Paths.get(fileName);
        Files.createDirectories(path.getParent());
        
        // Sauvegarder le fichier
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(fileName));
    }
    
    /**
     * Méthode principale pour générer tous les sons
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        generateAllSounds();
    }
}

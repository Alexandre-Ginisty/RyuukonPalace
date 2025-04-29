package com.ryuukonpalace.game.audio;

import com.ryuukonpalace.game.creatures.CreatureType;

/**
 * Classe utilitaire pour la gestion des effets sonores dans le jeu.
 * Permet de jouer facilement des sons en fonction du contexte.
 */
public class SoundEffect {
    
    private static AudioManager audioManager = AudioManager.getInstance();
    
    /**
     * Types d'effets sonores pour les combats
     */
    public enum CombatSoundType {
        ATTACK,
        SPECIAL_ATTACK,
        DEFEND,
        ITEM_USE,
        CAPTURE_ATTEMPT,
        CAPTURE_SUCCESS,
        CAPTURE_FAIL,
        BATTLE_START,
        BATTLE_VICTORY,
        BATTLE_DEFEAT,
        LEVEL_UP,
        EVOLUTION,
        FLEE
    }
    
    /**
     * Types d'effets sonores pour l'interface utilisateur
     */
    public enum UISoundType {
        CLICK,
        HOVER,
        CONFIRM,
        CANCEL,
        MENU_OPEN,
        MENU_CLOSE,
        NOTIFICATION,
        QUEST_COMPLETE,
        QUEST_NEW,
        SAVE,
        LOAD,
        ERROR
    }
    
    /**
     * Types d'effets sonores ambiants
     */
    public enum AmbientSoundType {
        FOREST,
        CAVE,
        TOWN,
        RAIN,
        THUNDER,
        WIND,
        NIGHT,
        WATER,
        FIRE
    }
    
    /**
     * Jouer un son de combat
     * @param type Type de son de combat
     */
    public static void playCombatSound(CombatSoundType type) {
        switch (type) {
            case ATTACK:
                audioManager.playSFX("attack");
                break;
            case SPECIAL_ATTACK:
                audioManager.playSFX("attack_special");
                break;
            case DEFEND:
                audioManager.playSFX("defend");
                break;
            case ITEM_USE:
                audioManager.playSFX("item_use");
                break;
            case CAPTURE_ATTEMPT:
                audioManager.playSFX("capture_attempt");
                break;
            case CAPTURE_SUCCESS:
                audioManager.playSFX("capture_success");
                break;
            case CAPTURE_FAIL:
                audioManager.playSFX("capture_fail");
                break;
            case BATTLE_START:
                audioManager.playSFX("battle_start");
                break;
            case BATTLE_VICTORY:
                audioManager.playMusic("victory");
                break;
            case BATTLE_DEFEAT:
                audioManager.playMusic("defeat");
                break;
            case LEVEL_UP:
                audioManager.playSFX("level_up");
                break;
            case EVOLUTION:
                audioManager.playSFX("evolution");
                break;
            case FLEE:
                audioManager.playSFX("flee");
                break;
        }
    }
    
    /**
     * Jouer un son de combat basé sur le type d'élément
     * @param type Type de créature
     */
    public static void playElementalAttackSound(CreatureType type) {
        switch (type) {
            case FIRE:
                audioManager.playSFX("attack_fire");
                break;
            case WATER:
                audioManager.playSFX("attack_water");
                break;
            case EARTH:
                audioManager.playSFX("attack_earth");
                break;
            case AIR:
                audioManager.playSFX("attack_air");
                break;
            case LIGHT:
                audioManager.playSFX("attack_light");
                break;
            case SHADOW:
                audioManager.playSFX("attack_dark");
                break;
            default:
                audioManager.playSFX("attack");
                break;
        }
    }
    
    /**
     * Jouer un son d'interface utilisateur
     * @param type Type de son UI
     */
    public static void playUISound(UISoundType type) {
        switch (type) {
            case CLICK:
                audioManager.playUISound("click");
                break;
            case HOVER:
                audioManager.playUISound("hover");
                break;
            case CONFIRM:
                audioManager.playUISound("confirm");
                break;
            case CANCEL:
                audioManager.playUISound("cancel");
                break;
            case MENU_OPEN:
                audioManager.playUISound("menu_open");
                break;
            case MENU_CLOSE:
                audioManager.playUISound("menu_close");
                break;
            case NOTIFICATION:
                audioManager.playUISound("notification");
                break;
            case QUEST_COMPLETE:
                audioManager.playUISound("quest_complete");
                break;
            case QUEST_NEW:
                audioManager.playUISound("quest_new");
                break;
            case SAVE:
                audioManager.playUISound("save");
                break;
            case LOAD:
                audioManager.playUISound("load");
                break;
            case ERROR:
                audioManager.playUISound("error");
                break;
        }
    }
    
    /**
     * Jouer un son ambiant
     * @param type Type de son ambiant
     * @param loop Si true, le son sera joué en boucle
     */
    public static void playAmbientSound(AmbientSoundType type, boolean loop) {
        switch (type) {
            case FOREST:
                audioManager.playAmbient("forest", loop);
                break;
            case CAVE:
                audioManager.playAmbient("cave", loop);
                break;
            case TOWN:
                audioManager.playAmbient("town", loop);
                break;
            case RAIN:
                audioManager.playAmbient("rain", loop);
                break;
            case THUNDER:
                audioManager.playAmbient("thunder", loop);
                break;
            case WIND:
                audioManager.playAmbient("wind", loop);
                break;
            case NIGHT:
                audioManager.playAmbient("night", loop);
                break;
            case WATER:
                audioManager.playAmbient("water", loop);
                break;
            case FIRE:
                audioManager.playAmbient("fire", loop);
                break;
        }
    }
    
    /**
     * Jouer une musique de fond en fonction de la zone
     * @param zone Nom de la zone
     */
    public static void playMusicForZone(String zone) {
        if (zone == null) {
            return;
        }
        
        zone = zone.toLowerCase();
        
        if (zone.contains("cave") || zone.contains("grotte") || zone.contains("mine")) {
            audioManager.playMusic("cave");
        } else if (zone.contains("town") || zone.contains("ville") || zone.contains("village")) {
            audioManager.playMusic("town");
        } else if (zone.contains("tacticien") || zone.contains("tactician")) {
            audioManager.playMusic("faction_tacticien");
        } else if (zone.contains("guerrier") || zone.contains("warrior")) {
            audioManager.playMusic("faction_guerrier");
        } else {
            // Par défaut, jouer la musique du monde ouvert
            audioManager.playMusic("overworld");
        }
    }
    
    /**
     * Jouer une musique de combat en fonction du type de combat
     * @param isBossBattle Si true, jouer la musique de combat de boss
     */
    public static void playBattleMusic(boolean isBossBattle) {
        if (isBossBattle) {
            audioManager.playMusic("battle_boss");
        } else {
            audioManager.playMusic("battle_normal");
        }
    }
    
    /**
     * Arrêter tous les sons ambiants
     */
    public static void stopAllAmbient() {
        audioManager.stopCategory(AudioManager.CATEGORY_AMBIENT);
    }
    
    /**
     * Arrêter la musique actuelle
     */
    public static void stopMusic() {
        audioManager.stopMusic();
    }
}

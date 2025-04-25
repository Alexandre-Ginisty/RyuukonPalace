package com.ryuukonpalace.game.save;

/**
 * Métadonnées d'une sauvegarde de jeu.
 * Contient les informations essentielles sur une sauvegarde.
 */
public class SaveMetadata {
    
    // Numéro du slot de sauvegarde
    private int slot;
    
    // Nom du joueur
    private String playerName;
    
    // Horodatage de la sauvegarde
    private String timestamp;
    
    // Localisation du joueur
    private String location;
    
    // Niveau du joueur
    private int playerLevel;
    
    // Temps de jeu en secondes
    private int playTime;
    
    // Nombre de variants capturés
    private int capturedVariants;
    
    // Checksum de la sauvegarde
    private String checksum;
    
    // Chemin du fichier de sauvegarde
    private String filePath;
    
    /**
     * Constructeur
     * 
     * @param slot Numéro du slot de sauvegarde
     * @param playerName Nom du joueur
     * @param timestamp Horodatage de la sauvegarde
     * @param location Localisation du joueur
     * @param playerLevel Niveau du joueur
     * @param playTime Temps de jeu en secondes
     * @param capturedVariants Nombre de variants capturés
     * @param checksum Checksum de la sauvegarde
     * @param filePath Chemin du fichier de sauvegarde
     */
    public SaveMetadata(int slot, String playerName, String timestamp, String location, 
                       int playerLevel, int playTime, int capturedVariants, String checksum, String filePath) {
        this.slot = slot;
        this.playerName = playerName;
        this.timestamp = timestamp;
        this.location = location;
        this.playerLevel = playerLevel;
        this.playTime = playTime;
        this.capturedVariants = capturedVariants;
        this.checksum = checksum;
        this.filePath = filePath;
    }
    
    /**
     * Obtenir le numéro du slot de sauvegarde
     * @return Numéro du slot de sauvegarde
     */
    public int getSlot() {
        return slot;
    }
    
    /**
     * Définir le numéro du slot de sauvegarde
     * @param slot Numéro du slot de sauvegarde
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }
    
    /**
     * Obtenir le nom du joueur
     * @return Nom du joueur
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Définir le nom du joueur
     * @param playerName Nom du joueur
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    /**
     * Obtenir l'horodatage de la sauvegarde
     * @return Horodatage de la sauvegarde
     */
    public String getTimestamp() {
        return timestamp;
    }
    
    /**
     * Définir l'horodatage de la sauvegarde
     * @param timestamp Horodatage de la sauvegarde
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Obtenir la localisation du joueur
     * @return Localisation du joueur
     */
    public String getLocation() {
        return location;
    }
    
    /**
     * Définir la localisation du joueur
     * @param location Localisation du joueur
     */
    public void setLocation(String location) {
        this.location = location;
    }
    
    /**
     * Obtenir le niveau du joueur
     * @return Niveau du joueur
     */
    public int getPlayerLevel() {
        return playerLevel;
    }
    
    /**
     * Définir le niveau du joueur
     * @param playerLevel Niveau du joueur
     */
    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }
    
    /**
     * Obtenir le temps de jeu en secondes
     * @return Temps de jeu en secondes
     */
    public int getPlayTime() {
        return playTime;
    }
    
    /**
     * Définir le temps de jeu en secondes
     * @param playTime Temps de jeu en secondes
     */
    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
    
    /**
     * Obtenir le nombre de variants capturés
     * @return Nombre de variants capturés
     */
    public int getCapturedVariants() {
        return capturedVariants;
    }
    
    /**
     * Définir le nombre de variants capturés
     * @param capturedVariants Nombre de variants capturés
     */
    public void setCapturedVariants(int capturedVariants) {
        this.capturedVariants = capturedVariants;
    }
    
    /**
     * Obtenir le checksum de la sauvegarde
     * @return Checksum de la sauvegarde
     */
    public String getChecksum() {
        return checksum;
    }
    
    /**
     * Définir le checksum de la sauvegarde
     * @param checksum Checksum de la sauvegarde
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    /**
     * Obtenir le chemin du fichier de sauvegarde
     * @return Chemin du fichier de sauvegarde
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Définir le chemin du fichier de sauvegarde
     * @param filePath Chemin du fichier de sauvegarde
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Convertir le temps de jeu en format lisible
     * @return Temps de jeu au format "HH:MM:SS"
     */
    public String getFormattedPlayTime() {
        int hours = playTime / 3600;
        int minutes = (playTime % 3600) / 60;
        int seconds = playTime % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    @Override
    public String toString() {
        return String.format("Slot %d: %s (Niveau %d) - %s - %s - %d variants - %s",
                slot, playerName, playerLevel, location, timestamp, capturedVariants, getFormattedPlayTime());
    }
}

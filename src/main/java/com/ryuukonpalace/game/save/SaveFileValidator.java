package com.ryuukonpalace.game.save;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Classe utilitaire pour valider et réparer les fichiers de sauvegarde.
 * Vérifie l'intégrité des fichiers et tente de les réparer si possible.
 */
public class SaveFileValidator {
    
    // Dossier de sauvegarde
    private static final String SAVE_DIRECTORY = "saves";
    
    // Extension des fichiers de sauvegarde
    private static final String SAVE_EXTENSION = ".json";
    
    // Extension des fichiers de sauvegarde de secours
    private static final String BACKUP_EXTENSION = ".bak";
    
    /**
     * Vérifier l'intégrité d'un fichier de sauvegarde
     * @param filePath Chemin du fichier de sauvegarde
     * @return true si le fichier est valide, false sinon
     */
    public static boolean validateSaveFile(String filePath) {
        try {
            // Vérifier si le fichier existe
            File saveFile = new File(filePath);
            if (!saveFile.exists() || !saveFile.isFile()) {
                System.err.println("Le fichier de sauvegarde n'existe pas: " + filePath);
                return false;
            }
            
            // Lire le fichier JSON
            JsonObject saveData = readSaveFile(filePath);
            if (saveData == null) {
                System.err.println("Erreur de syntaxe JSON dans le fichier de sauvegarde: " + filePath);
                return false;
            }
            
            // Vérifier les sections obligatoires
            if (!saveData.has("metadata") || !saveData.has("player") || !saveData.has("world")) {
                System.err.println("Structure de sauvegarde invalide: sections obligatoires manquantes");
                return false;
            }
            
            // Vérifier le checksum
            if (saveData.has("metadata") && saveData.getAsJsonObject("metadata").has("checksum")) {
                String storedChecksum = saveData.getAsJsonObject("metadata").get("checksum").getAsString();
                
                // Créer une copie des données sans le checksum pour calculer le nouveau checksum
                JsonObject saveDataCopy = saveData.deepCopy();
                saveDataCopy.getAsJsonObject("metadata").remove("checksum");
                
                String saveDataString = saveDataCopy.toString();
                Checksum crc32 = new CRC32();
                crc32.update(saveDataString.getBytes());
                String calculatedChecksum = Long.toHexString(crc32.getValue());
                
                if (!calculatedChecksum.equals(storedChecksum)) {
                    System.err.println("Erreur de checksum: la sauvegarde pourrait être corrompue");
                    return false;
                }
            } else {
                System.err.println("Checksum manquant dans les métadonnées");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la validation du fichier de sauvegarde: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tenter de réparer un fichier de sauvegarde corrompu
     * @param filePath Chemin du fichier de sauvegarde
     * @return true si la réparation a réussi, false sinon
     */
    public static boolean repairSaveFile(String filePath) {
        try {
            // Vérifier si le fichier existe
            File saveFile = new File(filePath);
            if (!saveFile.exists() || !saveFile.isFile()) {
                System.err.println("Le fichier de sauvegarde n'existe pas: " + filePath);
                return false;
            }
            
            // Créer une copie de sauvegarde
            String backupPath = filePath + BACKUP_EXTENSION;
            try {
                Files.copy(Paths.get(filePath), Paths.get(backupPath));
                System.out.println("Copie de sauvegarde créée: " + backupPath);
            } catch (IOException e) {
                System.err.println("Erreur lors de la création de la copie de sauvegarde: " + e.getMessage());
                return false;
            }
            
            // Essayer de lire le fichier JSON
            JsonObject saveData = readSaveFile(filePath);
            if (saveData == null) {
                // Essayer de restaurer depuis la sauvegarde de secours
                if (restoreFromBackup(filePath)) {
                    System.out.println("Sauvegarde restaurée depuis la copie de secours");
                    return true;
                }
                return false;
            }
            
            // Vérifier et réparer les sections obligatoires
            boolean needsRepair = false;
            
            // Vérifier les métadonnées
            if (!saveData.has("metadata")) {
                saveData.add("metadata", new JsonObject());
                needsRepair = true;
            }
            
            // Vérifier les données du joueur
            if (!saveData.has("player")) {
                saveData.add("player", new JsonObject());
                needsRepair = true;
            }
            
            // Vérifier les données du monde
            if (!saveData.has("world")) {
                saveData.add("world", new JsonObject());
                needsRepair = true;
            }
            
            // Si des réparations sont nécessaires, recalculer le checksum et sauvegarder
            if (needsRepair) {
                // Calculer le nouveau checksum
                String saveDataString = saveData.toString();
                Checksum crc32 = new CRC32();
                crc32.update(saveDataString.getBytes());
                String newChecksum = Long.toHexString(crc32.getValue());
                
                // Mettre à jour le checksum dans les métadonnées
                saveData.getAsJsonObject("metadata").addProperty("checksum", newChecksum);
                
                // Sauvegarder le fichier réparé
                try {
                    Files.write(Paths.get(filePath), saveData.toString().getBytes());
                    System.out.println("Fichier de sauvegarde réparé avec succès");
                    return true;
                } catch (IOException e) {
                    System.err.println("Erreur lors de l'écriture du fichier réparé: " + e.getMessage());
                    
                    // Restaurer depuis la sauvegarde de secours
                    if (restoreFromBackup(filePath)) {
                        System.out.println("Sauvegarde restaurée depuis la copie de secours après échec de réparation");
                    }
                    
                    return false;
                }
            }
            
            // Aucune réparation nécessaire
            System.out.println("Aucune réparation nécessaire pour le fichier de sauvegarde");
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la tentative de réparation: " + e.getMessage());
            
            // Restaurer depuis la sauvegarde de secours
            if (restoreFromBackup(filePath)) {
                System.out.println("Sauvegarde restaurée depuis la copie de secours après erreur");
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * Lire un fichier de sauvegarde JSON
     * @param filePath Chemin du fichier de sauvegarde
     * @return Objet JSON contenant les données de sauvegarde, ou null en cas d'erreur
     */
    private static JsonObject readSaveFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            System.err.println("Erreur de syntaxe JSON: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier de sauvegarde: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Restaurer un fichier de sauvegarde depuis sa copie de secours
     * @param filePath Chemin du fichier de sauvegarde
     * @return true si la restauration a réussi, false sinon
     */
    private static boolean restoreFromBackup(String filePath) {
        String backupPath = filePath + BACKUP_EXTENSION;
        Path backupFile = Paths.get(backupPath);
        
        // Vérifier si la copie de secours existe
        if (!Files.exists(backupFile)) {
            System.err.println("Aucune copie de secours trouvée: " + backupPath);
            return false;
        }
        
        try {
            // Restaurer depuis la copie de secours
            Files.copy(backupFile, Paths.get(filePath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Fichier restauré depuis la copie de secours");
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la restauration depuis la copie de secours: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Vérifier tous les fichiers de sauvegarde dans le dossier de sauvegarde
     * @return Nombre de fichiers valides
     */
    public static int validateAllSaveFiles() {
        File saveDir = new File(SAVE_DIRECTORY);
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            System.err.println("Le dossier de sauvegarde n'existe pas: " + SAVE_DIRECTORY);
            return 0;
        }
        
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(SAVE_EXTENSION));
        if (saveFiles == null || saveFiles.length == 0) {
            System.out.println("Aucun fichier de sauvegarde trouvé");
            return 0;
        }
        
        int validCount = 0;
        for (File saveFile : saveFiles) {
            if (validateSaveFile(saveFile.getPath())) {
                validCount++;
            } else {
                System.out.println("Fichier de sauvegarde invalide: " + saveFile.getName());
            }
        }
        
        System.out.println(validCount + " fichiers de sauvegarde valides sur " + saveFiles.length);
        return validCount;
    }
    
    /**
     * Créer une copie de secours d'un fichier de sauvegarde
     * @param filePath Chemin du fichier de sauvegarde
     * @return true si la copie a réussi, false sinon
     */
    public static boolean createBackup(String filePath) {
        try {
            // Vérifier si le fichier existe
            File saveFile = new File(filePath);
            if (!saveFile.exists() || !saveFile.isFile()) {
                System.err.println("Le fichier de sauvegarde n'existe pas: " + filePath);
                return false;
            }
            
            // Créer une copie de sauvegarde
            String backupPath = filePath + BACKUP_EXTENSION;
            Files.copy(Paths.get(filePath), Paths.get(backupPath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copie de sauvegarde créée: " + backupPath);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la création de la copie de sauvegarde: " + e.getMessage());
            return false;
        }
    }
}

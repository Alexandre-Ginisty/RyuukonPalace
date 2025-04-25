package com.ryuukonpalace.game.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Utilitaire pour charger et parser des fichiers JSON.
 */
public class JsonLoader {

    /**
     * Charge un fichier JSON et le parse en JSONObject.
     * 
     * @param filePath Chemin du fichier JSON (relatif au dossier resources)
     * @return JSONObject représentant le contenu du fichier
     * @throws IOException Si le fichier ne peut pas être lu
     */
    public static JSONObject loadJsonObject(String filePath) throws IOException {
        String jsonContent = loadJsonContent(filePath);
        return new JSONObject(jsonContent);
    }
    
    /**
     * Charge un fichier JSON et le parse en JSONArray.
     * 
     * @param filePath Chemin du fichier JSON (relatif au dossier resources)
     * @return JSONArray représentant le contenu du fichier
     * @throws IOException Si le fichier ne peut pas être lu
     */
    public static JSONArray loadJsonArray(String filePath) throws IOException {
        String jsonContent = loadJsonContent(filePath);
        return new JSONArray(jsonContent);
    }
    
    /**
     * Charge le contenu d'un fichier JSON en tant que chaîne de caractères.
     * 
     * @param filePath Chemin du fichier JSON (relatif au dossier resources)
     * @return Contenu du fichier en tant que chaîne de caractères
     * @throws IOException Si le fichier ne peut pas être lu
     */
    private static String loadJsonContent(String filePath) throws IOException {
        // Essayer de charger depuis les ressources (jar)
        try (InputStream inputStream = JsonLoader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            }
        }
        
        // Si le chargement depuis les ressources échoue, essayer de charger depuis le système de fichiers
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8);
        }
        
        // Si le chargement depuis le système de fichiers échoue, essayer avec le préfixe "src/main/resources/"
        path = Paths.get("src/main/resources/", filePath);
        if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8);
        }
        
        throw new IOException("Impossible de trouver le fichier JSON: " + filePath);
    }
    
    /**
     * Sauvegarde un JSONObject dans un fichier.
     * 
     * @param jsonObject JSONObject à sauvegarder
     * @param filePath Chemin du fichier de destination
     * @param prettyPrint Si true, le JSON sera formaté avec des indentations
     * @throws IOException Si le fichier ne peut pas être écrit
     */
    public static void saveJsonObject(JSONObject jsonObject, String filePath, boolean prettyPrint) throws IOException {
        String jsonContent = prettyPrint ? jsonObject.toString(4) : jsonObject.toString();
        Files.writeString(Paths.get(filePath), jsonContent, StandardCharsets.UTF_8);
    }
    
    /**
     * Sauvegarde un JSONArray dans un fichier.
     * 
     * @param jsonArray JSONArray à sauvegarder
     * @param filePath Chemin du fichier de destination
     * @param prettyPrint Si true, le JSON sera formaté avec des indentations
     * @throws IOException Si le fichier ne peut pas être écrit
     */
    public static void saveJsonArray(JSONArray jsonArray, String filePath, boolean prettyPrint) throws IOException {
        String jsonContent = prettyPrint ? jsonArray.toString(4) : jsonArray.toString();
        Files.writeString(Paths.get(filePath), jsonContent, StandardCharsets.UTF_8);
    }
    
    /**
     * Charge un fichier JSON et le parse en JSONObject.
     * Cette méthode gère les exceptions en interne et retourne null en cas d'erreur.
     * 
     * @param filePath Chemin du fichier JSON (relatif au dossier resources)
     * @return JSONObject représentant le contenu du fichier, ou null en cas d'erreur
     */
    public static JSONObject loadJsonFromFile(String filePath) {
        try {
            return loadJsonObject(filePath);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier JSON: " + filePath);
            e.printStackTrace();
            return null;
        }
    }
}

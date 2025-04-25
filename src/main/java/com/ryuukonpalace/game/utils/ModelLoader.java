package com.ryuukonpalace.game.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lwjgl.opengl.GL11;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire pour charger et gérer les modèles 3D (FBX) et leurs animations.
 */
public class ModelLoader {
    
    private static ModelLoader instance;
    
    // Chemin vers le fichier de configuration des animations
    private static final String CONFIG_PATH = "src/main/resources/data/animations_config.json";
    
    // Cache pour stocker les modèles chargés
    private Map<String, Model> modelCache;
    
    // Configuration des animations
    private JsonObject animConfig;
    
    /**
     * Constructeur privé (singleton)
     */
    private ModelLoader() {
        modelCache = new HashMap<>();
        loadAnimationConfig();
    }
    
    /**
     * Obtenir l'instance unique du ModelLoader
     * @return L'instance du ModelLoader
     */
    public static ModelLoader getInstance() {
        if (instance == null) {
            instance = new ModelLoader();
        }
        return instance;
    }
    
    /**
     * Charger la configuration des animations depuis le fichier JSON
     */
    private void loadAnimationConfig() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(CONFIG_PATH)));
            animConfig = JsonParser.parseString(content).getAsJsonObject();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la configuration des animations: " + e.getMessage());
            animConfig = new JsonObject();
        }
    }
    
    /**
     * Obtenir le chemin du modèle pour un personnage
     * @param type Type de personnage (characters, npcs, variants)
     * @param name Nom du personnage
     * @return Chemin du modèle
     */
    public String getModelPath(String type, String name) {
        if (animConfig.has(type) && animConfig.getAsJsonObject(type).has(name)) {
            JsonObject character = animConfig.getAsJsonObject(type).getAsJsonObject(name);
            if (character.has("model") && !character.get("model").getAsString().isEmpty()) {
                return "src/main/resources/images/" + type + "/" + name + "/" + character.get("model").getAsString();
            }
        }
        return null;
    }
    
    /**
     * Obtenir le chemin de l'animation pour un personnage
     * @param type Type de personnage (characters, npcs, variants)
     * @param name Nom du personnage
     * @param animation Nom de l'animation (idle, walk, attack, etc.)
     * @return Chemin de l'animation
     */
    public String getAnimationPath(String type, String name, String animation) {
        if (animConfig.has(type) && animConfig.getAsJsonObject(type).has(name)) {
            JsonObject character = animConfig.getAsJsonObject(type).getAsJsonObject(name);
            if (character.has("animations") && character.getAsJsonObject("animations").has(animation)) {
                String animFile = character.getAsJsonObject("animations").get(animation).getAsString();
                if (!animFile.isEmpty()) {
                    return "src/main/resources/images/" + type + "/" + name + "/animations/" + animFile;
                }
            }
        }
        
        // Si l'animation n'est pas trouvée, essayer d'utiliser l'animation par défaut
        if (animConfig.has("animation_sets") && animConfig.getAsJsonObject("animation_sets").has("default")) {
            JsonObject defaultAnims = animConfig.getAsJsonObject("animation_sets").getAsJsonObject("default");
            if (defaultAnims.has(animation) && !defaultAnims.get(animation).getAsString().isEmpty()) {
                return "src/main/resources/images/animations/" + defaultAnims.get(animation).getAsString();
            }
        }
        
        return null;
    }
    
    /**
     * Charger un modèle 3D
     * @param type Type de personnage (characters, npcs, variants)
     * @param name Nom du personnage
     * @return Le modèle chargé, ou null si le chargement a échoué
     */
    public Model loadModel(String type, String name) {
        String cacheKey = type + ":" + name;
        
        // Vérifier si le modèle est déjà en cache
        if (modelCache.containsKey(cacheKey)) {
            return modelCache.get(cacheKey);
        }
        
        // Obtenir le chemin du modèle
        String modelPath = getModelPath(type, name);
        if (modelPath == null) {
            System.err.println("Modèle non trouvé pour " + type + ":" + name);
            return null;
        }
        
        // Charger le modèle (à implémenter avec une bibliothèque de chargement de FBX)
        Model model = loadFbxModel(modelPath);
        
        // Mettre en cache
        if (model != null) {
            modelCache.put(cacheKey, model);
        }
        
        return model;
    }
    
    /**
     * Charger une animation pour un modèle
     * @param model Le modèle à animer
     * @param type Type de personnage (characters, npcs, variants)
     * @param name Nom du personnage
     * @param animation Nom de l'animation (idle, walk, attack, etc.)
     * @return true si l'animation a été chargée avec succès, false sinon
     */
    public boolean loadAnimation(Model model, String type, String name, String animation) {
        if (model == null) {
            return false;
        }
        
        // Obtenir le chemin de l'animation
        String animPath = getAnimationPath(type, name, animation);
        if (animPath == null) {
            System.err.println("Animation " + animation + " non trouvée pour " + type + ":" + name);
            return false;
        }
        
        // Charger l'animation (à implémenter avec une bibliothèque de chargement de FBX)
        return loadFbxAnimation(model, animPath, animation);
    }
    
    /**
     * Méthode à implémenter pour charger un modèle FBX
     * @param path Chemin du fichier FBX
     * @return Le modèle chargé, ou null si le chargement a échoué
     */
    private Model loadFbxModel(String path) {
        // TODO: Implémenter le chargement de modèle FBX
        // Cette implémentation dépendra de la bibliothèque utilisée (jME, LWJGL, etc.)
        System.out.println("Chargement du modèle: " + path);
        
        // Placeholder pour le moment
        return new Model(path);
    }
    
    /**
     * Méthode à implémenter pour charger une animation FBX
     * @param model Le modèle à animer
     * @param path Chemin du fichier FBX contenant l'animation
     * @param animationName Nom de l'animation
     * @return true si l'animation a été chargée avec succès, false sinon
     */
    private boolean loadFbxAnimation(Model model, String path, String animationName) {
        // TODO: Implémenter le chargement d'animation FBX
        // Cette implémentation dépendra de la bibliothèque utilisée (jME, LWJGL, etc.)
        System.out.println("Chargement de l'animation " + animationName + ": " + path);
        
        // Placeholder pour le moment
        model.addAnimation(animationName, path);
        return true;
    }
    
    /**
     * Classe représentant un modèle 3D avec ses animations
     */
    public class Model {
        private String path;
        private Map<String, String> animations;
        
        public Model(String path) {
            this.path = path;
            this.animations = new HashMap<>();
        }
        
        public void addAnimation(String name, String path) {
            animations.put(name, path);
        }
        
        public String getPath() {
            return path;
        }
        
        public Map<String, String> getAnimations() {
            return animations;
        }
    }
}

package com.ryuukonpalace.game.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Gestionnaire de ressources pour le jeu.
 * Permet de charger, stocker et accéder aux ressources comme les images, sons et polices.
 * Optimisé avec chargement asynchrone et mise en cache.
 */
public class ResourceManager {
    
    // Singleton instance
    private static ResourceManager instance;
    
    // Maps to store resources
    private final Map<String, Integer> textures;
    private final Map<String, Sound> sounds;
    private final Map<String, Font> fonts;
    
    // Cache pour les ressources
    private final Map<String, ByteBuffer> resourceCache;
    
    // Pool de threads pour le chargement asynchrone
    private final ExecutorService loadingThreadPool;
    
    // Map pour suivre les chargements en cours
    private final Map<String, CompletableFuture<Integer>> pendingTextures;
    private final Map<String, CompletableFuture<Sound>> pendingSounds;
    private final Map<String, CompletableFuture<Font>> pendingFonts;
    
    // Statistiques de performance
    private long totalLoadingTime = 0;
    private int resourcesLoaded = 0;
    private int cacheHits = 0;
    
    /**
     * Constructeur privé pour le singleton
     */
    private ResourceManager() {
        textures = new ConcurrentHashMap<>();
        sounds = new ConcurrentHashMap<>();
        fonts = new ConcurrentHashMap<>();
        resourceCache = new ConcurrentHashMap<>();
        
        pendingTextures = new ConcurrentHashMap<>();
        pendingSounds = new ConcurrentHashMap<>();
        pendingFonts = new ConcurrentHashMap<>();
        
        // Créer un pool de threads pour le chargement asynchrone
        loadingThreadPool = Executors.newFixedThreadPool(2);
        
        System.out.println("ResourceManager initialized with async loading capability");
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire de ressources
     * @return L'instance du ResourceManager
     */
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    /**
     * Initialiser le gestionnaire de ressources
     */
    public void init() {
        System.out.println("Starting resource initialization...");
        long startTime = System.currentTimeMillis();
        
        // Précharger les textures de base en parallèle
        CompletableFuture<?>[] futures = new CompletableFuture<?>[16];
        
        futures[0] = loadTextureAsync("assets/textures/ground.png", "ground", null);
        futures[1] = loadTextureAsync("assets/textures/tall_grass.png", "tall_grass", null);
        futures[2] = loadTextureAsync("assets/textures/player.png", "player", null);
        
        // Charger les textures pour le QTE
        futures[3] = loadTextureAsync("assets/textures/ui/qte_frame.png", "qte_frame", null);
        futures[4] = loadTextureAsync("assets/textures/ui/qte_time_bar.png", "qte_time_bar", null);
        futures[5] = loadTextureAsync("assets/textures/ui/key_up.png", "key_up", null);
        futures[6] = loadTextureAsync("assets/textures/ui/key_down.png", "key_down", null);
        futures[7] = loadTextureAsync("assets/textures/ui/key_left.png", "key_left", null);
        futures[8] = loadTextureAsync("assets/textures/ui/key_right.png", "key_right", null);
        futures[9] = loadTextureAsync("assets/textures/ui/key_w.png", "key_w", null);
        futures[10] = loadTextureAsync("assets/textures/ui/key_a.png", "key_a", null);
        futures[11] = loadTextureAsync("assets/textures/ui/key_s.png", "key_s", null);
        futures[12] = loadTextureAsync("assets/textures/ui/key_d.png", "key_d", null);
        
        // Charger les textures pour le combat
        futures[13] = loadTextureAsync("assets/textures/ui/combat_bg.png", "combat_bg", null);
        futures[14] = loadTextureAsync("assets/textures/ui/combat_frame.png", "combat_frame", null);
        
        // Attendre que toutes les ressources soient chargées
        CompletableFuture.allOf(futures).join();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Resource initialization completed in " + (endTime - startTime) + "ms");
        System.out.println("Loaded " + textures.size() + " textures, " + sounds.size() + " sounds, " + fonts.size() + " fonts");
    }
    
    /**
     * Charger une texture depuis un fichier
     * @param path Chemin du fichier image
     * @param name Nom de référence pour la texture
     * @return L'ID OpenGL de la texture
     */
    public int loadTexture(String path, String name) {
        // Check if texture is already loaded
        if (textures.containsKey(name)) {
            return textures.get(name);
        }
        
        // Check if texture is being loaded
        if (pendingTextures.containsKey(name)) {
            try {
                return pendingTextures.get(name).get();
            } catch (Exception e) {
                System.err.println("Error waiting for texture: " + name);
                e.printStackTrace();
                return 0;
            }
        }
        
        long startTime = System.currentTimeMillis();
        int textureId = 0;
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            
            // Load image
            ByteBuffer imageBuffer = loadResource(path);
            if (imageBuffer == null) {
                System.err.println("Failed to load texture: " + path);
                return 0;
            }
            
            // Decode the image
            ByteBuffer decodedImage = STBImage.stbi_load_from_memory(
                    imageBuffer, width, height, channels, 4);
            
            if (decodedImage == null) {
                System.err.println("Failed to decode texture: " + path);
                return 0;
            }
            
            // Create OpenGL texture
            textureId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
            
            // Set texture parameters
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            
            // Upload texture data
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(), height.get(), 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, decodedImage);
            
            // Free the decoded image
            STBImage.stbi_image_free(decodedImage);
            
            // Store the texture
            textures.put(name, textureId);
            
            long endTime = System.currentTimeMillis();
            totalLoadingTime += (endTime - startTime);
            resourcesLoaded++;
            
            if (resourcesLoaded % 10 == 0) {
                System.out.println("Average texture loading time: " + (totalLoadingTime / resourcesLoaded) + "ms");
            }
            
            return textureId;
        }
    }
    
    /**
     * Charger une texture de manière asynchrone
     * @param path Chemin du fichier image
     * @param name Nom de référence pour la texture
     * @param callback Callback à appeler une fois la texture chargée
     * @return CompletableFuture pour suivre le chargement
     */
    public CompletableFuture<Integer> loadTextureAsync(String path, String name, Consumer<Integer> callback) {
        // Check if texture is already loaded
        if (textures.containsKey(name)) {
            int textureId = textures.get(name);
            CompletableFuture<Integer> future = CompletableFuture.completedFuture(textureId);
            if (callback != null) {
                callback.accept(textureId);
            }
            return future;
        }
        
        // Check if texture is being loaded
        if (pendingTextures.containsKey(name)) {
            CompletableFuture<Integer> future = pendingTextures.get(name);
            if (callback != null) {
                future.thenAccept(callback);
            }
            return future;
        }
        
        // Create a new future for this texture
        CompletableFuture<Integer> future = new CompletableFuture<>();
        pendingTextures.put(name, future);
        
        // Load the texture asynchronously
        CompletableFuture.supplyAsync(() -> {
            try {
                // Load the resource data
                ByteBuffer imageBuffer = loadResource(path);
                if (imageBuffer == null) {
                    throw new RuntimeException("Failed to load texture: " + path);
                }
                return imageBuffer;
            } catch (Exception e) {
                throw new RuntimeException("Error loading texture data: " + path, e);
            }
        }, loadingThreadPool).thenAcceptAsync(imageBuffer -> {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
                
                // Decode the image
                ByteBuffer decodedImage = STBImage.stbi_load_from_memory(
                        imageBuffer, width, height, channels, 4);
                
                if (decodedImage == null) {
                    throw new RuntimeException("Failed to decode texture: " + path);
                }
                
                // Create OpenGL texture
                int textureId = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                
                // Set texture parameters
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                
                // Upload texture data
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(), height.get(), 0,
                        GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, decodedImage);
                
                // Free the decoded image
                STBImage.stbi_image_free(decodedImage);
                
                // Store the texture
                textures.put(name, textureId);
                
                // Complete the future
                future.complete(textureId);
                
                // Call the callback if provided
                if (callback != null) {
                    callback.accept(textureId);
                }
                
                // Remove from pending
                pendingTextures.remove(name);
            } catch (Exception e) {
                future.completeExceptionally(e);
                pendingTextures.remove(name);
                System.err.println("Error processing texture: " + path);
                e.printStackTrace();
            }
        });
        
        return future;
    }
    
    /**
     * Obtenir une texture déjà chargée
     * @param name Nom de la texture
     * @return L'ID OpenGL de la texture, ou 0 si non trouvée
     */
    public int getTexture(String name) {
        return textures.getOrDefault(name, 0);
    }
    
    /**
     * Vérifier si une texture est chargée
     * @param name Nom de la texture
     * @return true si la texture est chargée, false sinon
     */
    public boolean hasTexture(String name) {
        return textures.containsKey(name);
    }
    
    /**
     * Obtenir l'ID d'une texture par son nom
     * @param name Nom de la texture
     * @return ID de la texture, ou -1 si non trouvée
     */
    public int getTextureId(String name) {
        return textures.getOrDefault(name, -1);
    }
    
    /**
     * Charger un son depuis un fichier
     * @param path Chemin du fichier son
     * @param name Nom de référence pour le son
     * @return L'objet Sound créé
     */
    public Sound loadSound(String path, String name) {
        // Check if sound is already loaded
        if (sounds.containsKey(name)) {
            return sounds.get(name);
        }
        
        // Create a new sound
        Sound sound = new Sound(name);
        sounds.put(name, sound);
        
        return sound;
    }
    
    /**
     * Charger un son de manière asynchrone
     * @param path Chemin du fichier son
     * @param name Nom de référence pour le son
     * @param callback Callback à appeler une fois le son chargé
     * @return CompletableFuture pour suivre le chargement
     */
    public CompletableFuture<Sound> loadSoundAsync(String path, String name, Consumer<Sound> callback) {
        // Check if sound is already loaded
        if (sounds.containsKey(name)) {
            Sound sound = sounds.get(name);
            CompletableFuture<Sound> future = CompletableFuture.completedFuture(sound);
            if (callback != null) {
                callback.accept(sound);
            }
            return future;
        }
        
        // Check if sound is being loaded
        if (pendingSounds.containsKey(name)) {
            CompletableFuture<Sound> future = pendingSounds.get(name);
            if (callback != null) {
                future.thenAccept(callback);
            }
            return future;
        }
        
        // Create a new future for this sound
        CompletableFuture<Sound> future = new CompletableFuture<>();
        pendingSounds.put(name, future);
        
        // Load the sound asynchronously
        CompletableFuture.supplyAsync(() -> {
            try {
                // Create a new sound
                Sound sound = new Sound(name);
                sounds.put(name, sound);
                
                // Complete the future
                future.complete(sound);
                
                // Call the callback if provided
                if (callback != null) {
                    callback.accept(sound);
                }
                
                // Remove from pending
                pendingSounds.remove(name);
                
                return sound;
            } catch (Exception e) {
                future.completeExceptionally(e);
                pendingSounds.remove(name);
                System.err.println("Error loading sound: " + path);
                e.printStackTrace();
                return null;
            }
        }, loadingThreadPool);
        
        return future;
    }
    
    /**
     * Obtenir un son déjà chargé
     * @param name Nom du son
     * @return L'objet Sound, ou null si non trouvé
     */
    public Sound getSound(String name) {
        return sounds.get(name);
    }
    
    /**
     * Charger une police depuis un fichier
     * @param path Chemin du fichier de police
     * @param name Nom de référence pour la police
     * @param size Taille de la police
     * @return L'objet Font créé
     */
    public Font loadFont(String path, String name, int size) {
        // Check if font is already loaded
        String fontKey = name + "_" + size;
        if (fonts.containsKey(fontKey)) {
            return fonts.get(fontKey);
        }
        
        // Create a new font
        Font font = new Font(name, size);
        fonts.put(fontKey, font);
        
        return font;
    }
    
    /**
     * Charger une police de manière asynchrone
     * @param path Chemin du fichier de police
     * @param name Nom de référence pour la police
     * @param size Taille de la police
     * @param callback Callback à appeler une fois la police chargée
     * @return CompletableFuture pour suivre le chargement
     */
    public CompletableFuture<Font> loadFontAsync(String path, String name, int size, Consumer<Font> callback) {
        // Check if font is already loaded
        String fontKey = name + "_" + size;
        if (fonts.containsKey(fontKey)) {
            Font font = fonts.get(fontKey);
            CompletableFuture<Font> future = CompletableFuture.completedFuture(font);
            if (callback != null) {
                callback.accept(font);
            }
            return future;
        }
        
        // Check if font is being loaded
        if (pendingFonts.containsKey(fontKey)) {
            CompletableFuture<Font> future = pendingFonts.get(fontKey);
            if (callback != null) {
                future.thenAccept(callback);
            }
            return future;
        }
        
        // Create a new future for this font
        CompletableFuture<Font> future = new CompletableFuture<>();
        pendingFonts.put(fontKey, future);
        
        // Load the font asynchronously
        CompletableFuture.supplyAsync(() -> {
            try {
                // Create a new font
                Font font = new Font(name, size);
                fonts.put(fontKey, font);
                
                // Complete the future
                future.complete(font);
                
                // Call the callback if provided
                if (callback != null) {
                    callback.accept(font);
                }
                
                // Remove from pending
                pendingFonts.remove(fontKey);
                
                return font;
            } catch (Exception e) {
                future.completeExceptionally(e);
                pendingFonts.remove(fontKey);
                System.err.println("Error loading font: " + path);
                e.printStackTrace();
                return null;
            }
        }, loadingThreadPool);
        
        return future;
    }
    
    /**
     * Obtenir une police déjà chargée
     * @param name Nom de la police
     * @param size Taille de la police
     * @return L'objet Font, ou null si non trouvé
     */
    public Font getFont(String name, int size) {
        return fonts.get(name + "_" + size);
    }
    
    /**
     * Obtenir une police déjà chargée
     * @param name Nom de la police
     * @return L'objet Font, ou null si non trouvé
     */
    public Font getFont(String name) {
        // Rechercher une police avec ce nom (peu importe la taille)
        for (Map.Entry<String, Font> entry : fonts.entrySet()) {
            if (entry.getKey().startsWith(name + "_")) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    /**
     * Libérer toutes les ressources
     */
    public void dispose() {
        // Shutdown the loading thread pool
        loadingThreadPool.shutdown();
        
        // Dispose textures
        for (int textureId : textures.values()) {
            GL11.glDeleteTextures(textureId);
        }
        textures.clear();
        
        // Dispose sounds
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
        
        // Dispose fonts
        for (Font font : fonts.values()) {
            font.dispose();
        }
        fonts.clear();
        
        // Clear cache
        resourceCache.clear();
        
        System.out.println("ResourceManager disposed. Performance stats:");
        System.out.println("Total resources loaded: " + resourcesLoaded);
        System.out.println("Cache hits: " + cacheHits);
        System.out.println("Average loading time: " + (resourcesLoaded > 0 ? totalLoadingTime / resourcesLoaded : 0) + "ms");
    }
    
    /**
     * Précharger un ensemble de ressources en arrière-plan
     * @param paths Liste des chemins de ressources à précharger
     */
    public void preloadResources(String[] paths) {
        System.out.println("Preloading " + paths.length + " resources...");
        
        for (String path : paths) {
            CompletableFuture.runAsync(() -> {
                try {
                    ByteBuffer buffer = loadResource(path);
                    if (buffer != null) {
                        resourceCache.put(path, buffer);
                        System.out.println("Preloaded: " + path);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to preload: " + path);
                    e.printStackTrace();
                }
            }, loadingThreadPool);
        }
    }
    
    /**
     * Charger une ressource depuis le classpath
     * @param resourcePath Chemin de la ressource
     * @return ByteBuffer contenant les données de la ressource
     */
    private ByteBuffer loadResource(String resourcePath) {
        // Check cache first
        if (resourceCache.containsKey(resourcePath)) {
            cacheHits++;
            return resourceCache.get(resourcePath).duplicate();
        }
        
        try {
            Path path = Paths.get(resourcePath);
            ByteBuffer buffer;
            
            if (Files.exists(path)) {
                buffer = loadFromFile(path);
            } else {
                buffer = loadFromClasspath(resourcePath);
            }
            
            // Cache the resource
            if (buffer != null) {
                resourceCache.put(resourcePath, buffer.duplicate());
            }
            
            return buffer;
        } catch (IOException e) {
            System.err.println("Failed to load resource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Charger une ressource depuis un fichier
     * @param path Chemin du fichier
     * @return ByteBuffer contenant les données du fichier
     * @throws IOException En cas d'erreur de lecture
     */
    private ByteBuffer loadFromFile(Path path) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(path)) {
            ByteBuffer buffer = BufferUtils.createByteBuffer((int) channel.size() + 1);
            while (channel.read(buffer) != -1) {
                // Keep reading
            }
            buffer.flip();
            return buffer;
        }
    }
    
    /**
     * Charger une ressource depuis le classpath
     * @param resourcePath Chemin de la ressource dans le classpath
     * @return ByteBuffer contenant les données de la ressource
     * @throws IOException En cas d'erreur de lecture
     */
    private ByteBuffer loadFromClasspath(String resourcePath) throws IOException {
        try (InputStream source = ResourceManager.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (source == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            
            try (ReadableByteChannel channel = Channels.newChannel(source)) {
                ByteBuffer buffer = BufferUtils.createByteBuffer(8192);
                
                while (true) {
                    int bytes = channel.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        ByteBuffer newBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 2);
                        buffer.flip();
                        newBuffer.put(buffer);
                        buffer = newBuffer;
                    }
                }
                
                buffer.flip();
                return buffer;
            }
        }
    }
    
    /**
     * Classe interne pour représenter un son
     */
    public static class Sound {
        private final String name;
        
        public Sound(String name) {
            this.name = name;
        }
        
        public void play() {
            // Sera implémenté plus tard
            System.out.println("Playing sound: " + name);
        }
        
        public void dispose() {
            // Sera implémenté plus tard
        }
    }
    
    /**
     * Classe interne pour représenter une police
     */
    public static class Font {
        private final String name;
        private final int size;
        
        public Font(String name, int size) {
            this.name = name;
            this.size = size;
        }
        
        public String getName() {
            return name;
        }
        
        public int getSize() {
            return size;
        }
        
        public void dispose() {
            // Sera implémenté plus tard
        }
    }
}

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

/**
 * Gestionnaire de ressources pour le jeu.
 * Permet de charger, stocker et accéder aux ressources comme les images, sons et polices.
 */
public class ResourceManager {
    
    // Singleton instance
    private static ResourceManager instance;
    
    // Maps to store resources
    private final Map<String, Integer> textures;
    private final Map<String, Sound> sounds;
    private final Map<String, Font> fonts;
    
    /**
     * Constructeur privé pour le singleton
     */
    private ResourceManager() {
        textures = new HashMap<>();
        sounds = new HashMap<>();
        fonts = new HashMap<>();
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
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(), height.get(), 
                    0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, decodedImage);
            
            // Free the decoded image
            STBImage.stbi_image_free(decodedImage);
            
            // Store the texture ID
            textures.put(name, textureId);
            
            System.out.println("Loaded texture: " + name + " (" + width.get() + "x" + height.get() + ")");
        }
        
        return textureId;
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
     * Charger un son depuis un fichier
     * @param path Chemin du fichier son
     * @param name Nom de référence pour le son
     * @return L'objet Sound créé
     */
    public Sound loadSound(String path, String name) {
        // Cette méthode sera implémentée plus tard avec OpenAL
        Sound sound = new Sound(name);
        sounds.put(name, sound);
        return sound;
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
        // Cette méthode sera implémentée plus tard avec STB TrueType
        Font font = new Font(name, size);
        fonts.put(name, font);
        return font;
    }
    
    /**
     * Obtenir une police déjà chargée
     * @param name Nom de la police
     * @return L'objet Font, ou null si non trouvé
     */
    public Font getFont(String name) {
        return fonts.get(name);
    }
    
    /**
     * Libérer toutes les ressources
     */
    public void dispose() {
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
    }
    
    /**
     * Charger une ressource depuis le classpath
     * @param resourcePath Chemin de la ressource
     * @return ByteBuffer contenant les données de la ressource
     */
    private ByteBuffer loadResource(String resourcePath) {
        try {
            Path path = Paths.get(resourcePath);
            if (Files.exists(path)) {
                return loadFromFile(path);
            } else {
                return loadFromClasspath(resourcePath);
            }
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

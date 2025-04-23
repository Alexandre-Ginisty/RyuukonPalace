package com.ryuukonpalace.game.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Système de rendu 2D pour le jeu.
 * Gère le rendu des sprites, textes et formes primitives.
 */
public class Renderer {
    
    // Singleton instance
    private static Renderer instance;
    
    // Shader program
    private int shaderProgram;
    
    // Vertex Array Object and Vertex Buffer Object
    private int vao;
    private int vbo;
    private int ebo;
    
    // Default quad vertices
    private final float[] quadVertices = {
        // positions   // texture coords
        0.0f, 1.0f,    0.0f, 1.0f, // top left
        1.0f, 1.0f,    1.0f, 1.0f, // top right
        1.0f, 0.0f,    1.0f, 0.0f, // bottom right
        0.0f, 0.0f,    0.0f, 0.0f  // bottom left
    };
    
    // Default quad indices
    private final int[] quadIndices = {
        0, 1, 2, // first triangle
        2, 3, 0  // second triangle
    };
    
    // Vertex shader source
    private final String vertexShaderSource = 
            "#version 330 core\n" +
            "layout (location = 0) in vec2 aPos;\n" +
            "layout (location = 1) in vec2 aTexCoord;\n" +
            "out vec2 TexCoord;\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 projection;\n" +
            "uniform mat4 view;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = projection * view * model * vec4(aPos, 0.0, 1.0);\n" +
            "    TexCoord = aTexCoord;\n" +
            "}";
    
    // Fragment shader source
    private final String fragmentShaderSource = 
            "#version 330 core\n" +
            "in vec2 TexCoord;\n" +
            "out vec4 FragColor;\n" +
            "uniform sampler2D texture1;\n" +
            "uniform vec4 color;\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor = texture(texture1, TexCoord) * color;\n" +
            "}";
    
    // Caméra
    private Camera camera;
    
    /**
     * Constructeur privé pour le singleton
     */
    private Renderer() {
        init();
        camera = Camera.getInstance();
    }
    
    /**
     * Obtenir l'instance unique du renderer
     * @return L'instance du Renderer
     */
    public static Renderer getInstance() {
        if (instance == null) {
            instance = new Renderer();
        }
        return instance;
    }
    
    /**
     * Initialiser le renderer
     */
    private void init() {
        // Créer et compiler le vertex shader
        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);
        checkShaderCompilation(vertexShader, "vertex");
        
        // Créer et compiler le fragment shader
        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);
        checkShaderCompilation(fragmentShader, "fragment");
        
        // Créer le shader program
        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);
        checkProgramLinking(shaderProgram);
        
        // Supprimer les shaders car ils sont maintenant liés au program
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
        
        // Créer le VAO
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        
        // Créer le VBO
        vbo = GL20.glGenBuffers();
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, vbo);
        
        // Allouer la mémoire pour le VBO
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(quadVertices.length);
        vertexBuffer.put(quadVertices).flip();
        GL20.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);
        MemoryUtil.memFree(vertexBuffer);
        
        // Créer le EBO
        ebo = GL20.glGenBuffers();
        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, ebo);
        
        // Allouer la mémoire pour le EBO
        IntBuffer indexBuffer = MemoryUtil.memAllocInt(quadIndices.length);
        indexBuffer.put(quadIndices).flip();
        GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL20.GL_STATIC_DRAW);
        MemoryUtil.memFree(indexBuffer);
        
        // Configurer les vertex attributes
        // Position attribute
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
        
        // Texture coord attribute
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
        
        // Unbind
        GL30.glBindVertexArray(0);
        
        // Activer le blending pour la transparence
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        System.out.println("Renderer initialized");
    }
    
    /**
     * Vérifier la compilation d'un shader
     * @param shader ID du shader
     * @param type Type de shader (vertex ou fragment)
     */
    private void checkShaderCompilation(int shader, String type) {
        int success = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
        if (success == GL11.GL_FALSE) {
            String infoLog = GL20.glGetShaderInfoLog(shader);
            System.err.println("ERROR::SHADER::" + type.toUpperCase() + "::COMPILATION_FAILED\n" + infoLog);
        }
    }
    
    /**
     * Vérifier le linking d'un shader program
     * @param program ID du shader program
     */
    private void checkProgramLinking(int program) {
        int success = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
        if (success == GL11.GL_FALSE) {
            String infoLog = GL20.glGetProgramInfoLog(program);
            System.err.println("ERROR::PROGRAM::LINKING_FAILED\n" + infoLog);
        }
    }
    
    /**
     * Dessiner un sprite en tenant compte de la caméra
     * @param textureId ID de la texture
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     * @param rotation Rotation en degrés
     * @param r Composante rouge (0-1)
     * @param g Composante verte (0-1)
     * @param b Composante bleue (0-1)
     * @param a Composante alpha (0-1)
     */
    public void drawSprite(int textureId, float x, float y, float width, float height, float rotation, 
                          float r, float g, float b, float a) {
        // Vérifier si l'objet est visible à l'écran
        if (!isVisibleOnScreen(x, y, width, height)) {
            return; // Ne pas dessiner les objets hors écran
        }
        
        // Utiliser le shader program
        GL20.glUseProgram(shaderProgram);
        
        // Créer les matrices de transformation
        float[] model = createModelMatrix(x, y, width, height, rotation);
        float[] view = createViewMatrix();
        float[] projection = createOrthographicProjection(0, camera.getViewWidth(), 0, camera.getViewHeight());
        
        // Définir les uniforms
        int modelLoc = GL20.glGetUniformLocation(shaderProgram, "model");
        GL20.glUniformMatrix4fv(modelLoc, false, model);
        
        int viewLoc = GL20.glGetUniformLocation(shaderProgram, "view");
        GL20.glUniformMatrix4fv(viewLoc, false, view);
        
        int projLoc = GL20.glGetUniformLocation(shaderProgram, "projection");
        GL20.glUniformMatrix4fv(projLoc, false, projection);
        
        int colorLoc = GL20.glGetUniformLocation(shaderProgram, "color");
        GL20.glUniform4f(colorLoc, r, g, b, a);
        
        // Activer la texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        
        // Dessiner le quad
        GL30.glBindVertexArray(vao);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
        
        // Désactiver le shader program
        GL20.glUseProgram(0);
    }
    
    /**
     * Vérifier si un objet est visible à l'écran
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     * @return true si l'objet est visible, false sinon
     */
    private boolean isVisibleOnScreen(float x, float y, float width, float height) {
        float camX = camera.getX();
        float camY = camera.getY();
        float camRight = camX + (camera.getViewWidth() / camera.getZoom());
        float camBottom = camY + (camera.getViewHeight() / camera.getZoom());
        
        return x + width >= camX && x <= camRight && y + height >= camY && y <= camBottom;
    }
    
    /**
     * Dessiner un sprite avec des valeurs par défaut pour la couleur
     * @param textureId ID de la texture
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     */
    public void drawSprite(int textureId, float x, float y, float width, float height) {
        drawSprite(textureId, x, y, width, height, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Dessiner un rectangle coloré en tenant compte de la caméra
     * @param x Position X dans le monde
     * @param y Position Y dans le monde
     * @param width Largeur
     * @param height Hauteur
     * @param r Composante rouge (0-1)
     * @param g Composante verte (0-1)
     * @param b Composante bleue (0-1)
     * @param a Composante alpha (0-1)
     */
    public void drawRectangle(float x, float y, float width, float height, float r, float g, float b, float a) {
        // Utiliser un texture blanche de 1x1 pixel
        int whiteTexture = createWhiteTexture();
        drawSprite(whiteTexture, x, y, width, height, 0.0f, r, g, b, a);
        GL11.glDeleteTextures(whiteTexture);
    }
    
    /**
     * Dessiner un élément d'interface utilisateur (non affecté par la caméra)
     * @param textureId ID de la texture
     * @param x Position X sur l'écran
     * @param y Position Y sur l'écran
     * @param width Largeur
     * @param height Hauteur
     * @param r Composante rouge (0-1)
     * @param g Composante verte (0-1)
     * @param b Composante bleue (0-1)
     * @param a Composante alpha (0-1)
     */
    public void drawUI(int textureId, float x, float y, float width, float height, float r, float g, float b, float a) {
        // Utiliser le shader program
        GL20.glUseProgram(shaderProgram);
        
        // Créer les matrices de transformation
        float[] model = createModelMatrix(x, y, width, height, 0.0f);
        float[] view = createIdentityMatrix(); // Pas de transformation de vue pour l'UI
        float[] projection = createOrthographicProjection(0, camera.getViewWidth(), 0, camera.getViewHeight());
        
        // Définir les uniforms
        int modelLoc = GL20.glGetUniformLocation(shaderProgram, "model");
        GL20.glUniformMatrix4fv(modelLoc, false, model);
        
        int viewLoc = GL20.glGetUniformLocation(shaderProgram, "view");
        GL20.glUniformMatrix4fv(viewLoc, false, view);
        
        int projLoc = GL20.glGetUniformLocation(shaderProgram, "projection");
        GL20.glUniformMatrix4fv(projLoc, false, projection);
        
        int colorLoc = GL20.glGetUniformLocation(shaderProgram, "color");
        GL20.glUniform4f(colorLoc, r, g, b, a);
        
        // Activer la texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        
        // Dessiner le quad
        GL30.glBindVertexArray(vao);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
        
        // Désactiver le shader program
        GL20.glUseProgram(0);
    }
    
    /**
     * Créer une texture blanche de 1x1 pixel
     * @return ID de la texture
     */
    private int createWhiteTexture() {
        int textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        
        // Paramètres de la texture
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        
        // Données de la texture (1x1 pixel blanc)
        java.nio.ByteBuffer data = MemoryUtil.memAlloc(4);
        data.put((byte) 255).put((byte) 255).put((byte) 255).put((byte) 255).flip();
        
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 1, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
        MemoryUtil.memFree(data);
        
        return textureId;
    }
    
    /**
     * Créer une matrice de modèle pour la transformation
     * @param x Position X
     * @param y Position Y
     * @param width Largeur
     * @param height Hauteur
     * @param rotation Rotation en degrés
     * @return Matrice de modèle 4x4 (array de 16 floats)
     */
    private float[] createModelMatrix(float x, float y, float width, float height, float rotation) {
        // Cette implémentation est simplifiée et ne gère pas correctement les rotations
        // Une vraie implémentation utiliserait une bibliothèque mathématique comme JOML
        float[] model = new float[16];
        
        // Matrice identité
        model[0] = 1.0f; model[4] = 0.0f; model[8] = 0.0f; model[12] = x;
        model[1] = 0.0f; model[5] = 1.0f; model[9] = 0.0f; model[13] = y;
        model[2] = 0.0f; model[6] = 0.0f; model[10] = 1.0f; model[14] = 0.0f;
        model[3] = 0.0f; model[7] = 0.0f; model[11] = 0.0f; model[15] = 1.0f;
        
        // Scale
        model[0] *= width;
        model[5] *= height;
        
        // Rotation sera implémentée plus tard
        
        return model;
    }
    
    /**
     * Créer une matrice de vue pour la caméra
     * @return Matrice de vue 4x4 (array de 16 floats)
     */
    private float[] createViewMatrix() {
        float[] view = new float[16];
        
        // Matrice identité
        view[0] = 1.0f; view[4] = 0.0f; view[8] = 0.0f; view[12] = 0.0f;
        view[1] = 0.0f; view[5] = 1.0f; view[9] = 0.0f; view[13] = 0.0f;
        view[2] = 0.0f; view[6] = 0.0f; view[10] = 1.0f; view[14] = 0.0f;
        view[3] = 0.0f; view[7] = 0.0f; view[11] = 0.0f; view[15] = 1.0f;
        
        // Translation inverse de la caméra
        view[12] = -camera.getX() * camera.getZoom();
        view[13] = -camera.getY() * camera.getZoom();
        
        // Zoom
        view[0] *= camera.getZoom();
        view[5] *= camera.getZoom();
        
        return view;
    }
    
    /**
     * Créer une matrice identité
     * @return Matrice identité 4x4 (array de 16 floats)
     */
    private float[] createIdentityMatrix() {
        float[] identity = new float[16];
        
        identity[0] = 1.0f; identity[4] = 0.0f; identity[8] = 0.0f; identity[12] = 0.0f;
        identity[1] = 0.0f; identity[5] = 1.0f; identity[9] = 0.0f; identity[13] = 0.0f;
        identity[2] = 0.0f; identity[6] = 0.0f; identity[10] = 1.0f; identity[14] = 0.0f;
        identity[3] = 0.0f; identity[7] = 0.0f; identity[11] = 0.0f; identity[15] = 1.0f;
        
        return identity;
    }
    
    /**
     * Créer une matrice de projection orthographique
     * @param left Bord gauche
     * @param right Bord droit
     * @param bottom Bord inférieur
     * @param top Bord supérieur
     * @return Matrice de projection 4x4 (array de 16 floats)
     */
    private float[] createOrthographicProjection(float left, float right, float bottom, float top) {
        // Cette implémentation est simplifiée
        // Une vraie implémentation utiliserait une bibliothèque mathématique comme JOML
        float[] projection = new float[16];
        
        projection[0] = 2.0f / (right - left);
        projection[5] = 2.0f / (top - bottom);
        projection[10] = -1.0f;
        projection[12] = -(right + left) / (right - left);
        projection[13] = -(top + bottom) / (top - bottom);
        projection[15] = 1.0f;
        
        return projection;
    }
    
    /**
     * Libérer les ressources
     */
    public void dispose() {
        GL20.glDeleteProgram(shaderProgram);
        GL30.glDeleteVertexArrays(vao);
        GL20.glDeleteBuffers(vbo);
        GL20.glDeleteBuffers(ebo);
    }
}

package com.ryuukonpalace.game.core;

/**
 * Gère la caméra du jeu, qui suit le joueur et définit ce qui est visible à l'écran.
 * Permet de transformer les coordonnées du monde en coordonnées d'écran et vice versa.
 */
public class Camera {
    
    // Singleton instance
    private static Camera instance;
    
    // Position de la caméra (coin supérieur gauche de la vue)
    private float x;
    private float y;
    
    // Dimensions de la vue
    private float viewWidth;
    private float viewHeight;
    
    // Limites du monde
    private float worldWidth;
    private float worldHeight;
    
    // Objet suivi par la caméra (généralement le joueur)
    private GameObject target;
    
    // Facteur de zoom
    private float zoom = 1.0f;
    
    // Vitesse de suivi (1.0 = instantané, plus petit = plus lent)
    private float followSpeed = 0.1f;
    
    /**
     * Constructeur privé pour le singleton
     */
    private Camera() {
        this.x = 0;
        this.y = 0;
        this.viewWidth = 800;
        this.viewHeight = 600;
        this.worldWidth = 800;
        this.worldHeight = 600;
    }
    
    /**
     * Obtenir l'instance unique de la caméra
     * @return L'instance de la Camera
     */
    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }
    
    /**
     * Initialiser la caméra avec les dimensions de la vue et du monde
     * @param viewWidth Largeur de la vue (fenêtre)
     * @param viewHeight Hauteur de la vue (fenêtre)
     * @param worldWidth Largeur du monde
     * @param worldHeight Hauteur du monde
     */
    public void init(float viewWidth, float viewHeight, float worldWidth, float worldHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
    
    /**
     * Définir l'objet à suivre (généralement le joueur)
     * @param target L'objet à suivre
     */
    public void follow(GameObject target) {
        this.target = target;
    }
    
    /**
     * Arrêter de suivre l'objet
     */
    public void stopFollowing() {
        this.target = null;
    }
    
    /**
     * Mettre à jour la position de la caméra
     * Cette méthode doit être appelée à chaque frame
     */
    public void update() {
        if (target != null) {
            // Calculer la position cible de la caméra (centrée sur l'objet suivi)
            float targetX = target.getX() - (viewWidth / (2 * zoom)) + (target.getWidth() / 2);
            float targetY = target.getY() - (viewHeight / (2 * zoom)) + (target.getHeight() / 2);
            
            // Interpolation linéaire pour un mouvement fluide
            x += (targetX - x) * followSpeed;
            y += (targetY - y) * followSpeed;
            
            // Limiter la caméra aux bords du monde
            clampToWorld();
        }
    }
    
    /**
     * Limiter la position de la caméra aux bords du monde
     */
    private void clampToWorld() {
        float maxX = worldWidth - (viewWidth / zoom);
        float maxY = worldHeight - (viewHeight / zoom);
        
        if (maxX < 0) maxX = 0;
        if (maxY < 0) maxY = 0;
        
        x = Math.max(0, Math.min(x, maxX));
        y = Math.max(0, Math.min(y, maxY));
    }
    
    /**
     * Convertir une coordonnée X du monde en coordonnée X de l'écran
     * @param worldX Coordonnée X dans le monde
     * @return Coordonnée X sur l'écran
     */
    public float worldToScreenX(float worldX) {
        return (worldX - x) * zoom;
    }
    
    /**
     * Convertir une coordonnée Y du monde en coordonnée Y de l'écran
     * @param worldY Coordonnée Y dans le monde
     * @return Coordonnée Y sur l'écran
     */
    public float worldToScreenY(float worldY) {
        return (worldY - y) * zoom;
    }
    
    /**
     * Convertir une coordonnée X de l'écran en coordonnée X du monde
     * @param screenX Coordonnée X sur l'écran
     * @return Coordonnée X dans le monde
     */
    public float screenToWorldX(float screenX) {
        return (screenX / zoom) + x;
    }
    
    /**
     * Convertir une coordonnée Y de l'écran en coordonnée Y du monde
     * @param screenY Coordonnée Y sur l'écran
     * @return Coordonnée Y dans le monde
     */
    public float screenToWorldY(float screenY) {
        return (screenY / zoom) + y;
    }
    
    /**
     * Vérifier si un objet est visible à l'écran
     * @param obj L'objet à vérifier
     * @return true si l'objet est visible, false sinon
     */
    public boolean isVisible(GameObject obj) {
        float objRight = obj.getX() + obj.getWidth();
        float objBottom = obj.getY() + obj.getHeight();
        
        float camRight = x + (viewWidth / zoom);
        float camBottom = y + (viewHeight / zoom);
        
        return objRight >= x && obj.getX() <= camRight &&
               objBottom >= y && obj.getY() <= camBottom;
    }
    
    /**
     * Définir la position de la caméra
     * @param x Position X
     * @param y Position Y
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        clampToWorld();
    }
    
    /**
     * Définir le zoom de la caméra
     * @param zoom Facteur de zoom (1.0 = normal, > 1.0 = zoom avant, < 1.0 = zoom arrière)
     */
    public void setZoom(float zoom) {
        if (zoom > 0) {
            this.zoom = zoom;
            clampToWorld();
        }
    }
    
    /**
     * Définir la vitesse de suivi
     * @param speed Vitesse de suivi (1.0 = instantané, plus petit = plus lent)
     */
    public void setFollowSpeed(float speed) {
        if (speed > 0 && speed <= 1.0f) {
            this.followSpeed = speed;
        }
    }
    
    /**
     * Obtenir la position X de la caméra
     * @return Position X
     */
    public float getX() {
        return x;
    }
    
    /**
     * Obtenir la position Y de la caméra
     * @return Position Y
     */
    public float getY() {
        return y;
    }
    
    /**
     * Obtenir la largeur de la vue
     * @return Largeur de la vue
     */
    public float getViewWidth() {
        return viewWidth;
    }
    
    /**
     * Obtenir la hauteur de la vue
     * @return Hauteur de la vue
     */
    public float getViewHeight() {
        return viewHeight;
    }
    
    /**
     * Obtenir le facteur de zoom
     * @return Facteur de zoom
     */
    public float getZoom() {
        return zoom;
    }
}

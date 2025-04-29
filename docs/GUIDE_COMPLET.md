# 🎮 Guide Complet - Ryuukon Palace

Ce document regroupe toutes les informations essentielles pour le développement de Ryuukon Palace, avec un focus particulier sur le style graphique 2.5D et les animations.

## 📋 Table des matières

1. [Style graphique 2.5D](#style-graphique-25d)
2. [Système d'animation](#système-danimation)
3. [Création d'assets](#création-dassets)
4. [Intégration dans le moteur](#intégration-dans-le-moteur)
5. [Optimisations](#optimisations)
6. [Lancement du démonstrateur](#lancement-du-démonstrateur)

## 🖼️ Style graphique 2.5D

### Concept général

Le style 2.5D de Ryuukon Palace s'inspire directement de Pokémon Noir/Blanc, caractérisé par :

- Des sprites 2D classiques
- Un effet de perspective isométrique
- Des bâtiments avec plusieurs niveaux de hauteur
- Un système de couches pour créer la profondeur

![Exemple de style 2.5D](https://i.imgur.com/example.png)

### Éléments clés du style 2.5D

1. **Décalage vertical** : Les objets sont décalés vers le haut en fonction de leur position Y
   ```java
   float drawY = y - (tileY * isoOffsetY);
   ```

2. **Système de couches** : Les éléments sont rendus dans l'ordre suivant :
   - Couche 0 : Terrain (herbe, chemin, eau)
   - Couche 1 : Détails (fleurs, petites pierres)
   - Couche 2 : Objets arrière (bases des bâtiments)
   - Couche 3 : Objets avant (parties supérieures des bâtiments)
   - Couche 4 : Personnages et créatures

3. **Bâtiments en perspective** : Les bâtiments sont divisés en deux parties :
   - Base (toujours visible)
   - Partie supérieure (peut être cachée si le joueur passe derrière)

4. **Ombres** : Des ombres simples sont ajoutées sous les objets pour renforcer l'effet 3D

### Dimensions recommandées

| Élément | Taille (pixels) | Notes |
|---------|----------------|-------|
| Tuiles de base | 32×32 | Terrain, chemin, eau |
| Personnages | 32×48 | 8 frames (4 directions × 2 animations) |
| Arbres | 32×64 | Base + feuillage |
| Bâtiments | Variable | Divisés en sections |
| Variants (créatures) | 48×48 | Taille de base, peut varier |

## 🎭 Système d'animation

### Types d'animations

1. **Animations de déplacement** :
   - 4 directions (haut, bas, gauche, droite)
   - 2 frames par direction minimum
   - Cycle d'animation : 10-12 frames par seconde

2. **Animations d'attaque** :
   - 1-3 frames par attaque
   - Effets visuels supplémentaires

3. **Animations d'état** :
   - Idle (immobile)
   - Victoire
   - Défaite/blessé

### Structure des spritesheets

Les spritesheets sont organisées de la manière suivante :

```
Character_Name.png
├── Down_Idle (0,0)
├── Down_Walk1 (32,0)
├── Down_Walk2 (64,0)
├── Left_Idle (0,48)
├── Left_Walk1 (32,48)
├── Left_Walk2 (64,48)
├── Right_Idle (0,96)
├── Right_Walk1 (32,96)
├── Right_Walk2 (64,96)
├── Up_Idle (0,144)
├── Up_Walk1 (32,144)
└── Up_Walk2 (64,144)
```

### Implémentation de l'animation

```java
// Exemple de code pour l'animation de personnage
private void updateAnimation() {
    if (isMoving) {
        animCounter++;
        if (animCounter >= 10) { // Vitesse d'animation
            animCounter = 0;
            animFrame = (animFrame + 1) % 2; // Alterne entre 0 et 1
        }
    } else {
        animFrame = 0; // Frame statique quand immobile
    }
    
    // Calculer l'index du sprite
    int spriteIndex = direction * 2 + animFrame;
    currentSprite = characterSprites[spriteIndex];
}
```

## 🎨 Création d'assets

### Outils recommandés

- **Aseprite** : Création de sprites et animations pixel art
- **PyxelEdit** : Création de tilesets
- **Tiled** : Éditeur de cartes
- **GIMP/Photoshop** : Édition d'images avancée

### Workflow de création

1. **Tuiles de base** :
   - Créer les tuiles de terrain (herbe, chemin, eau)
   - Ajouter des variations pour plus de diversité
   - Créer des transitions entre différents types de terrain

2. **Objets et bâtiments** :
   - Dessiner la base (partie inférieure)
   - Dessiner la partie supérieure
   - Créer des ombres
   - Exporter en sections distinctes

3. **Personnages et variants** :
   - Dessiner la pose de base (face)
   - Créer les autres directions
   - Ajouter les frames d'animation
   - Organiser en spritesheet

### Palette de couleurs

Pour maintenir une cohérence visuelle, utilisez une palette limitée :

- **Palette principale** : 32-64 couleurs maximum
- **Tons** : Légèrement désaturés pour un style plus mature
- **Contraste** : Moyen à élevé pour une bonne lisibilité
- **Ombres** : Teintes violettes/bleues pour les ombres (style Pokémon N/B)

## 🔧 Intégration dans le moteur

### Chargement des sprites

```java
// Exemple de chargement de spritesheet
public void loadCharacterSprites(String characterName) {
    String basePath = "assets/characters/" + characterName + "/";
    
    // Charger les 8 sprites (4 directions × 2 frames)
    characterSprites[0] = loadSprite(basePath + "down_idle.png");
    characterSprites[1] = loadSprite(basePath + "down_walk.png");
    characterSprites[2] = loadSprite(basePath + "left_idle.png");
    characterSprites[3] = loadSprite(basePath + "left_walk.png");
    characterSprites[4] = loadSprite(basePath + "right_idle.png");
    characterSprites[5] = loadSprite(basePath + "right_walk.png");
    characterSprites[6] = loadSprite(basePath + "up_idle.png");
    characterSprites[7] = loadSprite(basePath + "up_walk.png");
}
```

### Rendu des bâtiments

```java
// Exemple de rendu d'un bâtiment en 2.5D
public void drawBuilding(int textureBaseId, int textureTopId, float x, float y, float width, float baseHeight, float topHeight) {
    // Calculer la profondeur basée sur la position Y
    float depth = y;
    
    // Dessiner la base du bâtiment (toujours visible)
    renderer.addToLayer(
        Layer.OBJECT_BACK,
        textureBaseId,
        x,
        y,
        width,
        baseHeight,
        depth
    );
    
    // Dessiner le reste du bâtiment (peut être caché par le joueur)
    renderer.addToLayer(
        Layer.OBJECT_FRONT,
        textureTopId,
        x,
        y - topHeight, // Décalage vers le haut
        width,
        topHeight,
        depth
    );
}
```

### Système de couches

Le système de rendu utilise plusieurs couches pour créer l'effet de profondeur :

```java
public enum Layer {
    TERRAIN(0),      // Couche de base (herbe, eau, etc.)
    DETAIL(1),       // Détails de terrain (fleurs, petites pierres)
    OBJECT_BACK(2),  // Objets arrière (bases des bâtiments)
    PLAYER(3),       // Joueur et PNJ
    OBJECT_FRONT(4); // Objets avant (parties supérieures des bâtiments)
    
    private final int value;
    
    Layer(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
```

## ⚡ Optimisations

### Système de cache

```java
// Exemple de système de cache LRU pour les tuiles
private LinkedHashMap<String, Integer> tileCache = new LinkedHashMap<String, Integer>(1000, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
        return size() > 1000; // Limite de taille du cache
    }
};

// Utilisation du cache
public int getTileTextureWithCache(int tileId, int x, int y) {
    String key = tileId + ":" + x + ":" + y;
    
    if (tileCache.containsKey(key)) {
        return tileCache.get(key);
    }
    
    int textureId = generateTileTexture(tileId, x, y);
    tileCache.put(key, textureId);
    return textureId;
}
```

### Chargement asynchrone

```java
// Exemple de chargement asynchrone
private ExecutorService threadPool = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()
);

public Future<Integer> loadSpriteAsync(final String path) {
    return threadPool.submit(() -> {
        // Charger le sprite
        return loadSprite(path);
    });
}
```

### Rendu sélectif

```java
// Exemple de rendu sélectif (ne dessiner que ce qui est visible)
private void renderVisibleTiles(float cameraX, float cameraY, int screenWidth, int screenHeight) {
    // Calculer les indices de tuiles visibles
    int startX = Math.max(0, (int)(cameraX / tileSize));
    int startY = Math.max(0, (int)(cameraY / tileSize));
    int endX = Math.min(mapWidth, startX + (screenWidth / tileSize) + 1);
    int endY = Math.min(mapHeight, startY + (screenHeight / tileSize) + 1);
    
    // Ne dessiner que les tuiles visibles
    for (int y = startY; y < endY; y++) {
        for (int x = startX; x < endX; x++) {
            drawTile(x, y);
        }
    }
}
```

## 🚀 Lancement du démonstrateur

### Version simplifiée (recommandée)

La version simplifiée utilise Swing et ne nécessite aucune dépendance externe :

```
run_simple_demo.bat
```

### Version complète (avec LWJGL)

La version complète offre de meilleures performances mais nécessite LWJGL :

1. Télécharger les dépendances :
   ```
   download_dependencies.bat
   ```

2. Lancer le démonstrateur :
   ```
   run_demo.bat
   ```

### Commandes

- **Flèches directionnelles** ou **ZQSD** : Déplacer le personnage
- **ESC** : Quitter le démonstrateur

---

## 📚 Ressources supplémentaires

- [Documentation LWJGL](https://www.lwjgl.org/guide)
- [Tutoriels de pixel art](https://lospec.com/pixel-art-tutorials)
- [Principes d'animation](https://www.doublefine.com/news/comments/principles_of_animation_for_pixel_artists)
- [Exemples de style 2.5D](https://www.spriters-resource.com/ds_dsi/pokemonblackwhite/)

---

Pour toute question ou suggestion, n'hésitez pas à contacter l'équipe de développement.

# Tutoriel Aseprite pour la création de sprites et animations 2.5D

Ce guide détaillé vous explique comment utiliser Aseprite pour créer des sprites et animations dans le style 2.5D de Pokémon Noir/Blanc pour votre jeu Ryuukon Palace.

## Table des matières

1. [Installation d'Aseprite](#1-installation-daseprite)
2. [Interface et outils de base](#2-interface-et-outils-de-base)
3. [Création de sprites de personnages](#3-création-de-sprites-de-personnages)
4. [Création de sprites de variants (créatures)](#4-création-de-sprites-de-variants-créatures)
5. [Animation des sprites](#5-animation-des-sprites)
6. [Exportation pour le jeu](#6-exportation-pour-le-jeu)
7. [Intégration avec le système SpriteSheet](#7-intégration-avec-le-système-spritesheet)
8. [Astuces pour le style 2.5D](#8-astuces-pour-le-style-25d)

## 1. Installation d'Aseprite

### Achat et téléchargement
1. Rendez-vous sur le site officiel: https://www.aseprite.org/
2. Cliquez sur "Buy Aseprite" (environ 20€)
3. Après l'achat, téléchargez la version Windows

### Alternative gratuite
Si vous préférez une option gratuite:
- **Piskel**: https://www.piskelapp.com/ (fonctionne dans le navigateur)
- **LibreSprite**: https://libresprite.github.io/ (fork gratuit d'Aseprite)

### Installation
1. Exécutez le fichier d'installation téléchargé
2. Suivez les instructions à l'écran
3. Lancez Aseprite après l'installation

## 2. Interface et outils de base

### Familiarisation avec l'interface
![Interface Aseprite](https://www.aseprite.org/assets/images/screenshot.png)

1. **Barre d'outils** (gauche)
   - Outil de sélection (M)
   - Crayon (B)
   - Ligne (L)
   - Rectangle (U)
   - Remplissage (G)
   - Pipette (I)

2. **Palette de couleurs** (droite)
   - Couleurs prédéfinies
   - Création de palettes personnalisées

3. **Timeline** (bas)
   - Gestion des frames d'animation
   - Gestion des calques

### Configuration des préférences
1. Allez dans Edit > Preferences
2. Configurez la grille (View > Grid > Grid Settings):
   - Width: 8 pixels
   - Height: 8 pixels
3. Activez les règles (View > Show Rulers)

## 3. Création de sprites de personnages

### Configuration du canevas
1. Créez un nouveau fichier (File > New)
   - Width: 32 pixels
   - Height: 48 pixels
   - Mode: RGB Color
   - Background: Transparent

### Création d'un personnage de base (Tacticien)
1. **Esquisse**
   - Créez un nouveau calque nommé "Esquisse"
   - Dessinez une silhouette simple avec des proportions chibi (tête grande, corps petit)
   - Utilisez une couleur claire comme guide

2. **Ligne de contour**
   - Créez un nouveau calque nommé "Contour"
   - Tracez les contours définitifs avec l'outil Crayon (B) en noir
   - Utilisez une épaisseur de 1px pour un style pixel art

3. **Couleurs de base**
   - Créez un nouveau calque nommé "Couleurs"
   - Remplissez les zones avec les couleurs de base:
     - Peau: #F8D8B0
     - Cheveux: #A05010 (brun) ou #F8E850 (blond)
     - Vêtements: #2878C8 (bleu) ou #D82800 (rouge)

4. **Détails et ombres**
   - Créez un nouveau calque nommé "Détails"
   - Ajoutez des détails comme les yeux, la bouche, les plis des vêtements
   - Ajoutez des ombres légères pour donner du volume

### Création des différentes orientations
1. **Vue de face**
   - Commencez par la vue de face (déjà créée)

2. **Vue de dos**
   - Dupliquez les calques (clic droit > Duplicate)
   - Modifiez la forme des cheveux et des vêtements
   - Simplifiez le visage (pas visible)

3. **Vue de côté**
   - Créez une nouvelle vue de profil
   - Assurez-vous que la silhouette est reconnaissable
   - Dupliquez et inversez horizontalement (Edit > Flip Horizontal) pour l'autre côté

### Exemple de personnage Tacticien
```
Vue de face:
    o
   /|\
   / \

Vue de dos:
    o
   /|\
   / \

Vue de côté:
  o
 /|
 / \
```

## 4. Création de sprites de variants (créatures)

### Configuration du canevas
1. Créez un nouveau fichier
   - Width: 64 pixels (plus grand pour les créatures)
   - Height: 64 pixels
   - Mode: RGB Color
   - Background: Transparent

### Création d'un variant de base
1. **Référence au lore**
   - Consultez le fichier `creatures_dictionary.json` pour les descriptions
   - Identifiez le type (Feu, Eau, Terre, Air, etc.)
   - Notez les caractéristiques spéciales

2. **Esquisse**
   - Dessinez une silhouette basée sur la description
   - Respectez le style chibi de Pokémon

3. **Ligne de contour**
   - Tracez les contours définitifs
   - Utilisez des lignes plus épaisses pour les contours extérieurs (2px)
   - Utilisez des lignes plus fines pour les détails intérieurs (1px)

4. **Couleurs et textures**
   - Appliquez les couleurs selon le type:
     - Feu: Rouges, oranges, jaunes
     - Eau: Bleus, cyans
     - Terre: Marrons, verts
     - Air: Blancs, bleus clairs
   - Ajoutez des textures caractéristiques (écailles, fourrure, plumes)

5. **Effets spéciaux**
   - Ajoutez des effets selon le type (flammes, gouttes d'eau, feuilles)
   - Utilisez des couleurs vives pour les éléments magiques

### Exemple de variant de type Feu
```
      /\
     /  \
    |    |
    |    |
     \__/
    /    \
   /      \
  /        \
```

## 5. Animation des sprites

### Configuration de la timeline
1. Ouvrez la timeline (Window > Timeline)
2. Cliquez sur le bouton "New Frame" pour ajouter des frames

### Animation de marche
1. **Personnage - 4 frames**
   - Frame 1: Position neutre
   - Frame 2: Jambe gauche en avant, bras droit en avant
   - Frame 3: Position neutre (ou légèrement différente)
   - Frame 4: Jambe droite en avant, bras gauche en avant

2. **Timing**
   - Cliquez sur chaque frame dans la timeline
   - Réglez la durée (Duration) à 100-150ms pour une marche normale
   - Pour une course, réduisez à 80-100ms

### Animation d'attaque
1. **Préparation**
   - Frame 1: Position de recul (2-3 frames)
   - Frame 2: Position d'élan

2. **Attaque**
   - Frame 3: Mouvement d'attaque rapide
   - Frame 4: Extension complète

3. **Récupération**
   - Frame 5: Retour à la position normale

### Animation de capture (QTE)
1. **Séquence de lancer**
   - Frame 1: Position initiale
   - Frame 2: Recul du bras
   - Frame 3: Lancer
   - Frame 4: Suivi du mouvement

2. **Animation de la pierre de capture**
   - Créez un nouveau fichier pour la pierre
   - Animez la rotation et les effets lumineux
   - 8-10 frames pour une animation fluide

### Prévisualisation
1. Appuyez sur Entrée pour lancer la prévisualisation
2. Ajustez le timing si nécessaire
3. Utilisez Ctrl+Entrée pour une prévisualisation en boucle

## 6. Exportation pour le jeu

### Exportation de spritesheet
1. Allez dans File > Export Sprite Sheet
2. Configurez les options:
   - Layout: Horizontal Strip (toutes les frames côte à côte)
   - Borders: 0px
   - Trim: Désactivé
   - Sheet Type: Packed
   - Cochez "JSON Data" pour exporter les métadonnées

3. Choisissez le dossier de destination:
   - `c:\Users\Test IA\RyuukonPalace\src\main\resources\assets\characters\` pour les personnages
   - `c:\Users\Test IA\RyuukonPalace\src\main\resources\assets\creatures\` pour les variants

### Format du fichier JSON
Le fichier JSON exporté ressemblera à ceci:
```json
{
  "frames": [
    {
      "filename": "player_walk_0",
      "frame": { "x": 0, "y": 0, "w": 32, "h": 48 },
      "rotated": false,
      "trimmed": false,
      "spriteSourceSize": { "x": 0, "y": 0, "w": 32, "h": 48 },
      "sourceSize": { "w": 32, "h": 48 }
    },
    {
      "filename": "player_walk_1",
      "frame": { "x": 32, "y": 0, "w": 32, "h": 48 },
      "rotated": false,
      "trimmed": false,
      "spriteSourceSize": { "x": 0, "y": 0, "w": 32, "h": 48 },
      "sourceSize": { "w": 32, "h": 48 }
    }
  ],
  "meta": {
    "app": "https://www.aseprite.org/",
    "version": "1.3",
    "image": "player_walk.png",
    "format": "RGBA8888",
    "size": { "w": 128, "h": 48 },
    "scale": "1"
  }
}
```

## 7. Intégration avec le système SpriteSheet

### Création d'un chargeur de spritesheet Aseprite
Ajoutez cette méthode à votre classe `SpriteLoader`:

```java
/**
 * Charger une spritesheet exportée depuis Aseprite
 * @param jsonPath Chemin du fichier JSON exporté
 * @return SpriteSheet chargée
 */
public SpriteSheet loadAsepriteSheet(String jsonPath) {
    try {
        // Charger le fichier JSON
        JsonObject sheetJson = JsonParser.parseReader(new FileReader(new File(jsonPath))).getAsJsonObject();
        
        // Obtenir le chemin de l'image
        String imagePath = sheetJson.getAsJsonObject("meta").get("image").getAsString();
        String imageFullPath = new File(jsonPath).getParent() + File.separator + imagePath;
        
        // Obtenir les dimensions d'un sprite
        JsonObject firstFrame = sheetJson.getAsJsonArray("frames").get(0).getAsJsonObject();
        int spriteWidth = firstFrame.getAsJsonObject("sourceSize").get("w").getAsInt();
        int spriteHeight = firstFrame.getAsJsonObject("sourceSize").get("h").getAsInt();
        
        // Charger l'image
        BufferedImage image = ImageIO.read(new File(imageFullPath));
        
        // Créer la spritesheet
        SpriteSheet spriteSheet = new SpriteSheet(image, spriteWidth, spriteHeight);
        
        // Ajouter les animations
        JsonArray frames = sheetJson.getAsJsonArray("frames");
        Map<String, List<Integer>> animations = new HashMap<>();
        
        // Regrouper les frames par animation (format: name_X)
        for (int i = 0; i < frames.size(); i++) {
            JsonObject frame = frames.get(i).getAsJsonObject();
            String filename = frame.get("filename").getAsString();
            
            // Extraire le nom de l'animation et l'index
            String[] parts = filename.split("_");
            if (parts.length >= 2) {
                String animName = parts[0];
                for (int j = 1; j < parts.length - 1; j++) {
                    animName += "_" + parts[j];
                }
                
                // Calculer la position dans la spritesheet
                int frameX = frame.getAsJsonObject("frame").get("x").getAsInt() / spriteWidth;
                int frameY = frame.getAsJsonObject("frame").get("y").getAsInt() / spriteHeight;
                
                // Ajouter à la liste des frames pour cette animation
                if (!animations.containsKey(animName)) {
                    animations.put(animName, new ArrayList<>());
                }
                animations.get(animName).add(frameX);
                animations.get(animName).add(frameY);
            }
        }
        
        // Définir les animations dans la spritesheet
        for (Map.Entry<String, List<Integer>> entry : animations.entrySet()) {
            int[] frameArray = new int[entry.getValue().size()];
            for (int i = 0; i < entry.getValue().size(); i++) {
                frameArray[i] = entry.getValue().get(i);
            }
            spriteSheet.defineAnimation(entry.getKey(), frameArray);
        }
        
        return spriteSheet;
    } catch (Exception e) {
        System.err.println("Erreur lors du chargement de la spritesheet Aseprite: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
```

### Utilisation dans le code du jeu
```java
// Dans votre classe de jeu
SpriteLoader loader = SpriteLoader.getInstance();
SpriteSheet playerSheet = loader.loadAsepriteSheet("src/main/resources/assets/characters/player.json");

// Obtenir les frames d'animation
int[] walkFrames = playerSheet.getAnimationFrames("player_walk");

// Utiliser les frames dans le rendu
// ...
```

## 8. Astuces pour le style 2.5D

### Effet de perspective
1. **Décalage vertical**
   - Dessinez les objets plus éloignés plus haut sur l'écran
   - Utilisez un décalage vertical proportionnel à la distance

2. **Taille relative**
   - Les objets plus éloignés sont légèrement plus petits
   - Les objets plus proches sont légèrement plus grands

### Ombres pour la profondeur
1. **Ombre portée**
   - Ajoutez une ombre sous chaque personnage/objet
   - Utilisez une forme simple et semi-transparente

2. **Ombrage directionnel**
   - Appliquez des ombres cohérentes (lumière venant du haut-gauche)
   - Utilisez 1-2 teintes plus foncées pour les ombres

### Bâtiments avec perspective
1. **Structure en deux parties**
   - Base: Partie inférieure du bâtiment (visible même quand le joueur est devant)
   - Façade: Partie supérieure (peut être cachée quand le joueur passe devant)

2. **Façades pré-rendues**
   - Dessinez les bâtiments avec une perspective cohérente
   - Utilisez des lignes de fuite convergeant vers un point de fuite central

### Exemple de bâtiment 2.5D
```
    /\
   /  \
  /____\  <- Façade (objectFrontLayer)
  |    |
  |    |  <- Base (objectBackLayer)
  |____|
```

## Conclusion

En suivant ce tutoriel, vous avez appris à:
1. Installer et configurer Aseprite
2. Créer des sprites de personnages et de variants
3. Animer ces sprites pour différentes actions
4. Exporter les animations au format compatible avec votre jeu
5. Intégrer ces assets dans votre système SpriteSheet existant
6. Appliquer des techniques pour créer un effet 2.5D convaincant

Ces compétences vous permettront de créer tous les assets graphiques nécessaires pour Ryuukon Palace dans un style cohérent avec l'esthétique 2.5D de Pokémon Noir/Blanc.

N'hésitez pas à expérimenter et à développer votre propre style tout en respectant les principes fondamentaux du 2.5D!

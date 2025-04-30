# Tutoriel complet pour la création d'assets 2.5D pour Ryuukon Palace

Ce tutoriel est destiné aux débutants et couvre l'ensemble du processus de création d'assets graphiques pour le jeu Ryuukon Palace, depuis l'installation des outils jusqu'à l'intégration dans le projet Java existant.

## Table des matières

1. [Choix des outils](#1-choix-des-outils)
2. [Installation des outils](#2-installation-des-outils)
3. [Création des sprites de personnages](#3-création-des-sprites-de-personnages)
4. [Création des animations](#4-création-des-animations)
5. [Création des maps](#5-création-des-maps)
6. [Exportation et intégration dans le projet Java](#6-exportation-et-intégration-dans-le-projet-java)
7. [Ressources additionnelles](#7-ressources-additionnelles)

## 1. Choix des outils

Pour un débutant travaillant sur un jeu 2.5D style Pokémon Noir/Blanc, voici les outils les plus adaptés:

- **Tiled Map Editor** : Pour créer les maps avec plusieurs couches (terrain, objets, collision)
- **Aseprite** : Pour créer et animer les sprites des personnages et créatures
- **GIMP** : Pour éditer les images et créer des assets graphiques généraux
- **TexturePacker** : Pour organiser les sprites en spritesheets

Ces outils sont relativement faciles à prendre en main et s'intègrent parfaitement avec votre projet Java existant.

## 2. Installation des outils

### Tiled Map Editor

1. Téléchargez Tiled depuis le site officiel: https://www.mapeditor.org/
2. Exécutez l'installateur et suivez les instructions
3. Une fois installé, lancez Tiled

### Aseprite

Aseprite est un logiciel payant (environ 20€), mais c'est l'outil le plus adapté pour les sprites pixel art.

1. Achetez et téléchargez Aseprite depuis: https://www.aseprite.org/
2. Exécutez l'installateur et suivez les instructions
3. Lancez Aseprite

**Alternative gratuite**: Si vous préférez une option gratuite, vous pouvez utiliser Piskel (https://www.piskelapp.com/) qui fonctionne directement dans le navigateur.

### GIMP

1. Téléchargez GIMP depuis: https://www.gimp.org/downloads/
2. Exécutez l'installateur et suivez les instructions
3. Lancez GIMP

### TexturePacker

1. Téléchargez TexturePacker depuis: https://www.codeandweb.com/texturepacker
2. Installez le logiciel
3. Vous pouvez utiliser la version gratuite qui a quelques limitations mais reste suffisante pour débuter

## 3. Création des sprites de personnages

### Configuration initiale dans Aseprite

1. Lancez Aseprite
2. Créez un nouveau fichier (Fichier > Nouveau)
   - Largeur: 32 pixels
   - Hauteur: 48 pixels (pour un personnage standard)
   - Mode: RGB Color
   - Background: Transparent

### Création d'un personnage de base

1. Utilisez l'outil crayon (touche B) avec une taille de 1px
2. Commencez par dessiner la silhouette du personnage
3. Ajoutez les détails: tête, corps, membres
4. Utilisez des couleurs différentes pour les différentes parties (peau, vêtements, cheveux)
5. Gardez un style cohérent avec Pokémon Noir/Blanc (proportions chibi, détails simplifiés)

### Création des différentes orientations

Pour un jeu RPG, vous aurez besoin de sprites pour 4 directions:

1. Créez un nouveau calque pour chaque direction (face, dos, gauche, droite)
2. Vous pouvez dupliquer le sprite face et le modifier pour créer les autres directions
3. Pour les vues de côté, vous pouvez créer une seule vue puis la retourner horizontalement

### Conseils pour le style 2.5D

1. Utilisez des ombres sous les personnages pour donner l'impression qu'ils sont posés sur le sol
2. Ajoutez de légères ombres sur les personnages pour suggérer la profondeur
3. Variez la taille des personnages selon leur importance (PNJ principaux plus grands)

## 4. Création des animations

### Animation de marche dans Aseprite

1. Dans Aseprite, utilisez la timeline (Window > Timeline)
2. Pour chaque direction, créez 4 frames:
   - Position neutre
   - Jambe gauche en avant
   - Position neutre (ou légèrement différente)
   - Jambe droite en avant
3. Réglez la durée de chaque frame à 100-150ms (vitesse de marche normale)
4. Prévisualisez l'animation avec la touche Entrée

### Animation d'actions spéciales

1. Créez des animations pour les actions importantes:
   - Lancer une capture (mouvement de bras)
   - Réaction à un combat
   - Célébration de capture
2. Pour chaque action, créez 3-6 frames selon la complexité
3. Exportez chaque animation séparément

### Export des animations

1. Dans Aseprite, allez dans File > Export Sprite Sheet
2. Configurez l'export:
   - Layout: Horizontal Strip (toutes les frames côte à côte)
   - Borders: 0px
   - Trim: Désactivé
   - Sheet Type: Packed
   - Cochez "JSON Data" pour exporter les métadonnées
3. Exportez dans le dossier `src/main/resources/assets/characters/`

## 5. Création des maps

### Configuration initiale dans Tiled

1. Lancez Tiled
2. Créez un nouveau projet (Fichier > Nouveau > Nouveau Projet)
3. Sauvegardez-le dans un dossier `map_project` à la racine de votre projet

### Création d'un tileset

1. Dans Tiled, créez un nouveau tileset (Fichier > Nouveau > Nouveau Tileset)
   - Nom: "terrain_tileset"
   - Type: Basé sur une image
   - Source: Choisissez une image de tileset (ou créez-en une avec GIMP/Aseprite)
   - Taille des tuiles: 32x32 pixels
2. Enregistrez le tileset dans `map_project/tilesets/`

### Création d'une carte avec plusieurs couches

1. Créez une nouvelle carte (Fichier > Nouveau > Nouvelle Carte)
   - Orientation: Orthogonale
   - Format de couche de tuiles: CSV
   - Taille de la carte: 50x50 tuiles (ajustez selon vos besoins)
   - Taille des tuiles: 32x32 pixels
2. Créez les couches suivantes (clic droit sur le panneau Couches > Ajouter Couche de Tuiles):
   - terrainLayer (base du terrain)
   - detailLayer (détails comme l'herbe, fleurs)
   - objectBackLayer (objets derrière les personnages)
   - objectFrontLayer (objets devant les personnages)
   - collisionLayer (zones non accessibles)
   - heightLayer (variations de hauteur pour l'effet 3D)

### Création d'une ville avec effet 2.5D

1. Sur la couche terrainLayer, placez les tuiles de base (routes, herbe, eau)
2. Sur detailLayer, ajoutez des détails (fleurs, petites pierres)
3. Sur objectBackLayer, placez les bâtiments et grands objets
4. Sur objectFrontLayer, placez les objets qui doivent apparaître devant le joueur
5. Sur collisionLayer, marquez les zones non accessibles (utilisez une tuile rouge semi-transparente)
6. Sur heightLayer, utilisez des valeurs numériques (1-5) pour indiquer la hauteur relative

### Conseils pour l'effet 2.5D

1. Utilisez des bâtiments pré-rendus avec perspective
2. Placez les objets plus "hauts" sur la carte plus haut à l'écran
3. Utilisez des ombres pour renforcer l'effet de profondeur
4. Créez des variations de hauteur pour les collines et les escaliers

## 6. Exportation et intégration dans le projet Java

### Exportation des maps depuis Tiled

1. Dans Tiled, allez dans Fichier > Exporter sous
2. Choisissez le format JSON
3. Enregistrez dans `src/main/resources/maps/`

### Création d'un convertisseur Tiled vers format Ryuukon

Créez un script Java pour convertir les fichiers Tiled en format compatible avec votre TileSystem:

```java
// Créez ce fichier dans src/main/java/com/ryuukonpalace/game/utils/TiledMapConverter.java

package com.ryuukonpalace.game.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TiledMapConverter {
    
    public static void convertTiledMapToRyuukonFormat(String tiledMapPath, String outputPath) {
        try {
            // Charger le fichier JSON de Tiled
            JsonObject tiledMap = JsonParser.parseReader(new FileReader(new File(tiledMapPath))).getAsJsonObject();
            
            // Créer un nouvel objet JSON pour le format Ryuukon
            JsonObject ryuukonMap = new JsonObject();
            
            // Copier les dimensions
            ryuukonMap.addProperty("width", tiledMap.get("width").getAsInt());
            ryuukonMap.addProperty("height", tiledMap.get("height").getAsInt());
            
            // Extraire les données des couches
            JsonArray layers = tiledMap.getAsJsonArray("layers");
            
            // Initialiser les couches Ryuukon
            JsonArray terrainLayer = new JsonArray();
            JsonArray detailLayer = new JsonArray();
            JsonArray objectBackLayer = new JsonArray();
            JsonArray objectFrontLayer = new JsonArray();
            JsonArray collisionLayer = new JsonArray();
            JsonArray heightLayer = new JsonArray();
            
            // Parcourir les couches Tiled
            for (int i = 0; i < layers.size(); i++) {
                JsonObject layer = layers.get(i).getAsJsonObject();
                String layerName = layer.get("name").getAsString();
                JsonArray data = layer.getAsJsonArray("data");
                
                // Convertir les données de la couche au format Ryuukon
                switch (layerName) {
                    case "terrainLayer":
                        convertLayerData(data, terrainLayer, tiledMap.get("width").getAsInt());
                        break;
                    case "detailLayer":
                        convertLayerData(data, detailLayer, tiledMap.get("width").getAsInt());
                        break;
                    case "objectBackLayer":
                        convertLayerData(data, objectBackLayer, tiledMap.get("width").getAsInt());
                        break;
                    case "objectFrontLayer":
                        convertLayerData(data, objectFrontLayer, tiledMap.get("width").getAsInt());
                        break;
                    case "collisionLayer":
                        convertLayerData(data, collisionLayer, tiledMap.get("width").getAsInt());
                        break;
                    case "heightLayer":
                        convertLayerData(data, heightLayer, tiledMap.get("width").getAsInt());
                        break;
                }
            }
            
            // Ajouter les couches au format Ryuukon
            ryuukonMap.add("terrainLayer", terrainLayer);
            ryuukonMap.add("detailLayer", detailLayer);
            ryuukonMap.add("objectBackLayer", objectBackLayer);
            ryuukonMap.add("objectFrontLayer", objectFrontLayer);
            ryuukonMap.add("collisionLayer", collisionLayer);
            ryuukonMap.add("heightLayer", heightLayer);
            
            // Écrire le fichier JSON
            try (FileWriter writer = new FileWriter(outputPath)) {
                writer.write(ryuukonMap.toString());
            }
            
            System.out.println("Conversion réussie: " + outputPath);
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void convertLayerData(JsonArray tiledData, JsonArray ryuukonLayer, int width) {
        int height = tiledData.size() / width;
        
        // Créer un tableau 2D pour le format Ryuukon
        for (int y = 0; y < height; y++) {
            JsonArray row = new JsonArray();
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                row.add(tiledData.get(index).getAsInt());
            }
            ryuukonLayer.add(row);
        }
    }
    
    public static void main(String[] args) {
        // Exemple d'utilisation
        convertTiledMapToRyuukonFormat(
            "src/main/resources/maps/tiled/lumina_city.json",
            "src/main/resources/maps/lumina_city.json"
        );
    }
}
```

### Exportation des spritesheets depuis TexturePacker

1. Lancez TexturePacker
2. Ajoutez vos sprites individuels (glissez-déposez les fichiers)
3. Configurez les paramètres:
   - Data Format: JSON (Array)
   - Texture Format: PNG
   - Size Constraints: POT (Power of Two)
   - Allow Rotation: Désactivé
   - Trim Mode: None
4. Publiez la spritesheet dans `src/main/resources/assets/spritesheets/`

### Intégration des spritesheets dans le code Java

Modifiez votre classe SpriteLoader pour charger les spritesheets générées:

```java
// Ajoutez cette méthode à votre classe SpriteLoader

public SpriteSheet loadSpriteSheetFromJson(String jsonPath) {
    try {
        // Charger le fichier JSON
        JsonObject sheetJson = JsonParser.parseReader(new FileReader(new File(jsonPath))).getAsJsonObject();
        
        // Obtenir le chemin de l'image
        String imagePath = sheetJson.get("meta").getAsJsonObject().get("image").getAsString();
        
        // Charger l'image
        BufferedImage image = ImageIO.read(new File(imagePath));
        
        // Créer la spritesheet
        SpriteSheet spriteSheet = new SpriteSheet(image, 
            sheetJson.get("frames").getAsJsonArray().get(0).getAsJsonObject().get("sourceSize").getAsJsonObject().get("w").getAsInt(),
            sheetJson.get("frames").getAsJsonArray().get(0).getAsJsonObject().get("sourceSize").getAsJsonObject().get("h").getAsInt());
        
        // Ajouter les frames
        JsonArray frames = sheetJson.get("frames").getAsJsonArray();
        for (int i = 0; i < frames.size(); i++) {
            JsonObject frame = frames.get(i).getAsJsonObject();
            String frameName = frame.get("filename").getAsString();
            
            // Extraire les coordonnées
            int x = frame.get("frame").getAsJsonObject().get("x").getAsInt() / spriteSheet.getSpriteWidth();
            int y = frame.get("frame").getAsJsonObject().get("y").getAsInt() / spriteSheet.getSpriteHeight();
            
            // Définir l'animation
            spriteSheet.defineAnimation(frameName, new int[]{x, y});
        }
        
        return spriteSheet;
    } catch (Exception e) {
        System.err.println("Erreur lors du chargement de la spritesheet: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
```

## 7. Ressources additionnelles

### Tutoriels recommandés

- **Tiled Map Editor**: https://doc.mapeditor.org/en/stable/manual/introduction/
- **Aseprite**: https://www.aseprite.org/docs/
- **Pixel Art pour débutants**: https://lospec.com/pixel-art-tutorials

### Ressources graphiques gratuites

- **OpenGameArt**: https://opengameart.org/
- **Kenney Assets**: https://kenney.nl/assets
- **Itch.io**: https://itch.io/game-assets/free

### Communautés pour l'aide

- **Reddit r/gamedev**: https://www.reddit.com/r/gamedev/
- **Pixel Art Discord**: https://discord.gg/pixelart
- **TigSource Forums**: https://forums.tigsource.com/

## Conclusion

Ce tutoriel vous a guidé à travers les étapes essentielles pour créer et intégrer des assets 2.5D dans votre projet Ryuukon Palace. En suivant ces instructions, vous pourrez:

1. Créer des sprites de personnages et de créatures
2. Animer ces sprites pour différentes actions
3. Concevoir des maps avec un effet de perspective 2.5D
4. Intégrer tous ces éléments dans votre moteur de jeu Java existant

N'hésitez pas à explorer les ressources additionnelles pour approfondir vos connaissances et améliorer vos compétences en création d'assets pour jeux vidéo.

Bon développement!

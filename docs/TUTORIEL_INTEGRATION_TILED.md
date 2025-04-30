# Tutoriel d'intégration de Tiled pour Ryuukon Palace

Ce tutoriel détaillé vous guidera pas à pas dans l'installation et l'utilisation de Tiled Map Editor pour créer des cartes 2.5D dans le style de Pokémon Noir/Blanc pour votre jeu Ryuukon Palace.

## Table des matières

1. [Installation de Tiled](#1-installation-de-tiled)
2. [Configuration initiale](#2-configuration-initiale)
3. [Création d'un tileset](#3-création-dun-tileset)
4. [Création d'une carte pour Lumina](#4-création-dune-carte-pour-lumina)
5. [Ajout de l'effet de perspective 2.5D](#5-ajout-de-leffet-de-perspective-25d)
6. [Placement des bâtiments et PNJ](#6-placement-des-bâtiments-et-pnj)
7. [Exportation et intégration dans le projet](#7-exportation-et-intégration-dans-le-projet)
8. [Exemple complet](#8-exemple-complet)

## 1. Installation de Tiled

1. **Téléchargement**
   - Rendez-vous sur le site officiel: https://www.mapeditor.org/
   - Cliquez sur "Download" et choisissez la version pour Windows
   - Téléchargez le fichier d'installation (ex: `Tiled-1.8.6-win64.msi`)

2. **Installation**
   - Exécutez le fichier d'installation téléchargé
   - Suivez les instructions à l'écran
   - Acceptez l'emplacement d'installation par défaut
   - Cliquez sur "Terminer" une fois l'installation complétée

3. **Premier lancement**
   - Lancez Tiled depuis le menu Démarrer
   - Familiarisez-vous avec l'interface:
     - Panneau de calques (à droite)
     - Panneau de tilesets (en bas)
     - Zone d'édition principale (au centre)

## 2. Configuration initiale

1. **Création d'un nouveau projet**
   - Allez dans Fichier > Nouveau > Nouveau Projet
   - Choisissez un emplacement dans votre projet: `c:\Users\Test IA\RyuukonPalace\map_project`
   - Nommez le fichier `ryuukon_maps.tiled-project`
   - Cliquez sur "Enregistrer"

2. **Configuration des préférences**
   - Allez dans Edition > Préférences
   - Dans l'onglet "Général", configurez:
     - Format d'exportation par défaut: JSON
     - Taille de tuile par défaut: 32x32 pixels
   - Cliquez sur "OK" pour valider

3. **Création des dossiers de projet**
   - Dans l'explorateur Windows, créez les dossiers suivants:
     - `c:\Users\Test IA\RyuukonPalace\map_project\tilesets`
     - `c:\Users\Test IA\RyuukonPalace\map_project\maps`
     - `c:\Users\Test IA\RyuukonPalace\map_project\objects`

## 3. Création d'un tileset

1. **Téléchargement de ressources de base**
   - Pour commencer rapidement, téléchargez des tilesets gratuits:
     - Site recommandé: https://opengameart.org/content/16x16-rpg-tileset
     - Enregistrez les images dans `map_project\tilesets`

2. **Création d'un nouveau tileset**
   - Dans Tiled, allez dans Fichier > Nouveau > Nouveau Tileset
   - Configurez le tileset:
     - Nom: `terrain_base`
     - Type: Basé sur une image
     - Source: Sélectionnez votre image de tileset
     - Taille des tuiles: 32x32 pixels
     - Marge: 0 pixels
     - Espacement: 0 pixels
   - Cliquez sur "OK"

3. **Configuration des propriétés des tuiles**
   - Sélectionnez une tuile dans le tileset
   - Dans le panneau Propriétés (à droite), cliquez sur "+"
   - Ajoutez les propriétés suivantes:
     - `collision` (type: bool) - Pour marquer les tuiles solides
     - `height` (type: int) - Pour l'effet de hauteur 2.5D
   - Configurez ces propriétés pour chaque tuile importante

4. **Enregistrement du tileset**
   - Allez dans Fichier > Enregistrer le tileset sous
   - Enregistrez dans `map_project\tilesets\terrain_base.tsx`

## 4. Création d'une carte pour Lumina

1. **Création d'une nouvelle carte**
   - Allez dans Fichier > Nouveau > Nouvelle Carte
   - Configurez la carte:
     - Orientation: Orthogonale
     - Format de calque de tuiles: CSV
     - Ordre de rendu: Droit
     - Taille de la carte: 50x50 tuiles
     - Taille des tuiles: 32x32 pixels
   - Cliquez sur "OK"

2. **Ajout du tileset à la carte**
   - Dans le panneau Tilesets, cliquez sur "+"
   - Sélectionnez "Ajouter un tileset existant"
   - Naviguez jusqu'à `map_project\tilesets\terrain_base.tsx`
   - Cliquez sur "Ouvrir"

3. **Création des calques nécessaires**
   - Dans le panneau Calques, créez les calques suivants (clic droit > Ajouter calque de tuiles):
     - `terrainLayer` - Base du terrain
     - `detailLayer` - Détails comme l'herbe, fleurs
     - `objectBackLayer` - Objets derrière les personnages
     - `objectFrontLayer` - Objets devant les personnages
     - `collisionLayer` - Zones non accessibles
     - `heightLayer` - Variations de hauteur pour l'effet 3D

4. **Dessin de la carte de base**
   - Sélectionnez le calque `terrainLayer`
   - Utilisez l'outil Pinceau (B) pour dessiner le terrain de base
   - Placez les routes, l'herbe, l'eau, etc.
   - Pensez à la disposition générale de Lumina selon le lore (palais central, académie, bibliothèque)

## 5. Ajout de l'effet de perspective 2.5D

1. **Utilisation du calque de hauteur**
   - Sélectionnez le calque `heightLayer`
   - Utilisez des tuiles numérotées (1-5) pour indiquer la hauteur
   - Plus la valeur est élevée, plus l'élément sera "haut" dans la perspective

2. **Création de l'effet de profondeur**
   - Sur les calques `objectBackLayer` et `objectFrontLayer`:
     - Placez les objets plus "hauts" (bâtiments, arbres) sur `objectBackLayer`
     - Placez les objets plus "bas" (buissons, bancs) sur `objectFrontLayer`
   - Utilisez le décalage vertical pour simuler la perspective:
     - Les objets plus éloignés sont placés plus haut sur l'écran
     - Les objets plus proches sont placés plus bas

3. **Ajout d'ombres**
   - Sur le calque `detailLayer`, ajoutez des ombres sous les objets
   - Utilisez des tuiles semi-transparentes noires pour les ombres
   - Placez les ombres légèrement décalées par rapport aux objets

## 6. Placement des bâtiments et PNJ

1. **Création d'objets pour les bâtiments**
   - Allez dans Fichier > Nouveau > Nouveau Jeu de Tuiles d'Objets
   - Nommez-le `buildings`
   - Ajoutez des objets pour les bâtiments principaux:
     - Palais de Ryuukon
     - Académie des Tacticiens
     - Bibliothèque
     - Maisons
     - Boutiques

2. **Placement des bâtiments**
   - Créez un nouveau calque d'objets: `buildingsLayer`
   - Placez les bâtiments selon la disposition de Lumina
   - Configurez les propriétés pour chaque bâtiment:
     - `name` - Nom du bâtiment
     - `type` - Type (palace, academy, shop, house)
     - `entranceX`, `entranceY` - Position de l'entrée

3. **Placement des PNJ**
   - Créez un nouveau calque d'objets: `npcsLayer`
   - Ajoutez des objets pour les PNJ principaux:
     - Maître Orion
     - Lady Elara
     - Marchands
     - Habitants
   - Configurez les propriétés pour chaque PNJ:
     - `id` - Identifiant unique
     - `name` - Nom du PNJ
     - `type` - Type (tacticien, guerrier, marchand)
     - `dialogue` - ID du dialogue initial

## 7. Exportation et intégration dans le projet

1. **Exportation de la carte au format JSON**
   - Allez dans Fichier > Exporter sous
   - Choisissez le format JSON
   - Enregistrez dans `c:\Users\Test IA\RyuukonPalace\src\main\resources\maps\lumina_city.json`

2. **Création d'un convertisseur Tiled vers format Ryuukon**
   - Créez un nouveau fichier Java: `c:\Users\Test IA\RyuukonPalace\src\main\java\com\ryuukonpalace\game\utils\TiledMapConverter.java`
   - Implémentez le code de conversion (voir l'exemple dans le tutoriel précédent)

3. **Intégration dans le code existant**
   - Modifiez la classe `TileSystem` pour charger les cartes converties
   - Ajoutez le support pour les propriétés personnalisées des tuiles
   - Implémentez le rendu des objets et PNJ

4. **Test de la carte**
   - Exécutez le convertisseur pour transformer la carte Tiled en format Ryuukon
   - Lancez le jeu avec la nouvelle carte
   - Vérifiez que l'effet 2.5D fonctionne correctement

## 8. Exemple complet

Voici un exemple de structure pour la ville de Lumina avec l'effet 2.5D:

```json
{
  "width": 50,
  "height": 50,
  "tilewidth": 32,
  "tileheight": 32,
  "layers": [
    {
      "name": "terrainLayer",
      "data": [...],
      "width": 50,
      "height": 50
    },
    {
      "name": "detailLayer",
      "data": [...],
      "width": 50,
      "height": 50
    },
    {
      "name": "objectBackLayer",
      "data": [...],
      "width": 50,
      "height": 50
    },
    {
      "name": "objectFrontLayer",
      "data": [...],
      "width": 50,
      "height": 50
    },
    {
      "name": "collisionLayer",
      "data": [...],
      "width": 50,
      "height": 50
    },
    {
      "name": "heightLayer",
      "data": [...],
      "width": 50,
      "height": 50
    },
    {
      "name": "buildingsLayer",
      "type": "objectgroup",
      "objects": [
        {
          "id": 1,
          "name": "Ryuukon Palace",
          "type": "palace",
          "x": 800,
          "y": 400,
          "width": 256,
          "height": 320,
          "properties": {
            "entranceX": 928,
            "entranceY": 720,
            "entranceWidth": 64
          }
        },
        {
          "id": 2,
          "name": "Tactician Academy",
          "type": "academy",
          "x": 400,
          "y": 600,
          "width": 192,
          "height": 224,
          "properties": {
            "entranceX": 496,
            "entranceY": 824,
            "entranceWidth": 48
          }
        }
      ]
    },
    {
      "name": "npcsLayer",
      "type": "objectgroup",
      "objects": [
        {
          "id": 1,
          "name": "Master Orion",
          "type": "tacticien",
          "x": 816,
          "y": 464,
          "properties": {
            "dialogueId": "orion_greeting"
          }
        },
        {
          "id": 2,
          "name": "Lady Elara",
          "type": "tacticien",
          "x": 880,
          "y": 464,
          "properties": {
            "dialogueId": "elara_greeting"
          }
        }
      ]
    }
  ]
}
```

Ce format JSON peut être converti en format compatible avec votre système de tuiles existant.

## Conclusion

En suivant ce tutoriel, vous avez appris à:
1. Installer et configurer Tiled Map Editor
2. Créer des tilesets adaptés au style 2.5D
3. Concevoir une carte complète pour la ville de Lumina
4. Ajouter l'effet de perspective caractéristique de Pokémon Noir/Blanc
5. Placer des bâtiments et PNJ
6. Exporter et intégrer la carte dans votre projet Java

Cette approche vous permet de créer rapidement des environnements riches et détaillés pour Ryuukon Palace, tout en maintenant l'effet visuel 2.5D souhaité.

N'hésitez pas à explorer les fonctionnalités avancées de Tiled comme les animations de tuiles, les calques d'image, et les scripts d'automatisation pour enrichir davantage vos cartes.

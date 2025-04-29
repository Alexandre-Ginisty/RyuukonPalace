# Guide du Style Graphique 2.5D pour Ryuukon Palace

Ce document détaille les spécifications et directives pour créer des assets graphiques dans le style 2.5D inspiré de Pokémon Noir/Blanc pour le projet Ryuukon Palace.

## Table des matières

1. [Introduction au style 2.5D](#introduction-au-style-25d)
2. [Spécifications techniques](#spécifications-techniques)
3. [Personnages et créatures](#personnages-et-créatures)
4. [Environnements et bâtiments](#environnements-et-bâtiments)
5. [Interface utilisateur](#interface-utilisateur)
6. [Effets visuels](#effets-visuels)
7. [Ressources et références](#ressources-et-références)
8. [Pipeline de travail](#pipeline-de-travail)

## Introduction au style 2.5D

Le style 2.5D de Pokémon Noir/Blanc se caractérise par une combinaison unique d'éléments 2D et d'effets de perspective qui créent une illusion de profondeur. Les principales caractéristiques de ce style sont :

- **Perspective isométrique modifiée** : Vue de dessus légèrement inclinée
- **Bâtiments avec effet de profondeur** : Structures dessinées avec des faces visibles et des ombres
- **Personnages en 2D** : Sprites plats qui se déplacent dans un environnement avec profondeur
- **Couches de profondeur** : Plusieurs niveaux de rendu pour créer l'illusion de 3D
- **Variations de hauteur** : Différents niveaux d'élévation pour le terrain

Ce style permet de créer un monde visuellement riche sans nécessiter de modélisation 3D complexe.

## Spécifications techniques

### Résolutions et dimensions

- **Taille des tuiles de base** : 32x32 pixels
- **Taille des personnages** : 64x64 pixels (debout)
- **Taille des créatures** : Variable selon l'espèce, base de 64x64 pixels
- **Taille des bâtiments** : Variable selon le type, base de 128x128 pixels
- **Résolution de travail** : 1920x1080 pixels (redimensionnable)

### Palette de couleurs

- **Style général** : Palette riche mais cohérente, avec des contrastes marqués
- **Environnements** : 
  - Zones naturelles : Tons verts, bruns, bleus
  - Zones urbaines : Gris, beiges, accents de couleurs vives
  - Zones spéciales : Palettes thématiques (volcans, grottes, etc.)
- **Personnages** : Couleurs vives pour se démarquer des environnements
- **Interface** : Palette sombre avec accents lumineux, inspirée de Fear & Hunger

### Format des fichiers

- **Sprites** : PNG avec transparence (32 bits)
- **Spritesheets** : PNG avec transparence, organisés en grilles
- **Tilesets** : PNG avec transparence, organisés en grilles de 32x32 pixels
- **Fichiers de travail** : PSD (Photoshop) ou équivalent avec calques

## Personnages et créatures

### Personnages jouables

- **Vues** : 4 directions (haut, bas, gauche, droite)
- **Animations par direction** : 
  - Idle (1-2 frames)
  - Marche (4 frames)
  - Course (4 frames)
  - Action (2-4 frames)
- **Style** : Proportions légèrement stylisées (têtes plus grandes)
- **Détails** : Contours nets, ombres minimales, détails intérieurs simplifiés

### PNJ (Personnages Non Joueurs)

- **Vues** : 4 directions (selon importance du PNJ)
- **Animations** : Plus simples que le personnage jouable
- **Variations** : Différentes tenues, coiffures, accessoires pour créer de la diversité

### Créatures (Variants)

- **Vues** : Face et dos (combat), 4 directions (monde)
- **Animations de combat** : 
  - Idle (1-2 frames)
  - Attaque (2-4 frames)
  - Dégât (1-2 frames)
  - Spécial (variable)
- **Style** : Cohérent avec le lore, mélange d'éléments organiques et mystiques
- **Variations** : Différentes couleurs/motifs pour les variants rares

## Environnements et bâtiments

### Tuiles de terrain

- **Types de base** : 
  - Herbe (plusieurs variations)
  - Terre/Chemin
  - Sable
  - Eau (peu profonde, profonde)
  - Pierre/Roche
  - Neige/Glace
- **Variations de hauteur** : 3-4 niveaux d'élévation par type de terrain
- **Transitions** : Tuiles de transition entre différents types de terrain

### Bâtiments

- **Structure** : 
  - Base (fondations)
  - Corps (murs, fenêtres)
  - Toit
  - Détails (cheminées, antennes, etc.)
- **Perspective** : Face avant et côté visible, toit vu de dessus
- **Échelle** : Proportionnelle aux personnages (entrées à hauteur de personnage)
- **Variations** : Différents styles architecturaux selon les régions

### Objets d'environnement

- **Petits objets** : Rochers, buissons, fleurs, panneaux
- **Objets moyens** : Arbres, lampadaires, fontaines
- **Grands objets** : Statues, monuments, structures naturelles
- **Interactifs** : Objets avec lesquels le joueur peut interagir (portes, coffres)

## Interface utilisateur

### Menus

- **Style** : Sombre et élégant, avec des accents lumineux
- **Cadres** : Bordures ornées mais lisibles
- **Boutons** : Clairement identifiables, avec états (normal, survolé, pressé)

### HUD (Affichage tête haute)

- **Minimaliste** : Informations essentielles uniquement
- **Positionnement** : Coins de l'écran, non intrusif
- **Animations** : Transitions fluides entre les états

### Dialogues

- **Boîtes de dialogue** : Style cohérent avec les menus
- **Portraits** : Expressions faciales des personnages importants
- **Indicateurs** : Signaux clairs pour les choix et la progression

## Effets visuels

### Effets météorologiques

- **Pluie** : Gouttes animées, reflets au sol
- **Neige** : Flocons qui tombent, accumulation au sol
- **Brouillard** : Voile semi-transparent, densité variable
- **Orage** : Éclairs, ombres dynamiques

### Effets de combat

- **Attaques** : Animations stylisées selon le type d'attaque
- **Impacts** : Effets visuels au point d'impact
- **États** : Indicateurs visuels pour les états altérés

### Transitions

- **Entre zones** : Fondus, balayages
- **Entrée en combat** : Animation distinctive
- **Jour/Nuit** : Transition graduelle des couleurs et de l'éclairage

## Ressources et références

### Inspirations principales

- **Pokémon Noir/Blanc** : Pour le style 2.5D général
- **Fear & Hunger** : Pour l'ambiance sombre et mature
- **Octopath Traveler** : Pour les effets de profondeur et d'éclairage
- **Persona 5** : Pour le style d'interface

### Outils recommandés

- **Création de sprites** : Aseprite, Photoshop, GIMP
- **Animation** : Spine, DragonBones, ou animation frame-by-frame
- **Tilesets** : Tiled pour la création et le test
- **Effets** : After Effects pour les concepts, implémentation via code

## Pipeline de travail

### Création d'un personnage

1. **Concept** : Dessins et descriptions du personnage
2. **Sprite de base** : Création du sprite face/dos
3. **Animations** : Création des frames d'animation
4. **Directions** : Adaptation pour les 4 directions
5. **Test en jeu** : Intégration et ajustements

### Création d'un environnement

1. **Concept** : Plan général de la zone
2. **Tilesets** : Création des tuiles de base et variations
3. **Bâtiments** : Conception des structures principales
4. **Objets** : Ajout des éléments décoratifs et interactifs
5. **Éclairage** : Définition de l'ambiance lumineuse
6. **Test en jeu** : Intégration et ajustements

### Conversion d'assets 3D en 2.5D

1. **Modèle 3D** : Si disponible, utiliser comme référence
2. **Rendu orthographique** : Capturer des vues sous angles fixes
3. **Simplification** : Réduire les détails pour correspondre au style
4. **Adaptation** : Redessiner en 2D avec effet de perspective
5. **Intégration** : Ajuster pour correspondre au système de couches

## Conseils pour maintenir la cohérence

1. Utiliser des modèles (templates) pour les proportions
2. Créer une palette de couleurs de référence et s'y tenir
3. Tester régulièrement les assets dans le jeu
4. Documenter les décisions stylistiques importantes
5. Maintenir une bibliothèque d'éléments réutilisables

# 🎮 Guide du Démonstrateur - Ryuukon Palace

Ce guide explique comment utiliser les démonstrateurs de Ryuukon Palace pour tester les fonctionnalités du jeu.

## 📋 Versions disponibles

Deux versions du démonstrateur sont disponibles :

### 1. 🚀 Version complète (LWJGL)

La version complète utilise LWJGL (Lightweight Java Game Library) pour offrir des graphismes optimisés et des fonctionnalités avancées.

**Prérequis :**
- Java JDK 11 ou supérieur
- Bibliothèques LWJGL (voir section Installation)

**Lancement :**
- Exécutez `run_demo.bat` dans le répertoire principal

### 2. 🔄 Version simplifiée (Swing)

La version simplifiée utilise Swing, la bibliothèque graphique standard de Java. Elle ne nécessite aucune dépendance externe.

**Prérequis :**
- Java JDK 11 ou supérieur

**Lancement :**
- Exécutez `run_simple_demo.bat` dans le répertoire principal

## 🛠️ Installation

### Version simplifiée (recommandée pour démarrer)

1. Assurez-vous que Java est installé sur votre système
2. Double-cliquez sur `run_simple_demo.bat`
3. Le script compile et lance automatiquement le démonstrateur

### Version complète (avec LWJGL)

#### Option 1 : Installation automatique

1. Exécutez `download_dependencies.bat` pour télécharger les dépendances LWJGL
2. Attendez que le téléchargement soit terminé
3. Exécutez `run_demo.bat` pour lancer le démonstrateur

#### Option 2 : Installation manuelle

1. Téléchargez les bibliothèques LWJGL depuis [lwjgl.org](https://www.lwjgl.org/download)
2. Créez un dossier `lib` dans le répertoire principal s'il n'existe pas déjà
3. Placez les fichiers JAR de LWJGL dans le dossier `lib`
4. Exécutez `run_demo.bat` pour lancer le démonstrateur

## 🎯 Fonctionnalités démontrées

### Système de rendu 2.5D
- Affichage de tuiles avec perspective isométrique
- Superposition de couches pour créer l'effet de profondeur
- Gestion des objets à différentes hauteurs

### Animation de personnage
- Déplacement dans quatre directions
- Animation de marche
- Gestion des collisions

### Système de carte
- Différents types de terrain (herbe, chemin, eau)
- Obstacles (arbres, rochers)
- Effets de perspective

## 🎮 Commandes

### Déplacement
- **Flèches directionnelles** ou **ZQSD** : Déplacer le personnage
- **ESC** : Quitter le démonstrateur

## 🔍 Exploration du code

Les classes principales du démonstrateur se trouvent dans le package `com.ryuukonpalace.game.demo` :

- `CharacterDemo.java` : Implémentation principale du démonstrateur LWJGL
- `DemoLauncher.java` : Point d'entrée pour la version LWJGL
- `SimpleDemoLauncher.java` : Version simplifiée utilisant Swing

Ces classes utilisent les composants principaux du moteur de jeu :

- `Renderer` : Système de rendu graphique
- `TileSystem` : Gestion des tuiles et de la carte
- `SpriteLoader` : Chargement et gestion des sprites

## 🐛 Résolution des problèmes

### Le démonstrateur LWJGL ne démarre pas

1. Vérifiez que les dépendances LWJGL sont correctement installées dans le dossier `lib`
2. Assurez-vous que Java est correctement installé et accessible dans le PATH
3. Essayez la version simplifiée (`run_simple_demo.bat`) qui ne nécessite pas LWJGL

### Problèmes graphiques

1. Assurez-vous que votre carte graphique supporte OpenGL
2. Mettez à jour vos pilotes graphiques
3. Si les problèmes persistent, utilisez la version simplifiée qui utilise Swing

### Erreurs de compilation

1. Vérifiez que vous utilisez Java 11 ou supérieur
2. Assurez-vous que tous les fichiers source sont présents
3. Consultez les messages d'erreur pour identifier le problème spécifique

## 📝 Notes de développement

Le démonstrateur est conçu pour illustrer les concepts clés du jeu sans implémenter toutes les fonctionnalités. Il se concentre sur :

1. Le système de rendu 2.5D
2. L'animation de personnage
3. La gestion de la caméra
4. Les collisions de base

Pour une expérience complète, le jeu final inclura :
- Système de combat
- Capture de variants
- Quêtes et progression
- Interface utilisateur complète

---

Pour toute question ou suggestion concernant le démonstrateur, veuillez consulter le fichier README.md ou contacter l'équipe de développement.

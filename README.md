# 🏯 Ryuukon Palace

![Version](https://img.shields.io/badge/version-0.5.0-blue)
![License](https://img.shields.io/badge/license-MIT-green)

Un RPG inspiré de Pokémon avec une ambiance plus mature, où les joueurs peuvent capturer des créatures (variants) à l'aide de QTE (Quick Time Events) et explorer un monde divisé par des conflits idéologiques.

## 📖 Présentation

Ryuukon Palace est un jeu RPG qui propose :

- 🎮 **Gameplay innovant** : Capture de créatures via des séquences de QTE
- 🌟 **Évolution des créatures** : Système d'évolution basé sur les niveaux et conditions spéciales
- ⚔️ **Combats tactiques** : Affrontements stratégiques entre créatures
- 🌍 **Monde ouvert** : Exploration d'un univers avec un style visuel 2.5D inspiré de Pokémon Noir/Blanc
- 📜 **Histoire immersive** : Récit mature avec des thèmes profonds
- 🏴 **Système de factions** : Choix d'allégeance influençant l'expérience de jeu

## 🎮 Démo

Pour essayer la démo du jeu :

1. **Version complète** : `run_demo.bat` (Windows) ou `./run_demo.sh` (Mac/Linux, nécessite Java 17+ et Maven ; les natives LWJGL sont choisies automatiquement selon l'OS)
2. **Version simplifiée** : `run_simple_demo.bat` (Windows) ou `./run_simple_demo.sh` (Mac/Linux), aucune dépendance requise

Consultez [Guide de la démo](docs/DEMO_GUIDE.md) pour plus d'informations.

## 🌍 Univers et Lore

Depuis des siècles, l'humanité s'est divisée en deux factions opposées :

- **Tacticiens** : Utilisent et éduquent les Variants pour combattre
- **Guerriers** : S'opposent à l'utilisation des Variants comme outils et les affrontent directement

Cette division fondamentale a façonné le monde de Ryuukon Palace, créant un univers riche en conflits et en intrigues.

[En savoir plus sur l'univers du jeu](docs/LORE_RESUME.md)

## 🏢 Factions principales

| Faction | Base | Philosophie | Leaders |
|---------|------|-------------|---------|
| **Ordre des Tacticiens** | Lumina | Maîtriser les variants pour le progrès | Maître Orion, Lady Elara |
| **Fraternité des Guerriers** | Stormpeak | Combattre directement les variants | Général Thorne, Sir Rainer |
| **Guilde des Marchands** | Darkhaven | Profit et commerce, neutralité | Baron Silvius, Dame Ophelia |
| **Gardiens de la Nature** | Whisperwind Valley | Coexistence avec les variants | Sage Thalia, Ranger Eldon |
| **Cercle des Mystiques** | Ancient Ruins | Étude des origines des variants | Archimage Vex, Prophétesse Lyra |

## 🖼️ Style graphique

Ryuukon Palace utilise un style graphique 2.5D inspiré de Pokémon Noir/Blanc :

- Rendu par couches pour créer l'illusion de profondeur
- Bâtiments avec perspective
- Variations de hauteur pour simuler le relief
- Éclairage dynamique adapté au style 2.5D

[En savoir plus sur le style graphique](docs/STYLE_GRAPHIQUE.md)

## 🧩 Architecture du projet

```
RyuukonPalace/
├── src/main/java/com/ryuukonpalace/game/
│   ├── core/         # Moteur du jeu et composants principaux
│   ├── world/        # Système de monde et génération
│   ├── creatures/    # Système de variants (créatures)
│   ├── battle/       # Système de combat
│   ├── player/       # Gestion du joueur
│   ├── quest/        # Système de quêtes
│   ├── ui/           # Interface utilisateur
│   ├── utils/        # Utilitaires
│   └── demo/         # Démonstrateurs
└── src/main/resources/
    ├── assets/       # Ressources graphiques et audio
    └── data/         # Données du jeu (JSON)
```

## 🛠️ Technologies

- **Java** : Langage principal
- **LWJGL** : Bibliothèque graphique légère pour Java
- **OpenGL** : API graphique
- **Maven** : Gestion des dépendances
- **JUnit** : Tests unitaires

## ⚡ Optimisations

Le projet intègre plusieurs optimisations pour garantir des performances optimales :

- **Cache intelligent** : Mise en cache des tuiles fréquemment utilisées (LRU)
- **Chargement asynchrone** : Ressources chargées en arrière-plan
- **Rendu sélectif** : Seules les tuiles visibles sont rendues
- **Gestion efficace de la mémoire** : Libération automatique des ressources

## 🚀 Installation

### Prérequis

- Java JDK 11 ou supérieur
- Maven 3.6 ou supérieur (optionnel)

### Installation

1. Clonez le dépôt
   ```
   git clone https://github.com/Alexandre-Ginisty/RyuukonPalace.git
   ```

2. Exécutez l'un des scripts de démo :
   - `run_simple_demo.bat` (version Swing, sans dépendances)
   - `run_demo.bat` (version LWJGL, nécessite les dépendances)

## 📋 Roadmap

- [x] Structure de base du projet
- [x] Système de rendu 2D
- [x] Conversion vers un style 2.5D
- [x] Système de créatures et de combat
- [x] Mécanisme de QTE pour la capture
- [x] Système d'évolution
- [x] Optimisation des performances
- [x] Système de cache pour les ressources
- [x] Démonstrateur fonctionnel
- [ ] Création des assets graphiques finaux
- [ ] Composition des musiques et effets sonores
- [ ] Équilibrage final du gameplay

## 🤝 Contribution

Vous souhaitez contribuer au projet ? Consultez notre [guide de contribution](CONTRIBUTING.md) pour plus d'informations.

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## 📚 Documentation

Toute la documentation détaillée se trouve dans le dossier `docs/` :

- [Guide complet](docs/GUIDE_COMPLET.md) - Guide principal avec toutes les informations essentielles
- [Lore résumé](docs/LORE_RESUME.md) - Version condensée de l'univers du jeu
- [Lore complet](docs/LORE_COMPLET.md) - Histoire complète et détaillée
- [Guide d'animation](docs/ANIMATION_GUIDE.md) - Instructions pour les animations
- [Style graphique](docs/STYLE_GRAPHIQUE.md) - Détails sur le style visuel 2.5D
- [Guide de la démo](docs/DEMO_GUIDE.md) - Instructions pour utiliser le démonstrateur
- [Liste des tâches](docs/TASKS.md) - Tâches restantes et roadmap

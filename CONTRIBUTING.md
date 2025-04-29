# Guide de contribution pour Ryuukon Palace

Bienvenue dans le guide de contribution pour le projet Ryuukon Palace ! Ce document vous aidera à comprendre comment contribuer efficacement au développement du jeu.

## Table des matières

1. [Introduction](#introduction)
2. [Structure du projet](#structure-du-projet)
3. [Configuration de l'environnement](#configuration-de-lenvironnement)
4. [Workflow de développement](#workflow-de-développement)
5. [Standards de code](#standards-de-code)
6. [Système de variants](#système-de-variants)
7. [Système de combat](#système-de-combat)
8. [Ressources et assets](#ressources-et-assets)
9. [Soumission de Pull Requests](#soumission-de-pull-requests)

## Introduction

Ryuukon Palace est un jeu RPG inspiré de Pokémon avec une ambiance sombre inspirée de Fear & Hunger. Le jeu met en scène deux factions principales : les Tacticiens qui utilisent des variants (créatures) pour combattre, et les Guerriers qui combattent directement.

## Structure du projet

Le projet est organisé selon la structure suivante :

```
RyuukonPalace/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ryuukonpalace/
│   │   │           ├── game/
│   │   │           │   ├── combat/       # Système de combat
│   │   │           │   ├── core/         # Fonctionnalités de base
│   │   │           │   ├── creatures/    # Gestion des variants
│   │   │           │   ├── faction/      # Système de factions
│   │   │           │   ├── items/        # Objets et équipements
│   │   │           │   ├── player/       # Gestion du joueur
│   │   │           │   ├── quest/        # Système de quêtes
│   │   │           │   ├── save/         # Sauvegarde/chargement
│   │   │           │   └── ui/           # Interfaces utilisateur
│   │   │           └── Main.java         # Point d'entrée
│   │   └── resources/
│   │       ├── data/                     # Fichiers JSON de données
│   │       ├── images/                   # Assets graphiques
│   │       ├── models/                   # Modèles 3D (FBX)
│   │       └── sounds/                   # Ressources audio
│   └── test/                             # Tests unitaires
├── docs/                                 # Documentation
├── ANIMATION_GUIDE.md                    # Guide d'animation
├── LORE_COMPLET.md                       # Lore du jeu
├── README.md                             # Présentation du projet
└── TASKS.md                              # Liste des tâches
```

## Configuration de l'environnement

### Prérequis

- Java JDK 11 ou supérieur
- Maven 3.6 ou supérieur
- IDE recommandé : IntelliJ IDEA ou Eclipse

### Installation

1. Clonez le dépôt :
   ```
   git clone https://github.com/Alexandre-Ginisty/RyuukonPalace.git
   ```

2. Importez le projet dans votre IDE en tant que projet Maven.

3. Installez les dépendances :
   ```
   mvn install
   ```

## Workflow de développement

1. **Branches** : Créez une branche pour chaque fonctionnalité ou correction.
   ```
   git checkout -b feature/nom-de-la-fonctionnalite
   ```

2. **Commits** : Utilisez des messages de commit clairs et descriptifs.
   ```
   git commit -m "COMB-8: Implémentation du système de combat avancé pour Tacticiens"
   ```

3. **Tests** : Assurez-vous que vos modifications passent tous les tests existants.

4. **Pull Requests** : Soumettez une PR pour intégrer vos modifications dans la branche principale.

## Standards de code

### Style de code

- Utilisez des noms de variables et méthodes explicites en camelCase
- Les noms de classes doivent être en PascalCase
- Les constantes doivent être en UPPER_SNAKE_CASE
- Commentez votre code, en particulier pour les algorithmes complexes
- Utilisez des annotations `@SuppressWarnings` uniquement lorsque nécessaire et avec un commentaire explicatif

### Documentation

- Documentez toutes les classes et méthodes publiques avec des commentaires Javadoc
- Utilisez `@param`, `@return` et `@throws` dans les Javadoc
- Mettez à jour la documentation lorsque vous modifiez le comportement d'une méthode

## Système de variants

Le système de variants est au cœur du gameplay. Voici les points essentiels à comprendre :

### Types de variants

Les variants sont classés selon différents types (Feu, Eau, Terre, Air, etc.) et catégories (Stratège, Furieux, Mystique, Serein, Chaotique, etc.).

### Évolution

Les variants peuvent évoluer jusqu'à 2 fois. L'évolution peut être déclenchée par niveau, par objet, ou par condition spéciale.

### Combat

Les variants ont des statistiques (HP, Attaque, Défense, Vitesse) et des capacités spéciales. Le système de combat est tour par tour, similaire à Pokémon.

## Système de combat

### Combat de Tacticiens

Les Tacticiens utilisent leurs variants pour combattre. Implémentez les fonctionnalités dans `TacticianCombatInterface.java`.

### Combat de Guerriers

Les Guerriers (PNJ uniquement) combattent directement avec des armes et armures. Ce système est géré dans le package `combat`.

## Ressources et assets

### Fichiers JSON

Les données du jeu sont stockées dans des fichiers JSON dans le dossier `resources/data/`. Respectez la structure existante lors de l'ajout de nouvelles données.

### Assets graphiques

Les assets graphiques sont stockés dans `resources/images/`. Utilisez le format PNG pour les textures 2D et FBX pour les modèles 3D.

## Soumission de Pull Requests

1. Assurez-vous que votre code respecte les standards du projet
2. Testez vos modifications
3. Créez une Pull Request avec une description claire
4. Référencez les issues ou tâches associées (ex: "Fixes #123" ou "Implements COMB-8")
5. Attendez la revue de code et répondez aux commentaires

---

Merci de contribuer au projet Ryuukon Palace ! Si vous avez des questions, n'hésitez pas à contacter l'équipe de développement.

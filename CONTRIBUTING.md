# 🤝 Guide de contribution - Ryuukon Palace

Bienvenue dans le guide de contribution pour le projet Ryuukon Palace ! Ce document vous aidera à comprendre comment contribuer efficacement au développement du jeu.

## 📑 Table des matières

1. [Structure du projet](#structure-du-projet)
2. [Configuration de l'environnement](#configuration-de-lenvironnement)
3. [Workflow de développement](#workflow-de-développement)
4. [Standards de code](#standards-de-code)
5. [Systèmes principaux](#systèmes-principaux)
6. [Ressources et assets](#ressources-et-assets)
7. [Soumission de Pull Requests](#soumission-de-pull-requests)

## 📂 Structure du projet

Le projet est organisé selon la structure suivante :

```
RyuukonPalace/
├── src/
│   ├── main/
│   │   ├── java/com/ryuukonpalace/game/
│   │   │   ├── core/         # Moteur du jeu et composants principaux
│   │   │   ├── world/        # Système de monde et génération
│   │   │   ├── creatures/    # Système de variants (créatures)
│   │   │   ├── battle/       # Système de combat
│   │   │   ├── player/       # Gestion du joueur
│   │   │   ├── quest/        # Système de quêtes
│   │   │   ├── ui/           # Interface utilisateur
│   │   │   ├── utils/        # Utilitaires
│   │   │   └── demo/         # Démonstrateurs
│   │   └── resources/
│   │       ├── assets/       # Ressources graphiques et audio
│   │       └── data/         # Données du jeu (JSON)
│   └── test/                 # Tests unitaires
├── docs/                     # Documentation
└── [Fichiers de documentation]
```

## ⚙️ Configuration de l'environnement

### Prérequis

- Java JDK 11 ou supérieur
- Maven 3.6 ou supérieur (recommandé)
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

4. Pour tester le démonstrateur simplifié :
   ```
   run_simple_demo.bat
   ```

## 🔄 Workflow de développement

1. **Branches** : Créez une branche pour chaque fonctionnalité ou correction.
   ```
   git checkout -b feature/nom-de-la-fonctionnalite
   ```

2. **Commits** : Utilisez des messages de commit clairs et descriptifs.
   ```
   git commit -m "FEAT: Implémentation du système de combat avancé"
   ```

3. **Tests** : Assurez-vous que vos modifications passent tous les tests existants.
   ```
   mvn test
   ```

4. **Pull Requests** : Soumettez une PR pour intégrer vos modifications dans la branche principale.

## 📝 Standards de code

### Style de code

- Utilisez des noms de variables et méthodes explicites en camelCase
- Les noms de classes doivent être en PascalCase
- Les constantes doivent être en UPPER_SNAKE_CASE
- Indentation : 4 espaces (pas de tabulations)
- Limite de 100 caractères par ligne

### Documentation

- Documentez toutes les classes et méthodes publiques avec des commentaires Javadoc
- Utilisez `@param`, `@return` et `@throws` dans les Javadoc
- Mettez à jour la documentation lorsque vous modifiez le comportement d'une méthode

### Exemple de documentation

```java
/**
 * Calcule les dégâts infligés par une attaque en fonction du type et des statistiques.
 *
 * @param attacker La créature qui attaque
 * @param defender La créature qui défend
 * @param move L'attaque utilisée
 * @return Le montant de dégâts calculé
 * @throws IllegalArgumentException Si l'attaque n'est pas compatible avec l'attaquant
 */
public int calculateDamage(Creature attacker, Creature defender, Move move) {
    // Implémentation
}
```

## 🧩 Systèmes principaux

### Système de variants (créatures)

Les variants sont classés selon différents types et catégories :

- **Types** : Feu, Eau, Terre, Air, etc.
- **Catégories** : Stratège, Furieux, Mystique, Serein, Chaotique, etc.

Chaque variant peut évoluer jusqu'à 2 fois, débloqué par niveau, objet, ou condition spéciale.

### Système de combat

- **Combat de Tacticiens** : Utilisation de variants pour combattre d'autres variants
- **Combat de Guerriers** : Combat direct avec armes et armures (PNJ uniquement)

### Système de factions

Le jeu comporte 5 factions principales avec des systèmes de réputation à plusieurs niveaux :
- Ordre des Tacticiens
- Fraternité des Guerriers
- Guilde des Marchands
- Gardiens de la Nature
- Cercle des Mystiques

## 🎨 Ressources et assets

### Fichiers JSON

Les données du jeu sont stockées dans des fichiers JSON dans le dossier `resources/data/`. Respectez la structure existante lors de l'ajout de nouvelles données.

### Assets graphiques

- Utilisez le format PNG pour les textures 2D
- Résolution recommandée : 32x32 pixels pour les tuiles de base
- Style visuel : 2.5D inspiré de Pokémon Noir/Blanc

## 📤 Soumission de Pull Requests

1. Assurez-vous que votre code respecte les standards du projet
2. Testez vos modifications
3. Créez une Pull Request avec une description claire
4. Référencez les issues ou tâches associées (ex: "Fixes #123")
5. Attendez la revue de code et répondez aux commentaires

---

Merci de contribuer au projet Ryuukon Palace ! Si vous avez des questions, n'hésitez pas à contacter l'équipe de développement.

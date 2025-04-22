# Ryuukon Palace

Un jeu RPG de style Pokémon où les joueurs peuvent capturer des créatures à l'aide de QTE (Quick Time Events). Les créatures peuvent évoluer, combattre et être collectionnées.

## Description

Ryuukon Palace est un jeu RPG pour un public adolescent/mature inspiré par les jeux Pokémon. Le jeu propose :

- Capture de créatures via des séquences de QTE (Quick Time Events)
- Système d'évolution des créatures basé sur les niveaux
- Combats tactiques entre créatures
- Exploration d'un monde ouvert
- Histoire immersive

## Structure du Projet

Le projet est organisé en plusieurs packages :

- `core` : Contient les classes principales du jeu et la gestion des états
- `creatures` : Définit les créatures, leurs types, capacités et évolutions
- `battle` : Système de combat
- `ui` : Interface utilisateur et rendu graphique
- `world` : Génération et gestion du monde de jeu
- `player` : Gestion du joueur, inventaire et progression
- `utils` : Classes utilitaires

## Technologies

- Java
- LWJGL (Lightweight Java Game Library)
- OpenGL pour le rendu graphique
- Maven pour la gestion des dépendances

## Installation

1. Cloner le dépôt
2. Installer Maven si ce n'est pas déjà fait
3. Exécuter `mvn clean install` pour compiler le projet
4. Exécuter `mvn exec:java -Dexec.mainClass="com.ryuukonpalace.game.core.RyuukonPalace"` pour lancer le jeu

## Roadmap

- [x] Structure de base du projet
- [ ] Système de rendu 2D
- [ ] Système de créatures et de combat
- [ ] Mécanisme de QTE pour la capture
- [ ] Système d'évolution
- [ ] Génération de monde
- [ ] Interface utilisateur complète
- [ ] Système de sauvegarde/chargement
- [ ] Audio et effets sonores
- [ ] Histoire et quêtes

## Contribution

Ce projet est actuellement développé en solo, mais structuré comme un projet d'équipe pour faciliter l'organisation et la progression.

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

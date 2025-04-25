# Ryuukon Palace

Un jeu RPG de style Pokémon où les joueurs peuvent capturer des créatures à l'aide de QTE (Quick Time Events). Les créatures peuvent évoluer, combattre et être collectionnées.

## Description

Ryuukon Palace est un jeu RPG pour un public adolescent/mature inspiré par les jeux Pokémon. Le jeu propose :

- Capture de créatures via des séquences de QTE (Quick Time Events)
- Système d'évolution des créatures basé sur les niveaux
- Combats tactiques entre créatures
- Exploration d'un monde ouvert
- Histoire immersive
- Système de factions avec réputation et récompenses

## Lore

Depuis des siècles, l'humanité s'est divisée en deux factions diamétralement opposées. D'un côté se trouvent les **Tacticiens**, ceux qui exploitent et éduquent les Variants pour combattre d'autres Variants et accomplir leurs tâches quotidiennes. De l'autre côté se dressent les **Guerriers**, farouchement opposés à l'utilisation de Variants comme outils, préférant s'engager dans des duels directs contre ces créatures. Cette fracture idéologique engendre un conflit permanent dans le monde de Ryuukon Palace, influençant la politique, l'économie et la vie de tous les jours.

### Système de Factions

Le monde de Ryuukon Palace est structuré autour de plusieurs factions majeures qui s'affrontent pour le contrôle des ressources et l'influence politique. Chaque faction possède :

- Une philosophie et des objectifs uniques
- Des centres d'opération et des territoires
- Des leaders charismatiques
- Un système de réputation à plusieurs niveaux
- Des récompenses exclusives pour les membres loyaux

#### Factions Principales

1. **L'Ordre des Tacticiens** - Basé à Lumina et au Temple of Dawn
   - *Philosophie* : Maîtriser et utiliser les variants pour le progrès de l'humanité
   - *Leaders* : Maître Orion, Lady Elara, Elder Mira
   - *Spécialité* : Capture avancée, éducation des variants, recherche sur les Signes de Pouvoir

2. **La Fraternité des Guerriers** - Basée à Stormpeak et Fort Eisenberg
   - *Philosophie* : Combattre directement les variants, rejeter leur utilisation comme outils
   - *Leaders* : Général Thorne, Sir Rainer Wolfheart, Eamon
   - *Spécialité* : Combat direct, fabrication d'armes anti-variants, arts martiaux

3. **La Guilde des Marchands** - Basée à Darkhaven
   - *Philosophie* : Profit et commerce, neutralité dans le conflit Tacticiens/Guerriers
   - *Leaders* : Baron Silvius, Dame Ophelia
   - *Spécialité* : Commerce de variants rares, d'artefacts et d'équipement

4. **Les Gardiens de la Nature** - Basés à Whisperwind Valley
   - *Philosophie* : Coexistence harmonieuse avec les variants, protection de l'environnement
   - *Leaders* : Sage Thalia, Ranger Eldon
   - *Spécialité* : Communication avec les variants, médecine naturelle, connaissance des habitats

5. **Le Cercle des Mystiques** - Basé dans les Ancient Ruins
   - *Philosophie* : Étude des origines des variants et des secrets anciens
   - *Leaders* : Archimage Vex, Prophétesse Lyra
   - *Spécialité* : Magie ancienne, artefacts légendaires, prophéties

#### Système de Réputation

Chaque faction possède un système de réputation à plusieurs niveaux, allant de l'hostilité à la vénération :

- **Hostile** : La faction vous considère comme un ennemi, ses membres vous attaqueront à vue
- **Méfiant** : On vous tolère, mais avec suspicion et restrictions
- **Neutre** : Position par défaut, ni ami ni ennemi
- **Amical** : Accès à certains services et récompenses de base
- **Honoré** : Accès à des missions spéciales et des récompenses rares
- **Exalté** : Statut le plus élevé, accès à des équipements légendaires et des connaissances secrètes

#### Récompenses de Faction

En gagnant de la réputation auprès d'une faction, le joueur peut débloquer :

- Des variants exclusifs liés à la philosophie de la faction
- Des tenues et équipements spéciaux
- Des capacités et techniques uniques
- L'accès à des zones restreintes
- Des quêtes spéciales à haute récompense
- Des titres honorifiques conférant des bonus passifs

#### Impact sur le Gameplay

Les choix du joueur concernant les factions influencent profondément l'expérience de jeu :

- L'histoire principale se déroule différemment selon vos allégeances
- Certaines zones sont accessibles ou hostiles selon votre réputation
- Les PNJ réagissent différemment à votre présence
- Les prix des marchands varient selon votre statut
- Les variants disponibles et leurs comportements changent
- Les mécaniques de combat et de capture sont modifiées par les techniques de faction

## Structure du Projet

Le projet est organisé en plusieurs packages :

- `core` : Contient les classes principales du jeu et la gestion des états
- `creatures` : Définit les créatures, leurs types, capacités et évolutions
- `battle` : Système de combat
- `ui` : Interface utilisateur et rendu graphique
- `world` : Génération et gestion du monde de jeu
- `player` : Gestion du joueur, inventaire et progression
- `utils` : Classes utilitaires
- `faction` : Système de factions, réputation et récompenses

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

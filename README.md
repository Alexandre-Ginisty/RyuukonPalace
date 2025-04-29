# Ryuukon Palace

Un jeu RPG de style Pokémon où les joueurs peuvent capturer des créatures à l'aide de QTE (Quick Time Events). Les créatures peuvent évoluer, combattre et être collectionnées.

## Description

Ryuukon Palace est un jeu RPG pour un public adolescent/mature inspiré par les jeux Pokémon. Le jeu propose :

- Capture de créatures via des séquences de QTE (Quick Time Events)
- Système d'évolution des créatures basé sur les niveaux
- Combats tactiques entre créatures
- Exploration d'un monde ouvert avec un style visuel 2.5D inspiré de Pokémon Noir/Blanc
- Histoire immersive et mature
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

## Documentation Technique

Cette section détaille l'architecture technique du jeu Ryuukon Palace, organisée par packages et composants.

### Architecture Globale

| Package | Description | Interaction avec d'autres packages |
|---------|-------------|-----------------------------------|
| `core` | Contient les classes principales du jeu, le moteur de rendu et la gestion des états | Interagit avec tous les autres packages |
| `creatures` | Définit les créatures (variants), leurs types, capacités et évolutions | Interagit avec `battle`, `player`, `quest` |
| `battle` | Système de combat entre créatures | Interagit avec `creatures`, `player`, `ui` |
| `capture` | Mécanismes de capture des créatures via QTE | Interagit avec `creatures`, `qte`, `player` |
| `combat` | Système de combat direct (pour les Guerriers) | Interagit avec `creatures`, `player`, `ui` |
| `player` | Gestion du joueur, inventaire et progression | Interagit avec la plupart des autres packages |
| `ui` | Interface utilisateur et rendu graphique | Interagit avec tous les autres packages pour l'affichage |
| `world` | Génération et gestion du monde de jeu | Interagit avec `player`, `creatures`, `faction` |
| `quest` | Système de quêtes et objectifs | Interagit avec `player`, `world`, `faction` |
| `faction` | Système de factions, réputation et récompenses | Interagit avec `player`, `quest`, `world` |
| `save` | Système de sauvegarde et chargement | Interagit avec la plupart des autres packages |
| `utils` | Classes utilitaires diverses | Utilisé par tous les autres packages |
| `dialogue` | Système de dialogues avec les PNJ | Interagit avec `player`, `quest`, `faction` |
| `items` | Définition des objets du jeu | Interagit avec `player`, `creatures`, `quest` |
| `qte` | Système de Quick Time Events | Interagit avec `capture`, `ui` |

### Package Core

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `RyuukonPalace` | Classe principale du jeu | Point d'entrée, initialisation des systèmes |
| `GameState` | Gestion de l'état global du jeu | Maintient l'état actuel du jeu, transitions entre états |
| `Renderer` | Moteur de rendu graphique | Rendu des sprites, textes, et interfaces |
| `InputManager` | Gestionnaire d'entrées utilisateur | Capture et traitement des entrées clavier/souris |
| `ResourceManager` | Gestionnaire de ressources | Chargement des textures, sons, et fichiers de données |
| `AudioManager` | Gestionnaire du système audio | Lecture des effets sonores et musiques |
| `GameLoop` | Boucle principale du jeu | Mise à jour et rendu à fréquence fixe |
| `PhysicsEngine` | Moteur physique simplifié | Gestion des collisions et mouvements |

### Package Creatures

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `Creature` | Classe de base pour toutes les créatures | Attributs et comportements communs |
| `CreatureType` | Énumération des types de créatures | Définit les types spécifiques à Ryuukon Palace (Stratège, Furieux, Mystique, Serein, Chaotique, Ombreux, Lumineux, Terrestre, Aérien, Aquatique, Spirituel, Ancestral) |
| `CreatureFactory` | Fabrique de créatures | Création de créatures à partir de données |
| `Ability` | Capacités des créatures | Définition des attaques et effets |
| `Evolution` | Système d'évolution | Gestion des conditions et processus d'évolution |
| `CreatureStats` | Statistiques des créatures | Gestion des stats (Vitalité, Puissance, etc.) |
| `LevelSystem` | Système de niveau | Progression des créatures par expérience |
| `CreatureStorage` | Stockage des créatures | Gestion des créatures capturées |
| `CreatureAI` | Intelligence artificielle | Comportement des créatures sauvages |

### Package Battle

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `BattleSystem` | Système de combat | Gestion du déroulement des combats |
| `BattleState` | État d'un combat | Maintient l'état actuel d'un combat |
| `BattleAction` | Actions possibles en combat | Définit les actions (Attaquer, Fuir, etc.) |
| `BattleCalculator` | Calculateur de dégâts | Calcul des dégâts selon types et stats |
| `BattleRewards` | Récompenses de combat | Gestion de l'expérience et objets gagnés |
| `BattleAnimation` | Animations de combat | Effets visuels pendant les combats |

### Package Player

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `Player` | Classe représentant le joueur | Attributs et actions du joueur |
| `Inventory` | Inventaire du joueur | Gestion des objets possédés |
| `PlayerStats` | Statistiques du joueur | Progression et attributs du joueur |
| `PlayerController` | Contrôleur du joueur | Gestion des mouvements et interactions |
| `PlayerProgress` | Progression du joueur | Suivi des accomplissements |
| `PlayerCustomization` | Personnalisation | Apparence et options du joueur |

### Package UI

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `UIManager` | Gestionnaire d'interface | Coordination de toutes les interfaces |
| `MainMenu` | Menu principal | Interface d'accueil du jeu |
| `BattleInterface` | Interface de combat | Affichage des combats |
| `InventoryInterface` | Interface d'inventaire | Gestion des objets |
| `CreatureStorageInterface` | Interface de stockage | Gestion des créatures capturées |
| `DialogueInterface` | Interface de dialogue | Affichage des conversations |
| `QuestJournalInterface` | Journal de quêtes | Suivi des quêtes actives |
| `MapInterface` | Interface de carte | Navigation dans le monde |
| `FactionInterface` | Interface de faction | Gestion des relations de faction |
| `ShopInterface` | Interface de boutique | Achat et vente d'objets |

### Package Quest

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `Quest` | Classe représentant une quête | Structure et progression d'une quête |
| `QuestManager` | Gestionnaire de quêtes | Suivi de toutes les quêtes |
| `QuestObjective` | Objectif de quête | Conditions de réussite d'une quête |
| `QuestReward` | Récompense de quête | Objets et avantages gagnés |
| `QuestStatus` | État d'une quête | Énumération des états possibles |
| `QuestType` | Type de quête | Catégorisation des quêtes |
| `QuestCallback` | Interface de callback | Notifications d'événements de quête |

### Package Save

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `SaveManager` | Gestionnaire de sauvegarde | Sauvegarde et chargement des données |
| `SaveData` | Données de sauvegarde | Structure des données sauvegardées |
| `SaveMetadata` | Métadonnées de sauvegarde | Informations sur les sauvegardes |
| `SaveSerializer` | Sérialiseur de données | Conversion des objets en format sauvegardable |
| `AutoSave` | Sauvegarde automatique | Sauvegarde périodique des données |

### Package World

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `WorldManager` | Gestionnaire de monde | Coordination de tous les éléments du monde |
| `WorldGenerator` | Générateur de monde | Création procédurale du monde |
| `Region` | Région du monde | Zone géographique avec propriétés |
| `Location` | Lieu spécifique | Point d'intérêt dans le monde |
| `Weather` | Système météorologique | Gestion des conditions météo |
| `TimeSystem` | Système temporel | Cycle jour/nuit et calendrier |
| `NPCManager` | Gestionnaire de PNJ | Gestion des personnages non-joueurs |
| `SpawnSystem` | Système d'apparition | Apparition des créatures dans le monde |

### Package Faction

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `Faction` | Classe représentant une faction | Attributs et comportements d'une faction |
| `FactionManager` | Gestionnaire de factions | Coordination de toutes les factions |
| `Reputation` | Système de réputation | Gestion de la réputation du joueur |
| `FactionQuest` | Quêtes de faction | Quêtes spécifiques aux factions |
| `FactionReward` | Récompenses de faction | Objets et avantages spécifiques |
| `FactionRelation` | Relations entre factions | Définit les relations entre factions |

### Package Items

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `Item` | Classe de base pour tous les objets | Attributs communs des objets |
| `ItemFactory` | Fabrique d'objets | Création d'objets à partir de données |
| `Consumable` | Objets consommables | Potions, nourriture, etc. |
| `Equipment` | Équipement | Objets équipables par le joueur |
| `KeyItem` | Objets clés | Objets importants pour la progression |
| `CaptureItem` | Objets de capture | Objets pour capturer des créatures |
| `ItemEffect` | Effets des objets | Actions déclenchées par les objets |

### Package Dialogue

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `DialogueManager` | Gestionnaire de dialogues | Coordination des conversations |
| `DialogueTree` | Arbre de dialogue | Structure des conversations |
| `DialogueNode` | Nœud de dialogue | Réplique individuelle |
| `DialogueOption` | Option de dialogue | Choix proposés au joueur |
| `DialogueCondition` | Condition de dialogue | Conditions d'accès aux dialogues |
| `DialogueAction` | Action de dialogue | Effets déclenchés par les dialogues |

### Package QTE

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `QTESystem` | Système de Quick Time Events | Gestion des séquences de QTE |
| `QTESequence` | Séquence de QTE | Définition d'une série d'actions |
| `QTEAction` | Action de QTE | Action individuelle à réaliser |
| `QTEResult` | Résultat de QTE | Succès ou échec d'une séquence |
| `QTEDifficulty` | Difficulté de QTE | Paramètres de difficulté |

### Package Utils

| Classe | Description | Responsabilités |
|--------|-------------|----------------|
| `MathUtils` | Utilitaires mathématiques | Fonctions mathématiques diverses |
| `RandomGenerator` | Générateur aléatoire | Génération de nombres aléatoires |
| `FileUtils` | Utilitaires de fichiers | Opérations sur les fichiers |
| `JsonParser` | Analyseur JSON | Lecture et écriture de fichiers JSON |
| `Logger` | Système de journalisation | Enregistrement des événements et erreurs |
| `ConfigManager` | Gestionnaire de configuration | Paramètres du jeu |
| `StringUtils` | Utilitaires de chaînes | Manipulation de texte |

## Technologies

- Java
- LWJGL (Lightweight Java Game Library)
- OpenGL pour le rendu graphique
- Système de rendu 2.5D par couches pour un effet de profondeur
- Système de cache optimisé pour les tuiles et ressources graphiques
- Chargement asynchrone des ressources pour de meilleures performances
- Maven pour la gestion des dépendances
- JSON pour le stockage des données
- JUnit pour les tests unitaires
- Git pour le contrôle de version

## Installation

1. Cloner le dépôt
2. Installer Maven si ce n'est pas déjà fait
3. Exécuter `mvn clean install` pour compiler le projet
4. Exécuter `mvn exec:java -Dexec.mainClass="com.ryuukonpalace.game.core.RyuukonPalace"` pour lancer le jeu

## Style Graphique 2.5D

Ryuukon Palace utilise un style graphique 2.5D inspiré de Pokémon Noir/Blanc, caractérisé par :

- **Rendu par couches** : Plusieurs couches de profondeur pour créer l'illusion de 3D
- **Bâtiments avec perspective** : Structures dessinées avec un effet de profondeur
- **Variations de hauteur** : Tuiles avec différentes hauteurs pour simuler le relief
- **Décalage vertical** : Objets positionnés avec un décalage pour simuler la perspective
- **Éclairage dynamique** : Effets d'ombre et de lumière adaptés au style 2.5D
- **Animations 2D** : Spritesheets pour les personnages et créatures
- **Effets météorologiques** : Pluie, neige et autres effets adaptés au style visuel
- **Cache optimisé** : Système de mise en cache des tuiles fréquemment utilisées
- **Chargement asynchrone** : Chargement des ressources en arrière-plan pour une meilleure fluidité

Cette approche permet de créer un monde visuellement riche sans nécessiter de modélisation 3D complexe, tout en conservant l'esthétique distinctive de Pokémon Noir/Blanc.

## Performances et Optimisations

Le projet intègre plusieurs optimisations pour garantir des performances optimales :

- **Système de cache intelligent** : Les tuiles fréquemment utilisées sont mises en cache avec une politique LRU (Least Recently Used)
- **Chargement asynchrone** : Les ressources sont chargées en arrière-plan via un pool de threads dédié
- **Rendu sélectif** : Seules les tuiles visibles dans la vue actuelle sont rendues
- **Gestion efficace de la mémoire** : Libération automatique des ressources non utilisées
- **Statistiques de performance** : Suivi des taux de succès du cache pour l'optimisation continue

## Roadmap

- [x] Structure de base du projet
- [x] Système de rendu 2D
- [x] Conversion vers un style 2.5D (Pokémon Noir/Blanc)
- [x] Système de créatures et de combat
- [x] Mécanisme de QTE pour la capture
- [x] Système d'évolution
- [x] Génération de monde
- [x] Interface utilisateur de base
- [x] Interface utilisateur complète
- [x] Système de sauvegarde/chargement
- [x] Optimisation des performances de rendu
- [x] Système de cache pour les ressources graphiques
- [x] Tests unitaires pour les composants principaux
- [ ] Création des assets graphiques finaux
- [ ] Composition des musiques et effets sonores
- [ ] Équilibrage final du gameplay
- [ ] Version bêta jouable

## Contribution

Ce projet est actuellement développé en solo, mais structuré comme un projet d'équipe pour faciliter l'organisation et la progression.

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

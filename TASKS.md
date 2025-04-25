# Tâches pour le projet Ryuukon Palace

Ce document liste les tâches à accomplir pour le développement du jeu, organisées par catégories et priorités.

## Système de base (Priorité Haute)

- [x] **CORE-1**: Implémenter le système de gestion des ressources (images, sons)
- [x] **CORE-2**: Mettre en place le système de rendu 2D
- [x] **CORE-3**: Développer le système de gestion des entrées (clavier, souris)
- [x] **CORE-4**: Créer le système de collision
- [x] **CORE-5**: Implémenter le système de caméra et de suivi du joueur
- [x] **CORE-6**: Développer le système de gestion des données (chargement/sauvegarde JSON)
- [x] **CORE-7**: Créer le système de chargement et rendu des modèles 3D (FBX)

## Créatures (Priorité Haute)

- [x] **CREA-1**: Concevoir 10 créatures de base avec leurs caractéristiques
- [x] **CREA-2**: Implémenter le système d'évolution des créatures
- [x] **CREA-3**: Créer le système de stockage/inventaire des créatures
- [x] **CREA-4**: Développer l'IA des créatures sauvages
- [-] **CREA-5**: Concevoir les animations des créatures
- [x] **CREA-6**: Structurer les données des variants selon les types définis dans le lore
- [x] **CREA-7**: Implémenter le système de résistances et faiblesses entre types de variants
- [x] **CREA-8**: Harmoniser les types de variants dans le code et la documentation (Stratège, Furieux, etc.)

## Système de combat (Priorité Moyenne)

- [x] **COMB-1**: Développer le système de tour par tour
- [x] **COMB-2**: Implémenter les effets de statut 
- [x] **COMB-3**: Créer l'interface de combat
- [x] **COMB-4**: Développer le système d'expérience et de niveau
- [x] **COMB-5**: Implémenter les récompenses de combat
- [x] **COMB-6**: Intégrer le système de capture dans le combat
- [x] **COMB-7**: Développer le système de combat style Pokémon avec écran dédié

## Système de capture (Priorité Haute)

- [x] **CAPT-1**: Développer le système de QTE (Quick Time Events)
- [x] **CAPT-2**: Implémenter les différents niveaux de difficulté de capture
- [x] **CAPT-3**: Créer les animations de capture
- [x] **CAPT-4**: Développer le système de probabilité de capture basé sur l'état de la créature
- [x] **CAPT-5**: Implémenter les objets spéciaux de capture (pierres de capture)

## Interface utilisateur (Priorité Moyenne)

- [x] **UI-1**: Concevoir et implémenter le menu principal
- [x] **UI-2**: Créer l'interface d'inventaire
- [x] **UI-3**: Développer l'écran de statut des créatures
- [x] **UI-4**: Implémenter le système de dialogue
- [x] **UI-5**: Créer la carte du monde et la mini-carte
- [x] **UI-6**: Développer l'interface de faction (Tacticiens vs Guerriers)
- [x] **UI-7**: Implémenter le journal de quêtes et les notifications

## Monde de jeu (Priorité Moyenne)

- [x] **WORLD-1**: Concevoir la carte du monde
- [x] **WORLD-2**: Implémenter le système de génération des zones
- [x] **WORLD-3**: Créer les PNJ (Personnages Non Joueurs)
- [x] **WORLD-4**: Développer le système jour/nuit et météo
- [x] **WORLD-5**: Implémenter les événements aléatoires

## Système de sauvegarde (Priorité Basse)

- [x] **SAVE-1**: Développer le système de sauvegarde/chargement
- [x] **SAVE-2**: Implémenter la gestion de plusieurs sauvegardes
- [x] **SAVE-3**: Créer le système d'auto-sauvegarde
- [x] **SAVE-4**: Développer la validation et la récupération des sauvegardes corrompues

## Audio (Priorité Basse)

- [ ] **AUDIO-1**: Implémenter le système de gestion audio
- [ ] **AUDIO-2**: Créer/acquérir les effets sonores de base
- [ ] **AUDIO-3**: Composer/acquérir les musiques de fond
- [ ] **AUDIO-4**: Développer le système de mixage audio dynamique

## Histoire et quêtes (Priorité Basse)

- [x] **QUEST-0**: Définir le lore complet du monde et des factions
- [x] **QUEST-1**: Développer le système de quêtes
- [x] **QUEST-1.1**: Intégrer le système de quêtes avec le système de dialogue
- [x] **QUEST-1.2**: Implémenter les conditions environnementales pour les quêtes (jour/nuit, météo)
- [x] **QUEST-1.3**: Implémenter le chargement des quêtes depuis les fichiers JSON
- [x] **QUEST-1.4**: Implémenter la sauvegarde des quêtes dans les fichiers JSON
- [x] **QUEST-1.5**: Corriger les erreurs dans le système de quêtes (QuestStatus, isActive, etc.)
- [x] **QUEST-6**: Créer des quêtes spécifiques aux factions (Tacticiens/Guerriers)
- [x] **QUEST-2**: Écrire l'histoire principale
- [x] **QUEST-3**: Créer les quêtes secondaires
- [x] **QUEST-4**: Implémenter le système de progression de l'histoire
- [x] **QUEST-5**: Développer le système de choix et conséquences
- [x] **QUEST-7**: Enrichir le lore avec des clans et factions secrètes
- [x] **QUEST-8**: Développer des quêtes matures avec des thèmes adultes
- [x] **QUEST-9**: Créer un système de signes mystiques avec différentes catégories
- [x] **QUEST-10**: Implémenter des quêtes avec des choix moraux et des conséquences importantes

## Documentation (Priorité Moyenne)

- [x] **DOC-1**: Créer une documentation technique complète du projet
- [x] **DOC-2**: Documenter les types de variants et leurs interactions
- [x] **DOC-3**: Mettre à jour le README avec la structure du projet
- [ ] **DOC-4**: Créer un guide de contribution pour les développeurs
- [ ] **DOC-5**: Documenter l'API du jeu pour les extensions futures

## Tests et équilibrage (Priorité Moyenne)

- [ ] **TEST-1**: Mettre en place les tests unitaires
- [ ] **TEST-2**: Effectuer des tests d'équilibrage du gameplay
- [ ] **TEST-3**: Optimiser les performances
- [x] **TEST-4**: Corriger les bugs identifiés
- [x] **TEST-5**: Corriger les erreurs de typage et les avertissements dans le code
- [ ] **TEST-6**: Réaliser des tests utilisateurs

## Design et assets graphiques (Priorité Moyenne)

- [-] **DESIGN-1**: Créer les modèles 3D des variants (format .fbx)
- [ ] **DESIGN-2**: Concevoir les assets d'environnement
- [ ] **DESIGN-3**: Développer l'interface utilisateur avec style 2.5D
- [ ] **DESIGN-4**: Créer les effets visuels (particules, transitions)
- [x] **DESIGN-5**: Implémenter le style visuel sombre inspiré de Fear & Hunger
- [-] **DESIGN-6**: Créer les animations des variants et personnages (format .fbx)

## Prochaines tâches prioritaires

1. **DOC-4**: Créer un guide de contribution pour les développeurs
2. **DESIGN-2**: Concevoir les assets d'environnement pour les nouveaux lieux (Shadow Caverns, Laboratoire Secret)
3. **DESIGN-3**: Développer l'interface utilisateur pour le système de signes mystiques
4. **AUDIO-1**: Implémenter le système de gestion audio
5. **TEST-2**: Effectuer des tests d'équilibrage du gameplay pour les nouvelles quêtes et factions
6. **TEST-3**: Optimiser les performances du jeu, notamment pour le chargement des ressources
7. **QUEST-11**: Développer des quêtes dynamiques qui s'adaptent aux actions du joueur
8. **UI-8**: Améliorer l'interface utilisateur pour les combats entre Tacticiens et variants

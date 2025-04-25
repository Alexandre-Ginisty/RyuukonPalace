# Guide d'organisation des animations et modèles 3D

## Structure des dossiers

J'ai créé la structure suivante pour organiser vos modèles FBX :

```
src/main/resources/
├── images/
│   ├── characters/
│   │   └── hero/          # Modèles et animations du personnage principal
│   ├── npcs/              # Contient déjà vos fichiers Kael_tpose.fbx et Start Walking kael.fbx
│   │   └── animations/    # Pour les animations génériques des PNJ
│   └── variants/
│       └── animations/    # Pour les animations génériques des variants
└── data/
    └── animations_config.json  # Configuration des modèles et animations
```

## Fichier de configuration

J'ai créé un fichier `animations_config.json` qui sert de registre pour tous vos modèles et animations. Il est structuré comme suit :

```json
{
  "characters": {
    "hero": {
      "model": "hero.fbx",
      "animations": {
        "idle": "idle.fbx",
        "walk": "walk.fbx",
        "attack": "attack.fbx"
      }
    }
  },
  "npcs": {
    "kael": {
      "model": "Kael_tpose.fbx",
      "animations": {
        "idle": "Kael_tpose.fbx",
        "walk": "Start Walking kael.fbx"
      }
    },
    // Autres PNJ...
  },
  "variants": {
    // Types de variants...
  }
}
```

## Utilisation des modèles FBX existants

J'ai détecté que vous avez déjà deux fichiers FBX pour Kael :
- `Kael_tpose.fbx` : Modèle en T-pose (peut servir de modèle de base et d'animation idle)
- `Start Walking kael.fbx` : Animation de marche

Ces fichiers sont déjà référencés dans le fichier de configuration.

## Classes Java pour le chargement et le rendu

J'ai créé deux classes pour gérer vos modèles FBX :

1. `ModelLoader.java` : Charge les modèles et animations depuis les fichiers FBX
2. `FbxRenderer.java` : Gère le rendu des modèles 3D dans votre jeu 2D

## Comment ajouter de nouveaux modèles et animations

1. Placez vos fichiers FBX dans les dossiers appropriés :
   - Personnage principal : `images/characters/hero/`
   - PNJ : `images/npcs/[nom_du_pnj]/`
   - Variants : `images/variants/[type_de_variant]/`

2. Mettez à jour le fichier `animations_config.json` pour référencer vos nouveaux fichiers

3. Dans votre code, utilisez le `FbxRenderer` pour afficher vos modèles :
   ```java
   FbxRenderer renderer = FbxRenderer.getInstance();
   renderer.renderModel("npcs", "kael", "walk", x, y, scale, rotation);
   ```

## Animations requises pour chaque personnage

Pour un jeu complet, chaque personnage devrait avoir au minimum ces animations :

- `idle` : Position neutre/attente
- `walk` : Marche
- `attack` : Attaque
- `hurt` : Réaction aux dégâts
- `die` : Mort/disparition

## Prochaines étapes

1. Complétez votre collection de modèles FBX pour les personnages principaux
2. Ajoutez les animations manquantes
3. Mettez à jour le fichier de configuration à chaque ajout
4. Utilisez le `FbxRenderer` dans votre code de jeu pour afficher les modèles

---

**Note** : Les classes Java fournies sont des squelettes à compléter avec la bibliothèque de chargement FBX de votre choix (jMonkeyEngine, LWJGL avec assimp, etc.).




Utiliser makehuman community pour créer des modèles 3D
faire les animations avec mixamo
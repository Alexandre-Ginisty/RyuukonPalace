package com.ryuukonpalace.game.player;

import com.ryuukonpalace.game.core.Camera;
import com.ryuukonpalace.game.world.GameObject;
import com.ryuukonpalace.game.core.InputManager;
import com.ryuukonpalace.game.core.Renderer;
import com.ryuukonpalace.game.core.physics.RectangleCollider;
import com.ryuukonpalace.game.creatures.Creature;
import com.ryuukonpalace.game.utils.ResourceManager;
import com.ryuukonpalace.game.world.SpawnZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Représente le joueur dans le jeu.
 * Gère le déplacement, les collisions et l'interaction avec les créatures.
 */
public class Player extends GameObject {
    
    // Vitesse de déplacement
    private float moveSpeed;
    
    // Direction actuelle
    private Direction direction;
    
    // État de mouvement
    private boolean isMoving;
    
    // Compteur de pas
    private float stepCounter;
    
    // Distance entre deux pas
    private final float stepDistance = 0.5f;
    
    // Inventaire des créatures capturées
    private List<Creature> capturedCreatures;
    
    // Inventaire des objets
    private Inventory inventory;
    
    // Créature actuellement en combat
    private Creature currentBattleCreature;
    
    // Zone d'apparition actuelle
    private SpawnZone currentSpawnZone;
    
    // ID de la texture du joueur
    private int textureId;
    
    // Gestionnaire d'entrées
    private InputManager inputManager;
    
    // Renderer
    private Renderer renderer;
    
    // Callback pour le démarrage d'un combat
    private CombatStartCallback combatStartCallback;
    
    // Monnaie du joueur (crystaux)
    private int crystals;
    
    // Nom du joueur
    private String name;
    
    // Niveau du joueur
    private int level;
    
    // Expérience du joueur
    private int experience;
    
    // Faction du joueur
    private Faction faction;
    
    // Journal du joueur (entrées d'événements importants)
    private List<String> journal;
    
    // Nombre maximum d'entrées dans le journal
    private static final int MAX_JOURNAL_ENTRIES = 100;
    
    // Zones découvertes par le joueur
    private List<String> discoveredAreas;
    
    // Réputations auprès des factions
    private Map<String, Integer> factionReputations;
    
    /**
     * Singleton instance
     */
    private static Player instance;
    
    /**
     * Constructeur pour le joueur
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public Player(float x, float y) {
        super(x, y, 32, 48);
        this.moveSpeed = 150.0f; // Pixels par seconde
        this.direction = Direction.DOWN;
        this.isMoving = false;
        this.stepCounter = 0.0f;
        this.capturedCreatures = new ArrayList<>();
        this.inventory = new Inventory();
        this.currentBattleCreature = null;
        this.currentSpawnZone = null;
        this.crystals = 100; // Le joueur commence avec 100 crystaux
        this.name = "Tacticien"; // Nom par défaut
        this.level = 1; // Niveau initial
        this.experience = 0; // Expérience initiale
        this.faction = Faction.NEUTRE; // Faction initiale
        this.journal = new LinkedList<>(); // Initialisation du journal
        this.discoveredAreas = new ArrayList<>(); // Initialisation des zones découvertes
        this.factionReputations = new HashMap<>(); // Initialisation des réputations
        
        // Créer un collider pour le joueur
        this.collider = new RectangleCollider(x, y, 32, 48);
        
        // Obtenir les instances des gestionnaires
        this.inputManager = InputManager.getInstance();
        this.renderer = Renderer.getInstance();
        
        // Charger la texture du joueur
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.textureId = resourceManager.getTextureId("player");
    }
    
    /**
     * Constructeur par défaut pour le joueur
     * Utilisé principalement pour la désérialisation
     */
    public Player() {
        super(0, 0, 32, 48);
        this.moveSpeed = 150.0f;
        this.direction = Direction.DOWN;
        this.isMoving = false;
        this.stepCounter = 0.0f;
        this.capturedCreatures = new ArrayList<>();
        this.inventory = new Inventory();
        this.currentBattleCreature = null;
        this.currentSpawnZone = null;
        this.crystals = 100;
        this.name = "Tacticien";
        this.level = 1;
        this.experience = 0;
        this.faction = Faction.NEUTRE;
        this.journal = new LinkedList<>();
        this.discoveredAreas = new ArrayList<>();
        this.factionReputations = new HashMap<>();
        
        // Créer un collider pour le joueur
        this.collider = new RectangleCollider(0, 0, 32, 48);
        
        // Obtenir les instances des gestionnaires
        this.inputManager = InputManager.getInstance();
        this.renderer = Renderer.getInstance();
        
        // Charger la texture du joueur
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.textureId = resourceManager.getTextureId("player");
    }
    
    /**
     * Obtenir l'instance unique du joueur
     * 
     * @return L'instance du joueur
     */
    public static Player getInstance() {
        if (instance == null) {
            instance = new Player(0, 0); // Position par défaut
        }
        return instance;
    }
    
    @Override
    public void update(float deltaTime) {
        // Ne pas mettre à jour si en combat
        if (currentBattleCreature != null) {
            return;
        }
        
        // Position précédente pour détecter les mouvements
        float oldX = x;
        float oldY = y;
        
        // Gérer les entrées de déplacement
        handleMovementInput(deltaTime);
        
        // Mettre à jour le compteur de pas si le joueur se déplace
        if (isMoving) {
            float distanceMoved = (float) Math.sqrt(Math.pow(x - oldX, 2) + Math.pow(y - oldY, 2));
            stepCounter += distanceMoved;
            
            // Vérifier si un pas complet a été effectué
            if (stepCounter >= stepDistance) {
                stepCounter -= stepDistance;
                onStepTaken();
            }
        }
        
        // Mettre à jour la position du collider
        if (collider != null) {
            collider.setPosition(x, y);
        }
        
        // Faire suivre la caméra
        Camera.getInstance().follow(this);
    }
    
    @Override
    public void render() {
        // Dessiner le joueur avec la texture appropriée selon la direction
        int directionOffset = 0;
        
        // Déterminer l'offset de texture en fonction de la direction
        switch (direction) {
            case DOWN:
                directionOffset = 0;
                break;
            case LEFT:
                directionOffset = 1;
                break;
            case RIGHT:
                directionOffset = 2;
                break;
            case UP:
                directionOffset = 3;
                break;
        }
        
        // Dessiner le joueur avec la texture correspondant à sa direction
        // Note: Ceci suppose que les textures du joueur sont organisées en une seule image
        // avec différentes directions (comme une spritesheet)
        renderer.drawSprite(textureId, x, y, width, height, directionOffset);
    }
    
    @Override
    public void onCollision(GameObject other) {
        // Gérer les collisions avec les zones d'apparition
        if (other instanceof SpawnZone) {
            SpawnZone spawnZone = (SpawnZone) other;
            
            // Si c'est une nouvelle zone d'apparition
            if (currentSpawnZone != spawnZone) {
                currentSpawnZone = spawnZone;
                System.out.println("Entré dans une zone d'apparition de type: " + spawnZone.getType());
            }
        }
    }
    
    /**
     * Gérer les entrées de déplacement
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    private void handleMovementInput(float deltaTime) {
        float moveX = 0.0f;
        float moveY = 0.0f;
        
        // Déplacement horizontal
        if (inputManager.isKeyPressed(GLFW_KEY_LEFT) || inputManager.isKeyPressed(GLFW_KEY_A)) {
            moveX -= moveSpeed * deltaTime;
            direction = Direction.LEFT;
        } else if (inputManager.isKeyPressed(GLFW_KEY_RIGHT) || inputManager.isKeyPressed(GLFW_KEY_D)) {
            moveX += moveSpeed * deltaTime;
            direction = Direction.RIGHT;
        }
        
        // Déplacement vertical
        if (inputManager.isKeyPressed(GLFW_KEY_UP) || inputManager.isKeyPressed(GLFW_KEY_W)) {
            moveY -= moveSpeed * deltaTime;
            direction = Direction.UP;
        } else if (inputManager.isKeyPressed(GLFW_KEY_DOWN) || inputManager.isKeyPressed(GLFW_KEY_S)) {
            moveY += moveSpeed * deltaTime;
            direction = Direction.DOWN;
        }
        
        // Mettre à jour la position
        x += moveX;
        y += moveY;
        
        // Mettre à jour l'état de mouvement
        isMoving = moveX != 0.0f || moveY != 0.0f;
    }
    
    /**
     * Appelé lorsqu'un pas complet est effectué
     */
    private void onStepTaken() {
        // Si le joueur est dans une zone d'apparition, vérifier si une créature apparaît
        if (currentSpawnZone != null) {
            Creature spawnedCreature = currentSpawnZone.incrementSteps();
            if (spawnedCreature != null) {
                startBattle(spawnedCreature);
            }
        }
    }
    
    /**
     * Démarrer un combat avec une créature
     * 
     * @param creature La créature à combattre
     */
    public void startBattle(Creature creature) {
        currentBattleCreature = creature;
        System.out.println("Combat démarré contre " + creature.getName() + " (Niveau " + creature.getLevel() + ")");
        
        // Si la créature était visible, la désactiver pendant le combat
        if (currentSpawnZone != null && currentSpawnZone.hasVisibleCreature()) {
            currentSpawnZone.setVisibleCreatureActive(false);
        }
        
        // Notifier le callback pour démarrer le combat
        if (combatStartCallback != null) {
            combatStartCallback.onCombatStart(this, creature);
        }
    }
    
    /**
     * Terminer le combat actuel
     * 
     * @param captured true si la créature a été capturée, false sinon
     */
    public void endBattle(boolean captured) {
        if (currentBattleCreature != null) {
            if (captured) {
                capturedCreatures.add(currentBattleCreature);
                System.out.println(currentBattleCreature.getName() + " a été capturé !");
            } else {
                System.out.println(currentBattleCreature.getName() + " s'est échappé !");
            }
            
            currentBattleCreature = null;
            
            // Si la créature était visible, elle reste désactivée après la capture
            // Sinon, elle pourrait être réactivée plus tard
        }
    }
    
    /**
     * Définir le callback pour le démarrage d'un combat
     * 
     * @param callback Callback à appeler lorsqu'un combat démarre
     */
    public void setCombatStartCallback(CombatStartCallback callback) {
        this.combatStartCallback = callback;
    }
    
    /**
     * Vérifier si le joueur est en combat
     * 
     * @return true si le joueur est en combat, false sinon
     */
    public boolean isInBattle() {
        return currentBattleCreature != null;
    }
    
    /**
     * Obtenir la créature actuellement en combat
     * 
     * @return La créature en combat, ou null si pas de combat
     */
    public Creature getCurrentBattleCreature() {
        return currentBattleCreature;
    }
    
    /**
     * Obtenir l'inventaire du joueur
     * 
     * @return L'inventaire du joueur
     */
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Obtenir la liste des créatures capturées
     * 
     * @return Liste des créatures capturées
     */
    public List<Creature> getCapturedCreatures() {
        // Combiner les créatures de l'inventaire et de l'ancienne liste
        List<Creature> allCreatures = new ArrayList<>(capturedCreatures);
        allCreatures.addAll(inventory.getCapturedCreatures());
        return allCreatures;
    }
    
    /**
     * Obtenir le nombre de crystaux du joueur
     * 
     * @return Nombre de crystaux
     */
    public int getCrystals() {
        return crystals;
    }
    
    /**
     * Définir le nombre de crystaux du joueur
     * 
     * @param crystals Nouveau nombre de crystaux
     */
    public void setCrystals(int crystals) {
        this.crystals = Math.max(0, crystals); // Empêcher les valeurs négatives
    }
    
    /**
     * Ajouter des crystaux au joueur
     * 
     * @param amount Montant à ajouter
     */
    public void addCrystals(int amount) {
        this.crystals += amount;
    }
    
    /**
     * Retirer des crystaux au joueur
     * 
     * @param amount Montant à retirer
     * @return true si le joueur avait assez de crystaux, false sinon
     */
    public boolean removeCrystals(int amount) {
        if (crystals >= amount) {
            crystals -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * Obtenir le nom du joueur
     * 
     * @return Nom du joueur
     */
    public String getName() {
        return name;
    }
    
    /**
     * Définir le nom du joueur
     * 
     * @param name Nouveau nom du joueur
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Obtenir le niveau du joueur
     * 
     * @return Niveau du joueur
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Définir le niveau du joueur
     * 
     * @param level Nouveau niveau du joueur
     */
    public void setLevel(int level) {
        this.level = Math.max(1, level); // Niveau minimum de 1
    }
    
    /**
     * Obtenir l'expérience du joueur
     * 
     * @return Expérience du joueur
     */
    public int getExperience() {
        return experience;
    }
    
    /**
     * Définir l'expérience du joueur
     * 
     * @param experience Nouvelle expérience du joueur
     */
    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
    }
    
    /**
     * Obtenir la faction du joueur
     * 
     * @return Faction du joueur
     */
    public Faction getFaction() {
        return faction;
    }
    
    /**
     * Définir la faction du joueur
     * 
     * @param faction Nouvelle faction du joueur
     */
    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    
    /**
     * Obtenir la direction du joueur
     * 
     * @return Direction du joueur
     */
    public Direction getDirection() {
        return direction;
    }
    
    /**
     * Définir la direction du joueur
     * 
     * @param direction Nouvelle direction du joueur
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    /**
     * Obtenir la monnaie du joueur (alias pour getCrystals)
     * 
     * @return Montant de monnaie
     */
    public int getMoney() {
        return getCrystals();
    }
    
    /**
     * Définir la monnaie du joueur (alias pour setCrystals)
     * 
     * @param money Nouveau montant de monnaie
     */
    public void setMoney(int money) {
        setCrystals(money);
    }
    
    /**
     * Définir les créatures capturées
     * 
     * @param creatures Liste des créatures capturées
     */
    public void setCapturedCreatures(List<Creature> creatures) {
        this.capturedCreatures = new ArrayList<>(creatures);
    }
    
    /**
     * Ajouter une entrée au journal du joueur
     * 
     * @param entry Texte de l'entrée à ajouter
     */
    public void addJournalEntry(String entry) {
        if (entry == null || entry.trim().isEmpty()) {
            return; // Ne pas ajouter d'entrées vides
        }
        
        // Ajouter l'entrée au début de la liste (les plus récentes en premier)
        journal.add(0, entry);
        
        // Limiter la taille du journal
        if (journal.size() > MAX_JOURNAL_ENTRIES) {
            journal.remove(journal.size() - 1); // Supprimer l'entrée la plus ancienne
        }
    }
    
    /**
     * Obtenir le journal du joueur
     * 
     * @return Liste des entrées du journal (les plus récentes en premier)
     */
    public List<String> getJournal() {
        return new ArrayList<>(journal); // Retourner une copie pour éviter les modifications externes
    }
    
    /**
     * Effacer le journal du joueur
     */
    public void clearJournal() {
        journal.clear();
    }
    
    /**
     * Ajouter une zone découverte
     * 
     * @param areaId ID de la zone découverte
     * @return true si la zone n'avait pas encore été découverte, false sinon
     */
    public boolean addDiscoveredArea(String areaId) {
        if (!discoveredAreas.contains(areaId)) {
            discoveredAreas.add(areaId);
            return true;
        }
        return false;
    }
    
    /**
     * Vérifier si une zone a été découverte
     * 
     * @param areaId ID de la zone à vérifier
     * @return true si la zone a été découverte, false sinon
     */
    public boolean hasDiscoveredArea(String areaId) {
        return discoveredAreas.contains(areaId);
    }
    
    /**
     * Obtenir la liste des zones découvertes
     * 
     * @return Liste des zones découvertes
     */
    public List<String> getDiscoveredAreas() {
        return new ArrayList<>(discoveredAreas);
    }
    
    /**
     * Ajouter de l'expérience au joueur
     * 
     * @param amount Quantité d'expérience à ajouter
     * @return true si le joueur a gagné un niveau, false sinon
     */
    public boolean addExperience(int amount) {
        int oldLevel = level;
        experience += amount;
        
        // Vérifier si le joueur gagne un niveau
        updateLevel();
        
        return level > oldLevel;
    }
    
    /**
     * Mettre à jour le niveau du joueur en fonction de son expérience
     */
    private void updateLevel() {
        // Formule simple : 100 * niveau actuel pour passer au niveau suivant
        while (experience >= 100 * level) {
            experience -= 100 * level;
            level++;
        }
    }
    
    /**
     * Ajouter de l'argent au joueur
     * 
     * @param amount Montant à ajouter
     */
    public void addMoney(int amount) {
        this.crystals += amount;
    }
    
    /**
     * Ajouter une créature à la liste des créatures capturées
     * 
     * @param creature Créature à ajouter
     * @return true si la créature a été ajoutée, false sinon
     */
    public boolean addCreature(Creature creature) {
        if (creature != null) {
            capturedCreatures.add(creature);
            return true;
        }
        return false;
    }
    
    /**
     * Vérifier si le joueur est membre d'une faction
     * 
     * @param factionName Nom de la faction
     * @return true si le joueur est membre de la faction, false sinon
     */
    public boolean isMemberOfFaction(String factionName) {
        // Vérifier si le joueur est membre de la faction par son enum
        if (faction != null && faction.name().equalsIgnoreCase(factionName)) {
            return true;
        }
        
        // Vérifier si le joueur a une réputation suffisante avec la faction
        Integer reputation = factionReputations.get(factionName);
        return reputation != null && reputation >= 50; // Seuil arbitraire de 50 pour être considéré comme membre
    }
    
    /**
     * Obtenir la réputation du joueur auprès d'une faction
     * 
     * @param factionName Nom de la faction
     * @return Réputation (0 par défaut si non définie)
     */
    public int getFactionReputation(String factionName) {
        return factionReputations.getOrDefault(factionName, 0);
    }
    
    /**
     * Définir la réputation du joueur auprès d'une faction
     * 
     * @param factionName Nom de la faction
     * @param reputation Nouvelle réputation
     */
    public void setFactionReputation(String factionName, int reputation) {
        factionReputations.put(factionName, Math.max(0, Math.min(100, reputation))); // Limiter entre 0 et 100
    }
    
    /**
     * Ajouter de la réputation auprès d'une faction
     * 
     * @param factionName Nom de la faction
     * @param amount Montant à ajouter (peut être négatif)
     * @return Nouvelle réputation
     */
    public int addFactionReputation(String factionName, int amount) {
        int currentReputation = getFactionReputation(factionName);
        int newReputation = Math.max(0, Math.min(100, currentReputation + amount));
        setFactionReputation(factionName, newReputation);
        return newReputation;
    }
    
    /**
     * Vérifier si le joueur possède un objet de type spécifique
     * 
     * @param itemClass Classe de l'objet à vérifier
     * @return true si le joueur possède au moins un objet de ce type, false sinon
     */
    public <T> boolean hasItem(Class<T> itemClass) {
        return inventory.hasItem(itemClass);
    }
    
    /**
     * Vérifier si le joueur possède un objet avec un nom spécifique
     * 
     * @param itemName Nom de l'objet à vérifier
     * @return true si le joueur possède au moins un objet avec ce nom, false sinon
     */
    public boolean hasItem(String itemName) {
        return inventory.hasItem(itemName);
    }
    
    /**
     * Retirer un nombre spécifique d'objets d'un type donné de l'inventaire
     * 
     * @param itemClass Classe de l'objet à retirer
     * @param count Nombre d'objets à retirer
     * @return true si les objets ont été retirés, false sinon
     */
    public <T> boolean removeItem(Class<T> itemClass, int count) {
        return inventory.removeItems(itemClass, count);
    }
    
    /**
     * Retirer un nombre spécifique d'objets avec un nom donné de l'inventaire
     * 
     * @param itemName Nom de l'objet à retirer
     * @param count Nombre d'objets à retirer
     * @return true si les objets ont été retirés, false sinon
     */
    public boolean removeItem(String itemName, int count) {
        return inventory.removeItems(itemName, count);
    }
    
    /**
     * Directions possibles pour le joueur
     */
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    
    /**
     * Factions possibles pour le joueur
     */
    public enum Faction {
        NEUTRE,      // Faction neutre, par défaut
        LUMIERE,     // Faction de la Lumière
        TENEBRES,    // Faction des Ténèbres
        EQUILIBRE,   // Faction de l'Équilibre
        CHAOS,       // Faction du Chaos
        HARMONIE     // Faction de l'Harmonie
    }
    
    /**
     * Interface pour le callback de démarrage de combat
     */
    public interface CombatStartCallback {
        void onCombatStart(Player player, Creature creature);
    }
}

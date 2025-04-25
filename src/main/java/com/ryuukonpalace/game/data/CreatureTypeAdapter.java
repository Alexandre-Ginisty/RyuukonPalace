package com.ryuukonpalace.game.data;

import com.google.gson.*;
import com.ryuukonpalace.game.creatures.Creature;
import java.lang.reflect.Type;

/**
 * Adaptateur pour la sérialisation/désérialisation des créatures avec Gson.
 * Permet de gérer correctement l'héritage et les références circulaires.
 */
public class CreatureTypeAdapter implements JsonSerializer<Creature>, JsonDeserializer<Creature> {
    
    @Override
    public JsonElement serialize(Creature creature, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        
        // Ajouter les propriétés de base
        jsonObject.addProperty("id", creature.getId());
        jsonObject.addProperty("name", creature.getName());
        jsonObject.addProperty("type", creature.getType().toString());
        jsonObject.addProperty("level", creature.getLevel());
        jsonObject.addProperty("health", creature.getHealth());
        jsonObject.addProperty("maxHealth", creature.getMaxHealth());
        jsonObject.addProperty("attack", creature.getAttack());
        jsonObject.addProperty("defense", creature.getDefense());
        jsonObject.addProperty("speed", creature.getSpeed());
        jsonObject.addProperty("friendship", creature.getFriendship());
        jsonObject.addProperty("isWild", creature.isWild());
        
        // Ajouter les capacités
        JsonArray abilitiesArray = new JsonArray();
        for (int i = 0; i < creature.getAbilities().size(); i++) {
            JsonObject abilityObject = new JsonObject();
            abilityObject.addProperty("id", creature.getAbilities().get(i).getId());
            abilityObject.addProperty("name", creature.getAbilities().get(i).getName());
            abilitiesArray.add(abilityObject);
        }
        jsonObject.add("abilities", abilitiesArray);
        
        return jsonObject;
    }
    
    @Override
    public Creature deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Récupérer les propriétés de base
        int id = jsonObject.get("id").getAsInt();
        String name = jsonObject.get("name").getAsString();
        String typeStr = jsonObject.get("type").getAsString();
        int level = jsonObject.get("level").getAsInt();
        int health = jsonObject.get("health").getAsInt();
        int attack = jsonObject.get("attack").getAsInt();
        int defense = jsonObject.get("defense").getAsInt();
        int speed = jsonObject.get("speed").getAsInt();
        
        // Convertir le type
        com.ryuukonpalace.game.creatures.CreatureType type = 
                com.ryuukonpalace.game.creatures.CreatureType.valueOf(typeStr);
        
        // Créer la créature
        Creature creature = new Creature(id, name, type, level, health, attack, defense, speed);
        
        // Définir les propriétés supplémentaires si présentes
        if (jsonObject.has("friendship")) {
            creature.setFriendship(jsonObject.get("friendship").getAsInt());
        }
        
        if (jsonObject.has("isWild")) {
            creature.setWild(jsonObject.get("isWild").getAsBoolean());
        }
        
        // Ajouter les capacités
        if (jsonObject.has("abilities")) {
            JsonArray abilitiesArray = jsonObject.getAsJsonArray("abilities");
            for (JsonElement element : abilitiesArray) {
                JsonObject abilityObject = element.getAsJsonObject();
                int abilityId = abilityObject.get("id").getAsInt();
                
                // Récupérer la capacité depuis une factory ou un gestionnaire
                com.ryuukonpalace.game.creatures.Ability ability = 
                        com.ryuukonpalace.game.creatures.AbilityFactory.getInstance().getAbility(abilityId);
                
                if (ability != null) {
                    creature.addAbility(ability);
                }
            }
        }
        
        return creature;
    }
}

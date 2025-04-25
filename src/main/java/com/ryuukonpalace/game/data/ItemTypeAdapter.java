package com.ryuukonpalace.game.data;

import com.google.gson.*;
import com.ryuukonpalace.game.items.Item;
import com.ryuukonpalace.game.items.CaptureStone;
import java.lang.reflect.Type;

/**
 * Adaptateur pour la sérialisation/désérialisation des objets avec Gson.
 * Permet de gérer correctement l'héritage et les sous-types d'objets.
 */
public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
    
    @Override
    public JsonElement serialize(Item item, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        
        // Ajouter les propriétés communes à tous les objets
        jsonObject.addProperty("id", item.getId());
        jsonObject.addProperty("name", item.getName());
        jsonObject.addProperty("description", item.getDescription());
        jsonObject.addProperty("value", item.getValue());
        jsonObject.addProperty("textureId", item.getTextureId());
        
        // Ajouter le type d'objet pour la désérialisation
        jsonObject.addProperty("itemType", item.getClass().getSimpleName());
        
        // Ajouter les propriétés spécifiques selon le type d'objet
        if (item instanceof CaptureStone) {
            CaptureStone stone = (CaptureStone) item;
            jsonObject.addProperty("material", stone.getMaterial().name());
            jsonObject.addProperty("type", stone.getType().name());
            jsonObject.addProperty("active", stone.isActive());
            
            // Ajouter la créature capturée si présente
            if (stone.isActive() && stone.getCapturedCreature() != null) {
                jsonObject.addProperty("capturedCreatureId", stone.getCapturedCreature().getId());
            }
        }
        
        return jsonObject;
    }
    
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Récupérer le type d'objet
        String itemType = jsonObject.get("itemType").getAsString();
        
        // Récupérer les propriétés communes
        int id = jsonObject.get("id").getAsInt();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        int value = jsonObject.get("value").getAsInt();
        
        // Créer l'objet selon son type
        Item item = null;
        
        if ("CaptureStone".equals(itemType)) {
            // Récupérer les propriétés spécifiques aux pierres de capture
            String materialStr = jsonObject.get("material").getAsString();
            String typeStr = jsonObject.get("type").getAsString();
            
            // Convertir les enums
            com.ryuukonpalace.game.items.CaptureStoneMaterial material = 
                    com.ryuukonpalace.game.items.CaptureStoneMaterial.valueOf(materialStr);
            com.ryuukonpalace.game.items.CaptureStoneType type = 
                    com.ryuukonpalace.game.items.CaptureStoneType.valueOf(typeStr);
            
            // Créer la pierre de capture
            CaptureStone stone = new CaptureStone(id, name, description, value, material, type);
            
            // Définir si la pierre est active
            if (jsonObject.has("active")) {
                stone.setActive(jsonObject.get("active").getAsBoolean());
            }
            
            // Récupérer la créature capturée si présente
            if (jsonObject.has("capturedCreatureId")) {
                // On stocke l'ID dans un commentaire pour indiquer qu'il faudra
                // charger la créature plus tard et l'assigner à la pierre
                // La créature sera chargée séparément et assignée à la pierre
                // par le système de gestion des données
                @SuppressWarnings("unused") // Cette variable sera utilisée par le système de chargement
                int capturedCreatureId = jsonObject.get("capturedCreatureId").getAsInt();
                // Nous ne pouvons pas assigner directement la créature ici car elle n'est pas encore chargée
                // Le système de chargement devra utiliser cet ID pour récupérer la créature
                // et l'assigner à la pierre avec stone.captureCreature(creature)
            }
            
            item = stone;
        } else {
            // Créer un objet générique pour les autres types
            item = new Item(id, name, description, value) {
                @Override
                public boolean use() {
                    // Implémentation par défaut
                    return false;
                }
            };
        }
        
        return item;
    }
}

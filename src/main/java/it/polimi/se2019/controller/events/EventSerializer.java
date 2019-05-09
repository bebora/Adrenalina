package it.polimi.se2019.controller.events;

import com.google.gson.*;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.view.ViewTileCoords;

import java.lang.reflect.Type;

public class EventSerializer implements JsonSerializer<EventVisitable> {
    Gson gson = new Gson();
    @Override
    public JsonElement serialize(EventVisitable eventVisitable, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject wrapper = new JsonObject();
        String objectType = eventVisitable.getClass().getSimpleName();
        wrapper.add("type", new JsonPrimitive(objectType));
        String event = "event";
        switch (objectType) {
            case "SelectAction":
            {
                String action = gson.toJson(eventVisitable);
                wrapper.add(event , new JsonPrimitive(action));
                break;
            }
            case "SelectPlayers":
            {
                JsonArray jsonArray = new JsonArray();
                SelectPlayers selectPlayers = (SelectPlayers) eventVisitable;
                for (String player : selectPlayers.getPlayerIds())
                    jsonArray.add(player);
                wrapper.add(event , jsonArray);
                break;
            }
            case "SelectTiles": {
                JsonArray jsonArray = new JsonArray();
                SelectTiles selectTiles = (SelectTiles) eventVisitable;
                for (ViewTileCoords coord : selectTiles.getSelectedTiles()) {
                    jsonArray.add(gson.toJson(coord));
                }
                wrapper.add(event, jsonArray);
                break;
            }
            case "SelectWeapon": {
                String weapon = gson.toJson(eventVisitable);
                wrapper.add(event, new JsonPrimitive(weapon));
                break;
            }
            default:
                throw new JsonParseException("PARSING NOT IMPLEMENTED YET");

        }
        JsonElement jsonElement =  gson.fromJson(wrapper.toString(), JsonElement.class);
        return jsonElement;
    }
}

package it.polimi.se2019.controller.events;

import com.google.gson.*;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.view.ViewTileCoords;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EventDeserializer implements JsonDeserializer<EventVisitable> {
    @Override
    public EventVisitable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        Gson gson = new Gson();
        EventVisitable eventVisitable;
        JsonObject wrapper = jsonElement.getAsJsonObject();
        String objectType = wrapper.get("type").getAsString();
        JsonObject event = wrapper.get("event").getAsJsonObject();
        switch (objectType) {
            case "ConnectionRequest":
                eventVisitable = gson.fromJson(jsonElement,SelectAction.class);
                break;
            case "SelectAction":
            {
                eventVisitable = gson.fromJson(jsonElement,SelectAction.class);
                break;
            }
            case "SelectPlayers":
            {
                List<String> players= new ArrayList<>();
                JsonArray jsonPlayers = event.get("players").getAsJsonArray();
                for (JsonElement player : jsonPlayers) {
                    players.add(player.getAsString());
                }
                String token = event.get("token").getAsString();
                eventVisitable = new SelectPlayers(players, token);
                break;
            }
            case "SelectWeapon": {
                eventVisitable = gson.fromJson(jsonElement, SelectWeapon.class);
                break;
            }
            case "SelectTiles": {
                List<ViewTileCoords> coords = new ArrayList<>();
                JsonArray jsonCoords = event.get("tiles").getAsJsonArray();
                for (JsonElement tileCoord : jsonCoords) {
                    coords.add(gson.fromJson(tileCoord,ViewTileCoords.class));
                }
                String token = event.get("token").getAsString();
                eventVisitable = new SelectTiles(coords, token);
                break;
            }
            default:
                throw new JsonParseException("WRONG FORMAT");
        }
        return eventVisitable;
    }

}

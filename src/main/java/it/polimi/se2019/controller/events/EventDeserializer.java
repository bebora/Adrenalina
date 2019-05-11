package it.polimi.se2019.controller.events;

import com.google.gson.*;
import it.polimi.se2019.controller.EventVisitable;

import java.lang.reflect.Type;

public class EventDeserializer implements JsonDeserializer<EventVisitable> {
    @Override
    public EventVisitable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        Gson gson = new Gson();
        EventVisitable eventVisitable;
        JsonObject wrapper = jsonElement.getAsJsonObject();
        String objectType = wrapper.get("type").getAsString();
        JsonObject event = new JsonParser().parse(wrapper.get("event").getAsString()).getAsJsonObject();
        switch (objectType) {
            case "ConnectionRequest":
                eventVisitable = gson.fromJson(jsonElement,ConnectionRequest.class);
                break;
            case "SelectAction":

                eventVisitable = gson.fromJson(jsonElement,SelectAction.class);
                break;

            case "SelectPlayers":

                eventVisitable = gson.fromJson(event, SelectPlayers.class);
                break;

            case "SelectWeapon":
                eventVisitable = gson.fromJson(event, SelectWeapon.class);
                break;

            case "SelectTiles":
                eventVisitable = gson.fromJson(event, SelectTiles.class);

                break;

            default:
                throw new JsonParseException("WRONG FORMAT");
        }
        return eventVisitable;
    }

}

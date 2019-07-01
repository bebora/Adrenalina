package it.polimi.se2019.network.events;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Deserializer class used to deserialize EventVisitable
 * Need a new case in the switch for every new EventVisitable added to the logic
 * For nested custom Objects, it may be necessary to implement custom parsing
 */
public class EventDeserializer implements JsonDeserializer<EventVisitable> {
    @Override
    public EventVisitable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        Gson gson = new Gson();
        EventVisitable eventVisitable;
        JsonObject wrapper = jsonElement.getAsJsonObject();
        String objectType = wrapper.get("type").getAsString();
        JsonObject event = new JsonParser().parse(wrapper.get("event").getAsString()).getAsJsonObject();
        switch (objectType) {
            case "AckEvent":
                eventVisitable = gson.fromJson(event, AckEvent.class);
                break;
            case "SelectAmmo":
                eventVisitable = gson.fromJson(event, SelectAmmo.class);
                break;
            case "ConnectionRequest":
                eventVisitable = gson.fromJson(event, ConnectionRequest.class);
                break;

            case "SelectAction":
                eventVisitable = gson.fromJson(event, SelectAction.class);
                break;

            case "SelectDirection":
                eventVisitable = gson.fromJson(event, SelectDirection.class);
                break;

            case "SelectEffect":
                eventVisitable = gson.fromJson(event, SelectEffect.class);
                break;

            case "SelectPlayers":
                eventVisitable = gson.fromJson(event, SelectPlayers.class);
                break;

            case "SelectPowerUps":
                eventVisitable = gson.fromJson(event, SelectPowerUps.class);
                break;

            case "SelectRoom":
                eventVisitable = gson.fromJson(event, SelectRoom.class);
                break;

            case "SelectStop":
                eventVisitable = gson.fromJson(event, SelectStop.class);
                break;

            case "SelectTiles":
                eventVisitable = gson.fromJson(event, SelectTiles.class);
                break;

            case "SelectWeapon":
                eventVisitable = gson.fromJson(event, SelectWeapon.class);
                break;

            default:
                throw new JsonParseException("WRONG FORMAT");
        }
        return eventVisitable;
    }

}

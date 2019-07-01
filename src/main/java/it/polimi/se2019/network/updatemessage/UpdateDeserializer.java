package it.polimi.se2019.network.updatemessage;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Deserializer class used to deserialize {@link UpdateVisitable}
 * Need a new case in the switch for every new UpdateVisitable added to the logic
 * For nested custom Objects, it may be necessary to implement custom parsing
 */
public class UpdateDeserializer implements JsonDeserializer<UpdateVisitable>{
    @Override
    public UpdateVisitable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        Gson gson = new Gson();
        UpdateVisitable updateVisitable;
        JsonObject wrapper = jsonElement.getAsJsonObject();
        String objectType = wrapper.get("type").getAsString();
        JsonObject update = new JsonParser().parse(wrapper.get("update").getAsString()).getAsJsonObject();
        switch (objectType) {
            case "SelectableOptionsUpdate":
                updateVisitable = gson.fromJson(update, SelectableOptionsUpdate.class);
                break;
            case "AmmosTakenUpdate":
                updateVisitable = gson.fromJson(update, AmmosTakenUpdate.class);
                break;

            case "AttackPlayerUpdate":
                updateVisitable = gson.fromJson(update, AttackPlayerUpdate.class);
                break;

            case "MovePlayerUpdate":
                updateVisitable = gson.fromJson(update, MovePlayerUpdate.class);
                break;

            case "PopupMessageUpdate":
                updateVisitable = gson.fromJson(update, PopupMessageUpdate.class);
                break;
            case "TileUpdate":
                updateVisitable = gson.fromJson(update, TileUpdate.class);
                break;

            case "TotalUpdate":
                updateVisitable = gson.fromJson(update, TotalUpdate.class);
                break;

            case "WeaponTakenUpdate":
                updateVisitable = gson.fromJson(update, WeaponTakenUpdate.class);
                break;
            case "PingUpdate":
                updateVisitable = gson.fromJson(update, PingUpdate.class);
                break;

            default:
                throw new JsonParseException("WRONG FORMAT");
        }
        return updateVisitable;
    }

}

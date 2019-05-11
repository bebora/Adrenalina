package it.polimi.se2019.model.updatemessage;

import com.google.gson.*;

import java.lang.reflect.Type;

public class UpdateDeserializer implements JsonDeserializer<UpdateVisitable>{
    @Override
    public UpdateVisitable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        Gson gson = new Gson();
        UpdateVisitable updateVisitable;
        JsonObject wrapper = jsonElement.getAsJsonObject();
        String objectType = wrapper.get("type").getAsString();
        JsonObject event = new JsonParser().parse(wrapper.get("update").getAsString()).getAsJsonObject();
        switch (objectType) {
            case "AmmosTakenUpdate":
                updateVisitable = gson.fromJson(jsonElement, AmmosTakenUpdate.class);
                break;
            case "AttackPlayerUpdate":

                updateVisitable = gson.fromJson(jsonElement, AttackPlayerUpdate.class);
                break;

            case "AvailableActionsUpdate":
                updateVisitable = gson.fromJson(event, AvailableActionsUpdate.class);
                break;

            case "MovePlayerUpdate":
                updateVisitable = gson.fromJson(event, MovePlayerUpdate.class);
                break;

            case "PopupMessageUpdate":
                updateVisitable = gson.fromJson(event, PopupMessageUpdate.class);
                break;
            case "SelectFromPlayersUpdate":
                updateVisitable = gson.fromJson(event, SelectFromPlayersUpdate.class);
                break;

            case "SelectFromRoomsUpdate":
                updateVisitable = gson.fromJson(event, SelectFromRoomsUpdate.class);
                break;

            case "SelectFromTilesUpdate":
                updateVisitable = gson.fromJson(event, SelectFromTilesUpdate.class);
                break;

            case "SuccessConnectionUpdate":
                updateVisitable = gson.fromJson(event, SuccessConnectionUpdate.class);
                break;
            case "TileUpdate":
                updateVisitable = gson.fromJson(event, TileUpdate.class);
                break;

            case "TotalUpdate":
                updateVisitable = gson.fromJson(event, TotalUpdate.class);
                break;

            case "WeaponTakenUpdate":
                updateVisitable = gson.fromJson(event, WeaponTakenUpdate.class);
                break;
            default:
                throw new JsonParseException("WRONG FORMAT");
        }
        return updateVisitable;
    }

}

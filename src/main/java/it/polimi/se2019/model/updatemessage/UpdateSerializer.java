package it.polimi.se2019.model.updatemessage;

import com.google.gson.*;

import java.lang.reflect.Type;

public class UpdateSerializer implements JsonSerializer<UpdateVisitable>{
    Gson gson = new Gson();
    @Override
    public JsonElement serialize(UpdateVisitable updateVisitable, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject wrapper = new JsonObject();
        String objectType = updateVisitable.getClass().getSimpleName();
        wrapper.add("type", new JsonPrimitive(objectType));
        String event = "update";
        switch (objectType) {
            default:
                String update = gson.toJson(updateVisitable);
                wrapper.add(event, new JsonPrimitive(update));
                break;
        }
        return gson.fromJson(wrapper.toString(), JsonElement.class);
    }
}

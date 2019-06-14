package it.polimi.se2019.controller.updatemessage;

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
        String update = gson.toJson(updateVisitable);
        wrapper.add(event, new JsonPrimitive(update));
        return gson.fromJson(wrapper.toString(), JsonElement.class);
    }
}

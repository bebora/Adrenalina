package it.polimi.se2019.controller.events;

import com.google.gson.*;
import it.polimi.se2019.controller.EventVisitable;

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
            default:
                String weapon = gson.toJson(eventVisitable);
                wrapper.add(event, new JsonPrimitive(weapon));
                break;
        }
        JsonElement jsonElement =  gson.fromJson(wrapper.toString(), JsonElement.class);
        return jsonElement;
    }
}

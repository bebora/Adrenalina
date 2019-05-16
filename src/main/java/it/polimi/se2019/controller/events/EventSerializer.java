package it.polimi.se2019.controller.events;

import com.google.gson.*;
import it.polimi.se2019.controller.EventVisitable;

import java.lang.reflect.Type;

/**
 * Serialize EventVisitable in a json having as the type, the name of the class serialized.
 * Add particular objectType to switch-case if needed custom parsing
 */
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
                String json = gson.toJson(eventVisitable);
                wrapper.add(event, new JsonPrimitive(json));
                break;
        }
        return gson.fromJson(wrapper.toString(), JsonElement.class);
    }
}

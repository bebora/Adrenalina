package it.polimi.se2019.network.events;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Serialize EventVisitable in a json having as the type, the name of the class serialized.
 * Add particular objectType to switch-case if needed custom parsing
 */
public class EventSerializer implements JsonSerializer<EventVisitable> {
    private Gson gson = new Gson();
    @Override
    public JsonElement serialize(EventVisitable eventVisitable, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject wrapper = new JsonObject();
        String objectType = eventVisitable.getClass().getSimpleName();
        wrapper.add("type", new JsonPrimitive(objectType));
        String event = "event";
        String json = gson.toJson(eventVisitable);
        wrapper.add(event, new JsonPrimitive(json));
        return gson.fromJson(wrapper.toString(), JsonElement.class);
    }
}

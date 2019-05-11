package it.polimi.se2019.controller.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.view.ViewTileCoords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventDeserializerTest {
    Gson gson;
    @BeforeEach
    void initizialize() {
        GsonBuilder gsonBuilder;
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EventVisitable.class, new EventDeserializer());
        gsonBuilder.registerTypeAdapter(EventVisitable.class, new EventSerializer());
        gson = gsonBuilder.create();
    }
    @Test
    void deserialize() {
        //Use serializer, being tested and working to create correct json
        ViewTileCoords tileCoord1 = new ViewTileCoords(0, 0);
        ViewTileCoords tileCoord2 = new ViewTileCoords(0, 1);
        EventVisitable selectTiles = new SelectTiles(new ArrayList<>(Arrays.asList(tileCoord1, tileCoord2)));
        String json = gson.toJson(selectTiles, EventVisitable.class);
        JsonObject jsonElement = gson.fromJson(json, JsonObject.class);
        SelectTiles selectTiles1 = (SelectTiles) gson.fromJson(jsonElement, EventVisitable.class);
        assertEquals(1, selectTiles1.getSelectedTiles().get(1).getPosx());
    }
}
package it.polimi.se2019.controller.events;

import com.google.gson.*;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.view.ViewTileCoords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EventSerializerTest {
    Gson gson;

    @BeforeEach
    void initizialize() {
        GsonBuilder gsonBuilder;
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EventVisitable.class, new EventSerializer());
        gson = gsonBuilder.create();
    }
    @Test
    void serialize() {
        //Serializing array list of custom type
        ViewTileCoords tileCoord1 = new ViewTileCoords(0, 0);
        ViewTileCoords tileCoord2 = new ViewTileCoords(0, 1);
        EventVisitable selectTiles = new SelectTiles(new ArrayList<>(Arrays.asList(tileCoord1, tileCoord2)));
        String json = gson.toJson(selectTiles, EventVisitable.class);
        System.out.println(json);
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        assertEquals("SelectTiles", jsonObject.get("type").getAsString());
        JsonObject event = jsonObject = new JsonParser().parse(jsonObject.get("event").getAsString()).getAsJsonObject();
        JsonArray array = event.getAsJsonArray("selectedTiles");
        assertEquals(1, array.get(1).getAsJsonObject().get("posx").getAsInt());

        //Serializing array list of strings
        String player1 = "NoobMaster69";
        String player2 = "Foo";
        EventVisitable selectPlayers = new SelectPlayers(new ArrayList<>(Arrays.asList(player1, player2)));
        json = gson.toJson(selectPlayers, EventVisitable.class);
        jsonObject = new JsonParser().parse(json).getAsJsonObject();
        event = jsonObject = new JsonParser().parse(jsonObject.get("event").getAsString()).getAsJsonObject();
        array = event.getAsJsonArray("playersIds");
        assertEquals("Foo", array.get(1).getAsString());
    }
}
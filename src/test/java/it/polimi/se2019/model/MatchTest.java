package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchTest {
    @Test
    void getPlayersInTile() {
        List<Player> players = Arrays.asList(new Player(), new Player(), new Player());
        Match match = new NormalMatch(players, "board4.btlb", 8);
        Tile destTile = match.getBoard().getTile(0, 0);
        players.get(0).setTile(destTile);
        players.get(1).setTile(match.getBoard().getTile(1, 0));
        players.get(2).setTile(destTile);
        List<Player> inSameTile = match.getPlayersInTile(destTile);
        assertEquals(2, inSameTile.size());
        assertTrue(inSameTile.containsAll(Arrays.asList(players.get(0), players.get(2))));
    }

    @Test
    void getPlayersInRoom() {
        List<Player> players = Arrays.asList(new Player(), new Player(), new Player());
        Match match = new NormalMatch(players, "board4.btlb", 8);
        Tile destTile = match.getBoard().getTile(1, 2);
        Tile otherTile = match.getBoard().getTile(2, 3);
        players.get(0).setTile(destTile);
        players.get(1).setTile(otherTile);
        players.get(2).setTile(destTile);
        List<Player> inSameRoom = match.getPlayersInRoom(Color.YELLOW);
        assertEquals(3, inSameRoom.size());
        assertTrue(inSameRoom.containsAll(players));
    }
}

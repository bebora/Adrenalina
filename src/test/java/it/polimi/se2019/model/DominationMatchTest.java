package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DominationMatchTest {
    private DominationMatch match;
    private Player foo, poo, boo;
    private List<Player> spawnerPlayers;

    @BeforeEach
    void prepareMatch() {
        foo = new Player();
        poo = new Player();
        boo = new Player();
        match = new DominationMatch(new ArrayList<>(Arrays.asList(foo,poo,boo)), "board1.btlb", 8);
        spawnerPlayers = match.getSpawnPoints();
    }

    @Test
    void insertSpawnPoints() {
        assertEquals(0, spawnerPlayers.size());
        for (int i = 0; i < 3; i++) {
            match.newTurn();
        }
        spawnerPlayers = match.getSpawnPoints();
        assertEquals(3, spawnerPlayers.size());
        assertTrue(spawnerPlayers.stream().allMatch(p -> p.getDominationSpawn() && p.getUsername().equals(p.getColor().toString())));
    }

    @Test
    void checkPlayerDamaged() {
        for (int i = 0; i < 3; i++) {
            match.newTurn();
        }
        spawnerPlayers = match.getSpawnPoints();
        Tile destTile = spawnerPlayers.get(0).getTile();
        Player current = match.getPlayers().get(match.getCurrentPlayer());
        current.setTile(destTile);
        match.newTurn();
        assertEquals(1, current.getDamagesCount());
    }

    @Test
    void scoreDeadShot() {

    }

    @Test
    void checkFrenzy() {
    }
}
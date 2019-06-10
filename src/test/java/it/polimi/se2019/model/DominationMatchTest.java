package it.polimi.se2019.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DominationMatchTest {
    DominationMatch match;
    Player foo, poo, boo;

    @BeforeEach
    void prepareMatch() {
        foo = new Player();
        poo = new Player();
        boo = new Player();
        match = new DominationMatch(new ArrayList<>(Arrays.asList(foo,poo,boo)), "board1.btlb", 8);
    }

    @Test
    void insertSpawnPoints() {
        for (int i = 0; i < 3; i++) {
            match.newTurn();
        }
        assertEquals(3, match.getPlayers().stream().filter(Player::getDominationSpawn).count());
        assertTrue(match.getSpawnPoints().stream().allMatch(p -> p.getDominationSpawn() && p.getUsername().equals(p.getColor().toString())));
    }

    @Test
    void scoreDeadShot() {

    }

    @Test
    void checkFrenzy() {
    }
}
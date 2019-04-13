package model;
import java.util.*;
import java.util.stream.Collectors;
import model.ammos.*;

import model.board.Board;
import model.board.BoardCreator;
import model.board.Color;
import model.board.Tile;
import model.cards.PowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    Board test;
    @BeforeEach
    void Before() {
        test = BoardCreator.parseBoard("board1.btlb",8);
    }

    @Test
    void isLinked() {
        // Test bidirectionaly, given the override of equals for Door Class
        assertEquals(test.isLinked(test.getTile(0,0), test.getTile(0,1)), true);
        assertEquals(test.isLinked(test.getTile(0,1), test.getTile(0,0)), true);
        assertEquals(test.isLinked(test.getTile(1,1), test.getTile(2,1)), true);
        assertEquals(test.isLinked(test.getTile(1,1), test.getTile(2,1)), true);
        assertEquals(test.isLinked(test.getTile(1,0), test.getTile(2,1)), false);
        assertEquals(test.isLinked(test.getTile(1,0), test.getTile(1,1)), false);



        // Test same rooms and close tiles
        assertEquals(test.isLinked(test.getTile(1,1), test.getTile(1,2)), true);
        assertEquals(test.isLinked(test.getTile(2,2), test.getTile(2,3)), false);
    }

    @Test
    void visible() {
        Set<Tile> visibleTiles= test.visibleTiles(test.getTile(1,1));
        Set<Tile> assertTiles = new HashSet<>();
        assertTiles.add(test.getTile(1,2));
        assertTiles.add(test.getTile(0,1));
        assertTiles.add(test.getTile(0,2));
        assertTiles.add(test.getTile(1,1));
        assertTiles.add(test.getTile(2,2));
        assertTiles.add(test.getTile(2,1));
        assertTiles.add(test.getTile(2,0));


        // Test if all and only the assertTiles are the visibleTiles
        assertTrue(assertTiles.containsAll(visibleTiles));
        assertTrue(assertTiles.size() == visibleTiles.size());
    }

    @Test
    void reachable() {
        Set<Tile> reachableTiles = test.reachable(test.getTile(1,1), 2, 2);

        Set<Tile> assertTiles = new HashSet<>();
        assertTiles.add(test.getTile(0,2));
        assertTiles.add(test.getTile(0,0));
        assertTiles.add(test.getTile(1,3));
        assertTiles.add(test.getTile(2,0));
        assertTiles.add(test.getTile(2,2));
        // Test if all ond only the assertTiles are the reachableTiles
        assertTrue(assertTiles.containsAll(reachableTiles));
        assertTrue(assertTiles.size() == reachableTiles.size());


        reachableTiles = test.reachable(test.getTile(1,1), 0, 1);
        assertTiles = new HashSet<>();
        assertTiles.add(test.getTile(0,1));
        assertTiles.add(test.getTile(1,2));
        assertTiles.add(test.getTile(2,1));
        assertTiles.add(test.getTile(1,1));

        // Test if all ond only the assertTiles are the reachableTiles
        assertTrue(assertTiles.containsAll(reachableTiles));
        assertTrue(assertTiles.size() == reachableTiles.size());
    }

    /**
     * Tests the weapons refresher mechanism, as well as the UnlimitedDeck functionality
     */
    @Test
    void refreshWeapons() {
        //Tests that weapons refreshes only when needed
        test.refreshWeapons();
        for (Tile t : test.getTiles().stream().flatMap(List::stream).filter(Objects::nonNull).filter(Tile::isSpawn).collect(Collectors.toList())) {
            assertEquals(t.getWeaponsNumber(),3);
        }

        //Tests that weapons get refreshed correctly
        test.getTile(0,2).getWeapons().remove(0);
        test.refreshWeapons();
        for (Tile t : test.getTiles().stream().flatMap(List::stream).filter(Objects::nonNull).filter(Tile::isSpawn).collect(Collectors.toList())) {
            assertEquals(t.getWeaponsNumber(),3);
        }

        //TODO add test for checking if weapons are really limited
    }

    @Test
    void drawPowerUp() {
        for (int i = 0; i < 100; i++) {
            PowerUp testPowerUp = test.drawPowerUp();
            test.discardPowerUp(testPowerUp);
            assertNotNull(testPowerUp);
        }
    }

    @Test
    void refreshAmmos() {
            test.getTiles().stream().
                    flatMap(List::stream).
                    filter(Objects::nonNull).
                    filter(t -> !(t.isSpawn())).
                    forEach(Tile::grabAmmoCard);
            test.refreshAmmos();
            for (Tile t : test.getTiles().stream().flatMap(List::stream).filter(Objects::nonNull).filter(t -> !(t.isSpawn())).collect(Collectors.toList())) {
                assertNotNull(t.getAmmoCard());
            }
    }

    @Test
    void getSpawnPointFromAmmo() {
        Ammo ammo = Ammo.RED;
        assertEquals(test.getTile(1,0), test.getSpawnPointFromAmmo(ammo));
    //TODO insert assert for thrown exception
    }
}
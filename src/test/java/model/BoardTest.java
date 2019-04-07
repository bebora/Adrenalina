package model;
import java.util.*;

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
}
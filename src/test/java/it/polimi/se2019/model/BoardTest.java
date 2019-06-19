package it.polimi.se2019.model;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.PowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    Board test;
    @BeforeEach
    void Before() {
        test = BoardCreator.parseBoard("board3.btlb",8);
    }

    @Test
    void isLinked() {
        // Test bidirectionally, given the override of equals for Door Class
        assertEquals(test.isLinked(test.getTile(0,0), test.getTile(0,1),false), true);
        assertEquals(test.isLinked(test.getTile(0,1), test.getTile(0,0), false), true);
        assertEquals(test.isLinked(test.getTile(1,1), test.getTile(2,1), false), true);
        assertEquals(test.isLinked(test.getTile(1,1), test.getTile(2,1), false), true);
        assertEquals(test.isLinked(test.getTile(1,0), test.getTile(2,1), false), false);
        assertEquals(test.isLinked(test.getTile(1,0), test.getTile(1,1), false), false);



        // Test same rooms and close tiles
        assertEquals(test.isLinked(test.getTile(1,1), test.getTile(1,2), false), true);
        assertEquals(true, test.isLinked(test.getTile(2,2), test.getTile(2,3), false));
        assertEquals(test.isLinked(test.getTile(2,2), test.getTile(2,3), true), true);
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
        Set<Tile> reachableTiles = test.reachable(test.getTile(1,1), 2, 2, false);

        Set<Tile> assertTiles = new HashSet<>();
        assertTiles.add(test.getTile(0,2));
        assertTiles.add(test.getTile(0,0));
        assertTiles.add(test.getTile(1,3));
        assertTiles.add(test.getTile(2,0));
        assertTiles.add(test.getTile(2,2));
        // Test if all ond only the assertTiles are the reachableTiles
        assertTrue(assertTiles.containsAll(reachableTiles));
        assertTrue(assertTiles.size() == reachableTiles.size());


        reachableTiles = test.reachable(test.getTile(1,1), 0, 1, false);
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
        assertEquals(test.getTile(1,0), test.getSpawningPoint(CardCreator.parsePowerUp("targetingScope.btl", Ammo.RED)));
    }
}
package model;

import model.ammos.Ammo;
import model.board.Board;
import model.board.BoardCreator;
import model.board.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardCreatorTest {

    @Test
    void parseBoard() {
        Board test = BoardCreator.parseBoard("board1.btlb",8);
        // Test skull setter
        assertEquals(test.getSkulls(),8);

        // Test Color parser
        assertEquals(test.getTiles().get(0).get(0).getRoom(), Color.RED);
        assertEquals(test.getTiles().get(2).get(2).getRoom(), Color.WHITE);

        //Test positions parsing and setting
        assertEquals(test.getTiles().get(1).get(2).getPosx(), 2);

        // Test empty spaces in map
        assertEquals(test.getTiles().get(0).get(3), null);

        // Test spawn points as capital letters
        assertEquals(test.getTiles().get(0).get(2).isSpawn(), Boolean.TRUE);
        assertEquals(test.getTiles().get(0).get(1).isSpawn(), Boolean.FALSE);

        }
}
package it.polimi.se2019.view;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.model.updatemessage.TotalUpdate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
//TODO refactor to new updating logic with receiver
class UpdateVisitorTest {
    static View firstView;
    static String username;
    static Board board;
    static List<Player> players;
    static String idView;
    static int points;
    static PowerUp powerUp;
    static Weapon weapon;
    static List<PowerUp> powerUps;
    static List<Weapon> loadedWeapons;
    static Player currentPlayer;
    @BeforeAll
    static void setup() {
        firstView = new View();
        username = "Noobmaster69";
        board = BoardCreator.parseBoard("board1.btlb",8);
        players = Arrays.asList(
                new Player("Noobmaster69"),
                new Player("Korg"),
                new Player("Thor")
        );
        for (Player p: players) {
            p.setTile(board.getTile(2,1));
        }
        idView = "test";
        points = 0;
        powerUp = board.drawPowerUp();
        powerUps = Arrays.asList(powerUp);
        weapon = board.drawWeapon();
        loadedWeapons = Arrays.asList(weapon);
        currentPlayer = players.get(0);
        TotalUpdate update = new TotalUpdate(username, board, players, idView, points, powerUps, loadedWeapons, currentPlayer);
        //firstView.update(update);
    }
    /*
    @Test
    void visitTotalUpdate() {


        assertEquals(username, firstView.getUsername());
        assertEquals(players.size(), firstView.getPlayers().size());
        assertEquals(players.get(2).getColor().name(), firstView.getPlayers().get(2).getColor());
        assertEquals(1, firstView.getPlayers().get(0).getTile().getCoords().getPosx());
        assertEquals(2, firstView.getPlayers().get(0).getTile().getCoords().getPosy());
        assertEquals(idView, firstView.getIdView());
        assertEquals(points, firstView.getPoints());
        assertEquals(1, firstView.getPowerUps().size());
        assertEquals(powerUp.getDiscardAward().name(), firstView.getPowerUps().get(0).getDiscardAward());
        assertEquals(1, firstView.getLoadedWeapons().size());
        assertEquals(weapon.getName(), firstView.getLoadedWeapons().get(0));
    }*/
    //TODO add other tests
    @Test
    void visitTileUpdate() {

    }
}

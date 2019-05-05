package it.polimi.se2019.view;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.model.updatemessage.TotalUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateVisitorTest {
    ClientView firstView;

    @BeforeEach
    void setup() {
        firstView = new ClientView();
    }

    @Test
    void visitTotalUpdate() {
        String username = "Noobmaster69";
        Board board = BoardCreator.parseBoard("board1.btlb",8);
        List<Player> players = Arrays.asList(
                new Player("Noobmaster69", Color.RED),
                new Player("Korg" ,Color.YELLOW),
                new Player("Thor", Color.BLUE)
        );
        for (Player p: players) {
            p.setTile(board.getTile(2,1));
        }
        String idView = "test";
        int points = 0;
        PowerUp powerup = board.drawPowerUp();
        List<PowerUp> powerUps = Arrays.asList(powerup);
        Weapon weapon = board.drawWeapon();
        List<Weapon> loadedWeapons = Arrays.asList(weapon);
        TotalUpdate update = new TotalUpdate(username, board, players, idView, points, powerUps, loadedWeapons);
        firstView.update(update);

        assertEquals(username, firstView.getUsername());
        assertEquals(players.size(), firstView.getPlayers().size());
        assertEquals(players.get(2).getColor().name(), firstView.getPlayers().get(2).getColor());
        assertEquals(1, firstView.getPlayers().get(0).getTile().getCoords().getPosx());
        assertEquals(2, firstView.getPlayers().get(0).getTile().getCoords().getPosy());
        assertEquals(idView, firstView.getIdView());
        assertEquals(points, firstView.getPoints());
        assertEquals(1, firstView.getPowerUps().size());
        assertEquals(powerup.getDiscardAward().name(), firstView.getPowerUps().get(0).getDiscardAward());
        assertEquals(1, firstView.getLoadedWeapons().size());
        assertEquals(weapon.getName(), firstView.getLoadedWeapons().get(0));
    }
    //TODO add other tests
}

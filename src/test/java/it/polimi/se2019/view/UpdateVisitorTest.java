package it.polimi.se2019.view;
import it.polimi.se2019.controller.updatemessage.PopupMessageUpdate;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.controller.updatemessage.TotalUpdate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateVisitorTest {
    private static View firstView;
    private static String username;
    private static Board board;
    private static Tile perspective;
    private static List<Player> players;
    private static String idView;
    private static int points;
    private static PowerUp powerUp;
    private static Weapon weapon;
    private static ArrayList<PowerUp> powerUps;
    private static ArrayList<Weapon> loadedWeapons;
    private static Player currentPlayer;
    private static UpdateVisitor viewVisitor;
    @BeforeAll
    static void setup() {
        firstView = new View();
        username = "Noobmaster69";
        board = BoardCreator.parseBoard("board3.btlb",8);
        perspective = board.getTile(2, 1);
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
        powerUps = new ArrayList<>(Collections.singletonList(powerUp));
        weapon = board.drawWeapon();
        loadedWeapons = new ArrayList<>(Collections.singletonList(weapon));
        currentPlayer = players.get(0);
        viewVisitor = new UpdateVisitor(firstView.getReceiver());
    }

    @Test
    void visitTotalUpdate() {
        TotalUpdate update = new TotalUpdate(username, board, players, idView, points, powerUps, loadedWeapons, currentPlayer);
        viewVisitor.visit(update);
        assertEquals(username, firstView.getUsername());
        assertEquals(new ViewTile(perspective), firstView.getPerspective());
        assertEquals(players.size(), firstView.getPlayers().size());
        assertEquals(players.get(2).getUsername(), firstView.getPlayers().get(2).getUsername());
        assertEquals(1, firstView.getPlayers().get(0).getTile().getCoords().getPosx());
        assertEquals(2, firstView.getPlayers().get(0).getTile().getCoords().getPosy());
        assertEquals(idView, firstView.getIdView());
        assertEquals(points, firstView.getPoints());
        assertEquals(1, firstView.getPowerUps().size());
        assertEquals(powerUp.getDiscardAward().name(), firstView.getPowerUps().get(0).getDiscardAward());
        assertEquals(1, firstView.getLoadedWeapons().size());
        assertEquals(weapon.getName(), firstView.getLoadedWeapons().get(0).getName());
    }

    @Test
    void visitPopupMessage() {
        String message = "Drink more water";
        PopupMessageUpdate update = new PopupMessageUpdate(message);
        viewVisitor.visit(update);
        assertEquals(message, firstView.getMessages().get(firstView.getMessages().size()-1));
    }
    //TODO add other tests if other updates will be used
}

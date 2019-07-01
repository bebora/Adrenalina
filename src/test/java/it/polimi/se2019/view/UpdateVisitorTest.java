package it.polimi.se2019.view;

import it.polimi.se2019.controller.ModelToViewConverter;
import it.polimi.se2019.controller.UpdateMessageCreator;
import it.polimi.se2019.controller.updatemessage.PopupMessageUpdate;
import it.polimi.se2019.controller.updatemessage.TotalUpdate;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.cli.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateVisitorTest {
    private View firstView;
    private String username;
    private Board board;
    private Tile perspective;
    private List<Player> players;
    private String idView;
    private int points;
    private PowerUp powerUp;
    private Weapon weapon;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<Weapon> loadedWeapons;
    private Player currentPlayer;
    private UpdateVisitor viewVisitor;
    @BeforeEach
    void setup() {
        firstView = new CLI();
        firstView.setStatus(Status.END);
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
        TotalUpdate update = UpdateMessageCreator.totalUpdate(username, board, players, idView, points, powerUps, loadedWeapons, currentPlayer);
        viewVisitor.visit(update);
        assertEquals(username, firstView.getUsername());
        assertEquals(ModelToViewConverter.fromTileToViewTile(perspective), firstView.getPerspective());
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
        firstView.setStatus(Status.PLAYING);
        PopupMessageUpdate update = new PopupMessageUpdate(message);
        viewVisitor.visit(update);
        assertEquals(message, firstView.getMessages().get(firstView.getMessages().size()-1));
    }
}

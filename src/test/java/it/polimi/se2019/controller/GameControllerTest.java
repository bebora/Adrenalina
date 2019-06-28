package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.cards.PowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameControllerTest {
    List<Player> players;
    GameController gc;
    @BeforeEach
    void prepareGame() {
        players = Arrays.asList(new Player("Mega"),new Player("minx"), new Player("pyra"));
        players.forEach(p -> {
            VirtualView temp = new VirtualView();
            VirtualView vw = spy(temp);
            when(vw.isOnline()).thenReturn(true);
            RequestDispatcher rq = mock(RequestDispatcher.class);
            doNothing().when(rq).clear();
            doReturn(rq).when(vw).getRequestDispatcher();
            p.setVirtualView(vw);
        });
        //New game with only 1 skull
        GameController tempGc = new GameController(players,"board4.btlb",1,false, new LobbyController(Collections.singletonList(Mode.NORMAL)));
        gc = spy(tempGc);
        players.forEach(p -> {
                for (int i = 0; i < 3; i++ ) {
                    PowerUp powerUp = p.getMatch().getBoard().drawPowerUp();
                    p.addPowerUp(powerUp, true);
                }
        });
    }

    @Test
    public void checkEndGame() {
        players.get(0).receiveShot(players.get(1), 12, 0, true);
        doNothing().when(gc).sendWinners();
        doNothing().when(gc).startSpawning();
        assertFalse(gc.getMatch().getFinalFrenzy());
        System.out.println("Calling endTurn 1");
        assertEquals(ThreeState.FALSE, players.get(0).getAlive());
        gc.endTurn(false);
        players.get(0).setAlive(ThreeState.TRUE);
        players.get(0).setTile(gc.getMatch().getBoard().getTile(1, 1));
        //Frenzy should start and match end after
        assertTrue(gc.getMatch().getFinalFrenzy());
        verify(gc, times(0)).sendWinners();
        System.out.println("Calling endTurn 2");
        gc.endTurn(false);
        verify(gc, times(0)).sendWinners();
        System.out.println("Calling endTurn 3");
        gc.endTurn(false);
        verify(gc, times(0)).sendWinners();
        System.out.println("Calling endTurn 4");
        gc.endTurn(false);
        verify(gc, times(1)).sendWinners();
    }

    @Test
    public void spawningOfflineTest() {
        players.forEach(p -> {
            VirtualView vw = p.getVirtualView();
            when(vw.isOnline()).thenReturn(false);
        });
        players.get(0).receiveShot(players.get(1), 12, 0, true);
        assertEquals(ThreeState.FALSE, players.get(0).getAlive());
        gc.endTurn(false);
        //Check that respawn happened correctly
        assertEquals(ThreeState.TRUE, players.get(0).getAlive());
        assertEquals(3, players.get(0).getPowerUps().size());
    }
}

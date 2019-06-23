package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.view.VirtualView;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class GameControllerTest {
    @Test
    void checkEndTest() {
        //TODO fix NullPointerException in TimerConstrainedEventHandler
        List<Player> players = Arrays.asList(new Player("Mega"),new Player("minx"), new Player("pyra"));
        players.forEach(p -> {
            VirtualView temp = new VirtualView();
            VirtualView vw = spy(temp);
            when(vw.isOnline()).thenReturn(true);
            p.setVirtualView(vw);
        });
        //New game with only 1 skull
        GameController tempGc = new GameController(players,"board4.btlb",1,false, new LobbyController(Collections.singletonList(Mode.NORMAL)));
        GameController gc = spy(tempGc);
        players.get(0).receiveShot(players.get(1), 12, 0, true);
        doNothing().when(gc).sendWinners();
        doNothing().when(gc).startSpawning();
        assertFalse(gc.getMatch().getFinalFrenzy());
        System.out.println("Calling endTurn 1");
        gc.endTurn(false);
        assertEquals(ThreeState.FALSE, players.get(0).getAlive());
        players.get(0).setAlive(ThreeState.TRUE);
        players.get(0).setTile(gc.getMatch().getBoard().getTile(1, 1));
        //Frenzy should start
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
}

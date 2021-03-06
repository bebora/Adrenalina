package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.network.EventUpdaterRMI;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.cli.CLI;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LobbyControllerTest {
    /**
     * Example test on using the fake virtualview for debugging, using it for adding a player to lobbyController
     */
    @Test
    void checkWaitingPlayer() {
        //Test the login of players in a lobbycontroller
        LobbyController lobbyController = new LobbyController(Arrays.asList(Mode.NORMAL));
        VirtualView vv = new VirtualView(lobbyController);
        EventUpdater eventUpdater = new EventUpdaterRMI(lobbyController);
        try {
            eventUpdater.login(vv, "fabio", "rizzo", false, "NORMAL");
            eventUpdater.login(vv, "simone", "rigoli", false, "NORMAL");
            eventUpdater.login(vv, "simona", "rizzo", false, "NORMAL");
            eventUpdater.login(vv, "simona1", "rizzo", false, "NORMAL");
            assertEquals(4,lobbyController.getWaitingPlayers().get(Mode.NORMAL).size());
            eventUpdater.login(vv, "simona2", "rizzo", false, "NORMAL");
        }
        catch (RemoteException e) {
            System.out.println(e);
        }
        Utils.waitABit();
        //Test that there is no waiting players because the match started
        assertEquals(0,lobbyController.getWaitingPlayers().get(Mode.NORMAL).size());
    }

    @Test
    void checkStartedGames() {
        LobbyController lobbyController = new LobbyController(new ArrayList<>(Collections.singleton(Mode.NORMAL)));
        List<View> views = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            EventUpdater eventUpdater = new EventUpdaterRMI(lobbyController);
            View view = new CLI();
            views.add(view);
            try {
                eventUpdater.login(view, "ciao" + i, "ciao", false, "normal");
            } catch (RemoteException e) {
                System.out.println("Error!");
            }
            Utils.waitABit();
        }
        Utils.waitABit();
        assertEquals(1, lobbyController.getGames().size());
    }
}


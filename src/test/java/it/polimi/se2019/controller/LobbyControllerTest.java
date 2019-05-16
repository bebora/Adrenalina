package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.network.EventUpdaterRMI;
import it.polimi.se2019.view.VirtualView;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class LobbyControllerTest {
    /**
     * Example test on using the fake virtualview for debugging, using it for adding a player to lobbyController
     */
    @Test
    void addView() {
        LobbyController lobbyController = new LobbyController(Arrays.asList(Mode.NORMAL));
        VirtualView vv = new VirtualView(lobbyController);
        EventUpdater eventUpdater = new EventUpdaterRMI(lobbyController);
        eventUpdater.login(vv, "fabio", "rizzo", false, "NORMAL");
    }

}
package it.polimi.se2019.network;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.model.Mode;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.cli.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RMIServerTest {
    LobbyController lobbyController;
    View view;
    @BeforeEach
    void before() {
        lobbyController = new LobbyController(Collections.singletonList(Mode.NORMAL));
        view = new CLI();

    }

    @Test
    void initizializeRMI() {
        RMIServer rmiServer = new RMIServer(lobbyController);
        rmiServer.start();
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            System.out.print("Interrupted");
        }
        EventUpdater eventUpdater = new EventUpdaterRMI("localhost", 1099);
        try {
            eventUpdater.login(view, "ciao", "ciao", false, "normal");
        }
        catch (RemoteException e){
            System.out.println("Error!");
        }
        assertEquals(1, lobbyController.getWaitingPlayers().get(Mode.NORMAL).size());
    }
}

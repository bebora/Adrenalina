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

class SocketServerTest {
    LobbyController lobbyController;
    View view;
    @BeforeEach
    void before() {
        lobbyController = new LobbyController(Collections.singletonList(Mode.NORMAL));
        view = new CLI();
    }
    @Test
    void initializeSocket() {
        //Create the socket server
        SocketServer socketServer = new SocketServer(65535, lobbyController);
        socketServer.start();
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            System.out.print("interrputed");
        }
        //Setup the event updater and login, verifying there are 1 user connected to the lobby
        EventUpdater eventUpdater = new EventUpdaterSocket("0.0.0.0", 65535);
        try {
            eventUpdater.login(view, "ciao", "ciao", false, "normal");
        }
        catch (RemoteException e){
            System.out.println("Error!");
        }try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            System.out.print("interrputed");
        }
        assertEquals(1, lobbyController.getWaitingPlayers().get(Mode.NORMAL).size());
    }
}
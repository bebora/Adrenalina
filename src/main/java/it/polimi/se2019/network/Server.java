package it.polimi.se2019.network;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.model.Mode;

import java.util.Arrays;

public class Server {
    public void main() {
        LobbyController lobbyController = new LobbyController(Arrays.asList(Mode.NORMAL, Mode.DOMINATION));
        SocketServer socketServer = new SocketServer(65535, lobbyController);
        socketServer.start();
        RMIServer rmiServer = new RMIServer(lobbyController, 1099);
        rmiServer.start();
    }
}

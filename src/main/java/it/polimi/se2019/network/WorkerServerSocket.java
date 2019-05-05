package it.polimi.se2019.network;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.VirtualView;

import java.io.ObjectInputStream;
import java.net.Socket;

public class WorkerServerSocket extends Thread {
    private Socket socket;
    private ObjectInputStream oos;
    private ObjectInputStream ois;
    private View associatedVirtualView;
    private Player player;
    private LobbyController lobbyController;

    public WorkerServerSocket(Socket socket, LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        this.socket = socket;

        //TODO check if player already in lobby, if not add
        //TODO add security measures password hashing
    }



}

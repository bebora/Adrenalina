package it.polimi.se2019.network;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.MessageHandler;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.updatemessage.UpdateVisitable;
import it.polimi.se2019.view.View;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class WorkerServerSocket extends Thread implements ServerInterface {
    private Socket socket;
    private ObjectInputStream oos;
    private ObjectInputStream ois;
    private View VirtualView;
    private Player player;
    private LobbyController lobbyController;
    private MessageHandler messageHandler;
    BlockingQueue queue = new LinkedBlockingDeque();

    public WorkerServerSocket(Socket socket, LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        this.socket = socket;
        //TODO read from socket the connection request and initizialize using lobbyController the Worker
    }


    @Override
    public void run() {
        //TODO run listener and sender
    }

    public void update(UpdateVisitable update) {
        //TODO serialize update

        /*try {
            //queue.put(serializedUpdate);
        }
        catch (InterruptedException e) {
            //TODO insert logger class to log exceptions
        }
    }*/
    }

    private class Updater extends Thread {
            @Override
            public void run() {
                //TODO take message from queue and send them using oos
            }
    }

    private class Listener extends Thread {
        @Override
        public void run() {
            //TODO Get messages, parse them and use messageHandler relative visitor
        }
    }


}

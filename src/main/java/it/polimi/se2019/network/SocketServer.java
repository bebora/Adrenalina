    package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.LobbyController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SocketServer for creating related WorkerServerSocket for each client connecting.
 */
public class SocketServer extends Thread{
    private ServerSocket serverSocket;
    private int serverPort;
    private LobbyController lobbyController;

    public SocketServer(int serverPort, LobbyController lobbyController) {
        this.serverPort = serverPort;
        this.lobbyController = lobbyController;
    }

    /**
     * Initialize the socket, accepting new connections.
     * It opens a {@link WorkerServerSocket} for every new connection.
     */
    @Override
    public void run() {
        if (!initializeSocket()) {
            return;
        }
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                Logger.log(Priority.DEBUG, "Can't open socket");
                return;
            }
            WorkerServerSocket workerServerSocket;
            try {
                workerServerSocket = new WorkerServerSocket(clientSocket, lobbyController);
                workerServerSocket.start();
            } catch (AuthenticationErrorException e) {
                Logger.log(Priority.ERROR, "Unable to authenticate");
            }
        }
    }

    public boolean initializeSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            return true;
        }
        catch (IOException e) {
            Logger.log(Priority.WARNING, String.format("Port %d cannot be open\"", this.serverPort));
            return false;
        }
    }
}

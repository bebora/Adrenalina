package it.polimi.se2019.network;

import it.polimi.se2019.controller.LobbyController;
import sun.tools.jconsole.Worker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread{
    private ServerSocket serverSocket;
    private int serverPort;
    private LobbyController lobbyController;

    public SocketServer(int serverPort, LobbyController lobbyController) {
        this.serverPort = serverPort;
        this.lobbyController = lobbyController;
    }

    @Override
    public void run() {
        initializeSocket();
        Socket clientSocket = null;
        while (true) {
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException("Can't accept connection", e);
            }
            WorkerServerSocket workerServerSocket = new WorkerServerSocket(clientSocket, lobbyController);
            //TODO start thread
        }

    }

    public void initializeSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("Port %d cannot be open", this.serverPort));
        }
    }
}

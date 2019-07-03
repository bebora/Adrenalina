package it.polimi.se2019.network;

import it.polimi.se2019.GameProperties;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.model.Mode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

/**
 * Main class to start the server.
 * It runs both the RMI and Socket service.
 */
public class Server {
    public static void main(String[] args) {
        if (args.length > 0) {
            GameProperties.setDefaultPath(args[0]);
        }
        Logger.setPrioritiesLoggingToStdout(new HashSet<>(Arrays.asList(Priority.values())));
        Properties connectionProperties = new Properties();
        InputStream fin;
        try{
            fin = new FileInputStream("./connection.properties");
            connectionProperties.load(fin);
        }catch (IOException e){
            Logger.log(Priority.ERROR,e.getMessage());
            try {
                fin = Server.class.getClassLoader().getResourceAsStream("connection.properties");
                connectionProperties.load(fin);
            }
            catch (IOException ex) {
                Logger.log(Priority.ERROR, e.getMessage());
                return;
            }
        }
        int socketPort = Integer.parseInt(connectionProperties.getProperty("SocketPort"));
        int rmiPort = Integer.parseInt(connectionProperties.getProperty("RMIPort"));
        LobbyController lobbyController = new LobbyController(Arrays.asList(Mode.NORMAL, Mode.DOMINATION));
        SocketServer socketServer = new SocketServer(socketPort, lobbyController);
        socketServer.start();
        RMIServer rmiServer = new RMIServer(lobbyController, rmiPort);
        rmiServer.start();
    }
}

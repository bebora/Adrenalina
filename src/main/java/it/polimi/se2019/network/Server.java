package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.model.Mode;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

public class Server {
    public void main() {
        Properties connectionProperties = new Properties();
        FileInputStream fin;
        try{
            fin = new FileInputStream(getClass().getClassLoader().getResource("connection.properties").getPath());
            connectionProperties.load(fin);
        }catch (Exception e){
            Logger.log(Priority.ERROR,e.getMessage());
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

package it.polimi.se2019.network;

import it.polimi.se2019.controller.ConnectHandler;
import it.polimi.se2019.controller.LobbyController;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Create WorkerServerRMI and export it on the RMI registry
 */
public class RMIServer extends Thread{
    private static int defaultPort = 1099;
    private LobbyController lobbyController;
    private int port;

    public RMIServer(LobbyController lobbyController, int port) {
        this.lobbyController = lobbyController;
        this.port = port;
    }
    public RMIServer(LobbyController lobbyController) {
        this(lobbyController, defaultPort);
    }

    @Override
    public void run() {
        try {
            LocateRegistry.createRegistry(port);
        }
        catch (RemoteException e) {
            //TODO log remote exception
        }
        try {
            ConnectHandler connectHandler = new ConnectHandler(lobbyController);
            Naming.rebind("//localhost/AdrenalineServer", connectHandler);
        }
        catch (RemoteException e) {
            //TODO log remote exception
        }
        catch (MalformedURLException e) {
            //TODO log malformed url
        }
    }

}

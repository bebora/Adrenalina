package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.LobbyController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Creates WorkerServerRMI and export it on the RMI registry.
 * It bind the {@link ConnectHandler} to the registry, allowing clients to send logins.
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
            Registry registry = LocateRegistry.createRegistry(port);
            ConnectHandler connectHandler = new ConnectHandler(lobbyController);
            Logger.log(Priority.DEBUG, "Created registry on port "+port);
            registry.rebind("AdrenalineServer", connectHandler);
        }
        catch (RemoteException e) {
            Logger.log(Priority.ERROR, "Could not bind connectHandler to RMI registry due to RemoteException: " + e.getMessage());
        }
    }

}

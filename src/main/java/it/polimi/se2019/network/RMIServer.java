package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
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
            Logger.log(Priority.ERROR, "Could not create create RMI registry due to RemoteException");
        }
        try {
            ConnectHandler connectHandler = new ConnectHandler(lobbyController);
            Naming.rebind("//localhost/AdrenalineServer", connectHandler);
        }
        catch (RemoteException e) {
            Logger.log(Priority.ERROR, "Could not bind connectHandler to RMI registry due to RemoteException");
        }
        catch (MalformedURLException e) {
            Logger.log(Priority.ERROR, "Could not bind connectHandler to RMI registry due to MalformedURLException");
        }
    }

}

package it.polimi.se2019.network;

import it.polimi.se2019.controller.LobbyController;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Create WorkerServerRMI and export it on the RMI registry
 */
public class RMIServer {
    private static int defaultPort = 1099;
    private LobbyController lobbyController;

    public RMIServer(LobbyController lobbyController, int port) {
        try {
            LocateRegistry.createRegistry(port);
        }
        catch (RemoteException e) {
            //TODO log remote exception
        }
        try {
            WorkerServerRMI serverWorker = new WorkerServerRMI(lobbyController);
            Naming.rebind("//localhost/AdrenalineServer", serverWorker);
        }
        catch (RemoteException e) {
            //TODO log remote exception
        }
        catch (MalformedURLException e) {
            //TODO log malformed url
        }
    }
    public RMIServer(LobbyController lobbyController) {
        this(lobbyController, defaultPort);
    }

}

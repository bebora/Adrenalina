package it.polimi.se2019.network;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    private static int defaultPort = 1099;

    public RMIServer(int port) {
        try {
            LocateRegistry.createRegistry(port);
        }
        catch (RemoteException e) {
            //TODO log remote exception
        }
        try {
            RMIServerWorker serverWorker = new RMIServerWorker();
            Naming.rebind("//localhost/AdrenalineServer", serverWorker);
        }
        catch (RemoteException e) {
            //TODO log remote exception
        }
        catch (MalformedURLException e) {
            //TODO log malformed url
        }
    }
    public RMIServer() {
        this(defaultPort);
    }

}

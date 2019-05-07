package it.polimi.se2019.network;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerWorker extends UnicastRemoteObject {
    public RMIServerWorker() throws RemoteException {
        super();
    }
}

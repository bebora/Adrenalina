package it.polimi.se2019.network;

import it.polimi.se2019.controller.LobbyController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class WorkerServerRMI extends UnicastRemoteObject{
    private LobbyController lobbyController;
    /**
     * Interface that will receive updates from the WorkerServer and
     * apply them to its view
     */
    private ViewReceiverInterface viewReceiver;

    public WorkerServerRMI(LobbyController lobbyController) throws RemoteException {
        super();
        this.lobbyController = lobbyController;
    }
    //TODO save receiver in list of receivers
}

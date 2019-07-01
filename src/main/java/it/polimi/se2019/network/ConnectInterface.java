package it.polimi.se2019.network;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface exposed by the RMI server to the client so that it can request to connect
 */
public interface ConnectInterface extends Remote, Serializable {

    /**
     * Handles connection from client to servers
     * Creates a virtualView, set the parameters and uses the {@link #lobbyController} methods to connect
     * @param username chosen from the client
     * @param hashedPassword chosen from the client, saved in the server hashed
     * @param existingGame whether the client is reconnecting
     * @param mode the mode chosen by the client
     * @param receiver client to interface used for callbacks
     * @throws RemoteException
     */
    void connect(String username, String hashedPassword, boolean existingGame, String mode, ViewReceiverInterface receiver) throws RemoteException;

    RequestDispatcherInterface getRequestHandler(String username, String password) throws RemoteException;
}

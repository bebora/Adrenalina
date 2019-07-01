package it.polimi.se2019.network;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface exposed by the RMI server to the client so that it can request to connect
 */
public interface ConnectInterface extends Remote, Serializable {
    void connect(String username, String hashedPassword, boolean existingGame, String mode, ViewReceiverInterface receiver) throws RemoteException;
    RequestDispatcherInterface getRequestHandler(String username, String password) throws RemoteException;
}

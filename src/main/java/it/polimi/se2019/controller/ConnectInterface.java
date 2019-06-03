package it.polimi.se2019.controller;

import it.polimi.se2019.network.ViewReceiverInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface exposed by the RMI server to the client so that it can request to connect
 */
public interface ConnectInterface extends Remote, Serializable {
    void connect(String username, String hashedPassword, boolean existingGame, String mode, ViewReceiverInterface receiver) throws RemoteException;
    RequestDispatcher getRequestHandler(String username, String password) throws RemoteException;
}

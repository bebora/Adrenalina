package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.RequestDispatcher;
import it.polimi.se2019.controller.VirtualView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static it.polimi.se2019.Priority.DEBUG;

/**
 * Handler exposed by the RMI server to the client so that it can request to connect
 */
public class ConnectHandler extends UnicastRemoteObject implements ConnectInterface {
    private transient LobbyController lobbyController;

    @Override
    public void connect(String username, String password, boolean existingGame, String mode, ViewReceiverInterface receiver) throws RemoteException{
        VirtualView virtualView = new VirtualView(lobbyController);
        virtualView.setUsername(username);
        ViewUpdaterRMI updater = new ViewUpdaterRMI(receiver, virtualView);
        virtualView.setViewUpdater(updater, existingGame);
        Logger.log(DEBUG, String.format("%s is trying to connect via RMI for a %s match", username, mode));
        if (!existingGame)
            lobbyController.connectPlayer(username, password, mode, virtualView);
        else
            lobbyController.reconnectPlayer(username, password, virtualView);
        updater.getPinger().start();
    }

    /**
     * Handles serving the related object that every client uses to communicate with the server
     * Parse the {@link #lobbyController} searching for the related user.
     * @param username login username used when signing up for a game
     * @param password password used for signing up for a game
     * @return the RequestDispatcher object needed for the client to send events.
     * @throws RemoteException
     */
    @Override
    public RequestDispatcher getRequestHandler(String username, String password) throws RemoteException {
        String token = String.format("%s$%s", username, password.hashCode());
        return lobbyController.getRequestHandler(token);
    }

    public ConnectHandler(LobbyController lobbyController) throws RemoteException {
        this.lobbyController = lobbyController;
        try {
            Logger.log(Priority.DEBUG, "Server hostname: "+InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            Logger.log(Priority.DEBUG, e.getMessage());
        }
    }
}

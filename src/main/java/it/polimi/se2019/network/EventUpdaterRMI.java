package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.*;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * View use this to send events to server
 */
//TODO check if logging is the best way to handle RemoteException
public class EventUpdaterRMI implements EventUpdater{
    /**
     * ConnectInterface used to establish the connection with the remote server
     */
    private ConnectInterface connectInterface;
    /**
     * remote RequestDispatcher on which methods will be called
     */
    private RequestDispatcherInterface remoteHandler;

    @Override
    public void sendStop() {
        Runnable task = () -> {
            try {
                remoteHandler.receiveStopAction();
            } catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send stop!");
            }
        };
        new Thread(task).start();
    }

    //TODO force login first
    @Override
    public void login(View view, String nickname, String password, boolean existingGame, String mode)  throws RemoteException{
        connectInterface.connect(nickname, password, existingGame, mode, view.getReceiver());
        remoteHandler = connectInterface.getRequestHandler(nickname, password);
        //TODO Manage when login doesn't go as expected!
    }
    public EventUpdaterRMI(String url, int port) {
        //TODO use port
        try {
            connectInterface = (ConnectInterface) Naming.lookup("//" + url + "/AdrenalineServer");
        }
        catch (RemoteException e) {
            Logger.log(Priority.ERROR, "Adrenaline server not found due to RemoteException");
        }
        catch (NotBoundException e) {
            Logger.log(Priority.ERROR, "Adrenaline server not found due to NotBoundException");
        }
        catch (MalformedURLException e) {
            Logger.log(Priority.ERROR, "Adrenaline server URL is not valid");
        }
    }

    /**
     * Constructor used for debug purposes, view is local
     */
    public EventUpdaterRMI(LobbyController lobbyController) {
        try {
            connectInterface = new ConnectHandler(lobbyController);
        }
        catch (RemoteException e) {
            assert false;
        }
    }

    @Override
    public void sendAction(String action) {
        Runnable task = () -> {
            try {
                remoteHandler.receiveAction(action);
            } catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send action");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendDirection(String direction) {
        Runnable task = () -> {
            try {
                remoteHandler.receiveDirection(direction);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send effect");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendEffect(String effect) {
        Runnable task = () -> {
            try {
                remoteHandler.receiveEffect(effect);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send effect");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendPlayers(List<String> players) {
        Runnable task = () -> {
            try {
                remoteHandler.receivePlayers(new ArrayList<>(players));
            }
            catch (RemoteException e){
                Logger.log(Priority.ERROR, "Failed to send player");
            }
        };
        new Thread(task).start();
    }

    //TODO NEED TO SEE IF NEEDS TO BE ALWAYS A LIST OR SINGLE
    @Override
    public void sendPowerUp(List<ViewPowerUp> viewPowerUps, boolean discard) {
        Runnable task = () -> {
            try {
                remoteHandler.receivePowerUps(new ArrayList<>(viewPowerUps), discard);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send powerup");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendRoom(String room) {
        Runnable task = () -> {
            try {
                remoteHandler.receiveRoom(room);
            }
            catch (RemoteException e){
                Logger.log(Priority.ERROR, "Failed to send room");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendTiles(List<ViewTileCoords> tiles) {
        Runnable task = () -> {
            try {
                remoteHandler.receiveTiles(new ArrayList<>(tiles));
            }
            catch (RemoteException e){
                Logger.log(Priority.ERROR, "Failed to send tiles");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendWeapon(String weapon) {
        Runnable task = () -> {
            try {
                remoteHandler.receiveWeapon(weapon);
            }
            catch (RemoteException e){
                Logger.log(Priority.ERROR, "Failed to send weapon");
            }
        };
        new Thread(task).start();
    }
}

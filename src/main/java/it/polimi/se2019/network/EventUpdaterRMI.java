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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Implements the event Updater, allowing a client using RMI to send event to server
 */
public class EventUpdaterRMI implements EventUpdater{
    /**
     * ConnectInterface used to establish the connection with the remote server
     */
    private ConnectInterface connectInterface;

    /**
     * remote RequestDispatcher on which methods will be called
     */
    private RequestDispatcherInterface remoteHandler;

    /**
     * executor that will send events sequentially
     */
    private ThreadPoolExecutor eventExecutor;

    /**
     * executor that will send acks back to server
     */
    private ThreadPoolExecutor ackExecutor;

    @Override
    public void sendStop() {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receiveStopAction();
            } catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send stop!");
            }
        });
    }

    @Override
    public boolean login(View view, String nickname, String password, boolean existingGame, String mode)  throws RemoteException{
        if (connectInterface == null) return false;
        connectInterface.connect(nickname, password, existingGame, mode, view.getReceiver());
        remoteHandler = connectInterface.getRequestHandler(nickname, password);
        return true;
    }

    public EventUpdaterRMI(String url, int port) {
        this.eventExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        this.ackExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        try {
            connectInterface = (ConnectInterface) Naming.lookup(String.format("//%s:%d/AdrenalineServer", url, port));
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
        this.eventExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        this.ackExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        try {
            connectInterface = new ConnectHandler(lobbyController);
        }
        catch (RemoteException e) {
            assert false;
        }
    }

    @Override
    public void sendAck() {
        ackExecutor.submit(() -> {
            try {
                remoteHandler.receiveAck();
            } catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send ack");
            }
        });
    }

    @Override
    public void sendAction(String action) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receiveAction(action);
            } catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send action");
            }
        });
    }

    @Override
    public void sendDirection(String direction) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receiveDirection(direction);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send effect");
            }
        });
    }

    @Override
    public void sendEffect(String effect) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receiveEffect(effect);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send effect");
            }
        });
    }

    @Override
    public void sendPlayers(List<String> players) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receivePlayers(new ArrayList<>(players));
            }
            catch (RemoteException e){
                Logger.log(Priority.ERROR, "Failed to send player");
            }
        });
    }

    @Override
    public void sendAmmo(String ammo) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receiveAmmo(ammo);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send ammo");
            }
        });
    }

    @Override
    public void sendPowerUp(List<ViewPowerUp> viewPowerUps, boolean discard) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receivePowerUps(new ArrayList<>(viewPowerUps));
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Failed to send powerup");
            }
        });
    }

    @Override
    public void sendRoom(String room) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receiveRoom(room);
            }
            catch (RemoteException e){
                Logger.log(Priority.ERROR, "Failed to send room");
            }
        });
    }

    @Override
    public void sendTiles(List<ViewTileCoords> tiles) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receiveTiles(new ArrayList<>(tiles));
            }
            catch (RemoteException e){
                Logger.log(Priority.ERROR, "Failed to send tiles");
            }
        });
    }

    @Override
    public void sendWeapon(String weapon) {
        eventExecutor.submit(() -> {
            try {
                remoteHandler.receiveWeapon(weapon);
            }
            catch (RemoteException e){
                Logger.log(Priority.ERROR, "Failed to send weapon");
            }
        });
    }
}

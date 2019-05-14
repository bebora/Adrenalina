package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.ConnectInterface;
import it.polimi.se2019.controller.RequestDispatcher;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewTileCoords;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * View use this to send events to server
 */
public class EventUpdaterRMI implements EventUpdater{
    /**
     * ConnectInterface used to establish the connection with the remote server
     */
    private ConnectInterface connectInterface;
    /**
     * remote RequestDispatcher on which methods will be called
     */
    private RequestDispatcher remoteHandler;
    //TODO force login first
    @Override
    public void login(View view, String nickname, String password, boolean existingGame, String mode) {
        //TODO export receiver as remote object
        connectInterface.connect(nickname, password, existingGame, mode, view.getReceiver());
        remoteHandler = connectInterface.getRequestHandler();
    }
    EventUpdaterRMI(String url, int port) {
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

    @Override
    public void sendAction(String action) {
        try {
            remoteHandler.receiveAction(action);
        }
        catch (RemoteException e) {
            Logger.log(Priority.ERROR, "Failed to send action");
        }

    }

    @Override
    public void sendChoice(String choice) {
        try {
            remoteHandler.receiveChoice(choice);
        }
        catch (RemoteException e){
            Logger.log(Priority.ERROR, "Failed to send choice");
        }
    }

    @Override
    public void sendPlayers(List<String> players) {
        try {
            remoteHandler.receivePlayers(players);
        }
        catch (RemoteException e){
            Logger.log(Priority.ERROR, "Failed to send player");
        }
    }

    @Override
    public void sendPowerUp(String powerUp) {
        //TODO RequestDispatcher does not have any receivePowerup method at the moment
    }

    @Override
    public void sendRoom(String room) {
        try {
            remoteHandler.receiveRoom(room);
        }
        catch (RemoteException e){
            Logger.log(Priority.ERROR, "Failed to send room");
        }
    }

    @Override
    public void sendTiles(List<ViewTileCoords> tiles) {
        try {
            remoteHandler.receiveTiles(tiles);
        }
        catch (RemoteException e){
            Logger.log(Priority.ERROR, "Failed to send tiles");
        }
    }

    @Override
    public void sendWeapon(String weapon) {
        try {
            remoteHandler.receiveWeapon(weapon);
        }
        catch (RemoteException e){
            Logger.log(Priority.ERROR, "Failed to send weapon");
        }
    }
}

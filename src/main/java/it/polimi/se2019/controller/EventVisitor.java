package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.events.*;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor used by socket server implementation that apply events received on a RequestDispatcher
 */
public class EventVisitor {
    RequestDispatcher requestHandler;
    LobbyController lobbyController;

    public EventVisitor(RequestDispatcher requestHandler, LobbyController lobbyController) {
        this.requestHandler = requestHandler;
        this.lobbyController = lobbyController;
    }

    public void visit(ConnectionRequest event) {
        throw new UnsupportedOperationException();
    }

    public void visit(SelectAction event) {
        String selectedAction = event.getSelectedAction();
        try {
            requestHandler.receiveAction(selectedAction);
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }

    public void visit(SelectDirection event) {
        String direction = event.getDirection();
        try {
            requestHandler.receiveDirection(direction);
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }

    public void visit(SelectEffect event) {
        String effect = event.getEffect();
        try {
            requestHandler.receiveEffect(effect);
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }

    public void visit(SelectPlayers event) {
        List<String> playerIds = event.getPlayerIds();
        try {
            requestHandler.receivePlayers(new ArrayList<>(playerIds));
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }

    public void visit(SelectPowerUps selectPowerUps) {
        List<ViewPowerUp> powerups = selectPowerUps.getPowerUps();
        boolean discard = selectPowerUps.isDiscarded();
        try {
            requestHandler.receivePowerUps(new ArrayList<>(powerups), discard);
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }
    public void visit(SelectRoom selectRoom) {
        String room = selectRoom.getRoom();
        try {
            requestHandler.receiveRoom(room);
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }

    public void visit(SelectStop selectStop) {
        try {
            requestHandler.receiveStopAction(selectStop.isRevertAction());
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }

    public void visit(SelectTiles event){
        List<ViewTileCoords> viewTiles = event.getSelectedTiles();
        try {
            requestHandler.receiveTiles(new ArrayList<>(viewTiles));
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }

    public void visit(SelectWeapon event){
        String weapon = event.getWeapon();
        try {
            //ADD powerups discarding option
            requestHandler.receiveWeapon(weapon);
        }
        catch (RemoteException e) {
            Logger.log(Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }
}

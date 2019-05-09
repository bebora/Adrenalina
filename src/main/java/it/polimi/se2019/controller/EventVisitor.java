package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.Side;
import it.polimi.se2019.controller.events.*;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.List;

public class EventVisitor {
    RequestHandler requestHandler;
    public void visit(SelectPlayers event) {
        List<String> playerIds = event.getPlayerIds();
        try {
            requestHandler.receivePlayers(playerIds);
        }
        catch (RemoteException e) {
            Logger.log(Side.SERVER, Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }
    public void visit(SelectAction event) {
        String selectedAction = event.getSelectedAction();
        try {
            requestHandler.receiveAction(selectedAction);
        }
        catch (RemoteException e) {
            Logger.log(Side.SERVER, Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }
    public void visit(SelectWeapon event){
        String weapon = event.getWeapon();
        try {
            //ADD powerups discarding option
            requestHandler.receiveWeapon(weapon);
        }
        catch (RemoteException e) {
            Logger.log(Side.SERVER, Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }
    public void visit(SelectTiles event){
        List<ViewTileCoords> viewTiles = event.getSelectedTiles();
        try {
            requestHandler.receiveTiles(viewTiles);
        }
        catch (RemoteException e) {
            Logger.log(Side.SERVER, Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }


    public void visit(ConnectionRequest event) {
        String username = event.getUsername();
        String password = event.getPassword();
        boolean signingUp = event.getExistingGame();
        String mode = event.getMode();
        try {
            requestHandler.receiveConnection(username, password, signingUp, mode);
        }
        catch (RemoteException e) {
            Logger.log(Side.SERVER, Priority.WARNING, "Connection failed for " + e.getMessage());
        }
    }

}

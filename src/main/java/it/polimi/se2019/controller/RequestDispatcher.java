package it.polimi.se2019.controller;

import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.List;

public class RequestDispatcher implements RequestDispatcherInterface{
    EventHelper eventHelper;
    LobbyController lobbyController;

    @Override
    public void receiveRoom(String room) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    @Override
    public void receivePlayers(List<String> players) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    @Override
    public void receiveAction(String subAction) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    @Override
    public void receiveTiles(List<ViewTileCoords> viewTiles) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    @Override
    public void receiveWeapon(String weapon) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    @Override
    public void receiveDiscardPowerUps(List<ViewPowerUp> powerUps) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    @Override
    public void receiveConnection(String username, String password, Boolean signingUp, String mode) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    @Override
    public void receiveEffect(String effect) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    @Override
    public void receiveChoice(String choice) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }

    public void setLobbyController(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }

    public void setEventHelper(EventHelper eventHelper) {
        this.eventHelper = eventHelper;
    }
}



package it.polimi.se2019.controller;

import it.polimi.se2019.network.ViewUpdater;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestDispatcher implements RequestDispatcherInterface{
    EventHelper eventHelper;
    LobbyController lobbyController;
    ViewUpdater viewUpdater;
    Map<ReceivingType, EventHandler> observerTypes;

    public RequestDispatcher(ViewUpdater viewUpdater) {
        observerTypes = new HashMap<>();
        this.viewUpdater = viewUpdater;
    }

    public Map<ReceivingType, EventHandler> getObserverTypes() {
        return observerTypes;
    }

    @Override
    public synchronized void receiveRoom(String room) throws RemoteException {
        //TODO room
    }
    @Override
    public synchronized void receivePlayers(List<String> players) throws RemoteException {

    }
    @Override
    public synchronized void receiveAction(String subAction) throws RemoteException {
        //TODO popup to view saying it's wrong!!
    }
    @Override
    public synchronized void receiveTiles(List<ViewTileCoords> viewTiles) throws RemoteException {
        //TODO popup to view saying it's wrong!!
    }
    @Override
    public synchronized void receiveWeapon(String weapon) throws RemoteException {
        //TODO popup to view saying it's wrong!!
    }
    @Override
    public synchronized void receiveDiscardPowerUps(List<ViewPowerUp> powerUps) throws RemoteException {
        //TODO popup to view saying it's wrong!!
    }

    @Override
    public synchronized void receiveEffect(String effect) throws RemoteException {
        //TODO popup to view saying it's wrong!!
    }
    @Override
    public synchronized void receiveChoice(String choice) throws RemoteException {
        //TODO popup to view saying it's wrong!!
    }

    public synchronized void setLobbyController(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }

    public synchronized void setEventHelper(EventHelper eventHelper) {
        this.eventHelper = eventHelper;
    }

    public synchronized void addReceivingType(List<ReceivingType> receivingTypes, EventHandler eventHandler) {
        for (ReceivingType receivingType : receivingTypes) {
            observerTypes.put(receivingType, eventHandler);
        }
        //TODO UPDATE VIEW
    }

    public synchronized void removeReceivingType(List<ReceivingType> receivingTypes) {
        observerTypes.remove(receivingTypes);
        //TODO UPDATE VIEW
    }




}



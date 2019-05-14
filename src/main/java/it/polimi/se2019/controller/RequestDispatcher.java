package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;
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
        Color relatedRoom;
        try {
            if (observerTypes.keySet().contains(ReceivingType.ROOM)) {
                relatedRoom = eventHelper.getRoomFromString(room);
                EventHandler eventHandler = observerTypes.get(ReceivingType.ROOM);
                eventHandler.receiveRoom(relatedRoom);
            }
            else {
                throw new IncorrectEvent("Non posso accettare ROOM!");
            }
        }
        catch (IncorrectEvent e) {
            //TODO SEND UPDATE WRONG MESSAGE USING VIEWUPDATER
        }
    }
    @Override
    public synchronized void receivePlayers(List<String> players) throws RemoteException {
        try {
            if (observerTypes.keySet().contains(ReceivingType.PLAYERS)) {
                List<Player> relatedPlayers = eventHelper.getPlayersFromId(players);
                EventHandler eventHandler = observerTypes.get(ReceivingType.PLAYERS);
                eventHandler.receivePlayer(relatedPlayers);
            }
            else {
                throw new IncorrectEvent("Non posso accettare ROOM!");
            }
        }
        catch (IncorrectEvent e) {
            //TODO SEND UPDATE WRONG MESSAGE
        }
    }
    @Override
    public synchronized void receiveAction(String subAction) throws RemoteException {
        try {
            if (observerTypes.keySet().contains(ReceivingType.PLAYERS)) {
                Action action = eventHelper.getActionFromString(subAction);
                EventHandler eventHandler = observerTypes.get(ReceivingType.ACTION);
                eventHandler.receiveAction(action);
            } else {
                throw new IncorrectEvent("Non posso accettare ROOM!");
            }
        }
        catch (IncorrectEvent e) {
            //TODO SEND UPDATE WRONG MESSAGE!
        }
    }
    @Override
    public synchronized void receiveTiles(List<ViewTileCoords> viewTiles) throws RemoteException {
        try {
            if (observerTypes.keySet().contains(ReceivingType.TILES)) {
                List<Tile> tiles = eventHelper.getTilesFromViewTiles(viewTiles);
                EventHandler eventHandler = observerTypes.get(ReceivingType.TILES);
                eventHandler.receiveTiles(tiles);
            }
            else
                throw new IncorrectEvent("Non posso accettare TILES!");
        }
        catch (IncorrectEvent e) {
            //TODO SEND UPDATE WRONG MESSAGE!
        }
    }
    @Override
    public synchronized void receiveWeapon(String weapon) throws RemoteException {
        try {
            if (observerTypes.keySet().contains(ReceivingType.WEAPON)) {
                Weapon relatedWeapon = eventHelper.getWeaponFromString(weapon);
                EventHandler eventHandler = observerTypes.get(ReceivingType.WEAPON);
                eventHandler.receiveWeapon(relatedWeapon);
            }
            else
                throw new IncorrectEvent("Non posso accettare armi!");
        }
        catch (IncorrectEvent e) {
            //TODO SEND UPDATE WRONG :P
        }
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



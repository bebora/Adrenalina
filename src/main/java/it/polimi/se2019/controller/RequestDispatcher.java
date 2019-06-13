package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.network.ViewUpdater;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dispatcher class used by the VirtualView to allow the use of a EventHandler for each Receiving type; if null, the type can't be accepted.
 * Locking on an object for fixing lack of RMI documentation on synchronization.
 */
public class RequestDispatcher extends UnicastRemoteObject implements RequestDispatcherInterface{
    private EventHelper eventHelper;
    private ViewUpdater viewUpdater;
    private Map<ReceivingType, EventHandler> observerTypes;
    final Object lock = new Object();

    public void clear() {
        synchronized (lock) {
            for (EventHandler eventHandler : observerTypes.values()) {
                eventHandler.setBlocked(true);
            }
            observerTypes.clear();
            AcceptableTypes acceptableTypes = new AcceptableTypes(new ArrayList<>());
            viewUpdater.sendAcceptableType(acceptableTypes);
        }
    }

    public void setEventHelper(Match match) {
        eventHelper = new EventHelper(match);
    }

    public RequestDispatcher(ViewUpdater viewUpdater) throws RemoteException {
        observerTypes = new EnumMap<>(ReceivingType.class);
        this.viewUpdater = viewUpdater;
    }

    public Map<ReceivingType, EventHandler> getObserverTypes() {
        return observerTypes;
    }

    @Override
    public void receiveAction(String subAction) throws RemoteException {
        synchronized (lock) {
            try {
                if (observerTypes.keySet().contains(ReceivingType.ACTION)) {
                    Action action = eventHelper.getActionFromString(subAction);
                    EventHandler eventHandler = observerTypes.get(ReceivingType.ACTION);
                    eventHandler.receiveAction(action);
                } else {
                    throw new IncorrectEvent("Non posso accettare Action!");
                }
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void receiveDirection(String direction) throws RemoteException {
        synchronized (lock) {
            try {
                if (observerTypes.keySet().contains(ReceivingType.DIRECTION)) {
                    EventHandler eventHandler = observerTypes.get(ReceivingType.DIRECTION);
                    eventHandler.receiveDirection(eventHelper.getDirectionFromString(direction));
                } else
                    throw new IncorrectEvent("Non posso accettare una direzione!");
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void receiveEffect(String effect) throws RemoteException {
        synchronized (lock) {
            try {
                if (observerTypes.keySet().contains(ReceivingType.EFFECT)) {
                    EventHandler eventHandler = observerTypes.get(ReceivingType.EFFECT);
                    eventHandler.receiveEffect(effect);
                } else
                    throw new IncorrectEvent("Non posso accettare un effetto!");
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void receivePlayers(ArrayList<String> players) throws RemoteException {
        synchronized (lock) {
            try {
                if (observerTypes.keySet().contains(ReceivingType.PLAYERS)) {
                    List<Player> relatedPlayers = eventHelper.getPlayersFromId(players);
                    EventHandler eventHandler = observerTypes.get(ReceivingType.PLAYERS);
                    eventHandler.receivePlayer(relatedPlayers);
                } else {
                    throw new IncorrectEvent("Non posso accettare ROOM!");
                }
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void receivePowerUps(ArrayList<ViewPowerUp> powerUps, boolean discard) throws RemoteException {
        synchronized (lock) {
            try {
                if (observerTypes.keySet().contains(ReceivingType.POWERUP)) {
                    List<PowerUp> relatedPowerUps = powerUps.
                            stream().
                            map(eventHelper::getPowerUpFromViewPowerUp).
                            filter(Objects::nonNull).collect(Collectors.toList());
                    EventHandler eventHandler = observerTypes.get(ReceivingType.POWERUP);
                    eventHandler.receivePowerUps(relatedPowerUps, discard);
                } else
                    throw new IncorrectEvent("Non posso accettare powerUp!");
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void receiveRoom(String room) throws RemoteException {
        synchronized (lock) {
            Color relatedRoom;
            try {
                if (observerTypes.keySet().contains(ReceivingType.ROOM)) {
                    relatedRoom = eventHelper.getRoomFromString(room);
                    EventHandler eventHandler = observerTypes.get(ReceivingType.ROOM);
                    eventHandler.receiveRoom(relatedRoom);
                } else {
                    throw new IncorrectEvent("Non posso accettare ROOM!");
                }
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void receiveStopAction() throws RemoteException {
        synchronized (lock) {
            try {
                if (observerTypes.keySet().contains(ReceivingType.STOP)) {
                    EventHandler eventHandler = observerTypes.get(ReceivingType.STOP);
                    eventHandler.receiveStop();
                } else
                    throw new IncorrectEvent("Non posso accettare uno stop!");
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void receiveTiles(ArrayList<ViewTileCoords> viewTiles) throws RemoteException {
        synchronized (lock) {
            try {
                if (observerTypes.keySet().contains(ReceivingType.TILES)) {
                    List<Tile> tiles = eventHelper.getTilesFromViewTiles(viewTiles);
                    EventHandler eventHandler = observerTypes.get(ReceivingType.TILES);
                    eventHandler.receiveTiles(tiles);
                } else
                    throw new IncorrectEvent("Non posso accettare TILES!");
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    @Override
    public void receiveWeapon(String weapon) throws RemoteException {
        synchronized (lock) {
            try {
                if (observerTypes.keySet().contains(ReceivingType.WEAPON)) {
                    Weapon relatedWeapon = eventHelper.getWeaponFromString(weapon);
                    EventHandler eventHandler = observerTypes.get(ReceivingType.WEAPON);
                    eventHandler.receiveWeapon(relatedWeapon);
                } else
                    throw new IncorrectEvent("Non posso accettare armi!");
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    public void setEventHelper(EventHelper eventHelper) {
        this.eventHelper = eventHelper;
    }

    public void addReceivingType(List<ReceivingType> receivingTypes, EventHandler eventHandler) {
        synchronized (lock) {
            for (ReceivingType receivingType : receivingTypes) {
                observerTypes.put(receivingType, eventHandler);
            }
        }
    }

    public void removeReceivingType(List<ReceivingType> receivingTypes) {
        synchronized (lock) {
            observerTypes.remove(receivingTypes);
        }
    }

    public ViewUpdater getViewUpdater() {
        return viewUpdater;
    }
}



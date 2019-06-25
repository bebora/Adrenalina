package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.network.ViewUpdater;
import it.polimi.se2019.view.View;
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
    private transient EventHelper eventHelper;
    private transient ViewUpdater viewUpdater;
    private transient Map<ReceivingType, EventHandler> observerTypes;
    private transient View linkedVirtualView;
    private transient NetworkTimeoutControllerServer networkTimeoutController;
    private transient Long lastRequest = null;
    private transient AcceptableTypes acceptableTypes;
    final transient Object lock = new Object();

    /**
     * Clear the current accepted types, sending the update to the related view.
     */
    public void clear() {
        synchronized (lock) {
            for (EventHandler eventHandler : observerTypes.values()) {
                eventHandler.setActive(true);
            }
            observerTypes.clear();
            acceptableTypes = new AcceptableTypes(new ArrayList<>());
            viewUpdater.sendAcceptableType(acceptableTypes);
        }
    }

    /**
     * Update the view with selectable options, and sets them as acceptableTypes.
     * @param acceptableTypes accepted types and options
     */
    public void updateView(AcceptableTypes acceptableTypes) {
        this.acceptableTypes = acceptableTypes;
        viewUpdater.sendAcceptableType(acceptableTypes);
    }

    /**
     * Update the view with current selectable options.
     */
    public void updateView() {
        viewUpdater.sendAcceptableType(acceptableTypes);
    }

    public void setEventHelper(Match match, Player player) {
        eventHelper = new EventHelper(match, player);
    }

    /**
     * Set a new view as the linked view in this class.
     * Restart the {@link #networkTimeoutController}
     * @param viewUpdater
     * @param linkedVirtualView
     */
    public void setView(ViewUpdater viewUpdater, View linkedVirtualView) {
        this.viewUpdater = viewUpdater;
        this.linkedVirtualView = linkedVirtualView;
        this.networkTimeoutController = new NetworkTimeoutControllerServer(this);
        this.networkTimeoutController.start();
    }

    /**
     * Sets the current view and start the {@link #networkTimeoutController}.
     * @param viewUpdater used to update the related view
     * @param linkedVirtualView linked to the current RequestDispatcher
     * @throws RemoteException
     */
    public RequestDispatcher(ViewUpdater viewUpdater, View linkedVirtualView) throws RemoteException {
        observerTypes = new EnumMap<>(ReceivingType.class);
        this.viewUpdater = viewUpdater;
        this.linkedVirtualView = linkedVirtualView;
        this.networkTimeoutController = new NetworkTimeoutControllerServer(this);
        this.networkTimeoutController.start();
    }

    public Map<ReceivingType, EventHandler> getObserverTypes() {
        return observerTypes;
    }

    /**
     * Handles receiving an ack from the client.
     * Edits the lastRequest's time.
     * @throws RemoteException
     */
    @Override
    public void receiveAck() throws RemoteException {
        synchronized (lock) {
            lastRequest = System.nanoTime();
        }
    }

    /**
     * Handles receiving an ammo from the client.
     * Checks if the ammo is currently in {@link #observerTypes}.
     * Converts the {@code ammo} to a {@link Ammo}.
     * Calls the related method on the {@link EventHandler}.
     * @param ammo
     * @throws RemoteException
     */
    @Override
    public void receiveAmmo(String ammo) throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
                Ammo relatedAmmo;
                if (observerTypes.keySet().contains(ReceivingType.AMMO)) {
                    try {
                         relatedAmmo = Ammo.valueOf(ammo);
                    }
                    catch (IllegalArgumentException e) {
                        throw new IncorrectEvent("Ammo doesn't exist!");
                    }
                    EventHandler eventHandler = observerTypes.get(ReceivingType.AMMO);
                    eventHandler.receiveAmmo(relatedAmmo);
                } else {
                    throw new IncorrectEvent("Can't accept ammo!");
                }
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    /**
     * Handles receiving an action from the client.
     * Checks if the action is currently in {@link #observerTypes}.
     * Converts the {@code subAction} to a {@link Action} using the {@link #eventHelper}.
     * Calls the related method on the {@link EventHandler}.
     * @param subAction
     * @throws RemoteException
     */
    @Override
    public void receiveAction(String subAction) throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
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

    /**
     * Handles receiving a direction from the client.
     * Checks if the direction is currently in {@link #observerTypes}.
     * Converts the {@code direction} to a {@link it.polimi.se2019.model.cards.Direction}.
     * Calls the related method on the {@link EventHandler}.
     * @param direction
     * @throws RemoteException
     */
    @Override
    public void receiveDirection(String direction) throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
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

    /**
     * Handles receiving an effect from the client.
     * Checks if the effect is currently in {@link #observerTypes}.
     * Converts the {@code effect} to a {@link it.polimi.se2019.model.cards.Effect} using the {@link #eventHelper}.
     * Calls the related method on the {@link EventHandler}.
     * @param effect
     * @throws RemoteException
     */
    @Override
    public void receiveEffect(String effect) throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
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

    /**
     * Handles receiving a list of players from the client.
     * Checks if the players are currently in {@link #observerTypes}.
     * Converts the {@code players} to a list of {@link Player} using the {@link #eventHelper}.
     * Calls the related method on the {@link EventHandler}.
     * @param players
     * @throws RemoteException
     */
    @Override
    public void receivePlayers(ArrayList<String> players) throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
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

    /**
     * Handles receiving a list of powerups from the client.
     * Checks if the powerups are currently in {@link #observerTypes}.
     * Converts the {@link ViewPowerUp} to a {@link PowerUp} using the {@link #eventHelper}.
     * Calls the related method on the {@link EventHandler}.
     * @param powerUps
     * @throws RemoteException
     */
    @Override
    public void receivePowerUps(ArrayList<ViewPowerUp> powerUps) throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
                if (observerTypes.keySet().contains(ReceivingType.POWERUP)) {
                    List<PowerUp> relatedPowerUps = powerUps.
                            stream().
                            map(eventHelper::getPowerUpFromViewPowerUp).
                            filter(Objects::nonNull).collect(Collectors.toList());
                    if (relatedPowerUps.size() == powerUps.size()) {
                        EventHandler eventHandler = observerTypes.get(ReceivingType.POWERUP);
                        eventHandler.receivePowerUps(relatedPowerUps);
                    } else throw new IncorrectEvent("Wrong powerUps!");
                } else
                    throw new IncorrectEvent("Non posso accettare powerUp!");
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    /**
     * Handles receiving a room from the client.
     * Checks if the room is currently in {@link #observerTypes}.
     * Converts the {@code room} to a {@link Color} using the {@link #eventHelper}.
     * Calls the related method on the {@link EventHandler}.
     * @param room
     * @throws RemoteException
     */
    @Override
    public void receiveRoom(String room) throws RemoteException {
        synchronized (lock) {
            Color relatedRoom;
            try {
                lastRequest = System.nanoTime();
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

    /**
     * Handles receiving a stop from the client.
     * Checks if a stop is currently in {@link #observerTypes}.
     * Calls the related method on the {@link EventHandler}.
     * @throws RemoteException
     */
    @Override
    public void receiveStopAction() throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
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

    /**
     * Handles receiving a list of tiles from the client.
     * Checks if the tiles are currently in {@link #observerTypes}.
     * Converts the {@link ViewTileCoords} to a {@link Tile} using the {@link #eventHelper}.
     * Calls the related method on the {@link EventHandler}.
     * @param viewTiles
     * @throws RemoteException
     */
    @Override
    public void receiveTiles(ArrayList<ViewTileCoords> viewTiles) throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
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

    /**
     * Handles receiving a weapon from the client.
     * Checks if the weapon is currently in {@link #observerTypes}.
     * Converts the string to a {@link Weapon} using the {@link #eventHelper}.
     * Calls the related method on the {@link EventHandler}.
     * @param weapon
     * @throws RemoteException
     */
    @Override
    public void receiveWeapon(String weapon) throws RemoteException {
        synchronized (lock) {
            try {
                lastRequest = System.nanoTime();
                if (observerTypes.keySet().contains(ReceivingType.WEAPON)) {
                    Weapon relatedWeapon = eventHelper.getWeaponFromString(weapon);
                    EventHandler eventHandler = observerTypes.get(ReceivingType.WEAPON);
                    eventHandler.receiveWeapon(relatedWeapon);
                } else
                    throw new IncorrectEvent("Can't accept weapon!");
            } catch (IncorrectEvent e) {
                viewUpdater.sendPopupMessage(e.getMessage());
            }
        }
    }

    /**
     * Add receiving types to the current accepted types, linking them to the {@code eventhandler}
     * @param receivingTypes list of accepted {@link ReceivingType}
     * @param eventHandler handler for requests related to the list sent
     */
    public void addReceivingType(List<ReceivingType> receivingTypes, EventHandler eventHandler) {
        synchronized (lock) {
            for (ReceivingType receivingType : receivingTypes) {
                observerTypes.put(receivingType, eventHandler);
            }
        }
    }

    public ViewUpdater getViewUpdater() {
        return viewUpdater;
    }

    public View getLinkedVirtualView() {
        return linkedVirtualView;
    }

    public Long getLastRequest() {
        return lastRequest;
    }
}



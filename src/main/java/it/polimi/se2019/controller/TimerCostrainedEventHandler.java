package it.polimi.se2019.controller;

import it.polimi.se2019.GameProperties;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler class for events received by the client.
 * Extends EventHandler, adding a timing functionality to limit the time the player has for a single action.
 * In the default behaviour, it notifies the observer after it ended without being blocked by the related requestDispatcher.
 */
public class TimerCostrainedEventHandler extends Thread implements EventHandler {
    private long start;
    private long time;
    private boolean active;
    private boolean blocked;
    private Observer observer;
    private RequestDispatcher requestDispatcher;
    private AcceptableTypes acceptableTypes;
    private boolean notifyOnEnd;

    public synchronized boolean  isBlocked() {
        return blocked;
    }

    /**
     * Constructor used to resume a Timer after it has been blocked.
     * @param timerCostrainedEventHandler blocked timer, that contains the elapsed after after its start.
     */
    public TimerCostrainedEventHandler(TimerCostrainedEventHandler timerCostrainedEventHandler) {
        time = Integer.parseInt(GameProperties.getInstance().getProperty("time")) - (System.currentTimeMillis() - start);
        active = true;
        this.observer = timerCostrainedEventHandler.observer;
        this.requestDispatcher = timerCostrainedEventHandler.requestDispatcher;
        this.acceptableTypes = timerCostrainedEventHandler.acceptableTypes;
        this.blocked = false;
        this.notifyOnEnd = timerCostrainedEventHandler.notifyOnEnd;
    }

    public TimerCostrainedEventHandler(Observer observer, RequestDispatcher requestDispatcher, AcceptableTypes acceptableTypes) {
        time = Integer.parseInt(GameProperties.getInstance().getProperty("time"));
        active = true;
        this.observer = observer;
        this.requestDispatcher = requestDispatcher;
        this.acceptableTypes = acceptableTypes;
        this.blocked = false;
        this.notifyOnEnd = true;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setNotifyOnEnd(boolean notifyOnEnd) {
        this.notifyOnEnd = notifyOnEnd;
    }

    /**
     * End handler, blocking it and stopping it to notify the observer.
     */
    public void endHandler() {
        blocked = true;
        active = false;
        requestDispatcher.clear();
    }

    /**
     * Check if the timer is already finished.
     * If finished:
     * <li>Sets active to false, to avoid further requests to pass through and stop running the Thread..</li>
     * <li>Clear the options showed to the view</li>
     * <li>Notifies all the players of the lack of response from the player</li>
     * @return
     */
    public synchronized boolean checkFinished() {
        if (System.currentTimeMillis() >= start + time) {
            active = false;
            requestDispatcher.clear();
            String nick = requestDispatcher.getLinkedVirtualView().getUsername();
            try {
                List<Player> players = observer.getMatch().getPlayers().stream().filter(Player::getOnline).collect(Collectors.toList());
                players.forEach(p -> {
                    if (!p.getUsername().equals(nick))
                        p.getVirtualView().getViewUpdater().sendPopupMessage(String.format("Player %s didn't answer in time!", nick));
                    else
                        p.getVirtualView().getViewUpdater().sendPopupMessage("WAKEUP!");
                });
            }
            catch (NullPointerException e) {
                Logger.log(Priority.WARNING, "Null pointer into eventhandler match notify");
            }
            return true;
        }
        return false;
    }


    public synchronized void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * Check if it needs to notify to the {@link #observer} the end of the method with a {@link #observer#receiveStop}.
     * @return
     */
    public synchronized boolean checkIfNotify() {
        return !blocked && notifyOnEnd;
    }
    @Override
    public void run() {
        requestDispatcher.addReceivingType(acceptableTypes.getAcceptedTypes(), this);
        requestDispatcher.updateView(acceptableTypes);
        this.start = System.currentTimeMillis();
        Logger.log(Priority.DEBUG, String.format("Started event regarding %s for player %s - time remaining: %d", acceptableTypes.getAcceptedTypes(), requestDispatcher.getLinkedVirtualView().getUsername(), time));
        while (!blocked) {
            if (checkFinished())
                break;
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Logger.log(Priority.WARNING, "Sleep interrupted");
            }
        }
        Logger.log(Priority.DEBUG,String.format("Ended event regarding %s for player %s", acceptableTypes.getAcceptedTypes(), requestDispatcher.getLinkedVirtualView().getUsername()));
        if (checkIfNotify()) {
            observer.updateOnStopSelection(ThreeState.TRUE);
        }
    }

    @Override
    public synchronized void receiveAction(Action action) {
        if (active && acceptableTypes.getSelectableActions().checkForCoherency(Collections.singletonList(action))) {
            endHandler();
            Runnable task = () -> observer.updateOnAction(action);
            new Thread(task).start();
        }
    }

    @Override
    public void receiveDirection(Direction direction) {
        if (active && acceptableTypes.getSelectableDirections().checkForCoherency(Collections.singletonList(direction))) {
            endHandler();
            Runnable task = () -> observer.updateOnDirection(direction);
            new Thread(task).start();

        }
    }

    @Override
    public synchronized void receiveEffect(String effect) {
        if (active && acceptableTypes.getSelectableEffects().checkForCoherency(Collections.singletonList(effect))) {
            endHandler();
            Runnable task = () -> observer.updateOnEffect(effect);
            new Thread(task).start();
        }
    }

    @Override
    public synchronized void receivePlayer(List<Player> players) {
        if (active && acceptableTypes.getSelectablePlayers().checkForCoherency(players)) {
            endHandler();
            Runnable task = () -> observer.updateOnPlayers(players);
            new Thread(task).start();
        }
    }

    @Override
    public synchronized void receivePowerUps(List<PowerUp> powerUps) {
        if (active && acceptableTypes.getSelectablePowerUps().checkForCoherency(powerUps)) {
            endHandler();
            Runnable task = () -> observer.updateOnPowerUps(powerUps);
            new Thread(task).start();
        }
    }

    @Override
    public synchronized void receiveRoom(Color color) {
        if (active && acceptableTypes.getSelectableRooms().checkForCoherency(Collections.singletonList(color))) {
            endHandler();
            Runnable task = () -> observer.updateOnRoom(color);
            new Thread(task).start();

        }
    }

    public synchronized void receiveStop() {
        if (active){
            endHandler();
            Runnable task = () -> observer.updateOnStopSelection(ThreeState.FALSE);
            new Thread(task).start();
        }
    }

    @Override
    public synchronized void receiveTiles(List<Tile> tiles) {
        if (active && acceptableTypes.getSelectableTileCoords().checkForCoherency(tiles)) {
            endHandler();
            Runnable task = () -> observer.updateOnTiles(tiles);
            new Thread(task).start();
        }
    }

    @Override
    public synchronized void receiveWeapon(Weapon weapon) {
        if (active && acceptableTypes.getSelectableWeapons().checkForCoherency(Collections.singletonList(weapon))) {
            endHandler();
            Runnable task = () -> observer.updateOnWeapon(weapon);
            new Thread(task).start();
        }
    }

    @Override
    public void receiveAmmo(Ammo ammo) {
        if (active && acceptableTypes.getSelectableAmmos().checkForCoherency(Collections.singletonList(ammo))) {
            endHandler();
            Runnable task = () -> observer.updateOnAmmo(ammo);
            new Thread(task).start();
        }
    }
}

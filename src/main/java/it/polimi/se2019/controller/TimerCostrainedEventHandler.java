package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.GameProperties;
import it.polimi.se2019.Observer;
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

    public void setNotifyOnEnd(boolean notifyOnEnd) {
        this.notifyOnEnd = notifyOnEnd;
    }

    /**
     * End handler, blocking it and stopping it to notify the observer.
     */
    public void endHandler() {
        blocked = true;
        active = false;
    }

    /**
     * Check if the timer is already finished.
     * If finished, {@link #active} is set to false, to stop the running Thread.
     * @return
     */
    public synchronized boolean checkFinished() {
        if (System.currentTimeMillis() >= start + time) {
            active = false;
            return true;
        }
        return false;
    }

    public synchronized void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public synchronized boolean checkIfNotify() {
        return !blocked && notifyOnEnd;
    }
    @Override
    public void run() {
        requestDispatcher.addReceivingType(acceptableTypes.getAcceptedTypes(), this);
        requestDispatcher.getViewUpdater().sendAcceptableType(acceptableTypes);
        this.start = System.currentTimeMillis();
        while (!blocked) {
            if (checkFinished())
                break;
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Logger.log(Priority.WARNING, "Sleep interrupted");
            }
            Logger.log(Priority.DEBUG, "Millis elapsed: " + (System.currentTimeMillis() - this.start));
        }
        Logger.log(Priority.DEBUG,"Ended event related to " + acceptableTypes.getAcceptedTypes());
        if (checkIfNotify()) {
            observer.updateOnStopSelection(ThreeState.TRUE);
        }
    }

    @Override
    public synchronized void receiveAction(Action action) {
        if (active && acceptableTypes.getSelectableActions().checkForCoherency(Collections.singletonList(action))) {
            endHandler();
            observer.updateOnAction(action);
        }
    }

    @Override
    public void receiveDirection(Direction direction) {
        if (active && acceptableTypes.getSelectableDirections().checkForCoherency(Collections.singletonList(direction))) {
            endHandler();
            observer.updateOnDirection(direction);
        }
    }

    @Override
    public synchronized void receiveEffect(String effect) {
        if (active && acceptableTypes.getSelectableEffects().checkForCoherency(Collections.singletonList(effect))) {
            endHandler();
            observer.updateOnEffect(effect);
        }
    }

    @Override
    public synchronized void receivePlayer(List<Player> players) {
        if (active && acceptableTypes.getSelectablePlayers().checkForCoherency(players)) {
            endHandler();
            observer.updateOnPlayers(players);
        }
    }

    @Override
    public synchronized void receivePowerUps(List<PowerUp> powerUps, boolean discard) {
        if (active && acceptableTypes.getSelectablePowerUps().checkForCoherency(powerUps)) {
            endHandler();
            observer.updateOnPowerUps(powerUps, discard);
        }
    }

    @Override
    public synchronized void receiveRoom(Color color) {
        if (active && acceptableTypes.getSelectableRooms().checkForCoherency(Collections.singletonList(color))) {
            endHandler();
            observer.updateOnRoom(color);
        }
    }

    public synchronized void receiveStop() {
        if (active){
            endHandler();
            observer.updateOnStopSelection(ThreeState.FALSE);
        }
    }

    @Override
    public synchronized void receiveTiles(List<Tile> tiles) {
        if (active && acceptableTypes.getSelectableTileCoords().checkForCoherency(tiles)) {
            endHandler();
            observer.updateOnTiles(tiles);
        }
    }

    @Override
    public synchronized void receiveWeapon(Weapon weapon) {
        if (active && acceptableTypes.getSelectableWeapons().checkForCoherency(Collections.singletonList(weapon))) {
            endHandler();
            observer.updateOnWeapon(weapon);
        }
    }

    @Override
    public void receiveAmmo(Ammo ammo) {
        if (active && acceptableTypes.getSelectableAmmos().checkForCoherency(Collections.singletonList(ammo))) {
            endHandler();
            observer.updateOnAmmo(ammo);
        }
    }
}

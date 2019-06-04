package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.MyProperties;
import it.polimi.se2019.Observer;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;

public class TimerCostrainedEventHandler extends Thread implements EventHandler {
    private long start;
    private int time;
    private boolean active;
    private boolean blocked;
    private Observer observer;
    private RequestDispatcher requestDispatcher;
    private AcceptableTypes acceptableTypes;

    public boolean isBlocked() {
        return blocked;
    }

    public TimerCostrainedEventHandler(Observer observer, RequestDispatcher requestDispatcher, AcceptableTypes acceptableTypes) {
        time = Integer.parseInt(MyProperties.getInstance().getProperty("time"));
        active = true;
        this.observer = observer;
        this.requestDispatcher = requestDispatcher;
        this.acceptableTypes = acceptableTypes;
        this.blocked = false;
    }

    public void endHandler() {
        active = false;
    }

    public synchronized void checkFinished() {
        if (System.currentTimeMillis() >= start + time*1000) {
            endHandler();
        }

    }

    public synchronized void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public void run() {
        requestDispatcher.addReceivingType(acceptableTypes.getAcceptedTypes(), this);
        requestDispatcher.getViewUpdater().sendAcceptableType(acceptableTypes);
        this.start = System.currentTimeMillis();
        while (active && !blocked) {
            checkFinished();
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                Logger.log(Priority.WARNING, "FINISHING SLEEP");
            }
            Logger.log(Priority.DEBUG, "Millis remaining: " + (System.currentTimeMillis() - this.start));
        }
        if (!blocked) {
            observer.updateOnStopSelection(true, true);
        }
    }

    @Override
    public synchronized void receiveAction(Action action) {
        if (active) {
            observer.updateOnAction(action);
            endHandler();
        }
    }

    @Override
    public void receiveDirection(Direction direction) {
        if (active) {
            observer.updateOnDirection(direction);
            endHandler();
        }
    }

    @Override
    public synchronized void receiveEffect(String effect) {
        if (active) {
            observer.updateOnEffect(effect);
            endHandler();
        }
    }

    @Override
    public synchronized void receivePlayer(List<Player> players) {
        if (active) {
            observer.updateOnPlayers(players);
            endHandler();
        }
    }

    @Override
    public synchronized void receivePowerUps(List<PowerUp> powerUps, boolean discard) {
        if (active) {
            observer.updateOnPowerUps(powerUps, discard);
            endHandler();
        }
    }

    @Override
    public synchronized void receiveRoom(Color color) {
        if (active) {
            observer.updateOnRoom(color);
            endHandler();
        }
    }

    public synchronized  void receiveStop(boolean reverse) {
        observer.updateOnStopSelection(reverse, false);
        endHandler();
    }

    @Override
    public synchronized void receiveTiles(List<Tile> tiles) {
        if (active) {
            observer.updateOnTiles(tiles);
            endHandler();
        }
    }

    @Override
    public synchronized void receiveWeapon(Weapon weapon) {
        if (active) {
            observer.updateOnWeapon(weapon);
            endHandler();
        }
    }
}

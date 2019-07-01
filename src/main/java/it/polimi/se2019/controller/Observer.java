package it.polimi.se2019.controller;

import it.polimi.se2019.network.events.IncorrectEventException;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;

/**
 * Generic Controller - observer class used for receiving the updates from the {@link RequestDispatcher}
 */
public abstract class Observer {
    public void updateOnTiles(List<Tile> tiles) {
        throw new IncorrectEventException("Can't accept tiles!");
    }

    public void updateOnPlayers(List<Player> players) {
        throw new IncorrectEventException("Can't accept players");
    }

    public void updateOnDirection(Direction direction) {
        throw new IncorrectEventException("Can't accept direction");
    }

    public void updateOnRoom(Color room) {
        throw new IncorrectEventException("Can't accept room");
    }

    public void updateOnEffect(String effect) {
        throw new IncorrectEventException("Can't accept effects");
    }

    public void updateOnWeapon(Weapon weapon) {
        throw new IncorrectEventException("Can't accept weapon");
    }

    public void updateOnPowerUps(List<PowerUp> powerUps) {
        throw new IncorrectEventException("Can't accept powerUps");
    }

    public void updateOnAction(Action action) {
        throw new IncorrectEventException("Can't accept action");
    }

    public void updateOnStopSelection(ThreeState skip) {
        throw new IncorrectEventException("Can't accept stop!");
    }

    public void updateOnAmmo(Ammo ammo) {
        throw new IncorrectEventException("Can't accept ammo");
    }

    public void concludePayment() {
        throw new IncorrectEventException("Can't conclude payment!");
    }

    public void updateOnConclusion() {
        throw new IncorrectEventException("Can't process!");
    }

    public abstract Match getMatch();
}

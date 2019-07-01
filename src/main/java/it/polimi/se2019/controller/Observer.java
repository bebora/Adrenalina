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

    /**
     * Receives a notification containing a list of tiles.
     * @param tiles
     */
    public void updateOnTiles(List<Tile> tiles) {
        throw new IncorrectEventException("Can't accept tiles!");
    }

    /**
     * Receives a notification containing a list of players.
     * @param players
     */
    public void updateOnPlayers(List<Player> players) {
        throw new IncorrectEventException("Can't accept players");
    }

    /**
     * Receives a notification containing a direction.
     * @param direction
     */
    public void updateOnDirection(Direction direction) {
        throw new IncorrectEventException("Can't accept direction");
    }

    /**
     * Receives a notification containing a room.
     * @param room
     */
    public void updateOnRoom(Color room) {
        throw new IncorrectEventException("Can't accept room");
    }

    /**
     * Receives a notification containing an effect.
     * @param effect
     */
    public void updateOnEffect(String effect) {
        throw new IncorrectEventException("Can't accept effects");
    }

    /**
     * Receives a notification containing a weapon.
     * @param weapon
     */
    public void updateOnWeapon(Weapon weapon) {
        throw new IncorrectEventException("Can't accept weapon");
    }

    /**
     * Receives a notification containing a list of powerups.
     * @param powerUps
     */
    public void updateOnPowerUps(List<PowerUp> powerUps) {
        throw new IncorrectEventException("Can't accept powerUps");
    }

    /**
     * Receives a notification containing an action.
     * @param action
     */
    public void updateOnAction(Action action) {
        throw new IncorrectEventException("Can't accept action");
    }

    /**
     * Receives a notification containing a stop.
     * @param skip
     */
    public void updateOnStopSelection(ThreeState skip) {
        throw new IncorrectEventException("Can't accept stop!");
    }

    /**
     * Receives a notification containing an ammo.
     * @param ammo
     */
    public void updateOnAmmo(Ammo ammo) {
        throw new IncorrectEventException("Can't accept ammo");
    }

    /**
     * Receives the notification of the conclusion of the payment from the PaymentController
     */
    public void concludePayment() {
        throw new IncorrectEventException("Can't conclude payment!");
    }

    /**
     * Receives the notification of the conclusion of the current sub-Controller computing.
     */
    public void updateOnConclusion() {
        throw new IncorrectEventException("Can't process!");
    }

    /**
     * Gets the match that is being computed.
     * @return
     */
    public abstract Match getMatch();
}

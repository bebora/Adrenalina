package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;

/**
 * Generic interface used to handle the receiving of various events from the client.
 */
public interface EventHandler {

    /**
     * Set the event handler to {@code active}.
     * @param active if true allows the handler to notify events.
     */
    void setActive(boolean active);

    /**
     * Receive an action and notify the related observer,if the handler is still active.
     * @param action
     */
    void receiveAction(Action action);

    /**
     * Receive a direction and notify the related observer,if the handler is still active.
     * @param direction
     */
    void receiveDirection(Direction direction);

    /**
     * Receive an effect and notify the related observer,if the handler is still active.
     * @param effect
     */
    void receiveEffect(String effect);

    /**
     * Receive a list of players and notify the related observer,if the handler is still active.
     * @param players
     */
    void receivePlayer(List<Player> players);

    /**
     * Receive a list of powerups and notify the related observer,if the handler is still active.
     * @param powerUps
     */
    void receivePowerUps(List<PowerUp> powerUps);

    /**
     * Receive a room and notify the related observer,if the handler is still active.
     * @param color
     */
    void receiveRoom(Color color);

    /**
     * Receive a stop and notify the related observer,if the handler is still active.
     */
    void receiveStop();

    /**
     * Receive a list of tiles and notify the related observer,if the handler is still active.
     * @param tiles
     */
    void receiveTiles(List<Tile> tiles);

    /**
     * Receive a weapon and notify the related observer,if the handler is still active.
     * @param weapon
     */
    void receiveWeapon(Weapon weapon);

    /**
     * Receive an ammo and notify the related observer,if the handler is still active.
     * @param ammo
     */
    void receiveAmmo(Ammo ammo);

}

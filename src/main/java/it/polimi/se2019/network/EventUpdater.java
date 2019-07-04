package it.polimi.se2019.network;

import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Sender interface used by the view to send chosen events to controller.
 * View does not know what methods really do, only the network implementation
 * knows what to do, transforming sendEvent to correspondent receiveEvent
 */
public interface EventUpdater {
    /**
     * Sends an ack to notifies the server of being online.
     */
    void sendAck();

    /**
     * Sends the chosen action that need to be executed.
     * @param action to send
     */
    void sendAction(String action);

    /**
     * Sends direction the player wants to target
     * @param direction to send
     */
    void sendDirection(String direction);
    /**
     * Sends effect to activate related to the current weapon
     * @param effect name of the effect of the current weapon
     */
    void sendEffect(String effect);

    /**
     * Sends a list of players that will be targets of the current effect
     * @param players chosen
     */
    void sendPlayers(List<String> players);

    /**
     * Sends a list of powerups that will be chosen.
     * @param powerUp chosen
     * @param discard @deprecated
     */
    void sendPowerUp(List<ViewPowerUp> powerUp, boolean discard);

    /**
     * Sends the chosen room for the current effect.
     * @param room to send
     */
    void sendRoom(String room);

    /**
     * Sends the chosen tiles for the current effect
     * @param tiles to send
     */
    void sendTiles(List<ViewTileCoords> tiles);

    /**
     * Sends the chosen weapon to use in a shoot action.
     * @param weapon to send
     */
    void sendWeapon(String weapon);

    /**
     * Sends a stop action, resetting or reversing the current state.
     */
    void sendStop();

    /**
     * Sends ammo used to pay a special cost.
     * @param ammo to send
     */
    void sendAmmo(String ammo);
    /**
     * Login to controller
     * Calling other methods before login may throw an exception
     * @param view view that should receive updates from the server
     * @param nickname username of the connecting player
     * @param password password of the connecting player
     * @param existingGame true if the player is reconnecting, false if the player wants to join a new match
     * @param mode game mode (normal/domination)
     * @return {@code true} if login to server has been successful
     */
    boolean login(View view,
       String nickname,
       String password,
       boolean existingGame,
       String mode) throws RemoteException;
}

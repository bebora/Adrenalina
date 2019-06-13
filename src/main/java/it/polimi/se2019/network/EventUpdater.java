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
    void sendAck();
    void sendAction(String action);

    /**
     * Send direction the player wants to target
     * @param direction
     */
    void sendDirection(String direction);
    /**
     * Send effect to activate related to the current weapon
     * @param effect name of the effect of the current weapon
     */
    void sendEffect(String effect);
    void sendPlayers(List<String> players);
    void sendPowerUp(List<ViewPowerUp> powerUp, boolean discard);
    void sendRoom(String room);
    void sendTiles(List<ViewTileCoords> tiles);
    void sendWeapon(String weapon);
    void sendStop();
    void sendAmmo(String ammo);

    /**
     * Login to controller
     * Calling other methods before login may throw an exception
     * @param view
     * @param nickname
     * @param password
     * @param existingGame true if the player is reconnecting, false if the player wants to join a new match
     * @param mode
     */
    void login(View view,
               String nickname,
               String password,
               boolean existingGame,
               String mode) throws RemoteException;
}

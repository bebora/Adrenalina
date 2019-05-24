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
    //TODO add sendDirection
    void sendAction(String action);
    /**
     * Send effect to activate related to the current weapon
     * @param effect name of the effect of the current weapon
     */
    void sendEffect(String effect);
    void sendPlayers(List<String> players);
    void sendPowerUp(List<ViewPowerUp> powerUp);
    void sendRoom(String room);
    void sendTiles(List<ViewTileCoords> tiles);
    void sendWeapon(String weapon);

    /**
     * Login to controller
     * Calling other methods before login may throw an exception
     * @param view
     * @param nickname
     * @param password
     * @param existingGame
     * @param mode
     */
    void login(View view,
               String nickname,
               String password,
               boolean existingGame,
               String mode) throws RemoteException;
}

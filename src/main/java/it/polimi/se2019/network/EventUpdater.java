package it.polimi.se2019.network;

import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.util.List;

/**
 * Sender interface used by the view to send chosen events to controller.
 * View does not know what methods really do, only the network implementation
 * knows what to do, transforming sendEvent to correspondent receiveEvent
 */
public interface EventUpdater {

    void sendTiles(List<ViewTileCoords> tiles);
    void sendRoom(String room);
    void sendPlayers(List<String> players);
    void sendAction(String action);
    void sendWeapon(String weapon);
    void sendPowerUp(List<ViewPowerUp> powerUp);

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
               String mode);
}

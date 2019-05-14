package it.polimi.se2019.network;

import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewTileCoords;

import java.util.List;

/**
 * Sender interface used by the view to send chosen events to controller.
 * View does not know what methods really do, only the network implementation
 * knows what to do, transforming sendEvent to correspondent receiveEvent
 */
public interface EventUpdater {
    //TODO will use for messages from the View to the Controller
    //TODO used for handshaking

    void sendTiles(List<ViewTileCoords> tiles);
    void sendRoom(String room);
    void sendPlayers(List<String> players);
    void sendAction(String action);
    void sendChoice(String choice);
    void sendWeapon(String weapon);
    void sendPowerUp(String powerUp);
    void login(View view,
               String nickname,
               String password,
               boolean existingGame,
               String mode);
}

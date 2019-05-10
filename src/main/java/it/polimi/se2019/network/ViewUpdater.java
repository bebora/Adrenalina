package it.polimi.se2019.network;

import it.polimi.se2019.model.board.Tile;

import java.util.List;

/**
 * Sender interface used by controller and model to send updates to views.
 */
public interface ViewUpdater {
    //TODO used from the it.polimi.se2019.model to update ALL the views or the it.polimi.se2019.view corresponding to the player for a change
    //TODO used for handshaking


    /**
     * Send to the view the update on tiles.
     * @param tiles
     */
    void sendTiles(List<Tile> tiles);
}

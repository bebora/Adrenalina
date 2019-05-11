package it.polimi.se2019.network;

import it.polimi.se2019.model.board.Tile;

import java.util.List;

/**
 * Sender class used by controller and model to send updates to a linked view via RMI.
 */
public class ViewUpdaterRMI implements ViewUpdater {
    private ViewReceiverInterface remoteReceiver;
    @Override
    public void sendTiles(List<Tile> tiles) {
        //TODO call method on remote receiver
    }

    /**
     * Constructor which takes the remote ViewReceiverInterface, which will be used to send updates to the client
     */
    public ViewUpdaterRMI(ViewReceiverInterface remoteReceiver) {
        this.remoteReceiver = remoteReceiver;
    }
}

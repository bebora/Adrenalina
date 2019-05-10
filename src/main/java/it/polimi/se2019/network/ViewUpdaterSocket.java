package it.polimi.se2019.network;

import it.polimi.se2019.model.board.Tile;

import java.util.List;

/**
 * Updater class used to send Updates from Server to Client using Socket
 */
public class ViewUpdaterSocket implements ViewUpdater{
    private WorkerServerSocket workerServerSocket;

    public ViewUpdaterSocket(WorkerServerSocket workerServerSocket) {
        this.workerServerSocket = workerServerSocket;
    }

    public void sendTiles(List<Tile> tiles) {
        //TODO CONVERT IN JSON AND SEND TO WORKERSERVERSOCKET
    }
}

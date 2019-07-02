package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.Utils;
import it.polimi.se2019.controller.VirtualView;

/**
 * Class used by server controller to send ping periodically to the client, which should reply with an ack
 */
public class RMIPinger extends Thread{
    private VirtualView view;
    private ViewUpdaterRMI viewUpdaterRMI;
    private int PING_SLEEP_DELAY = 250;
    public RMIPinger(VirtualView view) {
        this.view = view;
    }

    @Override
    public void run() {
        viewUpdaterRMI = (ViewUpdaterRMI) view.getViewUpdater();
        do {
            viewUpdaterRMI.sendPing();
            Utils.sleepABit(PING_SLEEP_DELAY);
        } while (view.isOnline());
        Logger.log(Priority.DEBUG, "PLAYER DISCONNECTED (rmi) - " + view.getUsername());
    }
}

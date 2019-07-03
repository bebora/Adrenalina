package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.Utils;
import it.polimi.se2019.view.View;

/**
 * Class used by client controller to send ack periodically to the server
 */
public class ClientAcker extends Thread {
    private View view;
    private int ACK_SLEEP_DELAY = 500;
    public ClientAcker(View linkedView) {
        this.view = linkedView;
    }

    @Override
    public void run() {
        while (!view.isOnline()) {
            Utils.sleepABit(ACK_SLEEP_DELAY);
        }
        EventUpdater eu = view.getEventUpdater();
        do {
            eu.sendAck();
            Utils.sleepABit(ACK_SLEEP_DELAY);
        } while (view.isOnline());
        Logger.log(Priority.DEBUG, "Broken connection with server");
    }
}

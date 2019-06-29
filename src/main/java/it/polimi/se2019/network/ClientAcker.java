package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.View;

/**
 * Class used by client controller to send ack periodically to the server
 */
public class ClientAcker extends Thread {
    private View view;
    private int ACK_SLEEP_DELAY = 250;
    public ClientAcker(View linkedView) {
        this.view = linkedView;
    }

    @Override
    public void run() {
        while (!view.isOnline()) {
            try {
                Thread.sleep(ACK_SLEEP_DELAY);
            }
            catch (InterruptedException e) {
                Logger.log(Priority.ERROR, "InterruptedException in ClientAcker: "+e.getMessage());
            }
        }
        EventUpdater eu = view.getEventUpdater();
        do {
            eu.sendAck();
            try {
                Thread.sleep(ACK_SLEEP_DELAY);
            } catch (InterruptedException e) {
                Logger.log(Priority.ERROR, "Interrupted by " + e.getMessage());
            }
        } while (view.isOnline());
        Logger.log(Priority.DEBUG, "Broken connection with server");
    }
}

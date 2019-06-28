package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.Status;
import it.polimi.se2019.view.View;

/**
 * Utility class to expect an ack in return from the server, every {@link #timeout}.
 * If not found, it disconnects the view.
 */
public class NetworkTimeoutControllerClient extends Thread{
    private View view;
    private long lastRequest;
    private boolean checkingTimeout = true;
    int timeout = 5000;
    public NetworkTimeoutControllerClient(View linkedView) {
        this.view = linkedView;
    }

    @Override
    public void run() {

        do {
            try {
                Thread.sleep(timeout);
                long viewLastRequest = view.getLastRequest();
                if (viewLastRequest == lastRequest || view.getStatus() == Status.END) {
                    view.disconnect();
                    checkingTimeout = false;
                }
                else {
                    lastRequest = viewLastRequest;
                }
            } catch (InterruptedException e) {
                Logger.log(Priority.ERROR, "Interrupted by " + e.getMessage());
            }
        } while (checkingTimeout);
    }
}

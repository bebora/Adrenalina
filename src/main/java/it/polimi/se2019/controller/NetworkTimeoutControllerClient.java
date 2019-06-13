package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.View;

public class NetworkTimeoutControllerClient extends Thread{
    private View view;
    private long lastRequest;
    private boolean checkingTimeout = true;
    public NetworkTimeoutControllerClient(View linkedView) {
        this.view = linkedView;
    }

    @Override
    public void run() {
        int timeout = 2000;
        do {
            try {
                Thread.sleep(timeout);
                long viewLastRequest = view.getLastRequest();
                if (viewLastRequest == lastRequest) {
                    Logger.log(Priority.DEBUG, String.format("Did not receive any ping in %dms, assuming disconnected from server", timeout));
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

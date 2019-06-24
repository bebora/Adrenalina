package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;

public class NetworkTimeoutControllerServer extends Thread{
    private long lastRequest;
    private boolean checkingTimeout = true;
    private RequestDispatcher requestDispatcher;
    public NetworkTimeoutControllerServer(RequestDispatcher requestDispatcher){
        this.requestDispatcher = requestDispatcher;
    }
    @Override
    public void run() {
        int timeout = 500000000;
        do {
            try {
                Thread.sleep(timeout);
                Long dispatcherLastRequest = requestDispatcher.getLastRequest();
                if(dispatcherLastRequest == null) continue;
                if (dispatcherLastRequest == lastRequest) {
                    Logger.log(Priority.DEBUG, String.format("Did not receive any ack in %dms, assuming disconnected from %s", timeout, requestDispatcher.getLinkedVirtualView().getUsername()));
                    requestDispatcher.getLinkedVirtualView().setOnline(false);
                    checkingTimeout = false;
                }
                else {
                    lastRequest = dispatcherLastRequest;
                }
            } catch (InterruptedException e) {
                Logger.log(Priority.ERROR, "Interrupted by " + e.getMessage());
            }
        } while (checkingTimeout);
    }
}

package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.VirtualView;

public class RMIPinger extends Thread{
    private VirtualView view;
    private ViewUpdaterRMI viewUpdaterRMI;
    /**
     * Interface that will receive updates from the WorkerServer and
     * apply them to its view
     */
    private ViewReceiverInterface viewReceiver;

    public RMIPinger(VirtualView view) {
        this.view = view;
        viewUpdaterRMI = (ViewUpdaterRMI) view.getViewUpdater();
    }

    public void run() {
        do {
            viewUpdaterRMI.sendPopupMessage("ping");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Logger.log(Priority.ERROR, "Interrupted by " + e.getMessage());
            }
        } while (view.isOnline());
    }
}

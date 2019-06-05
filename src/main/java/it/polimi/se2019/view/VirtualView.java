package it.polimi.se2019.view;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.RequestDispatcher;
import it.polimi.se2019.network.ViewUpdater;

import java.rmi.RemoteException;

/**
 * Virtual view used by the model and server to:
 * <li>Send updates to the client,using {@link #viewUpdater}</li>
 * <li>Receive and compute events from the client, using {@link #requestDispatcher}</li>
 * Every client has its own VirtualView
 */
public class VirtualView extends View  {
    private LobbyController lobbyController;
    private ViewUpdater viewUpdater;
    private RequestDispatcher requestDispatcher;

    public VirtualView (LobbyController lobbyController) {
        super();
        this.lobbyController = lobbyController;
    }

    public VirtualView() {
        super();
    }

    /**
     * Set the viewUpdater, and create the related RequestDispatcher that will use the {@link #viewUpdater} to answer
     * //TODO @simone is really necessary to propagate the IncorrectEvent in {@link #requestDispatcher} like it's currently being done or viewUpdates could be done at a lower level? think and fix
     */
    public void setViewUpdater(ViewUpdater viewUpdater) {
        this.viewUpdater = viewUpdater;
        try {
            this.requestDispatcher = new RequestDispatcher(viewUpdater);
        }
        catch (RemoteException e) {
            Logger.log(Priority.ERROR, "Unexpected RemoteException while creating RequestDispatcher");
        }
    }

    @Override
    public void setOnline(boolean online) {
        super.setOnline(online);
        if (!online)
            getGameController().checkEnd();
    }

    public ViewUpdater getViewUpdater() {
        return viewUpdater;
    }

    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }
}

package it.polimi.se2019.view;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.RequestDispatcher;
import it.polimi.se2019.network.ViewUpdater;

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
        this.lobbyController = lobbyController;
    }

    /**
     * Set the viewUpdater, and create the related RequestDispatcher that will use the {@link #viewUpdater} to answer
     * //TODO @simone is really necessary to propagate the IncorrectEvent in {@link #requestDispatcher} like it's currently being done or viewUpdates could be done at a lower level? think and fix
     */
    public void setViewUpdater(ViewUpdater viewUpdater) {
        this.viewUpdater = viewUpdater;
        this.requestDispatcher = new RequestDispatcher(viewUpdater);
    }



    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }
}

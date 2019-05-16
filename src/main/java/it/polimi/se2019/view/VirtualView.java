package it.polimi.se2019.view;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.RequestDispatcher;
import it.polimi.se2019.network.ViewUpdater;


public class VirtualView extends View  {
    private LobbyController lobbyController;
    private ViewUpdater viewUpdater;
    private RequestDispatcher requestDispatcher;


    public VirtualView (LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }


    public RequestDispatcher getRequestHandler() {
        return requestDispatcher;
    }

    public void setViewUpdater(ViewUpdater viewUpdater) {
        this.viewUpdater = viewUpdater;
        requestDispatcher = new RequestDispatcher(viewUpdater);
    }
}

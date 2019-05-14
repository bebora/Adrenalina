package it.polimi.se2019.view;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.RequestDispatcher;
import it.polimi.se2019.network.ViewUpdater;


public class VirtualView extends View  {
    /**
     * Debug view used to test events without the server
     */
    private View debugView = null;
    private LobbyController lobbyController;
    private ViewUpdater viewUpdater;
    private RequestDispatcher requestDispatcher;


    public VirtualView (LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }


    /**
     * View used to debug without server
     * @param debugView client view
     */
    public VirtualView (View debugView) {
        this.debugView = debugView;
    }


    public RequestDispatcher getRequestHandler() {
        return requestDispatcher;
    }

    public void setViewUpdater(ViewUpdater viewUpdater) {
        this.viewUpdater = viewUpdater;
        requestDispatcher = new RequestDispatcher(viewUpdater);
    }


}

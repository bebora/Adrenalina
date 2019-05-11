package it.polimi.se2019.view;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.RequestHandler;
import it.polimi.se2019.network.ViewUpdater;


public class VirtualView extends View  {
    /**
     * Debug view used to test events without the server
     */
    private View debugView = null;
    private LobbyController lobbyController;
    private ViewUpdater viewUpdater;
    private RequestHandler requestHandler;


    public VirtualView (LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        requestHandler = new RequestHandler(lobbyController);
    }


    /**
     * View used to debug without server
     * @param debugView client view
     */
    public VirtualView (View debugView) {
        this.debugView = debugView;
    }


    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void setViewUpdater(ViewUpdater viewUpdater) {
        this.viewUpdater = viewUpdater;
    }
}

package it.polimi.se2019.view;

import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.RequestHandler;
import it.polimi.se2019.model.updatemessage.UpdateVisitable;


public class VirtualView extends View  {
    /**
     * Debug view used to test events without the server
     */
    private View debugView = null;
    private LobbyController lobbyController;
    //TODO INSERT SENDER INTERFACE
    private RequestHandler requestHandler;


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

    //TODO Observer class, the controller update the model, the model update the VirtualView passing a message with info on update needed, and the virtualview using the server interface updates the corresponding views

    /**
     * //TODO used by model to update, passing updates to do to the virtualview
     * @param u
     */
    public void update(UpdateVisitable u) {
        if (debugView != null) {
            debugView.update(u);
        }
    }

}

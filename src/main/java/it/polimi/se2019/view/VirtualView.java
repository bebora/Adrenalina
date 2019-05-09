package it.polimi.se2019.view;

import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.model.updatemessage.UpdateVisitable;
import it.polimi.se2019.network.ServerInterface;

public class VirtualView extends View  {
    private transient ServerInterface virtualClient;
    /**
     * Debug view used to test events without the server
     */
    private View debugView = null;
    private LobbyController lobbyController;
    private EventVisitor eventVisitor;

    public VirtualView (ServerInterface virtualClient, LobbyController lobbyController) {
        this.virtualClient = virtualClient;
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
        if (virtualClient != null) {
            virtualClient.update(u);
        }
        else if (debugView != null) {
            debugView.update(u);
        }
    }

}

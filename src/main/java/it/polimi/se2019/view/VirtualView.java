package it.polimi.se2019.view;

import it.polimi.se2019.model.updatemessage.UpdateVisitable;
import it.polimi.se2019.network.ServerInterface;

public class VirtualView extends View  {
    private transient ServerInterface virtualClient;
    /**
     * Debug view used to test events without the server
     */
    private View debugView = null;
    private final String username;
    private final String encodedPassword;

    public VirtualView (ServerInterface virtualClient, String username, String encodedPassword) {
        this.username = username;
        this.encodedPassword = encodedPassword;
        this.virtualClient = virtualClient;
    }

    public VirtualView (View debugView, String username, String encodedPassword) {
        this.username = username;
        this.encodedPassword = encodedPassword;
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

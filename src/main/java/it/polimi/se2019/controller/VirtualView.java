package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.network.ViewUpdater;
import it.polimi.se2019.view.View;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Virtual view used by the model and server to:
 * <li>Send updates to the client,using {@link #viewUpdater}</li>
 * <li>Receive and compute events from the client, using {@link #requestDispatcher}</li>
 * Every client has its own VirtualView
 */
public class VirtualView extends View {
    private LobbyController lobbyController;
    private ViewUpdater viewUpdater;
    private RequestDispatcher requestDispatcher;
    private GameController gameController;

    public VirtualView (LobbyController lobbyController) {
        super();
        this.lobbyController = lobbyController;
    }


    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public VirtualView() {
        super();
    }

    public VirtualView(VirtualView virtualView) {
        this.requestDispatcher = virtualView.getRequestDispatcher();
    }

    /**
     * Set the viewUpdater.
     * Create the related RequestDispatcher that will use the {@link #viewUpdater} to answer
     */
    public void setViewUpdater(ViewUpdater viewUpdater, boolean reconnection) {
        this.viewUpdater = viewUpdater;
        if (!reconnection) {
            try {
                this.requestDispatcher = new RequestDispatcher(viewUpdater, this);
            } catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unexpected RemoteException while creating RequestDispatcher");
            }
        }
    }


    /**
     * Set the view to {@code online}
     * @param online if false, it notifies the gameController.
     */
    @Override
    public void setOnline(boolean online) {
        super.setOnline(online);
        if (!online && getGameController() != null) {
            getGameController().checkEnd(getUsername());
        }
    }

    public ViewUpdater getViewUpdater() {
        return viewUpdater;
    }

    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }

    /**
     * It sets the requestDispatcher, setting a related view.
     * @param requestDispatcher that need to be updated with the new view
     */
    public void setRequestDispatcher(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
        requestDispatcher.setView(viewUpdater, this);
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void printWinners(List<String> winners) {
        throw new UnsupportedOperationException();
    }
}

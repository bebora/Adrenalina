package it.polimi.se2019.network;

import it.polimi.se2019.controller.ConnectInterface;
import it.polimi.se2019.controller.RequestHandler;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewTileCoords;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * View use this to send events to server
 */
public class EventUpdaterRMI implements EventUpdater{
    /**
     * ConnectInterface used to establish the connection with the remote server
     */
    private ConnectInterface connectInterface;
    /**
     * remote RequestHandler on which methods will be called
     */
    private RequestHandler remoteHandler;

    @Override
    public void login(View view, String nickname, String password, boolean existingGame, String mode) {
        //TODO export receiver as remote object
        connectInterface.connect(nickname, password, existingGame, mode, view.getReceiver());
        remoteHandler = connectInterface.getRequestHandler();
    }
    EventUpdaterRMI(String url, int port) {
        //TODO use port
        try {
            connectInterface = (ConnectInterface) Naming.lookup("//" + url + "/AdrenalineServer");
        }
        catch (RemoteException e) {
            //TODO log remote exception
        }
        catch (NotBoundException e) {
            //TODO log not bound exception
        }
        catch (MalformedURLException e) {
            //TODO log malformed url exception
        }
    }

    @Override
    public void sendAction(String action) {
        //TODO implement
    }

    @Override
    public void sendChoice(String choice) {
        //TODO implement
    }

    @Override
    public void sendPlayers(List<String> players) {
        //TODO implement
    }

    @Override
    public void sendPowerUp(String powerUp) {
        //TODO implement
    }

    @Override
    public void sendRoom(String string) {
        //TODO implement
    }

    @Override
    public void sendTiles(List<ViewTileCoords> tiles) {
        //TODO implement
    }

    @Override
    public void sendWeapon(String weapon) {
        //TODO implement
    }
}

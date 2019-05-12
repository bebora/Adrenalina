package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.List;

//TODO create RequestHandlerInterface so that remote RMI client does not have access to lobbyController and observer
public class RequestHandler {
    Observer observer;
    LobbyController lobbyController;

    public RequestHandler(LobbyController lobbyController) {
        observer = null;
        this.lobbyController = lobbyController;
    }

    void receiveRoom(String room) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }

    void receivePlayers(List<String> players) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }

    void receiveAction(String subAction) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }

    void receiveTiles(List<ViewTileCoords> viewTiles) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    void receiveWeapon(String weapon, ViewPowerUp... powerUps) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    void receiveDiscardPowerUps(List<ViewPowerUp> powerUps) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }
    void receiveConnection(String username, String password, Boolean signingUp, String mode) throws RemoteException {
        throw new IllegalArgumentException("WRONG METHOD");
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    public void setLobbyController(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }
}

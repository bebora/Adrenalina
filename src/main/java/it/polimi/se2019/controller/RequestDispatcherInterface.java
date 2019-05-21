package it.polimi.se2019.controller;

import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface used by RMI
 */
public interface RequestDispatcherInterface {
    void receiveRoom(String room) throws RemoteException ;

    void receivePlayers(List<String> players) throws RemoteException;

    void receiveAction(String subAction) throws RemoteException;

    void receiveTiles(List<ViewTileCoords> viewTiles) throws RemoteException;

    void receiveWeapon(String weapon) throws RemoteException;

    void receivePowerUps(List<ViewPowerUp> powerUps, boolean discarded) throws RemoteException;

    void receiveEffect(String effect) throws RemoteException;

    void receiveStopAction(boolean reverse) throws RemoteException;
}

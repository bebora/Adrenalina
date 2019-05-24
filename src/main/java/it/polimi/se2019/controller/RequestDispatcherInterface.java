package it.polimi.se2019.controller;

import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface used by RMI
 */
public interface RequestDispatcherInterface {

    void receiveAction(String subAction) throws RemoteException;

    void receiveDirection(String direction) throws RemoteException;

    void receiveEffect(String effect) throws RemoteException;

    void receivePlayers(List<String> players) throws RemoteException;

    void receivePowerUps(List<ViewPowerUp> powerUps, boolean discarded) throws RemoteException;

    void receiveRoom(String room) throws RemoteException ;

    void receiveStopAction(boolean reverse) throws RemoteException;

    void receiveTiles(List<ViewTileCoords> viewTiles) throws RemoteException;

    void receiveWeapon(String weapon) throws RemoteException;

}

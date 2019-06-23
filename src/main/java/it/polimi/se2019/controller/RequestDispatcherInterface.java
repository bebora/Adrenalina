package it.polimi.se2019.controller;

import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface used by RMI server to receive updates
 */
public interface RequestDispatcherInterface extends Remote, Serializable {

    void receiveAck() throws RemoteException;

    void receiveAction(String subAction) throws RemoteException;

    void receiveAmmo(String ammo) throws RemoteException;

    void receiveDirection(String direction) throws RemoteException;

    void receiveEffect(String effect) throws RemoteException;

    void receivePlayers(ArrayList<String> players) throws RemoteException;

    void receivePowerUps(ArrayList<ViewPowerUp> powerUps) throws RemoteException;

    void receiveRoom(String room) throws RemoteException ;

    void receiveStopAction() throws RemoteException;

    void receiveTiles(ArrayList<ViewTileCoords> viewTiles) throws RemoteException;

    void receiveWeapon(String weapon) throws RemoteException;

}

package it.polimi.se2019.controller;

import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.List;

public interface RequestDispatcherInterface {
    void receiveRoom(String room) throws RemoteException ;

    void receivePlayers(List<String> players) throws RemoteException;

    void receiveAction(String subAction) throws RemoteException;

    void receiveTiles(List<ViewTileCoords> viewTiles) throws RemoteException;

    void receiveWeapon(String weapon, ViewPowerUp... powerUps) throws RemoteException;

    void receiveDiscardPowerUps(List<ViewPowerUp> powerUps) throws RemoteException;

    void receiveConnection(String username, String password, Boolean signingUp, String mode) throws RemoteException;

    void receiveEffect(String effect) throws RemoteException;

    void receiveChoice(String choice) throws RemoteException;
}
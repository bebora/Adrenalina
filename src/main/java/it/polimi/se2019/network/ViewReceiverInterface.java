package it.polimi.se2019.network;

import it.polimi.se2019.view.*;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface for the view that will receive updates from controller/model
 */
public interface ViewReceiverInterface extends Remote, Serializable {
    void receiveAmmosTaken(String playerId, ArrayList<String> playerAmmos) throws RemoteException;
    void receiveAttackPlayer(String attackerId, String receiverId, int damageAmount, int marksAmount) throws RemoteException;
    void receiveMovePlayer(String playerId, ViewTileCoords coords) throws RemoteException;
    void receivePopupMessage(String message) throws RemoteException;
    void receiveSelectablesWrapper(SelectableOptionsWrapper selectableOptionsWrapper) throws RemoteException;
    void receiveTile(ViewTile tile) throws RemoteException;
    void receiveTotalUpdate(String username, ViewBoard board, ViewTileCoords perspective,
                            ArrayList<ViewPlayer> players, String idView, int points,
                            ArrayList<ViewPowerUp> powerUps, ArrayList<ViewWeapon> loadedWeapons, String currentPlayerId) throws RemoteException;
    void receiveWeaponTaken(ViewWeapon takenWeapon, ViewWeapon discardedWeapon, String playerId) throws RemoteException;
    void receivePing() throws RemoteException;
}

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
    void receiveAvailableActions(String playerId, ArrayList<ViewAction> actions) throws RemoteException;
    void receiveCurrentOptions(ArrayList<String> options) throws RemoteException;
    void receiveMovePlayer(String playerId, ViewTileCoords coords) throws RemoteException;
    void receivePopupMessage(String message) throws RemoteException;
    void receiveSelectablesWrapper(SelectableOptionsWrapper selectableOptionsWrapper) throws RemoteException;
    void receiveSuccessConnection(String token) throws RemoteException;
    void receiveTile(ViewTile tile) throws RemoteException;
    void receiveTotalUpdate(String username, ViewBoard board, ViewTileCoords perspective,
                            ArrayList<ViewPlayer> players, String idView, int points,
                            ArrayList<ViewPowerUp> powerUps, ArrayList<String> loadedWeapons, String currentPlayerId) throws RemoteException;
    void receiveWeaponTaken(String takenWeapon, String discardedWeapon, String playerId) throws RemoteException;
    void receivePing() throws RemoteException;
}

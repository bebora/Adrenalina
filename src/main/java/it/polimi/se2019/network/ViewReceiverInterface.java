package it.polimi.se2019.network;

import it.polimi.se2019.view.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Interface for the view that will receive updates from controller/model
 */
public interface ViewReceiverInterface extends Serializable {
    void receiveAmmosTaken(String playerId, ArrayList<String> playerAmmos);
    void receiveAttackPlayer(String attackerId, String receiverId, int damageAmount, int marksAmount);
    void receiveAvailableActions(String playerId, ArrayList<ViewAction> actions);
    void receiveCurrentOptions(ArrayList<String> options);
    void receiveMovePlayer(String playerId, ViewTileCoords coords);
    void receivePopupMessage(String message);
    void receiveSelectablesWrapper(SelectableOptionsWrapper selectableOptionsWrapper);
    void receiveSuccessConnection(String token);
    void receiveTile(ViewTile tile);
    void receiveTotalUpdate(String username, ViewBoard board, ViewTileCoords perspective,
                             ArrayList<ViewPlayer> players, String idView, int points,
                             ArrayList<ViewPowerUp> powerUps, ArrayList<String> loadedWeapons);
    void receiveWeaponTaken(String takenWeapon, String discardedWeapon, String playerId);
}

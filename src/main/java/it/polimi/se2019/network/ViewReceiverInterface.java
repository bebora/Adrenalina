package it.polimi.se2019.network;

import it.polimi.se2019.view.*;

import java.util.List;

/**
 * Interface that will receive updates from remote controller/model and
 * apply them to the linkedView
 */
//TODO move update docs here and remove updates
public interface ViewReceiverInterface {
    void receiveAmmosTaken(String playerId, List<String> playerAmmos, List<String> newTileAmmos);
    void receiveAttackPlayer(String attackerId, String receiverId, int damageAmount, int marksAmount);
    void receiveAvailableActions(String playerId, List<ViewAction> actions);
    void receiveMovePlayer(String playerId, ViewTileCoords coords);
    void receivePopupMessage(String message);
    void receiveSelectFromPlayers(List<String> players, int minPlayers, int maxPlayers);
    void receiveSelectFromRooms(List<String> rooms);
    void receiveSelectFromTiles(List<ViewTileCoords> coords, int minPlayers, int maxPlayers);
    void receiveSuccessConnection(String token);
    void receiveTiles(List<ViewTile> tile);
    void receiveTotalUpdate(String username, ViewBoard board, ViewTileCoords perspective,
                             List<ViewPlayer> players, String idView, int points,
                             List<ViewPowerUp> powerUps, List<String> loadedWeapons);
    void receiveWeaponTaken(String takenWeapon, String discardedWeapon, String playerId);
}

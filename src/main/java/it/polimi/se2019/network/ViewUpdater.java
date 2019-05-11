package it.polimi.se2019.network;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.*;

import java.util.List;

/**
 * Sender interface used by controller and model to send updates to a view.
 */
public interface ViewUpdater {
    /**
     * Expect to have the new ammos already in player
     * @param player
     */
    void sendAmmosTaken(Player player);
    void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount);
    void sendAvailableActions(Player player);
    void sendMovePlayer(Player player);
    void sendPopupMessage(String message);
    void sendSelectFromPlayers(List<Player> players, int minPlayers, int maxPlayers);
    void sendSelectFromRooms(List<Color> rooms);
    void sendSelectFromTiles(List<Tile> tiles, int minTiles, int maxTiles);
    void sendSuccessConnection(String token);
    void sendTile(Tile tile);
    void sendTotalUpdate(String username, Board board, List<Player> players,
                         String idView, int points, List<PowerUp> powerUps,
                         List<Weapon> loadedWeapons);
    void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player);
}

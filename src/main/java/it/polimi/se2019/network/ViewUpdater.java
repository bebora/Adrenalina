package it.polimi.se2019.network;

import it.polimi.se2019.controller.AcceptableTypes;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;

/**
 * Sender interface used by controller and model to send updates to a view.
 */
public interface ViewUpdater {

    void sendPing();
    /**
     * Expect to have the new ammos already in player
     * @param player
     */
    void sendAmmosTaken(Player player);
    void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount);
    void sendAvailableActions(Player player);
    void sendCurrentOptions(List<String> options);
    void sendMovePlayer(Player player);
    void sendPopupMessage(String message);
    void sendAcceptableType(AcceptableTypes acceptableTypes);
    void sendTile(Tile tile);
    void sendTotalUpdate(String username, Board board, List<Player> players,
                         String idView, int points, List<PowerUp> powerUps,
                         List<Weapon> loadedWeapons, Player currentPlayer);
    void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player);
}

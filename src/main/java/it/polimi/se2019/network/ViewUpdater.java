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
    /**
     * Send ping to communicate to the client of being alive.
     */
    void sendPing();
    void sendAmmosTaken(Player player);
    void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount);
    void sendMovePlayer(Player player);
    /**
     * Send popup message to the client, communicating an information about the game.
     * @param message
     */
    void sendPopupMessage(String message);

    /**
     * Send the current options that are acceptable to send
     * @param acceptableTypes
     */
    void sendAcceptableType(AcceptableTypes acceptableTypes);
    void sendTile(Tile tile);

    /**
     * Send a total update containing all necessary informations.
     * @param username of the player that receives the update.
     * @param board updated with latest changes
     * @param players other players in the game
     * @param points current points of the player receiving the update
     * @param powerUps current powerups of the player receiving the update
     * @param loadedWeapons current loaded weapons of the player receiving the update
     * @param currentPlayer who is the player that is having the turn
     */
    void sendTotalUpdate(String username, Board board, List<Player> players,
                         int points, List<PowerUp> powerUps,
                         List<Weapon> loadedWeapons, Player currentPlayer);

    void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player);
}

package it.polimi.se2019.network;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.model.updatemessage.*;

import java.util.List;

/**
 * Updater class used to send Updates from Server to Client using Socket
 */
public class ViewUpdaterSocket implements ViewUpdater{
    private WorkerServerSocket workerServerSocket;

    public ViewUpdaterSocket(WorkerServerSocket workerServerSocket) {
        this.workerServerSocket = workerServerSocket;
    }


    @Override
    public void sendAmmosTaken(Player player) {
        UpdateVisitable ammosTakenUpdate = new AmmosTakenUpdate(player);
        workerServerSocket.update(ammosTakenUpdate);
    }

    @Override
    public void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount) {
        UpdateVisitable attackPlayerUpdate = new AttackPlayerUpdate(attacker,receiver,damageAmount,marksAmount);
        workerServerSocket.update(attackPlayerUpdate);
    }

    @Override
    public void sendAvailableActions(Player player) {
        UpdateVisitable availableActionsUpdate = new AvailableActionsUpdate(player);
        workerServerSocket.update(availableActionsUpdate);
    }

    @Override
    public void sendCurrentOptions(List<String> options) {
        UpdateVisitable currentOptionsUpdate = new CurrentOptionsUpdate(options);
        workerServerSocket.update(currentOptionsUpdate);
    }

    @Override
    public void sendMovePlayer(Player player) {
        UpdateVisitable sendMovePlayer = new MovePlayerUpdate(player);
        workerServerSocket.update(sendMovePlayer);
    }

    @Override
    public void sendPopupMessage(String message) {
        UpdateVisitable popupMessageUpdate = new PopupMessageUpdate(message);
        workerServerSocket.update(popupMessageUpdate);
    }

    @Override
    public void sendSelectFromPlayers(List<Player> players, int minPlayers, int maxPlayers) {
        UpdateVisitable selectFromPlayersUpdate = new SelectFromPlayersUpdate(players, minPlayers, maxPlayers);
        workerServerSocket.update(selectFromPlayersUpdate);
    }

    @Override
    public void sendSelectFromRooms(List<Color> rooms) {
        UpdateVisitable selectFromRoomsUpdate = new SelectFromRoomsUpdate(rooms);
        workerServerSocket.update(selectFromRoomsUpdate);
    }

    @Override
    public void sendSelectFromTiles(List<Tile> tiles, int minTiles, int maxTiles) {
        UpdateVisitable selectFromTilesUpdate = new SelectFromTilesUpdate(tiles, minTiles, maxTiles);
        workerServerSocket.update(selectFromTilesUpdate);
    }

    @Override
    public void sendSuccessConnection(String token) {
        UpdateVisitable successConnectionUpdate = new SuccessConnectionUpdate(token);
        workerServerSocket.update(successConnectionUpdate);
    }

    @Override
    public void sendTile(Tile tile) {
        UpdateVisitable tileUpdate = new TileUpdate(tile);
        workerServerSocket.update(tileUpdate);
    }

    @Override
    public void sendTotalUpdate(String username, Board board, List<Player> players,
                                String idView, int points, List<PowerUp> powerUps,
                                List<Weapon> loadedWeapons) {
        UpdateVisitable totalUpdate = new TotalUpdate(username, board, players, idView, points, powerUps, loadedWeapons);
        workerServerSocket.update(totalUpdate);
    }

    @Override
    public void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player) {
        UpdateVisitable weaponTakenUpdate = new WeaponTakenUpdate(takenWeapon, discardedWeapon, player);
        workerServerSocket.update(weaponTakenUpdate);
    }
}

package it.polimi.se2019.network;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sender class used by controller and model to send updates to a linked view via RMI.
 * Transform sendUpdate to corresponding receiveUpdate on the view
 */
public class ViewUpdaterRMI implements ViewUpdater {
    private ViewReceiverInterface remoteReceiver;

    @Override
    public void sendAmmosTaken(Player player) {
        List<String> playerAmmos = player.getAmmos().stream().
                map(Ammo::name).
                collect(Collectors.toList());
        remoteReceiver.receiveAmmosTaken(player.getId(), playerAmmos);
    }

    @Override
    public void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount) {
        remoteReceiver.receiveAttackPlayer(attacker.getId(), receiver.getId(), damageAmount, marksAmount);
    }

    @Override
    public void sendAvailableActions(Player player) {
        List<ViewAction> actions = player.getActions().stream().
                map(ViewAction::new).
                collect(Collectors.toList());
        remoteReceiver.receiveAvailableActions(player.getId(), actions);
    }

    @Override
    public void sendCurrentOptions(List<String> options) {
        remoteReceiver.receiveCurrentOptions(options);
    }

    @Override
    public void sendMovePlayer(Player player) {
        remoteReceiver.receiveMovePlayer(player.getId(), new ViewTileCoords(player.getTile()));
    }

    @Override
    public void sendPopupMessage(String message) {
        remoteReceiver.receivePopupMessage(message);
    }

    @Override
    public void sendSelectFromPlayers(List<Player> players, int minPlayers, int maxPlayers) {
        List<String> viewPlayers = players.stream().
                map(Player::getId).
                collect(Collectors.toList());
        remoteReceiver.receiveSelectFromPlayers(viewPlayers, minPlayers, maxPlayers);
    }

    @Override
    public void sendSelectFromRooms(List<Color> rooms) {
        List<String> viewRooms = rooms.stream().
                map(Color::name).
                collect(Collectors.toList());
        remoteReceiver.receiveSelectFromRooms(viewRooms);
    }

    @Override
    public void sendSelectFromTiles(List<Tile> tiles, int minTiles, int maxTiles) {
        List<ViewTileCoords> coords = tiles.stream().
                map(ViewTileCoords::new).
                collect(Collectors.toList());
        remoteReceiver.receiveSelectFromTiles(coords, minTiles, maxTiles);
    }

    @Override
    public void sendSuccessConnection(String token) {
        remoteReceiver.receiveSuccessConnection(token);
    }

    @Override
    public void sendTile(Tile tile) {
        remoteReceiver.receiveTile(new ViewTile(tile));
    }

    @Override
    public void sendTotalUpdate(String username, Board board, List<Player> players,
                                String idView, int points, List<PowerUp> powerUps,
                                List<Weapon> loadedWeapons) {
        ViewBoard viewBoard = new ViewBoard(board);
        Player receivingPlayer = players.stream().
                filter(p-> p.getToken().equals(username)).
                findFirst().orElseThrow(()-> new InvalidUpdateException("No player has the given username"));
        ViewTileCoords perspective = new ViewTileCoords(receivingPlayer.getTile());
        List<ViewPlayer> viewPlayers = players.stream().map(ViewPlayer::new).collect(Collectors.toList());
        List<ViewPowerUp> viewPowerUps = powerUps.stream().map(ViewPowerUp::new).collect(Collectors.toList());
        List<String> viewLoadedWeapons = loadedWeapons.stream().map(Weapon::getName).collect(Collectors.toList());
        remoteReceiver.receiveTotalUpdate(username, viewBoard, perspective,
                                            viewPlayers, idView, points,
                                            viewPowerUps, viewLoadedWeapons);
    }

    @Override
    public void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player) {
        if (discardedWeapon == null)
            remoteReceiver.receiveWeaponTaken(takenWeapon.getName(), null, player.getId());
        else
            remoteReceiver.receiveWeaponTaken(takenWeapon.getName(), discardedWeapon.getName(), player.getId());
    }

    /**
     * Constructor which takes the remote ViewReceiverInterface, which will be used to send updates to the client
     */
    public ViewUpdaterRMI(ViewReceiverInterface remoteReceiver) {
        this.remoteReceiver = remoteReceiver;
    }
}

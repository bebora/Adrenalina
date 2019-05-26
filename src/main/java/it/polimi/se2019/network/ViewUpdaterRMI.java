package it.polimi.se2019.network;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.*;

import java.util.ArrayList;
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
        Runnable task = () -> {
            ArrayList<String> playerAmmos = player.getAmmos().stream().
                    map(Ammo::name).
                    collect(Collectors.toCollection(ArrayList::new));
            remoteReceiver.receiveAmmosTaken(player.getId(), playerAmmos);
        };
        new Thread(task).start();
    }

    @Override
    public void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount) {
        Runnable task = () ->
            remoteReceiver.receiveAttackPlayer(attacker.getId(), receiver.getId(), damageAmount, marksAmount);
        new Thread(task).start();
    }

    @Override
    public void sendAvailableActions(Player player) {
        Runnable task = () -> {
            ArrayList<ViewAction> actions = player.getActions().stream().
                    map(ViewAction::new).
                    collect(Collectors.toCollection(ArrayList::new));
            remoteReceiver.receiveAvailableActions(player.getId(), actions);
        };
        new Thread(task).start();
    }

    @Override
    public void sendCurrentOptions(List<String> options) {
        Runnable task = () ->
                remoteReceiver.receiveCurrentOptions(new ArrayList<>(options));
        new Thread(task).start();
    }

    @Override
    public void sendMovePlayer(Player player) {
        Runnable task = () ->
                remoteReceiver.receiveMovePlayer(player.getId(), new ViewTileCoords(player.getTile()));
        new Thread(task).start();
    }

    @Override
    public void sendPopupMessage(String message) {
        Runnable task = () ->
                remoteReceiver.receivePopupMessage(message);
        new Thread(task).start();
    }

    @Override
    public void sendSelectFromPlayers(List<Player> players, int minPlayers, int maxPlayers) {
        Runnable task = () -> {
            ArrayList<String> viewPlayers = players.stream().
                    map(Player::getId).
                    collect(Collectors.toCollection(ArrayList::new));
            remoteReceiver.receiveSelectFromPlayers(viewPlayers, minPlayers, maxPlayers);
        };
        new Thread(task).start();
    }

    @Override
    public void sendSelectFromRooms(List<Color> rooms) {
        Runnable task = () -> {
            ArrayList<String> viewRooms = rooms.stream().
                    map(Color::name).
                    collect(Collectors.toCollection(ArrayList::new));
            remoteReceiver.receiveSelectFromRooms(viewRooms);
        };
        new Thread(task).start();
    }

    @Override
    public void sendSelectFromTiles(List<Tile> tiles, int minTiles, int maxTiles) {
        Runnable task = () -> {
            ArrayList<ViewTileCoords> coords = tiles.stream().
                    map(ViewTileCoords::new).
                    collect(Collectors.toCollection(ArrayList::new));
            remoteReceiver.receiveSelectFromTiles(coords, minTiles, maxTiles);
        };
        new Thread(task).start();
    }

    @Override
    public void sendSuccessConnection(String token) {
        Runnable task = () ->
                remoteReceiver.receiveSuccessConnection(token);
        new Thread(task).start();
    }

    @Override
    public void sendTile(Tile tile) {
        Runnable task = () ->
                remoteReceiver.receiveTile(new ViewTile(tile));
        new Thread(task).start();
    }

    @Override
    public void sendTotalUpdate(String username, Board board, List<Player> players,
                                String idView, int points, List<PowerUp> powerUps,
                                List<Weapon> loadedWeapons) {
        Runnable task = () -> {
            ViewBoard viewBoard = new ViewBoard(board);
            Player receivingPlayer = players.stream().
                    filter(p-> p.getToken().equals(username)).
                    findFirst().orElseThrow(()-> new InvalidUpdateException("No player has the given username"));
            ViewTileCoords perspective = new ViewTileCoords(receivingPlayer.getTile());
            ArrayList<ViewPlayer> viewPlayers = players.stream().
                    map(ViewPlayer::new).
                    collect(Collectors.toCollection(ArrayList::new));
            ArrayList<ViewPowerUp> viewPowerUps = powerUps.stream().
                    map(ViewPowerUp::new).
                    collect(Collectors.toCollection(ArrayList::new));
            ArrayList<String> viewLoadedWeapons = loadedWeapons.stream().
                    map(Weapon::getName).
                    collect(Collectors.toCollection(ArrayList::new));
            remoteReceiver.receiveTotalUpdate(username, viewBoard, perspective,
                    viewPlayers, idView, points,
                    viewPowerUps, viewLoadedWeapons);
        };
        new Thread(task).start();
    }

    @Override
    public void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player) {
        Runnable task = () -> {
            if (discardedWeapon == null)
                remoteReceiver.receiveWeaponTaken(takenWeapon.getName(), null, player.getId());
            else
                remoteReceiver.receiveWeaponTaken(takenWeapon.getName(), discardedWeapon.getName(), player.getId());
        };
        new Thread(task).start();
    }

    /**
     * Constructor which takes the remote ViewReceiverInterface, which will be used to send updates to the client
     */
    public ViewUpdaterRMI(ViewReceiverInterface remoteReceiver) {
        this.remoteReceiver = remoteReceiver;
    }
}

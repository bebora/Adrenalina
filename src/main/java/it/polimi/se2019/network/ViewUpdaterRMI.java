package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.AcceptableTypes;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.*;

import java.rmi.RemoteException;
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
            try {
                remoteReceiver.receiveAmmosTaken(player.getId(), playerAmmos);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send ammos taken");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount) {
        Runnable task = () -> {
            try {
                remoteReceiver.receiveAttackPlayer(attacker.getId(), receiver.getId(), damageAmount, marksAmount);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send attack player");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendAvailableActions(Player player) {
        Runnable task = () -> {
            ArrayList<ViewAction> actions = player.getActions().stream().
                    map(ViewAction::new).
                    collect(Collectors.toCollection(ArrayList::new));
            try {
                remoteReceiver.receiveAvailableActions(player.getId(), actions);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send ammos taken");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendCurrentOptions(List<String> options) {
        Runnable task = () -> {
            try {
                remoteReceiver.receiveCurrentOptions(new ArrayList<>(options));
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send current options");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendMovePlayer(Player player) {
        Runnable task = () -> {
            try {
                remoteReceiver.receiveMovePlayer(player.getId(), new ViewTileCoords(player.getTile()));
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send movement");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendPopupMessage(String message) {
        Runnable task = () -> {
            try {
                remoteReceiver.receivePopupMessage(message);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send popup");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendAcceptableType(AcceptableTypes acceptableTypes) {
        Runnable task = () -> {
            try {
                remoteReceiver.receiveSelectablesWrapper(new SelectableOptionsWrapper(acceptableTypes));
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send acceptable type: " + e.getMessage());
            }
        };
        new Thread(task).start();
    }


    @Override
    public void sendTile(Tile tile) {
        Runnable task = () -> {
            try {
                remoteReceiver.receiveTile(new ViewTile(tile));
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send tile");
            }
        };
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
            try {
                remoteReceiver.receiveTotalUpdate(username, viewBoard, perspective,
                        viewPlayers, idView, points,
                        viewPowerUps, viewLoadedWeapons);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send total update");
            }
        };
        new Thread(task).start();
    }

    @Override
    public void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player) {
        Runnable task = () -> {
            try {
                if (discardedWeapon == null)
                    remoteReceiver.receiveWeaponTaken(takenWeapon.getName(), null, player.getId());
                else
                    remoteReceiver.receiveWeaponTaken(takenWeapon.getName(), discardedWeapon.getName(), player.getId());
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send weapon taken");
            }
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

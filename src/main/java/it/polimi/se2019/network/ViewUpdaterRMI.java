package it.polimi.se2019.network;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.AcceptableTypes;
import it.polimi.se2019.controller.ModelToViewConverter;
import it.polimi.se2019.controller.VirtualView;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Sender class used by controller and model to send updates to a linked view via RMI.
 * Transform sendUpdate to corresponding receiveUpdate on the view
 */
public class ViewUpdaterRMI implements ViewUpdater {
    private ViewReceiverInterface remoteReceiver;
    private View view;
    private RMIPinger pinger;
    private ThreadPoolExecutor executor;


    public void sendPing() {
        try {
            remoteReceiver.receivePing();
        }
        catch (RemoteException e) {
            Logger.log(Priority.ERROR, "Unable to Ping!");
            view.setOnline(false);
        }
    }
    @Override
    public void sendAmmosTaken(Player player) {
        executor.submit(() -> {
            ArrayList<String> playerAmmos = player.getAmmos().stream().
                    map(Ammo::name).
                    collect(Collectors.toCollection(ArrayList::new));
            try {
                remoteReceiver.receiveAmmosTaken(player.getId(), playerAmmos);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send ammos taken");
            }
        });
    }

    @Override
    public void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount) {
        executor.submit(() -> {
            try {
                remoteReceiver.receiveAttackPlayer(attacker.getId(), receiver.getId(), damageAmount, marksAmount);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send attack player");
            }
        });
    }


    @Override
    public void sendMovePlayer(Player player) {
        executor.submit(() -> {
            try {
                remoteReceiver.receiveMovePlayer(player.getId(), ModelToViewConverter.fromTileToViewTileCoords(player.getTile()));
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send movement");
                view.setOnline(false);
            }
        });
    }

    @Override
    public void sendPopupMessage(String message) {
        executor.submit(() -> {
            try {
                remoteReceiver.receivePopupMessage(message);
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send popup "+ e.getMessage());
            }
        });
    }

    /**
     * Send accepted options to the client.
     * The method is synchronized to avoid coherency errors in sending acceptable types.
     * @param acceptableTypes accepted options that can be selected
     */
    @Override
    public synchronized void sendAcceptableType(AcceptableTypes acceptableTypes) {
        executor.submit(() -> {
            try {
                remoteReceiver.receiveSelectablesWrapper(ModelToViewConverter.fromAcceptableTypes(acceptableTypes));
            } catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send acceptable type: " + e.getMessage());
            }
        });
    }


    @Override
    public void sendTile(Tile tile) {
        executor.submit(() -> {
            try {
                remoteReceiver.receiveTile(ModelToViewConverter.fromTileToViewTile(tile));
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send tile");
            }
        });
    }

    @Override
    public void sendTotalUpdate(String username, Board board, List<Player> players,
                                int points, List<PowerUp> powerUps,
                                List<Weapon> loadedWeapons, Player currentPlayer) {
        executor.submit(() -> {
            ViewBoard viewBoard = ModelToViewConverter.fromBoard(board);
            Player receivingPlayer = players.stream().
                    filter(p-> p.getUsername().equals(username)).
                    findFirst().orElseThrow(()-> new InvalidUpdateException("No player has the given username"));
            ViewTileCoords perspective;
            if (receivingPlayer.getTile() != null) {
                perspective = ModelToViewConverter.fromTileToViewTileCoords(receivingPlayer.getTile());
            }
            else perspective = null;
            ArrayList<ViewPlayer> viewPlayers = players.stream().
                    map(ModelToViewConverter::fromPlayer).
                    collect(Collectors.toCollection(ArrayList::new));
            ArrayList<ViewPowerUp> viewPowerUps = powerUps.stream().
                    map(ModelToViewConverter::fromPowerUp).
                    collect(Collectors.toCollection(ArrayList::new));
            ArrayList<ViewWeapon> viewLoadedWeapons = loadedWeapons.stream().
                    map(ModelToViewConverter::fromWeapon).
                    collect(Collectors.toCollection(ArrayList::new));
            try {
                remoteReceiver.receiveTotalUpdate(username, viewBoard, perspective,
                        viewPlayers, points,
                        viewPowerUps, viewLoadedWeapons, currentPlayer.getId());
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, String.format("Unable to send total update to %s:%n%s", username, e.getMessage()));
            }
        });
    }

    @Override
    public void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player) {
        executor.submit(() -> {
            try {
                if (discardedWeapon == null)
                    remoteReceiver.receiveWeaponTaken(ModelToViewConverter.fromWeapon(takenWeapon), null, player.getId());
                else
                    remoteReceiver.receiveWeaponTaken(ModelToViewConverter.fromWeapon(takenWeapon), ModelToViewConverter.fromWeapon(discardedWeapon), player.getId());
            }
            catch (RemoteException e) {
                Logger.log(Priority.ERROR, "Unable to send weapon taken");
                view.setOnline(false);
            }
        });
    }

    /**
     * Constructor which takes the remote ViewReceiverInterface, which will be used to send updates to the client
     */
    public ViewUpdaterRMI(ViewReceiverInterface remoteReceiver, VirtualView view) {
        this.remoteReceiver = remoteReceiver;
        this.view = view;
        view.setOnline(true);
        this.pinger = new RMIPinger(view);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }

    public RMIPinger getPinger() {
        return pinger;
    }
}

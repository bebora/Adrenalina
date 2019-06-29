package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.network.ViewUpdater;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Used by the server to send updates to a player or to all players
 * Default methods with no receivingPlayer will send an update to all online player in the match
 */
public class UpdateSender implements ViewUpdater {
    private Match match;
    private Map<String,Deque<Runnable>> totalUpdates;
    private Map<String, Object> locks;

    public UpdateSender(Match match) {
        this.match = match;
        totalUpdates = new HashMap<>();
        locks = new HashMap<>();
        for (Player p : match.getPlayers()) {
            totalUpdates.put(p.getUsername(), new ArrayDeque<>());
            locks.put(p.getUsername(), new Object());
        }
        UpdatePoller updatePoller = new UpdatePoller(locks);
        updatePoller.start();
    }

    class UpdatePoller extends Thread{
        private ThreadPoolExecutor updateExecutor;
        private Map<String, Object> locks;

        public UpdatePoller(Map<String, Object> locks) {
            updateExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            this.locks = locks;
        }

        @Override
        public void run() {
            while (true) {
                    for (Map.Entry entry : totalUpdates.entrySet()) {
                        synchronized (locks.get((String) entry.getKey())) {
                            try {
                                Runnable task = (Runnable) ((Deque) entry.getValue()).pop();
                                updateExecutor.submit(task);
                                ((Deque) entry.getValue()).clear();
                            } catch (NoSuchElementException e) {
                                //Do nothing if empty
                            }
                        }
                    }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    Logger.log(Priority.ERROR, "interrupted update poller");
                }
            }
        }
    }

    @Override
    public void sendPing() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendAmmosTaken(Player player) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendAmmosTaken(p, player));
    }

    public void sendAmmosTaken(Player receivingPlayer, Player player){
        receivingPlayer.getVirtualView().getViewUpdater().sendAmmosTaken(player);
    }

    @Override
    public void sendAttackPlayer(Player attacker, Player receiver, int damageAmount, int marksAmount) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendAttackPlayer(p, attacker, receiver, damageAmount, marksAmount));
    }

    public void sendAttackPlayer(Player receivingPlayer, Player attacker, Player receiver, int damageAmount, int marksAmount) {
        receivingPlayer.getVirtualView().getViewUpdater().sendAttackPlayer(attacker, receiver, damageAmount, marksAmount);
    }

    @Override
    public void sendMovePlayer(Player player) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendMovePlayer(p, player));
    }

    public void sendMovePlayer(Player receivingPlayer, Player player) {
        receivingPlayer.getVirtualView().getViewUpdater().sendMovePlayer(player);
    }

    @Override
    public void sendPopupMessage(String message) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendPopupMessage(p, message));
    }

    public void sendPopupMessage(Player receivingPlayer, String message) {
        if (receivingPlayer.getVirtualView().getViewUpdater() == null) return;
        if (!receivingPlayer.getOnline()) {
            Logger.log(Priority.DEBUG, "Can't send popup update to offline player " + receivingPlayer.getUsername());
        }
        else {
            receivingPlayer.getVirtualView().getViewUpdater().sendPopupMessage(message);
        }

    }

    public void sendAcceptableType(AcceptableTypes acceptableTypes) {
        throw new UnsupportedOperationException("Not supported");
    }

    public void sendAcceptableType(Player receivingPlayer, AcceptableTypes acceptableTypes) {
        receivingPlayer.getVirtualView().getViewUpdater().sendAcceptableType(acceptableTypes);
    }

    @Override
    public void sendTile(Tile tile) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendTile(p, tile));
    }

    public void sendTile(Player receivingPlayer, Tile tile) {
        receivingPlayer.getVirtualView().getViewUpdater().sendTile(tile);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void sendTotalUpdate(String username, Board board, List<Player> players,
                                String idView, int points, List<PowerUp> powerUps,
                                List<Weapon> loadedWeapons, Player currentPlayer) {
        throw new UnsupportedOperationException("Can't send a TotalUpdate to everyone with the same arguments");

    }

    public void sendTotalUpdate(Player receivingPlayer, String username, Board board, List<Player> players,
                                String idView, int points, List<PowerUp> powerUps,
                                List<Weapon> loadedWeapons, Player currentPlayer) {
        if (receivingPlayer.getVirtualView().getViewUpdater() == null) return;
        if (!receivingPlayer.getOnline()) {
            Logger.log(Priority.DEBUG, "Can't send total update to offline player " + receivingPlayer.getUsername());
        }
        else {
            synchronized (locks.get(receivingPlayer.getUsername())) {
                Runnable update = () -> {
                    receivingPlayer.getVirtualView().getViewUpdater().sendTotalUpdate(username, board, players,
                            idView, points, powerUps,
                            loadedWeapons, currentPlayer);
                };
                totalUpdates.get(receivingPlayer.getUsername()).add(update);
            }
        }
    }

    @Override
    public void sendWeaponTaken(Weapon takenWeapon, Weapon discardedWeapon, Player player) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendWeaponTaken(p, takenWeapon, discardedWeapon, player));
    }

    public void sendWeaponTaken(Player receivingPlayer, Weapon takenWeapon, Weapon discardedWeapon, Player player) {
        receivingPlayer.getVirtualView().getViewUpdater().sendWeaponTaken(takenWeapon, discardedWeapon, player);
    }
}

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
    private UpdatePoller updatePoller;

    public UpdateSender(Match match) {
        this.match = match;
        totalUpdates = new HashMap<>();
        locks = new HashMap<>();
        for (Player p : match.getPlayers()) {
            totalUpdates.put(p.getUsername(), new ArrayDeque<>());
            locks.put(p.getUsername(), new Object());
        }
        updatePoller = new UpdatePoller();
        updatePoller.start();
    }

    public UpdatePoller getUpdatePoller() {
        return updatePoller;
    }

    /**
     * Utility class to send the latest update to the corresponding player.
     * Every {@link #timeout}, the latest update for every player gets fetched from {@link #totalUpdates}.
     * The polling is made allows to avoid sending too many update at the same time.
     */
    class UpdatePoller extends Thread{
        private Map<String, ThreadPoolExecutor> updateExecutor;
        private int timeout = 500;

        public UpdatePoller() {
            updateExecutor = new HashMap<>();
            for (String key : locks.keySet()) {
                updateExecutor.put(key, (ThreadPoolExecutor) Executors.newFixedThreadPool(1));
            }
        }

        /**
         * Handles parsing the current update to send to each player, and sending the latest update in the Deque.
         * After sending it, it clears the other elements present and release the lock.
         */
        @Override
        public void run() {
            while (!isInterrupted()) {
                    for (Map.Entry entry : totalUpdates.entrySet()) {
                        synchronized (locks.get((String) entry.getKey())) {
                            try {
                                Runnable task = (Runnable) ((Deque) entry.getValue()).pop();
                                updateExecutor.get((String) entry.getKey()).submit(task);
                                ((Deque) entry.getValue()).clear();
                            } catch (NoSuchElementException e) {
                                //Do nothing if empty
                            }
                        }
                    }
                try {
                    sleep(timeout);
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

    /**
     * Send a total update to {@code receivingPlayer}.
     * It creates a Runnable, adding it to the related Deque for the player.
     * The Runnable gets parsed after from {@link #updatePoller}.
     * @param receivingPlayer the player that receives the Update
     * @param username  of the player
     * @param board of the game
     * @param players all the players present in the game
     * @param idView id of the view
     * @param points of the receiving player
     * @param powerUps of the receiving player
     * @param loadedWeapons of the receiving player
     * @param currentPlayer who is currently playing th turn
     */
    public synchronized void sendTotalUpdate(Player receivingPlayer, String username, Board board, List<Player> players,
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
                totalUpdates.get(receivingPlayer.getUsername()).addFirst(update);
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

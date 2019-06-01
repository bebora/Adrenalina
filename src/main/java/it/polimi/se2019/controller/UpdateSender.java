package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.network.ViewUpdater;

import java.util.List;

/**
 * Used by the server to send updates to a player or to all players
 * Default methods with no receivingPlayer will send an update to all online player in the match
 */
public class UpdateSender implements ViewUpdater {
    private Match match;

    public UpdateSender(Match match) {
        this.match = match;
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
    public void sendAvailableActions(Player player) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendAvailableActions(p, player));
    }

    public void sendAvailableActions(Player receivingPlayer, Player player) {
        receivingPlayer.getVirtualView().getViewUpdater().sendAvailableActions(player);
    }

    //TODO sending same options to everyone may be useless
    @Override
    public void sendCurrentOptions(List<String> options) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendCurrentOptions(p, options));
    }

    public void sendCurrentOptions(Player receivingPlayer, List<String> options) {
        receivingPlayer.getVirtualView().getViewUpdater().sendCurrentOptions(options);
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
        receivingPlayer.getVirtualView().getViewUpdater().sendPopupMessage(message);
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

    @Override
    public void sendTotalUpdate(String username, Board board, List<Player> players,
                                String idView, int points, List<PowerUp> powerUps,
                                List<Weapon> loadedWeapons) {
        match.getPlayers().stream().
                filter(Player::getOnline).
                forEach(p -> sendTotalUpdate(p, username, board, players, idView, points, powerUps, loadedWeapons));
    }

    public void sendTotalUpdate(Player receivingPlayer, String username, Board board, List<Player> players,
                                String idView, int points, List<PowerUp> powerUps,
                                List<Weapon> loadedWeapons) {
        receivingPlayer.getVirtualView().getViewUpdater().sendTotalUpdate(username, board, players, idView, points, powerUps, loadedWeapons);
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

package it.polimi.se2019.view;

import it.polimi.se2019.network.ViewReceiverInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;

public class ConcreteViewReceiver extends UnicastRemoteObject implements ViewReceiverInterface {
    private View linkedView;
    private ConcreteViewReceiverHelper helper;

    public ConcreteViewReceiver(View linkedView) throws RemoteException {
        this.linkedView = linkedView;
        this.helper = new ConcreteViewReceiverHelper(linkedView);
    }

    @Override
    public void receiveSelectablesWrapper(SelectableOptionsWrapper selectableOptionsWrapper) throws RemoteException {
    }

    /**
     * Set the player ammos to those received in playerAmmos
     * @param playerId
     * @param playerAmmos
     */
    @Override
    public void receiveAmmosTaken(String playerId, ArrayList<String> playerAmmos) throws RemoteException {
        ViewPlayer player = helper.getPlayerFromId(playerId);
        player.setAmmos(playerAmmos);
    }

    /**
     * Add damages and marks to receiver ViewPlayer
     * Expect to have effective damages and marks that
     * don't go over damages/marks limit. For instance
     * an attack with 3 damages to a player with already 10
     * damages would throw an exception
     * @param attackerId
     * @param receiverId
     * @param damageAmount
     * @param marksAmount
     */
    @Override
    public void receiveAttackPlayer(String attackerId, String receiverId, int damageAmount, int marksAmount) throws RemoteException {
        ViewPlayer attacker = helper.getPlayerFromId(attackerId);
        ViewPlayer receiver = helper.getPlayerFromId(receiverId);
        if (receiver.getDamages().size() + damageAmount > 12)
            throw new InvalidUpdateException("Player can't receive so many damages");
        if (Collections.frequency(receiver.getMarks(), attacker) + marksAmount > 3 )
            throw new InvalidUpdateException("Player can't receive so many marks");
        receiver.getDamages().addAll(Collections.nCopies(damageAmount, attacker.getId()));
        receiver.getMarks().addAll(Collections.nCopies(marksAmount, attacker.getId()));
    }

    /**
     * Replace player actions with given actions
     * @param playerId
     * @param actions
     */
    @Override
    public void receiveAvailableActions(String playerId, ArrayList<ViewAction> actions) throws RemoteException {
        ViewPlayer player = helper.getPlayerFromId(playerId);
        player.setActions(actions);
    }

    @Override
    public void receiveCurrentOptions(ArrayList<String> options) throws RemoteException {
        //TODO show options to player
    }

    /**
     * Move player by changing its tile
     * @param playerId
     * @param coords
     */
    @Override
    public void receiveMovePlayer(String playerId, ViewTileCoords coords) throws RemoteException {
        ViewPlayer player = helper.getPlayerFromId(playerId);
        player.setTile(helper.getTileFromCoords(coords));
    }

    /**
     * Receive and show a message
     * @param message
     */
    @Override
    public void receivePopupMessage(String message) throws RemoteException {
        if (message.equals("ping"))
            return;
        else if (linkedView.getStatus() == Status.LOGIN && message.contains("SUCCESS")){
            linkedView.setStatus(Status.WAITING);
        }
        else if (message.contains("$") && message.split("$")[0].equals("WINNERS")) {
            //TODO game ended! show winners
        }
        //TODO show message to the client, e.g. view.showPopup(message)
    }


    @Override
    public void receiveSuccessConnection(String token) throws RemoteException {
        //TODO do something with the token
    }

    /**
     * Set corresponding tile weapons and ammos to those in received tile
     * @param tile
     */
    @Override
    public void receiveTile(ViewTile tile) throws RemoteException {
        ViewTile viewTile = helper.getTileFromCoords(tile.getCoords());
        tile.setAmmos(tile.getAmmos());
        tile.setWeapons(tile.getWeapons());
    }

    /**
     * Set almost all View attributes.
     * This method should be used when a player join or
     * rejoin the match after a disconnection
     * @param username
     * @param board
     * @param perspective
     * @param players
     * @param idView
     * @param points
     * @param powerUps
     * @param loadedWeapons
     */
    @Override
    public void receiveTotalUpdate(String username, ViewBoard board, ViewTileCoords perspective,
                                   ArrayList<ViewPlayer> players, String idView, int points,
                                   ArrayList<ViewPowerUp> powerUps, ArrayList<String> loadedWeapons) {
        linkedView.setUsername(username);
        linkedView.setBoard(board);
        linkedView.setPerspective(helper.getTileFromCoords(perspective));
        linkedView.setPlayers(players);
        linkedView.setIdView(idView);
        linkedView.setPoints(points);
        linkedView.setPowerUps(powerUps);
        linkedView.setLoadedWeapons(loadedWeapons);
        if (linkedView.getStatus() != Status.PLAYING) {
            linkedView.setStatus(Status.PLAYING);
        }
        linkedView.refresh();
    }

    /**
     * Remove weapon from tile and add it to the loaded player weapons.
     * Throws exception if applying update would go against weapon number limit
     * @param takenWeapon
     * @param discardedWeapon
     * @param playerId
     */
    @Override
    public void receiveWeaponTaken(String takenWeapon, String discardedWeapon, String playerId) throws RemoteException {
        //TODO tell clients that a player has taken the weapon
        ViewPlayer player = helper.getPlayerFromId(playerId);
        ViewTile tile = player.getTile();
        if (!tile.getWeapons().contains(takenWeapon))
            throw new InvalidUpdateException("Taken weapon does not exist in selected tile");
        tile.getWeapons().remove(takenWeapon);
        if (linkedView.getUsername().equals(playerId)) {
            if (discardedWeapon != null)
                linkedView.getLoadedWeapons().remove(discardedWeapon);
            linkedView.getLoadedWeapons().add(takenWeapon);
            if (linkedView.getLoadedWeapons().size() + player.getUnloadedWeapons().size() > 3)
                throw new InvalidUpdateException("Player would have too many weapons");
        }
    }
}

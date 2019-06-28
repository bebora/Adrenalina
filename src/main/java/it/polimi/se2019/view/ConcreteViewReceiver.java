package it.polimi.se2019.view;

import it.polimi.se2019.network.ViewReceiverInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Concrete class that actually receives updates from model
 */
public class ConcreteViewReceiver extends UnicastRemoteObject implements ViewReceiverInterface {
    private transient View linkedView;
    private transient ConcreteViewReceiverHelper helper;
    private transient final Object lock = new Object();

    public ConcreteViewReceiver(View linkedView) throws RemoteException {
        this.linkedView = linkedView;
        this.helper = new ConcreteViewReceiverHelper(linkedView);
    }

    @Override
    public synchronized void receiveSelectablesWrapper(SelectableOptionsWrapper selectableOptionsWrapper) throws RemoteException {
        synchronized (lock) {
            linkedView.setSelectableOptionsWrapper(selectableOptionsWrapper);
            linkedView.addMessage("RECEIVED OPTIONS " + selectableOptionsWrapper.getAcceptedTypes());
            linkedView.refresh();
        }
    }

    @Override
    public void receivePing() {
        this.linkedView.setLastRequest(System.nanoTime());
        if (linkedView.getEventUpdater() != null) {
            linkedView.getEventUpdater().sendAck();
        }
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
     * Move player by changing its tile
     * @param playerId
     * @param coords
     */
    @Override
    public void receiveMovePlayer(String playerId, ViewTileCoords coords) throws RemoteException {
        ViewPlayer player = helper.getPlayerFromId(playerId);
        player.setTile(helper.getTileFromCoords(coords));
        linkedView.refresh();
    }

    /**
     * Receive and show a message
     * @param message
     */
    @Override
    public void receivePopupMessage(String message) throws RemoteException {
        if (linkedView.getStatus() == Status.LOGIN || linkedView.getStatus() == null) {
            if (message.contains("SUCCESS")) {
                linkedView.setStatus(Status.WAITING);
                System.out.println("WAITING TO PLAY...");
                linkedView.getMessages().clear();
            }
            else if (message.contains("END")){
                System.out.println(message);
                linkedView.addMessage(message);
                linkedView.setStatus(Status.END);
            }
            else {
                linkedView.addMessage(message);
            }
        }
        else{
            if (message.contains("WINNERS")) {
                List<String> winners =  new ArrayList<>(Arrays.asList(message.split(",")));
                winners.remove(0);
                linkedView.printWinners(winners);
            }
            else if(linkedView.getMessages() != null) {
                linkedView.addMessage(message);
                linkedView.refresh();
            }
        }
    }

    /**
     * Set corresponding tile weapons and ammos to those in received tile
     * @param tile
     */
    @Override
    public void receiveTile(ViewTile tile) throws RemoteException {
        ViewTile viewTile = helper.getTileFromCoords(tile.getCoords());
        viewTile.setAmmos(tile.getAmmos());
        viewTile.setWeapons(tile.getWeapons());
        linkedView.refresh();
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
     * @param currentPlayerId
     */
    @Override
    public void receiveTotalUpdate(String username, ViewBoard board, ViewTileCoords perspective,
                                   ArrayList<ViewPlayer> players, String idView, int points,
                                   ArrayList<ViewPowerUp> powerUps, ArrayList<ViewWeapon> loadedWeapons, String currentPlayerId) {
        synchronized (lock) {
            linkedView.setUsername(username);
            linkedView.setBoard(board);
            linkedView.setPerspective(helper.getTileFromCoords(perspective));
            linkedView.setPlayers(players);
            linkedView.setIdView(idView);
            linkedView.setPoints(points);
            linkedView.setPowerUps(powerUps);
            linkedView.setLoadedWeapons(loadedWeapons);
            linkedView.setCurrentPlayer(helper.getPlayerFromId(currentPlayerId));
            if (linkedView.getStatus() != Status.PLAYING && linkedView.getStatus() != Status.END) {
                linkedView.setStatus(Status.PLAYING);
            }
            linkedView.addMessage("RECEIVED TOTAL UPDATE");
            linkedView.refresh();
        }
    }

    /**
     * Remove weapon from tile and add it to the loaded player weapons.
     * Throws exception if applying update would go against weapon number limit
     * @param takenWeapon
     * @param discardedWeapon
     * @param playerId
     */
    @Override
    public void receiveWeaponTaken(ViewWeapon takenWeapon, ViewWeapon discardedWeapon, String playerId) throws RemoteException {
        //TODO tell clients that a player has taken the weapon
        ViewPlayer player = helper.getPlayerFromId(playerId);
        ViewTile tile = player.getTile();
        if (!tile.getWeapons().contains(takenWeapon.getName()))
            throw new InvalidUpdateException("Taken weapon does not exist in selected tile");
        tile.getWeapons().remove(takenWeapon.getName());
        if (linkedView.getUsername().equals(playerId)) {
            if (discardedWeapon != null)
                linkedView.getLoadedWeapons().remove(discardedWeapon);
            linkedView.getLoadedWeapons().add(takenWeapon);
            if (linkedView.getLoadedWeapons().size() + player.getUnloadedWeapons().size() > 3)
                throw new InvalidUpdateException("Player would have too many weapons");
        }
        linkedView.refresh();
    }
}

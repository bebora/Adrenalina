package it.polimi.se2019.view;

import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.network.ViewReceiverInterface;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConcreteViewReceiver implements ViewReceiverInterface {
    private View linkedView;
    private ConcreteViewReceiverHelper helper;

    public ConcreteViewReceiver(View linkedView) {
        this.linkedView = linkedView;
        this.helper = new ConcreteViewReceiverHelper(linkedView);
    }
    
    /**
     * Set the player ammos to those received in playerAmmos
     * @param playerId
     * @param playerAmmos
     */
    @Override
    public void receiveAmmosTaken(String playerId, List<String> playerAmmos) {
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
    public void receiveAttackPlayer(String attackerId, String receiverId, int damageAmount, int marksAmount) {
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
    public void receiveAvailableActions(String playerId, List<ViewAction> actions) {
        ViewPlayer player = helper.getPlayerFromId(playerId);
        player.setActions(actions);
    }

    /**
     * Move player by changing its tile
     * @param playerId
     * @param coords
     */
    @Override
    public void receiveMovePlayer(String playerId, ViewTileCoords coords) {
        ViewPlayer player = helper.getPlayerFromId(playerId);
        player.setTile(helper.getTileFromCoords(coords));
    }

    /**
     * Receive and show a message
     * @param message
     */
    @Override
    public void receivePopupMessage(String message) {
        //TODO show message to the client, e.g. view.showPopup(message)
    }

    /**
     * Receive possible players from which the player can choose from
     * @param players
     * @param minPlayers
     * @param maxPlayers
     */
    @Override
    public void receiveSelectFromPlayers(List<String> players, int minPlayers, int maxPlayers) {
        //TODO show possible players to the receiver player
        List<ViewPlayer> viewPlayers = players.stream().
                map(p->helper.getPlayerFromId(p)).
                collect(Collectors.toList());
    }

    /**
     * Receive possible rooms from which the player can choose from
     * @param rooms
     */
    @Override
    public void receiveSelectFromRooms(List<String> rooms) {
        //TODO show possible rooms to the player
        List<Color> viewRooms = rooms.stream().
                map(Color::valueOf).
                collect(Collectors.toList());
    }

    /**
     * Receive possible tiles from which the player can choose from
     * @param coords
     * @param minTiles
     * @param maxTiles
     */
    @Override
    public void receiveSelectFromTiles(List<ViewTileCoords> coords, int minTiles, int maxTiles) {
        //TODO show possible tiles to the player
        List<ViewTile> tiles = coords.stream().
                map(helper::getTileFromCoords).
                collect(Collectors.toList());
    }

    /**
     * Inform the view that the link with the game controller
     * has been successfully established
     * @param token
     */
    @Override
    public void receiveSuccessConnection(String token) {
        //TODO do something with the token
    }

    /**
     * Set corresponding tile weapons and ammos to those in received tile
     * @param tile
     */
    @Override
    public void receiveTile(ViewTile tile) {
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
                                   List<ViewPlayer> players, String idView, int points,
                                   List<ViewPowerUp> powerUps, List<String> loadedWeapons) {
        linkedView.setUsername(username);
        linkedView.setBoard(board);
        linkedView.setPerspective(helper.getTileFromCoords(perspective));
        linkedView.setPlayers(players);
        linkedView.setIdView(idView);
        linkedView.setPoints(points);
        linkedView.setPowerUps(powerUps);
        linkedView.setLoadedWeapons(loadedWeapons);
    }

    /**
     * Remove weapon from tile and add it to the loaded player weapons.
     * Throws exception if applying update would go against weapon number limit
     * @param takenWeapon
     * @param discardedWeapon
     * @param playerId
     */
    @Override
    public void receiveWeaponTaken(String takenWeapon, String discardedWeapon, String playerId) {
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

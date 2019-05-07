package it.polimi.se2019.view;

import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.updatemessage.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ConcreteUpdateVisitor implements UpdateVisitor {
    private ClientView view;
    private ConcreteUpdateVisitorHelper helper;


    public ConcreteUpdateVisitor(ClientView linkedView) {
        this.view = linkedView;
        this.helper = new ConcreteUpdateVisitorHelper(linkedView);
    }

    /**
     * Set the player ammos to those received in update
     * @param update
     */
    @Override
    public void visit(AmmosTakenUpdate update) {
        ViewPlayer player = helper.getPlayerFromId(update.getPlayerId());
        player.setAmmos(update.getPlayerAmmos());
    }

    /**
     * Add damages and marks to receiver ViewPlayer
     * Expect to have effective damages and marks in update
     * that don't go over damages/marks limit. For instance
     * an update with 3 damages to a player with already 10
     * damages would throw an exception
     * @param update
     */
    @Override
    public void visit(AttackPlayerUpdate update) {
        ViewPlayer attacker = helper.getPlayerFromId(update.getAttackerId());
        ViewPlayer receiver = helper.getPlayerFromId(update.getReceiverId());
        if (receiver.getDamages().size() + update.getDamageAmount() > 12)
            throw new InvalidUpdateException("Player can't receive so many damages");
        if (Collections.frequency(receiver.getMarks(), attacker) + update.getMarksAmount() > 3 )
            throw new InvalidUpdateException("Player can't receive so many marks");
        receiver.getDamages().addAll(Collections.nCopies(update.getDamageAmount(), attacker.getId()));
        receiver.getMarks().addAll(Collections.nCopies(update.getMarksAmount(), attacker.getId()));

    }

    /**
     * Replace player actions with those in update
     * @param update
     */
    @Override
    public void visit(AvailableActionsUpdate update) {
        ViewPlayer player = helper.getPlayerFromId(update.getPlayerId());
        player.setActions(update.getActions());
    }

    /**
     * Move player by changing its tile
     * @param update
     */
    @Override
    public void visit(MovePlayerUpdate update) {
        ViewPlayer player = helper.getPlayerFromId(update.getPlayerId());
        player.setTile(helper.getTileFromCoords(update.getPosx(), update.getPosy()));
    }

    @Override
    public void visit(PopupMessageUpdate update) {
        //TODO show message to the client, e.g. view.showPopup(message)
        String message = update.getMessage();
    }

    @Override
    public void visit(SelectFromPlayersUpdate update) {
        //TODO show possible players to the receiver player
        List<ViewPlayer> players = update.getPlayers().stream().
                map(m->helper.getPlayerFromId(m)).
                collect(Collectors.toList());
        int minPlayers = update.getMinPlayers();
        int maxPlayers = update.getMaxPlayers();
    }

    @Override
    public void visit(SelectFromRoomsUpdate update) {
        //TODO show possible rooms to the player
        List<Color> rooms = update.getRooms().stream().
                map(Color::valueOf).
                collect(Collectors.toList());
    }

    @Override
    public void visit(SelectFromTilesUpdate update) {
        //TODO show possible tiles to the player
        List<ViewTile> tiles = update.getCoords().stream().
                map(m->helper.getTileFromCoords(m.getPosx(), m.getPosy())).
                collect(Collectors.toList());
        int minPlayers = update.getMinPlayers();
        int maxPlayers = update.getMaxPlayers();
    }

    @Override
    public void visit(SuccessConnectionUpdate update) {
        view.setToken(update.getToken());
    }

    /**
     * Set tile weapons and ammos to those received in update
     * @param update
     */
    @Override
    public void visit(TileUpdate update) {
        ViewTile tile = helper.getTileFromCoords(update.getPosx(), update.getPosy());
        tile.setAmmos(update.getAmmos());
        tile.setWeapons(update.getWeapons());
    }

    /**
     * Set almost all View attributes.
     * This update should be used when a player join or
     * rejoin the match after a disconnection
     * @param update
     */
    @Override
    public void visit(TotalUpdate update) {
        view.setUsername(update.getUsername());
        view.setBoard(update.getBoard());
        view.setPlayers(update.getPlayers());
        view.setIdView(update.getIdView());
        view.setPoints(update.getPoints());
        view.setPowerUps(update.getPowerUps());
        view.setLoadedWeapons(update.getLoadedWeapons());

    }

    /**
     * Remove weapon from tile and add it to the loaded player weapons.
     * Throws exception if applying update would go against weapon number limit
     * @param update
     */
    @Override
    public void visit(WeaponTakenUpdate update) {
        //TODO tell clients that a player has taken the weapon
        ViewTile tile = helper.getTileFromCoords(update.getPosx(), update.getPosy());
        ViewPlayer player = helper.getPlayerFromId(update.getPlayerId());
        if (!tile.getWeapons().contains(update.getTakenWeapon()))
            throw new InvalidUpdateException("Taken weapon does not exist in selected tile");
        tile.getWeapons().remove(update.getTakenWeapon());
        if (view.getUsername().equals(update.getPlayerId())) {
            if (update.getDiscardedWeapon() != null)
                view.getLoadedWeapons().remove(update.getDiscardedWeapon());
            view.getLoadedWeapons().add(update.getTakenWeapon());
            if (view.getLoadedWeapons().size() + player.getUnloadedWeapons().size() > 3)
                throw new InvalidUpdateException("Player would have too many weapons");
        }
    }

}

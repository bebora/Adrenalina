package it.polimi.se2019.view;

import it.polimi.se2019.model.updatemessage.*;

import java.util.Collections;


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
        receiver.getDamages().addAll(Collections.nCopies(update.getDamageAmount(), attacker));
        receiver.getMarks().addAll(Collections.nCopies(update.getMarksAmount(), attacker));

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

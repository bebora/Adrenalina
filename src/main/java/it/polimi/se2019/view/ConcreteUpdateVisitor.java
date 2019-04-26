package it.polimi.se2019.view;

import it.polimi.se2019.model.updatemessage.*;


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
        ViewPlayer player = helper.getPlayerFromId(update.getPlayer());
        player.setAmmos(update.getPlayerAmmos());
    }

    @Override
    public void visit(AttackPlayerUpdate update) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(AvailableActionsUpdate update) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void visit(MovePlayerUpdate update) {
        throw new UnsupportedOperationException();
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

    @Override
    public void visit(WeaponTakenUpdate update) {
        throw new UnsupportedOperationException();
    }

}

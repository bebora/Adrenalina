package it.polimi.se2019.view;

import it.polimi.se2019.model.UpdateMessage.*;

public class ConcreteUpdateVisitor implements UpdateVisitor {
    private ClientView view;
    public ConcreteUpdateVisitor(ClientView linkedView) {
        this.view = linkedView;
    }

    @Override
    public void visit(AmmosTakenUpdate update) {
        ViewPlayer player = view.getPlayers().stream().
                filter(m -> m.getId().equals(update.getPlayer())).
                findFirst().
                get();
        player.getTile().setAmmos(update.getNewAmmos());
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

    @Override
    public void visit(WeaponTakenUpdate update) {
        throw new UnsupportedOperationException();
    }

}

package it.polimi.se2019.view;

import it.polimi.se2019.model.UpdateMessage.*;

public interface UpdateVisitor {
    void visit(AmmosTakenUpdate update);
    void visit(AttackPlayerUpdate update);
    void visit(AvailableActionsUpdate update);
    void visit(MovePlayerUpdate update);
    void visit(WeaponTakenUpdate update);
}
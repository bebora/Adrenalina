package it.polimi.se2019.view;

import it.polimi.se2019.model.updatemessage.*;

public interface UpdateVisitor {
    void visit(AmmosTakenUpdate update);
    void visit(AttackPlayerUpdate update);
    void visit(AvailableActionsUpdate update);
    void visit(MovePlayerUpdate update);
    void visit(PopupMessageUpdate update);
    void visit(SelectFromPlayersUpdate update);
    void visit(SelectFromRoomsUpdate update);
    void visit(SelectFromTilesUpdate update);
    void visit(SuccessConnectionUpdate update);
    void visit(TileUpdate update);
    void visit(TotalUpdate update);
    void visit(WeaponTakenUpdate update);
}

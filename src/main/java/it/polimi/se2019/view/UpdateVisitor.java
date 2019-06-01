package it.polimi.se2019.view;

import it.polimi.se2019.model.updatemessage.*;

import java.util.ArrayList;
import java.util.List;

public class UpdateVisitor {
    ConcreteViewReceiver concreteViewReceiver;

    public void visit(SelectableOptionsUpdate selectableOptionsWrapper) {
        //TODO
    }

    public void visit(AmmosTakenUpdate update) {
        List<String> ammos= update.getPlayerAmmos();
        String playerId = update.getPlayerId();
        concreteViewReceiver.receiveAmmosTaken(playerId, new ArrayList<>(ammos));
    }
    public void visit(AttackPlayerUpdate update) {
        String attackerId = update.getAttackerId();
        String receiverId = update.getReceiverId();
        int damageAmount = update.getDamageAmount();
        int marksAmount = update.getMarksAmount();
        concreteViewReceiver.receiveAttackPlayer(attackerId, receiverId, damageAmount,marksAmount);
    }
    public void visit(AvailableActionsUpdate update) {
        List<ViewAction> actions = update.getActions();
        String playerId = update.getPlayerId();
        concreteViewReceiver.receiveAvailableActions(playerId, new ArrayList<>(actions));
    }
    public void visit(CurrentOptionsUpdate update) {
        concreteViewReceiver.receiveCurrentOptions(new ArrayList<>(update.getOptions()));
    }
    public void visit(MovePlayerUpdate update) {
        String playerId = update.getPlayerId();
        int posx = update.getPosx();
        int posy = update.getPosy();
        concreteViewReceiver.receiveMovePlayer(playerId, new ViewTileCoords(posy, posx));
    }
    public void visit(PopupMessageUpdate update) {
        String message = update.getMessage();
        concreteViewReceiver.receivePopupMessage(message);
    }

    public void visit(SuccessConnectionUpdate update) {
        String token = update.getToken();
        concreteViewReceiver.receiveSuccessConnection(token);
    }
    public void visit(TileUpdate update) {
        List<String> ammos = update.getAmmos();
        List<String> weapons = update.getWeapons();
        ViewTileCoords coords = update.getCoords();
        ViewTile viewTile = new ViewTile(ammos, weapons, coords);
        concreteViewReceiver.receiveTile(viewTile);
    }
    public void visit(TotalUpdate update) {
        String username = update.getUsername();
        ViewBoard board = update.getBoard();
        ViewTileCoords perspective = update.getPerspective();
        List<ViewPlayer> players = update.getPlayers();
        String idView = update.getIdView();
        int points = update.getPoints();
        List<ViewPowerUp> powerUps = update.getPowerUps();
        List<String> loadedWeapons = update.getLoadedWeapons();
        concreteViewReceiver.receiveTotalUpdate(username,
                board,
                perspective,
                new ArrayList<>(players),
                idView,
                points,
                new ArrayList<>(powerUps),
                new ArrayList<>(loadedWeapons));
    }
    public void visit(WeaponTakenUpdate update) {
        String takenWeapon = update.getTakenWeapon();
        String discardedWeapon = update.getDiscardedWeapon();
        String playerId = update.getPlayerId();
        concreteViewReceiver.receiveWeaponTaken(takenWeapon, discardedWeapon, playerId);

    }
}

package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;

public interface EventHandler {

    void receiveAction(Action action);

    void receiveDirection(Direction direction);

    void receiveEffect(String effect);

    void receivePlayer(List<Player> players);

    void receivePowerUps(List<PowerUp> powerUps);

    void receiveRoom(Color color);

    void receiveStop();

    void receiveTiles(List<Tile> tiles);

    void receiveWeapon(Weapon weapon);

    void setBlocked(boolean toblock);

    void receiveAmmo(Ammo ammo);

}

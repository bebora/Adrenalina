package it.polimi.se2019;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.Effect;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;

public interface Observer {

    void updateOnTiles(List<Tile> tiles);

    void updateOnPlayers(List<Player> players);

    void updateOnDirection(Direction direction);

    void updateOnRoom(Color room);

    void updateOnEffect(Effect effect);

    void updateOnWeapon(Weapon weapon);

    void updateOnPowerUps(List<PowerUp> powerUps);
}

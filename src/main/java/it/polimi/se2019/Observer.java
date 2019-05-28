package it.polimi.se2019;

import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;

public class  Observer {

    public void updateOnTiles(List<Tile> tiles) {
        throw new IncorrectEvent("Non accetto tiles");
    }

    public void updateOnPlayers(List<Player> players) {
        throw new IncorrectEvent("Non accetto tiles");
    }

    public void updateOnDirection(Direction direction) {
        throw new IncorrectEvent("Non accetto direzioni");
    }

    public void updateOnRoom(Color room) {
        throw new IncorrectEvent("Non accetto stanze");
    }

    public void updateOnEffect(String effect) {
        throw new IncorrectEvent("Non accetto effetti");
    }

    public void updateOnWeapon(Weapon weapon) {
        throw new IncorrectEvent("Non accetto armi");
    }

    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard) {
        throw new IncorrectEvent("Non accetto potenziamenti");
    }

    public void updateOnAction(Action action) {
        throw new IncorrectEvent("Non accetto azioni");
    }

    public void updateOnAmmoCard(AmmoCard ammoCard){
        throw new IncorrectEvent("Non accetto carte munizioni");
    }

    public void updateOnStopSelection(boolean reverse, boolean skip) {
        throw new IncorrectEvent("Non accetto reset!");
    }
}

package it.polimi.se2019.network;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.events.*;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.util.List;

public class EventUpdaterSocket implements EventUpdater{
    //TODO add some sort of WorkerClientSocket or anything that can send the events
    @Override
    public void sendAction(String action) {
        EventVisitable actionEvent = new SelectAction(action);
        //TODO send event to server
    }

    @Override
    public void sendDirection(String direction) {
        EventVisitable directionEvent = new SelectDirection(direction);
        //TODO send event to server
    }

    @Override
    public void sendEffect(String effect) {
        EventVisitable effectEvent = new SelectEffect(effect);
        //TODO send event to server
    }

    @Override
    public void sendPlayers(List<String> players) {
        EventVisitable actionEvent = new SelectPlayers(players);
        //TODO send event to server
    }

    @Override
    public void sendPowerUp(List<ViewPowerUp> powerUp, boolean discard) {
        EventVisitable actionEvent = new SelectPowerUps(powerUp, discard);
        //TODO send event to server
    }

    @Override
    public void sendRoom(String room) {
        EventVisitable actionEvent = new SelectRoom(room);
        //TODO send event to server
    }

    @Override
    public void sendTiles(List<ViewTileCoords> tiles) {
        EventVisitable actionEvent = new SelectTiles(tiles);
        //TODO send event to server
    }

    @Override
    public void sendWeapon(String weapon) {
        EventVisitable actionEvent = new SelectWeapon(weapon);
        //TODO send event to server
    }

    @Override
    public void login(View view, String nickname, String password, boolean existingGame, String mode) {
        //TODO throw exception or integrate ClientSocket login based on ConnectionRequest?
        throw new UnsupportedOperationException();
    }
}

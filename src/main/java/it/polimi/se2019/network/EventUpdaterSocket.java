package it.polimi.se2019.network;

import it.polimi.se2019.network.events.EventVisitable;
import it.polimi.se2019.network.events.*;
import it.polimi.se2019.view.UpdateVisitor;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Implements the EventUpdater, allowing clients using socket to send events to the server
 */
public class EventUpdaterSocket implements EventUpdater{
    private ClientSocket socket;
    private UpdateVisitor updateVisitor;
    private String url;
    private int port;

    public EventUpdaterSocket(String url, int port) {
        this.url = url;
        this.port = port;
    }

    @Override
    public void sendAck() {
        EventVisitable ackEvent = new AckEvent();
        socket.addEventToQueue(ackEvent);
    }

    @Override
    public void sendAmmo(String ammo) {
        EventVisitable ammoEvent = new SelectAmmo(ammo);
        socket.addEventToQueue(ammoEvent);
    }

    @Override
    public void sendAction(String action) {
        EventVisitable actionEvent = new SelectAction(action);
        socket.addEventToQueue(actionEvent);
    }

    @Override
    public void sendStop() {
        EventVisitable stopEvent = new SelectStop();
        socket.addEventToQueue(stopEvent);
    }

    @Override
    public void sendDirection(String direction) {
        EventVisitable directionEvent = new SelectDirection(direction);
        socket.addEventToQueue(directionEvent);
    }

    @Override
    public void sendEffect(String effect) {
        EventVisitable effectEvent = new SelectEffect(effect);
        socket.addEventToQueue(effectEvent);
    }

    @Override
    public void sendPlayers(List<String> players) {
        EventVisitable playerEvent = new SelectPlayers(players);
        socket.addEventToQueue(playerEvent);
    }

    @Override
    public void sendPowerUp(List<ViewPowerUp> powerUp, boolean discard) {
        EventVisitable powerupEvent = new SelectPowerUps(powerUp, discard);
        socket.addEventToQueue(powerupEvent);
    }

    @Override
    public void sendRoom(String room) {
        EventVisitable roomEvent = new SelectRoom(room);
        socket.addEventToQueue(roomEvent);
    }

    @Override
    public void sendTiles(List<ViewTileCoords> tiles) {
        EventVisitable tileEvent = new SelectTiles(tiles);
        socket.addEventToQueue(tileEvent);
    }

    @Override
    public void sendWeapon(String weapon) {
        EventVisitable weaponEvent = new SelectWeapon(weapon);
        socket.addEventToQueue(weaponEvent);
    }

    @Override
    public boolean login(View view, String nickname, String password, boolean existingGame, String mode) throws RemoteException {
        ConnectionRequest loginEvent = new ConnectionRequest(nickname,password, existingGame, mode);
        updateVisitor = new UpdateVisitor(view.getReceiver());
        socket = new ClientSocket(url,port,loginEvent, updateVisitor);
        socket.start();
        //return true if everything is ok
        //RemoteException will be thrown if server is unreachable
        return true;
    }
}

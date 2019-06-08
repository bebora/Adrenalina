package it.polimi.se2019;

import it.polimi.se2019.controller.AcceptableTypes;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.controller.RequestDispatcher;
import it.polimi.se2019.controller.TimerCostrainedEventHandler;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;

/**
 * Blocking class to wait for client interaction
 * It joins on TimerCostrainedEventHandler, that will update this object with the received object from client
 **/
public class Choice extends Observer{
    private List<PowerUp> powerUps;
    private Action action;
    private Direction direction;
    private String effect;
    private List<Player> players;
    private List<Tile> tiles;
    private Weapon weapon;
    private Color room;
    private Ammo ammo;
    private ThreeState stop;
    private ReceivingType receivingType;
    private TimerCostrainedEventHandler timerCostrainedEventHandler;

    public Choice(RequestDispatcher requestDispatcher, AcceptableTypes acceptableTypes) {
        receivingType = ReceivingType.NULL;
        timerCostrainedEventHandler = new TimerCostrainedEventHandler(this, requestDispatcher, acceptableTypes);
    }


    public void receive() {
        timerCostrainedEventHandler.start();
        try {
            timerCostrainedEventHandler.join();
        }
        catch (InterruptedException e) {
            Logger.log(Priority.ERROR, "Join on Choice killed");
        }
    }

    @Override
    public void updateOnTiles(List<Tile> tiles) {
        this.tiles = tiles;
        receivingType = ReceivingType.TILES;
    }

    @Override
    public void updateOnPlayers(List<Player> players) {
        this.players = players;
        receivingType = ReceivingType.PLAYERS;
    }

    @Override
    public void updateOnDirection(Direction direction) {
        this.direction = direction;
        receivingType = ReceivingType.DIRECTION;
    }

    @Override
    public void updateOnRoom(Color room) {
        this.room = room;
        receivingType = ReceivingType.ROOM;
    }

    @Override
    public void updateOnEffect(String effect) {
        this.effect = effect;
        receivingType = ReceivingType.EFFECT;
    }

    @Override
    public void updateOnWeapon(Weapon weapon) {
        this.weapon = weapon;
        receivingType = ReceivingType.WEAPON;
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard) {
        this.powerUps = powerUps;
        receivingType = ReceivingType.POWERUP;
    }

    @Override
    public void updateOnAction(Action action) {
        this.action = action;
        receivingType = ReceivingType.ACTION;
    }

    @Override
    public void updateOnStopSelection(ThreeState skip) {
        this.stop = skip;
        receivingType = ReceivingType.STOP;
    }

    @Override
    public void updateOnAmmo(Ammo ammo) {
        this.ammo = ammo;
        receivingType = ReceivingType.COLOR;
    }

    public void setReceivingType(ReceivingType receivingType) {
        this.receivingType = receivingType;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public Action getAction() {
        return action;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getEffect() {
        return effect;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public Color getRoom() {
        return room;
    }

    public Ammo getAmmo() {
        return ammo;
    }

    public ThreeState getStop() {
        return stop;
    }

    public ReceivingType getReceivingType() {
        return receivingType;
    }
}

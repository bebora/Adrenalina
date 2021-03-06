package it.polimi.se2019.network.updatemessage;

import it.polimi.se2019.view.*;

import java.util.ArrayList;

/**
 * Represent whole View when the game starts or when a player reconnects
 */
public class TotalUpdate implements UpdateVisitable {
    private String username;
    private ViewBoard board;
    private ViewTileCoords perspective;
    private ArrayList<ViewPlayer> players;
    private int points;
    private ArrayList<ViewPowerUp> powerUps;
    private ArrayList<ViewWeapon> loadedWeapons;
    private String currentPlayerId;

    public String getUsername() {
        return username;
    }

    public ViewBoard getBoard() {
        return board;
    }

    public ViewTileCoords getPerspective() {
        return perspective;
    }

    public ArrayList<ViewPlayer> getPlayers() {
        return players;
    }

    public int getPoints() {
        return points;
    }

    public ArrayList<ViewPowerUp> getPowerUps() {
        return powerUps;
    }

    public ArrayList<ViewWeapon> getLoadedWeapons() {
        return loadedWeapons;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBoard(ViewBoard board) {
        this.board = board;
    }

    public void setPerspective(ViewTileCoords perspective) {
        this.perspective = perspective;
    }

    public void setPlayers(ArrayList<ViewPlayer> players) {
        this.players = players;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setPowerUps(ArrayList<ViewPowerUp> powerUps) {
        this.powerUps = powerUps;
    }

    public void setLoadedWeapons(ArrayList<ViewWeapon> loadedWeapons) {
        this.loadedWeapons = loadedWeapons;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }
}

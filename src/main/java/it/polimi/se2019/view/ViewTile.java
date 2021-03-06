package it.polimi.se2019.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simplified tile used by the view, with basic info to display
 */
public class ViewTile implements Serializable {
    /**
     * Color of the room as a String
     */
    private String room;

    /**
     * Coordinates of the tile
     */
    private ViewTileCoords coords;

    /**
     * Weapons contained in tile if it's a spawn tile
     */
    private ArrayList<String> weapons;

    /**
     * Ammos contained in tile if it's not a spawn tile
     */
    private ArrayList<String> ammos;

    /**
     * {@code true} if tile is a spawn
     */
    private boolean spawn;

    public void setRoom(String room) {
        this.room = room;
    }

    public void setCoords(ViewTileCoords coords) {
        this.coords = coords;
    }

    public void setWeapons(ArrayList<String> weapons) {
        this.weapons = weapons;
    }

    public void setAmmos(ArrayList<String> ammos) {
        this.ammos = ammos;
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }
    public ViewTileCoords getCoords() {
        return coords;
    }

    public ArrayList<String> getAmmos() {
        return ammos;
    }

    public ArrayList<String> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<String> weapons) {
        this.weapons = new ArrayList<>(weapons);
    }

    public void setAmmos(List<String> ammos) {
        this.ammos = new ArrayList<>(ammos);
    }

    public String getRoom(){return room;}

    public boolean isSpawn(){return spawn;}

    public ViewTile() {}

    /**
     * Manhattan distance between two ViewTiles
     * @param tile1 first tile
     * @param tile2 second tile
     * @return Manhattan distance between the two tiles
     */
    public static int cabDistance(ViewTile tile1, ViewTile tile2) {
        return Math.abs(tile1.getCoords().getPosx() - tile2.getCoords().getPosx()) + Math.abs(tile1.getCoords().getPosy() - tile2.getCoords().getPosy());
    }

    @Override
    public boolean equals(Object p) {
        boolean returnValue = false;
        if (p instanceof ViewTile) {
            ViewTile tile = (ViewTile) p;
            returnValue = (tile.getCoords().getPosx() == this.getCoords().getPosx() && tile.getCoords().getPosy() == this.getCoords().getPosy());
        }
        return returnValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coords.getPosx()*coords.getPosy());
    }

    /**
     * Constructor used in visitor, missing color because it won't be used: will get the correspondent tile using helper, from coords.
     * @param ammos
     * @param weapons
     * @param coords
     */
    public ViewTile(List<String> ammos, List<String> weapons, ViewTileCoords coords) {
        this.coords = coords;
        this.weapons = new ArrayList<>(weapons);
        this.ammos = new ArrayList<>(ammos);
    }
}

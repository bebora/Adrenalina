package it.polimi.se2019.view;

import java.io.Serializable;

/**
 * Position of a viewTile on the map
 * This class allows tiles to be sent without their unnecessary weapons and ammos
 */
public class ViewTileCoords implements Serializable {
    /**
     * x coordinate of the tile
     */
    private int posx;
    /**
     * y coordinate of the tile
     */
    private int posy;

    public int getPosx() {
        return posx;
    }

    public int getPosy() {
        return posy;
    }

    public void setPosx(int posx) {
        this.posx = posx;
    }

    public void setPosy(int posy) {
        this.posy = posy;
    }

    public ViewTileCoords(int posy, int posx) {
        this.posx = posx;
        this.posy = posy;
    }

    public ViewTileCoords() {}

    @Override
    public String toString() {
        return String.format("(%d,%d)",posx,posy);
    }
}

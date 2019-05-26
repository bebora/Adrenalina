package it.polimi.se2019.view;

import it.polimi.se2019.model.board.Tile;

import java.io.Serializable;

/**
 * Position of a viewTile on the map
 * This class allows tiles to be sent without their unnecessary weapons and ammos
 */
public class ViewTileCoords implements Serializable {
    private int posx;
    private int posy;

    public int getPosx() {
        return posx;
    }

    public int getPosy() {
        return posy;
    }

    public ViewTileCoords(int posy, int posx) {
        this.posx = posx;
        this.posy = posy;
    }

    public ViewTileCoords(Tile tile) {
        this.posx = tile.getPosx();
        this.posy = tile.getPosy();
    }
}

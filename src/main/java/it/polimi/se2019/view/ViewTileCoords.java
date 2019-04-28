package it.polimi.se2019.view;

/**
 * Position of a viewTile on the map
 * This class allows tiles to be sent without their unnecessary weapons and ammos
 */
public class ViewTileCoords {
    private int posx;
    private int posy;

    public int getPosx() {
        return posx;
    }

    public int getPosy() {
        return posy;
    }

    public ViewTileCoords(int posx, int posy) {
        this.posx = posx;
        this.posy = posy;
    }
}

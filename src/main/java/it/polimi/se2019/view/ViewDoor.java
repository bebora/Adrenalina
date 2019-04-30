package it.polimi.se2019.view;

import it.polimi.se2019.model.board.Door;

public class ViewDoor {
    private ViewTile tile1;
    private ViewTile tile2;
    public ViewDoor(Door door) {
        this.tile1 = new ViewTile(door.getTile1());
        this.tile2 = new ViewTile(door.getTile2());
    }
}
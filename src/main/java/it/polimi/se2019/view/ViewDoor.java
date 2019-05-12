package it.polimi.se2019.view;

import it.polimi.se2019.model.board.Door;

public class ViewDoor {
    private ViewTile tile1;
    private ViewTile tile2;
    public ViewDoor(Door door) {
        this.tile1 = new ViewTile(door.getTile1());
        this.tile2 = new ViewTile(door.getTile2());
    }

    public ViewDoor(ViewTile tile1, ViewTile tile2){
        this.tile1 = tile1;
        this.tile2 = tile2;
    }

    @Override
    public boolean equals(Object p) {
        boolean returnValue = false;
        if (p instanceof ViewDoor) {
            ViewDoor door = (ViewDoor) p;
            returnValue = (door.tile1.equals(this.tile1) && door.tile2.equals(this.tile2)) || (door.tile1.equals(this.tile2) && door.tile2.equals(this.tile1));
        }
        return returnValue;
    }
}
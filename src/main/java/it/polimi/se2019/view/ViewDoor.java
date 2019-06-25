package it.polimi.se2019.view;

import java.io.Serializable;

public class ViewDoor implements Serializable {
    private ViewTile tile1;

    private ViewTile tile2;

    public void setTile1(ViewTile tile1) {
        this.tile1 = tile1;
    }

    public void setTile2(ViewTile tile2) {
        this.tile2 = tile2;
    }

    public ViewDoor() {}

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
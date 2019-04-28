package it.polimi.se2019.view;

import java.util.List;

public class ViewTile {
    private String Room;
    private ViewTileCoords coords;
    private List<String> weapons;
    private List<String> ammos;

    public ViewTileCoords getCoords() {
        return coords;
    }

    public List<String> getAmmos() {
        return ammos;
    }

    public List<String> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<String> weapons) {
        this.weapons = weapons;
    }

    public void setAmmos(List<String> ammos) {
        this.ammos = ammos;
    }

}

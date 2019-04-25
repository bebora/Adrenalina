package it.polimi.se2019.view;

import java.util.List;

public class ViewTile {
    private String Room;
    private int posx;
    private int posy;
    private List<String> weapons;
    private List<String> ammos;

    public int getPosy() {
        return posy;
    }

    public int getPosx() {
        return posx;
    }

    public List<String> getAmmos() {
        return ammos;
    }

    public void setAmmos(List<String> ammos) {
        this.ammos = ammos;
    }
}

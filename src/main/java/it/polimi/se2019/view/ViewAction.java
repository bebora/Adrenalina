package it.polimi.se2019.view;

public class ViewAction {
    int movements;
    boolean reload;
    boolean grab;
    boolean shoot;
    public ViewAction(int movements, boolean reload, boolean grab, boolean shoot) {
        this.movements = movements;
        this.reload = reload;
        this.grab = grab;
        this.shoot = shoot;
    }
}

package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

public class SelectWeapon implements EventVisitable {
    private String weapon;
    public SelectWeapon(String weapon){
        this.weapon = weapon;
    }
    public void accept(EventVisitor visitor) { visitor.visit(this); }
    public String getWeapon() { return weapon; }
}

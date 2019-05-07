package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

public class SelectWeapon implements EventVisitable {
    private String weapon;
    String token;
    public SelectWeapon(String weapon, String token){
        this.weapon = weapon;
        this.token = token;
    }
    public void accept(EventVisitor visitor) { visitor.visit(this); }
    public String getWeapon() { return weapon; }
}

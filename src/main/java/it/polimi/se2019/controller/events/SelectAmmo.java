package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

public class SelectAmmo implements EventVisitable {
    String ammo;
    @Override
    public void accept(EventVisitor visitor) {visitor.visit(this);}
    public SelectAmmo(String ammo){
        this.ammo = ammo;
    }
    public String getAmmo() { return ammo; }
}

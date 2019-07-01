package it.polimi.se2019.network.events;

import it.polimi.se2019.network.EventVisitor;

/**
 * Event used by view to choose an ammo
 */
public class SelectAmmo implements EventVisitable {
    String ammo;
    @Override
    public void accept(EventVisitor visitor) {visitor.visit(this);}
    public SelectAmmo(String ammo){
        this.ammo = ammo;
    }
    public String getAmmo() { return ammo; }
}

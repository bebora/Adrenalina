package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

/**
 * Event used by view to attack with a weapon or to buy one.
 * Controller will understand the meaning depending on the context
 */
public class SelectWeapon implements EventVisitable {
    /**
     * Weapon name
     */
    private String weapon;
    public SelectWeapon(String weapon){
        this.weapon = weapon;
    }
    public void accept(EventVisitor visitor) { visitor.visit(this); }
    public String getWeapon() { return weapon; }
}

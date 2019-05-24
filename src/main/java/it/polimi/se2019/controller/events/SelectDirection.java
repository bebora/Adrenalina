package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;


/**
 * Event used by view when the player can choose
 * a cardinal direction to attack
 */
public class SelectDirection implements EventVisitable {
    private String direction;
    @Override
    public void accept(EventVisitor visitor) {visitor.visit(this);}
    public SelectDirection(String direction){
        this.direction = direction;
    }
    public String getDirection() { return direction; }
}


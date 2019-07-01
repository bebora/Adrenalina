package it.polimi.se2019.network.events;

import it.polimi.se2019.network.EventVisitor;


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


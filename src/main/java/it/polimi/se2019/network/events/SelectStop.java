package it.polimi.se2019.network.events;

import it.polimi.se2019.network.EventVisitor;

/**
 * Event used by view in two cases:
 * <li>player does not want to do anything else in its turn</li>
 * <li>player wants to undo his entire current action</li>
 * -
 */
public class SelectStop implements EventVisitable {
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}

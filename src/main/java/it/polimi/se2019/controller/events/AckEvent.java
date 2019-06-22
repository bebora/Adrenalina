package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

/**
 * Socket event used to acknowledge the Server after a {@link it.polimi.se2019.controller.updatemessage.PingUpdate} is received.
 */
public class AckEvent implements EventVisitable{
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}

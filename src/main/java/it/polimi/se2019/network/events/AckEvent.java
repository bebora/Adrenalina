package it.polimi.se2019.network.events;

import it.polimi.se2019.network.EventVisitor;

/**
 * Socket event used to acknowledge the Server after a {@link it.polimi.se2019.network.updatemessage.PingUpdate} is received.
 */
public class AckEvent implements EventVisitable{
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}

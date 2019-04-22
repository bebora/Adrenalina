package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

public class ConnectionRequest implements EventVisitable {
    String username, encodedPassword;
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}

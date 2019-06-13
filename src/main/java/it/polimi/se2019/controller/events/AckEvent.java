package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

public class AckEvent implements EventVisitable{
    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}

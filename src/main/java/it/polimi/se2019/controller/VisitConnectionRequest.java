package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.controller.events.ConnectionRequest;

public class VisitConnectionRequest extends EventVisitor{
    @Override
    public void visit(ConnectionRequest event) {
        EventVisitable currentEvent = event;
        currentObserver.update(event);
    }

}


package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.controller.events.*;


public class VisitPlayers extends EventVisitor {
    Observer currentObserver;
    @Override
    public void visit(SelectPlayers event) {
        EventVisitable currentEvent = event;
        currentObserver.update(event);
    }

    public void setObserver(Observer currentObserver) {
        this.currentObserver = currentObserver;
    }
}

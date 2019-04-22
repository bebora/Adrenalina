package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.controller.events.*;
import it.polimi.se2019.model.actions.Action;

import java.util.List;

public class VisitPlayers implements EventVisitor {
    Observer currentObserver;
    @Override
    public void visit(SelectPlayers event) {
        EventVisitable currentEvent = event;
        currentObserver.update(event);
    }
    public List<Action> visit(SelectAction event) {
        throw new IncorrectEvent();
    }

    public VisitPlayers(Observer currentObserver) {
        this.currentObserver = currentObserver;
    }
}

package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.SelectPlayers;


public class VisitPlayers extends EventVisitor {
    @Override
    public void visit(SelectPlayers event) {
        EventVisitable currentEvent = event;
    }

}

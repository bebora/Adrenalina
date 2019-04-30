package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.controller.events.ConnectionRequest;
import it.polimi.se2019.controller.events.SelectAction;
import it.polimi.se2019.controller.events.SelectPlayers;
import it.polimi.se2019.controller.events.SelectWeapon;

public class EventVisitor {
    Observer currentObserver;
    public void visit(SelectPlayers event) {
        throw new UnsupportedOperationException();
    }
    public void visit(SelectAction event) {
        throw new UnsupportedOperationException();
    }
    public void visit(SelectWeapon event){ throw new UnsupportedOperationException();}


    public void visit(ConnectionRequest event) {
        throw new UnsupportedOperationException();
    }

    public void setObserver(Observer currentObserver) {
        this.currentObserver = currentObserver;
    }

}

package controller;

import controller.events.SelectAction;
import controller.events.SelectPlayers;

public interface Visitor {
    void visit(SelectPlayers event);
    void visit(SelectAction event);
}

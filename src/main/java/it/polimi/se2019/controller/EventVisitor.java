package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.SelectAction;
import it.polimi.se2019.controller.events.SelectPlayers;
import it.polimi.se2019.model.actions.Action;

import java.util.List;

public interface EventVisitor {
    public abstract void visit(SelectPlayers event);
    public abstract List<Action> visit(SelectAction event);
}

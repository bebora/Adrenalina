package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.SelectAction;
import it.polimi.se2019.controller.events.SelectPlayers;
import it.polimi.se2019.model.actions.Action;

import java.util.List;

public interface Visitor {
    List<String> visit(SelectPlayers event);
    List<Action> visit(SelectAction event);
}

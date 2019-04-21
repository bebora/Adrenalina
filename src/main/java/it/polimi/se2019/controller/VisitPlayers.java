package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.*;
import it.polimi.se2019.model.actions.Action;

import java.util.List;

public class VisitPlayers implements Visitor {
    @Override
    public List<String> visit(SelectPlayers event) {
        //TODO DO corresponding action in it.polimi.se2019.controller related to the players
        return null;
    }
    public List<Action> visit(SelectAction event) {
        throw new IncorrectEvent();
    }
}

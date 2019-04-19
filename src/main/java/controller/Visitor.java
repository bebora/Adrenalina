package controller;

import controller.events.SelectAction;
import controller.events.SelectPlayers;
import model.actions.Action;

import java.util.List;

public interface Visitor {
    List<String> visit(SelectPlayers event);
    List<Action> visit(SelectAction event);
}

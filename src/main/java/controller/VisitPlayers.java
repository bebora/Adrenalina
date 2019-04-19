package controller;

import controller.events.*;
import model.actions.Action;

import java.util.List;

public class VisitPlayers implements Visitor{
    @Override
    public List<String> visit(SelectPlayers event) {
        //TODO DO corresponding action in controller related to the players
        return null;
    }
    public List<Action> visit(SelectAction event) {
        throw new IncorrectEvent();
    }
}

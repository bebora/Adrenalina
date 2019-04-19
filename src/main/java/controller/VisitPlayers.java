package controller;

import controller.events.*;

public class VisitPlayers implements Visitor{
    @Override
    public void visit(SelectPlayers event) {
        //TODO DO corresponding action in controller related to the players
    }
    public void visit(SelectAction event) {
        //TODO DO select action and set the player to do the first sub-action of the action
    }
}

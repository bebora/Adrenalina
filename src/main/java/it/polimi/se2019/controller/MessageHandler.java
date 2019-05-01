package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;

import java.util.HashMap;
import java.util.Map;

public class MessageHandler {
    Map<Player, EventVisitor> currentUpdate;

    public MessageHandler() {
        currentUpdate = new HashMap<>();
    }
    public void update(EventVisitable event) {
        //TODO get related Player from the signing of the event
        //TODO Visit the @event using the corresponding EventVisitor
    }

    public void addVisitorPlayer(Player player, EventVisitor eventvisitor) {
        currentUpdate.put(player, eventvisitor);
    }
}

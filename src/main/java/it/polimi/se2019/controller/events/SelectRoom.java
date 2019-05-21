package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

public class SelectRoom implements EventVisitable {
    private String room;

    @Override
    public void accept(EventVisitor visitor) {visitor.visit(this);}

    public String getRoom() {
        return room;
    }
    public SelectRoom(String room) {
        this.room = room;
    }
}

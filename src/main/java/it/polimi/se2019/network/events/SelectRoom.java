package it.polimi.se2019.network.events;

import it.polimi.se2019.network.EventVisitor;

/**
 * Event used by view after choosing a Room,
 * e.g. when it can attack a room and must decide from the possible ones
 */
public class SelectRoom implements EventVisitable {
    /**
     * Room color
     */
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

package it.polimi.se2019.network.events;


import it.polimi.se2019.network.EventVisitor;

/**
 * Visitable class representing the events that can be sent from the client to the server, using socket.
 * It follows a standard Visitor Pattern.
 */
public interface EventVisitable {

    void accept(EventVisitor visitor);
    String toString();
}

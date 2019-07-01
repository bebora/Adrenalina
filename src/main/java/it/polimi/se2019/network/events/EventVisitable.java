package it.polimi.se2019.network.events;


import it.polimi.se2019.network.EventVisitor;

public interface EventVisitable {
    void accept(EventVisitor visitor);
    String toString();
}

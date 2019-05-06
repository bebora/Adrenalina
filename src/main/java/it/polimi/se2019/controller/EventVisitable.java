package it.polimi.se2019.controller;


public interface EventVisitable {
    void accept(EventVisitor visitor);
    String toString();
}

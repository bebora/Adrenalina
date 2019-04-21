package it.polimi.se2019.controller;


public interface Visitable {
    void accept(EventVisitor visitor);
}

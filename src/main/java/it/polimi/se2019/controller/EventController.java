package it.polimi.se2019.controller;

public class EventController {
    private EventVisitor curVisitor = null;

    public void setCurVisitor(EventVisitor visitor){
        curVisitor=visitor;
    }

    public EventVisitor getCurVisitor(){
        return curVisitor;
    }
}

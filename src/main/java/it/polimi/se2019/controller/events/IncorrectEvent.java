package it.polimi.se2019.controller.events;

public class IncorrectEvent extends RuntimeException {
    //TODO CONSTRUCTOR WITH POPUP TO SEND
    String message;
    public IncorrectEvent(String message) {
        this.message = message;
    }
}

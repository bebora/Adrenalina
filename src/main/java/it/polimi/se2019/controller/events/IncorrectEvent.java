package it.polimi.se2019.controller.events;

public class IncorrectEvent extends RuntimeException {
    String message;
    public IncorrectEvent(String message) {
        this.message = message;
    }
}

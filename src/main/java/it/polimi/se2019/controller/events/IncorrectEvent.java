package it.polimi.se2019.controller.events;

/**
 * Exception used to communicate to the view a wrong event
 */
public class IncorrectEvent extends RuntimeException {
    final String message;
    public IncorrectEvent(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

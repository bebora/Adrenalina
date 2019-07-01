package it.polimi.se2019.network.events;

/**
 * Exception used to communicate to the view a wrong event
 */
public class IncorrectEventException extends RuntimeException {
    final String message;
    public IncorrectEventException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

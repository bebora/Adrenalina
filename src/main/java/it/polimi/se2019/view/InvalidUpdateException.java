package it.polimi.se2019.view;

/**
 * Exception used to indicate a wrong update received from the server.
 * The raise of this exception is forbidden, without changes to client code.
 */
public class InvalidUpdateException extends RuntimeException {
    public InvalidUpdateException() {
        super("Invalid update");
    }
    public InvalidUpdateException(String reason) {
        super("Invalid update: " + reason);
    }
}

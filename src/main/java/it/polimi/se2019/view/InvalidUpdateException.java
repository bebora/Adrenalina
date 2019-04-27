package it.polimi.se2019.view;

public class InvalidUpdateException extends RuntimeException {
    public InvalidUpdateException() {
        super("Invalid update");
    }
    public InvalidUpdateException(String reason) {
        super("Invalid update: " + reason);
    }
}

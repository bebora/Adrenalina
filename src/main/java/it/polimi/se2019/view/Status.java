package it.polimi.se2019.view;

/**
 * Defines the current status of the View
 */
public enum  Status {
    /**
     * Indicates that the view needs to login.
     */
    LOGIN,
    /**
     * Indicates that the view was successfully logged in, and is waiting for a game to start
     */
    WAITING,
    /**
     * Indicates that the view is currently playing.
     */
    PLAYING,
    /**
     * Indicates:
     * <li>If the view didn't login yet, that the login was unsuccessful</li>
     * <li>If the view was playing, it can indicates that the game finished or that client just disconnected.</li>
     */
    END
}

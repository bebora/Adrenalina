package it.polimi.se2019.network.events;

import it.polimi.se2019.network.EventVisitor;

/**
 * Socket message used for communicating a request of login.
 */
public class ConnectionRequest implements EventVisitable {

    private String username;
    private String password;
    private boolean existingGame;
    private String mode;

    /**
     * Create a ConnectionRequest
     * @param username name chosen by the client
     * @param password password chosen by the client, saved hashed in the server
     * @param existingGame whether the client is reconnecting
     * @param mode name of the chosen mode
     */
    public ConnectionRequest(String username, String password, boolean existingGame, String mode)

    {
        this.username = username;
        this.password = password;
        this.existingGame = existingGame;
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }


    public boolean getExistingGame() {
        return existingGame;
    }

    /**
     * Get the token saved in the server, hashing the password
     * @return
     */
    public String getToken() {
        if (!(username.contains("$") || password.contains("$")))
            return username + "$" + (password).hashCode();
        else
            throw new IllegalArgumentException();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void accept(EventVisitor visitor){
        visitor.visit(this);
    }
}

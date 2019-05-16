package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

/**
 * Socket message used for communicating a request of login
 */
public class ConnectionRequest implements EventVisitable {

    private String username;
    private String password;
    private boolean existingGame;
    private String mode;

    public ConnectionRequest(String username, String salt, String password, boolean signingUp, String mode)

    {
        this.username = username;
        this.password = password;
        this.existingGame = signingUp;
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }


    public boolean getExistingGame() {
        return existingGame;
    }

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

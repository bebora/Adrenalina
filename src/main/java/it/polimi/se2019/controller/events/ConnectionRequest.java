package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.view.View;

public class ConnectionRequest implements EventVisitable {

    String username;
    String salt;
    String password;
    boolean existingGame;
    String mode;
    View vv;

    public ConnectionRequest(String username, String salt, String password, boolean signingUp, String mode)

    {
        this.username = username;
        this.salt = salt;
        this.password = password;
        this.existingGame = signingUp;
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setVv(View vv) {
        this.vv = vv;
    }

    public View getVv() {
        return vv;
    }

    public boolean getExistingGame() {
        return existingGame;
    }

    public String getToken() {
        if (!(username.contains("$") || password.contains("$")))
            return username + "$" + salt + "$" + (password+salt).hashCode();
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

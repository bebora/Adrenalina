package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.view.View;

public class ConnectionRequest implements EventVisitable {

    String username;
    String salt;
    String password;
    View vv;

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }


    public void setVv(View vv) {
        this.vv = vv;
    }

    public View getVv() {
        return vv;
    }

    public String getToken() {
        if (!(username.contains("$") || password.contains("$")))
            return username + "$" + salt + "$" + (password+salt).hashCode();
        else
            throw new IllegalArgumentException();
    }
}

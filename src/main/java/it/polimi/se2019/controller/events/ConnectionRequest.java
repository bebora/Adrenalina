package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.view.View;

public class ConnectionRequest implements EventVisitable {
    String username;
    View vv;

    //TODO Insert encoded password for more security
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    public String getUsername() {
        return username;
    }

    public void setVv(View vv) {
        this.vv = vv;
    }

    public View getVv() {
        return vv;
    }
}

package it.polimi.se2019.network.updatemessage;

import it.polimi.se2019.view.UpdateVisitor;

/**
 * Represent a ping sent from the server
 */
public class PingUpdate implements UpdateVisitable{
    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }
}

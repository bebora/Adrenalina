package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.view.UpdateVisitor;

public class PingUpdate implements UpdateVisitable{
    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }
}

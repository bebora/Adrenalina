package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.view.UpdateVisitor;

public class SuccessConnectionUpdate implements UpdateVisitable{
    private String token;

    public SuccessConnectionUpdate(String token ) {
        this.token = token;
    }
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public String getToken() {
        return token;
    }
}

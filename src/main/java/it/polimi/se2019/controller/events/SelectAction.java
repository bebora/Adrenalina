package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

public class SelectAction implements EventVisitable {
    private String selectedAction;
    String token;
    public SelectAction(String selectedAction, String token){
        this.selectedAction = selectedAction;
        this.token = token;
    }
    public void accept(EventVisitor visitor){
        visitor.visit(this);
    }
}

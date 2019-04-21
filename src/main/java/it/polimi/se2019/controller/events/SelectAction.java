package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.view.ViewAction;

public class SelectAction implements EventVisitable {
    private ViewAction selectedAction;
    public void accept(EventVisitor visitor){
        visitor.visit(this);
    }
}

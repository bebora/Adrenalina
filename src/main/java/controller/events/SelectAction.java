package controller.events;

import controller.Visitable;
import controller.Visitor;
import model.actions.Action;

public class SelectAction implements Visitable {
    private Action selectedAction;
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}

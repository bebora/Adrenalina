package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitor;

//TODO:make SelectStop implement EventVisitable
public class SelectStop{
    private boolean revertAction;
    public SelectStop(boolean revertAction){this.revertAction = revertAction;}

    public boolean isRevertAction() {
        return revertAction;
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}

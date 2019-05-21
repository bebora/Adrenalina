package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

/**
 * Event used by view to choose from its available actions
 */
public class SelectAction implements EventVisitable {
    private String selectedAction;
    public SelectAction(String selectedAction){
        this.selectedAction = selectedAction;
    }
    public void accept(EventVisitor visitor){
        visitor.visit(this);
    }

    public String getSelectedAction() {
        return selectedAction;
    }
}

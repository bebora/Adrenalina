package it.polimi.se2019.model.UpdateMessage;

import it.polimi.se2019.view.UpdateVisitor;
import it.polimi.se2019.view.ViewAction;

import java.util.List;

/**
 * Represent change of available actions to receiving view
 */
public class AvailableActionsUpdate implements UpdateVisitable {
    /**
     * List of new ViewActions from which the view can choose from
     */
    private List<ViewAction> actions;
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public List<ViewAction> getActions() {
        return actions;
    }
}

package it.polimi.se2019.controller.updatemessage;

import it.polimi.se2019.view.SelectableOptionsWrapper;
import it.polimi.se2019.view.UpdateVisitor;

/**
 * Represent the wrapper containing the options that the client can select
 */
public class SelectableOptionsUpdate implements UpdateVisitable {
    private SelectableOptionsWrapper selectableOptionsWrapper;
    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public SelectableOptionsWrapper getSelectableOptionsWrapper() {
        return selectableOptionsWrapper;
    }

    public SelectableOptionsUpdate(SelectableOptionsWrapper selectableOptionsWrapper) {
        this.selectableOptionsWrapper = selectableOptionsWrapper;
    }
}

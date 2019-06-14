package it.polimi.se2019.controller.updatemessage;

import it.polimi.se2019.controller.AcceptableTypes;
import it.polimi.se2019.view.SelectableOptionsWrapper;
import it.polimi.se2019.view.UpdateVisitor;

public class SelectableOptionsUpdate implements UpdateVisitable {
    private SelectableOptionsWrapper selectableOptionsWrapper;
    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public SelectableOptionsWrapper getSelectableOptionsWrapper() {
        return selectableOptionsWrapper;
    }

    public SelectableOptionsUpdate(AcceptableTypes acceptableTypes) {
        selectableOptionsWrapper = new SelectableOptionsWrapper(acceptableTypes);
    }
}

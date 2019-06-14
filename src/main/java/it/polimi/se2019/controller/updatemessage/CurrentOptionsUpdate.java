package it.polimi.se2019.controller.updatemessage;

import it.polimi.se2019.view.UpdateVisitor;

import java.util.List;

public class CurrentOptionsUpdate implements UpdateVisitable {
    private List<String> options;

    public List<String> getOptions() {
        return options;
    }

    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }
    public CurrentOptionsUpdate(List<String> options) {
        this.options = options;
    }
}

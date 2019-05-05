package it.polimi.se2019.view;

import it.polimi.se2019.model.updatemessage.UpdateVisitable;

public class ClientView extends View {
    @Override
    public void update(UpdateVisitable u) {
        u.accept(visitor);
    }
    public ClientView() {
        super();
        this.visitor = new ConcreteUpdateVisitor(this);
    }
}

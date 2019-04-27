package it.polimi.se2019.view;

import it.polimi.se2019.model.updatemessage.UpdateVisitable;

public class ClientView extends View {
    private UpdateVisitor visitor = new ConcreteUpdateVisitor(this);

    @Override
    public void update(UpdateVisitable u) {
        u.accept(visitor);
    }
}

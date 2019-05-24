package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

public class SelectEffect implements EventVisitable {
    private String effect;

    public SelectEffect(String effect) {
        this.effect = effect;
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    public String getEffect() {
        return effect;
    }
}

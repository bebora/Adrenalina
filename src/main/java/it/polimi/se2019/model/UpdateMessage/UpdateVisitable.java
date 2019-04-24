package it.polimi.se2019.model.UpdateMessage;

import it.polimi.se2019.view.UpdateVisitor;

public interface UpdateVisitable {
    //TODO details of what to update; implement the visitor pattern
    void accept(UpdateVisitor visitor);
}

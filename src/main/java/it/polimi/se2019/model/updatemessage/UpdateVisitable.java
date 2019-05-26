package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.view.UpdateVisitor;

/**
 * Interface implemented by updates sent with the socket network implementation
 */
public interface UpdateVisitable {
    //TODO details of what to update; implement the visitor pattern
    void accept(UpdateVisitor visitor);
}

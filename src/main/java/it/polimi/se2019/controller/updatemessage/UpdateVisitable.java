package it.polimi.se2019.controller.updatemessage;

import it.polimi.se2019.view.UpdateVisitor;

/**
 * Interface implemented by updates sent with the socket network implementation
 */
public interface UpdateVisitable {
    void accept(UpdateVisitor visitor);
}

package it.polimi.se2019;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.model.UpdateMessage.UpdateVisitable;
import it.polimi.se2019.model.UpdateMessage.UpdateWrapper;

public interface Observer {
    // Update using an event visitable, method will be implemented by the controllers for receiving events (selection of resources) from the client
    void update(EventVisitable e);


    // Update using an UpdateVisitable describing how the view need to be modified
    // TODO type may be changed to UpdateWrapper
    void update(UpdateVisitable update);


}

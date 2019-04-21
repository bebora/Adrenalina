package it.polimi.se2019.view;

import it.polimi.se2019.Observer;
import it.polimi.se2019.controller.Visitable;

public class VirtualView extends View implements Observer {
    //TODO it.polimi.se2019.Observer class, the it.polimi.se2019.controller update the it.polimi.se2019.model, the it.polimi.se2019.model update the VirtualView passing a message with info on update needed, and the virtualview using the server interface updates the corresponding views

    /**
     * //TODO used by model to update, passing updates to do to the virtualview
     * @param v
     */
    public void update(Visitable v) {
        //TODO what to do when updated
    }
}

package it.polimi.se2019.network;

import it.polimi.se2019.model.UpdateMessage.Update;

public interface ServerInterface {
    //TODO used from the it.polimi.se2019.controller for possible choices and type of choice, needs to create different messages accordingly
    //TODO used from the it.polimi.se2019.model to update ALL the views or the it.polimi.se2019.view corresponding to the player for a change
    //TODO used for handshaking


    /**
     * Updates the client related to the interface with an Update type message
     * @param u
     */
    void update(Update u);
}

package it.polimi.se2019.model;

import it.polimi.se2019.ApplyOn;

public class UserInputRequest extends RuntimeException{
    String objectNeeded;
    ApplyOn applyOnPlayer;

    public UserInputRequest(String objectNeeded, ApplyOn applyOnPlayer) {
        this.objectNeeded = objectNeeded;
        this.applyOnPlayer = applyOnPlayer;
    }
    @Override
    public String getMessage() {
        return this.objectNeeded;
    }
}


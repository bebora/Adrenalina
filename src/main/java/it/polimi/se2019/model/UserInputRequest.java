package it.polimi.se2019.model;

import it.polimi.se2019.ApplyOnPlayer;

import java.util.function.Function;

public class UserInputRequest extends RuntimeException{
    String objectNeeded;
    ApplyOnPlayer applyOnPlayer;

    public UserInputRequest(String objectNeeded, ApplyOnPlayer applyOnPlayer) {
        this.objectNeeded = objectNeeded;
        this.applyOnPlayer = applyOnPlayer;
    }
    @Override
    public String getMessage() {
        return this.objectNeeded;
    }
}


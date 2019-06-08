package it.polimi.se2019.model.actions;

import java.util.ArrayList;

public class Reload extends Action{
    public Reload() {
        movements = 0;
        subActions = new ArrayList<>();
        subActions.add(SubAction.SHOOT);
    }

    @Override
    public String toString() {
        return "RELOAD";
    }
}

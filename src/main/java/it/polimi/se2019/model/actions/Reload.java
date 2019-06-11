package it.polimi.se2019.model.actions;

import java.util.ArrayList;

public class Reload extends Action{
    public Reload() {
        movements = 0;
        subActions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            subActions.add(SubAction.RELOAD);
        }
    }

    @Override
    public String toString() {
        return "RELOAD";
    }
}

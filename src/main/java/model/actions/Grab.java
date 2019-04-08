package model.actions;

import java.util.ArrayList;

public class Grab extends Action {
    public Grab(){
        subActions = new ArrayList<>();
        subActions.add(SubAction.MOVE);
        subActions.add(SubAction.GRAB);
        movements = 1;
    }

    @Override
    public void updateOnHealth() {
        movements = 2;
    }

    @Override
    public void updateOnFrenzy(Boolean afterFirst) {
        if(afterFirst)
            movements = 3;
        else
            movements = 2;
    }
}

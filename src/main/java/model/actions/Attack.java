package model.actions;

import java.util.ArrayList;

public class Attack extends Action {
    public Attack(){
        movements = 0;
        subActions = new ArrayList<>();
        subActions.add(SubAction.SHOOT);
    }

    @Override
    public void updateOnHealth(int damage) {
        if(damage > 5) {
            subActions.add(0, SubAction.MOVE);
            movements = 1;
        }
    }

    @Override
    public void updateOnFrenzy(Boolean afterFirst) {
        subActions.add(0,SubAction.RELOAD);
        subActions.add(0,SubAction.MOVE);
        if(afterFirst)
            movements = 2;
        else
            movements = 1;
    }
}

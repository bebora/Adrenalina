package it.polimi.se2019.model.actions;

import java.util.ArrayList;

public class Attack extends Action {
    /**
     * Stores if the health update has already been applied
     */
    private boolean alreadyUpdated = false;
    public Attack(){
        movements = 0;
        subActions = new ArrayList<>();
        subActions.add(SubAction.SHOOT);
    }

    @Override
    public void updateOnHealth(int damage) {
        if(damage > 5 && !alreadyUpdated) {
            subActions.add(0, SubAction.MOVE);
            movements = 1;
            alreadyUpdated = true;
        }
    }

    @Override
    public void updateOnFrenzy(Boolean afterFirst) {
        if (!alreadyUpdated)
            subActions.add(0,SubAction.MOVE);
        subActions.add(1,SubAction.RELOAD);
        if(afterFirst)
            movements = 2;
        else
            movements = 1;
    }

    public String toString() {
        return "ATTACK";
    }
}

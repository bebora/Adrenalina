package it.polimi.se2019.model.actions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Allows the player to attack using a weapon.
 */
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
    public void reset() {
        movements = 0;
        subActions = new ArrayList<>(Collections.singletonList(SubAction.SHOOT));
        alreadyUpdated = false;
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

    @Override
    public String toString() {
        return "ATTACK";
    }
}

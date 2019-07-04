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

    /**
     * If damages are higher than 5, add a move before the attack
     * @param damage current damage of the player of which action is updated
     */
    @Override
    public void updateOnHealth(int damage) {
        if(damage > 5 && !alreadyUpdated) {
            subActions.add(0, SubAction.MOVE);
            movements = 1;
            alreadyUpdated = true;
        }
    }

    /**
     * Reset the action at its starting point
     */
    @Override
    public void reset() {
        movements = 0;
        subActions = new ArrayList<>(Collections.singletonList(SubAction.SHOOT));
        alreadyUpdated = false;
    }

    /**
     * Update the action for when the frenzy mode is on
     * @param afterFirst if the player is after or before the first
     */
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

package it.polimi.se2019.model.actions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Allows movement in the board from the player.
 */
public class Move extends Action {
    public Move() {
        subActions = new ArrayList<>();
        subActions.add(SubAction.MOVE);
        movements = 3;
    }

    /**
     * Reset the action at its starting point
     */
    @Override
    public void reset() {
        movements = 3;
        subActions = new ArrayList<>(Collections.singletonList(SubAction.MOVE));
    }

    /**
     * In frenzy, every player not after the first gets 4 movements.
     * @param afterFirst
     */
    @Override
    public void updateOnFrenzy(Boolean afterFirst) {
        if (!afterFirst)
            movements = 4;
    }

    @Override
    public String toString() {
        return "MOVE";
    }
}

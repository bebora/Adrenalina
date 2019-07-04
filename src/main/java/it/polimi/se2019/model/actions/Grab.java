package it.polimi.se2019.model.actions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Allows the player to grab a {@link it.polimi.se2019.model.cards.Weapon} or an {@link it.polimi.se2019.model.ammos.AmmoCard}
 */
public class Grab extends Action {
    public Grab(){
        subActions = new ArrayList<>();
        subActions.add(SubAction.MOVE);
        subActions.add(SubAction.GRAB);
        movements = 1;
    }

    /**
     * Reset the action at its starting point
     */
    @Override
    public void reset() {
        movements = 1;
        subActions = new ArrayList<>(Arrays.asList(SubAction.MOVE, SubAction.GRAB));
    }

    /**
     * If damage is greater than two, the player can do two movements before grabbing
     * @param damage the player current damages
     */
    @Override
    public void updateOnHealth(int damage) {
        if(damage > 2)
            movements = 2;
    }

    /**
     * Update the action for when the frenzy mode is on, allowing 3 or 2 movements before grab
     * @param afterFirst if the player is after or before the first
     */
    @Override
    public void updateOnFrenzy(Boolean afterFirst) {
        if(afterFirst)
            movements = 3;
        else
            movements = 2;
    }

    @Override
    public String toString(){
        return "GRAB";
    }
}

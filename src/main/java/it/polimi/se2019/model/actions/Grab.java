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

    @Override
    public void reset() {
        movements = 1;
        subActions = new ArrayList<>(Arrays.asList(SubAction.MOVE, SubAction.GRAB));
    }

    @Override
    public void updateOnHealth(int damage) {
        if(damage > 2)
            movements = 2;
    }

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

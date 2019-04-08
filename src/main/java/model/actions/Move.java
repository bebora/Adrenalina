package model.actions;

import java.util.ArrayList;

public class Move extends Action {
    public Move() {
        subActions = new ArrayList<>();
        subActions.add(SubAction.MOVE);
        movements = 3;
    }

    @Override
    public void updateOnFrenzy(Boolean afterFirst) {
        if (!afterFirst)
            movements = 4;
        else
            subActions = new ArrayList<>();
    }
}

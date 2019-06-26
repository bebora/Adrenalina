package it.polimi.se2019.model.actions;

import it.polimi.se2019.GameProperties;

import java.util.ArrayList;

/**
 * Last action in the turn when final frenzy mode is not activated.
 * It allows player to reload weapons.
 */
public class Reload extends Action{
    public Reload() {
        movements = 0;
        subActions = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(GameProperties.getInstance().getProperty("max_weapons")); i++) {
            subActions.add(SubAction.RELOAD);
        }
    }

    @Override
    public String toString() {
        return "RELOAD";
    }
}

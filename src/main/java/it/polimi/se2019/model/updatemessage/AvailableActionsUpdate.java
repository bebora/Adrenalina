package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.view.UpdateVisitor;
import it.polimi.se2019.view.ViewAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent change of available actions to receiving view
 */
public class AvailableActionsUpdate implements UpdateVisitable {
    /**
     * List of new ViewActions from which the view can choose from
     */
    private List<ViewAction> actions;

    /**
     * Player whose actions have been updated
     */
    private String playerId;

    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public List<ViewAction> getActions() {
        return actions;
    }

    public String getPlayerId() {
        return playerId;
    }

    /**
     * Constructor from Player, expect to have the new actions already in the Player
     * @param player
     */
    public AvailableActionsUpdate(Player player) {
        List<Action> realActions = player.getActions();
        this.actions = new ArrayList<>();
        for (Action a : realActions){
            this.actions.add(new ViewAction(a));
        }
        this.playerId = player.getId();
    }
}

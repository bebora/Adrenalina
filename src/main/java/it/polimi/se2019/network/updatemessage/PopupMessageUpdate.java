package it.polimi.se2019.network.updatemessage;

import it.polimi.se2019.view.UpdateVisitor;

/**
 * Represent message that must be shown to player, e.g. after an invalid request by the player
 */
public class PopupMessageUpdate implements UpdateVisitable{
    private String message;
    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public String getMessage() {
        return message;
    }

    public PopupMessageUpdate(String givenMessage) {
        this.message = givenMessage;
    }
}

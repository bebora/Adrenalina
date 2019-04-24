package it.polimi.se2019.model.UpdateMessage;

import it.polimi.se2019.view.UpdateVisitor;

/**
 * Represent movement of any player to new coordinates
 */
public class MovePlayerUpdate implements UpdateVisitable {
    private String playerId;
    private int posx;
    private int posy;

    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public int getPosx() {
        return posx;
    }

    public int getPosy() {
        return posy;
    }

    public String getPlayerId() {
        return playerId;
    }
}

package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.model.Player;
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

    /**
     * Constructor from Player, expect to have the new tile already in the Player
     * @param player
     */
    public MovePlayerUpdate(Player player) {
        this.playerId = player.getId();
        this.posx = player.getTile().getPosx();
        this.posy = player.getTile().getPosy();
    }
}

package it.polimi.se2019.model.updatemessage;

import java.util.List;
import it.polimi.se2019.view.UpdateVisitor;

/**
 * Represent Ammos Card taken from a tile and replaced with a new one
 */
public class AmmosTakenUpdate {
    /**
     * Id of the player who took the ammos
     */
    private String player;
    /**
     * List of the ammos the player has after taking the ones on the tile where it stands
     */
    private List<String> playerAmmos;
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public String getPlayer() {
        return player;
    }

    public List<String> getPlayerAmmos() {
        return playerAmmos;
    }
}

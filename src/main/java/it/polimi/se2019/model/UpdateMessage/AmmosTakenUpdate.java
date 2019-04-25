package it.polimi.se2019.model.UpdateMessage;

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
     * List of the ammos in the new card that replaces the one just taken
     */
    private List<String> newAmmos;
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public String getPlayer() {
        return player;
    }

    public List<String> getNewAmmos() {
        return newAmmos;
    }
}

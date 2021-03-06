package it.polimi.se2019.network.updatemessage;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.view.UpdateVisitor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent Ammos Card taken from a tile and replaced with a new one
 */
public class AmmosTakenUpdate implements UpdateVisitable{
    /**
     * Id of the player who took the ammos
     */
    private String playerId;
    /**
     * List of the ammos the player has after taking the ones on the tile where it stands
     */
    private List<String> playerAmmos;
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public String getPlayerId() {
        return playerId;
    }

    public List<String> getPlayerAmmos() {
        return playerAmmos;
    }

    /**
     * Constructor from Player, expect to have the new ammos already in the Player
     * @param player
     */
    public AmmosTakenUpdate(Player player) {
        this.playerId = player.getId();
        this.playerAmmos = player.getAmmos().stream().map(Ammo::name).collect(Collectors.toList());
    }
}

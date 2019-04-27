package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.UpdateVisitor;
import it.polimi.se2019.view.ViewTile;

/**
 * Represent Weapon Card taken from a spawn tile but not already replaced
 */
public class WeaponTakenUpdate {
    private String takenWeapon;

    /**
     * Weapon which has been removed due to weapon number limit
     */
    private String discardedWeapon;

    private int posx;

    private int posy;

    /**
     * Player who takes the weapon
     */
    private String playerId;

    public String getTakenWeapon() {
        return takenWeapon;
    }

    public String getDiscardedWeapon() {
        return discardedWeapon;
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
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Default constructor without any discarded weapon
     * @param tile
     * @param weapon
     * @param player
     */
    public WeaponTakenUpdate(Tile tile, Weapon weapon, Player player) {
        this.posx = tile.getPosx();
        this.posy = tile.getPosy();
        this.takenWeapon = weapon.getName();
        this.playerId = player.getId();
        this.discardedWeapon = null;
    }

    /**
     * Constructor for when a weapon has been discarded
     * @param tile
     * @param weapon
     * @param player
     * @param discardedWeapon
     */
    public WeaponTakenUpdate(Tile tile, Weapon weapon, Player player, Weapon discardedWeapon) {
        this(tile, weapon, player);
        this.discardedWeapon = discardedWeapon.getName();
    }
}

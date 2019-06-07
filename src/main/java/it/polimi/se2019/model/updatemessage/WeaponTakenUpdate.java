package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.UpdateVisitor;
import it.polimi.se2019.view.ViewWeapon;

/**
 * Represent Weapon Card taken from a spawn tile but not already replaced
 */
public class WeaponTakenUpdate implements UpdateVisitable {
    private ViewWeapon takenWeapon;

    /**
     * Weapon which has been removed due to weapon number limit
     */
    private ViewWeapon discardedWeapon;

    /**
     * Player who takes the weapon
     */
    private String playerId;

    public ViewWeapon getTakenWeapon() {
        return takenWeapon;
    }

    public ViewWeapon getDiscardedWeapon() {
        return discardedWeapon;
    }

    public String getPlayerId() {
        return playerId;
    }
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Default constructor without any discarded weapon
     * @param weapon
     * @param player
     */
    public WeaponTakenUpdate(Weapon weapon, Player player) {
        this.takenWeapon = new ViewWeapon(weapon);
        this.playerId = player.getId();
        this.discardedWeapon = null;
    }

    /**
     * Constructor for when a weapon has been discarded
     * @param weapon
     * @param player
     * @param discardedWeapon
     */
    public WeaponTakenUpdate(Weapon weapon, Weapon discardedWeapon, Player player) {
        this(weapon, player);
        this.discardedWeapon = new ViewWeapon(discardedWeapon);
    }
}

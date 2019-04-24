package it.polimi.se2019.model.UpdateMessage;

import it.polimi.se2019.view.UpdateVisitor;

/**
 * Represent Weapon Card taken from a spawn tile and replaced with a new one if possible
 */
public class WeaponTakenUpdate {
    private String takedWeapon;

    /**
     * Weapon which replaces the weapon slot freed after the weapon taken event
     * May be null or ""
     */
    private String newWeapon;

    /**
     * Player who takes the weapon
     */
    private String player;

    public String getTakedWeapon() {
        return takedWeapon;
    }

    public String getNewWeapon() {
        return newWeapon;
    }

    public String getPlayer() {
        return player;
    }
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }
}

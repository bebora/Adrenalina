package model;

/**
 * Represent the actions that the Player can do; in order, the player can:
 * Move - Grab - Reload - Shoot
 */
public class Action {

	/**
	 * Maximum number of movements that the player can do
	 */
	private int movements;

	/**
	 * If True, the player can grab a weapon after the movement.
	 */
	private Boolean grab;

	/**
	 * If True, the player can choose a weapon to shoot.
	 */
	private Boolean shoot;

	/**
	 * If True, the player can reload the weapon before shooting.
	 */
	private Boolean reload;

	public int getMovements() {
		return movements;
	}

	public void setMovements(int movements) {
		this.movements = movements;
	}

	public Boolean getGrab() {
		return grab;
	}

	public void setGrab(Boolean grab) {
		this.grab = grab;
	}

	public Boolean getShoot() {
		return shoot;
	}

	public void setShoot(Boolean shoot) {
		this.shoot = shoot;
	}

	public Boolean getReload() {
		return reload;
	}

	public void setReload(Boolean reload) {
		this.reload = reload;
	}
}
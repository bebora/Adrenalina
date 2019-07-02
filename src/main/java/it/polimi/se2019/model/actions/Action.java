package it.polimi.se2019.model.actions;

import java.util.List;

/**
 * Represent the actions that the Player can do; in order, the player can:
 * Move - Grab - Reload - Shoot
 */
public abstract class Action {
	/**
	 * Maximum number of movements that the player can do
	 */
	protected int movements;
	/**
	 * Ordered list of possible subAction
	 */
	protected List<SubAction> subActions;

	public int getMovements() {
		return movements;
	}

	public List<SubAction> getSubActions() {
		return subActions;
	}

	/**
	 * Update action properties based on damage
	 * @param damage
	 */
	public void updateOnHealth(int damage){}

	/**
	 * Update action properties when entering frenzy mode
	 * @param afterFirst
	 */
	public void updateOnFrenzy(Boolean afterFirst){}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Action) {
			return obj.toString().equals(this.toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public abstract void reset();
}
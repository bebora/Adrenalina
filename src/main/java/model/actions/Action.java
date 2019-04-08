package model.actions;

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

	public void updateOnHealth(){};

	public void updateOnFrenzy(Boolean afterFirst){};
}
package model;

import java.util.*;

/**
 * Represent the actions that can be activated shooting with a Weapon
 */
public class Effect {


	public Effect() {
	}

	/**
	 * List of moves that make up the Effect
	 */
	private List <Move> moves;

	/**
	 * List of damages that make up the effect
	 */
	private List<DealDamage> damages;

	/**
	 * Order in which moves and damages need to be executed
	 */
	private List<ActionType> order;

	/**
	 * If True, the effect has already been activated.
	 */
	private Boolean activated;

	/**
	 * Ammo cost to use the effect
	 */
	private List<Ammo> cost;

	/**
	 * Name of the effect
	 */
	private String name;

	/**
	 * Direction for linear effects
	 */
	private Direction direction;

	/**
	 * Textual description of the effect
	 */
	private String desc;

	/**
	 * Represent the order (absolute) in which the Effect need to be activated.
	 */
	private int absolutePriority;

	/**
	 * Represent the order (relative) in which the Effect need to be activated. Number can be positive or negative:
	 * Positive: must be after the n° effect, considering for the n-order the order used in the weapons List
	 * Negative: must be before the n° effect, considering for the n-order the order used in the weapons List
	 */
	private List<Integer> relativePriority;

	public List<Move> getMoves() {
		return moves;
	}

	public void setMoves(List<Move> moves) {
		this.moves = moves;
	}

	public List<DealDamage> getDamages() {
		return damages;
	}

	public void setDamages(List<DealDamage> damages) {
		this.damages = damages;
	}

	public List<ActionType> getOrder() {
		return order;
	}

	public void setOrder(List<ActionType> order) {
		this.order = order;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public List<Ammo> getCost() {
		return cost;
	}

	public void setCost(List<Ammo> cost) {
		this.cost = cost;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setAbsolutePriority(int absolutePriority) {
		this.absolutePriority = absolutePriority;
	}

	public void setRelativePriority(List<Integer> relativePriority) {
		this.relativePriority = relativePriority;
	}
}
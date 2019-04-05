package model;

import java.util.*;


public class Effect {


	public Effect() {
	}


	private List <Move> moves;


	private List<DealDamage> damages;


	private List<ActionType> order;


	private Boolean activated;

	/**
	 * Ammo cost to use the effect
	 */
	private List<Ammo> cost;

	private String name;

	/**
	 * Direction for linear effects
	 */
	private Direction direction;


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
}
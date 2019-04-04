package Model;

import java.util.*;


public class Effect {


	public Effect() {
	}


	private ArrayList <Move> moves;


	private ArrayList<DealDamage> damages;


	private ArrayList<ActionType> order;


	private Boolean activated;

	/**
	 * Ammo cost to use the effect
	 */
	private ArrayList<Ammo> cost;


	private Priority priority;


	private String name;

	/**
	 * Direction for linear effects
	 */
	private Direction direction;


	private String desc;

	public ArrayList<Move> getMoves() {
		return moves;
	}

	public void setMoves(ArrayList<Move> moves) {
		this.moves = moves;
	}

	public ArrayList<DealDamage> getDamages() {
		return damages;
	}

	public void setDamages(ArrayList<DealDamage> damages) {
		this.damages = damages;
	}

	public ArrayList<ActionType> getOrder() {
		return order;
	}

	public void setOrder(ArrayList<ActionType> order) {
		this.order = order;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public ArrayList<Ammo> getCost() {
		return cost;
	}

	public void setCost(ArrayList<Ammo> cost) {
		this.cost = cost;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
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
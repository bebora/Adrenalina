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






}
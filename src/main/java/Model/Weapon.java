package Model;

import java.util.*;


public class Weapon {


	public Weapon() {
	}


	private ArrayList<Effect> effects;


	private ArrayList<Ammo> cost;

	/**
	 * Stores players that have received some effects and must be kept to select them to receive other effects
	 */
	private ArrayList<Player> targetPlayers;


	private String name;

	/**
	 * Stores players that have received some effects and must be kept to prevent them to receive some other effects
	 */
	private ArrayList<Player> blackListPlayers;


	private Boolean loaded;




}
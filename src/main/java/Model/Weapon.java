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

	public ArrayList<Effect> getEffects() {
		return effects;
	}

	public void setEffects(ArrayList<Effect> effects) {
		this.effects = effects;
	}

	public ArrayList<Ammo> getCost() {
		return cost;
	}

	public void setCost(ArrayList<Ammo> cost) {
		this.cost = cost;
	}

	public ArrayList<Player> getTargetPlayers() {
		return targetPlayers;
	}

	public void setTargetPlayers(ArrayList<Player> targetPlayers) {
		this.targetPlayers = targetPlayers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Player> getBlackListPlayers() {
		return blackListPlayers;
	}

	public void setBlackListPlayers(ArrayList<Player> blackListPlayers) {
		this.blackListPlayers = blackListPlayers;
	}

	public Boolean getLoaded() {
		return loaded;
	}

	public void setLoaded(Boolean loaded) {
		this.loaded = loaded;
	}
}
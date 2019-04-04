package model;

import java.util.*;


public class Weapon {


	public Weapon() {

	}

	private List<Effect> effects;

	private List<Ammo> cost;

	/**
	 * Stores players that have received some effects and must be kept to select them to receive other effects
	 */
	private List<Player> targetPlayers;


	private String name;

	/**
	 * Stores players that have received some effects and must be kept to prevent them to receive some other effects
	 */
	private List<Player> blackListPlayers;


	private Boolean loaded;

	public List<Effect> getEffects() {
		return effects;
	}

	public void setEffects(List<Effect> effects) {
		this.effects = effects;
	}

	public List<Ammo> getCost() {
		return cost;
	}

	public void setCost(List<Ammo> cost) {
		this.cost = cost;
	}

	public List<Player> getTargetPlayers() {
		return targetPlayers;
	}

	public void setTargetPlayers(List<Player> targetPlayers) {
		this.targetPlayers = targetPlayers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Player> getBlackListPlayers() {
		return blackListPlayers;
	}

	public void setBlackListPlayers(List<Player> blackListPlayers) {
		this.blackListPlayers = blackListPlayers;
	}

	public Boolean getLoaded() {
		return loaded;
	}

	public void setLoaded(Boolean loaded) {
		this.loaded = loaded;
	}
}
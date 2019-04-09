package model;
import model.ammos.Ammo;

import java.util.*;


public class Weapon {


	public Weapon() {

	}

	public static class Builder {

		private List<Effect> effects;
		private List<Ammo> cost;
		private List<Player> targetPlayers = new ArrayList<>();
		private List<Player> blackListPlayers = new ArrayList<>();
		private Boolean loaded = false;
		private String name;



		public Builder(List<Effect> effects) {
			this.effects = effects;
		}

		public Builder setCost(List<Ammo> cost) {
			this.cost = cost;
			return this;

		}

		public Builder setLoaded(Boolean loaded) {
			this.loaded = loaded;
			return this;

		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}


		public Weapon build() {
			return new Weapon(this);
		}
	}

	public Weapon(Builder builder) {
		effects = builder.effects;
		cost = builder.cost;
		targetPlayers = builder.targetPlayers;
		blackListPlayers = builder.blackListPlayers;
		name = builder.name;
		loaded = builder.loaded;
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

	public List<Ammo> getCost() {
		return cost;
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
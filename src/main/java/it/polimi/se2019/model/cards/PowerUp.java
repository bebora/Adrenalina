package it.polimi.se2019.model.cards;


import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PowerUp {

	/**
	 * Ammo type that can be obtained by discarding the PowerUp to pay a cost
	 */
	private Ammo discardAward;

	/**
	 * Moment in which the PowerUp can be used
	 */
	private Moment applicability;

	/**
	 * Player saved in the PowerUp temporarily in case of DAMAGING or DAMAGED status
	 */
	private Player player;

	/**
	 * Effect obtained by using the PowerUp
	 */
	private Effect effect;

	/**
	 * Name of the PowerUp
	 */
	private String name;

	public static class Builder {
		private Ammo discardAward;
		private Moment applicability;
		private Effect effect;
		private String name;

		public Builder setDiscardAward(Ammo discardAward) {
			this.discardAward = discardAward;
			return this;
		}

		public Builder setApplicability(Moment applicability) {
			this.applicability = applicability;
			return this;
		}

		public Builder setEffect(Effect effect) {
			this.effect = effect;
			return this;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		public  PowerUp build(){
			return new PowerUp(this);
		}
	}

	public PowerUp(Builder builder) {
		this.discardAward = builder.discardAward;
		this.applicability = builder.applicability;
		this.effect = builder.effect;
		this.name = builder.name;
	}

	public Effect getEffect() {
		return effect;
	}

	public Ammo getDiscardAward() {
		return discardAward;
	}

	public Moment getApplicability() {
		return applicability;
	}

	public String getName() {
		return name;
	}

	public static boolean checkCompatibility(List<PowerUp> powerUps, List<Ammo> ammos) {
		List<Ammo> relativeAmmos = powerUps.stream().map(PowerUp::getDiscardAward).collect(Collectors.toList());
		return relativeAmmos.containsAll(ammos);

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PowerUp powerUp = (PowerUp) o;
		return discardAward == powerUp.discardAward &&
				Objects.equals(name, powerUp.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(discardAward, name);
	}
}
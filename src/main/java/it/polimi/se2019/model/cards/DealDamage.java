package it.polimi.se2019.model.cards;


import it.polimi.se2019.model.ThreeState;

public class DealDamage {

	/**
	 * Amount of damages to give to the targets
	 */
	private int damagesAmount;

	/**
	 * Amount of marks to give to the targets
	 */
	private int marksAmount;

	/**
	 * Select what player(s) can be shot
	 */
	private Target target;

	/**
	 * Select where attacked player will go.
	 * TRUE: targetPlayers
	 * FALSE: blackListPLayers
	 * OPTIONAL: nowhere
	 */
	private ThreeState targeting;

	public static class Builder{
		private int damagesAmount;
		private int marksAmount;
		private ThreeState targeting;
		private Target target = new Target.Builder().build();

		public Builder setDamagesAmount(int damagesAmount) {
			this.damagesAmount = damagesAmount;
			return this;
		}

		public Builder setMarksAmount(int marksAmount) {
			this.marksAmount = marksAmount;
			return this;
		}

		public Builder setTargeting(ThreeState targeting) {
			this.targeting = targeting;
			return this;
		}

		public Builder setTarget(Target target) {
			this.target = target;
			return this;
		}
		public DealDamage build() {
			return new DealDamage(this);
		}
	}

	public DealDamage(Builder builder) {
		this.damagesAmount = builder.damagesAmount;
		this.marksAmount = builder.marksAmount;
		this.targeting = builder.targeting;
		this.target = builder.target;
	}

	public int getDamagesAmount() {
		return damagesAmount;
	}

	public int getMarksAmount() {
		return marksAmount;
	}

	public Target getTarget() {
		return target;
	}

	public ThreeState getTargeting() {
		return targeting;
	}

}
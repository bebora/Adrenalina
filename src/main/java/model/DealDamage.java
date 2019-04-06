package model;


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

	public int getDamagesAmount() {
		return damagesAmount;
	}

	public void setDamagesAmount(int damagesAmount) {
		this.damagesAmount = damagesAmount;
	}

	public int getMarksAmount() {
		return marksAmount;
	}

	public void setMarksAmount(int marksAmount) {
		this.marksAmount = marksAmount;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public ThreeState getTargeting() {
		return targeting;
	}

	public void setTargeting(ThreeState targeting) {
		this.targeting = targeting;
	}

}
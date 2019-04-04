package Model;


public class Move {


	public Move() {
	}

	/**
	 * If true move current player, else move targetSource player
	 */
	private Boolean self;

	/**
	 * Specify where selected players can go
	 */
	private Target targetDestination;

	/**
	 * Select where moved player will go. TRUE: targetPlayers
	 * FALSE: blackListPLayers
	 * OPTIONAL: nowhere
	 */
	private ThreeState targeting;

	/**
	 * Select players that can be selected to be moved
	 */
	private Target targetSource;

	public Boolean getSelf() {
		return self;
	}

	public void setSelf(Boolean self) {
		this.self = self;
	}

	public Target getTargetDestination() {
		return targetDestination;
	}

	public void setTargetDestination(Target targetDestination) {
		this.targetDestination = targetDestination;
	}

	public ThreeState getTargeting() {
		return targeting;
	}

	public void setTargeting(ThreeState targeting) {
		this.targeting = targeting;
	}

	public Target getTargetSource() {
		return targetSource;
	}

	public void setTargetSource(Target targetSource) {
		this.targetSource = targetSource;
	}
}
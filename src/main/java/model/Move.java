package model;


public class Move {

	/**
	 * Select the Object to move:
	 * 	SELF: move the current player
	 * 	PERSPECTIVE: move the perspective of the current player
	 * 	TARGETSOURCE: move the player(s) selected with targetSource
	 */
	private Boolean toMoveObject;

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
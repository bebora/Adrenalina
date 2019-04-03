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






}
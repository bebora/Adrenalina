package Model;


public class Target {


	public Target() {
	}

	/**
	 * TRUE: target must be visible from POV
	 * FALSE: target must not be visible from POV
	 * OPTIONAL: target can be anywhere
	 */
	private ThreeState visibility;

	/**
	 * How many players can be selected
	 */
	private int maxTargets;

	/**
	 * Minimum distance from POV
	 * Ignored if -1
	 */
	private int minDistance;

	/**
	 * Maximum distance from POV
	 * Ignored if -1
	 */
	private int maxDistance;


	private Area areaDamage;

	/**
	 * TRUE: target must be in same xcord or ycord of POV
	 * FALSE: target must not be in same xcord or ycord of POV
	 * OPTIONAL: not relevant
	 */
	private ThreeState cardinal;

	/**
	 * TRUE: targets must be in targetPlayers
	 * FALSE: targets must not be in targetPlayers
	 * OPTIONAL: not relevant
	 */
	private ThreeState checkTargetList;

	/**
	 * TRUE: targets must be in different Tile
	 * FALSE: targets must not be in different Tiles
	 * OPTIONAL: not relevant
	 */
	private ThreeState differentSquare;

	/**
	 * TRUE: targets must be in the same room of the POV
	 * FALSE: targets must not be in the same room of the POV
	 * OPTIONAL: not relevant
	 */
	private ThreeState samePlayerRoom;

	/**
	 * TRUE: 
	 * FALSE: 
	 * OPTIONAL: not relevant
	 */
	private ThreeState throughWalls;

	/**
	 * Point of view from where the matching targets are selected
	 */
	private PointOfView pointOfView;

	/**
	 * TRUE: targets must be in blackListPlayers
	 * FALSE: targets must not be in blackListPlayers
	 * OPTIONAL: not relevant
	 */
	private ThreeState checkBlackList;






}
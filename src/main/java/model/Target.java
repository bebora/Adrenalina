package model;

import static model.ThreeState.OPTIONAL;

public class Target {


	public Target() {
		visibility = OPTIONAL;
		maxTargets=-1;
		minDistance=0;
		maxDistance=-1;
		areaDamage=Area.SINGLE;
		cardinal=OPTIONAL;
		checkTargetList=OPTIONAL;
		differentSquare=OPTIONAL;
		samePlayerRoom=OPTIONAL;
		throughWalls=OPTIONAL;
		pointOfView=PointOfView.OWN;
		checkBlackList=OPTIONAL;
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


	/**
	 * Select the Area of the damage:
	 * SINGLE: all the targets (to maxtargets) get selected
	 * TILE: all the targets in the selected tile
	 * ROOM: all the target in the selected room get selected
	 */
	private Area areaDamage;

	/**
	 * TRUE: target must be in same xcord or ycord of POV
	 * FALSE: target must not be in same xcord or ycord of POV
	 * OPTIONAL: not relevant
	 */
	private ThreeState cardinal;

	/**
	 * If used in DealDamage:
	 * TRUE: targets must be in targetPlayers
	 * FALSE: targets must not be in targetPlayers
	 * OPTIONAL: not relevant
	 *
	 * If used in Move:
	 * TRUE: targets are the last $maxTargets of targetPlayers
	 * FALSE: targets must not be in targetPlayers
	 * OPTIONAL: not relevant
	 *
	 * In PowerUp:
	 * TRUE: target is the Player in player
	 * FALSE: target must not be the Player in player
	 * OPTIONAL: not relevant
	 */
	private ThreeState checkTargetList;

	/**
	 * If used in DealDamage:
	 * TRUE: targets must be in blackListPlayers
	 * FALSE: targets must not be in blackListPlayers
	 * OPTIONAL: not relevant
	 *
	 * If used in Move:
	 * TRUE: targets are the last $maxTargets of blackListPlayers
	 * FALSE: targets must not be in blackListPlayers
	 * OPTIONAL: not relevant
	 *
	 * In PowerUp, not relevant
	 */
	private ThreeState checkBlackList;

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


	public ThreeState getVisibility() {
		return visibility;
	}

	public void setVisibility(ThreeState visibility) {
		this.visibility = visibility;
	}

	public int getMaxTargets() {
		return maxTargets;
	}

	public void setMaxTargets(int maxTargets) {
		this.maxTargets = maxTargets;
	}

	public int getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}

	public Area getAreaDamage() {
		return areaDamage;
	}

	public void setAreaDamage(Area areaDamage) {
		this.areaDamage = areaDamage;
	}

	public ThreeState getCardinal() {
		return cardinal;
	}

	public void setCardinal(ThreeState cardinal) {
		this.cardinal = cardinal;
	}

	public ThreeState getCheckTargetList() {
		return checkTargetList;
	}

	public void setCheckTargetList(ThreeState checkTargetList) {
		this.checkTargetList = checkTargetList;
	}

	public ThreeState getDifferentSquare() {
		return differentSquare;
	}

	public void setDifferentSquare(ThreeState differentSquare) {
		this.differentSquare = differentSquare;
	}

	public ThreeState getSamePlayerRoom() {
		return samePlayerRoom;
	}

	public void setSamePlayerRoom(ThreeState samePlayerRoom) {
		this.samePlayerRoom = samePlayerRoom;
	}

	public ThreeState getThroughWalls() {
		return throughWalls;
	}

	public void setThroughWalls(ThreeState throughWalls) {
		this.throughWalls = throughWalls;
	}

	public PointOfView getPointOfView() {
		return pointOfView;
	}

	public void setPointOfView(PointOfView pointOfView) {
		this.pointOfView = pointOfView;
	}

	public ThreeState getCheckBlackList() {
		return checkBlackList;
	}

	public void setCheckBlackList(ThreeState checkBlackList) {
		this.checkBlackList = checkBlackList;
	}




}
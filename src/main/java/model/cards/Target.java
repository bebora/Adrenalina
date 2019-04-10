package model.cards;

import model.ThreeState;

public class Target {


	public static class Builder {
		ThreeState visibility;
		int maxTargets;
		int minDistance;
		int maxDistance = -1;
		Area areaDamage;
		ThreeState cardinal;
		ThreeState checkTargetList;
		ThreeState differentSquare;
		ThreeState samePlayerRoom;
		boolean throughWalls;
		PointOfView pointOfView;
		ThreeState checkBlackList;

		public Builder setVisibility(ThreeState visibility) {
			this.visibility = visibility;
			return this;
		}

		public Builder setMaxTargets(int maxTargets) {
			this.maxTargets = maxTargets;
			return this;
		}

		public Builder setMinDistance(int minDistance) {
			this.minDistance = minDistance;
			return this;
		}

		public Builder setMaxDistance(int maxDistance) {
			this.maxDistance = maxDistance;
			return this;
		}

		public Builder setAreaDamage(Area areaDamage) {
			this.areaDamage = areaDamage;
			return this;
		}

		public Builder setCardinal(ThreeState cardinal) {
			this.cardinal = cardinal;
			return this;
		}

		public Builder setCheckTargetList(ThreeState checkTargetList) {
			this.checkTargetList = checkTargetList;
			return this;
		}

		public Builder setDifferentSquare(ThreeState differentSquare) {
			this.differentSquare = differentSquare;
			return this;
		}

		public Builder setSamePlayerRoom(ThreeState samePlayerRoom) {
			this.samePlayerRoom = samePlayerRoom;
			return this;
		}

		public Builder setThroughWalls(boolean throughWalls) {
			this.throughWalls = throughWalls;
			return this;
		}

		public Builder setPointOfView(PointOfView pointOfView) {
			this.pointOfView = pointOfView;
			return this;
		}

		public Builder setCheckBlackList(ThreeState checkBlackList) {
			this.checkBlackList = checkBlackList;
			return this;
		}
		public Target build() {
			return new Target(this);
		}
	}

	public Target(Builder builder) {
		this.visibility = builder.visibility;
		this.maxDistance = builder.maxDistance;
		this.minDistance = builder.minDistance;
		this.maxTargets = builder.maxTargets;
		this.areaDamage = builder.areaDamage;
		this.cardinal = builder.cardinal;
		this.checkTargetList = builder.checkTargetList;
		this.checkBlackList = builder.checkBlackList;
		this.differentSquare = builder.differentSquare;
		this.samePlayerRoom = builder.samePlayerRoom;
		this.throughWalls = builder.throughWalls;
		this. pointOfView = builder.pointOfView;
	}

	/**
	 * TRUE: target must be visible from POV
	 * FALSE: target must not be visible from POV
	 * OPTIONAL: target can be anywhere
	 */
	private ThreeState visibility;

	/**
	 * How many targets can be selected
	 * -1:no limit on maximum targets
	 * 0:every target that satisfies the condition must be selected
	 * n:select up to n targets
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
	 * TRUE: targets must be in tar getPlayers
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
	private boolean throughWalls;

	/**
	 * Point of view from where the matching targets are selected
	 */
	private PointOfView pointOfView;


	public ThreeState getVisibility() {
		return visibility;
	}

	public int getMaxTargets() {
		return maxTargets;
	}

	public int getMinDistance() {
		return minDistance;
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public Area getAreaDamage() {
		return areaDamage;
	}

	public ThreeState getCardinal() {
		return cardinal;
	}

	public ThreeState getCheckTargetList() {
		return checkTargetList;
	}

	public ThreeState getDifferentSquare() {
		return differentSquare;
	}

	public ThreeState getSamePlayerRoom() {
		return samePlayerRoom;
	}

	public boolean getThroughWalls() {
		return throughWalls;
	}

	public PointOfView getPointOfView() {
		return pointOfView;
	}

	public ThreeState getCheckBlackList() {
		return checkBlackList;
	}




}
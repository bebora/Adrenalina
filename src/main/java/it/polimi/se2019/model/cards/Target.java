package it.polimi.se2019.model.cards;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Tile;

import java.util.List;
import java.util.function.Predicate;

public class Target {

	public static class Builder {
		ThreeState visibility = ThreeState.OPTIONAL;
		int minTargets = 1;
		int maxTargets = -1;
		int minDistance = 0;
		int maxDistance = -1;
		Area areaDamage = Area.SINGLE;
		ThreeState cardinal = ThreeState.OPTIONAL;
		ThreeState checkTargetList = ThreeState.OPTIONAL;
		ThreeState differentSquare = ThreeState.OPTIONAL;
		ThreeState samePlayerRoom = ThreeState.OPTIONAL;
		boolean throughWalls = true;
		PointOfView pointOfView = PointOfView.OWN;
		ThreeState checkBlackList = ThreeState.OPTIONAL;

		public Builder setVisibility(ThreeState visibility) {
			this.visibility = visibility;
			return this;
		}

		public Builder setMinTargets(int minTargets) {
			this.minTargets = minTargets;
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
		this.minTargets = builder.minTargets;
		this.maxTargets = builder.maxTargets;
		this.areaDamage = builder.areaDamage;
		this.cardinal = builder.cardinal;
		this.checkTargetList = builder.checkTargetList;
		this.checkBlackList = builder.checkBlackList;
		this.differentSquare = builder.differentSquare;
		this.samePlayerRoom = builder.samePlayerRoom;
		this.throughWalls = builder.throughWalls;
		this.pointOfView = builder.pointOfView;
	}

	/**
	 * TRUE: target must be visible from POV
	 * FALSE: target must not be visible from POV
	 * OPTIONAL: target can be anywhere
	 */
	private ThreeState visibility;

	/**
	 * Minimum amount of targets to be selected if possible
	 */
	private int minTargets;

	/**
	 * How many targets can be selected
	 * -1:no limit on maximum targets
	 * 0:every target that satisfies the condition must be selected
	 * n:select up to n targets
	 */
	private int maxTargets;

	/**
	 * Minimum distance from POV
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
	 * TRUE: target must be in same xcord or ycord of POV. Direction is saved
	 *   and other moves/dealdamages in the same effect with cardinal=TRUE should use
	 *   the same direction of the first move/dealdamage with cardinal active
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
	 * TRUE: movements can be done through walls
	 * FALSE: movements can't be done through walls
	 */
	private boolean throughWalls;

	/**
	 * Point of it.polimi.se2019.view from where the matching targets are selected
	 */
	private PointOfView pointOfView;


	public ThreeState getVisibility() {
		return visibility;
	}

	public int getMinTargets() {
		return minTargets;
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

	/**
	 * Get a Boolean to use to check if players are consistent with {@link #differentSquare}
	 * @param players targets to check
	 * @return whether the players passes the {@link #differentSquare} check.
	 */
	public boolean checkDifferentSquare(List<Player> players) {
		switch (differentSquare) {
			case TRUE:
				return players.stream().map(Player::getTile).distinct().count() == 1;
			case FALSE:
				return players.stream().map(Player::getTile).distinct().count() == players.size();
			default:
				return true;
		}
	}

    /**
     * Get a Predicate for visibility option regarding the target
     * @param board the corresponding board where analyze visibility
     * @param tile the point of it.polimi.se2019.view Tile
     * @return Predicate to filter a list of Tiles
     */
	public Predicate<Tile> getVisibilityFilter(Board board, Tile tile) {
		switch (visibility) {
			case OPTIONAL: return x-> true;
			case TRUE: return t -> board.visibleTiles(tile).contains(t);
			case FALSE: return t -> !(board.visibleTiles(tile).contains(t));
			default: throw new UnsupportedOperationException();
		}
	}

    /**
     * Get a Predicate for same player room option
     * @param tile the point of it.polimi.se2019.view Tile
     * @return Predicate to filter a list of Tiles
     */
    public Predicate<Tile> getSamePlayerRoomFilter(Tile tile) {
        switch (samePlayerRoom) {
            case OPTIONAL: return x-> true;
            case TRUE: return t -> t.getRoom().equals(tile.getRoom());
            case FALSE: return t -> !(t.getRoom().equals(tile.getRoom()));
            default: throw new UnsupportedOperationException();
        }
    }

	/**
	 * Get a Predicate for given direction
	 * @param tile the point of it.polimi.se2019.view Tile
	 * @return Predicate to filter a list of Tiles
	 */
    public Predicate<Tile> getSameDirectionTile(Tile tile, Direction direction) {
    	if (direction==null)
    	    return t -> true;
        else if (cardinal.equals(ThreeState.TRUE)) {
            switch (direction){
                case EAST: return t -> (t.getPosy() == tile.getPosy() && t.getPosx() >= tile.getPosx());
                case WEST: return t -> (t.getPosy() == tile.getPosy() && t.getPosx() <= tile.getPosx());
                case NORTH: return t -> (t.getPosx() == tile.getPosx() && t.getPosy() <= tile.getPosy());
                case SOUTH: return t -> (t.getPosx() == tile.getPosx() && t.getPosy() >= tile.getPosy());
                default: return t -> true;
            }
        }
        else return t -> true;
	}

	/**
	 * Get a Predicate using a distance filter
	 * @param board the used board
	 * @param tile the starting tile
	 * @return
	 */
	public Predicate<Tile> getDistanceFilter(Board board, Tile tile) {
    	return t -> board.reachable(tile,minDistance,maxDistance,throughWalls).contains(t);
	}

    /**
     * List of predicates to reduce in and and used to filtering the tiles
     * Uses the following
     * <li>visibility</li>
	 * <li>maxDistance and minDistance</li>
	 * <li>samePlayerRoom</li>
	 * <li>throughWalls</li>
     * @param board the board that is being used
     * @param tile the point of view Tile
     * @return List of predicates to use in stream()
     */
	public Predicate<Tile> getFilterTiles(Board board, Tile tile, Direction direction) {
		return getVisibilityFilter(board,tile).
				and(getSamePlayerRoomFilter(tile)).
				and(getDistanceFilter(board,tile)).
                and(getSameDirectionTile(tile, direction));
	}

	/**
	 * Get a Predicate using {@link #samePlayerRoom} value and visibility filter, to filter rooms.
	 * @param board
	 * @param tile
	 * @return
	 */
	public Predicate<Tile> getFilterRoom(Board board, Tile tile){
		return getVisibilityFilter(board,tile)
				.and(getSamePlayerRoomFilter(tile));
	}


	/**
	 * Filter players using TargetList and BlackList
	 * <li>If a List is true, the player must in that list</li>
	 * <li>If a List if false, the player mustn't be in that list</li>
	 * @param player
	 * @param targetList
	 * @param blackList
	 * @return
	 */
	public Predicate<Player> getPlayerListFilter(Player player, List<Player> targetList, List<Player> blackList) {
		Predicate<Player> optionalTarget =
				p -> checkTargetList == ThreeState.OPTIONAL;
		Predicate<Player> trueTarget =
				p -> checkTargetList == ThreeState.TRUE &&
						targetList.contains(p);
		Predicate<Player> falseTarget =
				p -> checkTargetList == ThreeState.FALSE &&
						!targetList.contains(p);

		Predicate<Player> optionalBlack =
				p -> checkBlackList == ThreeState.OPTIONAL;
		Predicate<Player> trueBlack =
				p -> checkBlackList == ThreeState.TRUE &&
						blackList.contains(p);
		Predicate<Player> falseBlack =
				p -> checkBlackList == ThreeState.FALSE &&
						!blackList.contains(p);
		Predicate<Player> notSelf = p -> !p.getUsername().equals(player.getUsername());

		return (optionalTarget.or(trueTarget).or(falseTarget)).
				and(optionalBlack.or(trueBlack).or(falseBlack)).
				and(notSelf);
	}

	/**
	 * Clone the current target, changing the distance option.
	 * It handles the case of a {@link Move} with {@link ObjectToMove#TARGETSOURCE}, when the Target is a Spawn Point: the only possible tile for the SpawnPoint is the same tile he is in, so {@link #minDistance} and {@link #maxTargets} must be equal to 0.
	 * @return
	 */
	public Target getMoveDominationTarget() {
		return new Target.Builder().
                setMaxDistance(0).
                setMinDistance(0).
                setVisibility(visibility).
                setMinTargets(minTargets).
                setMaxTargets(maxTargets).
                setAreaDamage(areaDamage).
                setCardinal(cardinal).
                setCheckTargetList(checkTargetList).
                setDifferentSquare(differentSquare).
                setSamePlayerRoom(samePlayerRoom).
                setThroughWalls(throughWalls).
                setPointOfView(pointOfView).
                setCheckTargetList(checkBlackList).build();
	}
}

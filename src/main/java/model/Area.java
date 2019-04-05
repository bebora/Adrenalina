package model;

/**
 * Represent the different areas targeted by a Target
 * <li>
 *     ROOM: All targets in a Room
 *     TILE: All targets in a Tile
 *     SINGLE: Only maxTargets target
 * </li>
 */
public enum Area {
	ROOM,
	TILE,
	SINGLE;

	/**
	 * Get Area given an input String
	 * @param input String parsed from a configuration file
	 * @return Area related to the input
	 */
	public static Area stringToArea(String input){
		switch(input.toLowerCase()){
			case "room":
				return ROOM;
			case "tile":
				return TILE;
			case "single":
				return SINGLE;
			default:
				return null;
		}

	}
}
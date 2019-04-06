package model;

/**
 * Represent the possible direction in which the Weapon is shooting
 * Used in cardinal weapons
 */
public enum Direction {
	NORTH,
	SOUTH,
	EAST,
	WEST;

	/**
	 * Get Direction given an input String
	 * @param input String parsed from a configuration file
	 * @return Direction related to the input String
	 */
	public static Direction stringToDirection(String input) {
		switch (input) {
			case "north":
				return NORTH;
			case "south":
				return SOUTH;
			case "east":
				return EAST;
			case "west":
				return WEST;
			default:
				return null;
		}
	}
}
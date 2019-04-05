package model;

public enum Direction {
	NORTH,
	SOUTH,
	EAST,
	WEST;

	public static Direction stringToDirection(String input) {
		switch (input) {
			case "nord":
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
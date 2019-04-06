package model;

/**
 * Represent the possible colors of Rooms in the Board
 */
public enum Color {
	RED,
	BLUE,
	PURPLE,
	GREEN,
	YELLOW,
	WHITE;

	/**
	 * Get Color given an input char
	 * @param input String parsed from a configuration file
	 * @return Color related to the input char
	 */
	public static Color initialToColor(char input){
		switch(Character.toLowerCase(input)){
			case 'r':
				return RED;
			case 'b':
				return BLUE;
			case 'p':
				return PURPLE;
			case 'g':
				return GREEN;
			case 'y':
				return YELLOW;
			case 'w':
				return WHITE;
			default:
				return null;
		}

	}
}
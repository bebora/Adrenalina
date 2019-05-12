package it.polimi.se2019.model.board;

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

	public static String getANSIColor(Color color){
		switch(color){
			case RED:
				return "\u001B[31m";
			case BLUE:
				return "\u001B[34m";
			case PURPLE:
				return "\u001B[35m";
			case GREEN:
				return "\u001B[32m";
			case YELLOW:
				return "\u001B[33m";
			case WHITE:
				return "\u001B[37m";
			default:
				return "\u001B[0m";
		}
	}
}

package model;

/**
 * Possible contents of ammoCards and used for activating weapons / powerUps
 */
public enum Ammo {
	RED,
	BLUE,
	YELLOW,
	ANY,
	POWERUP;

	/**
	 * Get Ammo given an input String
	 * @param input String parsed from a configuration file
	 * @return Ammo related to the input
	 */
	public static Ammo stringToAmmo(String input){
		switch(input.toLowerCase()){
			case "red":
				return RED;
			case "blue":
				return BLUE;
			case "yellow":
				return YELLOW;
			case "any":
				return ANY;
			case "powerup":
				return POWERUP;
			default:
				return null;
		}
	}
}

package model;


public enum Ammo {
	RED,
	BLUE,
	YELLOW,
	ANY,
	POWERUP;

	public static Ammo stringToAmmo(String input){
		switch(input){
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

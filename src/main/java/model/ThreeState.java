package model;


public enum ThreeState {
	TRUE,
	FALSE,
	OPTIONAL;

	/**
	 * Get ThreeState given an input string
	 * @param input string parsed from a configuration file
	 * @return the ThreeState related to the input
	 */
	public static ThreeState stringToThreeState(String input){
		switch(input.toLowerCase()){
			case "optional":
				return OPTIONAL;
			case "false":
				return FALSE;
			case "true":
				return TRUE;
			default:
				return null;
		}
	}
}
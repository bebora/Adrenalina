package model;


public enum ThreeState {
	TRUE,
	FALSE,
	OPTIONAL;

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
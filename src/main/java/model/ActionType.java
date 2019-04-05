package model;

/**
 * Represent the type of action that the single Effect can be composed of.
 */
public enum ActionType {
	MOVE,
	DEALDAMAGE;

	/**
	 * Get ActionType given an input String
	 * @param input String parsed from a configuration file
	 * @return ActionType related to the input
	 */
	public static ActionType stringToActionType(String input){
		switch (input.toLowerCase()){
			case "move":
				return MOVE;
			case "dealdamage":
				return DEALDAMAGE;
			default:
				return null;
		}
	}

}
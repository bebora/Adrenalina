package model;

public enum ActionType {
	MOVE,
	DEALDAMAGE;

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
package Model;

public enum ActionType {
	MOVE,
	DEALDAMAGE;

	public static ActionType stringToActionType(String input){
		switch (input){
			case "move":
				return MOVE;
			case "dealdamage":
				return DEALDAMAGE;
			default:
				return null;
		}
	}

}
package Model;


public enum Area {
	ROOM,
	TILE,
	SINGLE;

	public static Area stringToArea(String input){
		switch(input){
			case "room":
				return ROOM;
			case "tile":
				return TILE;
			case "single":
				return SINGLE;
			default:
				return null;
		}

	}
}
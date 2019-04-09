package model.cards;


public enum ViewPoint {
	OWN,
	LASTPLAYER;

	public static ViewPoint stringToViewPoint(String input){
		switch(input.toLowerCase()){
			case "own":
				return OWN;
			case "lastplayer":
				return LASTPLAYER;
			default:
				return null;
		}
	}
}
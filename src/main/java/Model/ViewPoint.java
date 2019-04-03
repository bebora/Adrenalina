package Model;


public enum ViewPoint {
	OWN,
	LASTPLAYER;

	public static ViewPoint stringToViewPoint(String input){
		switch(input){
			case "own":
				return OWN;
			case "lastplayer":
				return LASTPLAYER;
			default:
				return null;
		}
	}
}
package Model;


public enum PointOfView {
	OWN,
	PERSPECTIVE,
	LASTPLAYER;

	public static PointOfView stringToPointOfView(String input){
		switch(input){
			case "own":
				return OWN;
			case "perspective":
				return PERSPECTIVE;
			case "lastplayer":
				return LASTPLAYER;
			default:
				return null;
		}
	}
}
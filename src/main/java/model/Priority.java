package model;


public enum Priority {
	ANY,
	FIRST,
	SECOND,
	THIRD;

	public static Priority stringToPriority(String input){
		switch(input){
			case "first":
				return FIRST;
			case "any":
				return ANY;
			case "second":
				return SECOND;
			case "third":
				return THIRD;
			default:
				return null;
		}
	}
}
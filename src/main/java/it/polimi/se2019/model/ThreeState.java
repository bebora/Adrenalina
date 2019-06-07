package it.polimi.se2019.model;


public enum ThreeState {
	TRUE,
	FALSE,
	OPTIONAL;

	public boolean toBoolean() {
		if (this == TRUE || this == OPTIONAL) {
			return true;
		} else return false;
	}

	public boolean toSkip() {
		if (this == TRUE) {
			return true;
		}
		else return false;
	}

	public ThreeState compare(boolean reverse) {
		if (this == TRUE) return TRUE;
		else if (this == FALSE && reverse) {
			return OPTIONAL;
		}
		else return this;
	}
}
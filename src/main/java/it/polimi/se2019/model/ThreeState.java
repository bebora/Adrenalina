package it.polimi.se2019.model;


public enum ThreeState {
	TRUE,
	FALSE,
	OPTIONAL;

	public boolean toBoolean() {
		return this == TRUE || this == OPTIONAL;
	}

	public boolean toSkip() {
		return this == TRUE;
	}

	public ThreeState compare(boolean reverse) {
		switch (this) {
			case TRUE:
				return TRUE;
			case OPTIONAL:
				return OPTIONAL;
			case FALSE:
				if (reverse) return OPTIONAL;
				else return FALSE;
		}
		return TRUE;
	}
}
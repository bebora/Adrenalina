package it.polimi.se2019.model;

/**
 * Utility class used to represent ternary logic in operations.
 */
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

	/**
	 * Used to indicate whether or not a stop is reverse-stop.
	 * <li>TRUE: the stop is always a reverse-stop</li>
	 * <li>FALSE: the stop is a reverse stop only if {@code reverse} is true</li>
	 * <li>OPTIONAL: the stop is a reverse stop, but the user is still active in his own turn.</li>
	 */

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
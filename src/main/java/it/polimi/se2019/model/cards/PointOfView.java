package it.polimi.se2019.model.cards;

/**
 * OWN: physical position of the attacking player
 * PERSPECTIVE: virtual position of the attacking player
 * LASTPLAYER: position of the last attacked player in TargetList
 * TARGET: position of selected target player
 */
public enum PointOfView {
	OWN,
	PERSPECTIVE,
	LASTPLAYER,
	TARGET
}
package model.cards;

/**
 * Represent the different areas targeted by a Target
 * <li>
 *     ROOM: All targets in a Room
 *     TILE: All targets in a Tile
 *     SINGLE: Only maxTargets target
 * </li>
 */
public enum Area {
	ROOM,
	TILE,
	SINGLE
}
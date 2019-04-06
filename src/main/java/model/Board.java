package model;

import java.util.*;


public class Board {
	/**
	 * List of tiles that make up the Board
	 */
	private List<List<Tile>> tiles;
	/**
	 * Adjacency list of doors between tiles
	 */
	private List<Door> doors;
	/**
	 * Number of skulls remaining on the Board
	 */
	private int skulls;

	/**
	 * List of weapons remaining to be drawn
	 */
	private List<Weapon> weaponsDeck;

	/**
	 * List of players that got a Kill and an Overkill
	 * Every kill is composed by two elements added to the list:
	 * <li>
	 *     Player who gave the KillShot
	 * </li>
	 * <li>
	 *     Player who gave the OverKill; null gets added if no overkill.
	 * </li>
	 */
	private List<Player> killShotTrack;

	/**
	 * Check if two tiles are linked by:
	 * <li>
	 *     A door
	 * </li>
	 * <li>
	 *     Being in the same room and having distance 1
	 * </li>
	 * @param tile1 first tile
	 * @param tile2 second tile
	 * @return true if the tiles are connected
	 */
	public boolean isLinked(Tile tile1,Tile tile2) {
		return doors.contains(new Door(tile1,tile2)) || (tile1.getRoom() == tile2.getRoom() && Tile.cabDistance(tile1,tile2) == 1);
	}


	public List<List<Tile>> getTiles() {
		return tiles;
	}

	public void setTiles(List<List<Tile>> tiles) {
		this.tiles = tiles;
	}

	public List<Door> getDoors() {
		return doors;
	}

	public void setDoors(List<Door> doors) {
		this.doors = doors;
	}

	public int getSkulls() {
		return skulls;
	}

	public void setSkulls(int skulls) {
		this.skulls = skulls;
	}

	public List<Weapon> getWeaponsDeck() {
		return weaponsDeck;
	}

	public void setWeaponsDeck(List<Weapon> weaponsDeck) {
		this.weaponsDeck = weaponsDeck;
	}

	public List<Player> getKillShotTrack() {
		return killShotTrack;
	}

	public void setKillShotTrack(List<Player> killShotTrack) {
		this.killShotTrack = killShotTrack;
	}
}
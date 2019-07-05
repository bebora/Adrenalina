package it.polimi.se2019.view;

import java.io.Serializable;
import java.util.*;

/**
 * Contains all the information necessary for the board in the view
 */
public class ViewBoard implements Serializable {

	/**
	 * List of list of the tiles in the board
	 */
	private ArrayList<ArrayList<ViewTile>> tiles;

	/**
	 * List of string representing the killShotTrack of the current game
	 */
	private ArrayList<String> killShotTrack;

	/**
	 * List of the doors between the tiles
	 */
	private ArrayList<ViewDoor> doors;

	/**
	 * Number of skulls remaining
	 */
	private int skulls;

	/**
	 * Name of the board
	 */
	private String name;

	public void setTiles(ArrayList<ArrayList<ViewTile>> tiles) {
		this.tiles = tiles;
	}

	public void setKillShotTrack(ArrayList<String> killShotTrack) {
		this.killShotTrack = killShotTrack;
	}

	public void setDoors(ArrayList<ViewDoor> doors) {
		this.doors = doors;
	}

	public void setSkulls(int skulls) {
		this.skulls = skulls;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ArrayList<ViewTile>> getTiles() {
		return tiles;
	}

	/**
	 * Check if two tiles is linked
	 * @param tile1 first tile to check
	 * @param tile2 second tile to check
	 * @param throughWalls whether or not they are linked through walls
	 * @return
	 */
	public boolean isLinked(ViewTile tile1, ViewTile tile2, boolean throughWalls) {
		if (!throughWalls)
			return doors.contains(new ViewDoor(tile1,tile2)) || (tile1.getRoom().equals(tile2.getRoom()) && ViewTile.cabDistance(tile1,tile2) == 1);
		else return ViewTile.cabDistance(tile1,tile2) == 1;
	}

	public String getName(){
		return name;
	}

	public ArrayList<String> getKillShotTrack() {
		return killShotTrack;
	}

	public int getSkulls() {
		return skulls;
	}
}
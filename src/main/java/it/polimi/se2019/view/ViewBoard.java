package it.polimi.se2019.view;

import java.io.Serializable;
import java.util.*;

public class ViewBoard implements Serializable {


	private ArrayList<ArrayList<ViewTile>> tiles;

	private ArrayList<String> killShotTrack;

	private ArrayList<ViewDoor> doors;

	private int skulls;

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

	public ViewBoard () {}

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
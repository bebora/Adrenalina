package model;

import java.util.*;


public class Board {

	private List<List<Tile>> tiles;
	private List<Door> doors;
	private int skulls;

	private List<Weapon> weaponsDeck;


	private List<Player> killShotTrack;

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
package Model;

import java.util.*;


public class Board {


	public Board() {
	}


	private ArrayList<ArrayList<Tile>> tiles;


	private ArrayList<Door> doors;


	private int skulls;


	private ArrayList<Weapon> weaponsDeck;


	private ArrayList<Player> killShotTrack;



	public void Board(int skulls) {
	}


	public void existDoor(Tile tile1, Tile tile2) {
	}

	public ArrayList<ArrayList<Tile>> getTiles() {
		return tiles;
	}

	public void setTiles(ArrayList<ArrayList<Tile>> tiles) {
		this.tiles = tiles;
	}

	public ArrayList<Door> getDoors() {
		return doors;
	}

	public void setDoors(ArrayList<Door> doors) {
		this.doors = doors;
	}

	public int getSkulls() {
		return skulls;
	}

	public void setSkulls(int skulls) {
		this.skulls = skulls;
	}

	public ArrayList<Weapon> getWeaponsDeck() {
		return weaponsDeck;
	}

	public void setWeaponsDeck(ArrayList<Weapon> weaponsDeck) {
		this.weaponsDeck = weaponsDeck;
	}

	public ArrayList<Player> getKillShotTrack() {
		return killShotTrack;
	}

	public void setKillShotTrack(ArrayList<Player> killShotTrack) {
		this.killShotTrack = killShotTrack;
	}
}
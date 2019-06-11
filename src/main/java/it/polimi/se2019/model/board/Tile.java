package it.polimi.se2019.model.board;

import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.cards.Weapon;

import java.util.*;


public class Tile {

	public static class Builder {
		/**
		 *
		 */
		private Color sRoom;
		private int sPosx;
		private int sPosy;
		private Boolean sSpawn;
		private List<Weapon> weapons = new ArrayList<>();
		private AmmoCard ammoCard;

		public Builder setRoom(Color room) {
			sRoom = room;
			return this;
		}

		public Builder setpos(int posx, int posy) {
			sPosx = posx;
			sPosy = posy;
			return this;
		}

		public Builder setspawn(Boolean spawn) {
			sSpawn = spawn;
			return this;
		}

		public Tile build() {
			return new Tile(this);
		}


	}
	public Tile(Builder builder) {
		room = builder.sRoom;
		posx = builder.sPosx;
		posy = builder.sPosy;
		spawn = builder.sSpawn;
		weapons = builder.weapons;
		ammoCard = builder.ammoCard;
	}

	private Color room;
	private int posx;
	private int posy;
	private Boolean spawn;

	private List<Weapon> weapons;
	private AmmoCard ammoCard;
	public Boolean isSpawn() {
		return spawn;
	}

	public Color getRoom() {
		return room;
	}

	public int getPosy() {
		return posy;
	}

	public int getPosx() {
		return posx;
	}

	public static int cabDistance(Tile tile1, Tile tile2) {
		return Math.abs(tile1.getPosx() - tile2.getPosx()) + Math.abs(tile1.getPosy() - tile2.getPosy());
	}

	public void addWeapon(Weapon weapon) {
		weapons.add(weapon);
	}

	public void addAmmo(AmmoCard ammoCard) {
		this.ammoCard = ammoCard;
	}

	public AmmoCard getAmmoCard() {
		return ammoCard;
	}

	public int getWeaponsNumber() {
		return weapons.size();
	}

	public List<Weapon> getWeapons() {
		return weapons;
	}

	public Weapon grabWeapon(Weapon weapon) {
		if (isSpawn())
			weapons.remove(weapon);
		else throw new UnsupportedOperationException();
		weapon.setLoaded(true);
		return weapon;
	}

	public AmmoCard grabAmmoCard() {
		AmmoCard grabbed;
		if (!isSpawn()) {
			grabbed = ammoCard;
			ammoCard = null;
			return grabbed;
		}
		else {
			throw new UnsupportedOperationException();
		}
	}
}
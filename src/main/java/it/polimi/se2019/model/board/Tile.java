package it.polimi.se2019.model.board;

import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.cards.Weapon;

import java.util.*;

/**
 * Contains all information needed for a Tile, a single part of a board.
 * It supports grabbing {@link AmmoCard} or {@link Weapon}.
 */
public class Tile {

	public static class Builder {
		private Color sRoom;
		private int posx;
		private int posy;
		private boolean spawn;
		private List<Weapon> weapons = new ArrayList<>();
		private AmmoCard ammoCard = null;

		public Builder setRoom(Color room) {
			sRoom = room;
			return this;
		}

		public Builder setPos(int posx, int posy) {
			this.posx = posx;
			this.posy = posy;
			return this;
		}

		public Builder setSpawn(Boolean spawn) {
			this.spawn = spawn;
			return this;
		}

		public Tile build() {
			return new Tile(this);
		}


	}
	public Tile(Builder builder) {
		room = builder.sRoom;
		posx = builder.posx;
		posy = builder.posy;
		spawn = builder.spawn;
		weapons = builder.weapons;
		ammoCard = builder.ammoCard;
	}

	/**
	 * Color of the room in which the tile is
	 */
	private Color room;

	/**
	 * x coordinate of the tile
	 */
	private int posx;

	/**
	 * y coordinate of the tile
	 */
	private int posy;

	/**
	 * True: the tile contains a spawnpoint
	 * False: the tile does not contain any spawnpoint
	 */
	private Boolean spawn;

	/**
	 * Weapons contained in tile spawnpoint if {@code spawn} is True
	 */
	private List<Weapon> weapons;

	/**
	 * Ammo card contained in tile if {@code spawn} is False
	 */
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

	/**
	 * Manhattan distance between two tiles
	 * @param tile1 first tile
	 * @param tile2 second tile
	 * @return Manhattan distance between the two tiles
	 */
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

	public List<Weapon> getWeapons() {
		return weapons;
	}

	/**
	 * Get the {@code weapon} and remove it from the tile if {@code} spawn is True
	 * @param weapon Weapon which will be grabbed
	 * @return {@code weapon}
	 */
	public Weapon grabWeapon(Weapon weapon) {
		if (isSpawn())
			weapons.remove(weapon);
		else throw new UnsupportedOperationException();
		weapon.setLoaded(true);
		return weapon;
	}

	/**
	 * Get the ammo card and remove it from the tile if {@code} spawn is False
	 * @return ammo card in tile
	 */
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tile tile = (Tile) o;
		return posx == tile.posx &&
				posy == tile.posy;
	}

	@Override
	public int hashCode() {
		return Objects.hash(posx, posy);
	}
}
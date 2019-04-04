package model;

import java.util.*;


public class Tile {

	public static class Builder {
		private Color sRoom;
		private int sPosx;
		private int sPosy;
		private Boolean sSpawn;

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
	}

	private Color room;
	private int posx;
	private int posy;
	private Boolean spawn;

	private List<Weapon> weapons;
	private List<Ammo> ammoCard;
	public Boolean isSpawn() {
		return spawn;
	}

	public Color getRoom() {
		return room;
	}
}
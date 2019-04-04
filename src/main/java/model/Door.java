package model;

public class Door {

	private Tile tile1;
	private Tile tile2;


	public Door(Tile tile1, Tile tile2) {
		this.tile1 = tile1;
		this.tile2 = tile2;
	}

	@Override
	public boolean equals(Object p) {
		boolean returnValue = false;
		if (p instanceof Door) {
			Door door = (Door) p;
			returnValue = (door.tile1 ==  this.tile1 && door.tile2 == this.tile2) || (door.tile1 ==  this.tile2 && door.tile2 == this.tile1);
		}
		return returnValue;
	}

	@Override
	public int hashCode() {
		return this.tile1.hashCode() * this.tile2.hashCode();
	}
}
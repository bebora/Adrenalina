package Model;


public class Action {


	public Action() {
	}


	private int movements;


	private Boolean grab;


	private Boolean shoot;


	private Boolean reload;

	public int getMovements() {
		return movements;
	}

	public void setMovements(int movements) {
		this.movements = movements;
	}

	public Boolean getGrab() {
		return grab;
	}

	public void setGrab(Boolean grab) {
		this.grab = grab;
	}

	public Boolean getShoot() {
		return shoot;
	}

	public void setShoot(Boolean shoot) {
		this.shoot = shoot;
	}

	public Boolean getReload() {
		return reload;
	}

	public void setReload(Boolean reload) {
		this.reload = reload;
	}
}
package Model;


public class PowerUp {


	public PowerUp() {
	}

	/**
	 * Ammo type that can be obtained by discarding the PowerUp to pay a cost
	 */
	private Ammo discardAward;

	/**
	 * Moment in which the PowerUp can be used
	 */
	private Moment applicability;


	private Player player;

	/**
	 * Effect obtained by using the PowerUp
	 */
	private Effect effect;


	private String name;



}
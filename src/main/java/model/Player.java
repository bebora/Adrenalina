package model;

import java.util.*;


public class Player {


	public Player() {
	}

	/**
	 * Real position of the Player
	 */
	private Tile tile;

	/**
	 * Unique identifier of the Player
	 */
	private int id;

	/**
	 * Ordered list of damage tokens received, represented by players who gave them
	 */
	private List<Player> damages;

	/**
	 * Ordered list of marks received, represented by players who gave them
	 */
	private List<Player> marks;

	/**
	 * Owned ammos by the Player; there can't be more than three for each color
	 * No Ammo.POWERUP is allowed into the Set
	 */
	private Set<Ammo> ammos;

	/**
	 * If True, the Player is the first player.
	 */
	private Boolean firstPlayer;

	/**
	 * Current total score
	 */
	private int points;

	/**
	 * List of available actions the player can choose
	 */
	private ArrayList<Action> actions;

	/**
	 * Ordered list of reward points given to other players if player dies
	 */
	private ArrayList<Integer> rewardPoints;

	/**
	 * Loaded and unloaded weapons owned by the Player
	 */
	private ArrayList<Weapon> weapons;

	/**
	 * How many kills the player did in the current turn
	 */
	private int kills;

	/**
	 * Stores how many actions have been made by the player in current turn
	 */
	private int actionCount;

	/**
	 * Represent the status of the Player; when transitioning and at the end of the turn,
	 * the Controller will respawn the player accordingly to the rules.
	 */
	private ThreeState alive;

	/**
	 * PowerUps cards owned by the Player
	 */
	private ArrayList<PowerUp> powerUps;

	/**
	 * Virtual position of the player, used to make some effects possible
	 */
	private Tile perspective;

	/**
	 * True if the player is in fact a spawnpoint
	 */
	private Boolean dominationSpawn;

	/**
	 * True if player turn has ended
	 */
	private Boolean turnEnd;

	/**
	 * Number of time that the player has been killed before FinalFrenzy
	 */
	private int trackSkulls;

	public void convertMarks(Player player) {
		//TODO
	}
	public void shoot(Player shooter, int damage, int marks) {
		//TODO
	}
	public void updateActions() {
		//TODO
		// Create Resources file for according action and parse those according to the number of skulls remaining
	}

	public void reload(Weapon weapon) {
		//TODO
		//Complete the method, create an exception according to the examples and make it throw that
		//If for example the player doesn't have enough $$ to reload.
	}
}
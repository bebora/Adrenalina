package model;

import java.util.*;


public class Player {


	public Player() {
	}

	/**
	 * Real position of the player
	 */
	private Tile tile;


	private int id;

	/**
	 * Ordered list of damage tokens received, represented by players who gave them
	 */
	private ArrayList<Player> damages;

	/**
	 * Ordered list of marks received, represented by players who gave them
	 */
	private ArrayList<Player> marks;


	private ArrayList<Ammo> ammos;


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


	private ArrayList<Weapon> weapons;


	private int kills;

	/**
	 * Stores how many actions have been made by the player in current turn
	 */
	private int actionCount;


	private ThreeState alive;


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


	public void convertMarks(int idPlayer) {
}

	public void reload(Weapon weapon) {
}

}
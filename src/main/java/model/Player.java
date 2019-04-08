package model;

import model.actions.Action;

import java.util.*;
import java.util.stream.Collectors;


public class Player {

	public Player(boolean spawnPlayer) {
		id = UUID.randomUUID().toString();
		alive = ThreeState.TRUE;
		dominationSpawn = spawnPlayer;
		marks = new ArrayList<>();
		damages = new ArrayList<>();
		ammos = new ArrayList<>();
		rewardPoints = new ArrayList<>();
		weapons = new ArrayList<>();
		powerUps = new ArrayList<>();
		this.setMaxActions(3);
	}

	/**
	 * Max number of actions
	 */
	private int maxActions;
	/**
	 * Real position of the Player
	 */
	private Tile tile;

	/**
	 * Unique identifier of the Player
	 */
	private String id;

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
	private List<Ammo> ammos;

	/**
	 * Current total score
	 */
	private int points;

	/**
	 * List of available actions the player can choose
	 */
	private List<Action> actions;

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

	public void setMaxActions(int maxActions) {
		this.maxActions = maxActions;
	}

	public List<Player> getDamages() {
		return damages;
	}

	/**
	 * Convert the marks of the shooting player in damages
	 * @param player user who is shooting
	 */


	public void convertMarks(Player player) {
		List<Player> unrelatedMarks = this.marks.stream().filter(m -> m.id != player.id).collect(Collectors.toList());
		for (int i = 0; i < this.marks.size() - unrelatedMarks.size() && damages.size() < 13; i++) {
		    damages.add(player);
        }
        marks = unrelatedMarks;
	}

	public void receiveShot(Player shooter, int damage, int marks) {
		while(damage > 0 && damages.size() < 13){
			damages.add(shooter);
			damage--;
		}
		convertMarks(shooter);
		while(marks > 0){
			receiveMark(shooter);
			marks--;
		}
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public void addWeapon(Weapon weapon) {
		weapons.add(weapon);
	}

	public List<Weapon> getWeapons() {
		return weapons;
	}

	/**
	 * Recharge the parameter weapon
	 * and set the weapon as loaded.
	 * @param weapon Weapon to be recharged
	 */
	public void reload(Weapon weapon) {
		weapon.getCost().forEach(cost->ammos.remove(cost));
		weapon.setLoaded(true);
	}

	/**
	 * Return true if the player has enough ammo.
	 * @param cost list of Ammo to pay
	 * @return <code>true</code> if the player has enough ammo
	 * 		   <cose>false</cose> otherwise
	 */
	public Boolean checkForAmmos(List<Ammo> cost){
		for (Ammo c : cost)
			if (Collections.frequency(cost,c) > Collections.frequency(ammos,c))
				return false;
		return true;
	}

	public int getDamagesCount() {
		return damages.size();
	}

    public List<Player> getMarks() {
        return marks;
    }

    public String getId() {
        return id;
    }

    public void receiveMark(Player shooter){
		int counter = Collections.frequency(marks,shooter);
		if(counter<3)
			marks.add(shooter);
	}

	public void addAmmo(Ammo ammo) {
		ammos.add(ammo);
	}

	public void notifyFrenzy(Boolean afterFirst){
		for(Action a: actions)
			a.updateOnFrenzy(afterFirst);
		if (afterFirst) {
			this.setMaxActions(2);
		}
	}

	public void notifyHealthChange(){
		for(Action a: actions)
			a.updateOnHealth(damages.size());
	}
}

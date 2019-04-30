package it.polimi.se2019.model;

import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.Attack;
import it.polimi.se2019.model.actions.Grab;
import it.polimi.se2019.model.actions.Move;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.VirtualView;

import java.util.*;
import java.util.stream.Collectors;


public class Player {

	public Player(String username, Color color) {
		this.firstPlayer = false;
		this.color = color;
		this.username = username;
		id = UUID.randomUUID().toString();
		alive = ThreeState.OPTIONAL;
		dominationSpawn = false;
		marks = new ArrayList<>();
		damages = new ArrayList<>();
		ammos = new ArrayList<>();
		rewardPoints = new ArrayList<>(Arrays.asList(8,6,4,2,1));
		weapons = new ArrayList<>();
		powerUps = new ArrayList<>();
		actions = new ArrayList<>(Arrays.asList(new Move(),new Grab(),new Attack()));
		this.setMaxActions(3);
	}


	public Player() {
		this.firstPlayer = false;
		this.damages = new ArrayList<>();
		this.rewardPoints = new ArrayList<>(Arrays.asList(8,6,4,2,1));
	}
	public String getUsername() {
		return username;
	}

	public View getVirtualView() {
		return virtualView;
	}

	public Player setOnline(Boolean online) {
		this.online = online;
		return this;
	}

	public Player setVirtualView(View virtualView) {
		this.virtualView = virtualView;
		return this;
	}
	private boolean firstPlayer;
	Color color;

	String username;

	/**
	 * Virtual View of the player
	 */
	private View virtualView;

	private Boolean online;

	/**
	 * Max number of actions that the player can use in its turn
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
	 * Number of time that the player has been killed before FinalFrenzy
	 */
	private int trackSkulls;


	public Boolean getDominationSpawn() {
		return Boolean.FALSE;
	}

	public void setMaxActions(int maxActions) {
		this.maxActions = maxActions;
	}

	public int getMaxActions() {
		return maxActions;
	}
	public List<Player> getDamages() {
		return damages;
	}

	public int getActionCount() {
		return actionCount;
	}

	/**
	 * Convert the marks of the shooting player in damages
	 * @param player user who is shooting
	 */


	public void convertMarks(Player player) {
		List<Player> unrelatedMarks = this.marks.stream().
                filter(m -> !m.getId().equals(player.getId())).
                collect(Collectors.toList());
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
		notifyHealthChange();
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void addWeapon(Weapon weapon) {
		if(weapons.size()<3)
			weapons.add(weapon);
	}

	public List<Weapon> getWeapons() {
		return weapons;
	}

	public Boolean getOnline() {
		return online;
	}

	public ArrayList<Integer> getRewardPoints() {
		return rewardPoints;
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

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile){this.tile = tile;}

	public boolean getFirstPlayer() {
		return firstPlayer;
	}
	public void setFirstPlayer(boolean firstPlayer) {
		this.firstPlayer = firstPlayer;
	}

	public ThreeState getAlive() {
		return alive;
	}

	public int getPoints() {
		return points;
	}

	public List<Ammo> getAmmos() {
		return ammos;
	}

	public Tile getPerspective() { return perspective; }

	public void setPerspective(Tile perspective){this.perspective = perspective;}

	public void receiveMark(Player shooter){
		int counter = Collections.frequency(marks,shooter);
		if(counter<3)
			marks.add(shooter);
	}

	public void addAmmo(Ammo ammo) {
		if (Collections.frequency(ammos, ammo) < 3)
			ammos.add(ammo);
	}

	public void refreshPlayer() {
		//TODO actions refreshing
	}

	public void resetPlayer(PowerUp powerUp) {
		refreshPlayer();
		addPowerUp(powerUp, false);
		damages.clear();
	}
	/**
	 * Update available actions when entering frenzy mode
	 * @param afterFirst
	 */
	public void notifyFrenzy(Boolean afterFirst){
		if (afterFirst) {
			this.setMaxActions(1);
			//Remove one of the actions as by rules
			actions.remove(0);
		}
		else {
			this.setMaxActions(2);
		}
		for(Action a: actions)
			a.updateOnFrenzy(afterFirst);
	}

	/**
	 * Update the current points adding an integer amount
	 * @param points new points to be added
	 */
	public void addPoints(int points) {
		this.points += points;
	}
	/**
	 * Update available actions based on current health
	 */
	public void notifyHealthChange(){
		for(Action a: actions)
			a.updateOnHealth(damages.size());
	}

	public void addPowerUp(PowerUp powerUp, boolean limit) {
		if (!(limit && powerUps.size() >= 3)) {
			powerUps.add(powerUp);
		}
	}

	public void discardPowerUp(PowerUp powerUp) {
		powerUps.remove(powerUp);
		addAmmo(powerUp.getDiscardAward());
	}


}

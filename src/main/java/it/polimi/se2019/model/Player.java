package it.polimi.se2019.model;

import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.Attack;
import it.polimi.se2019.model.actions.Grab;
import it.polimi.se2019.model.actions.Move;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Moment;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.VirtualView;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;


public class Player {

	public Player(String token) {
		editing = new Object();
		this.firstPlayer = false;
		this.token = token;
		this.color = Color.WHITE;
		id = UUID.randomUUID().toString();
		alive = ThreeState.OPTIONAL;
		dominationSpawn = false;
		marks = new ArrayList<>();
		damages = new ArrayList<>();
		ammos = new ArrayList<>(Arrays.asList(Ammo.RED,Ammo.YELLOW,Ammo.BLUE));
		rewardPoints = new ArrayList<>(Arrays.asList(8,6,4,2,1));
		weapons = new ArrayList<>();
		powerUps = new ArrayList<>();
		actions = new ArrayList<>(Arrays.asList(new Move(),new Grab(),new Attack()));
		this.setMaxActions(2);
		firstShotReward = Boolean.TRUE;
		damagesAllocable = 0;
	}
	public Player(Player originalPlayer){
		this.id = originalPlayer.getId();
		this.tile = originalPlayer.getTile();
		this.firstPlayer = originalPlayer.getFirstPlayer();
		this.virtualView = originalPlayer.getVirtualView();
		this.alive = originalPlayer.getAlive();
		this.dominationSpawn = getDominationSpawn();
		this.marks = new ArrayList<>();
		this.marks.addAll(originalPlayer.getMarks());
		this.damages = new ArrayList<>();
		this.damages.addAll(originalPlayer.getDamages());
		this.weapons = new ArrayList<>();
		this.weapons.addAll(originalPlayer.getWeapons());
		this.setMaxActions(originalPlayer.getMaxActions());
		this.ammos = new ArrayList<>();
		this.ammos.addAll(originalPlayer.getAmmos());
		this.actions = new ArrayList<>();
		this.actions.addAll(originalPlayer.actions);
		this.rewardPoints = originalPlayer.rewardPoints;
		this.powerUps = new ArrayList<>();
		this.powerUps.addAll(originalPlayer.powerUps);
		this.firstShotReward = originalPlayer.firstShotReward;
		this.damagesAllocable = originalPlayer.damagesAllocable;
		this.perspective = originalPlayer.getPerspective();
	}


	public Player() {
		this.firstPlayer = false;
		this.damages = new ArrayList<>();
		this.rewardPoints = new ArrayList<>(Arrays.asList(8,6,4,2,1));
	}

	void restorePlayer(Player oldPlayer){
		oldPlayer.alive = this.getAlive();
		oldPlayer.setTile(this.getTile());
		for(int i = oldPlayer.getMarks().size(); i < this.getMarks().size(); i++)
			oldPlayer.marks.add(i,this.getMarks().get(i));
		for(int i = oldPlayer.getDamages().size();i < this.getDamages().size(); i++)
			oldPlayer.getDamages().add(i,this.getDamages().get(i));
		oldPlayer.weapons = this.weapons;
		oldPlayer.setMaxActions(this.maxActions);
		oldPlayer.actions = this.actions;
		oldPlayer.ammos = this.ammos;
		oldPlayer.powerUps = this.powerUps;
	}

	private Object editing;

	/**
	 * Tracks how many damages can be allocated in spawnpoints
	 * Used in Domination Mode
	 */
	private int damagesAllocable;

	private boolean firstShotReward;

	private boolean firstPlayer;
	private Color color;

	/**
	 * Authentication token saved in this format: {username}$salt${HMAC(password+salt)}
	 */
	String token;

	/**
	 * Virtual View of the player
	 */
	private VirtualView virtualView;

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
	private List<Integer> rewardPoints;

	/**
	 * Loaded and unloaded weapons owned by the Player
	 */
	private List<Weapon> weapons;

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
	private List<PowerUp> powerUps;


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
		return FALSE;
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

	public boolean getFirstShotReward() {
		return firstShotReward;
	}

	public void setFirstShotReward(boolean firstShotReward) {
		this.firstShotReward = firstShotReward;
	}

	/**
	 * Convert the marks of the shooting player in damages, discarding the exceeding marks.
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

	/**
	 * Add given number of {@code damage} given by {@code shooter}
	 * Convert marks related to the shooting player in damages
	 * Add given number of {@code marks} given by {@code shooter}
	 * Change avaiable actions of the receiving player accordingly
	 * @param shooter player who shoots
	 * @param damage number of damages to add
	 * @param marks number of marks to add after converting the existing marks
	 */
	public void receiveShot(Player shooter, int damage, int marks) {
		if(shooter != this) {
			while (damage > 0 && damages.size() < 13) {
				damages.add(shooter);
				damage--;
			}
			convertMarks(shooter);
			while (marks > 0) {
				receiveMark(shooter);
				marks--;
			}
			notifyHealthChange();
		}
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Action> getActions() {
		return actions;
	}

	/**
	 * Add weapons to the player, without exceeding the size limit
	 * @param weapon weapon to add
	 */
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

	public List<Integer> getRewardPoints() {
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
	public Boolean checkForAmmos(List<Ammo> cost, List<Ammo> ammoPool){
		for (Ammo c : cost)
			if (Collections.frequency(cost, c) > Collections.frequency(ammoPool, c))
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

	public Color getColor() {
		return color;
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile){
		this.tile = tile;
		//TODO update view with new update(tile)
	}

	public boolean getFirstPlayer() {
		return firstPlayer;
	}
	public void setFirstPlayer(boolean firstPlayer) {
		this.firstPlayer = firstPlayer;
	}

	public ThreeState getAlive() {
		return alive;
	}

	public void setAlive(ThreeState alive){ this.alive = alive; }

	public int getPoints() {
		return points;
	}

	public List<Ammo> getAmmos() {
		return ammos;
	}

	public List<PowerUp> getPowerUps(){
		return powerUps;
	}

	public Tile getPerspective() { return perspective; }

	public void setPerspective(Tile perspective){this.perspective = perspective;}

	/**
	 * Add mark by {@code shooter}, if there aren't already three marks of the same shooter
	 * @param shooter player who gives the mark
	 */
	public void receiveMark(Player shooter){
		int counter = Collections.frequency(marks,shooter);
		if(counter<3)
			marks.add(shooter);
	}

	/**
	 * Add ammo, if there aren't already three ammos of the same color
	 * @param ammo ammo to add in the player's {@link #ammos}
	 */
	public void addAmmo(Ammo ammo) {
		if (Collections.frequency(ammos, ammo) < 3)
			ammos.add(ammo);
	}


	/**
	 * Reset Player when {@link #alive} is False
	 * Clear the damages
	 */
	public void resetPlayer() {
	    damages.clear();
	}

	/**
	 * Update available actions when entering frenzy mode
	 * @param afterFirst indicates if the current Player has its turn after or before the {@link Match#firstPlayer} in the last turn
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
		if(damages.size() >= 11)
			alive = ThreeState.FALSE;
		for(Action a: actions)
			a.updateOnHealth(damages.size());
	}

	/**
	 * Add {@code powerUp} to {@link #powerUps} if:
	 * <li>{@code limit} if False</li>
	 * <li>{@link #powerUps} size is less than three</li>
	 * @param powerUp powerup to add
	 * @param limit if limiting or not to three the size of {@link #powerUps}
	 */
	public void addPowerUp(PowerUp powerUp, boolean limit) {
		if (!(limit && powerUps.size() >= 3)) {
			powerUps.add(powerUp);
		}
	}

	/**
	 * Discard the powerUp and add the corresponding ammo
	 * @param powerUp powerUp to discard from {@link #powerUps}
	 */
	public void discardPowerUp(PowerUp powerUp) {
		powerUps.remove(powerUp);
		addAmmo(powerUp.getDiscardAward());
	}

	/**
	 * Returns true if the player has a powerUp which can be used at the given moment
	 * @param applicability the moment in which the player can use the powerup
	 * @return <code>true</code> if the player has the corresponding powerup
	 * 		   <code>false</code> otherwise
	 */
	public boolean hasPowerUp(Moment applicability){
		for(PowerUp p: powerUps)
			if(p.getApplicability() == applicability)
				return true;
		return false;
	}

	/**
	 * Returns true if the player has at least one powerup that can be discarded
	 * to pay for the cost
	 * @param cost the ammos the player has to pay
	 * @return <code>true</code> if the player can pay at least one ammo with a powerup
	 * 		   <code>false</code> otherwise
	 */
	public boolean canDiscardPowerUp(List<Ammo> cost){
		for(PowerUp p: powerUps){
			if(cost.contains(p.getDiscardAward()))
				return true;
		}
		return false;
	}

	public List<Ammo> totalAmmoPool(){
		List<Ammo> totalPool = powerUps.stream()
				.map(PowerUp::getDiscardAward)
				.collect(Collectors.toList());
		totalPool.addAll(ammos);
		return totalPool;
	}

	public VirtualView getVirtualView() {
		return virtualView;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public void setVirtualView(VirtualView virtualView) {
		this.virtualView = virtualView;
	}

	public void addDamagesAllocable() {
		this.damagesAllocable+=1;
	}

	public String getToken() {
		return token;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}

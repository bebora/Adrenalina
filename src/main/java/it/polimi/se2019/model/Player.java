package it.polimi.se2019.model;

import it.polimi.se2019.GameProperties;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
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
import it.polimi.se2019.controller.VirtualView;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;


/**
 * Contains information related to the player, necessary to the state of the game.
 * Supports cloning into another instance to save current progress of the player.
 *
 */
public class Player {

	public Player(String token) {
		this.firstPlayer = false;
		this.token = token;
		id = UUID.randomUUID().toString();
		alive = ThreeState.OPTIONAL;
		dominationSpawn = false;
		marks = new ArrayList<>();
		damages = new ArrayList<>();
		ammos = new ArrayList<>(Arrays.asList(Ammo.RED,Ammo.YELLOW,Ammo.BLUE));
		rewardPoints = GameProperties.toList(GameProperties.getInstance().getProperty("reward_points_normal"));
		weapons = new ArrayList<>();
		powerUps = new ArrayList<>();
		actions = new ArrayList<>(Arrays.asList(new Move(),new Grab(),new Attack()));
		this.setMaxActions(2);
		firstShotReward = Boolean.TRUE;
		frenzyActions = false;
		frenzyBoard = false;
	}

	/**
	 * Create a new instance of the player, cloning the player.
	 * Used in {@link it.polimi.se2019.controller.ActionController#sandboxMatch}.
	 * @param originalPlayer to clone
	 */
	public Player(Player originalPlayer){
		this.match = originalPlayer.getMatch();
		this.id = originalPlayer.getId();
		this.color = originalPlayer.color;
		this.token = originalPlayer.token;
		this.actionCount = originalPlayer.actionCount;
		this.dominationSpawn = originalPlayer.dominationSpawn;
		this.maxActions = originalPlayer.maxActions;
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
		this.perspective = originalPlayer.getPerspective();
		this.frenzyActions = originalPlayer.frenzyActions;
		this.frenzyBoard = originalPlayer.frenzyBoard;
	}

	public Player() {
		this.actions = new ArrayList<>();
		this.dominationSpawn = false;
		this.firstPlayer = false;
		this.id = UUID.randomUUID().toString();
		this.damages = new ArrayList<>();
		this.marks = new ArrayList<>();
		this.powerUps = new ArrayList<>();
		this.rewardPoints = GameProperties.toList(GameProperties.getInstance().getProperty("reward_points_normal"));
		this.weapons = new ArrayList<>();
	}

	/**
	 * Restore the {@code oldPlayer}, updating the values with the current ones
	 * @param oldPlayer player that needs to be updated
	 */
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
		//sendTotalUpdate is not needed due to being already sent in match.restoreMatch()
	}

	private Color color;

	/**
	 * Whether the player is the first player in the game.
	 */
	private boolean firstPlayer;

	/**
	 * Whether the player deserves the reward for first shot.
	 */
	private boolean firstShotReward;

	private Match match;

	/**
	 * Authentication token saved in this format: {username}$salt${HMAC(password+salt)}
	 */
	private String token;

	/**
	 * Virtual View of the player
	 */
	private VirtualView virtualView;

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
	List<Ammo> ammos;

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
	List<Integer> rewardPoints;

	/**
	 * Loaded and unloaded weapons owned by the Player
	 */
	private List<Weapon> weapons;


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
	private boolean dominationSpawn;

	/**
	 * True if player is in frenzy mode
	 */
	private boolean frenzyActions;

	/**
	 * True if player board has been flipped after starting frenzy
	 */
	private boolean frenzyBoard;

	public void setDominationSpawn(Boolean dominationSpawn) {
		this.dominationSpawn = dominationSpawn;
	}

	public boolean getDominationSpawn() {
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

	public boolean isFrenzyActions() {
		return frenzyActions;
	}

	public void setFrenzyActions(boolean frenzyActions) {
		this.frenzyActions = frenzyActions;
	}

	public boolean isFrenzyBoard() {
		return frenzyBoard;
	}

	public void setFrenzyBoard(boolean frenzyBoard) {
		this.frenzyBoard = frenzyBoard;
	}

	/**
	 * Convert the marks of the shooting player in damages, discarding the exceeding marks.
	 * @param player user who is shooting
	 */
	public void convertMarks(Player player) {
		List<Player> unrelatedMarks = this.marks.stream().
                filter(m -> !m.getId().equals(player.getId())).
                collect(Collectors.toList());
		for (int i = 0; i < this.marks.size() - unrelatedMarks.size() && damages.size() < 12; i++) {
		    damages.add(player);
        }
        marks = unrelatedMarks;
	}

	/**
	 * Add given number of {@code damage} given by {@code shooter}
	 * Convert marks related to the shooting player in damages
	 * Add given number of {@code marks} given by {@code shooter}
	 * Change available actions of the receiving player accordingly
	 * Synchronized to avoid instant powerup with MOMENT.DAMAGED activation
	 * @param shooter player who shoots
	 * @param damage number of damages to add
	 * @param marks number of marks to add after converting the existing marks
	 */
	public synchronized void receiveShot(Player shooter, int damage, int marks, boolean convert) {
		int temp = damage;
		String shooterName = shooter.getUsername();
		if(shooter == this) {
			Logger.log(Priority.DEBUG, "Receiving damage from spawnpoint");
			shooterName = this.tile.getRoom().name() + " spawnpoint";
		}
		while (temp > 0 && damages.size() < 12) {
			damages.add(shooter);
			temp--;
		}
		if (convert && damage != 0)
			convertMarks(shooter);
		temp = marks;
		while (temp > 0) {
			receiveMark(shooter);
			temp--;
		}
		if (match != null)
			match.updatePopupViews(String.format("%s receives %d marks and %d damages from %s", getUsername(), marks, damage, shooterName));
		notifyHealthChange();
	}

	/**
	 * @return whether the player can reload any weapon.
	 */
	public boolean canReload() {
		return weapons.stream().anyMatch(w -> (!w.getLoaded() && checkForAmmos(w.getCost())));
	}

	public List<Action> getActions() {
		return actions;
	}

	/**
	 * Add weapons to the player, without exceeding the size limit
	 * @param weapon weapon to add
	 */
	public void addWeapon(Weapon weapon) {
		if(weapons.size() < Integer.parseInt(GameProperties.getInstance().getProperty("max_weapons"))) {
			weapons.add(weapon);
			sendTotalUpdate();
		}
	}

	public List<Weapon> getWeapons() {
		return weapons;
	}

	/**
	 * @return whether the player is online; returns false if virtualView is not initialized.
	 */
	public boolean getOnline() {
		if (virtualView != null) {
			return virtualView.isOnline();
		}
		else {
			return false;
		}
	}

	public List<Integer> getRewardPoints() {
		return rewardPoints;
	}

	public void setRewardPoints(List<Integer> rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	/**
	 * Recharge the parameter weapon
	 * and set the weapon as loaded.
	 * @param weapon Weapon to be recharged
	 */
	public void reload(Weapon weapon) {
		weapon.setLoaded(true);
		weapon.reset();
		sendTotalUpdate();
	}

	/**
	 * Check if the player can pay the {@code cost} using the {@link #totalAmmoPool()}
	 * @param cost amount of ammos to pay
	 * @return whether the player can pay or not
	 */
	public boolean checkForAmmos(List<Ammo> cost) {
		List<Ammo> pool = totalAmmoPool();
		for (Ammo c : cost)
			if (Collections.frequency(cost, c) > Collections.frequency(pool, c))
				return false;
		return true;
	}

	/**
	 * Return true if the player has enough ammo.
	 * @param cost list of Ammo to pay
	 * @return <code>true</code> if the player has enough ammo
	 * 		   <cose>false</cose> otherwise
	 */
	public static boolean checkForAmmos(List<Ammo> cost, List<Ammo> ammoPool){
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
		sendTotalUpdate();
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
		if (counter < Integer.parseInt(GameProperties.getInstance().getProperty("max_marks")))
			marks.add(shooter);
	}

	/**
	 * Add ammo, if there aren't already three ammos of the same color
	 * @param ammo ammo to add in the player's {@link #ammos}
	 */
	public void addAmmo(Ammo ammo) {
		if (Collections.frequency(ammos, ammo) < Integer.parseInt(GameProperties.getInstance().getProperty("max_ammo")))
			ammos.add(ammo);
	}


	/**
	 * Reset Player when {@link #alive} is False
	 * Clear the damages and reset adrenaline actions
	 * Frenzy actions should not change after being set
	 */
	public void resetPlayer() {
	    damages.clear();
	    if (!frenzyActions){
			for (Action a: actions){
				a.reset();
			}
		}
	}

	/**
	 * Update available actions when entering frenzy mode
	 * @param afterFirst indicates if the current Player has its turn after or before the {@link Match#firstPlayer} in the last turn
	 */
	public void notifyFrenzy(Boolean afterFirst){
		frenzyActions = true;
		// Update reward points for players with no damage
		if (damages.isEmpty()) {
			firstShotReward = false;
			frenzyBoard = true;
			rewardPoints = GameProperties.toList(GameProperties.getInstance().getProperty("reward_points_frenzy"));
		}
		else {
			frenzyBoard = false;
		}
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
		//sendTotalUpdate is not needed due to being already sent in match.startFrenzy()
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
		//Actions do not change after being set in frenzy
		if (!frenzyActions){
			for(Action a: actions)
				a.updateOnHealth(damages.size());
		}
		sendTotalUpdate();
	}

	/**
	 * Add {@code powerUp} to {@link #powerUps} if:
	 * <li>{@code limit} if False</li>
	 * <li>{@link #powerUps} size is less than three</li>
	 * @param powerUp powerup to add
	 * @param limit if limiting or not to three the size of {@link #powerUps}
	 * @return true if the powerup is added, false if it gets discarded
	 */
	public boolean addPowerUp(PowerUp powerUp, boolean limit) {
		if (!limit || powerUps.size() < 3) {
			powerUps.add(powerUp);
			sendTotalUpdate();
			return true;
		}
		else return false;
	}

	/**
	 * Discard the powerUp and add the corresponding ammo
	 * @param powerUp powerUp to discard from {@link #powerUps}
	 * @param award <code>true</code> if discard award should be added to player ammos
	 */
	public void discardPowerUp(PowerUp powerUp, boolean award) {
		powerUps.remove(powerUp);
		match.getBoard().discardPowerUp(powerUp);
		if (award)
			addAmmo(powerUp.getDiscardAward());
		sendTotalUpdate();
	}

	/**
	 * Returns true if the player has a powerUp which can be used at the given moment
	 * @param applicability the moment in which the player can use the powerup
	 * @return <code>true</code> if the player has the corresponding powerup
	 * 		   <code>false</code> otherwise
	 */
	public boolean hasPowerUp(Moment applicability){
		for(PowerUp p: powerUps)
			if(p.getApplicability().equals(applicability))
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

	/**
	 * Returns the total pool of ammos for the player, considering:
	 * <li>{@link #ammos}</li>
	 * <li>{@link PowerUp#discardAward} of {@link PowerUp#discardAward}</li>
	 * @return
	 */
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

	public void setOnline(boolean online) {
		virtualView.setOnline(online);
	}

	public void setVirtualView(VirtualView virtualView) {
		this.virtualView = virtualView;

	}

    public Match getMatch() {
        return match;
    }

    public String getToken() {
		return token;
	}

	public String getUsername() {
	    if (token == null) return null;
	    else return token.split("\\$")[0];
    }

	public void setColor(Color color) {
		this.color = color;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	private void sendTotalUpdate(){
		if (match != null) {
			this.match.updateViews();
		}
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			return ((Player) obj).getId().equals(this.id);
		}
		return false;
	}
}

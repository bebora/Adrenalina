package it.polimi.se2019.view;
import it.polimi.se2019.model.ThreeState;


import java.io.Serializable;
import java.util.*;

/**
 * Simplified player used by the view, with basic info to display
 * Some attributes are {@link ArrayList} because regular {@link List} is not {@link Serializable}
 */
public class ViewPlayer implements Serializable {
	/**
	 * Color of the player
	 */
	private String color;

    /**
     * Id of the player
     */
	private String id;

    /**
     * Ordered list of damages containing color of players who have attacked the ViewPlayer
     */
	private ArrayList<String> damages;

    /**
     * Ordered list of marks containing color of players who have attacked the ViewPlayer
     */
	private ArrayList<String> marks;

    /**
     * Colors of the ammos the player has
     */
	private ArrayList<String> ammos;

    /**
     * true if the player is the one who started the game
     */
	private Boolean firstPlayer;

    /**
     * Order list of points that the player can give to others when killed
     */
	private ArrayList<Integer> rewardPoints;

    /**
     * Player's weapons which haven't been loaded
     */
	private ArrayList<ViewWeapon> unloadedWeapons;

    /**
     * Status of the player
     */
	private ThreeState alive;

    /**
     * True if the player isn't a real player but a domination spawn
     */
	private Boolean dominationSpawn;

    /**
     * Tile on which the player stands
     */
	private ViewTile tile;

    /**
     * Username of the player
     */
	private String username;

    /**
     * True if player is in frenzy mode
     */
	private boolean frenzyActions;

    /**
     * True if player board has been flipped after starting frenzy, as by rules
     */
	private boolean frenzyBoard;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDamages(ArrayList<String> damages) {
		this.damages = damages;
	}

	public void setMarks(ArrayList<String> marks) {
		this.marks = marks;
	}

	public void setAmmos(ArrayList<String> ammos) {
		this.ammos = ammos;
	}

	public void setFirstPlayer(Boolean firstPlayer) {
		this.firstPlayer = firstPlayer;
	}

	public void setRewardPoints(ArrayList<Integer> rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	public void setUnloadedWeapons(ArrayList<ViewWeapon> unloadedWeapons) {
		this.unloadedWeapons = unloadedWeapons;
	}

	public void setAlive(ThreeState alive) {
		this.alive = alive;
	}

	public void setFrenzyActions(boolean frenzyActions) {
		this.frenzyActions = frenzyActions;
	}

	public void setFrenzyBoard(boolean frenzyBoard) {
		this.frenzyBoard = frenzyBoard;
	}

	public String getId() {
		return id;
	}

	public String getUsername(){ return username;}

	public String getColor() {
		return color;
	}

	public void setAmmos(List<String> ammos) {
		this.ammos = new ArrayList<>(ammos);
	}

	public List<String> getAmmos(){return ammos;}

	public ViewTile getTile() {
		return tile;
	}

	public void setTile(ViewTile tile) {
		this.tile = tile;
	}

	public List<String> getMarks() {
		return marks;
	}

	public List<String> getDamages() {
		return damages;
	}

	public List<ViewWeapon> getUnloadedWeapons() {
		return unloadedWeapons;
	}

	public ArrayList<Integer> getRewardPoints() { return rewardPoints; }

	public void setDamages(List<String> damages) {
		this.damages = new ArrayList<>(damages);
	}

	public void setMarks(List<String> marks) {
		this.marks = new ArrayList<>(marks);
	}

	public Boolean getDominationSpawn() {
		return dominationSpawn;
	}

	public void setDominationSpawn(Boolean dominationSpawn) {
		this.dominationSpawn = dominationSpawn;
	}

	public boolean isFrenzyActions() {
		return frenzyActions;
	}

	public boolean isFrenzyBoard() {
		return frenzyBoard;
	}
}
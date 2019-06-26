package it.polimi.se2019.view;
import it.polimi.se2019.model.ThreeState;


import java.io.Serializable;
import java.util.*;

public class ViewPlayer implements Serializable {

	private String color;

	private String id;

	private ArrayList<String> damages;

	private ArrayList<String> marks;

	private ArrayList<String> ammos;

	private Boolean firstPlayer;

	private ArrayList<Integer> rewardPoints;

	private ArrayList<ViewWeapon> unloadedWeapons;

	private int actionCount;

	private ThreeState alive;

	private Boolean dominationSpawn;

	private int maxActions;

	private ViewTile tile;

	private String username;

	private boolean frenzyActions;

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

	public void setActionCount(int actionCount) {
		this.actionCount = actionCount;
	}

	public void setAlive(ThreeState alive) {
		this.alive = alive;
	}

	public void setMaxActions(int maxActions) {
		this.maxActions = maxActions;
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
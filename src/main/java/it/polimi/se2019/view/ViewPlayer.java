package it.polimi.se2019.view;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.Weapon;

import java.util.*;
import java.util.stream.Collectors;

public class ViewPlayer {

	public ViewPlayer(Player player) {
		this.color = player.getColor().name();
		this.id = player.getId();
		this.damages = player.getDamages().stream().map(p -> p.getColor().name()).collect(Collectors.toList());
		this.marks = player.getMarks().stream().map(p -> p.getColor().name()).collect(Collectors.toList());
		this.ammos = player.getAmmos().stream().map(Ammo::name).collect(Collectors.toList());
		this.firstPlayer = player.getFirstPlayer();
		this.actions = player.getActions().stream().map(ViewAction::new).collect(Collectors.toList());
		this.rewardPoints = player.getRewardPoints();
		this.unloadedWeapons = player.getWeapons().stream().
				filter(Weapon::getLoaded).map(Weapon::getName).collect(Collectors.toList());
		this.actionCount = player.getActionCount();
		this.alive = player.getAlive();
		this.dominationSpawn = player.getDominationSpawn();
		this.maxActions = player.getMaxActions();
	}

	private ViewTile tile;

	private String color;

	private String id;

	private List<String> damages;

	private List<String> marks;

	private List<String> ammos;

	private Boolean firstPlayer;

	private List<ViewAction> actions;

	private List<Integer> rewardPoints;

	private List<String> unloadedWeapons;

	private int actionCount;

	private ThreeState alive;

	private Boolean dominationSpawn;

	private int maxActions;

	public String getId() {
		return id;
	}

	public String getColor() {
		return color;
	}

	public void setAmmos(List<String> ammos) {
		this.ammos = ammos;
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

	public void setActions(List<ViewAction> actions) {
		this.actions = actions;
	}

	public List<String> getUnloadedWeapons() {
		return unloadedWeapons;
	}

	public void setDamages(List<String> damages) {
		this.damages = damages;
	}

	public void setMarks(List<String> marks) {
		this.marks = marks;
	}
}
package it.polimi.se2019.view;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.ammos.Ammo;


import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ViewPlayer implements Serializable {

	public ViewPlayer(Player player) {
		this.color = player.getColor().name();
		this.username = player.getToken().split("\\$")[0];
		this.id = player.getId();
		this.damages = player.getDamages().stream().
				map(p -> p.getColor().name()).
				collect(Collectors.toCollection(ArrayList::new));
		this.marks = player.getMarks().stream().
				map(p -> p.getColor().name()).
				collect(Collectors.toCollection(ArrayList::new));
		this.ammos = player.getAmmos().stream().
				map(Ammo::name).
				collect(Collectors.toCollection(ArrayList::new));
		this.firstPlayer = player.getFirstPlayer();
		this.actions = player.getActions().stream().
				map(ViewAction::new).
				collect(Collectors.toCollection(ArrayList::new));
		this.rewardPoints = new ArrayList<>(player.getRewardPoints());
		this.unloadedWeapons = player.getWeapons().stream().
				filter(w -> !w.getLoaded()).map(ViewWeapon::new).
				collect(Collectors.toCollection(ArrayList::new));
		this.actionCount = player.getActionCount();
		this.alive = player.getAlive();
		this.dominationSpawn = player.getDominationSpawn();
		this.maxActions = player.getMaxActions();
		if (player.getTile() == null) this.tile = null;
		else this.tile = new ViewTile(player.getTile());
	}

	private ViewTile tile;

	private String username;

	private String color;

	private String id;

	private ArrayList<String> damages;

	private ArrayList<String> marks;

	private ArrayList<String> ammos;

	private Boolean firstPlayer;

	private ArrayList<ViewAction> actions;

	private ArrayList<Integer> rewardPoints;

	private ArrayList<ViewWeapon> unloadedWeapons;

	private int actionCount;

	private ThreeState alive;

	private Boolean dominationSpawn;

	private int maxActions;

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

	public void setActions(List<ViewAction> actions) {
		this.actions = new ArrayList<>(actions);
	}

	public List<ViewAction> getActions(){ return this.actions;}

	public List<ViewWeapon> getUnloadedWeapons() {
		return unloadedWeapons;
	}

	public void setDamages(List<String> damages) {
		this.damages = new ArrayList<>(damages);
	}

	public void setMarks(List<String> marks) {
		this.marks = new ArrayList<>(marks);
	}
}
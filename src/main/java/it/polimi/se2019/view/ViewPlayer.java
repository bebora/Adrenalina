package it.polimi.se2019.view;
import java.util.*;

public class ViewPlayer {

	public ViewPlayer() {
	}

	private ViewTile tile;

	private String id;

	private List<ViewPlayer> damages;

	private List<ViewPlayer> marks;

	private List<String> ammos;

	private Boolean firstPlayer;

	private List<ViewAction> actions;

	private List<String> rewardPoints;

	private List<String> unloadedWeapons;

	private int kills;

	private int actionCount;

	private Boolean alive;

	private ViewTile perspective;

	private Boolean dominationSpawn;

	private int maxActions;

	public String getId() {
		return id;
	}

	public void setAmmos(List<String> ammos) {
		this.ammos = ammos;
	}

	public ViewTile getTile() {
		return tile;
	}

	public List<ViewPlayer> getMarks() {
		return marks;
	}

	public List<ViewPlayer> getDamages() {
		return damages;
	}

	public void setActions(List<ViewAction> actions) {
		this.actions = actions;
	}
}
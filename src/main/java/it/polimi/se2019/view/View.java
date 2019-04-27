package it.polimi.se2019.view;
import it.polimi.se2019.model.updatemessage.UpdateVisitable;

import java.util.*;

public abstract class View {
	private String username;
	private ViewBoard board;

	private List<ViewPlayer> players;

	private String idView;

	private int points;

	private List<String> powerUps;

	private List<String> loadedWeapons;

	private UpdateVisitor visitor;

	public void update(UpdateVisitable u) {
		//TODO implements the update parsing (visitor pattern)
	}

	public List<ViewPlayer> getPlayers() {
		return players;
	}

	public int getPoints() {
		return points;
	}

	public List<String> getPowerUps() {
		return powerUps;
	}

	public List<String> getLoadedWeapons() {
		return loadedWeapons;
	}

	public ViewBoard getBoard() {
		return board;
	}

	public String getUsername() {
		return username;
	}


}
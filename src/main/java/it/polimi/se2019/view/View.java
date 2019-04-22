package it.polimi.se2019.view;
import it.polimi.se2019.model.UpdateMessage.Update;

import java.util.*;

public abstract class View {
	private String username;
	private ViewBoard board;

	private List<ViewPlayer> players;

	private String idView;

	private int points;

	private List<String> powerUps;

	private List<String> loadedWeapons;

	public void update(Update u) {
		//TODO implements the update parsing (command pattern)
	}
}
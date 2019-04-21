package it.polimi.se2019.view;
import java.util.*;

public abstract class View {

	private ViewBoard board;

	private List<ViewPlayer> players;

	private String idView;

	private int points;

	private List<String> powerUps;

	private List<String> loadedWeapons;
}
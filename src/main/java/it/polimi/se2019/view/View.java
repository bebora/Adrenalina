package it.polimi.se2019.view;
import it.polimi.se2019.controller.RequestDispatcher;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.network.ViewReceiverInterface;
import it.polimi.se2019.network.ViewUpdater;

import java.util.List;

public abstract class View {

	private EventUpdater eventUpdater;

	private String username;

	private ViewBoard board;

	private List<ViewPlayer> players;

	private String idView;

	private int points;

	private List<ViewPowerUp> powerUps;

	private List<String> loadedWeapons;

	private ViewTile perspective;

	protected ViewReceiverInterface receiver;
	RequestDispatcher requestDispatcher;

	String token;

	public View(ViewReceiverInterface receiver, ViewUpdater viewUpdater) {
		this.receiver = receiver;
	}

	public View() {
		this.receiver = new ConcreteViewReceiver(this);
	}

	public List<ViewPlayer> getPlayers() {
		return players;
	}

	public int getPoints() {
		return points;
	}

	public List<ViewPowerUp> getPowerUps() {
		return powerUps;
	}

	public List<String> getLoadedWeapons() {
		return loadedWeapons;
	}

	public ViewBoard getBoard() {
		return board;
	}

	public ViewTile getPerspective() {
		return perspective;
	}

	public String getIdView() {
		return idView;
	}

	public String getUsername() {
		return username;
	}

	public ViewReceiverInterface getReceiver() {
		return receiver;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setBoard(ViewBoard board) {
		this.board = board;
	}

	public void setPlayers(List<ViewPlayer> players) {
		this.players = players;
	}

	public void setIdView(String idView) {
		this.idView = idView;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void setPowerUps(List<ViewPowerUp> powerUps) {
		this.powerUps = powerUps;
	}

	public void setLoadedWeapons(List<String> loadedWeapons) {
		this.loadedWeapons = loadedWeapons;
	}

	public void setPerspective(ViewTile perspective) {
		this.perspective = perspective;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setEventUpdater(EventUpdater eventUpdater) {
		this.eventUpdater = eventUpdater;
	}

	public void setReceiver(ViewReceiverInterface receiver) {
		this.receiver = receiver;
	}

	public void setRequestDispatcher(RequestDispatcher requestDispatcher) {
		this.requestDispatcher = requestDispatcher;
	}
}
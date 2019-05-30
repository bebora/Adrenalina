package it.polimi.se2019.view;

import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.network.ViewReceiverInterface;

import java.util.List;
import java.util.Queue;

/**
 * View used by the clients for:
 * <li>Access info for visualizing the state of the game</li>
 * <li>Receive updates from the {@link #View#receiver}</li>
 * <li>Sending events from the {@link #View#eventUpdater}</li>
 */
public class View {
	private Queue<String> messages;

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

	private List<ReceivingType> types;

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

	public void setPlayers(List<ViewPlayer> players) { this.players = players; }

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

	public void setEventUpdater(EventUpdater eventUpdater) {
		this.eventUpdater = eventUpdater;
	}

	public void setReceiver(ViewReceiverInterface receiver) {
		this.receiver = receiver;
	}

	public ViewPlayer getSelf(){
		return players.stream()
				.filter(p -> p.getUsername().equals(username))
				.findAny().orElse(null);
	}

	public EventUpdater getEventUpdater() {
		return eventUpdater;
	}

	public synchronized void update() {

    }

    public void setupConnection(String connectionType){
		//TODO:implement this method to setup RMI or socket connection
	}
}
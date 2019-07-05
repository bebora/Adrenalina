package it.polimi.se2019.view;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.*;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.network.EventUpdaterRMI;
import it.polimi.se2019.network.EventUpdaterSocket;
import it.polimi.se2019.network.ViewReceiverInterface;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * View used by the clients for:
 * <li>Access info for visualizing the state of the game</li>
 * <li>Receive updates from the {@link #View#receiver}</li>
 * <li>Sending events from the {@link #View#eventUpdater}</li>
 */
public abstract class View {

	/**
	 * Indicates the status of the view
	 */
	private Status status;

	/**
	 * List of messages received and displayed
	 */
	private List<String> messages;

	/**
	 * Current player playing the turn
	 */
	private ViewPlayer currentPlayer;

	/**
	 * Interface used to communicate chosen events to the backend
	 */
	private EventUpdater eventUpdater;

	/**
	 * Username of the current client
	 */
	private String username;

	/**
	 * Current board used during the game
	 */
	private ViewBoard board;

	/**
	 * List of the players in the game, including spawnpoints
	 */
	private List<ViewPlayer> players;

	/**
	 * Id of the view
	 */
	private String idView;

	/**
	 * Number of the points of the user playing with the client
	 */
	private int points;

	/**
	 * List of the powerUps of the user playing with the client
	 */
	private List<ViewPowerUp> powerUps;

	/**
	 * List of the loaded weapons of the user playing with the client
	 */
	private List<ViewWeapon> loadedWeapons;

	/**
	 * Perspective of the user
	 */
	private ViewTile perspective;

	/**
	 * Receiver interface, handling updates from the backend.
	 */
	protected ViewReceiverInterface receiver;

	/**
	 * Whether the current view is online or not
	 */
	private boolean online;

	/**
	 * Indicates the millis (passed after the OS dawn of time) of the last request
	 */
	private long lastRequest;

	private NetworkTimeoutControllerClient networkTimeoutController;

	/**
	 * Contains the options selectable from the user
	 */
	private SelectableOptionsWrapper selectableOptionsWrapper;

	/**
	 * Indicates what gameMode is currently being played
	 */
	private String gameMode;

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isOnline() {
		return online;
	}


	/**
	 * Construct a View and the related {@link ConcreteViewReceiver}.
	 */
	public View() {
		try {
			this.receiver = new ConcreteViewReceiver(this);
		}
		catch (RemoteException e) {
			Logger.log(Priority.ERROR, "Unexpected RemoteException while creating ViewReceiver!");
		}
		messages = new ArrayList<>();
		online = false;
		selectableOptionsWrapper = new SelectableOptionsWrapper();
		selectableOptionsWrapper.setAcceptedTypes(new ArrayList<>());
	}

	public SelectableOptionsWrapper getSelectableOptionsWrapper() {
		return selectableOptionsWrapper;
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

	public List<ViewWeapon> getLoadedWeapons() {
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

	public String getGameMode(){ return gameMode;}

	public ViewReceiverInterface getReceiver() {
		return receiver;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setSelectableOptionsWrapper(SelectableOptionsWrapper selectableOptionsWrapper) {
		this.selectableOptionsWrapper = selectableOptionsWrapper;
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

	public void setLoadedWeapons(List<ViewWeapon> loadedWeapons) {
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

	public void setCurrentPlayer(ViewPlayer currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public void setGameMode(String gameMode){ this.gameMode = gameMode; }

	public ViewPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	public void addMessage(String message){
		if(messages.size() > 4){
			messages.remove(0);
		}
		messages.add(message);
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public ViewPlayer getSelf(){
		return players.stream()
				.filter(p -> p.getUsername().equals(username))
				.findAny().orElse(null);
	}

	public EventUpdater getEventUpdater() {
		return eventUpdater;
	}

	/**
	 * Setup the connection
	 * @param connectionType network connection method (RMI/socket)
	 * @param username username of the connecting player
	 * @param password password of the connecting player
	 * @param connectionProperties properties of the remote server, which should contain url and port
	 * @param existingGame true if player is joining a match from which has been disconnected
	 * @param gameMode
	 * @return {@code true} if connection with server is successful
	 */
    public boolean setupConnection(String connectionType, String username, String password, Properties connectionProperties, boolean existingGame, String gameMode){
		networkTimeoutController = new NetworkTimeoutControllerClient(this);
    	String url = connectionProperties.getProperty("url");
		int port = Integer.parseInt(connectionProperties.getProperty("port"));
		if (connectionType.equalsIgnoreCase("socket")) {
			eventUpdater = new EventUpdaterSocket(url, port);
		}
		else if (connectionType.equalsIgnoreCase("rmi")) {
			eventUpdater = new EventUpdaterRMI(url, port);
		}
		else {
			try {
				receiver.receivePopupMessage("Error!");
			}
			catch (RemoteException e) {
				Logger.log(Priority.ERROR, "Unable to call local method");
			}
			return false;
		}
		try {
			boolean loginSuccessful = eventUpdater.login(this, username, password, existingGame, gameMode);
			if (!loginSuccessful) return false;
			online = true;
			networkTimeoutController.start();
			return true;
		}
		catch (RemoteException e) {
			try {
				receiver.receivePopupMessage(e.getMessage());
			}
			catch (RemoteException r) {
				Logger.log(Priority.ERROR, "Unable to call local method");
			}
			return false;
		}
	}

	public List<String> getReceivingTypes() {
		return selectableOptionsWrapper.getAcceptedTypes().stream().map(Objects::toString).collect(Collectors.toList());
	}

	public synchronized Status getStatus() {
		return status;
	}

	public abstract void disconnect();

	public synchronized void setStatus(Status status) {
		this.status = status;
	}

	public  abstract void refresh();

	public long getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(long lastRequest) {
		this.lastRequest = lastRequest;
	}

	public abstract void printWinners(List<String> winners);
}
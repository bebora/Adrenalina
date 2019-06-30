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

	private Status status;

	private List<String> messages;

	private ViewPlayer currentPlayer;

	private EventUpdater eventUpdater;

	private String username;

	private ViewBoard board;

	private List<ViewPlayer> players;

	private String idView;

	private int points;

	private List<ViewPowerUp> powerUps;

	private List<ViewWeapon> loadedWeapons;

	private ViewTile perspective;

	protected ViewReceiverInterface receiver;

	private boolean online;

	private GameController gameController;

	private long lastRequest;

	private NetworkTimeoutControllerClient networkTimeoutController;

	private SelectableOptionsWrapper selectableOptionsWrapper;

	private String gameMode;

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isOnline() {
		return online;
	}

	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	public GameController getGameController() {
		return gameController;
	}

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
		networkTimeoutController = new NetworkTimeoutControllerClient(this);
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

	public synchronized void update() {

    }

	/**
	 * Setup the connection
	 * @param connectionType
	 * @param username
	 * @param password
	 * @param connectionProperties
	 * @param existingGame
	 * @param gameMode
	 */
    public void setupConnection(String connectionType, String username, String password, Properties connectionProperties, boolean existingGame, String gameMode){
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

			return;
		}
		try {
			eventUpdater.login(this, username, password, existingGame, gameMode);
			online = true;
			networkTimeoutController.start();
		}
		catch (RemoteException e) {
			try {
				receiver.receivePopupMessage(e.getMessage());
			}
			catch (RemoteException r) {
				Logger.log(Priority.ERROR, "Unable to call local method");
			}
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
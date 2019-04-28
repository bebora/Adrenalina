package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Container class for all the information of the Match being played
 */
public abstract class Match {
	Random rand = new Random();

	/**
	 * True if player turn has ended
	 */
	private Boolean turnEnd;

	/**
	 * Board used for the Match
	 */
	private Board board;

	/**
	 * List of players playing the match
	 */
	private ArrayList <Player> players;

	/**
	 * Index of the player whose turn is the current
	 */
	private int currentPlayer;

	/**
	 * Index of the firstPlayer
	 */
	private int firstPlayer;

	/**
	 * If True, the activated mode is finalFrenzy
	 */
	private Boolean finalFrenzy;

	/**
	 * The game can be played in:
	 * <li>Normal Mode </li>
	 * <li> Domination Mode</li>
	 */


	public void addPlayer(Player player) {
		players.add(player);
	}

	public void startFrenzy() {
		Boolean afterFirst;
		if (firstPlayer < currentPlayer) {
			for (Player p : players) {
				afterFirst = !(players.indexOf(p) >= currentPlayer || players.indexOf(p) < firstPlayer);
				p.notifyFrenzy(afterFirst);
			}
		} else if (firstPlayer > currentPlayer) {
			for (Player p : players) {
				afterFirst = !(players.indexOf(p) >= currentPlayer && players.indexOf(p) < firstPlayer);
				p.notifyFrenzy(afterFirst);
			}
		}
		else {
			for (Player p : players)
				p.notifyFrenzy(true);
		}
	}

	public void newTurn() {
		List<Player> deadPlayers = players.stream().
				filter(p -> p.getAlive() == ThreeState.FALSE).collect(Collectors.toList());
		for (Player p : deadPlayers) {
			scorePlayerBoard(p);
			resetPlayer(p);
		}

        if (deadPlayers.stream().filter(p -> !p.getDamages().get(11).getDominationSpawn()).count() > 1)
            players.get(currentPlayer).addPoints(1);

		board.refreshWeapons();
		board.refreshAmmos();
	}

	public void resetPlayer(Player player) {
		player.addPowerUp(board.drawPowerUp(),false);
		player.getDamages().clear();
	}

	public void scorePlayerBoard(Player player) {
		// first blood
		if (finalFrenzy)
			player.getDamages().get(0).addPoints(1);
		Player maxPlayer = null;
		int currentReward = 0;

		// damages scoring
		Map<Player, Long> frequencyDamages =
				player.getDamages().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Comparator<Player> givenDamages = Comparator.comparing(frequencyDamages::get);
		Comparator<Player> indices = Comparator.comparing(player.getDamages()::indexOf);


		Set<Player> damagingPlayers = new HashSet<>(player.getDamages());

		List<Player> damageOrder = damagingPlayers.stream().sorted(givenDamages.thenComparing(indices)).collect(Collectors.toList());
		while (damageOrder.isEmpty()) {
			if (currentReward < player.getRewardPoints().size()) {
				damageOrder.get(0).addPoints(player.getRewardPoints().get(currentReward));
			} else {
				damageOrder.get(0).addPoints(1);
			}

			damageOrder.remove(0);
			currentReward++;
		}
	}

	public abstract void scoreDeadShot(Player player);
	public List<Player> getPlayersInTile(Tile tile){
		return this.getPlayers().stream()
				.filter(p -> p.getTile() == tile)
				.collect(Collectors.toList());
	}

	public List<Player> getPlayersInRoom(Color room){
		return this.getPlayers().stream()
				.filter(p -> p.getTile().getRoom() == room)
				.collect(Collectors.toList());
	}

	public abstract List<Player> getWinners();
	public List<Player> getPlayers(){ return players;}
	public int getCurrentPlayer(){return currentPlayer;}
	public Board getBoard(){return board; }


}
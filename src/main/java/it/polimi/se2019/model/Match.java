package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Tile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Container class for all the information of the Match being played
 */
public class Match {
	Random rand = new Random();

	public Match() {
		players = new ArrayList<>();
		finalFrenzy = false;
		turnEnd = false;
	}


	public void startMatch(String boardFilename, Mode mode, int numSkulls) {
		firstPlayer = rand.nextInt(players.size());
		currentPlayer = firstPlayer;
		board = BoardCreator.parseBoard(boardFilename, numSkulls);
		this.mode = mode;
		//TODO switch different modes
	}
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
	private Mode mode;


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

		if (deadPlayers.size() > 1)
			players.get(currentPlayer).addPoints(1);
	}

	public void resetPlayer(Player player) {
		player.addPowerUp(board.drawPowerUp(),false);
		player.getDamages().clear();
	}

	public void scorePlayerBoard(Player player) {
		// first blood
		player.getDamages().get(0).addPoints(1);
		Player maxPlayer = null;
		int currentReward = 0;

		// damages scoring
		Map<Player, Long> frequencyDamages =
				player.getDamages().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		while (frequencyDamages.size() != 0) {
			List<Player> maxPlayers = frequencyDamages.
					entrySet().
					stream().
					filter(e -> e.getValue() == Collections.max(frequencyDamages.values())).
					map(Map.Entry::getKey)
					.collect(Collectors.toList());

			for (Player p : player.getDamages()) {
				if (maxPlayers.contains(p)) {
					maxPlayer = p;
					break;
				}
			}
			if (maxPlayer == null)
				throw new UnsupportedOperationException();

			if (currentReward < player.getRewardPoints().size()) {
				maxPlayer.addPoints(player.getRewardPoints().get(currentReward));
			} else {
				maxPlayer.addPoints(1);
			}

			frequencyDamages.remove(maxPlayer);
		}

		// death shot
		board.addToKillShot(player.getDamages().get(10));
		player.getRewardPoints().remove(0);

		if (player.getDamages().size() == 12) {
			board.addToKillShot(player.getDamages().get(11));
			player.getDamages().get(11).receiveMark(player);
		} else {
			board.addToKillShot(null);
		}
	}
	public List<Player> getPlayersInTile(Tile tile){
		return this.getPlayers().stream()
				.filter(p -> p.getTile() == tile)
				.collect(Collectors.toList());
	}

	public List<Player> getPlayers(){ return players;}
	public int getCurrentPlayer(){return currentPlayer;}
	public Board getBoard(){return board; }


}
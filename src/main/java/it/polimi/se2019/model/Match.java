package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;

import java.util.*;

/**
 * Container class for all the information of the Match being played
 */
public class Match {

	public Match(String boardFilename, Mode mode, int numSkulls) {
		board = BoardCreator.parseBoard(boardFilename, numSkulls);
		players = new ArrayList<>();
		this.mode = mode;
		finalFrenzy = false;
		currentPlayer = firstPlayer;
		turnEnd = false;
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

	public List<Player> getPlayers(){ return players;}
	public int getCurrentPlayer(){return currentPlayer;}
}
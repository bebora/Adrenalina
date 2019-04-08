package model;

import java.util.*;

/**
 * Container class for all the information of the Match being played
 */
public class Match {

	public Match(String boardFilename, int numPlayer, Mode mode, int numSkulls) {
		board = BoardCreator.parseBoard(boardFilename, numSkulls);
		for (int i = 0; i < numPlayer; i++)
			players.add(new Player(false));
		this.mode = mode;
		Random r = new Random();
		firstPlayer = r.ints(0, numPlayer).findFirst().getAsInt();
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
}
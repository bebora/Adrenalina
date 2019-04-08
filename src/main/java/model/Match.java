package model;

import java.util.*;

/**
 * Container class for all the information of the Match being played
 */
public class Match {

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
		if (firstPlayer < currentPlayer) {
			for (Player p : players) {
				if (players.indexOf(p) >= currentPlayer || players.indexOf(p) < firstPlayer)
					p.notifyFrenzy(false);
				else
					p.notifyFrenzy(true);
			}
		} else if (firstPlayer > currentPlayer)
			for (Player p : players) {
				if (players.indexOf(p) >= currentPlayer && players.indexOf(p) < firstPlayer)
					p.notifyFrenzy(false);
				else
					p.notifyFrenzy(true);
			}
		else {
			for (Player p : players)
				p.notifyFrenzy(true);
		}
	}
}
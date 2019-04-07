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
	private int currentTurn;

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


	private void updateActions() {
		//TODO consider passing an argument and using enum for actions
		if(!finalFrenzy) {
			Action defaultMove = new Action(3, false, false, false);
			Action defaultGrab = new Action(1, true, false, false);
			Action upgradedGrab = new Action(2, true, false, false);
			Action defaultShoot = new Action(0, false, true, false);
			Action upgradedShoot = new Action(1, false, true, false);
			//Default actions up to 2 damages
			ArrayList<Action> zeroLevelActions = new ArrayList<>();
			zeroLevelActions.add(defaultMove);
			zeroLevelActions.add(defaultGrab);
			zeroLevelActions.add(defaultShoot);

			//Improved actions up to 5 damages
			ArrayList<Action> firstLevelActions = new ArrayList<>();
			firstLevelActions.add(defaultMove);
			firstLevelActions.add(upgradedGrab);
			firstLevelActions.add(defaultShoot);

			//Improved actions from 6 damages
			ArrayList<Action> secondLevelActions = new ArrayList<>();
			secondLevelActions.add(defaultMove);
			secondLevelActions.add(upgradedGrab);
			secondLevelActions.add(upgradedShoot);
			for(Player p: players) {
				if(p.getDamagesCount() < 3) p.setActions(zeroLevelActions);
				else if(p.getDamagesCount() < 6) p.setActions(firstLevelActions);
				else p.setActions(secondLevelActions);
			}
		}
		else {
			//TODO handle frenzy actions
		}
	}

}
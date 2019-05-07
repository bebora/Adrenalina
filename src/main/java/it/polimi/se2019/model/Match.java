package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

/**
 * Container class for all the information of the Match being played
 */
public abstract class Match {

    public Match(List<Player> players, String boardFilename, int numSkulls) {
        this.players = players;
        finalFrenzy = false;
        turnEnd = false;
        firstPlayer = rand.nextInt(players.size());
        currentPlayer = firstPlayer;
        board = BoardCreator.parseBoard(boardFilename, numSkulls);
    }
    public Match(Match originalMatch){
    	this.currentPlayer = originalMatch.getCurrentPlayer();
    	this.finalFrenzy = originalMatch.getFinalFrenzy();
    	this.firstPlayer = originalMatch.getFirstPlayer();
    	this.currentPlayer = originalMatch.getCurrentPlayer();
    	this.board = originalMatch.getBoard();
    	this.players = originalMatch.getPlayers().stream()
				.map(Player::new).collect(Collectors.toList());
	}


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
	private List <Player> players;

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
	 * Manage the change to frenzy mode
	 * Update the actions available for each player
	 * Update the reward points given by each player
 	 */
	public void startFrenzy() {
		finalFrenzy = TRUE;
		Boolean afterFirst;
		// Update actions
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

		// Update reward points
		List<Player> toUpdate = players.stream().filter(p->!p.getDominationSpawn()).collect(Collectors.toList());
		for (Player p : toUpdate) {
			p.setFirstShotReward(false);
			if (!p.getRewardPoints().isEmpty())
				p.getRewardPoints().subList(1,p.getRewardPoints().size()).clear();
			p.getRewardPoints().addAll(new ArrayList<Integer>(Arrays.asList(2,1,1,1)));
		}
	}

	/**
	 * Manage the start of a new turn
	 * Score the board of dead players
	 * Add the points for double kill (domination spawn kill doesn't count)
	 * Refresh {@link Board#weaponsDeck} and {@link Board#ammoCards} on the board
	 * Start Frenzy if conditions are met
	 */
	public void newTurn() {
		// Dead players
		List<Player> deadPlayers = players.stream().
				filter(p -> p.getAlive() == ThreeState.FALSE).collect(Collectors.toList());
		for (Player p : deadPlayers) {
			scorePlayerBoard(p);
			p.resetPlayer();
			p.addPowerUp(board.drawPowerUp(), false);
		}

		// Point for double shot
        if (deadPlayers.stream().filter(p -> !p.getDamages().get(11).getDominationSpawn()).count() > 1)
            players.get(currentPlayer).addPoints(1);

		board.refreshWeapons();
		board.refreshAmmos();

		currentPlayer = (currentPlayer + 1) %
				players.
					stream().
					filter(p->!p.getDominationSpawn()).
					collect(Collectors.toList()).
					size();

		//TODO add checking for winners and end of the game, finding a way to keep count of when it happens!

		if (!finalFrenzy && checkFrenzy())
			startFrenzy();
	}

	/**
	 * Score personal Player Board when the corresponding Player is dead or match is ended
	 * Check and score the first blood if {@link Player#firstShotReward} is TRUE
	 * Score the damage of each player accordingly to the rules, following the {@link Player#rewardPoints} and giving a minimum of 1 points to every shooter
	 * Score for overkill
	 * @param player player to score
	 */
	public void scorePlayerBoard(Player player) {
		// first blood
		if (player.getFirstShotReward() == TRUE)
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

		// Score for overkill
		if (player.getDamages().size() >= 11) {
			scoreDeadShot(player);
		}
	}

	public abstract void scoreDeadShot(Player player);

	/**
	 * Get players in {@code tile}
	 * @param tile tile where to take the players
	 * @return List of players in Tile
	 */
	public List<Player> getPlayersInTile(Tile tile){
		return this.getPlayers().stream()
				.filter(p -> p.getTile() == tile)
				.collect(Collectors.toList());
	}

	/**
	 * Get players in {@code room}
	 * @param room room where to take the players
	 * @return List of players in Room
	 */
	public List<Player> getPlayersInRoom(Color room){
		return this.getPlayers().stream()
				.filter(p -> p.getTile().getRoom() == room)
				.collect(Collectors.toList());
	}

	/**
	 * Check if conditions for Frenzy are met
	 * @return True if need to start frenzy, false otherwise
	 */
	public boolean checkFrenzy() {
		return getBoard().getKillShotTrack().size() >= getBoard().getSkulls() * 2;
	}

	public abstract List<Player> getWinners();

	public List<Player> getPlayers(){ return players;}

	public int getCurrentPlayer(){return currentPlayer;}

	public Board getBoard(){return board; }


	public int getFirstPlayer() {
		return firstPlayer;
	}

	public Boolean getFinalFrenzy() {
		return finalFrenzy;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

}
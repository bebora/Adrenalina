package it.polimi.se2019.model;

import it.polimi.se2019.controller.UpdateSender;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.VirtualView;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

/**
 * Container class for all the information of the Match being played.
 * Abstract class, supporting common logic in the different modes.
 */
public abstract class Match {

    public Match(List<Player> players, String boardFilename, int numSkulls) {
        this.players = players;
        List<Color> colors = Arrays.asList(Color.values());
        Collections.shuffle(colors);
        int i = 0;
        for (Player p : players) {
        	p.setColor(colors.get(i));
        	i = (i + 1) % colors.size();
        	VirtualView v= p.getVirtualView();
        	if (v != null && v.getRequestDispatcher() != null) {
        		v.getRequestDispatcher().setEventHelper(this, p);
			}
		}
        finalFrenzy = false;
        turnEnd = false;
        firstPlayer = rand.nextInt(players.size());
        currentPlayer = firstPlayer;
        players.get(firstPlayer).setFirstPlayer(true);
        board = BoardCreator.parseBoard(boardFilename, numSkulls);
        this.updateSender = new UpdateSender(this);
        for(Player p: this.players){
        	p.setMatch(this);
		}
    }

	/**
	 * Clones the match, creating a new one to save progress.
	 * Used in {@link it.polimi.se2019.controller.ActionController#sandboxMatch}.
	 * @param originalMatch
	 */
	public Match(Match originalMatch){
    	this.currentPlayer = originalMatch.getCurrentPlayer();
     	this.finalFrenzy = originalMatch.getFinalFrenzy();
    	this.firstPlayer = originalMatch.getFirstPlayer();
    	this.currentPlayer = originalMatch.getCurrentPlayer();
    	this.board = originalMatch.getBoard();
    	this.players = originalMatch.getPlayers().stream().filter(p -> !p.getDominationSpawn())
				.map(Player::new).peek(p->p.setMatch(this)).collect(Collectors.toList());
    	this.updateSender = originalMatch.getUpdateSender();
    	//Initialize match and notify player with the first totalUpdate
	}

	/**
	 * Restores the {@code oldMatch}, updating the players.
	 * @param oldMatch to restore
	 */
	public void restoreMatch(Match oldMatch){
    	for(int i = 0; i < players.size(); i++)
    		this.players.get(i).restorePlayer(oldMatch.getPlayers().get(i));
    	// Set event helper to original player
		this.players.stream().filter(p -> p.getVirtualView() != null && p.getVirtualView().getRequestDispatcher() != null).forEach(p -> p.getVirtualView().getRequestDispatcher().setEventHelper(oldMatch, p));
		oldMatch.updateViews();
	}


    protected Random rand = new Random();
	/**
	 * Sender used to send updates to players in the match
	 */
    protected UpdateSender updateSender;

	/**
	 * True if player turn has ended
	 */
	protected boolean turnEnd;

	/**
	 * Board used for the Match
	 */
	protected Board board;

	/**
	 * List of players playing the match
	 */
	protected List <Player> players;

	/**
	 * Index of the player whose turn is the current
	 */
	protected int currentPlayer;

	/**
	 * Index of the firstPlayer
	 */
	protected int firstPlayer;

	/**
	 * If True, the activated mode is finalFrenzy
	 */
	protected boolean finalFrenzy;

	public void setCurrentPlayer(Player player) {
		this.currentPlayer = players.indexOf(player);
	}

	public void setCurrentPlayer(int player) {
		this.currentPlayer = player;
	}

	/**
	 * Manage the change to frenzy mode
	 * Update the actions available for each player
	 * Update the reward points given by each player
 	 */
	public void startFrenzy() {
		finalFrenzy = TRUE;
		Boolean afterFirst;
		List<Player> toUpdate = players.stream().filter(p->!p.getDominationSpawn()).collect(Collectors.toList());
		// Update actions
		for (Player p: toUpdate) {
			if (firstPlayer < currentPlayer) {
				afterFirst = !(players.indexOf(p) >= currentPlayer || players.indexOf(p) < firstPlayer);
				p.notifyFrenzy(afterFirst);
			}
			else if (firstPlayer > currentPlayer) {
				afterFirst = !(players.indexOf(p) >= currentPlayer && players.indexOf(p) < firstPlayer);
				p.notifyFrenzy(afterFirst);
			}
			else {
				p.notifyFrenzy(true);
			}
		}
		// Update reward points for players with no damage
		for (Player p : toUpdate) {
			p.setFrenzyActions(true);
			if (p.getDamages().isEmpty()) {
				p.setFirstShotReward(false);
				p.setFrenzyBoard(true);
				p.setRewardPoints(new ArrayList<>(Arrays.asList(2, 1, 1, 1)));
			}
			else {
				p.setFrenzyBoard(false);
			}
		}
		updateViews();
		firstPlayer = currentPlayer;
	}

	/**
	 * Manage the start of a new turn
	 * Score the board of dead players
	 * Add the points for double kill (domination spawn kill doesn't count)
	 * Refresh {@link Board#weaponsDeck} and {@link Board#ammoCards} on the board
	 * Start Frenzy if conditions are met
	 * @return true if match is finished
	 */
	public boolean newTurn() {
		// Dead players
		List<Player> deadPlayers = players.stream().
				filter(p -> p.getAlive() == ThreeState.FALSE).collect(Collectors.toList());
		if (board.getSkulls() > 0) {
			board.setSkulls(board.getSkulls() - deadPlayers.size());
			if (board.getSkulls() < 0)
				board.setSkulls(0);
		}
		// Point for double kill, filtering players killed by ending turn on domination spawn
		// Handle case of multiple deadshots also from players that aren't the current player (thanks to powerups)
		List<Player> playersNotSelfDead = deadPlayers.stream().filter(p -> !p.getDamages().get(10).equals(p)).collect(Collectors.toList());
		if (playersNotSelfDead.size() > 1)
			players.stream().
					filter(p -> playersNotSelfDead.stream().filter(x -> x.getDamages().get(10).equals(p)).count() > 1). //Get players who have done at least 2 deadshots in the dead players
					collect(Collectors.toList()).
					forEach(p -> p.addPoints(1));

		for (Player p : deadPlayers) {
			scorePlayerBoard(p);
			p.resetPlayer();
			p.addPowerUp(board.drawPowerUp(), false);
			// Set reward points and first shot reward to players who haven't changed it yet
			if (finalFrenzy  && !p.isFrenzyBoard()) {
				p.setRewardPoints(Arrays.asList(2, 1, 1, 1));
				p.setFirstShotReward(false);
				p.setFrenzyBoard(true);
			}
		}

		board.refreshWeapons();
		board.refreshAmmos();

		do {
			currentPlayer = (currentPlayer + 1)% players.size();
		} while (players.get(currentPlayer).getDominationSpawn());

		if (finalFrenzy && currentPlayer == firstPlayer) {
			updateViews();
			return true;
		}

		updateViews();
		if (!finalFrenzy && checkFrenzy())
			startFrenzy();
		updateViews();
		return false;
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
		if (player.getFirstShotReward() == TRUE && !player.getDamages().isEmpty()) {
		    // Don't add reward if damage is given from spawnpoint
            if (!player.getDamages().get(0).equals(player)) {
                player.getDamages().get(0).addPoints(1);
            }
        }

		int currentRewardIndex = 0;

		// damages scoring
		Map<Player, Long> frequencyDamages =
				player.getDamages().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Comparator<Player> givenDamages = Comparator.comparing(frequencyDamages::get, Comparator.reverseOrder());
		Comparator<Player> indices = Comparator.comparing(player.getDamages()::indexOf);


		Set<Player> damagingPlayers = new HashSet<>(player.getDamages());

		List<Player> damageOrder = damagingPlayers.stream().sorted(givenDamages.thenComparing(indices)).collect(Collectors.toList());
		while (!damageOrder.isEmpty()) {
			Player current = damageOrder.get(0);
			// Don't add points to current if it's the dead player
			if (!current.equals(player)) {
				if (currentRewardIndex < player.getRewardPoints().size()) {
					damageOrder.get(0).addPoints(player.getRewardPoints().get(currentRewardIndex));
				} else {
					damageOrder.get(0).addPoints(1);
				}
			}
			damageOrder.remove(0);
			currentRewardIndex++;
		}

		// Score for deadshot and overkill
		if (player.getDamages().size() >= 11) {
			scoreDeadShot(player);
		}
		updateViews();
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

	public List<SpawnPlayer> getSpawnPoints(){return new ArrayList<>(); }

	public UpdateSender getUpdateSender() {
		return updateSender;
	}

	/**
	 * Send total update to each player in match
	 */
	public void updateViews() {
		for (Player p: players) {
			// Some players may not have any virtualView in tests
			if (p.getMatch() == null || p.getVirtualView() == null  || !p.getOnline()) continue;
			updateSender.sendTotalUpdate(p, p.getUsername(), board, players,
					p.getId(), p.getPoints(), p.getPowerUps(),
					p.getWeapons().stream().filter(Weapon::getLoaded).collect(Collectors.toList()), players.get(currentPlayer));
		}
	}
}
package it.polimi.se2019.model;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NormalMatch extends Match {

	public NormalMatch(List<Player> players, String boardFilename, int numSkulls) {
		super(players, boardFilename, numSkulls);
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
	private List<Player> players;

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


	public void resetPlayer(Player player) {
		player.addPowerUp(board.drawPowerUp(), false);
		player.getDamages().clear();
	}

	public void scorePlayerBoard(Player player) {
		super.scorePlayerBoard(player);
		// death shot
		if (player.getDamages().size() >= 11) {
			scoreDeadShot(player);
		}
	}

	public void scoreDeadShot(Player player) {
		board.addToKillShot(player.getDamages().get(10));
		player.getRewardPoints().remove(0);
		if (player.getDamages().size() == 12) {
			board.addToKillShot(player.getDamages().get(11));
			player.getDamages().get(11).receiveMark(player);
		} else {
			board.addToKillShot(null);
		}
	}

	public List<Player> getWinners() {
		Map<Player, Long> deadTrackPoints = new HashMap<>();
		for (Player p : players)
			scorePlayerBoard(p);

		int currentReward = 0;

		Map<Player, Long> frequencyShots =
				board.getKillShotTrack().
						stream().
						filter(Objects::nonNull).
						collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Comparator<Player> givenDamages = Comparator.comparing(frequencyShots::get);
		Comparator<Player> indices = Comparator.comparing(board.getKillShotTrack()::indexOf);

		HashSet<Player> shotGiver = new HashSet<>(board.getKillShotTrack());
		List<Player> shotOrder = shotGiver.stream().sorted(givenDamages.thenComparing(indices)).collect(Collectors.toList());


		for (Player p : shotOrder) {
            deadTrackPoints.put(p, frequencyShots.get(p));
            p.addPoints(board.getKillShotReward().get(currentReward));
            currentReward++;
		}

		for (Player p : players) {
			if (!deadTrackPoints.keySet().contains(p)) {
				deadTrackPoints.put(p, Long.valueOf(0));
			}
		}

		List<Player> maxPlayers = players.stream().filter(p -> p.getPoints() == players.stream().max(Comparator.comparing(Player::getPoints)).get().getPoints()).collect(Collectors.toList());
		if (maxPlayers.size() == 1) {
			return maxPlayers;
		} else {
			int maxTrack = Collections.max(maxPlayers, Comparator.comparing(deadTrackPoints::get)).getPoints();
			return maxPlayers.stream().
					filter(p -> p.getPoints() == maxTrack).
					collect(Collectors.toList());
		}

	}

}





















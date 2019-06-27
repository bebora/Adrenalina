package it.polimi.se2019.model;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains and supports the information for the flow of a Match in standard mode.
 * Supports the killShotTrack and a custom parsing of winners, compared to the general {@link Match}.
 */
public class NormalMatch extends Match {

	public NormalMatch(List<Player> players, String boardFilename, int numSkulls) {
		super(players, boardFilename, numSkulls);
	}
	public NormalMatch(Match originalMatch){
		super(originalMatch);
	}

	/**
	 * Score a deadShot to {@code player}.
	 * The player that gave the kill shot gets added to the killShotTrack.
	 * If an overkill took place, the player that overkilled gets added to the track and a mark gets added to the overkiller.
	 * @param player who got the killshot
	 */
	@Override
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

	/**
	 * Parse the winners of the current Game, at the end of it.
	 * <p><ul>
	 *    * <li>It scores the players in the game.
	 *    * <li>It scores the killShotTrack, sorting looking at the players that gave the more shots.
	 *    * <li>It checks the online players, and the order of the killings to create the list of winners.
	 *    * </ul><p>
	 * @return winning players
	 */
	@Override
	public List<Player> getWinners() {
		for (Player p : players)
			scorePlayerBoard(p);

		int currentReward = 0;
		//How much drops each player has in the killShotTrack
		Map<Player, Long> frequencyShots =
				board.getKillShotTrack().
						stream().
						filter(Objects::nonNull).
						collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		Comparator<Player> givenDamages = Comparator.comparing(frequencyShots::get, Comparator.reverseOrder());
		Comparator<Player> indices = Comparator.comparing(board.getKillShotTrack()::indexOf);

		HashSet<Player> shotGiver = board.getKillShotTrack().stream().
				filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
		List<Player> shotOrder = shotGiver.stream().sorted(givenDamages.thenComparing(indices)).collect(Collectors.toList());
		//Store how many points each player got from the killshot track
		Map<Player, Integer> deadTrackPoints = new HashMap<>();
		for (Player p : shotOrder) {
            deadTrackPoints.put(p, board.getKillShotReward().get(currentReward));
            p.addPoints(board.getKillShotReward().get(currentReward));
            currentReward++;
		}
		//Populate deadTrackPoints with players who haven't got any points from it
		for (Player p : players) {
			if (!deadTrackPoints.keySet().contains(p)) {
				deadTrackPoints.put(p, 0);
			}
		}
		//If nobody is online, nobody won
		if (players.stream().noneMatch(Player::getOnline)) {
			return new ArrayList<>();
		}
		//Get max points across all players who can win (they can't be offline)
		int bestPoints = players.stream().filter(Player::getOnline).max(Comparator.comparing(Player::getPoints)).get().getPoints();
		//Get players who have those points
		List<Player> maxPlayers = players.stream().filter(p -> p.getOnline() && p.getPoints() == bestPoints).collect(Collectors.toList());
		if (maxPlayers.size() <= 1) {
			return maxPlayers;
		} else {
			//Get player(s) who got max points from killshot track. If parity with 0 points, both win
			int maxTrackPoints = deadTrackPoints.get(Collections.max(maxPlayers, Comparator.comparing(deadTrackPoints::get)));
			return maxPlayers.stream().
					filter(p -> deadTrackPoints.get(p) == maxTrackPoints).
					collect(Collectors.toList());
		}
	}
}





















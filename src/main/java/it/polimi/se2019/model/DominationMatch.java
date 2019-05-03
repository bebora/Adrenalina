package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DominationMatch extends Match{

    /**
     * Tracks the current turn, used to add the Domination Spawn in the correct moment
     */
    int currentTurn = -1;

    /**
     * Helper attribute that keeps track of the Domination Spawn players
     */
    List<Player> spawnPoints;

    /**
     * Create a Domination Match, using {@link #Match} constructor
     * Initializes {@link #spawnPoints}  with an ArrayList
     * @param players players to add in the Match
     * @param boardFilename name of the Board that uses the Match
     * @param numSkulls number of the skulls to use
     */
    public DominationMatch(List<Player> players, String boardFilename, int numSkulls) {
        super(players,boardFilename,numSkulls);
        spawnPoints = new ArrayList<>();
    }

    /**
     * Initializes a new turn in Domination Mode
     * Add damages to {@link Match#currentPlayer} if it's on a SpawnPoint
     * Hit the spawnPoint (even if already hit)
     * Insert spawnPoints if second Turn
     */
    @Override
    public void newTurn() {
        Player currentPlayer = super.getPlayers().get(getCurrentPlayer());
        List<Tile> spawnTiles = spawnPoints.stream().map(Player::getTile).collect(Collectors.toList());

        // Add a damage to the current Player if it's in a spawnTile
        // Add a damage to the spawnPoint if the currentPlayer is the only one in the spawnPoint
        if (spawnTiles.contains(currentPlayer.getTile())) {
            currentPlayer.receiveShot(currentPlayer, 1, 0);
            long numPlayerInTile = getPlayers().
                    stream().
                    filter(p -> !p.getDominationSpawn()).
                    filter(p -> p.getTile() == currentPlayer.getTile()).count();
            if (numPlayerInTile == 1) {
                SpawnPlayer spawnPoint = (SpawnPlayer) spawnPoints.
                        stream().
                        filter(s -> s.getTile() == currentPlayer.getTile()).findFirst().
                        orElseThrow(UnsupportedOperationException::new);
                spawnPoint.setDamaged(false);
                spawnPoint.receiveShot(currentPlayer, 1, 0);
            }
        }

        super.newTurn();

        if (super.getCurrentPlayer() == super.getFirstPlayer()) {
            currentTurn++;
        }

        for (Player p : spawnPoints) {
            p.resetPlayer();
        }

        if (currentTurn == 1) {
            insertSpawnPoints();
        }
    }

    /**
     * Add spawnPoints to the Board, in the following spawn tiles:
     * <li>RED</li>
     * <li>YELLOW</li>
     * <li>BLUE</li>
     */
    public void insertSpawnPoints() {
        List<Color> colors = new ArrayList<>(Arrays.asList(Color.RED, Color.YELLOW, Color.BLUE));
        for (Color color : colors) {
            Player temp = new SpawnPlayer(color);
            Tile relatedTile = getBoard().
                    getTiles().stream().
                    flatMap(List::stream).
                    filter(t -> t.isSpawn() && t.getRoom().equals(color)).
                    findFirst().orElseThrow(UnsupportedOperationException::new);
            temp.setTile(relatedTile);
        }
        spawnPoints = getPlayers().stream().filter(Player::getDominationSpawn).collect(Collectors.toList());

    }

    /**
     * Score the dead shot, adding damages that can be allocated to a spawn point of choice
     * @param player player where dead shot is scored
     */
    @Override
    public void scoreDeadShot(Player player) {
        if (player.getDamages().size() == 12 && !player.getDamages().get(12).getDominationSpawn())
            player.getDamages().get(12).addDamagesAllocable();
    }

    /**
     * Get winners by scoring the players and the spawnPoints, and returning the list of winners
     * @return list of player declared winner of the game
     */
    @Override
    public List<Player> getWinners() {
        for (Player p : getPlayers()) {
            if (!spawnPoints.contains(p))
                scorePlayerBoard(p);
            else {
                scoreSpawnPoint(p);
            }
        }

        return getPlayers().
                stream().
                filter(s -> s.getPoints() == getPlayers().stream().max(Comparator.comparing(Player::getPoints)).get().getPoints()).
                collect(Collectors.toList());
    }


    /**
     * Score a spawn point ad the end of the game
     * Map the frequency of damages, compare and give points in order to the more frequent shooter
     * Give same points to the n shooter with same frequency, but skip n {@link Player#rewardPoints} for the next shooter
     * @param p spawnPoint to score
     */
    private void scoreSpawnPoint(Player p) {
        int currentReward = 0;

        //Map the frequency of damages
        Map<Player, Long> frequencyShots =
                p.getDamages().
                        stream().
                        filter(Objects::nonNull).
                        collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        //Create comparator to compare the different players
        Comparator<Player> givenDamages = Comparator.comparing(frequencyShots::get);
        Comparator<Player> indices = Comparator.comparing(p.getDamages()::indexOf); //necessary?

        HashSet<Player> shotGiver = new HashSet<>(p.getDamages());
        List<Player> shotOrder = shotGiver.stream().sorted(givenDamages.thenComparing(indices)).collect(Collectors.toList());

        //Add same points to same player with same value in frequencyShots
        int currentSameShots = 1;
        for (int i = 0; i < shotOrder.size(); i++) {
            if (i != 0 && frequencyShots.get(shotOrder.get(i)) ==
                    frequencyShots.get(shotOrder.get(i)) - 1)
                currentSameShots ++;
            else {
                currentReward += currentSameShots;
                currentSameShots = 1;
            }
            shotOrder.get(i).addPoints(p.getRewardPoints().get(currentReward));
        }
    }

    /**
     * Check if frenzy can be started
     * Frenzy can start if spawnpoints with >8 damages are equal or more than 2, or if {@link Match#checkFrenzy()} is True
     * @return true if frenzy need to start
     */
    @Override
    public boolean checkFrenzy() {
        Long countDeadSpawnPoint = getPlayers().
                stream().
                filter(p -> p.getDominationSpawn()).
                filter(p -> p.getDamages().size() >= 8).
                count();
        return super.checkFrenzy() || countDeadSpawnPoint >= 2;
    }




}

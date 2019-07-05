package it.polimi.se2019.model;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Contains and supports the information for the flow of a Match in domination mode.
 * Supports the SpawnPoints and inserts them after a turn of every player passed.
 */

public class DominationMatch extends Match {

    /**
     * Tracks the current turn, used to add the Domination Spawn in the correct moment
     */
    private int currentTurn = -1;

    /**
     * Create a Domination Match, using {@link Match} constructor
     * Initializes {@link #spawnPoints}  with an ArrayList
     * @param players       players to add in the Match
     * @param boardFilename name of the Board that uses the Match
     * @param numSkulls     number of the skulls to use
     */
    public DominationMatch(List<Player> players, String boardFilename, int numSkulls) {
        super(players, boardFilename, numSkulls);
        spawnPoints = new ArrayList<>();
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public void setSpawnPoints(List<SpawnPlayer> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Restores a Domination Match, by setting the current Turn to the current one, avoiding to insert spawnPoints more than once.
     * @param oldMatch to restore
     */
    @Override
    public void restoreMatch(Match oldMatch) {
        DominationMatch oldDominationMatch = (DominationMatch) oldMatch;
        oldDominationMatch.setCurrentTurn(currentTurn);
        super.restoreMatch(oldMatch);
    }

    public DominationMatch(Match match){
        super(match);
        this.spawnPoints = match.players.stream().filter(Player::getDominationSpawn).map(p -> (SpawnPlayer) p).map( p -> new SpawnPlayer(p)).peek(p->p.setMatch(this)).collect(Collectors.toList());
        this.players.addAll(spawnPoints);
        this.currentTurn = ((DominationMatch) match).getCurrentTurn();
    }

    /**
     * Initializes a new turn in Domination Mode
     * Add damages to {@link Match#currentPlayer} if it's on a SpawnPoint
     * Hit the spawnPoint (even if already hit)
     * Insert spawnPoints if second Turn
     */
    @Override
    public boolean newTurn() {
        Player currentPlayer = super.getPlayers().get(getCurrentPlayer());
        List<Tile> spawnTiles = spawnPoints.stream().map(Player::getTile).collect(Collectors.toList());

        // Add a damage to the current Player if it's in a spawnTile
        // Add a damage to the spawnPoint if the currentPlayer is the only one in the spawnPoint
        if (spawnTiles.contains(currentPlayer.getTile())) {
            currentPlayer.receiveShot(currentPlayer, 1, 0, true);
            long numPlayerInTile = getPlayers().
                    stream().
                    filter(p -> !p.getDominationSpawn()).
                    filter(p -> p.getTile() == currentPlayer.getTile()).count();
            if (numPlayerInTile == 1) {
                SpawnPlayer spawnPoint = spawnPoints.
                        stream().
                        filter(s -> s.getTile().equals(currentPlayer.getTile())).findFirst().
                        orElseThrow(UnsupportedOperationException::new);
                Logger.log(Priority.DEBUG, "Damaging the spawn " + spawnPoint.getColor().toString());
                Logger.log(Priority.DEBUG, spawnPoint.getColor().toString() + " has " + spawnPoint.getDamagesCount());
                spawnPoint.setDamaged(false);
                spawnPoint.receiveShot(currentPlayer, 1, 0, true);
                spawnPoint.setDamaged(false);
            }
        }

        boolean toReturn = super.newTurn();

        if (super.getCurrentPlayer() == super.getFirstPlayer()) {
            currentTurn++;
        }

        for (Player p : spawnPoints) {
            p.resetPlayer();
        }



        if (currentTurn == 0) {
            insertSpawnPoints();
            currentTurn++;
        }
        return toReturn;
    }

    /**
     * Add spawnPoints to the Board, in the following spawn tiles:
     * <li>RED</li>
     * <li>YELLOW</li>
     * <li>BLUE</li>
     */
    public void insertSpawnPoints() {
        spawnPoints = new ArrayList<>();
        List<Color> colors = new ArrayList<>(Arrays.asList(Color.RED, Color.YELLOW, Color.BLUE));
        for (Color color : colors) {
            SpawnPlayer temp = new SpawnPlayer(color);
            Tile relatedTile = getBoard().
                    getTiles().stream().
                    flatMap(List::stream).
                    filter(t -> t!= null && t.isSpawn() && t.getRoom().equals(color)).
                    findFirst().orElseThrow(UnsupportedOperationException::new);
            temp.setTile(relatedTile);
            temp.setMatch(this);
            spawnPoints.add(temp);
            super.getPlayers().add(temp);
        }
    }

    /**
     * Deadshots in domination are not handled, they are just ignored as by rules
     * Reward points are modified, though
     * @param player player where dead shot is scored
     */
    @Override
    public void scoreDeadShot(Player player) {
        if (!player.getRewardPoints().isEmpty())
            player.getRewardPoints().remove(0);
    }

    /**
     * Get winners by scoring the players and the spawnPoints, and returning the list of winners
     * Offline players can't be winners
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
        Player maxPointPlayer = getPlayers().stream().filter(Player::getOnline).max(Comparator.comparing(Player::getPoints)).orElse(null);
        if (maxPointPlayer != null) {
            int maxPoints = maxPointPlayer.getPoints();
            return getPlayers().
                    stream().
                    filter(p -> p.getPoints() == maxPoints && p.getOnline() && !p.getDominationSpawn()).
                    collect(Collectors.toList());
        }
        else
            return new ArrayList<>();
    }


    /**
     * Score a spawn point ad the end of the game
     * Map the frequency of damages, compare and give points in order to the more frequent shooter
     * Give same points to the n shooter with same frequency, but skip n {@link Player#rewardPoints} for the next shooter
     * @param spawnPlayer spawnPoint to score
     */
    public void scoreSpawnPoint(Player spawnPlayer) {
        //How many damages gave each player
        int currentRewardIndex = 0;
        //Map the frequency of damages
        Map<Player, Long> frequencyShots =
                spawnPlayer.getDamages().
                        stream().
                        filter(Objects::nonNull).
                        collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        //Set with all unique damage value received by spawnPlayer, in descending order
        SortedSet<Long> damagesSet = new TreeSet<>(Collections.reverseOrder());
        damagesSet.addAll(frequencyShots.values());
        for (Long damage: damagesSet) {
            List<Player> playerWithSameDamageGiven = frequencyShots.entrySet().stream().
                    filter(d -> d.getValue().equals(damage)).map(Map.Entry::getKey).collect(Collectors.toList());
            //Add same points to players with same value in frequencyShots
            for (Player t: playerWithSameDamageGiven) {
                t.addPoints(spawnPlayer.getRewardPoints().get(currentRewardIndex));
            }
            currentRewardIndex += playerWithSameDamageGiven.size();
        }
    }

    /**
     * Check if frenzy can be started
     * Frenzy can start if spawnpoints with >8 damages are equal or more than 2, or if {@link Match#checkFrenzy()} is True
     * @return true if frenzy need to start
     */
    @Override
    public boolean checkFrenzy() {
        long countDeadSpawnPoint = getPlayers().
                stream().
                filter(Player::getDominationSpawn).
                filter(p -> p.getDamages().size() >= 8).
                count();
        return super.checkFrenzy() || countDeadSpawnPoint >= 2;
    }

    @Override
    public List<SpawnPlayer> getSpawnPoints(){ return spawnPoints; }
}

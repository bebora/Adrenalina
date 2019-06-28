package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.controller.VirtualView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DominationMatchTest {
    private DominationMatch match;
    private Player foo, poo, boo;
    private List<SpawnPlayer> spawnerPlayers;

    private void insertSpawnPoints() {
        int limit = match.getPlayers().size();
        for (int i = 0; i < limit; i++) {
            match.newTurn();
        }
    }
    @BeforeEach
    void prepareMatch() {
        List<Player> players = new ArrayList<>(Arrays.asList(new Player("yijie"), new Player("roland"), new Player("antonio")));
        match = new DominationMatch(players, "board1.btlb", 8);
        spawnerPlayers = match.getSpawnPoints();
    }

    @Test
    void insertSpawnPointsTest() {
        assertEquals(0, spawnerPlayers.size());
        insertSpawnPoints();
        spawnerPlayers = match.getSpawnPoints();
        assertEquals(3, spawnerPlayers.size());
        assertTrue(spawnerPlayers.stream().allMatch(p -> p.getDominationSpawn() && p.getUsername().equals(p.getColor().toString())));
    }

    @Test
    void checkPlayerDamaged() {
        insertSpawnPoints();
        spawnerPlayers = match.getSpawnPoints();
        Tile destTile = spawnerPlayers.get(match.getCurrentPlayer()).getTile();
        Player current = match.getPlayers().get(match.getCurrentPlayer());
        current.setTile(destTile);
        match.newTurn();
        assertEquals(1, current.getDamagesCount());
        assertEquals(current, current.getDamages().get(0));
    }

    @Test
    void checkSpawnDamaged() {
        insertSpawnPoints();
        //SpawnPoints have been generated after first round
        spawnerPlayers = match.getSpawnPoints();
        Tile destTile = spawnerPlayers.get(0).getTile();
        Tile otherTile = spawnerPlayers.get(1).getTile();
        Player current = match.getPlayers().get(match.getCurrentPlayer());
        //Move current player to spawn alone
        current.setTile(destTile);
        //Set other players to other spawn tile
        match.getPlayers().stream().filter(p -> !p.getDominationSpawn() && !p.equals(current)).forEach(p -> p.setTile(otherTile));
        match.newTurn();
        //A player alone in a spawn tile should give it one damage
        assertEquals(1, spawnerPlayers.get(0).getDamagesCount());
        //Other spawn should not have any damage
        assertEquals(0, spawnerPlayers.get(1).getDamagesCount());
        //Current player will be one of the other two, so now we can go to the next turn and see if the other spawn has been damaged
        match.newTurn();
        assertEquals(0, spawnerPlayers.get(1).getDamagesCount());
        Player newCurrent = match.getPlayers().get(match.getCurrentPlayer());
        //Move all player except the current one to another tile and check damage received by spawn
        match.getPlayers().stream().filter(p -> !p.getDominationSpawn() && !p.equals(newCurrent)).forEach(p -> p.setTile(destTile));
        spawnerPlayers.get(1).receiveShot(newCurrent, 4, 4, true);
        assertEquals(1, spawnerPlayers.get(1).getDamagesCount());
        spawnerPlayers.get(1).receiveShot(newCurrent, 4, 4, true);
        assertEquals(1, spawnerPlayers.get(1).getDamagesCount());
        match.newTurn();
        assertEquals(2, spawnerPlayers.get(1).getDamagesCount());
    }

    @Test
    void checkPointsGivenFromTracks() {
        //Setup 5 player match
        foo = new Player();
        poo = new Player();
        boo = new Player();
        Player doo = new Player();
        Player moo = new Player();
        match = new DominationMatch(new ArrayList<>(Arrays.asList(foo, poo, boo, doo, moo)), "board1.btlb", 8);
        insertSpawnPoints();
        Player spawn =  match.getSpawnPoints().get(0);
        spawn.getDamages().addAll(Collections.nCopies(3, foo));
        spawn.getDamages().addAll(Collections.nCopies(2, poo));
        spawn.getDamages().addAll(Collections.nCopies(2, boo));
        spawn.getDamages().addAll(Collections.nCopies(1, doo));
        spawn.getDamages().addAll(Collections.nCopies(1, moo));
        match.scoreSpawnPoint(spawn);
        assertEquals(8, foo.getPoints());
        assertEquals(6, poo.getPoints());
        assertEquals(6, boo.getPoints());
        assertEquals(2, doo.getPoints());
        assertEquals(2, moo.getPoints());
    }

    @Test
    void scorePlayerBoard() {
        insertSpawnPoints();
        Player current = match.getPlayers().get(match.getCurrentPlayer());
        // Enable first shot reward
        current.setFirstShotReward(true);
        Tile destTile = match.getSpawnPoints().get(0).getTile();
        current.setTile(destTile);
        //Receive 1 damage from spawn, 6 from one player and 4 from the other
        match.newTurn();
        Player newCurrent = match.getPlayers().get(match.getCurrentPlayer());
        Player last = match.getPlayers().get(MatchTest.nextPlayerIndex(match));
        current.receiveShot(newCurrent, 5, 0, true);
        current.receiveShot(last, 5, 0, true);
        match.newTurn();
        // Current should not receive any points from its death
        assertEquals(0, current.getPoints());
        assertEquals(8, newCurrent.getPoints());
        assertEquals(6, last.getPoints());
    }

    @Test
    void noDoubleKillFromSpawnKillShot() {
        insertSpawnPoints();
        Player current = match.getPlayers().get(match.getCurrentPlayer());
        Player other = match.getPlayers().get(MatchTest.nextPlayerIndex(match));
        //Disable first shot reward for test
        current.setFirstShotReward(false);
        other.setFirstShotReward(false);
        //Give 10 damages to current player and attack another one with 11 damages
        other.receiveShot(current, 11, 0, true);
        current.getDamages().addAll(Collections.nCopies(10, other));
        Tile destTile = match.getSpawnPoints().get(0).getTile();
        //Move current player to spawn so that it can receive damage
        current.setTile(destTile);
        match.newTurn();
        //8 = points given only from first death of other player, assuming points for regular death are right
        assertEquals(8, current.getPoints());
    }

    @Test
    void getWinners() {
        //Player must be set to online with a virtualView to be counted in winners
        match.getPlayers().forEach(p -> p.setVirtualView(new VirtualView()));
        match.getPlayers().forEach(p -> p.setOnline(true));
        insertSpawnPoints();
        Player current = match.getPlayers().get(match.getCurrentPlayer());
        Player other = match.getPlayers().get(MatchTest.nextPlayerIndex(match));
        //Enable first shot reward for test
        current.setFirstShotReward(true);
        other.setFirstShotReward(true);
        current.getDamages().addAll(Collections.nCopies(10, other));
        other.getDamages().addAll(Collections.nCopies(10, current));
        match.getSpawnPoints().get(0).getDamages().addAll(Collections.nCopies(4, current));
        match.getSpawnPoints().get(0).getDamages().addAll(Collections.nCopies(4, other));
        match.getSpawnPoints().get(1).getDamages().addAll(Collections.nCopies(4, current));
        match.getSpawnPoints().get(1).getDamages().addAll(Collections.nCopies(4, other));
        List<Player> winners = match.getWinners();
        assertEquals(2, winners.size());
        assertTrue(winners.containsAll(Arrays.asList(current, other)));
        //Points received should be: 1 (first shot) + 8 best player in Player "other" damages + 2*8 best player in spawns
        assertEquals(1+8+2*8, winners.get(0).getPoints());
        assertEquals(1+8+2*8, winners.get(1).getPoints());
    }
}
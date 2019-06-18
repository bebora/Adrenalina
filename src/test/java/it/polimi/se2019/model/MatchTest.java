package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchTest {
    private Match match;
    public static int nextPlayerIndex(Match match) {
        long numPlayers = match.getPlayers().stream().filter(p -> !p.getDominationSpawn()).count();
        int cur = match.getCurrentPlayer();
        if (cur == numPlayers-1) return 0;
        else return cur+1;
    }

    @Test
    void getPlayersInTile() {
        List<Player> players = Arrays.asList(new Player(), new Player(), new Player());
        match = new NormalMatch(players, "board4.btlb", 8);
        Tile destTile = match.getBoard().getTile(0, 0);
        players.get(0).setTile(destTile);
        players.get(1).setTile(match.getBoard().getTile(1, 0));
        players.get(2).setTile(destTile);
        List<Player> inSameTile = match.getPlayersInTile(destTile);
        assertEquals(2, inSameTile.size());
        assertTrue(inSameTile.containsAll(Arrays.asList(players.get(0), players.get(2))));
    }

    @Test
    void getPlayersInRoom() {
        List<Player> players = Arrays.asList(new Player(), new Player(), new Player());
        match = new NormalMatch(players, "board4.btlb", 8);
        Tile destTile = match.getBoard().getTile(1, 2);
        Tile otherTile = match.getBoard().getTile(2, 3);
        players.get(0).setTile(destTile);
        players.get(1).setTile(otherTile);
        players.get(2).setTile(destTile);
        List<Player> inSameRoom = match.getPlayersInRoom(Color.YELLOW);
        assertEquals(3, inSameRoom.size());
        assertTrue(inSameRoom.containsAll(players));
    }

    @Test
    void startFrenzy() {
        List<Player> players = Arrays.asList(new Player("lorenzo"), new Player("pietro"), new Player("carmelo"));
        match = new NormalMatch(players, "board4.btlb", 8);
        players.get(0).getDamages().addAll(Collections.nCopies(10, players.get(1)));
        match.startFrenzy();
        assertTrue(match.getFinalFrenzy());
        assertTrue(players.get(0).getFirstShotReward());
        assertFalse(players.get(1).getFirstShotReward());
        assertFalse(players.get(2).getFirstShotReward());
        players.get(0).receiveShot(players.get(1), 1, 0, true);
        match.newTurn();
        assertFalse(players.get(0).getFirstShotReward());
    }

    @Test
    void checkRewardPoints() {
        List<Player> players = Arrays.asList(new Player("lorenzo"), new Player("pietro"), new Player("carmelo"));
        match = new NormalMatch(players, "board4.btlb", 8);
        Player current = match.getPlayers().get(match.getCurrentPlayer());
        Player other = match.getPlayers().get(nextPlayerIndex(match));
        assertEquals(Arrays.asList(8, 6, 4, 2, 1), current.getRewardPoints());
        current.receiveShot(other, 11, 0, true);
        match.newTurn();
        //New turn should remove first reward point
        assertEquals(Arrays.asList(6, 4, 2, 1), current.getRewardPoints());
        current.receiveShot(other, 1, 0, true);
        match.startFrenzy();
        //Frenzy should change reward points of players with no damage
        assertEquals(Arrays.asList(6, 4, 2, 1), current.getRewardPoints());
        assertEquals(Arrays.asList(2, 1, 1, 1), other.getRewardPoints());
        current.receiveShot(other, 11, 0, true);
        other.receiveShot(current, 11, 0, true);
        match.newTurn();
        //"skulls are set aside", so rewardPoints does not change anymore
        assertEquals(Arrays.asList(2, 1, 1, 1), current.getRewardPoints());
        assertEquals(Arrays.asList(2, 1, 1, 1), other.getRewardPoints());
    }
}

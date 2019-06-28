package it.polimi.se2019.model;

import it.polimi.se2019.controller.VirtualView;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NormalMatchTest {
    @Test
    void getWinnersWithParityTest() {
        List<Player> players = Arrays.asList(new Player("frederik"), new Player("leonardo"), new Player("luigi"));
        Match match = new NormalMatch(players, "board4.btlb", 8);
        players.forEach(p -> p.setVirtualView(new VirtualView()));
        players.forEach(p -> p.setOnline(true));
        Player foo = players.get(0);
        Player moo = players.get(1);
        Player boo = players.get(2);
        //Disable first shot reward for test
        players.forEach(p -> p.setFirstShotReward(false));
        foo.receiveShot(moo, 11, 0, true);
        match.newTurn();
        //Set alive so that newTurn does not check again foo next time
        foo.setAlive(ThreeState.TRUE);
        moo.receiveShot(boo, 11, 0, true);
        match.newTurn();
        //boo should have 8 points for killing moo
        //moo should have 8 points for killing foo
        assertEquals(8, moo.getPoints());
        assertEquals(8, boo.getPoints());
        //Add 2 points to make boo have the same points as moo
        boo.addPoints(2);
        List<Player> winners = match.getWinners();
        //Both should have 14 points
        assertEquals(16, moo.getPoints());
        assertEquals(16, boo.getPoints());
        //But moo should be winner, having received more points from killshot track (8 vs 6)
        assertEquals(1, winners.size());
        assertEquals(moo, winners.get(0));
    }

    @Test
    void getWinnersNoParity() {
        List<Player> players = Arrays.asList(new Player("maurizio"), new Player("davide"), new Player("maria paola"));
        Match match = new NormalMatch(players, "board4.btlb", 8);
        players.forEach(p -> p.setVirtualView(new VirtualView()));
        players.forEach(p -> p.setOnline(true));
        players.get(0).receiveShot(players.get(1), 11, 0, true);
        match.newTurn();
        players.get(0).receiveShot(players.get(2), 11, 0, true);
        match.newTurn();
        players.get(1).receiveShot(players.get(2), 11, 0, true);
        List<Player> winners = match.getWinners();
        //Player2 should win, having killed two times
        assertEquals(1, winners.size());
        assertEquals(players.get(2), winners.get(0));
    }

    @Test
    void getWinnersWhenNotOnline() {
        List<Player> players = Arrays.asList(new Player("cosimo"), new Player("simeone"), new Player("giorgio"));
        Match match = new NormalMatch(players, "board4.btlb", 8);
        players.forEach(p -> p.setVirtualView(new VirtualView()));
        players.forEach(p -> p.setOnline(true));
        players.get(0).receiveShot(players.get(1), 11, 0, true);
        match.newTurn();
        players.get(0).receiveShot(players.get(2), 11, 0, true);
        match.newTurn();
        players.get(1).receiveShot(players.get(2), 11, 0, true);
        //Player who had best points go offline
        players.get(2).setOnline(false);
        List<Player> winners = match.getWinners();
        //So now it can't win, only the second wins, who is the best of the remaining
        assertEquals(1, winners.size());
        assertEquals(players.get(1), winners.get(0));
    }

    @Test
    void boardKillShotTrackTest() {
        List<Player> players = Arrays.asList(new Player("dwm"), new Player("i3"), new Player("gnome"));
        Match match = new NormalMatch(players, "board4.btlb", 8);
        players.get(0).receiveShot(players.get(1), 11, 0, true);
        match.newTurn();
        players.get(0).receiveShot(players.get(2), 12, 0, true);
        match.newTurn();
        assertEquals(4, match.getBoard().getKillShotTrack().size());
        assertNull(match.getBoard().getKillShotTrack().get(1));
        assertNotNull(match.getBoard().getKillShotTrack().get(3));
    }
}

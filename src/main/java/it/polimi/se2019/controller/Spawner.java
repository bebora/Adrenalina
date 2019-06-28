package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.cards.PowerUp;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Controller class used for handling the spawning of dead players.
 * {@link #countDownLatch} assures asynchronous behaviour.
 */
public class Spawner extends Observer {
    private Player playerToSpawn;
    private Board board;
    private Random random;
    private CountDownLatch countDownLatch;

    public Spawner(CountDownLatch countDownLatch, Player playerToSpawn, Board board){
        Logger.log(Priority.DEBUG, "Trying to spawn a player!");
        this.playerToSpawn = playerToSpawn;
        this.board = board;
        random = new Random();
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Match getMatch() {
        return playerToSpawn.getMatch();
    }

    /**
     * Handles receiving the selected discarded PowerUp from the player.
     * @param powerUps gets discarded from player, and the spawning happens on {@link PowerUp#getDiscardAward()} spawn Tile.
     */
    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps) {
        PowerUp discarded = playerToSpawn.getPowerUps().stream().filter(p -> p.equals(powerUps.get(0))).findFirst().get();
        playerToSpawn.getMatch().getBoard().getPowerUps().addToDiscarded(discarded);
        playerToSpawn.getPowerUps().remove(discarded);
        playerToSpawn.setTile(board.getTiles().stream()
                .flatMap(Collection::stream)
                .filter(t -> t != null && t.isSpawn())
                .filter(t -> t.getRoom() == Color.valueOf(discarded.getDiscardAward().name()))
                .findFirst().orElse(null));
        playerToSpawn.setAlive(ThreeState.TRUE);
        countDownLatch.countDown();
    }

    /**
     * Handles not receiving the powerUp.
     * It chooses a random PowerUp to discard.
     * @param skip
     */
    @Override
    public void updateOnStopSelection(ThreeState skip) {
        int rnd = random.nextInt(playerToSpawn.getPowerUps().size());
        updateOnPowerUps(new ArrayList<>(Collections.singleton(playerToSpawn.getPowerUps().get(rnd))));
    }
}

package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Observer;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;

import java.util.*;

public class Spawner extends Observer {
    private Player playerToSpawn;
    private Board board;
    private Random random;

    public Spawner(Player playerToSpawn,Board board){
        Logger.log(Priority.DEBUG, "Trying to spawn a player!");
        this.playerToSpawn = playerToSpawn;
        this.board = board;
        playerToSpawn.addPowerUp(board.drawPowerUp(),false);
        random = new Random();
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard) {
        if (powerUps.size() == 1 && playerToSpawn.getPowerUps().contains(powerUps.get(0))) {
            PowerUp discarded = playerToSpawn.getPowerUps().get(random.nextInt(playerToSpawn.getPowerUps().size()));
            playerToSpawn.getMatch().getBoard().getPowerUps().addToDiscarded(discarded);
            playerToSpawn.getPowerUps().remove(discarded);
            playerToSpawn.setTile(board.getTiles().stream()
                    .flatMap(Collection::stream)
                    .filter(Tile::isSpawn)
                    .filter(t -> t.getRoom() == Color.valueOf(discarded.getDiscardAward().name()))
                    .findFirst().orElse(null));
            playerToSpawn.setAlive(ThreeState.TRUE);
        }
    }

    @Override
    public void updateOnStopSelection(ThreeState skip) {
        int rnd = random.nextInt(playerToSpawn.getPowerUps().size());
        updateOnPowerUps(new ArrayList<>(Collections.singleton(playerToSpawn.getPowerUps().get(rnd))), false);
    }
}

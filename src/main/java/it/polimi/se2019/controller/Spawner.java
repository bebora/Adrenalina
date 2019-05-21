package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;

public class Spawner implements Runnable{
    private Player playerToSpawn;
    private Board board;

    public Spawner(Player playerToSpawn,Board board){
        this.playerToSpawn = playerToSpawn;
        this.board = board;
    }

    @Override
    public void run() {
        //TODO: ask the player for a powerUp to discard and spawn him accordingly
    }
}

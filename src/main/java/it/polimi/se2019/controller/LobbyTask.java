package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;

import java.util.TimerTask;

/**
 * Handles the start of the game after the delay defined in {@link LobbyController}.
 */
public class LobbyTask extends TimerTask {
    private LobbyController lobbyController;
    private Mode mode;
    public LobbyTask(LobbyController lobbyController, Mode mode) {
        this.lobbyController = lobbyController;
        this.mode = mode;
    }
    @Override
    public void run() {
        lobbyController.startGame(mode);
    }
}

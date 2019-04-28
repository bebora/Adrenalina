package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
//TODO this class will be an observer for events
public class GameController extends Thread {
    private Match match;
    private LobbyController lobbyController;

    public GameController() {
        //TODO create the match class in the it.polimi.se2019.model accordingly to construct parameters, initizialing first player etc and notifying it.polimi.se2019.view

    }

    public void playTurn() {
    }

    public Match getMatch() {
        return match;
    }


}

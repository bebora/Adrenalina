package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;

import java.util.Arrays;

public class GameControllerTest {
    GameController gameController = new GameController(Arrays.asList(new Player("pinco"),new Player("pallino")),"board3.btlb",8, false, null);
}

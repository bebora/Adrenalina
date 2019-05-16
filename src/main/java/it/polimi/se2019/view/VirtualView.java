package it.polimi.se2019.view;

import it.polimi.se2019.controller.LobbyController;


public class VirtualView extends View  {
    private LobbyController lobbyController;


    public VirtualView (LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }

    public LobbyController getLobbyController() {
        return lobbyController;
    }
}

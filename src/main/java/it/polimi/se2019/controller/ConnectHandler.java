package it.polimi.se2019.controller;

import it.polimi.se2019.view.VirtualView;

public class ConnectHandler implements ConnectInterface{
    private LobbyController lobbyController;
    @Override
    //TODO link a view/viewReceiver with an argument so that the controller can call methods on the remote view
    public void connect(String username, String salt, String password, boolean signingUp, String mode) {
        VirtualView virtualView = new VirtualView(lobbyController);
        if (signingUp)
            lobbyController.connectPlayer(username,password,mode, virtualView);
        else
            lobbyController.reconnectPlayer(username,password,virtualView);
    }
    public ConnectHandler(LobbyController lobbyController){
        this.lobbyController = lobbyController;
    }
}

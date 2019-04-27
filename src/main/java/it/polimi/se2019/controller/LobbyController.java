package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.ConnectionRequest;
import it.polimi.se2019.controller.events.SelectAction;
import it.polimi.se2019.controller.events.SelectPlayers;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.VirtualView;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyController extends EventVisitor {
    private List<GameController> games;
    private GameController waitingGame;

    @Override
    public synchronized void visit(ConnectionRequest connectionRequest) {
        String username = connectionRequest.getUsername();
        boolean found = false;
        for (GameController game : games) {
            List<String> allUsername = game.getMatch().getPlayers().stream().map(p -> p.getUsername()).collect(Collectors.toList());
            if (allUsername.contains(username)) {
                found = true;
                reconnectPlayer(game, username, connectionRequest.getVv());
            }
        }
        if (!found) {
            connectPlayer(username, connectionRequest.getVv());
        }

    }

    public void reconnectPlayer(GameController game, String username, View vv) {
        Player player = game.getMatch().getPlayers().
                stream().filter(p -> p.getUsername() == username).findFirst().get();
        player.setVirtualView(vv);
        player.setOnline(true);
        //TODO send popup update success
    }


    public void connectPlayer(String username, View vv) {
        Player player = new Player(false, username);
        player.setVirtualView(vv);
        waitingGame.getMatch().addPlayer(player);
        //TODO send popup update success
    }
}


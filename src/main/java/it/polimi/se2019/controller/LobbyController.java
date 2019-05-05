package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.ConnectionRequest;
import it.polimi.se2019.controller.events.SelectAction;
import it.polimi.se2019.controller.events.SelectPlayers;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.VirtualView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manage new Players connection and reconnections.
 * Handles the creation and the linking to the VirtualViews, checking if the username is already used in an active game.
 */
public class LobbyController extends EventVisitor {
    private List<GameController> games;
    private List<Player> waitingPlayers;

    /**
     * Visit a ConnectionRequest handling the connection of a new user or the reconnection of a current one.
     * @param connectionRequest
     */
    @Override
    public synchronized void visit(ConnectionRequest connectionRequest) {
        String token = connectionRequest.getToken();
        boolean found = false;
        for (GameController game : games) {
            List<String> allTokens = game.getMatch().getPlayers().stream().
                    filter(p -> !p.getOnline()).
                    map(Player::getToken).
                    collect(Collectors.toList());
            if (allTokens.contains(token)) {
                found = true;
                reconnectPlayer(game, token, connectionRequest.getVv());
            }
        }
        if (!found) {
            connectPlayer(token, connectionRequest.getVv());
        }

    }

    /**
     * Reconnect a player to the game linking the player to the current VirtualView
     * @param game active game where the username was found
     * @param token username found in the game
     * @param vv New VirtualView to link to the corresponding player
     */
    public void reconnectPlayer(GameController game, String token, View vv) {
        Player player = game.getMatch().getPlayers().
                stream().filter(p -> p.getToken().equals(token)).findFirst().orElse(null);
        if (player != null) {
            player.setVirtualView(vv);
            player.setOnline(true);

        }
        //TODO send popup update success
    }


    /**
     * Add a player to the waiting Players list, linking the VirtualView to the Player.
     * Manage the start of the timeout to start the game if enough players are in.
     * @param username
     * @param vv
     */
    public void connectPlayer(String username, View vv) {
        Player player = new Player("test", Color.RED);
        player.setVirtualView(vv);
        waitingPlayers.add(player);
        //TODO start timer when players are 3
        //TODO send popup update success
    }
}


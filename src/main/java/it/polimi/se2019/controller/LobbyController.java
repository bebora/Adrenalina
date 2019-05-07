package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.ConnectionRequest;
import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manage new Players connection and reconnections.
 * Handles the creation and the linking to the VirtualViews, checking if the username is already used in an active game.
 */
public class LobbyController extends EventVisitor {
    private List<GameController> games;
    private Map<Mode, List<Player>> waitingPlayers;

    public LobbyController(List<Mode> modes) {
        for (Mode mode: modes) {
            waitingPlayers.put(mode, new ArrayList<>());
        }
        games = new ArrayList<>();
    }

    /**
     * Visit a ConnectionRequest handling the connection of a new user or the reconnection of a current one.
     * @param connectionRequest
     */
    @Override
    public synchronized void visit(ConnectionRequest connectionRequest) {
        String token = connectionRequest.getToken();
        boolean found = false;
        if (connectionRequest.getExistingGame()) {
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
            if (!found)
                throw new IncorrectEvent();
        } else {
            Mode mode = Mode.valueOf(connectionRequest.getMode());
            connectPlayer(token, connectionRequest.getVv(), mode);

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
     * @param token
     * @param vv
     */
    public void connectPlayer(String token, View vv, Mode mode) {
        Player player = new Player(token);
        player.setVirtualView(vv);
        List<Player> modeWaiting;
        modeWaiting = waitingPlayers.get(mode);
        if (modeWaiting == null) {
            throw new IncorrectEvent();
        }
        else
            modeWaiting.add(player);

        //TODO start timer when players are 3
        //TODO send popup update success
    }
}


package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.view.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manage new Players connection and reconnections.
 * Handles the creation and the linking to the VirtualViews, checking if the username is already used in an active game.
 */
public class LobbyController{
    private List<GameController> games;
    private Map<Mode, List<Player>> waitingPlayers;

    public LobbyController(List<Mode> modes) {
        for (Mode mode : modes) {
            waitingPlayers.put(mode, new ArrayList<>());
        }
        games = new ArrayList<>();
    }

    /**
     * Reconnect a player to a game already started
     * @param username
     * @param password
     * @param view
     */
    public void reconnectPlayer(String username, String password, VirtualView view) {
        Player player = null;
        String token = String.format("%s$%s", username, password.hashCode());
        for (GameController game : games) {
            List<String> allTokens = game.getMatch().getPlayers().stream().
                    filter(p -> !p.getOnline()).
                    map(Player::getToken).
                    collect(Collectors.toList());
            if (allTokens.contains(token)) {
                player = game.getMatch().getPlayers().
                        stream().filter(p -> p.getToken().equals(token)).findFirst().orElse(null);
                break;
            }
        }
        if (player != null) {
            player.setVirtualView(view);
            player.setOnline(true);
        }
        //TODO send popup update success
        //TODO send update match
    }

    /**
     * Add a player to the waiting Players list, linking the VirtualView to the Player.
     * Manage the start of the timeout to start the game if enough players are in.
     *
     * @param username
     * @param password
     * @param mode
     */
    public void connectPlayer(String username, String password, String mode, VirtualView view) {
        for (GameController game : games) {
            List<String> allUsername = game.getMatch().getPlayers().stream().
                    filter(p -> !p.getOnline()).
                    map(Player::getToken).
                    map(s -> s = s.split("\\$")[0]).
                    collect(Collectors.toList());
            if (allUsername.contains(username)) {
                //TODO Send UPDATE to view saying username already used
                //TODO throw exception to close the connection
            }
            String token = String.format("%s$%s", username, password.hashCode());
            Player player = new Player(token);
            player.setVirtualView(view);
            List<Player> modeWaiting = waitingPlayers.get(mode);
            if (modeWaiting == null) {
                //TODO Send UPDATE to view saying mode is wrong :O
                //TODO throw exception to close the connection
            } else {
                modeWaiting.add(player);
                //TODO start timer when players are 3
                //TODO send popup update success
            }
        }
    }

    public RequestDispatcher getRequestHandler(String token) {
        for (GameController game :games) {
            List<String> allTokens = game.getMatch().getPlayers().stream().
                    filter(p -> !p.getOnline()).
                    map(Player::getToken).
                    collect(Collectors.toList());
            if (allTokens.contains(token)) {
                Player player = game.getMatch().getPlayers().
                        stream().filter(p -> p.getVirtualView().getRequestHandler().equals(token)).findFirst().orElse(null);
                return player.getVirtualView().getRequestHandler();
            }
        }
        return null;
    }
}


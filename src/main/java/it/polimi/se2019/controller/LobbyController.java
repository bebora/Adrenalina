package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.view.VirtualView;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manage new Players connection and reconnections.
 * Handles the creation and the linking to the VirtualViews, checking if the username is already used in an active game.
 */
public class LobbyController extends Thread{
    private List<GameController> games;
    private Map<Mode, List<Player>> waitingPlayers;
    private Map<Mode, Timer> waitingTimers;

    public LobbyController(List<Mode> modes) {
        waitingPlayers = new EnumMap<>(Mode.class);
        for (Mode mode : modes) {
            waitingPlayers.put(mode, new ArrayList<>());
        }
        games = new ArrayList<>();
        waitingTimers = new EnumMap<>(Mode.class);

    }

    /**
     * Reconnect a player to a game already started
     * @param username
     * @param password
     * @param view
     */
    public synchronized void reconnectPlayer(String username, String password, VirtualView view) {
        Player player = null;
        String token = String.format("%s$%s", username, password.hashCode());
        Match ownGame;
        for (GameController game : games) {
            List<String> allTokens = game.getMatch().getPlayers().stream().
                    filter(p -> !p.getOnline()).
                    map(Player::getToken).
                    collect(Collectors.toList());
            if (allTokens.contains(token)) {
                player = game.getMatch().getPlayers().
                        stream().filter(p -> p.getToken().equals(token)).findFirst().orElse(null);
                ownGame = game.getMatch();
                player.setVirtualView(view);
                player.setOnline(true);
                view.getViewUpdater().sendPopupMessage("Reconnected succesfully!");
                player.getVirtualView().getViewUpdater().sendTotalUpdate(username,ownGame.getBoard(), ownGame.getPlayers(), view.getIdView(), player.getPoints(), player.getPowerUps(), player.getWeapons());
                break;
            }
        }
        if (player == null) {
            view.getViewUpdater().sendPopupMessage("Errore nel login!");
        }

    }

    /**
     * Add a player to the waiting Players list, linking the VirtualView to the Player.
     * Manage the start of the timeout to start the game if enough players are in.
     *
     * @param username
     * @param password
     * @param mode
     */
    public synchronized void connectPlayer(String username, String password, String mode, VirtualView view) {
        mode = mode.toUpperCase();
        for (GameController game : games) {
            List<String> allUsername = game.getMatch().getPlayers().stream().
                    filter(p -> !p.getOnline()).
                    map(Player::getToken).
                    map(s -> s = s.split("\\$")[0]).
                    collect(Collectors.toList());
            if (allUsername.contains(username)) {
                view.getViewUpdater().sendPopupMessage("Username is already in use! Can't connect.");
                //TODO throw exception to close the connection
            }
        }
        String token = String.format("%s$%s", username, password.hashCode());
        Player player = new Player(token);
        player.setVirtualView(view);
        List<Player> modeWaiting = waitingPlayers.get(Mode.valueOf(mode));
        if (modeWaiting == null) {
            view.getViewUpdater().sendPopupMessage("Selected mode does not exist! Can't connect.");
            //TODO throw exception to close the connection
        } else {
            modeWaiting.add(player);
            view.getViewUpdater().sendPopupMessage("SUCCESS");
            if (modeWaiting.size() == 3) {
                Timer timer = new Timer();
                waitingTimers.put(Mode.valueOf(mode), timer);
                timer.schedule(new LobbyTask(this, Mode.valueOf(mode)), 5000);
            }
            else if (modeWaiting.size() == 5) {
                waitingTimers.get(Mode.valueOf(mode)).cancel();
                Timer timer = new Timer();
                timer.schedule(new LobbyTask(this, Mode.valueOf(mode)), 0);
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
                        stream().filter(p -> p.getVirtualView().getRequestDispatcher().equals(token)).findFirst().orElse(null);
                return player.getVirtualView().getRequestDispatcher();
            }
        }
        return null;
    }

    public Map<Mode, List<Player>> getWaitingPlayers() {
        return waitingPlayers;
    }

    public synchronized void startGame(Mode mode) {
        List<Player> playing = new ArrayList<>(waitingPlayers.get(mode));
        if (playing.size() > 5) {
            playing = new ArrayList<>(playing.subList(0,5));
        }
        else {
            playing = new ArrayList<>(playing);
        }
        waitingPlayers.get(mode).removeAll(playing);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String nameDir = classloader.getResource("boards").getPath();
        File dir = new File(nameDir);
        File[] directoryListing = dir.listFiles();
        int rnd = new Random().nextInt(directoryListing.length);
        String boardName = directoryListing[rnd].getName();
        GameController gameController = new GameController(playing, boardName, 8, mode.equals(Mode.DOMINATION));
        games.add(gameController);
    }

    public List<GameController> getGames() {
        return games;
    }
}
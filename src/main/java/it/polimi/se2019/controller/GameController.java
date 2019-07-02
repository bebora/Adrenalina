package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.Utils;
import it.polimi.se2019.network.events.IncorrectEventException;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.Reload;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Moment;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.view.SelectableOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static it.polimi.se2019.controller.ReceivingType.PLAYERS;
import static it.polimi.se2019.controller.ReceivingType.STOP;
import static it.polimi.se2019.model.ThreeState.*;

/**
 * Controller class related to a single game.
 * Handles the game flow, using {@link #match} properties.
 * Supports:
 * <li>PowerUp with {@link Moment#OWNROUND} activation, using {@link EffectController}</li>
 * <li>Game ending and related winnes sending</li>
 * <li>Action selection</li>
 * <li></li>
 */
public class GameController extends Observer {
    private Match match;
    private LobbyController lobbyController;
    private int actionCounter = 0;
    private Player currentPlayer;
    private ActionController actionController;
    /**
     * List of players that need to be spawned after their death.
     */
    private List<Player> spawnablePlayers;
    /**
     * Flag to indicate that the current match ended, for two possible reasons:
     * <li>Game motivations, relative to {@link Match#newTurn()} boolean value</li>
     * <li>Less than 3 online players are playing.</li>
     */
    private boolean matchEnd;

    /**
     * Flag to indicate that the current turn ended, for different reasons:
     * <li>The players has no more actions.</li>
     */
    private boolean turnEnd;

    /**
     * Flag to indicate that the current player has to skips action in his current turn.
     * True when the player didn't answer in time, getting blocked for the current turn.
     */
    private boolean skip;

    /**
     * Whether {@link EffectController} is computing an action or not.
     */
    private boolean action;

    /**
     * Flag to indicate whether the controller is waiting for answers for overkilled players in DominationMode.
     */
    private boolean dominationOverkill;
    private TimerConstrainedEventHandler timerConstrainedEventHandler;
    private AcceptableTypes acceptableTypes;
    private CountDownLatch countDownLatch;

    /**
     * Handles the disconnection of a player, communicating to other players that the player got disconnected.
     * Calls the related checkEnd method, checking if the game needs to stop.
     * @param username
     */
    public synchronized void checkEnd(String username) {
        if (!matchEnd) {
            match.getUpdateSender().sendPopupMessage(String.format("Player %s is offline!", username));
            checkEnd();
        }
    }

    /**
     * Checks if the game needs to stop prematurely for lack of players.
     * Notify the players sending the winners if the game ends.
     */
    public synchronized void checkEnd() {
        if (!matchEnd && match.getPlayers().stream().filter(p -> !p.getDominationSpawn() && p.getOnline()).count() < 3) {
            matchEnd = true;
            sendWinners();
        }
    }

    /**
     * Handles receiving an action from the client, starting the related {@link #actionController}.
     * @param action chose by the corresponding client.
     */
    @Override
    public void updateOnAction(Action action){
        this.action = true;
        match.updatePopupViews(String.format("%s chose %s action!",currentPlayer.getUsername(), action.toString()));
        actionController = new ActionController(match,this);
        actionController.updateOnAction(action);
    }

    /**
     * Handles receiving a List of players from the client.
     * Used for domination spawn overkill, in Domination mode.
     * It updates the {@link #countDownLatch}.
     * @param players
     */
    @Override
    public void updateOnPlayers(List<Player> players) {
        players.get(0).receiveShot(currentPlayer, 1, 0, true);
        countDownLatch.countDown();
    }

    /**
     * Handles receiving a List of powerUps from the client.
     * Used for:
     * <li>Spawning players at the start of the Game</li>
     * <li>Choosing powerUps, and start the related {@code #effectController}.</li>
     * @param powerUps
     */
    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps) {
        if (currentPlayer.getAlive() == OPTIONAL) {
            currentPlayer.setAlive(TRUE);
            Tile tile = match.getBoard().getTiles().
                    stream().
                    flatMap(List::stream).
                    filter(t -> t != null && t.isSpawn() && t.getRoom().equals(Color.valueOf(powerUps.get(0).getDiscardAward().toString()))).
                    findFirst().
                    orElseThrow(() -> new IncorrectEventException("Can't find tile, board not correctly formatted"));
            currentPlayer.setTile(tile);
            currentPlayer.discardPowerUp(powerUps.get(0), false);
            match.updatePopupViews(String.format("%s discarded %s to spawn in %s room!",
                    currentPlayer.getUsername(),
                    powerUps.get(0).getName(),
                    powerUps.get(0).getDiscardAward().toString()));
            if (!skip)
                playTurn();
            else {
                skip = false;
                endTurn(true);
            }
        }
        else {
            this.action = false;
            currentPlayer.discardPowerUp(powerUps.get(0), false);
            match.updatePopupViews(String.format("%s chose to use %s!",
                    currentPlayer.getUsername(),
                    powerUps.get(0).getName()));
            EffectController effectController = new EffectController(powerUps.get(0).getEffect(), null, match, currentPlayer, match.getPlayers(), this);
            effectController.nextStep();
        }
    }


    /**
     * Creates a GameController istance, managing the flow of the game.
     * Contains the information required to create a new Match, the mode of the new Match, and the related {@link #lobbyController}.
     * @param players clients playing the game
     * @param boardName refers to the name of the board (chosen randomly) used
     * @param numSkulls number of max skulls in the board.
     * @param domination whether the mode is domination or not
     * @param lobbyController
     */
    public GameController(List<Player> players, String boardName, int numSkulls, boolean domination, LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        if(!domination){
            match = new NormalMatch(players,boardName,numSkulls);
        }else{
            match = new DominationMatch(players,boardName,numSkulls);
        }
        dominationOverkill = false;
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
        matchEnd = false;
        turnEnd = false;
        skip = false;
        acceptableTypes = new AcceptableTypes(new ArrayList<>());
        match.updateViews();
    }

    public void startTurn(){
        if (matchEnd) {
            Logger.log(Priority.DEBUG, "Game ended, stopping turn");
        }
        //Spawn Player if the match is starting
        else if(currentPlayer.getAlive() == ThreeState.OPTIONAL){
            for(int i = 0; i < 2; i++){
                currentPlayer.addPowerUp(match.getBoard().drawPowerUp(),false);
            }
            List<ReceivingType> receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.POWERUP));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(currentPlayer.getPowerUps(), 1,1, "Select a PowerUp to discard to spawn!"));
            timerConstrainedEventHandler = new TimerConstrainedEventHandler(
                    this,
                    currentPlayer.getVirtualView().getRequestDispatcher(),
                    acceptableTypes);
            timerConstrainedEventHandler.start();
        }
        //End turn if current player is not online
        else if (!currentPlayer.getOnline() || skip) {
            skip = false;
            endTurn(true);
        }
        else {
            playTurn();
        }
    }

    /**
     * Handles the start of the turn
     * Prompt the client with an action and/or powerups and/or the chance to stop the current turn.
     * Check if the turn is ended (if the player used a Reload action)
     */
    public void playTurn() {
        turnEnd = false;
        List<ReceivingType> receivingTypes = new ArrayList<>();
        acceptableTypes = new AcceptableTypes(receivingTypes);
        //Check for the availability of any action
        if(actionCounter < currentPlayer.getMaxActions()) {
            receivingTypes.add(ReceivingType.ACTION);
            acceptableTypes.setSelectableActions(new SelectableOptions<>(currentPlayer.getActions(), 1, 1, "Select an Action!"));
        } //Check if player can reload, at the end of his turn
        else if (!match.getFinalFrenzy() && currentPlayer.getWeapons().stream().anyMatch(w -> !w.getLoaded())) {
            turnEnd = true;
            receivingTypes.add(ReceivingType.ACTION);
            Action reload = new Reload();
            currentPlayer.getActions().add(reload);
            acceptableTypes.setSelectableActions(new SelectableOptions<>(Arrays.asList(reload), 1, 1, "Select an Action!"));
        }
        else turnEnd = true;

        //Check if any powerUps can be played
        if (currentPlayer.getPowerUps().stream().
                filter(p -> p.getApplicability() == Moment.OWNROUND).
                count() >= 1) {
            receivingTypes.addAll(Arrays.asList(ReceivingType.POWERUP));
            List<PowerUp> usablePowerups = currentPlayer.getPowerUps().stream().
                    filter(p -> p.getApplicability() == Moment.OWNROUND).collect(Collectors.toList());
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(usablePowerups, 1, 1, "Select a PowerUp!"));
        }
        //Check if player can end his action, after completing the maximum number of actions.
        if (turnEnd && !receivingTypes.isEmpty()) {
            receivingTypes.add(STOP);
            acceptableTypes.setStop(false, "End turn");
        }
        timerConstrainedEventHandler = new TimerConstrainedEventHandler(
                this,
                currentPlayer.getVirtualView().getRequestDispatcher(),
                acceptableTypes);
        timerConstrainedEventHandler.start();
    }

    @Override
    public Match getMatch() {
        return match;
    }

    /**
     * Checks whether the player has to end its turn. It checks:
     * <li>If the player has done all of its action</li>
     * <li>If there are no more usable powerUps during his turn</li>
     * <li>If there is the option to reload, or if the player already reloaded</li>
     * @return
     */
    private boolean checkEndTurn() {
        if ((actionCounter == currentPlayer.getMaxActions() && (!currentPlayer.hasPowerUp(Moment.OWNROUND) && (!match.getFinalFrenzy()) && !currentPlayer.canReload() || match.getFinalFrenzy())) || actionCounter == currentPlayer.getMaxActions() +1)
            return true;
        else return false;
    }

    /**
     * Handles the conclusion of the action.
     * It sets back the eventHelper to the original match.
     * It continues the flow of the turn, ending it or asking for the next action to the player.
     */
    @Override
    public void updateOnConclusion(){
        if (action)
            actionCounter++;
        actionController = null;
        match.getPlayers().stream().filter(p -> p.getVirtualView() != null && p.getVirtualView().getRequestDispatcher() != null).forEach(p -> p.getVirtualView().getRequestDispatcher().setEventHelper(match, p));
        if(currentPlayer.hasPowerUp(Moment.OWNROUND) || !checkEndTurn()){
            playTurn();
        }
        else {
            endTurn(false);
        }
    }

    /**
     * Checks if there are spawnPoints AND any overkilled Player, to assign the overkill damage to one of the spawns.
     * If the currentPlayer is offline, the assignable damages will go to a random spawn.
     */
    public void checkDominationOverKill(boolean toSkip) {
        dominationOverkill = true;
        // Players which have been killed in this turn with overkill
        List<Player> overkillPlayers = match.
                getPlayers().
                stream().
                filter(p -> !p.getDominationSpawn() && p.getDamages().size() == 12 && !p.getDamages().get(11).equals(p)).
                collect(Collectors.toList());
        List<Player> spawnPoints = match.
                getPlayers().
                stream().
                filter(Player::getDominationSpawn).
                collect(Collectors.toList());
        int overkilledSize = overkillPlayers.size();
        // This if can be true only in domination mode
        if (!spawnPoints.isEmpty() && !overkillPlayers.isEmpty() && !toSkip && currentPlayer.getOnline()) {
            acceptableTypes = new AcceptableTypes(Collections.singletonList(PLAYERS));
            for (Player current : overkillPlayers) {
                acceptableTypes.setSelectablePlayers(new SelectableOptions<>(spawnPoints, 1, 1, String.format("Select a spawn point to deposit %s overkill", current.getUsername())));
                timerConstrainedEventHandler = new TimerConstrainedEventHandler(this, currentPlayer.getVirtualView().getRequestDispatcher(), acceptableTypes);
                countDownLatch = new CountDownLatch(1);
                timerConstrainedEventHandler.start();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    Logger.log(Priority.ERROR, "Join on domination overkill blocked by " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
                if (!timerConstrainedEventHandler.isBlocked()) {
                    Player spawnPoint = spawnPoints.stream().findAny().orElse(null);
                    if (spawnPoint != null) {
                        spawnPoint.receiveShot(currentPlayer, overkilledSize, 0, true);
                    }
                    break;
                } else {
                    overkilledSize--;
                }
            }
        }
        // Same conditions as before but player offline
        else if (!overkillPlayers.isEmpty() && !spawnPoints.isEmpty()) {
            Player spawnPoint = spawnPoints.stream().findAny().orElse(spawnPoints.get(0));
            spawnPoint.receiveShot(currentPlayer, overkillPlayers.size(), 0, true);
        }
        dominationOverkill = false;
    }

    /**
     * Handles the ending of the turn.
     * Handles the deposit of the overkill of players in DominationMode into SpawnPoints of choice.
     * @param toSkip true when the player
     */
    public synchronized void endTurn(boolean toSkip) {
        currentPlayer.getActions().removeIf(p -> p.toString().equals("RELOAD"));
        checkDominationOverKill(toSkip);
        if (match.newTurn()) {
            matchEnd = true;
            sendWinners();
            return;
        }
        actionCounter = 0;
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
        spawnablePlayers = match.getPlayers().stream()
                .filter(p -> p.getAlive() == ThreeState.FALSE)
                .collect(Collectors.toList());
        //If the match isn't ending, compute a new turn.
        if (!matchEnd) {
            if (!spawnablePlayers.isEmpty())
                startSpawning();
            startTurn();
        } else {
            Logger.log(Priority.DEBUG, "Game ending");
    }

    }

    /**
     * Send winners to the players, parsing them using {@link Match#getWinners()}.
     * Handles putting players offline once the game ended.
     * Sends to the player the list of the winners.
     */
    public void sendWinners() {
        matchEnd = true;
        //Reset timers
        for (Player p : match.getPlayers())
            if (p.getVirtualView() != null)
                p.getVirtualView().getRequestDispatcher().block();
        lobbyController.getGames().remove(this);
        Logger.log(Priority.DEBUG, "Parsing winners");
        List<Player> players = match.getWinners();
        StringBuilder stringBuffer = new StringBuilder("WINNERS,");
        for (Player p : players) {
            stringBuffer.append(p.getUsername() + ",");
        }
        match.getPlayers().stream().filter(Player::getOnline).forEach(p -> p.getVirtualView().getViewUpdater().sendPopupMessage(stringBuffer.toString()));
        Utils.sleepABit(1000);
        match.getUpdateSender().getUpdatePoller().interrupt();
        //Set the players offline
        match.getPlayers().stream().filter(Player::getOnline).forEach(p -> p.setOnline(false));
    }

    /**
     * Handles the spawning of players after their death.
     * <li>It selects a random point if they are offline.</li>
     * <li>It handles the online players asynchronously.</li>
     */
    public void startSpawning(){
        List<TimerConstrainedEventHandler> timerConstrainedEventHandlers = new ArrayList<>();
        countDownLatch = new CountDownLatch(spawnablePlayers.size());
        for(Player p: spawnablePlayers) {
            Observer spawner = new Spawner(countDownLatch, p, match.getBoard());
            //If player is offline, spawn in a random point
            if (!p.getOnline()) {
                spawner.updateOnStopSelection(TRUE);
                break;
            }
            List<ReceivingType> receivingTypes = Collections.singletonList(ReceivingType.POWERUP);
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(p.getPowerUps(), 1, 1, "Select a PowerUp to discard!"));
            timerConstrainedEventHandlers.add(new TimerConstrainedEventHandler(spawner, p.getVirtualView().getRequestDispatcher(), acceptableTypes));
        }
        timerConstrainedEventHandlers.forEach(Thread::start);
        try {
            countDownLatch.await();
            Logger.log(Priority.DEBUG, "Spawning done!");
        }
        catch (Exception e) {
            Logger.log(Priority.DEBUG, "Ended handler spawner!");
        }
    }


    /**
     * Handles receiving a stop selection.
     * <li>If {@link #dominationOverkill} is true, the latch's count get decreased</li>
     * <li>If the current player is spawning at the end of the game, a random SpawnPoing gets selected</li>
     * <li>If the skip is a non-reverse skip, the turn ends and the player gets eventually prompted.</li>
     * <li>If the skip is a reverse skip, the turn ends and the player doesn't get prompted.</li>
     */
    @Override
    public void updateOnStopSelection(ThreeState skip){
        if (matchEnd)
            return;
        if (countDownLatch != null && countDownLatch.getCount() != 0 && dominationOverkill)
            countDownLatch.countDown();
        else if (currentPlayer.getAlive() == OPTIONAL) {
            this.skip = true;
            updateOnPowerUps(Arrays.asList(acceptableTypes.getSelectablePowerUps().getOptions().stream().findAny().orElse(null)));
        }
        else if (skip.toBoolean() || acceptableTypes.isReverse()) {
            if(action)
                actionCounter++;
            actionController = null;
            if (skip.toSkip() || checkEndTurn()) {
                endTurn(skip.toSkip());
            }
            else {
                playTurn();
            }
        }
        else if (skip == FALSE) {
            endTurn(false);
        }
    }

}

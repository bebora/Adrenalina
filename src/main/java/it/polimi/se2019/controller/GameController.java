package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Observer;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.Reload;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Moment;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.view.SelectableOptions;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.se2019.controller.ReceivingType.PLAYERS;
import static it.polimi.se2019.controller.ReceivingType.STOP;
import static it.polimi.se2019.model.ThreeState.FALSE;
import static it.polimi.se2019.model.ThreeState.OPTIONAL;
import static it.polimi.se2019.model.ThreeState.TRUE;

public class GameController extends Observer {
    private Match match;
    private LobbyController lobbyController;
    private int actionCounter=0;
    private Player currentPlayer;
    private ActionController actionController;
    private List<Player> spawnablePlayers;
    private Random random = new Random();
    private TimerCostrainedEventHandler timerCostrainedEventHandler;
    private AcceptableTypes acceptableTypes;
    private boolean end;
    private boolean skip;
    private boolean action;
    private PowerUp toDiscard;

    public synchronized void checkEnd(String username) {
        match.getUpdateSender().sendPopupMessage(String.format("Player %s is offline!", username));
        checkEnd();
    }
    public synchronized void checkEnd() {
        if (!end && match.getPlayers().stream().filter(p -> !p.getDominationSpawn() && p.getOnline()).count() < 3) {
            end = true;
            sendWinners();
        }
    }

    @Override
    public void updateOnAction(Action action){
        this.action = true;
        actionController = new ActionController(match,this);
        actionController.updateOnAction(action);
    }

    @Override
    public void updateOnPlayers(List<Player> players) {
        if (acceptableTypes.getSelectablePlayers().checkForCoherency(players)) {
            currentPlayer.getVirtualView().getRequestDispatcher().clear();
            players.get(0).receiveShot(currentPlayer, 1, 0);
        }
        else {
            throw new IncorrectEvent("Wrong players!");
        }
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard) {
        if (acceptableTypes.getSelectablePowerUps().checkForCoherency(powerUps)) {
            if (currentPlayer.getAlive() == OPTIONAL) {
                currentPlayer.getVirtualView().getRequestDispatcher().clear();
                currentPlayer.setAlive(TRUE);
                Tile tile = match.getBoard().getTiles().stream().flatMap(List::stream).
                        filter(t -> t != null && t.isSpawn() && t.getRoom() == Color.valueOf(powerUps.get(0).getDiscardAward().toString())).findFirst().orElseThrow(() -> new IncorrectEvent("Error in PowerUps!"));
                currentPlayer.setTile(tile);
                currentPlayer.discardPowerUp(powerUps.get(0), false);
                if (!skip)
                    playTurn();
                else {
                    skip = false;
                    endTurn(true);
                }
            }
            else {
                this.action = false;
                /*AcceptableTypes tilesAccepted = new AcceptableTypes(Collections.singletonList(ReceivingType.TILES));
                List<Tile> tiles = new ArrayList<>(match.getBoard().getTiles().stream().flatMap(List::stream).filter(t -> t!= null).collect(Collectors.toList()));
                tilesAccepted.setSelectableTileCoords(new SelectableOptions<>(tiles, 1 , 1, "Select a tile to move!"));
                Choice tileRequest = new Choice(currentPlayer.getVirtualView().getRequestDispatcher(), tilesAccepted);
                switch (tileRequest.getReceivingType()) {
                    case STOP:
                        updateOnStopSelection(TRUE);
                        break;
                    case TILES:
                        currentPlayer.discardPowerUp(powerUps.get(0), false);
                        currentPlayer.setTile(tiles.get(0));
                        break;
                }
                if(currentPlayer.hasPowerUp(Moment.OWNROUND) || actionCounter < currentPlayer.getMaxActions()){
                    playTurn();
                }
                else {
                    endTurn(false);
                }*/
                currentPlayer.getVirtualView().getRequestDispatcher().clear();
                toDiscard = (powerUps.get(0));
                currentPlayer.discardPowerUp(toDiscard, false);
                EffectController effectController = new EffectController(powerUps.get(0).getEffect(), null, match, currentPlayer, match.getPlayers(), this);
                effectController.nextStep();
            }
        }
        else {
            throw new IncorrectEvent("PowerUps not acceptable!");
        }
    }

    public GameController(List<Player> players, String boardName, int numSkulls, boolean domination, LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        if(!domination){
            match = new NormalMatch(players,boardName,numSkulls);
        }else{
            match = new DominationMatch(players,boardName,numSkulls);
        }
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
        end = false;
        skip = false;
    }

    public void startTurn(){
        if (!currentPlayer.getOnline() || skip) {
            skip = false;
            endTurn(true);
        }
        if(currentPlayer.getAlive() == ThreeState.OPTIONAL){
            for(int i = 0; i < 2; i++){
                currentPlayer.addPowerUp(match.getBoard().drawPowerUp(),false);
            }
            List<ReceivingType> receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.POWERUP));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(currentPlayer.getPowerUps(), 1,1, "Seleziona un PowerUp!"));
            timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                    this,
                    currentPlayer.getVirtualView().getRequestDispatcher(),
                    acceptableTypes);
            timerCostrainedEventHandler.start();
        }
        else {
            playTurn();
        }
    }

    public void playTurn() {
        boolean end = false;
        List<ReceivingType> receivingTypes = new ArrayList<>();
        acceptableTypes = new AcceptableTypes(receivingTypes);
        if(actionCounter < currentPlayer.getMaxActions()) {
            receivingTypes.add(ReceivingType.ACTION);
            acceptableTypes.setSelectableActions(new SelectableOptions<>(currentPlayer.getActions(), 1, 1, "Select an Action!"));
        } else if (!match.getFinalFrenzy() && currentPlayer.getWeapons().stream().anyMatch(w -> !w.getLoaded())) {
            end = true;
            receivingTypes.add(ReceivingType.ACTION);
            Action action = new Reload();
            currentPlayer.getActions().add(action);
            acceptableTypes.setSelectableActions(new SelectableOptions<>(Arrays.asList(action), 1, 1, "Select an Action!"));
        }
        else end = true;
        if (currentPlayer.getPowerUps().stream().
                filter(p -> p.getApplicability() == Moment.OWNROUND).
                count() >= 1) {
            receivingTypes.addAll(Arrays.asList(ReceivingType.POWERUP));
            List<PowerUp> usablePowerups = currentPlayer.getPowerUps().stream().
                    filter(p -> p.getApplicability() == Moment.OWNROUND).collect(Collectors.toList());
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(usablePowerups, 1, 1, "Select a PowerUp!"));
        }
        if (end && !receivingTypes.isEmpty()) {
            receivingTypes.add(STOP);
            acceptableTypes.setStop(false, "End turn");
        }
        timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                this,
                currentPlayer.getVirtualView().getRequestDispatcher(),
                acceptableTypes);
        timerCostrainedEventHandler.start();
    }

    public Match getMatch() {
        return match;
    }

    public void updateOnConclusion(){
        if (action)
            actionCounter++;
        actionController = null;
        match.getPlayers().stream().filter(p -> p.getVirtualView() != null && p.getVirtualView().getRequestDispatcher() != null).map(p -> p.getVirtualView().getRequestDispatcher()).forEach(rq -> rq.setEventHelper(match));
        if(currentPlayer.hasPowerUp(Moment.OWNROUND) || actionCounter < currentPlayer.getMaxActions()){
            playTurn();
        }
        else {
            endTurn(false);
        }
    }

    public synchronized void endTurn(boolean skip){
        List<Player> overkillPlayers = match.
                getPlayers().
                stream().
                filter(p -> !p.getDominationSpawn() && p.getDamages().size() == 8).
                collect(Collectors.toList());
        List<Player> spawnPoints = match.
                getPlayers().
                stream().
                filter(Player::getDominationSpawn).
                collect(Collectors.toList());
        if (!spawnPoints.isEmpty() &&  !overkillPlayers.isEmpty()) {
            if (skip) {
                acceptableTypes = new AcceptableTypes(Arrays.asList(PLAYERS));
                acceptableTypes.setSelectablePlayers(new SelectableOptions<>(spawnPoints, 1, 1, String.format("Select a spawn point to deposit %s overkill", overkillPlayers.get(0).getUsername())));
                for (Player current : overkillPlayers) {
                    current.getDamages().remove(7);
                    timerCostrainedEventHandler = new TimerCostrainedEventHandler(this, currentPlayer.getVirtualView().getRequestDispatcher(), acceptableTypes);
                    timerCostrainedEventHandler.setNotifyOnEnd(false);
                    timerCostrainedEventHandler.start();
                    try {
                        timerCostrainedEventHandler.join();
                    } catch (InterruptedException e) {
                        Logger.log(Priority.ERROR, "Join on domination overkill blocked by " + e.getMessage());
                    }
                    if (!timerCostrainedEventHandler.isBlocked()) {
                        overkillPlayers.forEach(p -> p.getDamages().remove(7));
                        break;
                    }
                }
            }
            else {
                Player spawnPoint = spawnPoints.stream().findAny().orElse(null);
                if (spawnPoint != null) {
                    spawnPoint.receiveShot(currentPlayer, overkillPlayers.size(), 0);
                }
                overkillPlayers.forEach(p -> p.getDamages().remove(7));
            }
        }
        else if (match.newTurn()) {
            end = true;
            sendWinners();
            lobbyController.getGames().remove(this);
            return;
        }
        actionCounter = 0;
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
        spawnablePlayers =match.getPlayers().stream()
                .filter(p -> p.getAlive() == ThreeState.FALSE)
                .collect(Collectors.toList());

        if (!end) {
            if(spawnablePlayers.isEmpty())
                startTurn();
            else
                startSpawning();
        }
    }

    public void sendWinners() {
        Logger.log(Priority.DEBUG, "Parsing winners");
        List<Player> players = match.getWinners();
        StringBuilder stringBuffer = new StringBuilder("WINNERS$Winners are ");
        for (Player p : players) {
            stringBuffer.append(p.getUsername() + ", ");
        }
        players.stream().filter(Player::getOnline).forEach(p -> p.getVirtualView().getViewUpdater().sendPopupMessage(stringBuffer.toString()));
        try {
            wait(500);
        }
        catch (InterruptedException e) {
            assert false;
        }
        //TODO stop all the socket connections
        match.getPlayers().stream().filter(Player::getOnline).forEach(p -> p.setOnline(false));
    }

    public void startSpawning(){
        List<TimerCostrainedEventHandler> timerCostrainedEventHandlers = new ArrayList<>();
        for(Player p: spawnablePlayers){
            List<ReceivingType> receivingTypes = Collections.singletonList(ReceivingType.POWERUP);
            Observer spawner = new Spawner(p,match.getBoard());
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(p.getPowerUps(), 1,1,"Seleziona il powerup da scartare!"));
            timerCostrainedEventHandlers.add(new TimerCostrainedEventHandler(spawner,p.getVirtualView().getRequestDispatcher(), acceptableTypes));
        }
        for (TimerCostrainedEventHandler t : timerCostrainedEventHandlers) {
            try {
                t.join();
            }
            catch (Exception e) {
                Logger.log(Priority.DEBUG, "Ended handler powerup damaged");
            }
        }
    }

    @Override
    public void updateOnStopSelection(ThreeState skip){
        currentPlayer.getVirtualView().getRequestDispatcher().clear();
        if (currentPlayer.getAlive() == OPTIONAL) {
            this.skip = true;
            updateOnPowerUps(Arrays.asList(acceptableTypes.getSelectablePowerUps().getOptions().stream().findAny().orElse(null)), true);
        }
        else if (skip.toBoolean() || acceptableTypes.isReverse()) {
            if(action)
                actionCounter++;
            actionController = null;
            if (skip.toSkip() || (actionCounter == currentPlayer.getMaxActions() && currentPlayer.hasPowerUp(Moment.OWNROUND))) {
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
